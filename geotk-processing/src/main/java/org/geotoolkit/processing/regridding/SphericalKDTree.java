package org.geotoolkit.processing.regridding;

/**
 * Simple 3D KDTree for nearest-neighbour lookup on a sphere.
 * <p>
 * Source points (lat/lon) are converted to 3D Cartesian (x,y,z) on a unit
 * sphere, then stored in a balanced KD-tree. Queries find the nearest source
 * point for each target point, subject to a maximum great-circle distance
 * (radius of influence).
 * <p>
 */
public class SphericalKDTree {

    /**
     * Constant for converting lat/lon to radians
     */
    private static final double DEG2RAD = Math.PI / 180.0;

    /**
     * Mean Earth radius in metres (used to convert radius of influence to angular distance)
     */
    private static final double EARTH_RADIUS_M = 6371000.0;

    /**
     * Source points in Cartesian coordinates
     */
    private final double[] xs, ys, zs;

    /**
     * Original indices of source points (to retrieve data values after finding nearest neighbor)
     */
    private final int[] indices;

    /**
     * Number of source points
     */
    private final int size;

    // KD-tree node storage (implicit balanced tree in arrays)
    private int[] tree;       // indices into xs/ys/zs
    private double[] splitVals;
    private int[] splitDims;
    private int treeSize;

    /**
     * Build a KDTree from source lat/lon arrays (flattened, in degrees).
     */
    public SphericalKDTree(float[] lats, float[] lons) {
        this.size = lats.length;
        this.xs = new double[size];
        this.ys = new double[size];
        this.zs = new double[size];
        this.indices = new int[size];

        for (int i = 0; i < size; i++) {
            double lat = lats[i] * DEG2RAD;
            double lon = lons[i] * DEG2RAD;
            double cosLat = Math.cos(lat);
            xs[i] = cosLat * Math.cos(lon);
            ys[i] = cosLat * Math.sin(lon);
            zs[i] = Math.sin(lat);
            indices[i] = i;
        }

        // Build balanced KD-tree
        treeSize = size;
        tree = new int[size];
        splitVals = new double[size];
        splitDims = new int[size];
        buildTree(0, size, 0, 0);
    }

    /**
     * Resample nearest neighbour.
     *
     * @param srcData           Source data array (flat, same length as source lats/lons).
     *                          NaN values are treated as missing.
     * @param targetLats        Target latitude array (flat, in degrees)
     * @param targetLons        Target longitude array (flat, in degrees)
     * @param radiusOfInfluence Maximum distance in metres
     * @return Resampled array (same length as targetLats), NaN where no source found.
     */
    public float[] resampleNearest(float[] srcData, float[] targetLats, float[] targetLons,
                                   double radiusOfInfluence) {
        int nTarget = targetLats.length;
        float[] result = new float[nTarget];

        // Convert radius to Cartesian distance on unit sphere
        double maxAngle = radiusOfInfluence / EARTH_RADIUS_M;
        // chord length^2 = 2 - 2*cos(angle)
        double maxDistSq = 2.0 - 2.0 * Math.cos(maxAngle);

        for (int i = 0; i < nTarget; i++) {
            double lat = targetLats[i] * DEG2RAD;
            double lon = targetLons[i] * DEG2RAD;
            double cosLat = Math.cos(lat);
            double qx = cosLat * Math.cos(lon);
            double qy = cosLat * Math.sin(lon);
            double qz = Math.sin(lat);

            int bestIdx = findNearest(qx, qy, qz, maxDistSq);

            if (bestIdx >= 0 && !Float.isNaN(srcData[bestIdx])) {
                result[i] = srcData[bestIdx];
            } else {
                result[i] = Float.NaN;
            }
        }
        return result;
    }

    /**
     * Resample using Gaussian-weighted average of neighbours within radius.
     *
     * @param srcData           Source data (flat)
     * @param targetLats        Target latitudes (degrees)
     * @param targetLons        Target longitudes (degrees)
     * @param radiusOfInfluence Max distance in metres
     * @param sigma             Gaussian sigma in metres
     * @return Resampled array
     */
    public float[] resampleGauss(float[] srcData, float[] targetLats, float[] targetLons,
                                 double radiusOfInfluence, double sigma) {
        int nTarget = targetLats.length;
        float[] result = new float[nTarget];

        double maxAngle = radiusOfInfluence / EARTH_RADIUS_M;
        double maxDistSq = 2.0 - 2.0 * Math.cos(maxAngle);
        double sigmaSq = (sigma / EARTH_RADIUS_M);
        sigmaSq = 2.0 - 2.0 * Math.cos(sigmaSq); // convert to chord^2 space
        // Gaussian: w = exp(-dist^2 / (2*sigma^2))

        for (int i = 0; i < nTarget; i++) {
            double lat = targetLats[i] * DEG2RAD;
            double lon = targetLons[i] * DEG2RAD;
            double cosLat = Math.cos(lat);
            double qx = cosLat * Math.cos(lon);
            double qy = cosLat * Math.sin(lon);
            double qz = Math.sin(lat);

            double[] weightedResult = gaussianNeighbours(qx, qy, qz, maxDistSq, sigmaSq, srcData);

            if (weightedResult[1] > 0) {
                result[i] = (float) (weightedResult[0] / weightedResult[1]);
            } else {
                result[i] = Float.NaN;
            }
        }
        return result;
    }

    // ---------------------------------------------------------------
    // KD-tree build (recursive, balanced)
    // ---------------------------------------------------------------

    private void buildTree(int lo, int hi, int depth, int nodeIdx) {
        if (lo >= hi) return;

        int dim = depth % 3;
        int mid = (lo + hi) / 2;

        // Partial sort: put median element at mid
        nthElement(lo, hi, mid, dim);

        if (nodeIdx < treeSize) {
            tree[nodeIdx] = indices[mid];
            splitDims[nodeIdx] = dim;
            splitVals[nodeIdx] = getCoord(indices[mid], dim);
        }

        // Build left and right subtrees
        // Using implicit tree: left child = 2*nodeIdx+1, right = 2*nodeIdx+2
        // But for large datasets this wastes memory. Use recursive approach with
        // index ranges instead.
        // Actually, let's use a simpler approach: store the sorted indices array
        // and do recursive search on it.
        buildTree(lo, mid, depth + 1, 2 * nodeIdx + 1);
        buildTree(mid + 1, hi, depth + 1, 2 * nodeIdx + 2);
    }

    // ---------------------------------------------------------------
    // KD-tree query: nearest neighbour
    // ---------------------------------------------------------------

    private double bestDistSq;
    private int bestIndex;

    private int findNearest(double qx, double qy, double qz, double maxDistSq) {
        bestDistSq = maxDistSq;
        bestIndex = -1;
        searchNearest(0, 0, size, 0, qx, qy, qz);
        return bestIndex;
    }

    private void searchNearest(int nodeIdx, int lo, int hi, int depth,
                               double qx, double qy, double qz) {
        if (lo >= hi || nodeIdx >= treeSize) return;

        int mid = (lo + hi) / 2;
        int idx = indices[mid];

        double dx = xs[idx] - qx;
        double dy = ys[idx] - qy;
        double dz = zs[idx] - qz;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq < bestDistSq) {
            bestDistSq = distSq;
            bestIndex = idx;
        }

        int dim = depth % 3;
        double qVal = getQueryCoord(qx, qy, qz, dim);
        double splitVal = getCoord(idx, dim);
        double diff = qVal - splitVal;

        // Search closer subtree first
        int firstLo, firstHi, secondLo, secondHi;
        if (diff <= 0) {
            firstLo = lo; firstHi = mid;
            secondLo = mid + 1; secondHi = hi;
        } else {
            firstLo = mid + 1; firstHi = hi;
            secondLo = lo; secondHi = mid;
        }

        searchNearest(nodeIdx * 2 + (diff <= 0 ? 1 : 2), firstLo, firstHi, depth + 1, qx, qy, qz);

        // Check if we need to search the other subtree
        if (diff * diff < bestDistSq) {
            searchNearest(nodeIdx * 2 + (diff <= 0 ? 2 : 1), secondLo, secondHi, depth + 1, qx, qy, qz);
        }
    }

    // ---------------------------------------------------------------
    // Gaussian: collect all neighbours within radius
    // ---------------------------------------------------------------

    private double gaussSum, gaussWeightSum;

    private double[] gaussianNeighbours(double qx, double qy, double qz,
                                        double maxDistSq, double sigmaSq,
                                        float[] srcData) {
        gaussSum = 0;
        gaussWeightSum = 0;
        searchGauss(0, size, 0, qx, qy, qz, maxDistSq, sigmaSq, srcData);
        return new double[]{gaussSum, gaussWeightSum};
    }

    private void searchGauss(int lo, int hi, int depth,
                             double qx, double qy, double qz,
                             double maxDistSq, double sigmaSq, float[] srcData) {
        if (lo >= hi) return;

        int mid = (lo + hi) / 2;
        int idx = indices[mid];

        double dx = xs[idx] - qx;
        double dy = ys[idx] - qy;
        double dz = zs[idx] - qz;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq < maxDistSq && !Float.isNaN(srcData[idx])) {
            double w = Math.exp(-distSq / (2.0 * sigmaSq));
            gaussSum += w * srcData[idx];
            gaussWeightSum += w;
        }

        int dim = depth % 3;
        double qVal = getQueryCoord(qx, qy, qz, dim);
        double splitVal = getCoord(idx, dim);
        double diff = qVal - splitVal;

        if (diff <= 0) {
            searchGauss(lo, mid, depth + 1, qx, qy, qz, maxDistSq, sigmaSq, srcData);
            if (diff * diff < maxDistSq) {
                searchGauss(mid + 1, hi, depth + 1, qx, qy, qz, maxDistSq, sigmaSq, srcData);
            }
        } else {
            searchGauss(mid + 1, hi, depth + 1, qx, qy, qz, maxDistSq, sigmaSq, srcData);
            if (diff * diff < maxDistSq) {
                searchGauss(lo, mid, depth + 1, qx, qy, qz, maxDistSq, sigmaSq, srcData);
            }
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private double getCoord(int idx, int dim) {
        return switch (dim) {
            case 0 -> xs[idx];
            case 1 -> ys[idx];
            case 2 -> zs[idx];
            default -> 0;
        };
    }

    private static double getQueryCoord(double qx, double qy, double qz, int dim) {
        return switch (dim) {
            case 0 -> qx;
            case 1 -> qy;
            case 2 -> qz;
            default -> 0;
        };
    }

    /**
     * Partial sort: rearrange indices[lo..hi) so that
     * indices[mid] is the median along the given dimension.
     * Uses quickselect (Floyd-Rivest variant for simplicity).
     */
    private void nthElement(int lo, int hi, int k, int dim) {
        while (lo < hi - 1) {
            int pivotIdx = lo + (int) (Math.random() * (hi - lo));
            double pivotVal = getCoord(indices[pivotIdx], dim);

            // Move pivot to end
            swap(pivotIdx, hi - 1);
            int store = lo;
            for (int i = lo; i < hi - 1; i++) {
                if (getCoord(indices[i], dim) < pivotVal) {
                    swap(i, store);
                    store++;
                }
            }
            swap(store, hi - 1);

            if (store == k) return;
            else if (k < store) hi = store;
            else lo = store + 1;
        }
    }

    private void swap(int a, int b) {
        int tmp = indices[a];
        indices[a] = indices[b];
        indices[b] = tmp;
    }
}
