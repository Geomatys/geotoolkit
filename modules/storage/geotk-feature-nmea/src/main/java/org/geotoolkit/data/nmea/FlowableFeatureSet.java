package org.geotoolkit.data.nmea;

import java.util.stream.Stream;

import org.opengis.feature.Feature;

import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;

import reactor.core.publisher.Flux;

/**
 * TODO: find a way to provide information about the flow type (hot, cold, long-running, etc.).
 */
public interface FlowableFeatureSet extends FeatureSet {

    /**
     * TODO: replace Reactor return type with standard Java 9 Flow.
     *
     * @return An asynchronous stream of features. Never null, but no requirements are enforced about its nature: Both
     * hot and cold streams are accepted, and there's no completion requirement (i.e: long-running/infinite datasets are
     * OK).
     */
    Flux<Feature> flow();

    @Override
    default Stream<Feature> features(boolean b) throws DataStoreException {
        return flow().toStream();
    }
}
