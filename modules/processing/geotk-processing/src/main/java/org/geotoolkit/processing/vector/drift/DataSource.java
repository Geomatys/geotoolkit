package org.geotoolkit.processing.vector.drift;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    abstract boolean load(Instant requested) throws Exception;

    /**
     * Open files for current data.
     */
    static final class HYCOM extends DataSource {
        /**
         * Pattern of RTOFS filename with one parameter, which is the day.
         */
        private static final String FILENAME_PATTERN = "archv.2015_%03d_00_3z%c.nc";

        /**
         * The current day. Used for checking if we need to load new data.
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
        boolean load(final Instant requested) throws Exception {
            final int day = OffsetDateTime.ofInstant(requested, ZoneOffset.UTC).get(ChronoField.DAY_OF_YEAR);
            if (day != currentDay) {
                currentDay = day;
                final Path uFile = directory.resolve(String.format(FILENAME_PATTERN, day, 'u'));
                final Path vFile = directory.resolve(String.format(FILENAME_PATTERN, day, 'v'));
                if (!Files.exists(uFile) || !Files.exists(vFile)) {
                    return false;
                }
                NetcdfDataset nc = NetcdfDataset.openDataset(uFile.toString());
                try {
                    u = new VelocityComponent.HYCOM(nc, "u", (VelocityComponent.HYCOM) u);
                } finally {
                    nc.close();
                }
                nc = NetcdfDataset.openDataset(vFile.toString());
                try {
                    v = new VelocityComponent.HYCOM(nc, "v", (VelocityComponent.HYCOM) u);  // Really 'u', not 'v'.
                } finally {
                    nc.close();
                }
            }
            return true;
        }
    }
}
