package org.geotoolkit.data.nmea;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.logging.Level;

import org.opengis.feature.Feature;

import net.sf.marineapi.nmea.parser.ParseException;
import net.sf.marineapi.nmea.parser.SentenceFactory;
import net.sf.marineapi.nmea.parser.UnsupportedSentenceException;
import net.sf.marineapi.nmea.sentence.DateSentence;
import net.sf.marineapi.nmea.sentence.DepthSentence;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.PositionSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.nmea.sentence.TimeSentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import reactor.core.publisher.Flux;

import static org.geotoolkit.data.nmea.NMEAStore.ALT_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.DATE_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.DEPTH_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.GEOM_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;
import static org.geotoolkit.data.nmea.NMEAStore.NMEA_TYPE;
import static org.geotoolkit.data.nmea.NMEAStore.SPEED_NAME;
import static org.geotoolkit.data.nmea.NMEAStore.TIME_NAME;

/**
 * Helper class to map NMEA messages into SIS Feature API. The aim is to provide a high-level API capable of providing
 * updates of main GPS information in a consistent way.
 * To reach this goal, we accumulate message information in an internal state, and offer {@link #getSnapshot() to get
 * a view of the current GPS data as a feature} at any time.
 * Each time a message is added, we return a flag to tell if the message modifies spatial or temporal information. See
 * {@link #update(Sentence)} method for more information.
 *
 * <em>NOT thread-safe</em>. That object maintains an internal state, and is designed to work with ordered/sequential
 * data. Please do not re-use an instance across multiple GPS data flows, and do not use in a parallel environment
 * without ensuring element ordering.
 */
class FeatureProcessor {

    private static final double EPSI = 1e-7;

    private static final GeometryFactory GFACTORY = new GeometryFactory();

    private GlobalPositioningState state = new GlobalPositioningState();

    /**
     * Modify internal state to take account of given message data. Message information overrides related data
     * previously set.
     *
     * @param newInfo A new piece of information to use to update current GPS state.
     * @return True if given information updates spatial or temporal information. False otherwise.
     */
    boolean update(final Sentence newInfo) {
        state = new GlobalPositioningState(state, newInfo);
        return state.isSpatialOrTemporalUpdate;
    }

    /**
     *
     * @return A representation of current GPS information in SIS API. Never null, but all attributes of the returned
     * feature are nullable. To ensure that valid space and time information will be returned, please check current GPS
     * state using {{@link #isSpaceTimeSet()}}.
     */
    Feature getSnapshot() {
        return state.toFeature();
    }

    /**
     *
     * @return True if current state contains a valid GPS position and a date/time. False if any of the cited condition
     * is not matched.
     */
    boolean isSpaceTimeSet() {
        return state.isSpaceTimeSet();
    }

    static Optional<Sentence> read(String message) {
        if (SentenceValidator.isValid(message)) {
            try {
                return Optional.of(SentenceFactory.getInstance().createParser(message));
            } catch (UnsupportedSentenceException e) {
                // Marine API is not complete. However, it provides enough sentence types for positioning, so we ignore
                // other ones.
                LOGGER.fine(e.getMessage());
            }
        }

        return Optional.empty();
    }

    /**
     * Creates a flux of features representing GPS information updates. This provides a new Feature each time source GPS
     * updates position or time information.
     *
     * @param rawData The GPS messages to listen to update information and build features.
     * @return A fresh reactive stream.
     */
    static Flux<Feature> emit(Flux<Sentence> rawData) {
        return rawData.scanWith(() -> new GlobalPositioningState(), GlobalPositioningState::new)
            .filter(state -> state.isSpaceTimeSet() && state.isSpatialOrTemporalUpdate)
            .map(GlobalPositioningState::toFeature);
    }

    private static OffsetTime convert(final Time marineTime) {
        final int seconds = (int) marineTime.getSeconds();
        final int nanoSeconds = (int) ((marineTime.getSeconds() - seconds) * 1e9);
        final ZoneOffset timeOffset = ZoneOffset.ofHoursMinutes(marineTime.getOffsetHours(), marineTime.getOffsetMinutes());
        return OffsetTime.of(marineTime.getHour(), marineTime.getMinutes(), seconds, nanoSeconds, timeOffset);
    }

    private static boolean eq(double d1, double d2) {
        return Double.doubleToLongBits(d1) == Double.doubleToLongBits(d2) || Math.abs(d1 - d2) < EPSI;
    }

    private static OptionalDouble getAltitude(final Sentence source) {
        if (source instanceof GGASentence) {
            try {
                return OptionalDouble.of(((GGASentence) source).getAltitude());
            } catch (NullPointerException | ParseException e) {
                LOGGER.log(Level.FINE, "Cannot decode altitude", e);
            }
        }
        return OptionalDouble.empty();
    }

    private static class GlobalPositioningState {
        private static final int LON = 0;
        private static final int LAT = 1;
        private static final int ALT = 2;
        private static final int DEPTH = 3;
        private static final int SPEED = 4;
        private static final int DATE = 5;
        private static final int TIME = 6;
        private static final int ZONE = 7;

        private static final int ENCODED_SIZE = 8;

        final boolean isSpatialOrTemporalUpdate;

        private final double[] encodedInfo;

        GlobalPositioningState() {
            encodedInfo = new double[ENCODED_SIZE];
            Arrays.fill(encodedInfo, Double.NaN);
            isSpatialOrTemporalUpdate = false;
        }

        private GlobalPositioningState(final GlobalPositioningState source, final Sentence newInfo) {
            final double[] value = Arrays.copyOf(source.encodedInfo, ENCODED_SIZE);
            boolean spaceTimeUpdate = false;
            if (newInfo instanceof PositionSentence) {
                // Note: Not all NMEA sentences provide elevation information. To avoid wrong updates, we only use
                // altitude on compatible sentences.
                final Position p = ((PositionSentence) newInfo).getPosition();
                final double lon = p.getLongitude();
                final double lat = p.getLatitude();
                if (!(eq(lon, value[LON]) && eq(lat, value[LAT]))) {
                    spaceTimeUpdate = true;
                    value[LON] = lon;
                    value[LAT] = lat;
                }
            }
            final OptionalDouble altitude = getAltitude(newInfo);
            if (altitude.isPresent()) {
                final double alt = altitude.getAsDouble();
                if (!eq(alt, value[ALT])) {
                    spaceTimeUpdate = true;
                    value[ALT] = alt;
                }
            }
            if (newInfo instanceof DepthSentence) {
                value[DEPTH] = ((DepthSentence) newInfo).getDepth();
            }
            if (newInfo instanceof RMCSentence) {
                value[SPEED] = ((RMCSentence) newInfo).getSpeed();
            }
            if (newInfo instanceof DateSentence) {
                final Date date = ((DateSentence) newInfo).getDate();
                final long days = LocalDate.of(date.getYear(), date.getMonth(), date.getDay()).toEpochDay();
                if (!eq(value[DATE], days)) {
                    spaceTimeUpdate = true;
                    value[DATE] = days;
                }
            }
            if (newInfo instanceof TimeSentence) {
                final OffsetTime time = convert(((TimeSentence) newInfo).getTime());
                final long nano = time.toLocalTime().toNanoOfDay();
                final int offset = time.getOffset().getTotalSeconds();
                if (!(eq(value[TIME], nano) && eq(value[ZONE], offset))) {
                    spaceTimeUpdate = true;
                    value[TIME] = nano;
                    value[ZONE] = offset;
                }
            }

            encodedInfo = value;
            isSpatialOrTemporalUpdate = spaceTimeUpdate;
        }

        boolean isPositionSet() {
            return Double.isFinite(encodedInfo[LON])
                    && Double.isFinite(encodedInfo[LAT]);
        }

        boolean isSpaceTimeSet() {
            return isPositionSet()
                    && Double.isFinite(encodedInfo[DATE])
                    && Double.isFinite(encodedInfo[TIME]);
        }

        private Optional<Point> getLocation() {
            if (isPositionSet()) {
                return Optional.of(GFACTORY.createPoint(new Coordinate(encodedInfo[LON], encodedInfo[LAT], encodedInfo[ALT])));
            } else return Optional.empty();
        }

        private Optional<LocalDate> getDate() {
            if (Double.isFinite(encodedInfo[DATE])) {
                return Optional.of(LocalDate.ofEpochDay((long) encodedInfo[DATE]));
            } else return Optional.empty();
        }

        private Optional<OffsetTime> getTime() {
            if (Double.isFinite(encodedInfo[TIME])) {
                return Optional.of(OffsetTime.of(
                        LocalTime.ofNanoOfDay((long)encodedInfo[TIME]),
                        Double.isFinite(encodedInfo[ZONE])? ZoneOffset.ofTotalSeconds((int)encodedInfo[ZONE]) : ZoneOffset.UTC
                ));
            }
            return Optional.empty();
        }

        Feature toFeature() {
            final Feature f = NMEA_TYPE.newInstance();
            getLocation().ifPresent(l -> f.setPropertyValue(GEOM_NAME.toString(), l));
            getDate().ifPresent(d -> f.setPropertyValue(DATE_NAME.toString(), d));
            getTime().ifPresent(t -> f.setPropertyValue(TIME_NAME.toString(), t));

            if (Double.isFinite(encodedInfo[ALT])) f.setPropertyValue(ALT_NAME.toString(), encodedInfo[ALT]);
            if (Double.isFinite(encodedInfo[SPEED])) f.setPropertyValue(SPEED_NAME.toString(), encodedInfo[SPEED]);
            if (Double.isFinite(encodedInfo[DEPTH])) f.setPropertyValue(DEPTH_NAME.toString(), encodedInfo[DEPTH]);

            return f;
        }
    }
}