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

    private int slabWidth;

    private int slabHeight;

    /**
     * Interpolation properties.
     */
    InterpolationCase interpolationCase = null;
    int lanczosWindow = -1;

    public PyramidBuilder() {
        formatter = new FilenameFormatter();
    }

//    /**
//     *
//     * @param interpolationCase
//     * @param lanczosWindow
//     */
//    public TileManager createTileManager(TileManager originalMosaic) throws IOException, NoninvertibleTransformException, TransformException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
//        ArgumentChecks.ensureNonNull("originalMosaic", originalMosaic);
//        if(outputDirectory == null)
//            throw new IllegalStateException("caller must set output directory");
//        if (coeffX == null || coeffY == null)
//            throw new IllegalStateException("caller must set resampling coefficients");
//        if (tileHeight == 0 || tileWidth == 0)
//            throw new IllegalStateException("caller must set tile dimension");
//        if (slabHeight == 0 || slabWidth == 0)
//            throw new IllegalStateException("caller must set slab dimension");
//        if (interpolationCase == null || lanczosWindow == 0)
//            throw new IllegalStateException("caller must set interpolation properties");
//        final Rectangle areaTemp = new Rectangle();
//        final int levelNbre = coeffX.length;
//
//        /*
//         * Clean destination directory.
//         */
//        cleanDirectory(outputDirectory);
//
//        final Rectangle globalRegion = originalMosaic.getRegion();
//
//        //write xml
//        writeProperties(globalRegion);
//
//        final int slabSizeX = slabWidth  * tileWidth;
//        final int slabSizeY = slabHeight * tileHeight;
//
//        final String outputDirectoryPath = outputDirectory.getAbsolutePath();
//        //architecture creation
//        for (int i = 0; i<coeffX.length; i++) {
//            int nbreSlabX = (globalRegion.width/coeffX[i] + slabSizeX - 1)/slabSizeX;
//            int nbreSlabY = (globalRegion.height/coeffY[i] + slabSizeY - 1)/slabSizeY;
//            final String archiPath = outputDirectoryPath+"/"+coeffX[i]+"_"+coeffY[i]+"/";
//            for (int y = 0; y<nbreSlabY; y++) {
//                for (int x = 0; x<nbreSlabX; x++) {
//                    new File(archiPath+x+"_"+y+"/").mkdirs();
//                }
//            }
//        }
//
//        for (int floor = 0; floor<levelNbre; floor++) {
//            String outPath = outputDirectoryPath+"/"+coeffX[floor]+"_"+coeffY[floor]+"/";
//
//            final int subGRminx   = globalRegion.x      / coeffX[floor];
//            final int subGRminy   = globalRegion.y      / coeffY[floor];
//            final int subGRwidth  = globalRegion.width  / coeffX[floor];
//            final int subGRheight = globalRegion.height / coeffY[floor];
//
//            final int idSlabMaxX  = (subGRwidth  + slabSizeX - 1) / slabSizeX;
//            final int idSlabMaxY  = (subGRheight + slabSizeY - 1) / slabSizeY;
//
//            int slabMinY = subGRminy;
//            //slab by slab
//            for (int idSY = 0; idSY < idSlabMaxY;idSY++) {
//                int slabMinX = subGRminx;
//                for (int idSX = 0; idSX < idSlabMaxX; idSX++) {
//
//                    final String outSlagPath = outPath+idSX+"_"+idSY+"/";
//                    //current slab coordinates
//                    final int slabMaxX = Math.min(subGRminx + subGRwidth,  slabMinX + slabSizeX);
//                    final int slabMaxY = Math.min(subGRminy + subGRheight, slabMinY + slabSizeY);
//                    final int w        = slabMaxX - slabMinX;
//                    final int h        = slabMaxY - slabMinY;
//
//                    //X direction tile number
//                    final int nbrTX = (w + tileWidth - 1)  / tileWidth;
//                    //Y direction tile number
//                    final int nbrTY = (h + tileHeight - 1) / tileHeight;
//                    int miny = slabMinY;
//
//                    for (int ity = 0; ity<nbrTY; ity++) {
//                        int tempMinx = slabMinX;
//              noSlab :  for (int itx = 0; itx < nbrTX; itx++) {
//                            final int tw = Math.min(tempMinx + tileWidth, slabMaxX) - tempMinx;
//                            final int th = Math.min(miny + tileHeight, slabMaxY)    - miny;
//
//                            areaTemp.setBounds(tempMinx, miny, tw, th);
//                            WritableRenderedImage tuile = getSlab(areaTemp, coeffX[floor], coeffY[floor], originalMosaic);
//                            if (tuile == null) {
//                                //next tile X position
//                                tempMinx += tileWidth;
//                                continue noSlab;
//                            }
//
//                            //generate name
//                            final String namePTile      = outSlagPath+formatter.generateFilename(floor, itx, ity)+"."+outputFormatName;
//                            //writing
//                            final File imgOutPutPath    = new File(namePTile);
//                            final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
//                            imgWriter.write(tuile);
//                            imgWriter.dispose();
//
//                            //next tile X position
//                            tempMinx += tileWidth;
//                        }
//                        //next tile row
//                        miny += tileHeight;
//                    }
//                    //next slab X position
//                    slabMinX += slabSizeX;
//                }
//                //next slab row
//                slabMinY += slabSizeY;
//            }
//        }
//        return new PyramidTileManager(outputDirectory, globalRegion.x, globalRegion.y, globalRegion.width, globalRegion.height,coeffX,coeffY, slabWidth, slabHeight, tileWidth, tileHeight,outputPrefixName, outputFormatName);
//    }

//    /**
//     *
//     * @param interpolationCase
//     * @param lanczosWindow
//     */
//    public TileManager createTileManager(TileManager originalMosaic) throws IOException, NoninvertibleTransformException, TransformException, TransformerConfigurationException, TransformerException, ParserConfigurationException {
//        ArgumentChecks.ensureNonNull("originalMosaic", originalMosaic);
//        if(outputDirectory == null)
//            throw new IllegalStateException("caller must set output directory");
//        if (coeffX == null || coeffY == null)
//            throw new IllegalStateException("caller must set resampling coefficients");
//        if (tileHeight == 0 || tileWidth == 0)
//            throw new IllegalStateException("caller must set tile dimension");
//        if (slabHeight == 0 || slabWidth == 0)
//            throw new IllegalStateException("caller must set slab dimension");
//        if (interpolationCase == null || lanczosWindow == 0)
//            throw new IllegalStateException("caller must set interpolation properties");
//        final int levelNbre = coeffX.length;
//
//        /*
//         * Clean destination directory.
//         */
//        cleanDirectory(outputDirectory);
//
//        final Rectangle globalRegion = originalMosaic.getRegion();
//
//        //write xml
//        writeProperties(globalRegion);
//
//        final int slabSizeX = slabWidth  * tileWidth;
//        final int slabSizeY = slabHeight * tileHeight;
//
//        final String outputDirectoryPath = outputDirectory.getAbsolutePath();
//        //architecture creation
//        for (int i = 0; i<coeffX.length; i++) {
//            int nbreSlabX = (globalRegion.width/coeffX[i] + slabSizeX - 1)/slabSizeX;
//            int nbreSlabY = (globalRegion.height/coeffY[i] + slabSizeY - 1)/slabSizeY;
//            final String archiPath = outputDirectoryPath+"/"+coeffX[i]+"_"+coeffY[i]+"/";
//            for (int y = 0; y<nbreSlabY; y++) {
//                for (int x = 0; x<nbreSlabX; x++) {
//                    new File(archiPath+x+"_"+y+"/").mkdirs();
//                }
//            }
//        }
//
//        final Rectangle slabAreaBase  = new Rectangle();
//        final Rectangle tuileAreaBase = new Rectangle();
//        final Rectangle readArea      = new Rectangle();
//
//        for (int floor = 0; floor<levelNbre; floor++) {
//            String outPath = outputDirectoryPath+"/"+coeffX[floor]+"_"+coeffY[floor]+"/";
//            final MathTransform mt = new AffineTransform2D(1.0 / coeffX[floor], 0, 0, 1.0 / coeffY[floor], 0, 0);
//            final int subGRminx   = globalRegion.x      / coeffX[floor];
//            final int subGRminy   = globalRegion.y      / coeffY[floor];
//            final int subGRwidth  = globalRegion.width  / coeffX[floor];
//            final int subGRheight = globalRegion.height / coeffY[floor];
//
//            final int idSlabMaxX  = (subGRwidth  + slabSizeX - 1) / slabSizeX;
//            final int idSlabMaxY  = (subGRheight + slabSizeY - 1) / slabSizeY;
//
//            int slabMinY = subGRminy;
//            //slab by slab
//            for (int idSY = 0; idSY < idSlabMaxY;idSY++) {
//                int slabMinX = subGRminx;
//                for (int idSX = 0; idSX < idSlabMaxX; idSX++) {
//
//                    final String outSlagPath = outPath+idSX+"_"+idSY+"/";
//                    //current slab coordinates
//                    final int slabMaxX = Math.min(subGRminx + subGRwidth,  slabMinX + slabSizeX);
//                    final int slabMaxY = Math.min(subGRminy + subGRheight, slabMinY + slabSizeY);
//                    final int w        = slabMaxX - slabMinX;
//                    final int h        = slabMaxY - slabMinY;
//
//                    //X direction tile number
//                    final int nbrTX = (w + tileWidth - 1)  / tileWidth;
//                    //Y direction tile number
//                    final int nbrTY = (h + tileHeight - 1) / tileHeight;
//                    int miny = slabMinY;
//                    List<Rectangle> listTileArea = new ArrayList<Rectangle>(nbrTX*nbrTY);
//                    //on rempli la liste
//                    for (int ity = 0; ity<nbrTY; ity++) {
//                        int tempMinx = slabMinX;
//                        for (int itx = 0; itx < nbrTX; itx++) {
//                            final int tw = Math.min(tempMinx + tileWidth, slabMaxX) - tempMinx;
//                            final int th = Math.min(miny + tileHeight, slabMaxY)    - miny;
//
//                            listTileArea.add(new Rectangle(tempMinx, miny, tw, th));
//
//                            //next tile X position
//                            tempMinx += tileWidth;
//                        }
//                        //next tile row
//                        miny += tileHeight;
//                    }
//
//                    //on calcule la taille a la base de la pyramide de la dalle
//                    slabAreaBase.setBounds(slabMinX*coeffX[floor], slabMinY*coeffY[floor], slabSizeX*coeffX[floor], slabSizeY*coeffY[floor]);
//                    //on cherche dans le tilemanager les tiles qui intersect
//                    final Collection<Tile> listTile = originalMosaic.getTiles(slabAreaBase, new Dimension(1, 1), true);
//                    //pour chacune des tiles de base on check si elles sont contenue
//                    //dans la tiles
//                    //pour chaque tiles
//      unnecessary : for (Tile tile : listTile) {
//                        //on creer un image reader a ameliorer avec le = null
//                        //faire une image avec imgreader et param
//                        //ensuite decoupé avec les iterateurs
//
//                        //avec des math.min et math.max
//                        final Rectangle tileRegion   = tile.getRegion();
//                        final Rectangle intersection = tileRegion.intersection(slabAreaBase);
//                        if ((intersection.width  / (tileWidth  * coeffX[floor]) < 2)
//                         && (intersection.height / (tileHeight * coeffY[floor]) < 2))
//                            continue unnecessary;
//
//                        final ImageReader imgReader    = tile.getImageReader();
//                        final ImageReadParam imgRParam = new ImageReadParam();
//                        final int ltaSize_1 = listTileArea.size()-1;
//                        for (int idl = ltaSize_1; idl >= 0; idl--) {
//                            final Rectangle tA = listTileArea.get(idl);
//                            tuileAreaBase.setBounds(tA.x*coeffX[floor], tA.y*coeffY[floor], tA.width*coeffX[floor], tA.height*coeffY[floor]);
//                            //si la tile contien la region en totalité
//                            //on enleve le rectangle on lit on resample on ecrit
//                            if (tileRegion.contains(tuileAreaBase)) {
//                                //j'enleve le rectangle
//                                listTileArea.remove(idl);
//                                //on defini le rectangle de lecture
//                                readArea.setBounds(tuileAreaBase.x-tileRegion.x, tuileAreaBase.y-tileRegion.y, tuileAreaBase.width, tuileAreaBase.height);
//                                imgRParam.setSourceRegion(readArea);
//                                RenderedImage renderImage = imgReader.read(0, imgRParam);//je pense quelle dois commencé en 0,0
//                                RenderedImage destImg;
//                                if (coeffX[floor] == 1 && coeffY[floor] == 1) {
//                                    destImg = renderImage;
//                                } else {
//                                    //propriétés de l'image
//                                    int iw = tuileAreaBase.width  / coeffX[floor];
//                                    int ih = tuileAreaBase.height / coeffY[floor];
//                                    ColorModel cm = renderImage.getColorModel();
//                                    //maintenant il faut la resamplée
//                                    //interpolator
//                                    final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderImage), interpolationCase, lanczosWindow);
//                                    // empty resampled image
//                                    destImg = new TiledImage(0, 0, iw, ih, 0, 0, cm.createCompatibleSampleModel(iw, ih), cm);
//                                    //resampling object
//                                    final Resample resample  = new Resample(mt,(WritableRenderedImage) destImg, interpolation, fillValue);
//                                    //fill empty resampled image
//                                    resample.fillImage();
//                                }
//                                //on ecrit l'image
//                                //generate name
//                                final String namePTile      = outSlagPath+formatter.generateFilename(floor, (tA.x-slabMinX)/tileWidth, (tA.y-slabMinY)/tileHeight)+"."+outputFormatName;
//                                //writing
//                                final File imgOutPutPath    = new File(namePTile);
//                                final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
//                                imgWriter.write(destImg);
//                                imgWriter.dispose();
//                            }
//                        }
//                    }
//
//                    //normalement maintenant il ne me reste plus que les tuiles aux intersections des tile de base
//                    //ou tuile etant dans des espaces vides
//
//       noTileFind : for (Rectangle tuile : listTileArea) {
//
//                        RenderedImage destImg = getSlab(tuile, coeffX[floor], coeffY[floor], originalMosaic);
//                        if (destImg == null) continue noTileFind;
//                        //generate name
//                        final String namePTile      = outSlagPath+formatter.generateFilename(floor, (tuile.x-slabMinX)/tileWidth, (tuile.y-slabMinY)/tileHeight)+"."+outputFormatName;
//                        //writing
//                        final File imgOutPutPath    = new File(namePTile);
//                        final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
//                        imgWriter.write(destImg);
//                        imgWriter.dispose();
//                    }
//                    //next slab X position
//                    slabMinX += slabSizeX;
//                }
//                //next slab row
//                slabMinY += slabSizeY;
//            }
//        }
//        return new PyramidTileManager(outputDirectory, globalRegion.x, globalRegion.y, globalRegion.width, globalRegion.height,coeffX,coeffY, slabWidth, slabHeight, tileWidth, tileHeight,outputPrefixName, outputFormatName);
//    }


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
        final Rectangle areaTemp = new Rectangle();
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

        final Rectangle slabAreaBase  = new Rectangle();
        final Rectangle tuileAreaBase = new Rectangle();
        final Rectangle readArea      = new Rectangle();

        for (int floor = 0; floor<levelNbre; floor++) {
            String outPath = outputDirectoryPath+"/"+coeffX[floor]+"_"+coeffY[floor]+"/";
//            final MathTransform mt = new AffineTransform2D(1.0 / coeffX[floor], 0, 0, 1.0 / coeffY[floor], 0, 0);
            final int subGRminx   = globalRegion.x      / coeffX[floor];
            final int subGRminy   = globalRegion.y      / coeffY[floor];
            final int subGRwidth  = globalRegion.width  / coeffX[floor];
            final int subGRheight = globalRegion.height / coeffY[floor];

            final int idSlabMaxX  = (subGRwidth  + slabSizeX - 1) / slabSizeX;
            final int idSlabMaxY  = (subGRheight + slabSizeY - 1) / slabSizeY;

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
                    //on rempli la liste
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

                    //on calcule la taille a la base de la pyramide de la dalle
                    slabAreaBase.setBounds(slabMinX*coeffX[floor], slabMinY*coeffY[floor], slabSizeX*coeffX[floor], slabSizeY*coeffY[floor]);
                    //on cherche dans le tilemanager les tiles qui intersect
                    final Collection<Tile> listTile = originalMosaic.getTiles(slabAreaBase, new Dimension(1, 1), true);
                    //pour chacune des tiles de base on check si elles sont contenue
                    //dans la tiles
                    //pour chaque tiles
      unnecessary : for (Tile tile : listTile) {
                        //on creer un image reader a ameliorer avec le = null
                        //faire une image avec imgreader et param
                        //ensuite decoupé avec les iterateurs

                        //avec des math.min et math.max
                        final Rectangle tileRegion   = tile.getRegion();
                        final Rectangle intersection = tileRegion.intersection(slabAreaBase);
                        if ((intersection.width  / (tileWidth  * coeffX[floor]) < 2)
                         && (intersection.height / (tileHeight * coeffY[floor]) < 2))
                            continue unnecessary;
                        //lecture totale de l'intersection
                        final ImageReader imgReader    = tile.getImageReader();
                        final ImageReadParam imgRParam = new ImageReadParam();
                        readArea.setBounds(intersection.x-tileRegion.x, intersection.y-tileRegion.y, intersection.width, intersection.height);
                        imgRParam.setSourceRegion(readArea);
                        RenderedImage renderImage = imgReader.read(0, imgRParam);//je pense quelle dois commencé en 0,0
                        imgReader.dispose();
                        ColorModel cm = renderImage.getColorModel();
                        int tgox = renderImage.getTileGridXOffset();
                        int tgoy = renderImage.getTileGridYOffset();

                        final int ltaSize_1 = listTileArea.size()-1;
                        for (int idl = ltaSize_1; idl >= 0; idl--) {
                            final Rectangle tA = listTileArea.get(idl);
                            tuileAreaBase.setBounds(tA.x*coeffX[floor], tA.y*coeffY[floor], tA.width*coeffX[floor], tA.height*coeffY[floor]);
                            //si la tile contien la region en totalité
                            //on enleve le rectangle on lit on resample on ecrit
                            if (intersection.contains(tuileAreaBase)) {
                                //j'enleve le rectangle
                                listTileArea.remove(idl);
                                //on defini le rectangle de lecture
//                              //deja creer l'image resultante

                                Rectangle areatuile = new Rectangle(tuileAreaBase.x-intersection.x, tuileAreaBase.y-intersection.y, tuileAreaBase.width, tuileAreaBase.height);


                                //cette partie sera a supprimer car je suis convaincu que je peu directement resamplé a partir de la renderimage
                                //avec le bon math transform associé

                                WritableRenderedImage destImg;
                                if (coeffX[floor] == 1 && coeffY[floor] == 1) {
                                    destImg = new TiledImage(areatuile.x, areatuile.y, areatuile.width, areatuile.height, tgox, tgoy,  cm.createCompatibleSampleModel(renderImage.getTileWidth(), renderImage.getTileHeight()), cm);
                                    //recopie
                                    PixelIterator pix = PixelIteratorFactory.createRowMajorWriteableIterator(renderImage, destImg, areatuile);

                                    while (pix.next()) pix.setSample(pix.getSample());
                                } else {
//                                    //propriétés de l'image
//                                    int ix = areatuile.x / coeffX[floor];
//                                    int iy = areatuile.y / coeffY[floor];
//                                    int iw = tuileAreaBase.width  / coeffX[floor];
//                                    int ih = tuileAreaBase.height / coeffY[floor];
//                                    //maintenant il faut la resamplée
//                                    //interpolator
//                                    final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(tuile), interpolationCase, lanczosWindow);
//                                    // empty resampled image
//                                    destImg = new TiledImage(ix, iy, iw, ih, ix, iy, cm.createCompatibleSampleModel(iw, ih), cm);
//                                    //resampling object
//                                    final Resample resample  = new Resample(mt,(WritableRenderedImage) destImg, interpolation, fillValue);
//                                    //fill empty resampled image
//                                    resample.fillImage();
                                    final MathTransform mt = new AffineTransform2D(1.0 / coeffX[floor], 0, 0, 1.0 / coeffY[floor], 0,0/*areatuile.x/coeffX[floor], areatuile.y/coeffY[floor]*/);

                                    //propriétés de l'image
                                    int ix = areatuile.x / coeffX[floor];
                                    int iy = areatuile.y / coeffY[floor];
                                    int iw = tuileAreaBase.width  / coeffX[floor];
                                    int ih = tuileAreaBase.height / coeffY[floor];
                                    //maintenant il faut la resamplée
                                    //interpolator
                                    final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderImage), interpolationCase, lanczosWindow);
                                    // empty resampled image
                                    destImg = new TiledImage(ix, iy, iw, ih, ix, iy, cm.createCompatibleSampleModel(iw, ih), cm);
                                    //resampling object
                                    final Resample resample  = new Resample(mt,(WritableRenderedImage) destImg, interpolation, fillValue);
                                    //fill empty resampled image
                                    resample.fillImage();
                                }
                                //on ecrit l'image
                                //generate name
                                final String namePTile      = outSlagPath+formatter.generateFilename(floor, (tA.x-slabMinX)/tileWidth, (tA.y-slabMinY)/tileHeight)+"."+outputFormatName;
                                //writing
                                final File imgOutPutPath    = new File(namePTile);
                                final ImageWriter imgWriter = XImageIO.getWriterByFormatName(outputFormatName, imgOutPutPath, null);
                                imgWriter.write(destImg);
                                imgWriter.dispose();
                            }
                        }
                    }

                    //normalement maintenant il ne me reste plus que les tuiles aux intersections des tile de base
                    //ou tuile etant dans des espaces vides

       noTileFind : for (Rectangle tuile : listTileArea) {

                        RenderedImage destImg = getSlab(tuile, coeffX[floor], coeffY[floor], originalMosaic);
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
    private WritableRenderedImage getSlab (Rectangle slabRegion, int subsamplingX, int subsamplingY, TileManager tileManager) throws IOException, NoninvertibleTransformException, TransformException {

        //get all tiles which we will be able to need.
        final Collection<Tile> listTile = tileManager.getTiles(new Rectangle(slabRegion.x*subsamplingX, slabRegion.y*subsamplingY, slabRegion.width*subsamplingX, slabRegion.height*subsamplingY), new Dimension(1, 1), true);

        ColorModel colorMod        = null;
        int numband = 0;
        WritableRenderedImage slab = null;
        PixelIterator inputPix;
        Rectangle areaTile = new Rectangle();
        PixelIterator destPix = null;
        //resampling affinetransform
        final MathTransform mt = new AffineTransform2D(1.0 / subsamplingX, 0, 0, 1.0 / subsamplingY, 0, 0);
        final ImageReadParam imgParam = new ImageReadParam();

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

            if (slab == null) {
                colorMod = renderedImage.getColorModel();
                numband  = renderedImage.getSampleModel().getNumBands();
                slab     = new TiledImage(slabRegion.x, slabRegion.y, slabRegion.width, slabRegion.height, slabRegion.x, slabRegion.y, colorMod.createCompatibleSampleModel(slabRegion.width, slabRegion.height), colorMod);
                destPix  = PixelIteratorFactory.createRowMajorWriteableIterator(slab, slab, slabRegion);
            }

            if (!renderedImage.getColorModel().equals(colorMod))
                throw new IllegalStateException("tile from basic mosaic haven't got same color model");

            //Slab resampling from original mosaic tiles.
            RenderedImage wri;
            if (subsamplingX == 1 && subsamplingY == 1) {
                wri      = renderedImage;
            } else {
                //interpolator
                final Interpolation interpolation = Interpolation.create(PixelIteratorFactory.createRowMajorIterator(renderedImage), interpolationCase, lanczosWindow);
                // empty resampled image
                wri = new TiledImage(0, 0, imgw, imgh, 0, 0, colorMod.createCompatibleSampleModel(imgw, imgh), colorMod);
                //resampling object
                final Resample resample  = new Resample(mt,(WritableRenderedImage) wri, interpolation, fillValue);
                //fill empty resampled image
                resample.fillImage();
            }
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
