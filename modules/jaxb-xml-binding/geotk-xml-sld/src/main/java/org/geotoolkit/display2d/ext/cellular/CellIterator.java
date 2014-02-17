/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display2d.ext.cellular;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import org.apache.sis.math.Statistics;

/**
 * Iterator over pixels, skipping values if decimation is set.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public class CellIterator {

    /**
     * Image to iterate over.
     */
    private final RenderedImage image;
    private final SampleModel sm;
    private final boolean decimate;
    private final int decimateX;
    private final int decimateY;
    private final Statistics[] cellStats;
    private final double[] pixel;

    /**
     * The upper limit (exclusive) for {@link #index}.
     */
    private final int count;

    /**
     * The index of current position.
     * The <var>i</var>,<var>j</var> index in the underlying grid coverage can be deduced with:
     * <blockquote><pre>
     * i = index % width;
     * j = index / width;
     * </pre></blockquote>
     */
    private int index = -1;
    /**
     * The <var>i</var>,<var>j</var> indices for the current position.
     * Will be computed only when first required.
     */
    private double i, j;

    /**
     * <code>true</code> if {@link #i}, {@link #j}, {@link #cellStats} are valids.
     */
    private boolean valid;

    /**
     * Create iterator over image without decimation.
     *
     * @param image to iterate over
     */
    public CellIterator(RenderedImage image) {
        this(image,1,1);
    }

    /**
     * Create iterator over image with decimation.
     *
     * @param image to iterate over
     * @param decimateX x decimation
     * @param decimateY y decimation
     */
    public CellIterator(RenderedImage image, int decimateX, int decimateY) {
        this.image = image;
        this.sm = image.getSampleModel();
        this.decimateX = decimateX;
        this.decimateY = decimateY;
        this.decimate = (decimateX!=1 || decimateY!=1);
        this.count = (image.getWidth()/decimateX) * (image.getHeight()/decimateY);
        this.cellStats = new Statistics[sm.getNumBands()];
        for(int i=0;i<this.cellStats.length;i++){
            this.cellStats[i] = new Statistics(null);
        }
        this.pixel = new double[this.cellStats.length];
    }


    /**
     * Moves the iterator to the next position.
     */
    public boolean next() {
        valid = false;
        return ++index < count;
    }

    /**
     * Calcule les composantes <var>x</var> et <var>y</var> du vecteur à l'index spécifié.
     */
    private void compute() {
        //reset stats
        for(int b=0;b<this.cellStats.length;b++){
            this.cellStats[b].reset();
        }

        int count = 0;
        int sumI  = 0;
        int sumJ  = 0;
        final int decWidth = image.getWidth()/decimateX;
        final int imin = (index % decWidth)*decimateX + image.getMinX();
        final int jmin = (index / decWidth)*decimateY + image.getMinY();
        for (int i=imin+decimateX; --i>=imin;) {
            for (int j=jmin+decimateY; --j>=jmin;) {
                final Raster tile = image.getTile(XToTileX(image,i), YToTileY(image,j));
                tile.getPixel(i, j, pixel);
                for(int b=0;b<pixel.length;b++){
                    if(!Double.isNaN(pixel[b])){
                        cellStats[b].accept(pixel[b]);
                    }
                }
                sumI += i;
                sumJ += j;
                count++;
            }
        }

        this.i = imin + (double)decimateX/2d;
        this.j = jmin + (double)decimateX/2d;
        valid = true;
    }

    /**
     * Retourne les coordonnées (<var>x</var>,<var>y</var>) d'un point de la grille.
     *
     * Si une décimation a été spécifiée alors la position retournée
     * sera située au milieu des points à moyenner.
     */
    public Point2D position() {
        if (!decimate) {
            final int width = image.getWidth();
            return new Point2D.Double(index % width, index / width);
        } else {
            if (!valid) {
                compute();
            }
            return new Point2D.Double(i, j);
        }
    }

    /**
     * Get the cell statistics.
     */
    public Statistics[] statistics() {
        if (!valid) {
            compute();
        }
        return cellStats;
    }

    /**
     * Returns <code>true</code> if the current mark is visible in the specified clip.
     */
    boolean visible(final Shape clip) {
        if (clip == null) {
            return true;
        }
        final int decWidth = image.getWidth()/decimateX;
        return clip.contains(index%decWidth, index/decWidth);
    }


    public static int XToTileX(final RenderedImage image, int x){
        final int tileWidth = image.getTileWidth();
        x -= image.getTileGridXOffset();
        if (x < 0) {
            x += 1 - tileWidth;
        }
        return x/tileWidth;
    }

    public static int YToTileY(final RenderedImage image, int y){
        final int tileHeight = image.getTileHeight();
        y -= image.getTileGridYOffset();
        if (y < 0) {
            y += 1 - tileHeight;
        }
        return y/tileHeight;
    }

}
