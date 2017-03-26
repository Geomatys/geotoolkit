package org.geotoolkit.processing.vector.drift;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

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
     * Open files used for archives. They are the preferred data to use before to fallback on predictions.
     */
    static final class Archive extends DataSource {
        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        Archive(final Path directory) {
            super(directory);
        }

        /**
         * Loads the archive file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        boolean load(final Instant requested) throws Exception {
            return false;    // TODO
        }
    }

    /**
     * Open files used for predictions. Those files are used as a fallback when no archive file
     * is available for the given time.
     */
    static final class RTOFS extends DataSource {
        /**
         * The time between two RTOFS files, in hours.
         */
        private static final int TIME_STEP = 3;

        /**
         * Pattern of RTOFS filename with one parameter, which is the hour as a multiple of {@link #TIME_STEP}.
         */
        private static final String FILENAME_PATTERN = "rtofs_glo_2ds_n%03d_" + TIME_STEP + "hrly_prog.nc";

        /**
         * Day 0 in RTOFS file, as documented in the MT variable.
         */
        private static final Instant EPOCH = Instant.parse("1900-12-31T00:00:00Z");

        /**
         * Creates a new data source.
         *
         * @param path  where to search for the data files.
         */
        RTOFS(final Path directory) {
            super(directory);
        }

        /**
         * Loads the prediction file for the given time.
         * If no data is available, return {@code false}.
         */
        @Override
        boolean load(final Instant requested) throws Exception {
            int hour = (int) ((requested.getEpochSecond() / (60*60)) % 24);
            hour -= hour % TIME_STEP;
            do {
                final Path file = directory.resolve(String.format(FILENAME_PATTERN, hour));
                if (Files.exists(file)) {
                    final NetcdfDataset nf = NetcdfDataset.openDataset(file.toString());
                    try {
                        final Variable mt = nf.findVariable("MT");
                        if (mt != null) {
                            final Instant actual = EPOCH.plusSeconds(Math.round(mt.read().getDouble(0) * (24*60*60)));
                            final Duration delay = Duration.between(actual, requested);
                            if (Math.abs(delay.toHours()) < TIME_STEP) {
                                u = new VelocityComponent.HYCOM(nf, 0, (VelocityComponent.HYCOM) u);
                                v = new VelocityComponent.HYCOM(nf, 1, (VelocityComponent.HYCOM) u);
                                return true;
                            }
                        }
                    } finally {
                        nf.close();
                    }
                }
                hour += 24;
            } while (false);        // TODO: stop condition here.
            return false;
        }
    }
}
