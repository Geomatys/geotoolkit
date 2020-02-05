package org.geotoolkit.data.nmea;

import reactor.core.publisher.Flux;

/**
 * Experimental API for discovery of communication channels. A discovery driver can give back as many communication
 * channels as wanted through {@link #discover()}. The aim is to provide independant channels.
 */
public interface Discovery {

    /**
     *
     * @return Asynchronously provide communication channels as they are discovered. There's no requirement about
     * completion, so your driver can make one-shot analysis then complete returned flux, or provide a long-running one
     * that is capable or returning newly discovered channels after some time.
     */
    Flux<FlowableFeatureSet> discover();

    /**
     *
     * @return A default discovery mechanism for serial ports.
     */
    static Discovery serial() {
        return new SerialPorts();
    }
}
