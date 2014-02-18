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
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.media.jai.TiledImage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
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
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.w3c.dom.Element;
/**
 * <p>Create and write an Image pyramid.<br/><br/>
 * Pyramid builder work from a TileManager which represent pyramid base.<br/>
 * To get mosaic from image list, see org.geotoolkit.image.io.mosaic<br/>
 * For the next example this TileManager will called "originalMosaic".<br/><br/>
 * Use example :<br/>
 * {@code PyramidBuilder pyramid = new PyramidBuilder();}<br/>
 * {@code pyramid.setInterpolationProperties(InterpolationCase.BILINEAR, 2,0);}<br/>
 * {@code pyramid.setOutputDirectory(new File("../output_directory/"));}<br/>
 * {@code pyramid.setOutputNames("chosen_prefix", "TIFF");}<br/>
 * {@code pyramid.setSubsampling(new int[]{1,2}, new int[]{1,2});}<br/>
 * {@code pyramid.setTileSize(new Dimension(100, 150));//or null for default value 256x256}<br/>
 * {@code pyramid.setSlabSize(new Dimension(20, 25));//or null for default value 16x16}<br/>
 * {@code TileManager ptm = pyramid.createTileManager(originalMosaic);}</p>
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

    /**
     * Minimum and default slab size.
     */
    private static final int DEFAULT_SLAB_SIZE = 16;

    /**
     * Maximum slab size.
     */
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
     * Written tile extention.
     */
    private String outputPrefixName = null;


    private FilenameFormatter formatter = null;
    /**
     * Resampling coefficient in X direction.
     */
    private int[] coeffX = null;

    /**
     * Resampling coefficient in Y direction.
     */
    private int[] coeffY = null;

    /**
     * Tile number within each slab in X direction.
     */
    private int slabWidth;

    /**
     * Tile number within each slab in Y direction.
     */
    private int slabHeight;

    /**
     * Interpolation properties.
     */
    InterpolationCase interpolationCase = null;
    int lanczosWindow = -1;

    /**
     * Construct a default pyramid builder.
     */
    public PyramidBuilder() {
        formatter = new FilenameFormatter();
    }

    /**
     * <p>Create a pyramid from specified image group.</p>
     *
     * @param originalMosaic TileManager which contains image base mosaic which will be pyramid.
     * @return an appropriate TileManager to search within builded pyramid.
     * @throws IOException If it was necessary to fetch an image dimension.
     * @throws TransformerException If an unrecoverable error occurs during the course of the transformation.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws TransformException if MathTransform use during resampling is non invertible.
     */
    public TileManager createTileManager(TileManager originalMosaic) throws IOException, TransformerException, ParserConfigurationException, TransformException {
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
        final int levelNbre = coeffX.length;

        /*
         * Clean destination directory.
         */
        cleanDirectory(outputDirectory);

        final Rectangle globalRegion = originalMosaic.getRegion();

        //write xml
        writeProperties(globalRegion);

        final int slabSizeX = slabWidth  * tileWidth;
        final int slabSizeY = slabHeight * tileHeight;

        final String outputDirectoryPath = outputDirectory.getAbsolutePath();
        //architecture creation
        for (int i = 0; i<coeffX.length; i++) {
            int nbreSlabX = (globalRegion.width/coeffX[i] + slabSizeX - 1)/slabSizeX;
            int nbreSlabY = (globalRegion.height/coeffY[i] + slabSizeY - 1)/slabSizeY;
            final String archiPath = outputDirectoryPath+"/"+coeffX[i]+"_"+coeffY[i]+"/";
            for (int y = 0; y<nbreSlabY; y++) {
                for (int x = 0; x<nbreSlabX; x++) {
                    new File(archiPath+x+"_"+y+"/").mkdirs();
                }
            }
        }

        Rectangle areatuile                    = new Rectangle();
        final Rectangle slabAreaBase           = new Rectangle();
        final Rectangle tuileAreaBase          = new Rectangle();
        final Dimension subsamplingPyramidBase = new Dimension(1, 1);
        final ImageReadParam imgRParam         = new ImageReadParam();

        for (int floor = 0; floor<levelNbre; floor++) {
            final int subX = coeffX[floor];
            final int subY = coeffY[floor];
            final String outPath   = outputDirectoryPath+"/"+subX+"_"+subY+"/";
            final MathTransform mt = new AffineTransform2D(1.0 / coeffX[floor], 0, 0, 1.0 / subY, 0, 0);
            final int subGRminx    = globalRegion.x      / subX;
            final int subGRminy    = globalRegion.y      / subY;
            final int subGRwidth   = globalRegion.width  / subX;
            final int subGRheight  = globalRegion.height / subY;
            final int idSlabMaxX   = (subGRwidth  + slabSizeX - 1) / slabSizeX;
            final int idSlabMaxY   = (subGRheight + slabSizeY - 1) / slabSizeY;

            int slabMinY = subGRminy;
            //slab by slab
            for (int idSY = 0; idSY < idSlabMaxY;idSY++) {
                int slabMinX = subGRminx;
                for (int idSX = 0; idSX < idSlabMaxX; idSX++) {

                    final String outSlagPath = outPath+idSX+"_"+idSY+"/";
                    //current slab coordinates
                    final int slabMaxX = Math.min(subGRminx + subGRwidth,  slabMinX + slabSizeX);
                    final int slabMaxY = Math.min(subGRminy + subGRheight, slabMinY + slabSizeY);
                    final int w        = slabMaxX - slabMinX;
                    final int h        = slabMaxY - slabMinY;

                    //X direction tile number
                    final int nbrTX = (w + tileWidth - 1)  / tileWidth;
                    //Y direction tile number
                    final int nbrTY = (h + tileHeight - 1) / tileHeight;
                    int miny = slabMinY;
                    List<Rectangle> listTileArea = new ArrayList<Rectangle>(nbrTX*nbrTY);
                    for (int ity = 0; ity<nbrTY; ity++) {
                        int tempMinx = slabMinX;
                        for (int itx = 0; itx < nbrTX; itx++) {
                            final int tw = Math.min(tempMinx + tileWidth, slabMaxX) - tempMinx;
                            final int th = Math.min(miny + tileHeight, slabMaxY)    - miny;
                            listTileArea.add(new Rectangle(tempMinx, miny, tw, th));
                            //next tile X position
                            tempMinx += tileWidth;
                        }
                        //next tile row
                        miny += tileHeight;
                    }
                    //slab size at pyramid base
                    slabAreaBase.setBounds(slabMinX*subX, slabMinY*subY, slabSizeX*subX, slabSizeY*subY);
                    final Collection<Tile> listTile = originalMosaic.getTiles(slabAreaBase, subsamplingPyramidBase, true);

      unnecessary : for (Tile tile : listTile) {

                        final Rectangle tileRegion   = tile.getRegion();
                        final Rectangle intersection = tileRegion.intersection(slabAreaBase);
                        if ((intersection.width  / (tileWidth  * subX) < 2)
                         && (intersection.height / (tileHeight * subY) < 2))
                            continue unnecessary;
                        final ImageReader imgReader  = tile.getImageReader();
                        ColorModel cm  = null;
                        SampleModel sm = null;
                        final int ltaSize_1 = listTileArea.size()-1;
                        for (int idl = ltaSize_1; idl >= 0; idl--) {
                            final Rectangle tA = listTileArea.get(idl);
                            tuileAreaBase.setBounds(tA.x*subX, tA.y*subY, tA.width*subX, tA.height*subY);
                            if (intersection.contains(tuileAreaBase)) {
                                listTileArea.remove(idl);
                                WritableRenderedImage destImg;
                                areatuile.setBounds(tuileAreaBase.x-tileRegion.x, tuileAreaBase.y-tileRegion.y, tuileAreaBase.width, tuileAreaBase.height);
                                imgRParam.setSourceRegion(areatuile);
                                if (subX == 1 && subY == 1) {
                                    destImg = imgReader.read(0, imgRParam);
                                    imgReader.dispose();
                                } else {
                                    final RenderedImage renderImage = imgReader.read(0, imgRParam);
                                    imgReader.dispose();
                                    if (cm == null) {
                                        cm = renderImage.getColorModel();
                                        sm = cm.createCompatibleSampleModel(tileWidth, tileHeight);
                                    }
                                    //interpolator
                                    final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderImage), interpolationCase, lanczosWindow);
                                    destImg = new TiledImage(0, 0, tileWidth, tileHeight, 0, 0, sm, cm);
                                    //resampling object
                                    final Resample resample  = new Resample(mt.inverse(),(WritableRenderedImage) destImg, interpolation, fillValue);//noninvert
                                    //fill empty resampled image
                                    resample.fillImage();
                                }
                                //generate name
                                final String namePTile      = outSlagPath+formatter.generateFilename(floor, (tA.x-slabMinX)/tileWidth, (tA.y-slabMinY)/tileHeight)+"."+outputFormatName;
                                final File imgOutPutPath    = new File(namePTile);
                                final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
                                imgWriter.write(destImg);
                                imgWriter.dispose();
                            }
                        }
                    }

       noTileFind : for (Rectangle tuile : listTileArea) {
                        final RenderedImage destImg = getIntersectTiles(tuile, subX, subY, originalMosaic);
                        if (destImg == null) continue noTileFind;
                        //generate name
                        final String namePTile      = outSlagPath+formatter.generateFilename(floor, (tuile.x-slabMinX)/tileWidth, (tuile.y-slabMinY)/tileHeight)+"."+outputFormatName;
                        //writing
                        final File imgOutPutPath    = new File(namePTile);
                        final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
                        imgWriter.write(destImg);
                        imgWriter.dispose();
                    }
                    //next slab X position
                    slabMinX += slabSizeX;
                }
                //next slab row
                slabMinY += slabSizeY;
            }
        }
        return new PyramidTileManager(outputDirectory, globalRegion.x, globalRegion.y, globalRegion.width, globalRegion.height,coeffX,coeffY, slabWidth, slabHeight, tileWidth, tileHeight,outputPrefixName, outputFormatName);
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
    private WritableRenderedImage getIntersectTiles (Rectangle slabRegion, int subsamplingX, int subsamplingY, TileManager tileManager) throws IOException, NoninvertibleTransformException, TransformException {

        //get all tiles which we will be able to need.
        final Collection<Tile> listTile = tileManager.getTiles(new Rectangle(slabRegion.x*subsamplingX, slabRegion.y*subsamplingY, slabRegion.width*subsamplingX, slabRegion.height*subsamplingY), new Dimension(1, 1), true);

        ColorModel colorMod = null;
        int numband = 0;
        WritableRenderedImage intersectTile = null;
        PixelIterator inputPix;
        Rectangle areaTile    = new Rectangle();
        PixelIterator destPix = null;
        //resampling affinetransform
        final AffineTransform mttranslate = new AffineTransform(1.0 / subsamplingX, 0, 0, 1.0 / subsamplingY, 0, 0);
        final ImageReadParam imgParam     = new ImageReadParam();

        for (Tile tile : listTile) {
            final ImageReader imgReader = tile.getImageReader();
            final Rectangle tileRegion  = tile.getRegion();

            //real intersection
            int iminx = Math.max(tileRegion.x, slabRegion.x * subsamplingX);
            int iminy = Math.max(tileRegion.y, slabRegion.y * subsamplingY);
            int imaxx = Math.min(tileRegion.x + tileRegion.width,  (slabRegion.x + slabRegion.width)  * subsamplingX);
            int imaxy = Math.min(tileRegion.y + tileRegion.height, (slabRegion.y + slabRegion.height) * subsamplingY);

            areaTile.setBounds(iminx - tileRegion.x, iminy - tileRegion.y, imaxx - iminx, imaxy - iminy);
            imgParam.setSourceRegion(areaTile);
            final WritableRenderedImage renderedImage = imgReader.read(0, imgParam);
            imgReader.dispose();

            //resampled intersection
            iminx /= subsamplingX;
            iminy /= subsamplingY;
            imaxx += (subsamplingX - 1);
            imaxy += (subsamplingY - 1);
            imaxx /= subsamplingX;
            imaxy /= subsamplingY;

            //image dimension
            final int imgw = imaxx - iminx;
            final int imgh = imaxy - iminy;

            if (intersectTile == null) {
                colorMod      = renderedImage.getColorModel();
                numband       = renderedImage.getSampleModel().getNumBands();
                intersectTile = new TiledImage(slabRegion.x, slabRegion.y, slabRegion.width, slabRegion.height, slabRegion.x, slabRegion.y, colorMod.createCompatibleSampleModel(slabRegion.width, slabRegion.height), colorMod);
                destPix       = PixelIteratorFactory.createRowMajorWriteableIterator(intersectTile, intersectTile, slabRegion);
            }

            if (!renderedImage.getColorModel().equals(colorMod))
                throw new IllegalStateException("tile from basic mosaic haven't got same color model");

            //Slab resampling from original mosaic tiles.
            RenderedImage wri;
            if (subsamplingX == 1 && subsamplingY == 1) {
                wri      = renderedImage;
                inputPix = PixelIteratorFactory.createRowMajorIterator(wri);
                for (int y = iminy; y < imaxy; y++) {
                    destPix.moveTo(iminx, y, 0);
                    for (int x = iminx; x < imaxx; x++) {
                        for (int b = 0; b < numband; b++) {
                            inputPix.next();
                            destPix.setSample(inputPix.getSample());
                            destPix.next();
                        }
                    }
                }
            } else {
                //interpolator
                final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
                //appropriate interpolation MathTransform
                mttranslate.setTransform(1.0/subsamplingX, 0, 0, 1.0/subsamplingY, iminx, iminy);
                areaTile.setBounds(iminx, iminy, imgw, imgh);
                //resampling object
                final Resample resample  = new Resample(MathTransforms.linear(mttranslate).inverse(), intersectTile, areaTile, interpolation, fillValue);
                //fill empty resampled image
                resample.fillImage();
            }
        }
        return intersectTile;
    }

    /**
     * <p> Define appropriate properties to build pyramid.<br/>
     * To build each pyramid level interpolation is used.<br/><br/>
     *
     * note : if chosen interpolation isn't lanczos "lanczoswindow" parameter has no impact.
     *
     * @param interpolationCase chosen interpolation.
     * @param lanczosWindow window size only use to Lanczos interpolation.
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
     * <p>Specify different subsample pyramid level.<br/>
     * Table length specify pyramid level number.<br/>
     * Subsample table in X and Y direction will be able to have same length.</p>
     *
     * @param coeffX table which represent subsample coefficient in X direction.
     * @param coeffY table which represent subsample coefficient in Y direction.
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
     * You must set {@code null} to set {@link #DEFAULT_SLAB_SIZE} = 16.<br/>
     * Also define tile number which will be within each sub directory in destination directory</p>
     *
     * @param slabSize slab size.
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
        if (!outputDirectory.exists()) outputDirectory.mkdirs();
    }

    /**
     * Set output image format name.
     *
     * @param outputFormatName name of writing image extension.
     */
    public void setOutputNames(String outPutPrefixName, String outputFormatName) {
        ArgumentChecks.ensureNonNull("outPutPrefixName", outPutPrefixName);
        ArgumentChecks.ensureNonNull("outputFormatName", outputFormatName);
        this.outputFormatName = outputFormatName;
        this.outputPrefixName = outPutPrefixName;
        formatter.ensurePrefixSet(outPutPrefixName);
    }

    /**
     * Return format tile size.
     *
     * @return format tile size.
     */
    public TileLayout getTileLayout() {
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
     * Write in XML file pyramid properties in specified output directory.
     *
     * @param globalArea global area of pyramid base.
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested.
     * @throws IOException  if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason.
     * @throws TransformerException If an unrecoverable error occurs during the course of the transformation.
     */
    private void writeProperties(Rectangle globalArea) throws ParserConfigurationException, IOException, TransformerException  {

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
            final Element mosaic = document.createElement("mosaic");
            {
                final Element minX = document.createElement("minX");
                minX.setTextContent(String.valueOf(globalArea.x));
                mosaic.appendChild(minX);
                final Element minY = document.createElement("minY");
                minY.setTextContent(String.valueOf(globalArea.y));
                mosaic.appendChild(minY);
                final Element imgwidth = document.createElement("width");
                imgwidth.setTextContent(String.valueOf(globalArea.width));
                mosaic.appendChild(imgwidth);
                final Element imgheight = document.createElement("height");
                imgheight.setTextContent(String.valueOf(globalArea.height));
                mosaic.appendChild(imgheight);
            }
            properties.appendChild(mosaic);
        }
        {
            //subsampling properties
            final Element subsampling = document.createElement("subsampling");
            {
                final Element level = document.createElement("level");
                level.setTextContent(String.valueOf(coeffX.length));
                subsampling.appendChild(level);
                for (int floor = 0; floor < coeffX.length; floor++) {
                    final Element subx = document.createElement("subx");
                    subx.setTextContent(String.valueOf(coeffX[floor]));
                    final Element suby = document.createElement("suby");
                    suby.setTextContent(String.valueOf(coeffY[floor]));
                    subsampling.appendChild(subx);
                    subsampling.appendChild(suby);
                }
            }
            properties.appendChild(subsampling);
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
                final Element prefix = document.createElement("prefix");
                prefix.setTextContent(String.valueOf(outputPrefixName));
                compression.appendChild(prefix);
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
