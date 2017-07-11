package org.geotoolkit.processing.science.drift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStores;
import org.geotoolkit.io.ContentFormatException;
import ucar.ma2.Array;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFileWriter;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Loads data of some specific format.
 * Sources:
 * <ul>
 *   <li>Current:    <a href="ftp://ftp.hycom.org/datasets/GLBa0.08/expt_91.2/2017">HYCOM FTP access</a></li>
 *   <li>Wind speed: <a href="ftp://podaac-ftp.jpl.nasa.gov/OceanWinds/windsat/L3/rss/v7/2015">NOAA FTP access</a></li>
 *   <li>Wind speed: <a href="https://donneespubliques.meteofrance.fr/">Public data from Météo-France</a></li>
 * </ul>
 */
abstract class DataSource {
    /**
     * Subdirectory of {@linkplain DriftPredictor#directory()} where to store the downloaded data.
     */
    private static final String CACHE = "cache";

    /**
     * The process for which this {@code DataSource} has been created.
     */
    final DriftPredictor process;

    /**
     * Directory where to store downloaded data.
     */
    final Path cacheDir;

    /**
     * The pattern of files in the {@link #cacheDir}. Example: {@code "glob:*.nc"}.
     */
    private final String filePattern;

    /**
     * Interval of time between two data files, in hours.
     * All data source are assumed to have a first file at midnight UTC.
     */
    final int timeInterval;

    /**
     * Whether the U and V components are in the same files or in two separated files.
     */
    private final boolean componentsInSeparatedFiles;

    /**
     * East-West (u) and North-South (v) component of the velocity vectors, in metres per second.
     * Note: in the case of wind speed, this is rather (speed, direction) vectors. We should convert to (u,v)
     * before to run the model, but we don't do that yet because we should load only the data in the region of
     * interest in order to reduce the amount of unnecessary calculations.
     */
    VelocityComponent u, v;

    /**
     * Creates a new data source.
     *
     * @param cacheSubDir   subdirectory of the cache directory where to store downloaded data.
     * @param filePattern   pattern of files in the {@link #cacheDir}. Example: {@code "glob:*.nc"}.
     * @param timeInterval  interval of time between two data files, in hours.
     * @param cisf          whether the U and V components are in the same files or in two separated files.
     */
    DataSource(final DriftPredictor process, final String cacheSubDir, final String filePattern,
            final int timeInterval, final boolean cisf) throws IOException
    {
        final Path parent = process.configuration().directory;
        if (!Files.isDirectory(parent)) {
            throw new FileNotFoundException(parent.toString() + " not found or not a directory.");
        }
        this.process      = process;
        this.cacheDir     = parent.resolve(CACHE).resolve(cacheSubDir);
        this.filePattern  = filePattern;
        this.timeInterval = timeInterval;
        componentsInSeparatedFiles = cisf;
        Files.createDirectories(cacheDir);
    }

    /**
     * Loads the archive (if available) or prediction (as a fallback) file for the given time.
     */
    abstract void load(OffsetDateTime requested) throws Exception;


    // ----------------------------------------------------------------------------------------------------------
    // HYCOM oceanic current data (NetCDF files)
    // ----------------------------------------------------------------------------------------------------------


    /**
     * Open files for current data.
     * Values are usually less than 1 m/s in magnitude.
     */
    static final class HYCOM extends DataSource {
        /**
         * Root URL to the directory containing the files to download.
         */
        static final String HYCOM_URL = "ftp://ftp.hycom.org/datasets/GLBa0.08/expt_91.2";

        /**
         * The pattern to the URL of the directory containing the files to download.
         * Example: {@code "ftp://ftp.hycom.org/datasets/GLBa0.08/expt_91.2/%d/%s/"}.
         * Parameters are the year (e.g. 2017) and the {@code "uvel"} or {@code "vvel"} sub-directory.
         */
        private final String downloadPath;

        /**
         * Pattern of RTOFS filename with four parameters, which are the year, the day of year,
         * the number of dimensions and the variable name.
         */
        private static final String FILENAME_PATTERN = "archv.%04d_%03d_00_%dz%c.nc";

        /**
         * The three-dimensional variable to transform into a two-dimensional variable for saving space.
         */
        private static final String VARIABLE_TO_TRIM = "Depth";

        /**
         * An encoded value for the current day. Used for checking if we need to load new data.
         */
        private int currentDay;

        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        HYCOM(final DriftPredictor process) throws IOException {
            super(process, "HYCOM", "glob:*.nc", 24, true);
            downloadPath = process.configuration().hycom_url + "/%d/%s/";
        }

        /**
         * Loads the prediction file for the given time.
         */
        @Override
        void load(final OffsetDateTime requested) throws Exception {
            final int year = requested.get(ChronoField.YEAR);
            final int day  = requested.get(ChronoField.DAY_OF_YEAR);
            final int code = (year << 9) | day;
            if (code != currentDay) {
                currentDay = code;
                final String uFileName = String.format(FILENAME_PATTERN, year, day, 2, 'u');
                final String vFileName = String.format(FILENAME_PATTERN, year, day, 2, 'v');
                final Path   directory = cacheDir;
                Path uFile = directory.resolve(uFileName);
                Path vFile = directory.resolve(vFileName);
                // If 2d data (surface) is not available, we try to use 3d data.
                if (!Files.isReadable(uFile))
                    uFile = directory.resolve(String.format(FILENAME_PATTERN, year, day, 3, 'u'));
                if (!Files.isReadable(vFile))
                    vFile = directory.resolve(String.format(FILENAME_PATTERN, year, day, 3, 'v'));

                uFile = install(year, "uvel", uFile, uFileName);
                vFile = install(year, "vvel", vFile, vFileName);
                NetcdfDataset nc = NetcdfDataset.openDataset(uFile.toString());
                try {
                    u = new VelocityComponent.HYCOM(nc, "u", (VelocityComponent.HYCOM) u, directory);
                } finally {
                    nc.close();
                }
                nc = NetcdfDataset.openDataset(vFile.toString());
                try {
                    v = new VelocityComponent.HYCOM(nc, "v", (VelocityComponent.HYCOM) u, directory);  // Really 'u', not 'v'.
                } finally {
                    nc.close();
                }
            }
        }

        /**
         * If the given three-dimensional data file does not exist,
         * downloads it and rewrites as a two-dimensional data file.
         *
         * @param  year        the year of data to download (if needed).
         * @param  subdir      the subdirectory of data to download: {@code uvel} or {@code vvel}.
         * @param  file3D      path to the three-dimensional data file.
         * @param  filename2D  name of the two-dimensional data file.
         * @return path to the file to use.
         */
        private Path install(final int year, final String subdir, Path file3D, final String filename2D) throws Exception {
            if (Files.exists(file3D)) {
                return file3D;                      // File exists but not downloaded by us - leave it unchanged.
            }
            final String filename = file3D.getFileName().toString();
            file3D = download(String.format(downloadPath, year, subdir) + filename, filename);
            process.progress("Rewriting as 2D file");
            final Path file2D = file3D.resolveSibling(filename2D);
            final Map<String,Dimension> dimensions = new LinkedHashMap<>();
            final NetcdfFile input = NetcdfFile.open(file3D.toString());
            final NetcdfFileWriter output = NetcdfFileWriter.createNew(NetcdfFileWriter.Version.netcdf3, file2D.toString());
            input.getGlobalAttributes().forEach((attribute) -> {
                output.addGroupAttribute(null, attribute);
            });
            for (final Dimension dimension : input.getDimensions()) {
                final String name = dimension.getShortName();
                int length = dimension.getLength();
                if (name.equals(VARIABLE_TO_TRIM)) {
                    length = 1;
                }
                if (dimensions.putIfAbsent(name, output.addDimension(null, name, length)) != null) {
                    throw new ContentFormatException("Duplicated dimension: " + name);
                }
            }
            final Map<Variable,int[]> variables = new LinkedHashMap<>();
            for (final Variable variable : input.getVariables()) {
                final int[] shape = variable.getShape();
                final List<Dimension> dim = new ArrayList<>(dimensions.size());
                int i = 0;
                for (final Dimension dimension : variable.getDimensions()) {
                    final String name = dimension.getShortName();
                    if (!dim.add(dimensions.get(name))) {
                        throw new ContentFormatException("Duplicated dimension:" + name);
                    }
                    if (name.equals(VARIABLE_TO_TRIM)) {
                        shape[i] = 1;
                    }
                    i++;
                }
                final Variable target = output.addVariable(null, variable.getShortName(), variable.getDataType(), dim);
                variable.getAttributes().forEach((attribute) -> {
                    output.addVariableAttribute(target, attribute);
                });
                if (variables.put(target, shape) != null) {
                    throw new ContentFormatException("Duplicated variable: " + target);
                }
            }
            output.create();
            for (final Map.Entry<Variable,int[]> entry : variables.entrySet()) {
                final int[]    shape  = entry.getValue();
                final Variable target = entry.getKey();
                final Variable source = input.findVariable(null, target.getShortName());
                final Array data = source.read(new int[shape.length], shape);
                output.write(target, data);
            }
            output.close();
            input.close();
            Files.delete(file3D);
            return file2D;
        }
    }


    // ----------------------------------------------------------------------------------------------------------
    // Météo-France ARPEGE data through WCS (GeoTIFF files)
    // ----------------------------------------------------------------------------------------------------------


    /**
     * Open files for wind speed data from Météo-France.
     * Values can be up to 50 m/s in magnitude.
     */
    static final class MeteoFrance extends DataSource {
        /**
         * The ROOT URL to Météo-France WCS service.
         * The parameter is the token given to the user by Météo-France.
         */
        private static final String ROOT_URL = "https://geoservices.meteofrance.fr/api/%s/MF-NWP-GLOBAL-ARPEGE-05-GLOBE-WCS?SERVICE=WCS&version=2.0.1";

        /**
         * The pattern to the URL of the directory containing the files to download.
         * Parameters are:
         * <ol>
         *   <li>the token given to the user by Météo-France</li>
         *   <li>the 'U' or 'V' parameter</li>
         *   <li>the time in ISO format (e.g. "2017-07-04T06:00:00Z")</li>
         *   <li>same time plus 3 hours (e.g. "2017-07-04T09:00:00Z")</li>
         * </ol>
         */
        private static final String DOWNLOAD_PATH = ROOT_URL + "&REQUEST=GetCoverage&format=image/tiff&coverageId="
                + "%c_COMPONENT_OF_WIND__SPECIFIC_HEIGHT_LEVEL_ABOVE_GROUND___%s&subset=time(%s)"
                + "&subset=lat(-90,90)&subset=long(-180,180)&subset=height(10)";

        /**
         * Pattern of cached filename with four parameters, which are the year, the day of year, the hour
         * and the variable name.
         */
        private static final String FILENAME_PATTERN = "ARPEGE_%04d_%03d_%02d_%c.tiff";

        /**
         * An encoded value for the current day. Used for checking if we need to load new data.
         */
        private int currentDay;

        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        MeteoFrance(final DriftPredictor process) throws IOException {
            super(process, "MeteoFrance", "glob:*.tiff", 6, true);
        }

        /**
         * Loads the prediction file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        void load(final OffsetDateTime requested) throws Exception {
            final int year =  requested.get(ChronoField.YEAR);
            final int day  =  requested.get(ChronoField.DAY_OF_YEAR);
            final int hour = (requested.get(ChronoField.HOUR_OF_DAY) / timeInterval) * timeInterval;
            final int code = (((year << 9) | day) << 5) | hour;
            if (code != currentDay) {
                currentDay = code;
                final Instant t = requested.truncatedTo(ChronoUnit.HOURS).withHour(hour).toInstant();
                final Path uFile = install(t, year, day, hour, 'U');
                final Path vFile = install(t, year, day, hour, 'V');
                u = new VelocityComponent.MeteoFrance(uFile.toUri());
                v = new VelocityComponent.MeteoFrance(vFile.toUri());
            }
        }

        /**
         * If the given data file does not exist, downloads it.
         */
        private Path install(final Instant time, final int year, final int day, final int hour, final char parameter) throws Exception {
            final String filename = String.format(FILENAME_PATTERN, year, day, hour, parameter);
            Path target = cacheDir.resolve(filename);
            if (!Files.exists(target)) {
                final String source = String.format(DOWNLOAD_PATH, process.configuration().meteoFranceToken,
                                                    parameter, time, time.plus(timeInterval/2, ChronoUnit.HOURS));
                target = download(source, filename);
                if (!"image/tiff".equals(DataStores.probeContentType(target))) {
                    Files.delete(target);                       // May be the XML that describe an exception.
                    throw new IOException("Not a TIFF file.");
                }
            }
            return target;
        }
    }


    // ----------------------------------------------------------------------------------------------------------
    // WindSat data (NetCDF files)
    // ----------------------------------------------------------------------------------------------------------


    /**
     * Open files for wind speed data.
     * Values can be up to 50 m/s in magnitude.
     * Automatic download is not implemented for this task.
     */
    static final class WindSat extends DataSource {
        /**
         * The pattern to the URL of the directory containing the files to download.
         * Parameter is the year (e.g. 2017).
         */
//      private static final String DOWNLOAD_PATH = "ftp://podaac-ftp.jpl.nasa.gov/OceanWinds/windsat/L3/rss/v7/%d/";

        /**
         * Pattern of filename with three parameters, which are the year, the month and the day of month.
         */
        private static final String FILENAME_PATTERN = "windsat_remss_ovw_l3_%04d%02d%02d_v7.0.1.nc";

        /**
         * An encoded value for the current day. Used for checking if we need to load new data.
         */
        private int currentDay;

        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        WindSat(final DriftPredictor process) throws IOException {
            super(process, "WindSat", "glob:*.nc", 24, false);
        }

        /**
         * Loads the prediction file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        void load(final OffsetDateTime requested) throws Exception {
            final int year  = requested.get(ChronoField.YEAR);
            final int month = requested.get(ChronoField.MONTH_OF_YEAR);
            final int day   = requested.get(ChronoField.DAY_OF_MONTH);
            final int code  = (((year << 4) | month) << 5) | day;
            if (code != currentDay) {
                currentDay = code;
                final Path file = cacheDir.resolve(String.format(FILENAME_PATTERN, year, month, day));
                final NetcdfDataset nc = NetcdfDataset.openDataset(file.toString());
                try {
                    u = new VelocityComponent.WindSat(nc, "wind_speed_aw");
                    v = new VelocityComponent.WindSat(nc, "wind_direction");
                } finally {
                    nc.close();
                }
            }
        }
    }


    // ----------------------------------------------------------------------------------------------------------
    // Support methods for above implementations.
    // ----------------------------------------------------------------------------------------------------------


    /**
     * Downloads a file and copies it in the cache directory.
     *
     * @param  source    URL of the file to download.
     * @param  filename  name (without path) of the file to create in the cache directory.
     * @return the local file which has been created.
     */
    final Path download(final String source, final String filename) throws IOException {
        process.progress("Downloading " + filename + " from " + source);
        final Path target = cacheDir.resolve(filename);
        try (InputStream in = new URL(source).openStream()) {
            Files.copy(in, target);
            return target;
        } catch (IOException e) {
            if (Files.exists(target)) {
                Files.delete(target);
            }
            throw e;
        }
    }

    /**
     * Deletes oldest files until the number of remaining files is not greater than
     * {@link Configuration#historyDuration} times the number of files per days.
     */
    final void deleteOldFiles() throws IOException {
        final List<Path> files;
        final PathMatcher pm = cacheDir.getFileSystem().getPathMatcher(filePattern);
        try (Stream<Path> s = Files.walk(cacheDir)) {
            files = s.filter((p) -> pm.matches(p.getFileName())).collect(Collectors.toList());
        }
        files.sort(null);
        int n = process.configuration().historyDuration * 24 / timeInterval;        // Maximal number of files.
        if (componentsInSeparatedFiles) n *= 2;
        n = files.size() - n;
        for (int i=0; i<n; i++) {
            Files.delete(files.get(i));
        }
    }
}
