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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import javax.imageio.ImageReader;
import javax.media.jai.TiledImage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.io.DefaultFileFilter;
import org.geotoolkit.util.ArgumentChecks;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidTileManager extends TileManager {

    /**
     * Tile manager properties file name.
     */
    private static final String XML_NAME = "properties.xml";

    private final String parentPath;
    private final int gRx;
    private final int gRy;
    private final int gRw;
    private final int gRh;
    private final Dimension[] subsampling;
    private final int slabWidth;
    private final int slabHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final String format;
    private final String prefix;
    private final FilenameFormatter formatter;
    private Collection<Tile> allTiles = null;

    /**
     *
     *
     * @param parentDirectory path to directory which contain all pyramid architecture.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public PyramidTileManager(File parentDirectory) throws ParserConfigurationException, SAXException, IOException {
        ArgumentChecks.ensureNonNull("parentDirectory", parentDirectory);
        if (!parentDirectory.exists())
            throw new IllegalArgumentException("path of parent direcory don't exist");
        final File[] xmlFile = parentDirectory.listFiles((FileFilter)new DefaultFileFilter(XML_NAME));
        if (xmlFile.length != 1)
            throw new IllegalStateException("you must create tile architecture from pyramid builder");
        this.parentPath = parentDirectory.getAbsolutePath();
        this.formatter = new FilenameFormatter();

        //xml
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder parser         = factory.newDocumentBuilder();
        final Document document              = parser.parse(new File(parentDirectory.getAbsolutePath()+"/"+XML_NAME));
        //document trunk
        Element properties = document.getDocumentElement();
        {
            final Element mosaic = (Element) properties.getElementsByTagName("mosaic").item(0);
            {
                this.gRx = Integer.decode(mosaic.getElementsByTagName("minX").item(0).getFirstChild().getNodeValue());
                this.gRy = Integer.decode(mosaic.getElementsByTagName("minY").item(0).getFirstChild().getNodeValue());
                this.gRw = Integer.decode(mosaic.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
                this.gRh = Integer.decode(mosaic.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
            }
            final Element subsampl = (Element) properties.getElementsByTagName("subsampling").item(0);
            {
                int level = Integer.decode(subsampl.getElementsByTagName("level").item(0).getFirstChild().getNodeValue());
                this.subsampling = new Dimension[level];
                final NodeList subx = subsampl.getElementsByTagName("subx");
                final NodeList suby = subsampl.getElementsByTagName("suby");
                for (int floor = 0; floor < level; floor++) {
                    final int x = Integer.decode(subx.item(floor).getFirstChild().getNodeValue());
                    final int y = Integer.decode(suby.item(floor).getFirstChild().getNodeValue());
                    this.subsampling[floor] = new Dimension(x, y);
                }
            }
            final Element slab  = (Element) properties.getElementsByTagName("slab").item(0);
            {
                this.slabWidth  = Integer.decode(slab.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
                this.slabHeight = Integer.decode(slab.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
            }
            final Element tile  = (Element) properties.getElementsByTagName("tile").item(0);
            {
                this.tileWidth  = Integer.decode(tile.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
                this.tileHeight = Integer.decode(tile.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
            }
            final Element compression = (Element) properties.getElementsByTagName("compression").item(0);
            {
                this.prefix = compression.getElementsByTagName("prefix").item(0).getFirstChild().getNodeValue();
                this.format = compression.getElementsByTagName("format").item(0).getFirstChild().getNodeValue();
            }
        }
        this.formatter.ensurePrefixSet(prefix);
    }

    /**
     *
     * @param parentDirectory
     * @param gRx
     * @param gRy
     * @param gRw
     * @param gRh
     * @param slabWidth
     * @param slabHeight
     * @param tileWidth
     * @param tileHeight
     * @param format
     */
    PyramidTileManager(File parentDirectory, int gRx, int gRy, int gRw, int gRh, int[] coeffX, int[] coeffY, int slabWidth, int slabHeight, int tileWidth, int tileHeight, String prefix, String format) {
        this.parentPath = parentDirectory.getAbsolutePath();
        this.gRx        = gRx;
        this.gRy        = gRy;
        this.gRw        = gRw;
        this.gRh        = gRh;
        this.subsampling = new Dimension[coeffX.length];
        for (int floor = 0; floor < coeffX.length; floor++) this.subsampling[floor] = new Dimension(coeffX[floor], coeffY[floor]);
        this.slabWidth  = slabWidth;
        this.slabHeight = slabHeight;
        this.tileWidth  = tileWidth;
        this.tileHeight = tileHeight;
        this.prefix     = prefix;
        this.format     = format;
        this.formatter  = new FilenameFormatter();
        this.formatter.ensurePrefixSet(prefix);
    }

    /**
     *
     * @param region
     * @param subsampling
     * @param sampleModel
     * @param colorModel
     * @return
     * @throws IOException
     */
    public RenderedImage getImage(Rectangle region, Dimension subsampling) throws IOException {
        ArgumentChecks.ensureNonNull("region", region);
        ArgumentChecks.ensureNonNull("subsampling", subsampling);
        String resultPath = parentPath+"/"+subsampling.width+"_"+subsampling.height+"/";
        if (!new File(resultPath).exists())
            throw new IllegalStateException("subsampling argument is not conform");

        int floor = 0;
        for (;floor < this.subsampling.length; floor++) if (this.subsampling[floor].equals(subsampling)) break;



        final int mx = gRx / subsampling.width;
        final int my = gRy / subsampling.height;
        final int slabSizeX = slabWidth  * tileWidth;
        final int slabSizeY = slabHeight * tileHeight;

        //image coordinate
        final int ix = Math.max(mx, region.x);
        final int iy = Math.max(my, region.y);
        final int iw = Math.min(region.x + region.width,  (gRx + gRw) / subsampling.width)  - ix;
        final int ih = Math.min(region.y + region.height, (gRy + gRh) / subsampling.height) - iy;

        if (iw <= 0 || ih <= 0)
            throw new IllegalArgumentException("region don't intersect pyramid area");

        final Rectangle imgIntersection = new Rectangle();

        WritableRenderedImage renderImage = null;
        PixelIterator destPix = null;
        int datatype;
        int numBand = 0;

//        final WritableRenderedImage renderImage = new TiledImage(ix, iy, iw, ih, ix, iy, new BandedSampleModel(datatype, iw, ih, numBand), colorModel);
//        final PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(renderImage, renderImage);

        final int idSlabMinX = (ix - mx) / slabSizeX;
        int idSlabMinY       = (iy - my) / slabSizeY;
        final int idSlabMaxX = (ix + iw - mx + slabSizeX - 1) / slabSizeX;
        final int idSlabMaxY = (iy + ih - my + slabSizeY - 1) / slabSizeY;

        final int smxBase = mx + idSlabMinX * slabSizeX;
        int smy = my + idSlabMinY * slabSizeY;

        for (; idSlabMinY<idSlabMaxY; idSlabMinY++) {
            int smx = smxBase;
            for (int idSMinX = idSlabMinX; idSMinX < idSlabMaxX; idSMinX++) {

                final String slabPath = resultPath+idSMinX+"_"+idSlabMinY+"/";

                final int tminx = (Math.max(ix, smx) - smx) / tileWidth;
                int tminy       = (Math.max(iy, smy) - smy) / tileHeight;
                final int tmaxx = (Math.min(ix + iw, smx + slabSizeX) - smx + tileWidth  - 1) / tileWidth;
                final int tmaxy = (Math.min(iy + ih, smy + slabSizeY) - smy + tileHeight - 1) / tileHeight;

                int imgminy = smy + tminy * tileWidth;
                final int imgminxBase = smx + tminx * tileWidth;
                //parcour des tuiles
                for (;tminy < tmaxy; tminy++) {
                    int imgminx = imgminxBase;
                    for (int tx = tminx; tx < tmaxx; tx++) {
//                        final File tilePathTemp = new File(slabPath+tx+"_"+tminy+"."+format);
                        final File tilePathTemp = new File(slabPath+formatter.generateFilename(floor, tx, tminy) +"."+format);
                        if (tilePathTemp.exists()) {
                            final ImageReader imgreader = XImageIO.getReader(tilePathTemp, Boolean.FALSE, Boolean.TRUE);
                            final RenderedImage imgTemp = imgreader.read(0);
                            imgreader.dispose();
                            if (renderImage == null) {
                                final SampleModel sm = imgTemp.getSampleModel();
                                datatype       = sm.getDataType();
                                numBand        = sm.getNumBands();
                                renderImage    = new TiledImage(ix, iy, iw, ih, ix, iy, new BandedSampleModel(datatype, iw, ih, numBand), imgTemp.getColorModel());
                                destPix        = PixelIteratorFactory.createRowMajorWriteableIterator(renderImage, renderImage);
                            }
                            //intersection
                            final int interdebx = Math.max(imgminx, ix);
                            int interdeby       = Math.max(imgminy, iy);
                            final int interendx = Math.min(imgminx + imgTemp.getWidth(),  ix + iw);
                            final int interendy = Math.min(imgminy + imgTemp.getHeight(), iy + ih);
                            imgIntersection.setBounds(interdebx - imgminx, interdeby - imgminy, interendx - interdebx, interendy - interdeby);
                            final PixelIterator temPix = PixelIteratorFactory.createRowMajorIterator(imgTemp, imgIntersection);

                            for (; interdeby < interendy; interdeby++) {
                                destPix.moveTo(interdebx, interdeby, 0);
                                for (int x = interdebx; x < interendx; x++) {
                                    for (int b = 0; b < numBand; b++) {
                                        temPix.next();
                                        destPix.setSampleDouble(temPix.getSampleDouble());
                                        destPix.next();
                                    }
                                }
                            }
                        }
                        //next tile in X direction
                        imgminx += tileWidth;
                    }
                    //next tile in Y direction
                    imgminy += tileHeight;
                }
                //next slab in X direction
                smx += slabSizeX;
            }
            //next slab in Y direction
            smy += slabSizeY;
        }
        return renderImage;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Tile> getTiles() throws IOException {
        if (allTiles != null) return allTiles;
        allTiles = new ArrayList<Tile>();

        throw new UnsupportedOperationException("Not supported yet.");
        //je pense faire methode recursive
    }


    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Tile> getTiles(Rectangle region, Dimension subsampling, boolean subsamplingChangeAllowed) throws IOException {
        ArgumentChecks.ensureNonNull("region", region);
        ArgumentChecks.ensureNonNull("subsampling", subsampling);
        String resultPath = parentPath+"/"+subsampling.width+"_"+subsampling.height+"/";
        if (!new File(resultPath).exists())
            throw new IllegalStateException("subsampling argument is not conform");
        Collection<Tile> tileList = new ArrayList<Tile>();
        int floor = 0;
        for (;floor < this.subsampling.length; floor++) if (this.subsampling[floor].equals(subsampling)) break;
        final int mx        = gRx / subsampling.width;
        final int my        = gRy / subsampling.height;
        final int slabSizeX = slabWidth  * tileWidth;
        final int slabSizeY = slabHeight * tileHeight;

        //image coordinate
        final int ix = Math.max(mx, region.x);
        final int iy = Math.max(my, region.y);
        final int iw = Math.min(region.x + region.width,  (gRx + gRw) / subsampling.width)  - ix;
        final int ih = Math.min(region.y + region.height, (gRy + gRh) / subsampling.height) - iy;

        if (iw <= 0 || ih <= 0)
            throw new IllegalArgumentException("region don't intersect pyramid area");


        final int idSlabMinX = (ix - mx) / slabSizeX;
        int idSlabMinY       = (iy - my) / slabSizeY;
        final int idSlabMaxX = (ix + iw - mx + slabSizeX - 1) / slabSizeX;
        final int idSlabMaxY = (iy + ih - my + slabSizeY - 1) / slabSizeY;

        final int smxBase = mx + idSlabMinX * slabSizeX;
        int smy = my + idSlabMinY * slabSizeY;

        for (; idSlabMinY<idSlabMaxY; idSlabMinY++) {
            int smx = smxBase;
            for (int idSMinX = idSlabMinX; idSMinX < idSlabMaxX; idSMinX++) {

                final String slabPath = resultPath+idSMinX+"_"+idSlabMinY+"/";

                final int tminx = (Math.max(ix, smx) - smx) / tileWidth;
                int tminy       = (Math.max(iy, smy) - smy) / tileHeight;
                final int tmaxx = (Math.min(ix + iw, smx + slabSizeX) - smx + tileWidth  - 1) / tileWidth;
                final int tmaxy = (Math.min(iy + ih, smy + slabSizeY) - smy + tileHeight - 1) / tileHeight;

                //parcour des tuiles
                for (;tminy < tmaxy; tminy++) {
                    for (int tx = tminx; tx < tmaxx; tx++) {
//                        final File tilePathTemp = new File(slabPath+tx+"_"+tminy+"."+format);
                        final File tilePathTemp = new File(slabPath+formatter.generateFilename(floor, tx, tminy) +"."+format);
                        if (tilePathTemp.exists()) {
                            final int mintx = mx + idSMinX    * slabSizeX + tx * tileWidth;
                            final int minty = my + idSlabMinY * slabSizeY + tminy * tileHeight;
                            final int wt    = Math.min(mintx + tileWidth,  mx + gRw / subsampling.width)  - mintx;
                            final int ht    = Math.min(minty + tileHeight, my + gRh / subsampling.height) - minty;
                            tileList.add(new Tile(null, tilePathTemp, 0, new Rectangle(mintx, minty, wt, ht), subsampling));
                        }
                    }
                }
                //next slab in X direction
                smx += slabSizeX;
            }
            //next slab in Y direction
            smy += slabSizeY;
        }
        return tileList;
    }
}
