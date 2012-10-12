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
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeFactory;
import org.geotoolkit.index.tree.nodefactory.TreeNodeFactory;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;
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
    public TileManager createTileManager(TileManager originalMosaic) throws IOException, NoninvertibleTransformException, TransformException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
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

        SampleModel sampleMod = null;
        ColorModel colorMod;
        Rectangle globalRegion = originalMosaic.getRegion();

        //write xml
        writeProperties(globalRegion);


        final int slabSizeX = slabWidth  * tileWidth;
        final int slabSizeY = slabHeight * tileHeight;

        //creer l'architecture
        for (int i = 0; i<coeffX.length; i++) {
            int nbreSlabX = (globalRegion.width/coeffX[i] + slabSizeX - 1)/slabSizeX;
            int nbreSlabY = (globalRegion.height/coeffY[i] + slabSizeY - 1)/slabSizeY;
            String archiPath = outputDirectory.getAbsolutePath()+"/"+coeffX[i]+"_"+coeffY[i]+"/";
            for (int y = 0; y<nbreSlabY; y++) {
                for (int x = 0; x<nbreSlabX; x++) {
                    new File(archiPath+x+"_"+y+"/").mkdirs();
                }
            }
        }

        final String outputDirectoryPath = outputDirectory.getAbsolutePath();
        //pour chaque etage de tile manager

        for (int floor = 0; floor<levelNbre; floor++) {
            String outPath = outputDirectoryPath+"/"+coeffX[floor]+"_"+coeffY[floor]+"/";

            int subGRminx   = globalRegion.x      / coeffX[floor];
            int subGRminy   = globalRegion.y      / coeffY[floor];
            int subGRwidth  = globalRegion.width  / coeffX[floor];
            int subGRheight = globalRegion.height / coeffY[floor];

            int idSlabMaxX = (subGRwidth  + slabSizeX - 1) / slabSizeX;
            int idSlabMaxY = (subGRheight + slabSizeY - 1) / slabSizeY;

            int slabMinY = subGRminy;
            //on passe dalle par dalle
            for (int idSY = 0; idSY < idSlabMaxY;idSY++) {
                int slabMinX = subGRminx;
      noSlab :  for (int idSX = 0; idSX < idSlabMaxX; idSX++) {

                    String outSlagPath = outPath+idSX+"_"+idSY+"/";
                    //coordonnées de la dalle courrente
//                    int minx = subGRminx + idSX * slabSizeX;//ici c constant je crois kon pourrai juste faire  des +=
//                    int csminy = subGRminy + idSY * slabSizeY;
                    int slabMaxX = Math.min(subGRminx + subGRwidth,  slabMinX + slabSizeX);
                    int slabMaxY = Math.min(subGRminy + subGRheight, slabMinY + slabSizeY);
                    final Rectangle slabArea = new Rectangle(slabMinX, slabMinY, slabMaxX-slabMinX, slabMaxY-slabMinY);
                    //on pourrai faire rectangle temporaire
                    WritableRenderedImage slab  = getSlab(slabArea, coeffX[floor], coeffY[floor], originalMosaic);
                    if (slab == null) {
                        slabMinX += slabSizeX;
                        continue noSlab;
                    }

                    sampleMod    = slab.getSampleModel();
                    int datatype = sampleMod.getDataType();
                    colorMod     = slab.getColorModel();
                    int numband  = sampleMod.getNumBands();

                    //nbre de tuile suivant x
                    final int nbrTX = (slabMaxX-slabMinX + tileWidth - 1)  / tileWidth;
                    //nbre de tuile suivant y
                    final int nbrTY = (slabMaxY-slabMinY + tileHeight - 1) / tileHeight;
                    final int tileGridXOffset = slab.getTileGridXOffset();
                    final int tileGridYOffset = slab.getTileGridYOffset();
                    int miny = slabMinY;
                    //on resample, ecrit sur le disk et insert dans l'arbre pour chaque tuile
                    for (int ity = 0; ity<nbrTY; ity++) {
                        int tempMinx = slabMinX;
                        for (int itx = 0; itx < nbrTX; itx++) {
                            final int tmaxx = Math.min(tempMinx + tileWidth, slabMaxX);
                            final int tmaxy = Math.min(miny + tileHeight, slabMaxY);
                            //on creer la tuile
                            TiledImage tuile = new TiledImage(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny, tileGridXOffset, tileGridYOffset, new BandedSampleModel(datatype, slab.getWidth()/*tmaxx - tempMinx*/, slab.getHeight()/*tmaxy - miny*/, numband), colorMod);
                            //on rempli la tuile
                            areaTile.setBounds(tempMinx, miny, tmaxx - tempMinx, tmaxy - miny);
                            //recopie
                            final PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(slab, tuile, areaTile);
                            while (destPix.next()) destPix.setSample(destPix.getSample());
                            //ECRITURE
                            //on genere un nom
                            String namePTile = outSlagPath+itx+"_"+ity+"."+outputFormatName;
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
                    slabMinX += slabSizeX;
                }
                slabMinY += slabSizeY;
            }
        }
        return new PyramidTileManager(outputDirectory, globalRegion.x, globalRegion.y, globalRegion.width, globalRegion.height, slabWidth, slabHeight, tileWidth, tileHeight, outputFormatName);
    }

    /**
     * <p>Compute slab which will be tiled.<br/>
     * Find all basic tiles which will be able to need to construct this slab and resample them.<br/>
     * May return null if no tiles are found.</p>
     *
     * @param slabRegion slab dimension.
     * @param subsamplingX X direction slab resolution.
     * @param subsamplingY y direction slab resolution.
     * @param tileManager to recover needed tiles.
     * @return image representing slab which will be tiled.
     * @throws IOException
     * @throws NoninvertibleTransformException
     * @throws TransformException
     */
    private WritableRenderedImage getSlab (Rectangle slabRegion, int subsamplingX, int subsamplingY, TileManager tileManager) throws IOException, NoninvertibleTransformException, TransformException {

        //get all tiles which we will be able to need.
        Collection<Tile> listTile = tileManager.getTiles(new Rectangle(slabRegion.x*subsamplingX, slabRegion.y*subsamplingY, slabRegion.width*subsamplingX, slabRegion.height*subsamplingY), new Dimension(1, 1), true);

        ColorModel colorMod = null;
        int datatype = 0 , numband = 0;
        WritableRenderedImage slab = null;
        PixelIterator inputPix;
        PixelIterator destPix = null;

        for (Tile tile : listTile) {
            final ImageReader imgReader       = tile.getImageReader();
            final RenderedImage renderedImage = imgReader.read(0);
            final Rectangle tileRegion        = tile.getRegion();

            //real intersection
            int iminx = Math.max(tileRegion.x, slabRegion.x * subsamplingX);
            int iminy = Math.max(tileRegion.y, slabRegion.y * subsamplingY);
            int imaxx = Math.min(tileRegion.x + tileRegion.width,  (slabRegion.x + slabRegion.width)  * subsamplingX);
            int imaxy = Math.min(tileRegion.y + tileRegion.height, (slabRegion.y + slabRegion.height) * subsamplingY);

            //resampled intersection
            iminx /= subsamplingX;
            iminy /= subsamplingY;
            imaxx += (subsamplingX - 1);
            imaxy += (subsamplingY - 1);
            imaxx /= subsamplingX;
            imaxy /= subsamplingY;

            if (slab == null) {
                colorMod              = renderedImage.getColorModel();
                SampleModel sampleMod = renderedImage.getSampleModel();
                datatype = sampleMod.getDataType();
                numband  = sampleMod.getNumBands();
                slab     = new TiledImage(slabRegion.x, slabRegion.y, slabRegion.width, slabRegion.height, slabRegion.x, slabRegion.y, new BandedSampleModel(datatype, slabRegion.width, slabRegion.height, numband), colorMod);
                destPix  = PixelIteratorFactory.createRowMajorWriteableIterator(slab, slab, slabRegion);
            }

            //Slab resampling from original mosaic tiles.
            RenderedImage wri;
            if (subsamplingX == 1 && subsamplingY == 1) {
                wri       = renderedImage;
            } else {
                //image dimension
                final int imgx = iminx - tileRegion.x / subsamplingX;
                final int imgy = iminy - tileRegion.y / subsamplingY;
                final int imgw = imaxx - iminx;
                final int imgh = imaxy - iminy;
                //interpolator
                final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
                //resampling affinetransform
                final MathTransform mt            = new AffineTransform2D(1.0/subsamplingX, 0, 0, 1.0/subsamplingY, 0, 0);
                // empty resampled image
                wri = new TiledImage(imgx, imgy, imgw, imgh, imgx, imgy, new BandedSampleModel(datatype, imgw, imgh, numband), colorMod);
                //resampling object
                final Resample resample  = new Resample(mt,(WritableRenderedImage) wri, interpolation, fillValue);
                //fill empty resampled image
                resample.fillImage();
            }

            inputPix = PixelIteratorFactory.createRowMajorIterator(wri);

            //write in result slab
            for (int y = iminy; y<imaxy;y++) {
                destPix.moveTo(iminx, y, 0);
                for (int x = iminx; x<imaxx; x++) {
                    for (int b = 0; b < numband; b++) {
                        inputPix.next();
                        destPix.setSample(inputPix.getSample());
                        destPix.next();
                    }
                }
            }
        }
        return slab;
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

        final String outputPath = outputDirectory.getAbsolutePath()+"/"+"properties.xml";
        final File outXml = new File(outputPath);

        //XML
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = dbf.newDocumentBuilder();
        final org.w3c.dom.Document document = constructeur.newDocument();

        // Propriétés du DOM
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);

        final Element properties = document.createElement("properties");
        properties.appendChild(document.createComment("TileManager properties"));

        {
            //image properties
            final Element image = document.createElement("image");
            {
                final Element minX = document.createElement("minX");
                minX.setTextContent(String.valueOf(globalArea.x));
                image.appendChild(minX);
                final Element minY = document.createElement("minY");
                minY.setTextContent(String.valueOf(globalArea.y));
                image.appendChild(minY);
                final Element imgwidth = document.createElement("width");
                imgwidth.setTextContent(String.valueOf(globalArea.width));
                image.appendChild(imgwidth);
                final Element imgheight = document.createElement("height");
                imgheight.setTextContent(String.valueOf(globalArea.height));
                image.appendChild(imgheight);
            }
            properties.appendChild(image);
        }
        {
            //slab properties
            final Element slab = document.createElement("slab");
            {
                final Element slabwidth = document.createElement("width");
                slabwidth.setTextContent(String.valueOf(slabWidth));
                slab.appendChild(slabwidth);
                final Element slabheight = document.createElement("height");
                slabheight.setTextContent(String.valueOf(slabHeight));
                slab.appendChild(slabheight);
            }
            properties.appendChild(slab);
        }
        {
            //tile properties
            final Element tile = document.createElement("tile");
            {
                final Element tilewidth = document.createElement("width");
                tilewidth.setTextContent(String.valueOf(tileWidth));
                tile.appendChild(tilewidth);
                final Element tileheight = document.createElement("height");
                tileheight.setTextContent(String.valueOf(tileHeight));
                tile.appendChild(tileheight);
            }
            properties.appendChild(tile);
        }
        {
            //compression properties
            final Element compression = document.createElement("compression");
            {
//                final Element prefix = document.createElement("prefix");
//                prefix.setTextContent(String.valueOf("le prefix"));
//                compression.appendChild(prefix);
                final Element format = document.createElement("format");
                format.setTextContent(outputFormatName);
                compression.appendChild(format);
            }
            properties.appendChild(compression);
        }

        //add in document
        document.appendChild(properties);

        final DOMSource domSource = new DOMSource(document);
        final FileWriter filewriter = new FileWriter(outXml);
        final StreamResult streamResult = new StreamResult(filewriter);
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, streamResult);
    }
}
