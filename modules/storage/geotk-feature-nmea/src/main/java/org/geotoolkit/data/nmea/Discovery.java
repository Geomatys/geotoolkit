/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2021, Geomatys
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

import com.fazecast.jSerialComm.SerialPort;
import java.util.function.Consumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Experimental API for discovery of communication channels. A discovery driver can give back as many communication
 * channels as wanted through {@link #discover()}. The aim is to provide independant channels.
 */
public interface Discovery<T> {

    /**
     * Give a live-stream of available GPS channels. Be aware that underlying provider can return a flow that stay
     * opened for a long time, even when no signal is emitted from source. The aim is to allow to detect a future
     * emission through a communication channel. If an application wants a short-running detection script, it is
     * recommended for it to add its own timeout and retry policy over the channel returned here.
     * <p>
     * Examples:
     * <ul>
     *     <li>To abort detection if nothing happens for 5 seconds, do:
     *         <code>discover().timeout(Duration.ofSeconds(5))</code></li>
     *     <li>Alternatively, a slightly different type of timeout can be setup, if you want a hard limit of 5 seconds
     *         (meaning, stop detection in 5 seconds): <code>discover().take(Duration.ofSeconds(5))</code></li>
     *     <li>Above detection limits can be combined with a retry policy, to start detection again after some time:
     *         <pre>
     *             final RetryBackoffSpec retryEveryMinutesFor10Minutes = Retry.fixedDelay(10, Duration.ofMinutes(1));
     *             discover()
     *                 .switchIfEmpty(Mono.error(new RuntimeException("No channel detected")))
     *                 .retryWhen(retryEveryMinutesFor10Minutes);
     *         </pre>
     *     </li>
     * </ul>
     * <p>
     * IMPORTANT: it is the user responsibility to close any returned feature set. Note that, if it would be assured
     * that feature sets from the result flow would not be extracted from it (using block() call, for example), we could
     * intern that responsibility. However, as we cannot assume such behavior, the task falls back to the user.
     * If you do not plan to extract the feature sets from reactive stream context, you can then simply chain the call
     * to discover with a call to {@link Flux#using}, like:
     * <code>
     *     discover().flatMap(dataset -> Flux.using(() -> dataset, FlowableFeatureSet::flow, FlowableFeatureSet::close))
     * </code>.
     *
     * Note: in the future, usage of a reactive pool of resource could fix that responsibility issue. Example:
     * <a href="https://github.com/reactor/reactor-pool">Spring Reactor pool</a>.
     *
     * @return Asynchronously provide communication channels as they are discovered. There's no requirement about
     * completion, so your driver can make one-shot analysis then complete returned flux, or provide a long-running one
     * that is capable or returning newly discovered channels after some time.
     */
    Flux<FlowableFeatureSet> discover();

    Mono<FlowableFeatureSet> connect(final T datasource);

    /**
     *
     * @return A default discovery mechanism for serial ports configured through {@link SerialPortConfig#NMEA_0183}.
     */
    static Discovery<SerialPort> serial() {
        return new SerialPorts();
    }

    /**
     *
     * @param configurer A component that mutates input serial port to configure it according to its needs. Can be a
     *                   {@link SerialPortConfig serial port configuration object}. If null, default
     *                   {@link SerialPortConfig#NMEA_0183} configuration is used.
     * @return The discovery service associated with the given configuration, never null.
     */
    static Discovery<SerialPort> serial(Configuration<SerialPort> configurer) {
        return new SerialPorts(configurer);
    }

    /**
     * Marker interface to denote a custom datasource configuration.
     * The only current implementation is {@link SerialPortConfig}.
     * In the future, a <pre>TcpConfiguration</pre> or <pre>FileConfiguration</pre> could be added.
     */
    interface Configuration<T> extends Consumer<T> {}
}
