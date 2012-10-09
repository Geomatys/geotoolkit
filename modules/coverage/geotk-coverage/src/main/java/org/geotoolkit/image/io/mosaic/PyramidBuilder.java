/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.image.io.mosaic;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.media.jai.TiledImage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Element;

/**
 * Create and write an Image pyramid.
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidBuilder {

    /**
     * The default tile size in pixels.
     */
    private static final int DEFAULT_TILE_SIZE = 256;

    /**
     * Minimum tile size.
     */
    private static final int MIN_TILE_SIZE = 64;

    private static final int DEFAULT_SLAB_SIZE = 16;

    private static final int MAX_SLAB_SIZE = 36;

    /**
     * Tile width.
     */
    private int tileWidth;

    /**
     * Tile height.
     */
    private int tileHeight;

    /**
     * Interpolation attributes.
     */
    private double[] fillValue = null;

    /**
     * Directory path which will contain tile.
     */
    private File outputDirectory = null;

    /**
     * Written tile extention.
     */
    private String outputFormatName = null;

    /**
     * Resampling coefficient in X direction.
     */
    private int[] coeffX = null;

    /**
     * Resampling coefficient in Y direction.
     */
    private int[] coeffY = null;

    private int slabWidth;

    private int slabHeight;

    /**
     * Interpolation properties.
     */
    InterpolationCase interpolationCase = null;
    int lanczosWindow = -1;

    public PyramidBuilder() {
    }

    /**
     *
     * @param interpolationCase
     * @param lanczosWindow
     */
    public TileManager createTileManager(TileManager[] originalMosaic) throws IOException, NoninvertibleTransformException, TransformException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
        ArgumentChecks.ensureNonNull("originalMosaic", originalMosaic);
        if(outputDirectory == null)
            throw new IllegalStateException("caller must set output directory");
        if (coeffX == null || coeffY == null)
            throw new IllegalStateException("caller must set resampling coefficients");
        if (tileHeight == 0 || tileWidth == 0)
            throw new IllegalStateException("caller must set tile dimension");
        if (slabHeight == 0 || slabWidth == 0)
            throw new IllegalStateException("caller must set slab dimension");
        if (interpolationCase == null || lanczosWindow == 0)
            throw new IllegalStateException("caller must set interpolation properties");
        final Rectangle areaTile = new Rectangle();
        final int levelNbre = coeffX.length;

        /*
         * Clean destination directory.
         */
        cleanDirectory(outputDirectory);

        SampleModel sampleMod;
        ColorModel colorMod;
        RenderedImage wri;
        Rectangle globalRegion = null;

        for (TileManager tm : originalMosaic) {
            if (globalRegion == null) globalRegion = new Rectangle(tm.getRegion());
            else globalRegion.add(tm.getRegion());
        }

        //write xml
        writeProperties(globalRegion);

        //creer l'architecture
        for (int i = 0; i<coeffX.length; i++) {
            int nbreSlabX = (globalRegion.width/coeffX[i] + slabWidth*tileWidth - 1)/(slabWidth*tileWidth);
            int nbreSlabY = (globalRegion.height/coeffY[i] + slabHeight*tileHeight - 1)/(slabHeight*tileHeight);
            String archiPath = outputDirectory.getAbsolutePath()+"/"+coeffX[i]+"_"+coeffY[i]+"/";
            for (int y = 0; y<nbreSlabY; y++) {
                for (int x = 0; x<nbreSlabX; x++) {
                    new File(archiPath+x+"_"+y+"/").mkdirs();
                }
            }
        }

        //pour chaque etage de tile manager
        for (TileManager tileManager : originalMosaic) {

            Collection<Tile> listTile = tileManager.getTiles();
            //pour chaque image de chacun des tiles manager
            for (Tile tile : listTile) {

                //on recup la region quelle occupe
                Rectangle tileRegion        = tile.getRegion();
                //on recupere l'etage courant
                Dimension tilesubSampling   = tile.getSubsampling();
                ImageReader imgReader       = tile.getImageReader();
                RenderedImage renderedImage = imgReader.read(0);
                imgReader.dispose();

                //on recupere ces données perso
                colorMod     = renderedImage.getColorModel();
                sampleMod    = renderedImage.getSampleModel();
                int datatype = sampleMod.getDataType();
                int numband  = sampleMod.getNumBands();
                int rIMinX   = renderedImage.getMinX();
                int rIMinY   = renderedImage.getMinY();
                int rIWidth  = renderedImage.getWidth();
                int rIHeight = renderedImage.getHeight();

                int minx,miny, maxx, maxy, resWidth, resHeight, nbrTX, nbrTY;

                //pour chaque etage de pyramide on regarde s'il faut la resamplée
                for (int floor = 0; floor < levelNbre; floor++) {
                    //creation des sous dossiers pour chaque étage de la pyramide
                    String outPath = outputDirectory.getAbsolutePath()+"/"+coeffX[floor]+"_"+coeffY[floor];
                    //on defini les positions de depart x et y des tuiles par rapport a la surface totale
                    int tminx = ((tileRegion.x - globalRegion.x)/coeffX[floor] + tileWidth - 1)  / tileWidth;
                    int tminy = ((tileRegion.y - globalRegion.y)/coeffY[floor] + tileHeight - 1) / tileHeight;
                    //si besoin de resamplé on resample
                    if (tilesubSampling.width == coeffX[floor] && tilesubSampling.height == coeffY[floor]) {
                        wri       = renderedImage;
                        minx      = rIMinX;
                        miny      = rIMinY;
                        resWidth  = rIWidth;
                        resHeight = rIHeight;
                    } else {
                        //creation de l'interpolateur
                        Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
                        //creation du mathTransform
                        MathTransform mt            = new AffineTransform2D(1.0/coeffX[floor], 0, 0, 1.0/coeffY[floor], 0, 0);
                        //creation renderedimage boundary de rendu
                        minx      = (int) (rIMinX   / coeffX[floor]);
                        miny      = (int) (rIMinY   / coeffY[floor]);
                        resWidth  = (int) (rIWidth  / coeffX[floor]);
                        resHeight = (int) (rIHeight / coeffY[floor]);
                        tileRegion.setBounds((int)(tileRegion.x/coeffX[floor]), (int)(tileRegion.y/coeffY[floor]), (int)(tileRegion.width/coeffX[floor]), (int)(tileRegion.height/coeffY[floor]));
                        // creation de l'image resamplée
                        wri = new TiledImage(minx, miny, resWidth, resHeight, minx, miny, new BandedSampleModel(datatype, resWidth, resHeight, numband), colorMod);
                        //on resample
                        Resample resample  = new Resample(mt,(WritableRenderedImage) wri, interpolation, fillValue);
                        //on rempli l'image
                        resample.fillImage();
                    }
                    maxx = minx + resWidth;
                    maxy = miny + resHeight;
                    //nbre de tuile suivant x
                    nbrTX     = (resWidth + tileWidth - 1)   / tileWidth;
                    //nbre de tuile suivant y
                    nbrTY     = (resHeight + tileHeight - 1) / tileHeight;
                    final int tileGridXOffset = wri.getTileGridXOffset();
                    final int tileGridYOffset = wri.getTileGridYOffset();
                    //on resample, ecrit sur le disk et insert dans l'arbre pour chaque tuile
                    for (int ity = 0; ity<nbrTY; ity++) {
                        int tempMinx = minx;
                        for (int itx = 0; itx < nbrTX; itx++) {
                            final int tmaxx = Math.min(tempMinx + tileWidth, maxx);
                            final int tmaxy = Math.min(miny + tileHeight, maxy);
                            //on creer la tuile
                            TiledImage tuile = new TiledImage(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny, tileGridXOffset, tileGridYOffset, new BandedSampleModel(datatype, resWidth/*tmaxx - tempMinx*/, resHeight/*tmaxy - miny*/, numband), colorMod);
                            //on rempli la tuile
                            areaTile.setBounds(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny);
                            //recopie
                            final PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(wri, tuile, areaTile);
                            while (destPix.next()) destPix.setSample(destPix.getSample());
                            //ECRITURE
                            //on genere un nom
                            int slabIndexX = (tminx+itx)/slabWidth;
                            int slabIndexY = (tminy+ity)/slabHeight;
                            //a voir pour les nom des tuiles
                            String namePTile = outPath+"/"+slabIndexX+"_"+slabIndexY+"/"+(tminx+itx-slabIndexX*slabWidth)+"_"+(tminy+ity-slabIndexY*slabHeight)+"."+outputFormatName;
                            File imgOutPutPath = new File(namePTile);
                            ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
                            imgWriter.write(tuile);
                            imgWriter.dispose();
                            //FIN ECRITURE
                            //tuile suivante en x
                            tempMinx += tileWidth;
                        }
                        //ligne de tuile suivante
                        miny += tileHeight;
                    }
                }
            }
        }
        return null;
    }


    /**
     *
     * @param interpolationCase
     * @param lanczosWindow
     */
    public TileManager createTileManager2(TileManager[] originalMosaic) throws IOException, NoninvertibleTransformException, TransformException {
        ArgumentChecks.ensureNonNull("originalMosaic", originalMosaic);
        if(outputDirectory == null)
            throw new IllegalStateException("caller must set output directory");
        if (coeffX == null || coeffY == null)
            throw new IllegalStateException("caller must set resampling coefficients");
        if (tileHeight == 0 || tileWidth == 0)
            throw new IllegalStateException("caller must set tile dimension");
        if (slabHeight == 0 || slabWidth == 0)
            throw new IllegalStateException("caller must set slab dimension");
        if (interpolationCase == null || lanczosWindow == 0)
            throw new IllegalStateException("caller must set interpolation properties");

        final Rectangle areaTile = new Rectangle();
        final int levelNbre = coeffX.length;

        /*
         * Clean destination directory.
         */
        cleanDirectory(outputDirectory);

        SampleModel sampleMod;
        ColorModel colorMod;
        RenderedImage wri;
        Rectangle globalRegion = new Rectangle();
        //pour chaque etage de tile manager
        for (TileManager tileManager : originalMosaic) {
            //on defini la region globale de l'ensemble des tuiles
            globalRegion  = tileManager.getRegion();

            Collection<Tile> listTile = tileManager.getTiles();
            //pour chaque image de chacun des tiles manager
            for (Tile tile : listTile) {

                //on recup la region quelle occupe
                Rectangle tileRegion        = tile.getRegion();
                //on recupere l'etage courant
                Dimension tilesubSampling   = tile.getSubsampling();
                ImageReader imgReader       = tile.getImageReader();
                RenderedImage renderedImage = imgReader.read(0);
                imgReader.dispose();

                //on recupere ces données perso
                colorMod     = renderedImage.getColorModel();
                sampleMod    = renderedImage.getSampleModel();
                int datatype = sampleMod.getDataType();
                int numband  = sampleMod.getNumBands();
                int rIMinX   = renderedImage.getMinX();
                int rIMinY   = renderedImage.getMinY();
                int rIWidth  = renderedImage.getWidth();
                int rIHeight = renderedImage.getHeight();

                int minx,miny, maxx, maxy, resWidth, resHeight, nbrTX, nbrTY;

                //pour chaque etage de pyramide on regarde s'il faut la resamplée
                for (int floor = 0; floor < levelNbre; floor++) {
                    //creation des sous dossiers pour chaque étage de la pyramide
                    String outPath = outputDirectory.getAbsolutePath()+"/"+coeffX[floor]+"_"+coeffY[floor];
                    new File(outPath).mkdirs();
                    //on defini les positions de depart x et y des tuiles par rapport a la surface totale
                    int tminx = ((tileRegion.x - globalRegion.x)/coeffX[floor] + tileWidth - 1)  / tileWidth;
                    int tminy = ((tileRegion.y - globalRegion.y)/coeffY[floor] + tileHeight - 1) / tileHeight;
                    //si besoin de resamplé on resample
                    if (tilesubSampling.width == coeffX[floor] && tilesubSampling.height == coeffY[floor]) {
                        wri       = renderedImage;
                        minx      = rIMinX;
                        miny      = rIMinY;
                        resWidth  = rIWidth;
                        resHeight = rIHeight;
                    } else {
                        //creation de l'interpolateur
                        Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
                        //creation du mathTransform
                        MathTransform mt            = new AffineTransform2D(1.0/coeffX[floor], 0, 0, 1.0/coeffY[floor], 0, 0);
                        //creation renderedimage boundary de rendu
                        minx      = (int) (rIMinX   / coeffX[floor]);
                        miny      = (int) (rIMinY   / coeffY[floor]);
                        resWidth  = (int) (rIWidth  / coeffX[floor]);
                        resHeight = (int) (rIHeight / coeffY[floor]);
                        tileRegion.setBounds((int)(tileRegion.x/coeffX[floor]), (int)(tileRegion.y/coeffY[floor]), (int)(tileRegion.width/coeffX[floor]), (int)(tileRegion.height/coeffY[floor]));
                        // creation de l'image resamplée
                        wri = new TiledImage(minx, miny, resWidth, resHeight, minx, miny, new BandedSampleModel(datatype, resWidth, resHeight, numband), colorMod);
                        //on resample
                        Resample resample  = new Resample(mt,(WritableRenderedImage) wri, interpolation, fillValue);
                        //on rempli l'image
                        resample.fillImage();
                    }
                    maxx = minx + resWidth;
                    maxy = miny + resHeight;
                    //nbre de tuile suivant x
                    nbrTX     = (resWidth + tileWidth - 1)   / tileWidth;
                    //nbre de tuile suivant y
                    nbrTY     = (resHeight + tileHeight - 1) / tileHeight;
                    final int tileGridXOffset = wri.getTileGridXOffset();
                    final int tileGridYOffset = wri.getTileGridYOffset();
                    //on resample, ecrit sur le disk et insert dans l'arbre pour chaque tuile
                    for (int ity = 0; ity<nbrTY; ity++) {
                        int tempMinx = minx;
                        for (int itx = 0; itx < nbrTX; itx++) {
                            final int tmaxx = Math.min(tempMinx + tileWidth, maxx);
                            final int tmaxy = Math.min(miny + tileHeight, maxy);
                            //on creer la tuile
                            TiledImage tuile = new TiledImage(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny, tileGridXOffset, tileGridYOffset, new BandedSampleModel(datatype, resWidth/*tmaxx - tempMinx*/, resHeight/*tmaxy - miny*/, numband), colorMod);
                            //on rempli la tuile
                            areaTile.setBounds(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny);
                            //recopie
                            final PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(wri, tuile, areaTile);
                            while (destPix.next()) destPix.setSample(destPix.getSample());
                            //ECRITURE
                            //on genere un nom
                            String namePTile = outPath+"/"+(tminx+itx)+"_"+(tminy+ity)+"."+outputFormatName;
                            File imgOutPutPath = new File(namePTile);
                            ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
                            imgWriter.write(tuile);
                            imgWriter.dispose();
                            //FIN ECRITURE
                            //tuile suivante en x
                            tempMinx += tileWidth;
                        }
                        //ligne de tuile suivante
                        miny += tileHeight;
                    }
                }
            }
        }
        return new PyramidTileManager(outputDirectory, globalRegion, new Dimension(tileWidth, tileHeight), outputFormatName);
    }

    /**
     *
     * @param interpolationCase
     * @param lanczosWindow
     * @param fillValue
     */
    public void setInterpolationProperties(InterpolationCase interpolationCase, int lanczosWindow, double...fillValue) {
        ArgumentChecks.ensureNonNull("interpolationCase", interpolationCase);
        if (lanczosWindow < 2)
            throw new IllegalArgumentException("lanczos window must be up to length 2");
        ArgumentChecks.ensureNonNull("fillValue", fillValue);
        this.interpolationCase = interpolationCase;
        this.lanczosWindow     = lanczosWindow;
        this.fillValue         = fillValue;
    }

    /**
     *
     * @param coeffX
     * @param coeffY
     */
    public void setSubsampling(int[] coeffX, int[] coeffY) {
        ArgumentChecks.ensureNonNull("coeffX", coeffX);
        ArgumentChecks.ensureNonNull("coeffY", coeffY);
        if (coeffX.length != coeffY.length)
            throw new IllegalArgumentException("coeffX and coeffY table will be able to have same length");
        this.coeffX    = coeffX;
        this.coeffY    = coeffY;
    }

    /**
     * <p>Set tile size which will be create.<br/>
     * You must set {@code null} to set {@link #DEFAULT_TILE_SIZE} = 256.</p>
     *
     * @param tileSize tile size.
     */
    public void setTileSize(Dimension tileSize) {
        tileWidth  = (tileSize == null) ? DEFAULT_TILE_SIZE : Math.max(tileSize.width, MIN_TILE_SIZE);
        tileHeight = (tileSize == null) ? DEFAULT_TILE_SIZE : Math.max(tileSize.height, MIN_TILE_SIZE);
    }

    /**
     * <p>Define slab size.<br/>
     * Slab width define tile number in X direction per slab.<br/>
     * Slab height define tile number in Y direction per slab.<br/>
     * You must set {@code null} to set {@link #DEFAULT_SLAB_SIZE} = 16.</p>
     *
     * @param slabSize
     */
    public void setSlabSize(Dimension slabSize) {
        slabWidth  = (slabSize == null) ? DEFAULT_SLAB_SIZE : Math.min(MAX_SLAB_SIZE, Math.max(DEFAULT_SLAB_SIZE, slabSize.width));
        slabHeight = (slabSize == null) ? DEFAULT_SLAB_SIZE : Math.min(MAX_SLAB_SIZE, Math.max(DEFAULT_SLAB_SIZE, slabSize.height));
    }


    /**
     * <p>Sets the directory where tiles will be written.<br/>
     * May be a relative or absolute path, or null (the default) for current directory.</p>
     *
     * @param outputDirectory directory path which will contain tile(s).
     */
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = (outputDirectory == null) ? new File("") : outputDirectory;
        //on check si les dossiers existes
        if (!outputDirectory.exists()) outputDirectory.mkdirs();
    }

    /**
     * Set output image format name.
     *
     * @param outputFormatName name of writing image extension.
     */
    public void setOutputFormatName(String outputFormatName) {
        ArgumentChecks.ensureNonNull("outputFormatName", outputFormatName);
        this.outputFormatName = outputFormatName;
    }

    /**
     * Return format tile size.
     *
     * @return format tile size.
     */
    public TileLayout getTileLayout(){
        return TileLayout.CONSTANT_TILE_SIZE;
    }

    /**
     * Clean all subDirectory of {@link parentDirectory}.
     *
     * @param parentDirectory directory which will be cleaned.
     */
    private void cleanDirectory(File parentDirectory) {
        for (File file : parentDirectory.listFiles()) {
            if (file.isDirectory()) cleanDirectory(file);
            file.delete();
        }
    }

    /**
     *
     * @param globalArea
     * @throws IOException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    private void writeProperties(Rectangle globalArea) throws IOException, TransformerConfigurationException, TransformerException, ParserConfigurationException {

        String outputPath = outputDirectory.getAbsolutePath()+"/"+"properties.xml";
        File outXml = new File(outputPath);

        //XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructeur = dbf.newDocumentBuilder();
        org.w3c.dom.Document document = constructeur.newDocument();

        // Propriétés du DOM
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        Element properties = document.createElement("properties");
        properties.appendChild(document.createComment("TileManager properties"));

        //image properties
        Element image = document.createElement("image");
        Element minX = document.createElement("minX");
        minX.setTextContent(String.valueOf(globalArea.x));
        Element minY = document.createElement("minY");
        minY.setTextContent(String.valueOf(globalArea.y));
        Element imgwidth = document.createElement("width");
        imgwidth.setTextContent(String.valueOf(globalArea.width));
        Element imgheight = document.createElement("height");
        imgheight.setTextContent(String.valueOf(globalArea.height));
        image.appendChild(minX);
        image.appendChild(minY);
        image.appendChild(imgwidth);
        image.appendChild(imgheight);
        properties.appendChild(image);

        //slab properties
        Element slab = document.createElement("slab");
        Element slabwidth = document.createElement("width");
        slabwidth.setTextContent(String.valueOf(slabWidth));
        Element slabheight = document.createElement("height");
        slabheight.setTextContent(String.valueOf(slabHeight));
        slab.appendChild(slabwidth);
        slab.appendChild(slabheight);
        properties.appendChild(slab);

        //tile properties
        Element tile = document.createElement("tile");
        Element tilewidth = document.createElement("width");
        tilewidth.setTextContent(String.valueOf(tileWidth));
        Element tileheight = document.createElement("height");
        tileheight.setTextContent(String.valueOf(tileHeight));
        tile.appendChild(tilewidth);
        tile.appendChild(tileheight);
        properties.appendChild(tile);

        //compression properties
        Element compression = document.createElement("compression");
//        org.w3c.dom.Element prefix = document.createElement("prefix");
//        prefix.setTextContent(String.valueOf("le prefix"));
        Element format = document.createElement("format");
        format.setTextContent(outputFormatName);
        compression.appendChild(format);
        properties.appendChild(compression);
        //add in document
        document.appendChild(properties);

        DOMSource domSource = new DOMSource(document);
        FileWriter filewriter = new FileWriter(outXml);
        StreamResult streamResult = new StreamResult(filewriter);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}
