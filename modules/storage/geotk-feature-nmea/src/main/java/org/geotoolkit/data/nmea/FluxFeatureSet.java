package org.geotoolkit.data.nmea;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;

import org.locationtech.jts.geom.Point;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * TODO:
 * <ul>
 *     <li>This could be generalized for any "hot" feature datasource.</li>
 *     <li>Check if it is required to close flux and dispose subscribers.</li>
 *     <li>Test if envelope access is safe through parameterized scheduler.</li>
 *     <li>Improve metadata build.</li>
 * </ul>
 */
public class FluxFeatureSet implements FeatureSet {

    public static final Scheduler ENVELOPE_SCHEDULER = Schedulers.newSingle("envelope");

    final Flux<Feature> datasource;
    private final EnvelopeSubscriber envelopeComputer;

    private final GenericName name;

    public FluxFeatureSet(final Flux<Feature> datasource) {
        this(datasource, null);
    }

    public FluxFeatureSet(final Flux<Feature> datasource, final GenericName name) {
        this.datasource = datasource;
        envelopeComputer = new EnvelopeSubscriber();
        this.datasource
                .publishOn(ENVELOPE_SCHEDULER)
                .subscribe(envelopeComputer);
        this.name = name;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return NMEAStore.NMEA_TYPE;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return datasource.toStream();
    }

    public Flux<Feature> flux() {
        return datasource;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Mono.fromSupplier(() -> envelopeComputer.getSnapshot())
                .subscribeOn(ENVELOPE_SCHEDULER)
                .block(Duration.ofMillis(20));
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(name);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        // TODO: fill, factorize with NMEAStore, and keep in cache.
        final DefaultMetadata meta = new DefaultMetadata();
        return meta;
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        throw new UnsupportedOperationException("Not supported yet"); // "Alexis Manin (Geomatys)" on 24/01/2020
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        throw new UnsupportedOperationException("Not supported yet"); // "Alexis Manin (Geomatys)" on 24/01/2020
    }

    private static class EnvelopeSubscriber extends BaseSubscriber<Feature> {

        GeneralEnvelope input = null;

        @Override
        protected void hookOnNext(Feature value) {
            getPosition(value).ifPresent(this::updateEnvelope);
        }

        Optional<Envelope> getSnapshot() {
            if (input == null) {
                return Optional.empty();
            }

            return Optional.of(new ImmutableEnvelope(input));
        }

        private void updateEnvelope(final DirectPosition newPoint) {
            if (input == null) {
                input = new GeneralEnvelope(newPoint, newPoint);
            } else input.add(newPoint);
        }
    }

    private static Optional<DirectPosition> getPosition(final Feature nmeaFeature) {
        if (nmeaFeature == null) return Optional.empty();
        final Object geometry = nmeaFeature.getPropertyValue(NMEAStore.GEOM_NAME.tip().toString());
        if (geometry instanceof Point) {
            final Point pt = (Point)geometry;
            return Optional.of(new DirectPosition2D(CommonCRS.WGS84.normalizedGeographic(), pt.getX(), pt.getY()));
        }

        return Optional.empty();
    }
}
