package org.geotoolkit.processing.regridding;

import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Regrids swath (curvilinear) NetCDF tiles onto a regular lat/lon grid.
 * <p>
 * When {@code keepTime} is false (default), all tiles are fused into a single
 * 2D grid via quality-aware mosaicking.
 * <p>
 * When {@code keepTime} is true, each unique timestamp from the source tiles
 * becomes a time step in the output. Tiles sharing the same timestamp are
 * fused together; tiles with different timestamps produce separate time steps.
 */
public class SwathRegridder {

    public enum ResampleMethod { NEAREST, GAUSS }

    private final double resolution;
    private final double[] bbox;
    private final List<String> variables;
    private final double radiusOfInfluence;
    private final ResampleMethod method;
    private final double sigma;
    private final int minQualityLevel;
    private final String outputLonConvention;
    private final boolean keepTime;

    private static final Logger LOGGER = Logger.getLogger(SwathRegridder.class.getName());

    public SwathRegridder(double resolution, double[] bbox, List<String> variables,
                          double radiusOfInfluence, ResampleMethod method,
                          double sigma, int minQualityLevel, String outputLonConvention,
                          boolean keepTime) {
        this.resolution = resolution;
        this.bbox = bbox;
        this.variables = variables;
        this.radiusOfInfluence = radiusOfInfluence;
        this.method = method;
        this.sigma = sigma;
        this.minQualityLevel = minQualityLevel;
        this.outputLonConvention = outputLonConvention;
        this.keepTime = keepTime;
    }

    /** Convenience constructor without keepTime (defaults to false). */
    public SwathRegridder(double resolution, double[] bbox, List<String> variables,
                          double radiusOfInfluence, ResampleMethod method,
                          double sigma, int minQualityLevel, String outputLonConvention) {
        this(resolution, bbox, variables, radiusOfInfluence, method,
                sigma, minQualityLevel, outputLonConvention, false);
    }

    /**
     * Merge multiple swath NetCDF files onto a regular grid and write result.
     */
    public void merge(List<Path> files, Path outputPath) throws Exception {

        // 1. Detect antimeridian crossing
        boolean crossesAm = detectAntimeridianCrossing(files);
        if (crossesAm) {
            LOGGER.info("Antimeridian crossing detected -- working in [0, 360].");
        }

        // 2. Determine bounding box
        double minLon, minLat, maxLon, maxLat;
        if (bbox != null) {
            minLon = bbox[0]; minLat = bbox[1]; maxLon = bbox[2]; maxLat = bbox[3];
            if (minLon > maxLon) {
                crossesAm = true;
                maxLon += 360.0;
                LOGGER.info("Antimeridian crossing inferred from bbox.");
            }
        } else {
            double[] computed = computeBbox(files, crossesAm);
            minLon = computed[0]; minLat = computed[1]; maxLon = computed[2]; maxLat = computed[3];
        }

        // 3. Build target grid
        int nx = (int) Math.ceil((maxLon - minLon) / resolution);
        int ny = (int) Math.ceil((maxLat - minLat) / resolution);
        float[] targetLats = new float[ny];
        float[] targetLons = new float[nx];
        for (int i = 0; i < ny; i++) targetLats[i] = (float) (minLat + i * resolution);
        for (int i = 0; i < nx; i++) targetLons[i] = (float) (minLon + i * resolution);

        LOGGER.info(String.format("Target grid: %d x %d (%,d pixels)%n", ny, nx, (long) ny * nx));

        // 4. Discover variables
        List<String> vars = discoverVariables(files.get(0));
        LOGGER.info(String.format("Variables: %s%n", vars));
        LOGGER.info(String.format("Resampling: %s%n", method));
        LOGGER.info(String.format("Keep time: %s%n", keepTime));

        // 5. Flatten target grid for KDTree queries
        float[] flatTargetLats = new float[ny * nx];
        float[] flatTargetLons = new float[ny * nx];
        for (int j = 0; j < ny; j++) {
            for (int i = 0; i < nx; i++) {
                flatTargetLats[j * nx + i] = targetLats[j];
                flatTargetLons[j * nx + i] = targetLons[i];
            }
        }

        // 6. Compute lon reorder index (for antimeridian)
        float[] outputLons = Arrays.copyOf(targetLons, nx);
        int[] sortIdx = null;
        if (crossesAm && "[-180, 180]".equals(outputLonConvention)) {
            for (int i = 0; i < outputLons.length; i++) {
                if (outputLons[i] > 180) outputLons[i] -= 360f;
            }
            final float[] lonsForSort = outputLons;
            sortIdx = IntStream.range(0, nx)
                    .boxed().sorted(Comparator.comparingDouble(a -> lonsForSort[a]))
                    .mapToInt(Integer::intValue).toArray();
            float[] sorted = new float[nx];
            for (int i = 0; i < nx; i++) sorted[i] = outputLons[sortIdx[i]];
            outputLons = sorted;
        }

        // ================================================================
        // CASE 1: keepTime=false  (all fused into 2D)
        // ================================================================
        if (!keepTime) {
            int progressCount = 0;

            Map<String, float[][]> merged = new LinkedHashMap<>();
            for (String v : vars) merged.put(v, initNaN(ny, nx));
            float[][] bestQuality = initFill(ny, nx, -1f);

            for (Path file : files) {
                regridSingleTile(file, crossesAm, flatTargetLats, flatTargetLons,
                        ny, nx, vars, merged, bestQuality);
                progressCount++;
                LOGGER.info(String.format("Regridding tiles [%d/%d]: %s", progressCount, files.size(), file.getFileName().toString()));
            }

            // Apply lon reorder
            if (sortIdx != null) {
                for (String v : vars) reorderColumns(merged.get(v), sortIdx, ny, nx);
                reorderColumns(bestQuality, sortIdx, ny, nx);
            }

            writeNetcdf2D(outputPath, targetLats, outputLons, vars, merged, bestQuality,
                    crossesAm, files);

            // ================================================================
            // CASE 2: keepTime=true  (group by timestamp, 3D output)
            // ================================================================
        } else {
            LOGGER.info("Grouping tiles by timestamp...");
            LinkedHashMap<Instant, List<Path>> groups = groupFilesByTimestamp(files);

            List<Instant> timestamps = new ArrayList<>(groups.keySet());
            int nTimes = timestamps.size();
            LOGGER.info(String.format("Found %d distinct timestamp(s):%n", nTimes));
            for (var entry : groups.entrySet()) {
                int n = entry.getValue().size();
                String label = n == 1 ? "1 tile" : n + " tiles -> will fuse";
                LOGGER.info(String.format("  %s : %s%n", entry.getKey(), label));
            }

            // Allocate 3D: [time][var] -> float[ny][nx]
            Map<String, float[][][]> allData = new LinkedHashMap<>();
            for (String v : vars) allData.put(v, new float[nTimes][][]);
            float[][][] allQuality = new float[nTimes][][];

            int progressCount = 0;

            int tIdx = 0;
            for (var entry : groups.entrySet()) {
                List<Path> tileFiles = entry.getValue();

                if (tileFiles.size() > 1) {
                    LOGGER.info(String.format("  %s: fusing %d tiles%n", entry.getKey(), tileFiles.size()));
                } else {
                    LOGGER.info(String.format("  %s: regridding 1 tile%n", entry.getKey()));
                }

                // Regrid this group
                Map<String, float[][]> merged = new LinkedHashMap<>();
                for (String v : vars) merged.put(v, initNaN(ny, nx));
                float[][] bestQuality = initFill(ny, nx, -1f);

                for (Path file : tileFiles) {
                    regridSingleTile(file, crossesAm, flatTargetLats, flatTargetLons,
                            ny, nx, vars, merged, bestQuality);
                    progressCount++;
                    LOGGER.info(String.format("Regridding tiles [%d/%d]: %s", progressCount, files.size(), file.getFileName().toString()));
                }

                // Apply lon reorder
                if (sortIdx != null) {
                    for (String v : vars) reorderColumns(merged.get(v), sortIdx, ny, nx);
                    reorderColumns(bestQuality, sortIdx, ny, nx);
                }

                // Store in 3D arrays
                for (String v : vars) {
                    allData.get(v)[tIdx] = merged.get(v);
                }
                // Replace -1 with NaN for quality
                for (int j = 0; j < ny; j++) {
                    for (int i = 0; i < nx; i++) {
                        if (bestQuality[j][i] < 0) bestQuality[j][i] = Float.NaN;
                    }
                }
                allQuality[tIdx] = bestQuality;
                tIdx++;
            }

            writeNetcdf3D(outputPath, timestamps, targetLats, outputLons,
                    vars, allData, allQuality, crossesAm, files);
        }
    }

    // ---------------------------------------------------------------
    // Regrid a single tile into the merged/bestQuality accumulator
    // ---------------------------------------------------------------

    private void regridSingleTile(Path file, boolean crossesAm,
                                  float[] flatTargetLats, float[] flatTargetLons,
                                  int ny, int nx, List<String> vars,
                                  Map<String, float[][]> merged,
                                  float[][] bestQuality) throws IOException {

        try (NetcdfFile nc = NetcdfFiles.open(file.toString())) {
            float[] srcLats = readFlatFloat(nc, "lat");
            float[] srcLons = readFlatFloat(nc, "lon");

            if (crossesAm) {
                for (int i = 0; i < srcLons.length; i++) {
                    if (srcLons[i] < 0) srcLons[i] += 360f;
                }
            }

            SphericalKDTree kdTree = new SphericalKDTree(srcLats, srcLons);

            boolean hasQuality = nc.findVariable("quality_level") != null;
            float[] qlResampled;
            if (hasQuality) {
                float[] qlSrc = readFlatFloat(nc, "quality_level");
                if (minQualityLevel > 0) {
                    for (int i = 0; i < qlSrc.length; i++) {
                        if (qlSrc[i] < minQualityLevel) qlSrc[i] = Float.NaN;
                    }
                }
                qlResampled = kdTree.resampleNearest(qlSrc, flatTargetLats, flatTargetLons,
                        radiusOfInfluence);
            } else {
                qlResampled = new float[ny * nx];
                Arrays.fill(qlResampled, 1f);
            }

            for (String var : vars) {
                float[] srcData = readFlatFloat(nc, var);

                if (hasQuality && minQualityLevel > 0) {
                    float[] qlRaw = readFlatFloat(nc, "quality_level");
                    for (int i = 0; i < srcData.length; i++) {
                        if (qlRaw[i] < minQualityLevel) srcData[i] = Float.NaN;
                    }
                }

                float[] resampled;
                if (method == ResampleMethod.GAUSS) {
                    resampled = kdTree.resampleGauss(srcData, flatTargetLats, flatTargetLons,
                            radiusOfInfluence, sigma);
                } else {
                    resampled = kdTree.resampleNearest(srcData, flatTargetLats, flatTargetLons,
                            radiusOfInfluence);
                }

                float[][] grid = merged.get(var);
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

            for (int j = 0; j < ny; j++) {
                for (int i = 0; i < nx; i++) {
                    int idx = j * nx + i;
                    if (!Float.isNaN(qlResampled[idx]) && qlResampled[idx] > bestQuality[j][i]) {
                        bestQuality[j][i] = qlResampled[idx];
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------
    // Timestamp reading and grouping
    // ---------------------------------------------------------------

    /**
     * Read the timestamp from a NetCDF tile.
     * Tries the 'time' variable, then global attributes
     * (start_time, time_coverage_start, start_date).
     */
    static Instant readTileTimestamp(Path file) throws IOException {
        try (NetcdfFile nc = NetcdfFiles.open(file.toString())) {
            // Try 'time' variable
            Variable timeVar = nc.findVariable("time");
            if (timeVar != null) {
                Array arr = timeVar.read();
                if (arr.getSize() > 0) {
                    // Time is often stored as seconds/days since epoch
                    String units = null;
                    Attribute unitsAttr = timeVar.findAttribute("units");
                    if (unitsAttr != null) units = unitsAttr.getStringValue();

                    double val = arr.getDouble(0);

                    if (units != null && units.contains("seconds since")) {
                        String epoch = units.replace("seconds since", "").trim();
                        Instant base = Instant.parse(normalizeIso(epoch));
                        return base.plusSeconds((long) val);
                    } else if (units != null && units.contains("days since")) {
                        String epoch = units.replace("days since", "").trim();
                        Instant base = Instant.parse(normalizeIso(epoch));
                        return base.plusSeconds((long) (val * 86400));
                    } else {
                        // Maybe it's already a numeric epoch seconds
                        if (val > 1e9 && val < 2e10) {
                            return Instant.ofEpochSecond((long) val);
                        }
                    }
                }
            }

            // Fallback: global attributes
            for (String attrName : new String[]{"start_time", "time_coverage_start", "start_date"}) {
                Attribute attr = nc.findGlobalAttribute(attrName);
                if (attr != null && attr.getStringValue() != null) {
                    try {
                        return Instant.parse(normalizeIso(attr.getStringValue()));
                    } catch (Exception ignored) {}
                }
            }
        }

        LOGGER.warning(String.format("WARNING: no timestamp found in %s, using epoch%n", file.getFileName()));
        return Instant.EPOCH;
    }

    /**
     * Group files by their NetCDF timestamp, sorted chronologically.
     */
    private LinkedHashMap<Instant, List<Path>> groupFilesByTimestamp(List<Path> files) throws IOException {
        TreeMap<Instant, List<Path>> map = new TreeMap<>();
        for (Path f : files) {
            Instant ts = readTileTimestamp(f);
            map.computeIfAbsent(ts, k -> new ArrayList<>()).add(f);
        }
        return new LinkedHashMap<>(map);
    }

    /**
     * Normalize a date/time string to ISO 8601 with trailing Z if needed.
     * Handles "YYYYMMDDTHHMMSSZ", "YYYY-MM-DD HH:MM:SS", etc.
     */
    private static String normalizeIso(String s) {
        s = s.trim();
        // Replace space with T
        s = s.replace(' ', 'T');
        // Ensure trailing Z or timezone
        if (!s.endsWith("Z") && !s.contains("+") && !s.matches(".*[+-]\\d{2}:\\d{2}$")) {
            s = s + "Z";
        }
        return s;
    }

    // ---------------------------------------------------------------
    // NetCDF writing: 2D (no time)
    // ---------------------------------------------------------------

    private void writeNetcdf2D(Path path, float[] lats, float[] lons,
                               List<String> vars, Map<String, float[][]> data,
                               float[][] quality, boolean crossesAm,
                               List<Path> sourceFiles) throws Exception {
        int ny = lats.length;
        int nx = lons.length;

        NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(path.toString());

        Dimension latDim = builder.addDimension("lat", ny);
        Dimension lonDim = builder.addDimension("lon", nx);

        builder.addVariable("lat", DataType.FLOAT, List.of(latDim))
                .addAttribute(new Attribute("units", "degrees_north"))
                .addAttribute(new Attribute("standard_name", "latitude"));
        builder.addVariable("lon", DataType.FLOAT, List.of(lonDim))
                .addAttribute(new Attribute("units", "degrees_east"))
                .addAttribute(new Attribute("standard_name", "longitude"));

        for (String var : vars) {
            builder.addVariable(var, DataType.FLOAT, List.of(latDim, lonDim))
                    .addAttribute(new Attribute("_FillValue", Float.NaN));
        }
        builder.addVariable("mosaic_quality_level", DataType.FLOAT, List.of(latDim, lonDim))
                .addAttribute(new Attribute("long_name", "Best quality_level used during mosaicking"))
                .addAttribute(new Attribute("comment", "Minimum accepted quality_level was " + minQualityLevel))
                .addAttribute(new Attribute("_FillValue", Float.NaN));

        addGlobalAttributes(builder, crossesAm, sourceFiles);

        try (NetcdfFormatWriter writer = builder.build()) {
            writer.write("lat", Array.factory(DataType.FLOAT, new int[]{ny}, lats));
            writer.write("lon", Array.factory(DataType.FLOAT, new int[]{nx}, lons));

            for (String var : vars) {
                writer.write(var, Array.factory(DataType.FLOAT, new int[]{ny, nx},
                        flatten(data.get(var), ny, nx)));
            }

            float[] qFlat = flatten(quality, ny, nx);
            for (int i = 0; i < qFlat.length; i++) {
                if (qFlat[i] < 0) qFlat[i] = Float.NaN;
            }
            writer.write("mosaic_quality_level",
                    Array.factory(DataType.FLOAT, new int[]{ny, nx}, qFlat));
        }
        LOGGER.info(String.format("Saved: %s%n", path));
    }

    // ---------------------------------------------------------------
    // NetCDF writing: 3D (with time)
    // ---------------------------------------------------------------

    private void writeNetcdf3D(Path path, List<Instant> timestamps,
                               float[] lats, float[] lons,
                               List<String> vars, Map<String, float[][][]> data,
                               float[][][] quality, boolean crossesAm,
                               List<Path> sourceFiles) throws Exception {
        int nTimes = timestamps.size();
        int ny = lats.length;
        int nx = lons.length;

        // Convert timestamps to "seconds since 1970-01-01T00:00:00Z"
        double[] timeValues = new double[nTimes];
        for (int t = 0; t < nTimes; t++) {
            timeValues[t] = timestamps.get(t).getEpochSecond();
        }

        NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(path.toString());

        Dimension timeDim = builder.addDimension("time", nTimes);
        Dimension latDim = builder.addDimension("lat", ny);
        Dimension lonDim = builder.addDimension("lon", nx);

        builder.addVariable("time", DataType.DOUBLE, List.of(timeDim))
                .addAttribute(new Attribute("units", "seconds since 1970-01-01T00:00:00Z"))
                .addAttribute(new Attribute("standard_name", "time"))
                .addAttribute(new Attribute("calendar", "standard"));
        builder.addVariable("lat", DataType.FLOAT, List.of(latDim))
                .addAttribute(new Attribute("units", "degrees_north"))
                .addAttribute(new Attribute("standard_name", "latitude"));
        builder.addVariable("lon", DataType.FLOAT, List.of(lonDim))
                .addAttribute(new Attribute("units", "degrees_east"))
                .addAttribute(new Attribute("standard_name", "longitude"));

        List<Dimension> dims3d = List.of(timeDim, latDim, lonDim);

        for (String var : vars) {
            builder.addVariable(var, DataType.FLOAT, dims3d)
                    .addAttribute(new Attribute("_FillValue", Float.NaN));
        }
        builder.addVariable("mosaic_quality_level", DataType.FLOAT, dims3d)
                .addAttribute(new Attribute("long_name", "Best quality_level used during mosaicking"))
                .addAttribute(new Attribute("comment", "Minimum accepted quality_level was " + minQualityLevel))
                .addAttribute(new Attribute("_FillValue", Float.NaN));

        addGlobalAttributes(builder, crossesAm, sourceFiles);
        builder.addAttribute(new Attribute("time_steps", nTimes));

        try (NetcdfFormatWriter writer = builder.build()) {
            writer.write("time", Array.factory(DataType.DOUBLE, new int[]{nTimes}, timeValues));
            writer.write("lat", Array.factory(DataType.FLOAT, new int[]{ny}, lats));
            writer.write("lon", Array.factory(DataType.FLOAT, new int[]{nx}, lons));

            for (String var : vars) {
                float[] flat3d = flatten3D(data.get(var), nTimes, ny, nx);
                writer.write(var, Array.factory(DataType.FLOAT, new int[]{nTimes, ny, nx}, flat3d));
            }

            float[] qFlat = flatten3D(quality, nTimes, ny, nx);
            writer.write("mosaic_quality_level",
                    Array.factory(DataType.FLOAT, new int[]{nTimes, ny, nx}, qFlat));
        }
        LOGGER.info(String.format("Saved: %s (time steps: %d)%n", path, nTimes));
    }

    private void addGlobalAttributes(NetcdfFormatWriter.Builder builder,
                                     boolean crossesAm, List<Path> sourceFiles) {
        builder.addAttribute(new Attribute("title", "Merged satellite data (quality-aware regridding)"));
        builder.addAttribute(new Attribute("resolution", resolution + " degrees"));
        builder.addAttribute(new Attribute("resample_method", method.name()));
        builder.addAttribute(new Attribute("min_quality_level", minQualityLevel));
        builder.addAttribute(new Attribute("antimeridian_crossing", String.valueOf(crossesAm)));

        StringBuilder sb = new StringBuilder();
        for (Path f : sourceFiles) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(f.getFileName());
        }
        builder.addAttribute(new Attribute("source_files", sb.toString()));
    }

    // ---------------------------------------------------------------
    // NetCDF I/O helpers
    // ---------------------------------------------------------------

    private List<String> discoverVariables(Path file) throws IOException {
        if (variables != null && !variables.isEmpty()) return variables;

        List<String> found = new ArrayList<>();
        try (NetcdfFile nc = NetcdfFiles.open(file.toString())) {
            for (Variable v : nc.getVariables()) {
                DataType dt = v.getDataType();
                boolean numeric = (dt == DataType.FLOAT || dt == DataType.DOUBLE
                        || dt == DataType.SHORT || dt == DataType.INT);
                boolean hasNj = v.getDimensions().stream()
                        .anyMatch(d -> "nj".equals(d.getShortName()));
                if (numeric && hasNj) {
                    found.add(v.getShortName());
                }
            }
        }
        return found;
    }

    private float[] readFlatFloat(NetcdfFile nc, String varName) throws IOException {
        Variable v = nc.findVariable(varName);
        if (v == null) throw new IOException("Variable not found: " + varName);

        Array arr;
        try {
            if (v.getRank() == 3) {
                int[] origin = new int[]{0, 0, 0};
                int[] shape = v.getShape();
                shape[0] = 1;
                arr = v.read(origin, shape).reduce();
            } else {
                arr = v.read();
            }
        } catch (InvalidRangeException e) {
            throw new IOException("Error reading " + varName, e);
        }

        float[] result = new float[(int) arr.getSize()];
        for (int i = 0; i < result.length; i++) {
            result[i] = arr.getFloat(i);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Antimeridian / bbox helpers
    // ---------------------------------------------------------------

    private boolean detectAntimeridianCrossing(List<Path> files) throws IOException {
        for (Path f : files) {
            try (NetcdfFile nc = NetcdfFiles.open(f.toString())) {
                float[] lons = readFlatFloat(nc, "lon");
                boolean hasNeg = false, hasPos = false;
                for (float lon : lons) {
                    if (lon < -90) hasNeg = true;
                    if (lon > 90) hasPos = true;
                    if (hasNeg && hasPos) return true;
                }
            }
        }
        return false;
    }

    private double[] computeBbox(List<Path> files, boolean crossesAm) throws IOException {
        double minLon = 360, minLat = 90, maxLon = 0, maxLat = -90;
        for (Path f : files) {
            try (NetcdfFile nc = NetcdfFiles.open(f.toString())) {
                float[] lats = readFlatFloat(nc, "lat");
                float[] lons = readFlatFloat(nc, "lon");
                for (int i = 0; i < lons.length; i++) {
                    float lon = lons[i];
                    if (crossesAm && lon < 0) lon += 360f;
                    if (lon < minLon) minLon = lon;
                    if (lon > maxLon) maxLon = lon;
                    if (lats[i] < minLat) minLat = lats[i];
                    if (lats[i] > maxLat) maxLat = lats[i];
                }
            }
        }
        return new double[]{minLon, minLat, maxLon, maxLat};
    }

    // ---------------------------------------------------------------
    // Array utils
    // ---------------------------------------------------------------

    private static float[][] initNaN(int ny, int nx) {
        float[][] arr = new float[ny][nx];
        for (float[] row : arr) Arrays.fill(row, Float.NaN);
        return arr;
    }

    private static float[][] initFill(int ny, int nx, float val) {
        float[][] arr = new float[ny][nx];
        for (float[] row : arr) Arrays.fill(row, val);
        return arr;
    }

    private static float[] flatten(float[][] grid, int ny, int nx) {
        float[] flat = new float[ny * nx];
        for (int j = 0; j < ny; j++) {
            System.arraycopy(grid[j], 0, flat, j * nx, nx);
        }
        return flat;
    }

    private static float[] flatten3D(float[][][] grid, int nt, int ny, int nx) {
        float[] flat = new float[nt * ny * nx];
        for (int t = 0; t < nt; t++) {
            for (int j = 0; j < ny; j++) {
                System.arraycopy(grid[t][j], 0, flat, (t * ny + j) * nx, nx);
            }
        }
        return flat;
    }

    private static void reorderColumns(float[][] grid, int[] sortIdx, int ny, int nx) {
        for (int j = 0; j < ny; j++) {
            float[] newRow = new float[nx];
            for (int i = 0; i < nx; i++) newRow[i] = grid[j][sortIdx[i]];
            grid[j] = newRow;
        }
    }
}
