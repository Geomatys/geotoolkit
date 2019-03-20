/*
 *     (C) 2019, Geomatys
 */
package org.geotoolkit.processing.science.drift.v2;

import java.time.Instant;
import java.util.Optional;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 * A simple aggregator to rely on for data source evaluation. We currently use only wind and current as drift factors.
 *
 * TODO : use a more generic approach using a list of factors applicable on a drifting object.
 *
 * @author Alexis Manin (Geomatys)
 */
public interface MeteoDataset {

    UVSource getWind();

    UVSource getCurrent();

    default Optional<TimeSet> setOrigin(final DirectPosition origin) {
        return getCurrent().atOrigin(origin)
                .flatMap(current -> getWind()
                            .atOrigin(origin)
                            .map(wind -> new TimeSet(current, wind))
                );
    }

    public static class TimeSet {
        final UVSource.TimeSet current;
        final UVSource.TimeSet wind;

        public TimeSet(UVSource.TimeSet current, UVSource.TimeSet wind) {
            this.current = current;
            this.wind = wind;
        }

        Optional<Calibration2D> setTime(final Instant time) {
            return current.setTime(time)
                    .flatMap(current -> wind.setTime(time).map(wind -> new Calibration2D(current, wind)));
        }
    }

    public static class Calibration2D {
        final UVSource.Calibration2D current;
        final UVSource.Calibration2D wind;

        public Calibration2D(UVSource.Calibration2D current, UVSource.Calibration2D wind) {
            this.current = current;
            this.wind = wind;
        }

        Snapshot setHorizontalComponent(final Envelope target) {
            return new Snapshot(current.setHorizontalComponent(target), wind.setHorizontalComponent(target));
        }
    }

    public static class Snapshot {
        final UVSource.Snapshot current;
        final UVSource.Snapshot wind;

        public Snapshot(UVSource.Snapshot current, UVSource.Snapshot wind) {
            this.current = current;
            this.wind = wind;
        }
    }
}
