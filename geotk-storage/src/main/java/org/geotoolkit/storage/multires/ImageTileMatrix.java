/*
 * (C) 2022, Geomatys
 */
package org.geotoolkit.storage.multires;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ImageTileMatrix extends TileMatrix {

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
        int[] tileSize = getTileSize();
        resolution[0] /= tileSize[0];
        resolution[1] /= tileSize[1];
        return resolution;
    }

    /**
     * @return tile dimension in cell units.
     */
    int[] getTileSize();
}
