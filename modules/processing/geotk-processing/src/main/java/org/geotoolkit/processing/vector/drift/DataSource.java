package org.geotoolkit.processing.vector.drift;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import ucar.nc2.dataset.NetcdfDataset;


/**
 * Loads data of some specific format.
 * Sources:
 * <ul>
 *   <li>Current:    <a href="ftp://ftp.hycom.org/datasets/GLBa0.08/expt_91.1/2015">HYCOM FTP access</a></li>
 *   <li>Wind speed: <a href="ftp://podaac-ftp.jpl.nasa.gov/OceanWinds/windsat/L3/rss/v7/2015">NOAA FTP access</a></li>
 * </ul>
 */
abstract class DataSource {
    /**
     * Where to search for the data files.
     */
    final Path directory;

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
     * @param path  where to search for the data files.
     */
    DataSource(final Path directory) {
        this.directory = directory;
    }

    /**
     * Loads the archive (if available) or prediction (as a fallback) file for the given time.
     * If no data is available, return {@code false}.
     */
    abstract boolean load(OffsetDateTime requested) throws Exception;

    /**
     * Open files for current data.
     * Values are usually less than 1 m/s in magnitude.
     */
    static final class HYCOM extends DataSource {
        /**
         * Pattern of RTOFS filename with three parameters, which are the year, the day of year and the variable name.
         */
        private static final String FILENAME_PATTERN = "archv.%04d_%03d_00_3z%c.nc";

        /**
         * An encoded value for the current day. Used for checking if we need to load new data.
         */
        private int currentDay;

        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        HYCOM(final Path directory) {
            super(directory);
        }

        /**
         * Loads the prediction file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        boolean load(final OffsetDateTime requested) throws Exception {
            final int year = requested.get(ChronoField.YEAR);
            final int day  = requested.get(ChronoField.DAY_OF_YEAR);
            final int code = (year << 7) | day;
            if (code != currentDay) {
                currentDay = code;
                final Path uFile = directory.resolve(String.format(FILENAME_PATTERN, year, day, 'u'));
                final Path vFile = directory.resolve(String.format(FILENAME_PATTERN, year, day, 'v'));
                if (!Files.exists(uFile) || !Files.exists(vFile)) {
                    return false;
                }
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
            return true;
        }
    }

    /**
     * Open files for wind speed data.
     * Values can be up to 50 m/s in magnitude.
     */
    static final class WindSat extends DataSource {
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
        WindSat(final Path directory) {
            super(directory);
        }

        /**
         * Loads the prediction file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        boolean load(final OffsetDateTime requested) throws Exception {
            final int year  = requested.get(ChronoField.YEAR);
            final int month = requested.get(ChronoField.MONTH_OF_YEAR);
            final int day   = requested.get(ChronoField.DAY_OF_MONTH);
            final int code  = (((year << 4) | month) << 5) | day;
            if (code != currentDay) {
                currentDay = code;
                final Path file = directory.resolve(String.format(FILENAME_PATTERN, year, month, day));
                if (!Files.exists(file)) {
                    return false;
                }
                NetcdfDataset nc = NetcdfDataset.openDataset(file.toString());
                try {
                    u = new VelocityComponent.WindSat(nc, "wind_speed_aw");
                    v = new VelocityComponent.WindSat(nc, "wind_direction");
                } finally {
                    nc.close();
                }
            }
            return true;
        }
    }
}
