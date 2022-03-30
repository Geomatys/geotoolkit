/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.awt.Dimension;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.tiling.Tile;
import org.apache.sis.storage.tiling.TileStatus;
import org.opengis.geometry.DirectPosition;

/**
 * A TileMatrix is collection of tiles with the same size and properties placed
 * on a regular grid with no overlaping.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface TileMatrix extends org.apache.sis.storage.tiling.TileMatrix {

    /**
     * Returns approximate resolutions (in units of CRS axes) of tiles in this tile matrix.
     * The array length shall be the number of CRS dimensions, and value at index <var>i</var>
     * is the resolution along CRS dimension <var>i</var> in units of the CRS axis <var>i</var>.
     *
     * <h4>Grid coverage resolution</h4>
     * If the tiled data is a {@link org.apache.sis.coverage.grid.GridCoverage},
     * then the resolution is the size of pixels (or cells in the multi-dimensional case).
     * If the coverage {@linkplain GridGeometry#getGridToCRS grid to CRS} transform is affine,
     * then that pixel size is constant everywhere.
     * Otherwise (non-affine transform) the pixel size varies depending on the location
     * and the returned value is the pixel size at some representative point,
     * typically the coverage center.
     *
     * <h4>Vector data resolution</h4>
     * If the tiled data is a set of features, then the resolution is a "typical" distance
     * (for example the average distance) between points in geometries.
     *
     * @return approximate resolutions of tiles.
     *
     * @see GridGeometry#getResolution(boolean)
     */
    default double[] getResolution() {
        double[] resolution = getTilingScheme().getResolution(true);
        Dimension tileSize = getTileSize();
        resolution[0] /= tileSize.width;
        resolution[1] /= tileSize.height;
        return resolution;
    }

    /**
     * Retrieves a stream of existing tiles in the specified region. The stream contains
     * the {@linkplain TileStatus#EXISTS existing} tiles that are inside the given region
     * and excludes all {@linkplain TileStatus#MISSING missing} tiles.
     * If a tile is {@linkplain TileStatus#IN_ERROR in error},
     * then the stream should nevertheless return a {@link Tile} instance
     * but its {@link Tile#getResource()} method should throw the exception.
     *
     * <p>The {@code parallel} argument specifies whether a parallelized stream is desired.
     * If {@code false}, the stream is guaranteed to be sequential.
     * If {@code true}, the stream may or may not be parallel;
     * implementations are free to ignore this argument if they do not support parallelism.</p>
     *
     * @param  indicesRanges  ranges of tile indices in all dimensions, or {@code null} for all tiles.
     * @param  parallel  {@code true} for a parallel stream (if supported), or {@code false} for a sequential stream.
     * @return stream of tiles, excluding {@linkplain TileStatus#MISSING missing} tiles.
     *         Iteration order of the stream may vary from one implementation to another and from one call to another.
     * @throws DataStoreException if the stream creation failed.
     */
    default Stream<Tile> getTiles(GridExtent indicesRanges, boolean parallel) throws DataStoreException {
        if (indicesRanges == null) indicesRanges = getTilingScheme().getExtent();
        final Stream<long[]> stream = TileMatrices.pointStream(indicesRanges);
        return stream.map((long[] t) -> {
            try {
                return getTile(t).orElse(null);
            } catch (DataStoreException ex) {
                return TileInError.create(t, ex);
            }
        }).filter(Objects::nonNull);
    }

    /**
     * Returns the upper left corner of the TileMatrix.
     * The corner is in PixelInCell.CELL_CORNER, so it contains a translate of a half
     * pixel compared to a GridToCrs transform of a coverage.
     *
     * @return upper left corner of the TileMatrix, expressed in pyramid CRS.
     */
    @Deprecated
    default DirectPosition getUpperLeftCorner() {
        final GeneralEnvelope envelope = new GeneralEnvelope(getTilingScheme().getEnvelope());
        final GeneralDirectPosition upperLeft = new GeneralDirectPosition(envelope.getCoordinateReferenceSystem());
        upperLeft.setOrdinate(0, envelope.getMinimum(0));
        upperLeft.setOrdinate(1, envelope.getMaximum(1));
        for (int i = 2, n = envelope.getDimension(); i < n; i++) {
            upperLeft.setOrdinate(i, envelope.getMedian(i));
        }
        return upperLeft;
    }

    /**
     * @return size of the grid in number of columns/rows.
     */
    @Deprecated
    default Dimension getGridSize() {
        final GridExtent extent = getTilingScheme().getExtent();
        return new Dimension(Math.toIntExact(extent.getSize(0)), Math.toIntExact(extent.getSize(1)));
    }

    /**
     * @return size of a pixel in crs unit
     */
    @Deprecated
    default double getScale() {
        return getResolution()[0];
    }

    /**
     * @return tile dimension in cell units.
     */
    @Deprecated
    Dimension getTileSize();

    /**
     * Find a tile in the TileMatrix.
     * This tile should be fast to access, it's purpose is to provide a sample
     * of content of the TileMatrix.
     *
     * The returned tile may not really exist or be part of this TileMatrix at all.
     *
     * @return tile, should not be null.
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Deprecated
    public Tile anyTile() throws DataStoreException;

}
