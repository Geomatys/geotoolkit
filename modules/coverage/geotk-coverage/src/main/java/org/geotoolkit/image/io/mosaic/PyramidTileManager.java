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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.imageio.ImageReader;
import javax.media.jai.TiledImage;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 *
 * @author Remi Marechal (Geomatys).
 */
public class PyramidTileManager extends TileManager {

    String parentPath;
    Rectangle globaleRegion;
    int gRx;
    int gRy;
    int tileWidth;
    int tileHeight;
    String format;

    public PyramidTileManager(File parentDirectory, Rectangle globaleRegion, Dimension tileSize, String format) {
        this.parentPath = parentDirectory.getAbsolutePath();
        this.globaleRegion = globaleRegion;
        this.gRx = globaleRegion.x;
        this.gRy = globaleRegion.y;
        this.tileWidth = tileSize.width;
        this.tileHeight = tileSize.height;
        this.format = format;
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
        int tminx  = (region.x - mx) / tileWidth;
        int tminy  = (region.y - my) / tileHeight;
        int tmaxx  = (region.width  + tileWidth  - 1) / tileWidth  + tminx;
        int tmaxy  = (region.height + tileHeight - 1) / tileHeight + tminy;

        //coordonnée de l'image
        int ix = Math.max(mx, region.x);
        int iy = Math.max(my, region.y);
        int iw = Math.min(region.x + region.width,  gRx + globaleRegion.width)  - ix;
        int ih = Math.min(region.y + region.height, gRy + globaleRegion.height) - iy;

        Rectangle imgIntersection = new Rectangle();

        WritableRenderedImage renderImage = new TiledImage(ix, iy, iw, ih, ix, iy, new BandedSampleModel(datatype, iw, ih, numBand), colorModel);
        PixelIterator destPix = PixelIteratorFactory.createRowMajorWriteableIterator(renderImage, renderImage);

        for (;tminy < tmaxy; tminy++) {
            for (int tx = tminx; tx < tmaxx; tx++) {
                //on lit la bonne image
                File tilePathTemp = new File(resultPath+tx+"_"+tminy+"."+format);
                //si la tuile existe
                if (tilePathTemp.exists()) {
                    ImageReader imgreader = XImageIO.getReader(tilePathTemp, Boolean.FALSE, Boolean.TRUE);
                    RenderedImage imgTemp = imgreader.read(0);
                    //on defini l'intersection par rapport a l'image qui commence en (0, 0)
                    int imgminx = gRx + tx * tileWidth;
                    int imgminy = gRy + tminy * tileHeight;
                    int imgmaxx = imgminx + tileWidth;
                    int imgmaxy = imgminy + tileHeight;

                    int interdebx = Math.max(imgminx, ix);
                    int interdeby = Math.max(imgminy, iy);
                    int interendx = Math.min(imgmaxx, ix + iw);
                    int interendy = Math.min(imgmaxy, iy + ih);
                    imgIntersection.setBounds(interdebx - imgminx, interdeby - imgminy, interendx - interdebx, interendy - interdeby);
                    PixelIterator temPix = PixelIteratorFactory.createRowMajorIterator(imgTemp, imgIntersection);

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
