/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.nmea;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.ALT_NAME;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.DATE_NAME;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.DEPTH_NAME;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.GEOM_NAME;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.SPEED_NAME;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.DateSentence;
import net.sf.marineapi.nmea.sentence.DepthSentence;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GLLSentence;
import net.sf.marineapi.nmea.sentence.PositionSentence;
import net.sf.marineapi.nmea.sentence.RMCSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.sentence.TimeSentence;
import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.Time;
import net.sf.marineapi.provider.event.ProviderEvent;
import net.sf.marineapi.provider.event.ProviderListener;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureUtilities;

/**
 * Create features of type {@link NMEAFeatureStore#NMEA_TYPE} from
 * {@link Sentence} read by a {@link SentenceReader} or given by the user.
 *
 * This class implements {@link SentenceListener} interface, what allows you to
 * attach a NMEABuilder to a reader. You can instanciate NMEABuilder by giving
 * it the reader, in which case listener is attached / released automatically.
 * You can also call the default constructor, and manage the reader on your
 * side. When the builder has enough data to build a feature, a
 * {@link FeatureEvent} is propagated to all its {@link ProviderListener}, which
 * allow them to get the built feature.
 *
 * To allow user reading data without using any listener, there's a function
 * {@link NMEABuilder#readSentence(net.sf.marineapi.nmea.sentence.Sentence)}.
 * With that function, you can give directly your sentences to the builder, and
 * it will give you back a boolean indicating if there's sufficient information
 * to create a valid point. If the method return true, you can get the built
 * feature using {@link NMEABuilder#next()}.
 *
 * When a feature is created, the sentences already given are deleted, and will
 * not involve in next feature generation.
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEABuilder implements SentenceListener {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data.nmea");
    private static final GeometryFactory GFACTORY = new GeometryFactory();

    private final SentenceReader sReader;
    private final HashMap<String, Sentence> cache = new HashMap<>();
    private final EventListenerList listeners = new EventListenerList();

    private Feature next = null;

    public NMEABuilder() {
        this(null);
    }

    public NMEABuilder(final SentenceReader reader) {
        sReader = reader;
        if (sReader != null) {
            sReader.addSentenceListener(this);
        }
    }

    public boolean readSentence(Sentence s) {

        // If the current sentence is already stored and we've got a position data,
        // We must have all needed information to build gps data.
        if (cache.containsKey(s.getSentenceId())
                && (cache.containsKey(SentenceId.GGA.name())
                || cache.containsKey(SentenceId.GLL.name())
                || cache.containsKey(SentenceId.RMC.name()))) {

            buildFeature();
            reset();
            if (next != null) {
                final FeatureEvent evt = new FeatureEvent(this, next);
                fireFeatureEvent(evt);
            }
        }

        cache.put(s.getSentenceId(), s);

        return next != null;
    }

    /**
     * Clears the list of collected sentences.
     */
    private void reset() {
        cache.clear();
    }

    /**
     * Call when there is nothing left in the stream, and try to build a last feature.
     *
     * @return
     */
    public boolean endFeature(){
        buildFeature();
        return next != null;
    }

    private void buildFeature() {
        Position position = null;
        Double depth = null;
        Double speed = null;
        Date date = null;
        Time time = null;

        for (Sentence s : cache.values()) {
            try {
                // Check for main geographic information.
                if (s instanceof GGASentence || s instanceof GLLSentence) {
                    position = ((PositionSentence) s).getPosition();
                } // If we can't get geographic position from main source, take it where we can.
                else if (s instanceof PositionSentence && position == null) {
                    position = ((PositionSentence) s).getPosition();
                }

                // Speed and time information
                if (s instanceof RMCSentence) {
                    final RMCSentence rmc = (RMCSentence) s;
                    speed = rmc.getSpeed();
                    date = rmc.getDate();
                    time = rmc.getTime();

                } else {
                    if (s instanceof TimeSentence && time == null) {
                        time = ((TimeSentence) s).getTime();
                    }
                    if (s instanceof DateSentence && date == null) {
                        date = ((DateSentence) s).getDate();
                    }
                }

                if (s instanceof DepthSentence) {
                    depth = ((DepthSentence) s).getDepth();
                }

            } catch (Exception ex) {
                // Do nothing. If a measure failed, it doesn't mean all data failed.
            }
        }

        if (position != null) {
            next = FeatureUtilities.defaultFeature(NMEAFeatureStore.NMEA_TYPE, FeatureUtilities.createDefaultFeatureId());
            final Point geom = GFACTORY.createPoint(new Coordinate(position.getLongitude(), position.getLatitude(), position.getAltitude()));
            next.setPropertyValue(GEOM_NAME.toString(), geom);
            next.setPropertyValue(ALT_NAME.toString(), position.getAltitude());
        } else {
            // We don't get geographic coordinate, so we give up the current feature.
            next = null;
            return;
        }

        if (date != null) {
            // Concatenate measure date with measure time in the day to get java date.
            final java.util.Date trueDate = new java.util.Date(
                    date.toDate().getTime()
                    + ((time == null) ? 0 : time.getMilliseconds()));
            next.setPropertyValue(DATE_NAME.toString(), trueDate);
        }

        if (speed != null) {
            next.setPropertyValue(SPEED_NAME.toString(), speed);
        }

        if (depth != null) {
            next.setPropertyValue(DEPTH_NAME.toString(), depth);
        }

    }

    public Feature next() {
        final Feature tmp = next;
        next = null;
        return tmp;
    }

    @Override
    public void readingPaused() {
        LOGGER.log(Level.INFO, "NMEA Reader paused.");
        reset();
        fireFeatureEvent(null);
    }

    @Override
    public void readingStarted() {
        LOGGER.log(Level.INFO, "NMEA Reader started.");
        reset();
    }

    @Override
    public void readingStopped() {
        LOGGER.log(Level.INFO, "NMEA Reader stopped.");
        reset();
        // TODO : Re-activate listener release ? It have been desactivated because of ConcurrentModificationException.
//        synchronized (sReader) {
//            if (sReader != null) {
//                sReader.removeSentenceListener(this);
//            }
//        }
        fireFeatureEvent(null);
    }

    @Override
    public void sentenceRead(SentenceEvent event) {
        readSentence(event.getSentence());
    }

    /**
     * Register the given listener the list of objects to notice.
     *
     * @param listener Listener to add
     */
    public void addListener(ProviderListener<FeatureEvent> listener) {
        listeners.add(ProviderListener.class, listener);
    }

    /**
     * Remove the specified listener from provider.
     *
     * @param listener Listener to remove
     */
    public void removeListener(ProviderListener<FeatureEvent> listener) {
        listeners.remove(ProviderListener.class, listener);
        LOGGER.log(Level.FINE, "A listener have been removed.");
    }

    /**
     * Notice listeners that a new feature have been created.
     *
     * @param event TPVUpdateEvent to dispatch
     */
    private void fireFeatureEvent(FeatureEvent event) {
        for (ProviderListener listener : listeners.getListeners(ProviderListener.class)) {
            listener.providerUpdate(event);
        }
    }

    /**
     * Event propagated when a feature is built. It contains the created feature,
     * which can be retrieved with {@link FeatureEvent#getData()} method.
     */
    public class FeatureEvent extends ProviderEvent {

        private final Feature data;

        public FeatureEvent(Object source, final Feature f) {
            super(source);
            data = f;
        }

        public Feature getData() {
            return data;
        }
    }
}
