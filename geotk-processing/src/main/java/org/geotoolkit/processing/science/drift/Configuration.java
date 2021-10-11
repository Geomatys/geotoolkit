package org.geotoolkit.processing.science.drift;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.process.ProcessException;

final class Configuration {
    /**
     * The directory of the configuration file.
     * Also the root directory where to search for data.
     */
    final Path directory;

    /**
     * URL to the HYCOM FTP directory.
     */
    final String hycom_url;

    /**
     * Cache size in numbers of days. This will be converted in number of files using the number of files per day
     * (this factor is different for each file format).
     */
    final int historyDuration;

    /**
     * The Météo-France token for access to WCS service, or {@code null} if none.
     */
    final String meteoFranceToken;

    /**
     * Weights to apply on wind vectors relative to current vectors.
     */
    final DriftPredictor.Weight[] weights;

    /**
     * Maximum number of trajectories to track;
     */
    final int maximumTrajectoryCount;

    /**
     * Output grid size.
     */
    final int gridWidth, gridHeight;

    /**
     * Size of a grid cell, in metres.
     */
    final int gridResolution;

    /**
     * Time elapsed between two steps, in seconds.
     */
    final long timeStep;


    Configuration(final Path directory) throws IOException, ProcessException {
        this.directory       = directory;
        String hycom_url     = DataSource.HYCOM.HYCOM_URL;
        String token         = null;
        int  count           = 2_000_000;         // Default value.
        int  historyDuration = 10;                // Default value (in days).
        int  gridWidth       = 1000;
        int  gridHeight      = 1000;
        int  gridResolution  = 1000;
        long timeStep        = 6 * 60*60;
        final List<DriftPredictor.Weight> wg = new ArrayList<>();
        for (String line : Files.readAllLines(directory.resolve("config.txt"))) {
            if (!(line = line.trim()).isEmpty() && !line.startsWith("#")) {
                final int s = line.indexOf('=');
                if (s < 0) {
                    throw new ProcessException("Not a key-value pair: " + line, null);
                }
                final String keyword = line.substring(0, s).trim();
                final CharSequence[] values = CharSequences.split(line.substring(s+1).trim(), ',');
                switch (keyword.toLowerCase()) {
                    case "weights": {
                        if (values.length != 3) {
                            throw new ProcessException(keyword + " shall have exactly three values.", null);
                        }
                        wg.add(new DriftPredictor.Weight(
                                Double.parseDouble(values[0].toString()),
                                Double.parseDouble(values[1].toString()),
                                Double.parseDouble(values[2].toString())));
                        break;
                    }
                    case "hycom_url": {
                        if (values.length != 1) {
                            throw new ProcessException(keyword + " shall have exactly one value.", null);
                        }
                        hycom_url = values[0].toString();
                        break;
                    }
                    case "meteo-france_token": {
                        if (values.length != 1) {
                            throw new ProcessException(keyword + " shall have exactly one value.", null);
                        }
                        token = values[0].toString();
                        break;
                    }
                    case "maximum_trajectory_count": {
                        count = singleton(keyword, values);
                        break;
                    }
                    case "history_duration": {
                        historyDuration = singleton(keyword, values);
                        break;
                    }
                    case "grid_width": {
                        gridWidth = singleton(keyword, values);
                        break;
                    }
                    case "grid_height": {
                        gridHeight = singleton(keyword, values);
                        break;
                    }
                    case "grid_resolution": {
                        gridResolution = singleton(keyword, values);
                        break;
                    }
                    case "time_step": {
                        timeStep = singleton(keyword, values) * (60*60L);
                        break;
                    }
                    default: {
                        throw new ProcessException("Unknown property: " + keyword, null);
                    }
                }
            }
        }
        weights = wg.toArray(new DriftPredictor.Weight[wg.size()]);
        Arrays.sort(weights);
        this.hycom_url              = hycom_url;
        this.historyDuration        = historyDuration;
        this.meteoFranceToken       = token;
        this.maximumTrajectoryCount = count;
        this.gridWidth              = gridWidth;
        this.gridHeight             = gridHeight;
        this.gridResolution         = gridResolution;
        this.timeStep               = timeStep;
    }

    private static int singleton(final String keyword, final CharSequence[] values) throws ProcessException {
        if (values.length != 1) {
            throw new ProcessException(keyword + " shall have exactly one value.", null);
        }
        return Integer.parseInt(values[0].toString());
    }
}
