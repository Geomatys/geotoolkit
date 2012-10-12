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
import java.util.Collection;
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
//    Rectangle globaleRegion;
    private final int gRx;
    private final int gRy;
    private final int gRw;
    private final int gRh;
    private final int slabWidth;
    private final int slabHeight;
    private final int tileWidth;
    private final int tileHeight;
    private final String format;
//    private final String prefix;


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

        //xml
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder parser         = factory.newDocumentBuilder();
        final Document document              = parser.parse(new File(parentDirectory.getAbsolutePath()+"/"+XML_NAME));
        //document trunk
        Element properties = document.getDocumentElement();
        {
            final Element image = (Element) properties.getElementsByTagName("image").item(0);
            {
                this.gRx = Integer.decode(image.getElementsByTagName("minX").item(0).getFirstChild().getNodeValue());
                this.gRy = Integer.decode(image.getElementsByTagName("minY").item(0).getFirstChild().getNodeValue());
                this.gRw = Integer.decode(image.getElementsByTagName("width").item(0).getFirstChild().getNodeValue());
                this.gRh = Integer.decode(image.getElementsByTagName("height").item(0).getFirstChild().getNodeValue());
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
//                this.prefix = compression.getElementsByTagName("prefix").item(0).getFirstChild().getNodeValue();
                this.format = compression.getElementsByTagName("format").item(0).getFirstChild().getNodeValue();
            }
        }
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
    PyramidTileManager(File parentDirectory, int gRx, int gRy, int gRw, int gRh, int slabWidth, int slabHeight, int tileWidth, int tileHeight, /*String prefix,*/ String format) {
        this.parentPath = parentDirectory.getAbsolutePath();
        this.gRx        = gRx;
        this.gRy        = gRy;
        this.gRw        = gRw;
        this.gRh        = gRh;
        this.slabWidth  = slabWidth;
        this.slabHeight = slabHeight;
        this.tileWidth  = tileWidth;
        this.tileHeight = tileHeight;
//        this.prefix     = prefix;
        this.format     = format;
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
    public RenderedImage getImage(Rectangle region, Dimension subsampling, SampleModel sampleModel, ColorModel colorModel) throws IOException {
        ArgumentChecks.ensureNonNull("region", region);
        ArgumentChecks.ensureNonNull("subsampling", subsampling);
        ArgumentChecks.ensureNonNull("sampleModel", sampleModel);
        ArgumentChecks.ensureNonNull("colorModel", colorModel);
        String resultPath = parentPath+"/"+subsampling.width+"_"+subsampling.height+"/";
        if (!new File(resultPath).exists())
            throw new IllegalStateException("subsampling argument is not conform");

        int datatype = sampleModel.getDataType();
        int numBand = sampleModel.getNumBands();

        int mx = gRx / subsampling.width;
        int my = gRy / subsampling.height;
//        int tminx  = (region.x - mx) / tileWidth;
//        int tminy  = (region.y - my) / tileHeight;
//        int tmaxx  = (region.width  + tileWidth  - 1) / tileWidth  + tminx;
//        int tmaxy  = (region.height + tileHeight - 1) / tileHeight + tminy;

        //coordonnée de l'image
        int ix = Math.max(mx, region.x);
        int iy = Math.max(my, region.y);
        int iw = Math.min(region.x + region.width,  gRx + gRw) - ix;
        int ih = Math.min(region.y + region.height, gRy + gRh) - iy;

        if (iw <= 0 || ih <= 0)
            throw new IllegalArgumentException("region don't intersect pyramid area");

        Rectangle imgIntersection = new Rectangle();

        WritableRenderedImage renderImage = new TiledImage(ix, iy, iw, ih, ix, iy, new BandedSampleModel(datatype, iw, ih, numBand), colorModel);
        PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(renderImage, renderImage);

        int idSlabMinX = (ix - mx) / (slabWidth * tileWidth);
        int idSlabMinY = (iy - my) / (slabHeight * tileHeight);
        int idSlabMaxX = (ix + iw - mx + slabWidth * tileWidth - 1) / (slabWidth * tileWidth);
        int idSlabMaxY = (iy + ih - my + slabHeight * tileHeight - 1) / (slabHeight * tileHeight);
        for (; idSlabMinY<idSlabMaxY; idSlabMinY++) {
            for (int idSMinX = idSlabMinX; idSMinX < idSlabMaxX; idSMinX++) {
                String slabPath = resultPath+idSMinX+"_"+idSlabMinY+"/";
                //redefinir les bornes des tuiles au sein de chaque dalle
                int tminx = (Math.max(ix, gRx+idSMinX*slabWidth*tileWidth) - (gRx+idSMinX*slabWidth*tileWidth))/tileWidth;
                int tminy = (Math.max(iy, gRy+idSlabMinY*slabHeight*tileHeight) - (gRy+idSlabMinY*slabHeight*tileHeight))/tileHeight;
                int tmaxx = (Math.min(ix + iw, gRx+(idSMinX+1)*slabWidth*tileWidth) - (gRx+idSMinX*slabWidth*tileWidth) + tileWidth - 1)/tileWidth;
                int tmaxy = (Math.min(iy + ih, gRy+(idSlabMinY+1)*slabHeight*tileHeight) - (gRy+idSlabMinY*slabHeight*tileHeight) + tileHeight - 1)/tileHeight;

                //parcour des tuiles
                for (;tminy < tmaxy; tminy++) {
                    for (int tx = tminx; tx < tmaxx; tx++) {
                        //on lit la bonne image
                        File tilePathTemp = new File(slabPath+tx+"_"+tminy+"."+format);
                        //si la tuile existe
                        if (tilePathTemp.exists()) {
                            ImageReader imgreader = XImageIO.getReader(tilePathTemp, Boolean.FALSE, Boolean.TRUE);
                            RenderedImage imgTemp = imgreader.read(0);
                            //on defini l'intersection par rapport a l'image qui commence en (0, 0)
                            int imgminx = gRx + idSMinX*slabWidth*tileWidth + tx * tileWidth;
                            int imgminy = gRy + idSlabMinY*slabHeight*tileHeight + tminy * tileHeight;
                            //largeur hauteur reel a voir
                            int imgmaxx = imgminx + imgTemp.getWidth();
                            int imgmaxy = imgminy + imgTemp.getHeight();

                            int interdebx = Math.max(imgminx, ix);
                            int interdeby = Math.max(imgminy, iy);
                            int interendx = Math.min(imgmaxx, ix + iw);
                            int interendy = Math.min(imgmaxy, iy + ih);
                            imgIntersection.setBounds(interdebx - imgminx, interdeby - imgminy, interendx - interdebx, interendy - interdeby);
                            PixelIterator temPix = PixelIteratorFactory.createRowMajorIterator(imgTemp, imgIntersection);

                            //caler le move to au bon endroit kan on pass d'une tuile a l'autre on sait plus
                            for (int y = interdeby; y < interendy; y++) {
                                destPix.moveTo(interdebx, y, 0);
                                for (int x = interdebx; x < interendx; x++) {
                                    for (int b = 0; b < numBand; b++) {
                                        temPix.next();
                                        destPix.setSampleDouble(temPix.getSampleDouble());
                                        destPix.next();
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return renderImage;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Collection<Tile> getTiles() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
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
        int mx = gRx / subsampling.width;
        int my = gRy / subsampling.height;
        //intersection des 2 rectangles region et region globale a revoir les maxs

        int tminx  = (region.x - mx) / tileWidth;
        int tminy  = (region.y - my) / tileHeight;
        int tmaxx  = (region.width  + tileWidth  - 1) / tileWidth  + tminx;
        int tmaxy  = (region.height + tileHeight - 1) / tileHeight + tminy;
        Collection<Tile> tileList = new ArrayList<Tile>();
        for (;tminy < tmaxy; tminy++) {
            for (int tx = tminx; tx < tmaxx; tx++) {
                File tileFile = new File(resultPath+tx+"_"+tminy);
                if (tileFile.exists())
                    tileList.add(new Tile(null, tileFile, 0, new Rectangle(gRx+tx*tileWidth, gRy+tminy*tileHeight, tileWidth, tileHeight), subsampling));
            }
        }
        return tileList;
    }
}
