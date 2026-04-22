package org.geotoolkit.processing.coverage.regridding;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.processing.regridding.SphericalKDTree;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Regrids swath (curvilinear) GridCoverageResource tiles onto a regular lat/lon GridCoverage.
 *
 * <p>Source pixel positions are derived from each coverage's grid-to-CRS transform, allowing
 * this class to handle both affine (regular grid) and non-affine (curvilinear/swath) coverages.
 * Multiple tiles are fused using quality-aware mosaicking when a quality band is specified.</p>
 */
public class CoverageSwathRegridder {

    public enum ResampleMethod { NEAREST, GAUSS }

    private final double resolution;
    private final double[] bbox;
    private final double radiusOfInfluence;
    private final ResampleMethod method;
    private final double sigma;
    private final int qualityBandIndex;
    private final int minQualityLevel;
    private final String outputLonConvention;

    private static final Logger LOGGER = Logger.getLogger(CoverageSwathRegridder.class.getName());

    public CoverageSwathRegridder(double resolution, double[] bbox,
                                   double radiusOfInfluence, ResampleMethod method,
                                   double sigma, int qualityBandIndex, int minQualityLevel,
                                   String outputLonConvention) {
        this.resolution = resolution;
        this.bbox = bbox;
        this.radiusOfInfluence = radiusOfInfluence;
        this.method = method;
        this.sigma = sigma;
        this.qualityBandIndex = qualityBandIndex;
        this.minQualityLevel = minQualityLevel;
        this.outputLonConvention = outputLonConvention;
    }

    // -------------------------------------------------------------------------
    // Internal tile representation
    // -------------------------------------------------------------------------

    private static final class TileData {
        /** Flat arrays of pixel latitudes and longitudes (row-major order). */
        final float[] lats;
        final float[] lons;
        /** Band data: bandData[bandIndex][pixelIndex] in row-major order. */
        final float[][] bandData;

        TileData(float[] lats, float[] lons, float[][] bandData) {
            this.lats = lats;
            this.lons = lons;
            this.bandData = bandData;
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Reads all input resources, regrids them onto a common regular lat/lon grid, and
     * returns the merged GridCoverage.
     *
     * @param resources list of swath tile resources
     * @return merged coverage on a regular WGS84 lat/lon grid
     */
    public GridCoverage merge(List<GridCoverage> resources) throws Exception {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException("No coverage resources provided.");
        }

        // 1. Read all tiles
        List<TileData> tiles = new ArrayList<>(resources.size());
        for (int i = 0; i < resources.size(); i++) {
            LOGGER.info(String.format("Reading tile [%d/%d]", i + 1, resources.size()));
            tiles.add(readTile(resources.get(i)));
        }

        int nbBands = tiles.get(0).bandData.length;

        // 2. Detect antimeridian crossing
        boolean crossesAm = detectAntimeridianCrossing(tiles);
        if (crossesAm) {
            LOGGER.info("Antimeridian crossing detected -- working in [0, 360].");
            for (TileData tile : tiles) {
                for (int i = 0; i < tile.lons.length; i++) {
                    if (tile.lons[i] < 0) tile.lons[i] += 360f;
                }
            }
        }

        // 3. Determine bounding box
        double minLon, minLat, maxLon, maxLat;
        if (bbox != null) {
            minLon = bbox[0]; minLat = bbox[1]; maxLon = bbox[2]; maxLat = bbox[3];
            if (minLon > maxLon) {
                crossesAm = true;
                maxLon += 360.0;
                LOGGER.info("Antimeridian crossing inferred from provided bbox.");
            }
        } else {
            double[] computed = computeBbox(tiles);
            minLon = computed[0]; minLat = computed[1]; maxLon = computed[2]; maxLat = computed[3];
        }

        // 4. Build target regular grid
        int nx = (int) Math.ceil((maxLon - minLon) / resolution);
        int ny = (int) Math.ceil((maxLat - minLat) / resolution);
        // targetLats[j] = center lat of row j, j=0 is southernmost
        float[] targetLats = new float[ny];
        float[] targetLons = new float[nx];
        for (int i = 0; i < ny; i++) targetLats[i] = (float) (minLat + i * resolution);
        for (int i = 0; i < nx; i++) targetLons[i] = (float) (minLon + i * resolution);

        LOGGER.info(String.format("Target grid: %d cols x %d rows (%,d pixels)", nx, ny, (long) nx * ny));

        // 5. Flatten target grid for KDTree queries
        float[] flatTargetLats = new float[ny * nx];
        float[] flatTargetLons = new float[ny * nx];
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                flatTargetLats[j * nx + i] = targetLats[j];
                flatTargetLons[j * nx + i] = targetLons[i];
            }
        }

        // 6. Initialize merged grids: merged[band][row_j][col_i], j=0 = southernmost
        float[][][] merged = new float[nbBands][ny][nx];
        for (int b = 0; b < nbBands; b++) {
            for (float[] row : merged[b]) Arrays.fill(row, Float.NaN);
        }
        float[][] bestQuality = new float[ny][nx];
        for (float[] row : bestQuality) Arrays.fill(row, -1f);

        // 7. Regrid each tile into the merged accumulator
        for (int t = 0; t < tiles.size(); t++) {
            LOGGER.info(String.format("Regridding tile [%d/%d]", t + 1, tiles.size()));
            regridTile(tiles.get(t), flatTargetLats, flatTargetLons, ny, nx, nbBands, merged, bestQuality);
        }

        // 8. Optionally reorder columns if antimeridian crossing + [-180, 180] convention
        float[] outputLons = Arrays.copyOf(targetLons, nx);
        if (crossesAm && "[-180, 180]".equals(outputLonConvention)) {
            for (int i = 0; i < outputLons.length; i++) {
                if (outputLons[i] > 180) outputLons[i] -= 360f;
            }
            final float[] lonsForSort = outputLons;
            int[] sortIdx = IntStream.range(0, nx)
                    .boxed().sorted(Comparator.comparingDouble(a -> lonsForSort[a]))
                    .mapToInt(Integer::intValue).toArray();
            float[] sorted = new float[nx];
            for (int i = 0; i < nx; i++) sorted[i] = outputLons[sortIdx[i]];
            outputLons = sorted;
            for (int b = 0; b < nbBands; b++) reorderColumns(merged[b], sortIdx, ny, nx);
            reorderColumns(bestQuality, sortIdx, ny, nx);
            // Recalculate minLon from reordered lons
            minLon = outputLons[0];
        }

        // 9. Build and return GridCoverage
        return buildGridCoverage(merged, nbBands, nx, ny, minLon, targetLats, outputLons);
    }

    // -------------------------------------------------------------------------
    // Tile regridding
    // -------------------------------------------------------------------------

    private void regridTile(TileData tile, float[] flatTargetLats, float[] flatTargetLons,
                             int ny, int nx, int nbBands, float[][][] merged, float[][] bestQuality) {
        SphericalKDTree kdTree = new SphericalKDTree(tile.lats, tile.lons);

        // Resample quality band (or use uniform quality=1 if no quality band)
        float[] qlResampled;
        if (qualityBandIndex >= 0 && qualityBandIndex < tile.bandData.length) {
            float[] qlSrc = Arrays.copyOf(tile.bandData[qualityBandIndex], tile.bandData[qualityBandIndex].length);
            if (minQualityLevel > 0) {
                for (int i = 0; i < qlSrc.length; i++) {
                    if (qlSrc[i] < minQualityLevel) qlSrc[i] = Float.NaN;
                }
            }
            qlResampled = kdTree.resampleNearest(qlSrc, flatTargetLats, flatTargetLons, radiusOfInfluence);
        } else {
            qlResampled = new float[ny * nx];
            Arrays.fill(qlResampled, 1f);
        }

        // Resample and mosaic each band
        for (int b = 0; b < nbBands; b++) {
            float[] srcData = Arrays.copyOf(tile.bandData[b], tile.bandData[b].length);

            // Mask source pixels that are below minimum quality
            if (qualityBandIndex >= 0 && minQualityLevel > 0) {
                float[] qlRaw = tile.bandData[qualityBandIndex];
                for (int i = 0; i < srcData.length; i++) {
                    if (qlRaw[i] < minQualityLevel) srcData[i] = Float.NaN;
                }
            }

            float[] resampled = (method == ResampleMethod.GAUSS)
                    ? kdTree.resampleGauss(srcData, flatTargetLats, flatTargetLons, radiusOfInfluence, sigma)
                    : kdTree.resampleNearest(srcData, flatTargetLats, flatTargetLons, radiusOfInfluence);

            float[][] grid = merged[b];
            for (int j = 0; j < ny; j++) {
                for (int i = 0; i < nx; i++) {
                    int idx = j * nx + i;
                    float val = resampled[idx];
                    if (!Float.isNaN(val)) {
                        boolean empty = Float.isNaN(grid[j][i]);
                        boolean better = qlResampled[idx] > bestQuality[j][i];
                        if (empty || better) {
                            grid[j][i] = val;
                        }
                    }
                }
            }
        }

        // Update best quality tracker
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                int idx = j * nx + i;
                if (!Float.isNaN(qlResampled[idx]) && qlResampled[idx] > bestQuality[j][i]) {
                    bestQuality[j][i] = qlResampled[idx];
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Tile reading: extract lat/lon and band data from a GridCoverageResource
    // -------------------------------------------------------------------------

    /**
     * Reads a single tile from a GridCoverageResource.
     * Pixel positions are derived by applying the coverage's grid-to-CRS transform
     * followed by a reprojection to WGS84 normalised geographic (lon, lat).
     */
    private TileData readTile(GridCoverage coverage) throws DataStoreException, FactoryException, TransformException {
        RenderedImage image = coverage.render(null);

        int nx = image.getWidth();
        int ny = image.getHeight();
        int minX = image.getMinX();
        int minY = image.getMinY();
        int nbBands = image.getSampleModel().getNumBands();

        // Extract band data in row-major order: bandData[b][row * nx + col]
        java.awt.image.Raster raster = image.getData();
        float[][] bandData = new float[nbBands][nx * ny];
        for (int b = 0; b < nbBands; b++) {
            raster.getSamples(minX, minY, nx, ny, b, bandData[b]);
        }

        // Build transform chain: grid -> CRS -> WGS84(lon, lat)
        GridGeometry gg = coverage.getGridGeometry();
        MathTransform gridToCRS = gg.getGridToCRS(PixelInCell.CELL_CENTER);

        CoordinateReferenceSystem fullCRS = gg.getCoordinateReferenceSystem();
        CoordinateReferenceSystem hCRS = CRS.getHorizontalComponent(fullCRS);
        if (hCRS == null) hCRS = fullCRS;

        // normalizedGeographic has (lon, lat) axis order
        CoordinateReferenceSystem normalizedGeo = CommonCRS.WGS84.normalizedGeographic();
        MathTransform crsToGeo = CRS.findOperation(hCRS, normalizedGeo, null).getMathTransform();

        float[] lats = new float[nx * ny];
        float[] lons = new float[nx * ny];

        int gridDims = gridToCRS.getSourceDimensions();
        int crsDims = gridToCRS.getTargetDimensions();
        double[] gridPt = new double[gridDims];
        double[] crsPt = new double[crsDims];
        double[] geoPt = new double[2];

        for (int row = 0; row < ny; row++) {
            for (int col = 0; col < nx; col++) {
                gridPt[0] = minX + col;
                gridPt[1] = minY + row;
                // Other dimensions (e.g. time) left at 0 -- works for 2D/sliced coverages
                gridToCRS.transform(gridPt, 0, crsPt, 0, 1);
                // Use the first two CRS dimensions for the geographic transform
                double[] crs2d = {crsPt[0], crsPt[1]};
                crsToGeo.transform(crs2d, 0, geoPt, 0, 1);
                int idx = row * nx + col;
                lons[idx] = (float) geoPt[0]; // normalizedGeo: dimension 0 = longitude
                lats[idx] = (float) geoPt[1]; // normalizedGeo: dimension 1 = latitude
            }
        }

        return new TileData(lats, lons, bandData);
    }

    // -------------------------------------------------------------------------
    // GridCoverage construction
    // -------------------------------------------------------------------------

    /**
     * Assembles the merged float arrays into a GridCoverage on a regular WGS84 lat/lon grid.
     *
     * <p>The output image is stored north-first (image row 0 = northernmost latitude).
     * The grid-to-CRS affine transform uses {@link PixelInCell#CELL_CENTER} convention.</p>
     */
    private GridCoverage buildGridCoverage(float[][][] merged, int nbBands, int nx, int ny,
                                            double minLon, float[] targetLats, float[] outputLons) throws Exception {
        // Build north-first image: image row 0 = targetLats[ny-1] (northernmost)
        BufferedImage image = BufferedImages.createImage(nx, ny, nbBands, DataBuffer.TYPE_FLOAT);
        WritableRaster raster = image.getRaster();

        for (int b = 0; b < nbBands; b++) {
            for (int row = 0; row < ny; row++) {
                int j = ny - 1 - row; // merged[b][0] = southernmost, image row 0 = northernmost
                for (int col = 0; col < nx; col++) {
                    raster.setSample(col, row, b, merged[b][j][col]);
                }
            }
        }

        // CELL_CENTER affine: (col, row) -> (lon, lat)
        // col=0, row=0 -> center of northwest pixel = (minLon, targetLats[ny-1])
        double northmostLat = targetLats[ny - 1];
        AffineTransform2D g2c = new AffineTransform2D(resolution, 0, 0, -resolution, minLon, northmostLat);

        CoordinateReferenceSystem geoCRS = CommonCRS.WGS84.normalizedGeographic(); // (lon, lat)
        GridGeometry gridGeom = new GridGeometry(new GridExtent(nx, ny), PixelInCell.CELL_CENTER, g2c, geoCRS);

        GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setDomain(gridGeom);
        gcb.setValues(image);
        return gcb.build();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean detectAntimeridianCrossing(List<TileData> tiles) {
        for (TileData tile : tiles) {
            boolean hasNeg = false, hasPos = false;
            for (float lon : tile.lons) {
                if (lon < -90) hasNeg = true;
                if (lon > 90) hasPos = true;
                if (hasNeg && hasPos) return true;
            }
        }
        return false;
    }

    private static double[] computeBbox(List<TileData> tiles) {
        double minLon = Double.MAX_VALUE, minLat = Double.MAX_VALUE;
        double maxLon = -Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        for (TileData tile : tiles) {
            for (int i = 0; i < tile.lons.length; i++) {
                float lon = tile.lons[i];
                float lat = tile.lats[i];
                if (Float.isNaN(lon) || Float.isNaN(lat)) continue;
                if (lon < minLon) minLon = lon;
                if (lon > maxLon) maxLon = lon;
                if (lat < minLat) minLat = lat;
                if (lat > maxLat) maxLat = lat;
            }
        }
        return new double[]{minLon, minLat, maxLon, maxLat};
    }

    private static void reorderColumns(float[][] grid, int[] sortIdx, int ny, int nx) {
        for (int j = 0; j < ny; j++) {
            float[] newRow = new float[nx];
            for (int i = 0; i < nx; i++) newRow[i] = grid[j][sortIdx[i]];
            grid[j] = newRow;
        }
    }
}
