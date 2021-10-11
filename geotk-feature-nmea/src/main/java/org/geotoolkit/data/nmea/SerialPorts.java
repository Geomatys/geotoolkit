package org.geotoolkit.data.nmea;

import java.util.function.Consumer;
import java.util.logging.Level;

import com.fazecast.jSerialComm.SerialPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;
import static org.geotoolkit.data.nmea.SerialPortConfig.NMEA_0183;

/**
 * @author Alexis Manin (Geomatys)
 */
class SerialPorts implements Discovery<SerialPort> {

    static final String NAMESPACE = "Serial";

    private final Consumer<SerialPort> configurer;

    SerialPorts() {
        this(NMEA_0183);
    }

    SerialPorts(final Consumer<SerialPort> configurer) {
        this.configurer = configurer == null ? NMEA_0183 : configurer;
    }

    @Override
    public Flux<FlowableFeatureSet> discover() {
        final SerialPort[] ports = SerialPort.getCommPorts();
        LOGGER.fine(() -> String.format("Found %d serial ports", ports.length));

        return Flux.just(ports)
                .flatMap(this::connect);
    }

    @Override
    public Mono<FlowableFeatureSet> connect(SerialPort port) {
        ensureNonNull("Serial port", port);
        return Mono.fromCallable(() -> SerialPortContext.open(port, configurer))
                .flatMap(ctx -> discardAndCloseIfNoSignal(ctx))
                .map(ctx -> new SerialPortFeatureSet(ctx));
    }

    private Mono<SerialPortContext> discardAndCloseIfNoSignal(final SerialPortContext context) {
        return context.flux()
                .hasElements()
                .doOnCancel(context::close)
                .onErrorResume(Exception.class, error -> {
                    LOGGER.log(Level.WARNING, "Failed connecting to port", error);
                    return Mono.just(false);
                })
                .doOnNext(result -> { if (result == false) context.close(); })
                .thenReturn(context);
    }
}
