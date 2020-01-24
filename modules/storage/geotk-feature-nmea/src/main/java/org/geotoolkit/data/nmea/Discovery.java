package org.geotoolkit.data.nmea;

import reactor.core.publisher.Flux;

/**
 * Experimental API for discovery of communication channels.
 * TODO: configure cache
 */
public interface Discovery {

    Flux<FluxFeatureSet> discover();

    static Discovery serial() {
        return new SerialPorts();
    }
}
