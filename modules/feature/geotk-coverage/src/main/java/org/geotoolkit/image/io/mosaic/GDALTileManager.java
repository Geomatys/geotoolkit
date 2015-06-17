/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.collection.FrequencySortedSet;

import static org.apache.sis.util.collection.Containers.hashMapCapacity;


/**
 * A mosaic organized in the GDAL way, where each overview is contained in the same file.
 * The scale factor between the base level and overviews may not be an integer. Fractional
 * scale values are usually not legal, but happen in practice with GDAL mosaics.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
final class GDALTileManager extends TileManager implements Comparator<Rectangle> {
    /**
     * For cross-version compatibility during serialization.
     */
    private static final long serialVersionUID = 7452795743008991530L;

    /**
     * A threshold value for considering a mosaic layout as a GDAL layout.
     * This is the minimal value required for the following quantity:
     *
     * <var>image size</var> / <var>maximal subsampling</var>.
     */
    private static final int THRESHOLD = 8;

    /**
     * Sorts the tiles by decreasing subsampling value.
     */
    private static final Comparator<Tile> BY_SUBSAMPLING = new Comparator<Tile>() {
        @Override public int compare(final Tile tile1, final Tile tile2) {
            final Dimension s1 = tile1.getSubsampling();
            final Dimension s2 = tile2.getSubsampling();
            return Long.signum(s2.width * (long) s2.height - s1.width * (long) s1.height);
        }
    };

    /**
     * The tiles in each geographic area. For any value of <var>i</var>, the array {@code tiles[i]}
     * contains tiles in the same geographic area sorted by decreasing values of subsampling.
     */
    private final Tile[][] tilesByRegion;

    /**
     * The regions, in "absolute" coordinates, of each array of tiles. For each index <var>i</var>,
     * {@code regions[i]} is the union of the absolute regions of every tiles in {@code tiles[i]}.
     */
    private final Rectangle[] tileRegions;

    /**
     * The region enclosing all tiles. This is the union of all rectangles in the
     * {@link #tileRegions} array.
     */
    private final Rectangle region;

    /**
     * {@code false} if the regions are sorted by <var>x</var> values,
     * or {@code true} if they are sorted by <var>y</var> values.
     */
    private final boolean sortedByY;

    /**
     * The tile dimensions. Will be computed only when first needed.
     */
    private transient Dimension tileSize;

    /**
     * Creates a new tile manager for the given tiles.
     *
     * @param tiles The tiles, including overviews.
     * @throws IOException if an I/O operation was required and failed.
     * @throws IllegalArgumentException if this class can not handle the given tiles.
     */
    protected GDALTileManager(final Tile[] tiles) throws IOException, IllegalArgumentException {
        /*
         * Find the highest subsampling values, and multiply them by 2. The intend is to
         * compare the tile regions with a tolerance of hx pixels in width, or hy pixels
         * in height, either as smaller or larger regions.
         */
        int hx=1, hy=1;
        for (final Tile tile : tiles) {
            final Dimension s = tile.getSubsampling();
            if (s.width  > hx) hx = s.width;
            if (s.height > hy) hy = s.height;
        }
        hx <<= 1;
        hy <<= 1;
        /*
         * Create a list of tiles by region whith the region coordinates in units of highest
         * subsampling. Note that we really want to perform the computation on (width,height)
         * rather than (xmax,ymax) because the later cause variations of 1 pixel in rectangle
         * sizes compared to the expected values.
         */
        long sumWidth = 0, sumHeight = 0;
        final Map<Rectangle,List<Tile>> byRegions = new HashMap<>();
        for (final Tile tile : tiles) {
            final Rectangle tileRegion = tile.getAbsoluteRegion();
            sumWidth  += tileRegion.width;
            sumHeight += tileRegion.height;
            tileRegion.x      = divide(tileRegion.x,      hx, false);
            tileRegion.y      = divide(tileRegion.y,      hy, false);
            tileRegion.width  = divide(tileRegion.width,  hx, true); // See above comment.
            tileRegion.height = divide(tileRegion.height, hy, true);
            if (tileRegion.width < THRESHOLD || tileRegion.height < THRESHOLD) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.UnexpectedImageSize));
            }
            List<Tile> list = byRegions.get(tileRegion);
            if (list == null) {
                list = new ArrayList<>();
                byRegions.put(tileRegion, list);
            }
            list.add(tile);
        }
        /*
         * Get the array of tiles for each region and sort them by decreasing subsampling.
         */
        int i = 0;
        final int numRegions = byRegions.size();
        region        = new Rectangle(-1, -1);
        tileRegions   = new Rectangle[numRegions];
        tilesByRegion = new Tile[numRegions][];
        for (final List<Tile> list : byRegions.values()) {
            final int n = list.size();
            if (n < 2) {
                /*
                 * We require every region to contain at least one overview,
                 * otherwise the mosaic geometry may not be a GDAL one.
                 */
                throw new IllegalArgumentException(Errors.format(Errors.Keys.IncompatibleGridGeometry));
            }
            final Tile[] overviews = list.toArray(new Tile[n]);
            Arrays.sort(overviews, BY_SUBSAMPLING);
            final Rectangle tileRegion = new Rectangle(-1, -1);
            for (final Tile overview : overviews) {
                tileRegion.add(overview.getAbsoluteRegion());
            }
            tilesByRegion[i] = overviews;
            tileRegions[i++] = tileRegion;
            region.add(tileRegion);
        }
        /*
         * Computes whatever sorting by x or by y ordinates is better, then sort the regions.
         */
        sortedByY = (tiles.length * (long)region.height / sumHeight >=
                     tiles.length * (long)region.width  / sumWidth);
        final Map<Rectangle,Tile[]> byAbsoluteRegion = new IdentityHashMap<>(hashMapCapacity(numRegions));
        for (i=0; i<numRegions; i++) {
            byAbsoluteRegion.put(tileRegions[i], tilesByRegion[i]);
        }
        Arrays.sort(tileRegions, this);
        for (i=0; i<numRegions; i++) {
            if ((tilesByRegion[i] = byAbsoluteRegion.get(tileRegions[i])) == null) {
                throw new AssertionError();
            }
            assert tileRegions[i].contains(tilesByRegion[i][0].getAbsoluteRegion());
        }
    }

    /**
     * Computes n/d with rounding toward positive or negative infinity.
     *
     * @param ceil {@code false} for rounding toward negative infinity, or
     *             {@code true} for rounding toward positive infinity.
     */
    private static int divide(int n, final int d, final boolean ceil) {
        if (ceil) if (n >= 0) n += (d-1);
        else      if (n <  0) n -= (d-1);
        return n / d;
    }

    /**
     * Compares two rectangle for order. The rectangles are ordered by either their <var>x</var>
     * or <var>y</var> ordinates. The ordinate used is determined by the constructor.
     */
    @Override
    public int compare(final Rectangle r1, final Rectangle r2) {
        return Long.signum(compare(r1, r2.x, r2.y));
    }

    /**
     * Compares one rectangle with the (x,y) location of an other rectangle for order.
     */
    private long compare(final Rectangle r1, final int x2, final int y2) {
        int p1, p2;
        if (sortedByY) {
            p1 = r1.y;
            p2 = y2;
        } else {
            p1 = r1.x;
            p2 = x2;
        }
        if (p1 == p2) {
            if (sortedByY) {
                p1 = r1.x;
                p2 = x2;
            } else {
                p1 = r1.y;
                p2 = y2;
            }
        }
        return (long) p1 - (long) p2;
    }

    /**
     * Returns the region enclosing all tiles. This method returns a direct reference
     * to the internal object; <strong>do not modify</strong>.
     */
    @Override
    final Rectangle getRegion() {
        return region;
    }

    /**
     * Returns the tiles dimension, to be returned by {@link MosaicImageReader#getTileWidth(int)}
     * and similar methods. The current implementation returns the most frequent size of base tiles.
     * <p>
     * This method returns a direct reference to the internal object; <strong>do not modify</strong>.
     */
    @Override
    final synchronized Dimension getTileSize() {
        if (tileSize == null) {
            final FrequencySortedSet<Dimension> sizes = new FrequencySortedSet<>(true);
            for (final Rectangle region : tileRegions) {
                sizes.add(region.getSize());
            }
            tileSize = sizes.first();
        }
        return tileSize;
    }

    /**
     * Returns {@code true} if there is more than one tile.
     */
    @Override
    final boolean isImageTiled() {
        return tilesByRegion.length >= 2;
    }

    /**
     * Copies every tiles from the given source array to the given flat target array.
     *
     * @param  source The source array.
     * @param  target The target array, or {@code null}.
     * @return The total number of tiles in the source array.
     */
    private static int getTiles(final Tile[][] source, final Tile[] target) {
        int n = 0;
        for (final Tile[] tiles : source) {
            if (target != null) {
                System.arraycopy(tiles, 0, target, n, tiles.length);
            }
            n += tiles.length;
        }
        return n;
    }

    /**
     * Returns every tiles in a flat list, in no particular order.
     */
    @Override
    public Collection<Tile> getTiles() {
        final Tile[] all = new Tile[getTiles(tilesByRegion, null)];
        getTiles(tilesByRegion, all);
        return Arrays.asList(all);
    }

    /**
     * Returns every tiles that intersect the given region.
     *
     * @param  region      The region of interest (shall not be {@code null}).
     * @param  subsampling On input, the minimal subsampling. On output, the effective subsampling.
     * @param  subsamplingChangeAllowed {@code true} if this method is allowed to modify subsampling.
     * @return The tiles that intercept the given region. May be empty but never {@code null}.
     */
    @Override
    public Collection<Tile> getTiles(final Rectangle region, final Dimension subsampling,
            final boolean subsamplingChangeAllowed) throws IOException
    {
        /*
         * Get the tile arrays in the regions intersecting the requested region.
         * This loop takes advantage of the regions ordering for stopping as soon as possible.
         */
        final int xmax = region.x + region.width;
        final int ymax = region.y + region.height;
        int intersectCount = 0;
        Tile[][] intersect = new Tile[Math.min(tilesByRegion.length, 4)][];
        for (int i=0; i<tileRegions.length; i++) {
            final Rectangle tr = tileRegions[i];
            if (compare(tr, xmax, ymax) > 0) {
                break; // There is no way the following tiles can intersect.
            }
            if (region.intersects(tr)) {
                if (intersectCount == intersect.length) {
                    intersect = Arrays.copyOf(intersect, intersectCount << 1);
                }
                intersect[intersectCount++] = tilesByRegion[i];
            }
        }
        final int[] startAt = new int[intersectCount]; // Initialized to 0.
        /*
         * For each array of tiles, search for the first tile having enough resolution.
         * The resolution effectively used will be stored in the 'newResolution' object.
         */
        Dimension newSubsampling = subsampling;
        final List<Tile> result = new ArrayList<>();
        for (int i=0; i<intersectCount; i++) {
            final Tile[] tiles = intersect[i];
            for (int j=startAt[i]; j<tiles.length; j++) {
                final Tile tile = tiles[j];
                final Dimension floor = tile.getSubsamplingFloor(newSubsampling);
                if (floor == null) {
                    /*
                     * The tile at index j does not have enough resolution.
                     * Search for an other tile at finer resolution.
                     */
                    continue;
                }
                if (floor != newSubsampling) {
                    /*
                     * The tile does not have the requested resolution. If we are not allowed
                     * to change that resolution, then we have to search for an other tile.
                     */
                    if (!subsamplingChangeAllowed) {
                        continue;
                    }
                    /*
                     * If we are allowed to change the resolution, change it. But if the new
                     * resolution is not the same than the resolution computed for the previous
                     * tiles, we need to recompute everything we the new (finer) resolution.
                     */
                    final boolean restart = (newSubsampling != subsampling);
                    newSubsampling = floor;
                    if (restart) {
                        result.clear();
                        startAt[i] = j;
                        i = -1;
                        break; // Restart the outer loop.
                    }
                }
                /*
                 * Add the tile that we just found and examine the next array. We test again for
                 * intersection because some overviews may cover a smaller region than the one
                 * stored in the tileRegions array.
                 */
                if (tile.getAbsoluteRegion().intersects(region)) {
                    result.add(tile);
                }
                startAt[i] = j+1;
                break;
            }
        }
        if (newSubsampling != subsampling) {
            subsampling.setSize(newSubsampling);
        }
        removeOverlaps(result, region);
        return result;
    }

    /**
     * Removes the overlaps in the given list of tiles, if any. This is a convenience method
     * to be invoked by {@link #getTiles(Rectangle, Dimension, boolean)} implementations.
     * <p>
     * This method should be invoked only for very small list, since its execution time
     * is quadratic to the list size.
     *
     * @param  tiles  The list in which to remove overlaps.
     * @param  region The requested region.
     * @throws IOException If it was necessary to fetch an image dimension from its
     *         {@linkplain Tile#getImageReader reader} and this operation failed.
     */
    private static void removeOverlaps(final List<Tile> tiles, final Rectangle region) throws IOException {
        int count = tiles.size();
        final Rectangle[] intersect = new Rectangle[count];
        for (int i=0; i<count; i++) {
            intersect[i] = region.intersection(tiles.get(i).getAbsoluteRegion());
        }
        for (int i=0; i<count; i++) {
            final Rectangle rc = intersect[i];
            for (int j=count; --j>=0;) {
                if (i != j && rc.contains(intersect[j])) {
                    System.arraycopy(intersect, j+1, intersect, j, --count - j);
                    tiles.remove(j);
                    if (j < i) i--;
                }
            }
        }
    }

    /**
     * Returns {@code true} if at least one tile having the given subsampling or a finer
     * one intersects the given region.
     *
     * @param  region The region of interest (shall not be {@code null}).
     * @param  subsampling The maximal subsampling to look for.
     * @return {@code true} if at least one tile having the given subsampling or a finer one
     *          intersects the given region.
     */
    @Override
    public boolean intersects(final Rectangle region, final Dimension subsampling) {
        final int xmax = region.x + region.width;
        final int ymax = region.y + region.height;
        for (int i=0; i<tileRegions.length; i++) {
            final Rectangle tr = tileRegions[i];
            if (compare(tr, xmax, ymax) > 0) {
                break; // There is no way the following tiles can intersect.
            }
            if (region.intersects(tr)) {
                final Tile[] array = tilesByRegion[i];
                for (int j=array.length; --j>=0;) {
                    final Dimension s = array[j].getSubsampling();
                    if (s.width <= subsampling.width && s.height <= subsampling.height) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
