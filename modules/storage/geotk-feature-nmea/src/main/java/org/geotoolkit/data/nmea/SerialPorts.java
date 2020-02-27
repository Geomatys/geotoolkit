package org.geotoolkit.data.nmea;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.opengis.util.LocalName;

import org.apache.sis.util.iso.Names;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import net.sf.marineapi.nmea.sentence.Sentence;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.publisher.SignalType;

import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;

class SerialPorts implements Discovery {

    public static final String NAMESPACE = "Serial";

    private static SerialPorts INSTANCE;

    static synchronized Discovery getOrCreate() {
        if (INSTANCE == null) {
            INSTANCE = new SerialPorts();
        }
        return INSTANCE;
    }

    private List<NMEAListener> listeners;

    private SerialPorts() {
        final SerialPort[] ports = SerialPort.getCommPorts();
        if (ports.length < 1) {
            throw new IllegalStateException("No serial port detected");
        }
        LOGGER.fine(() -> String.format("Found %d serial ports", ports.length));

        final List<NMEAListener> listeners = Arrays.stream(ports)
                .map(this::startListen)
                .collect(Collectors.toList());

        this.listeners = Collections.unmodifiableList(listeners);
    }

    @Override
    public Flux<FlowableFeatureSet> discover() {
        final SerialPort[] ports = SerialPort.getCommPorts();
        LOGGER.fine(() -> String.format("Found %d serial ports", ports.length));
        return Flux.fromIterable(listeners)
                .filterWhen(this::sentenceReceived)
                .map(this::publish);
    }

    private NMEAListener startListen(SerialPort source) {
        return new NMEAListener(source);
    }

    private Mono<Boolean> sentenceReceived(final NMEAListener source) {
        LOGGER.fine(() -> "Trying port: "+source.source.getDescriptivePortName());
        return source.output
                .hasElements()
                .doOnError(error -> LOGGER.log(Level.FINE, "Failed connecting to port", error))
                .onErrorReturn(Exception.class, false);
    }

    private FlowableFeatureSet publish(final NMEAListener source) {
        final LocalName portName = Names.createLocalName(NAMESPACE, ":", source.source.getDescriptivePortName());
        return new FluxFeatureSet(FeatureProcessor.emit(source.output), portName);
    }

    /**
     * Configure port to read NMEA from. Based on details provided
     * <a href="https://en.wikipedia.org/wiki/NMEA_0183#Serial_configuration_(data_link_layer)">in this article</a>.
     * @param port The serial port to configure.
     */
    private void configure(SerialPort port) {
        port.setComPortParameters(4800, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    private class NMEAListener implements SerialPortMessageListener, Disposable {

        private final SerialPort source;
        private final ReplayProcessor<Sentence> output;
        private final FluxSink<Sentence> sink;

        private NMEAListener(final SerialPort source) {
            this.source = source;
            configure(source);
            if (!source.openPort()) {
                throw new IllegalArgumentException(String.format("Cannot connect to input port [%s]", source.getDescriptivePortName()));
            }
            if (!source.addDataListener(this)) {
                throw new IllegalArgumentException(String.format("Input port [%s] is already bound", source.getDescriptivePortName()));

            }

            output = ReplayProcessor.cacheLast();
            output.doFinally(this::close);
            sink = output.sink(FluxSink.OverflowStrategy.LATEST);
        }

        @Override
        public byte[] getMessageDelimiter() {
            return Sentence.TERMINATOR.getBytes(StandardCharsets.US_ASCII);
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            try {
                final byte[] message = event.getReceivedData();
                if (message == null || message.length < 1) return;
                final String text = new String(message, StandardCharsets.US_ASCII);
                // Trim is very important for messages with a checksum at the end.
                FeatureProcessor.read(text.trim()).ifPresent(sink::next);
            } catch (RuntimeException e) {
                sink.error(e);
            }
        }

        private void close(final SignalType termination) {
            LOGGER.fine("Terminated by "+termination);
            source.removeDataListener();
            // TODO: should we close port ?
        }

        @Override
        public void dispose() {
            output.dispose();
        }
    }
}
