package org.geotoolkit.data.nmea;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.opengis.util.GenericName;
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
import reactor.util.function.Tuple2;

import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;

class SerialPorts implements Discovery {

    public static final String NAMESPACE = "Serial";

    @Override
    public Flux<FlowableFeatureSet> discover() {
        final SerialPort[] ports = SerialPort.getCommPorts();
        LOGGER.fine(() -> String.format("FAZELCAST found %d ports", ports.length));
        return Flux.fromArray(ports)
                .filterWhen(this::sentenceReceived)
                .map(this::publish);
    }


    private Mono<Boolean> sentenceReceived(final SerialPort port) {
        LOGGER.fine(() -> "Trying port: "+port.getDescriptivePortName());
        return Flux.<Sentence>create(sink -> new NMEAListener(port, sink))
                .hasElements()
                .doOnError(error -> LOGGER.log(Level.FINE, "Failed connecting to port", error))
                .onErrorReturn(Exception.class, false);
    }

    /**
     * Create an SIS Feature set capable of providing {@link FlowableFeatureSet#flow() asynchronous data streams}.
     * @param datasource
     * @return
     */
    private FlowableFeatureSet adapt(final Tuple2<GenericName, Flux<Sentence>> datasource) {
        return new FluxFeatureSet(FeatureProcessor.emit(datasource.getT2()), datasource.getT1());
    }

    private FlowableFeatureSet publish(final SerialPort port) {
        final LocalName portName = Names.createLocalName(NAMESPACE, ":", port.getDescriptivePortName());
        final Flux<Sentence> sentences = Flux.<Sentence>create(sink -> new NMEAListener(port, sink))
                // TODO: find a better strategy: serial port accept only a single listener at a time.
                // therefore, We're forced to use upstream as a hot source (note something more powerful should be possible
                // as we've got control over a global state.
                .cache(1000);
        return new FluxFeatureSet(FeatureProcessor.emit(sentences), portName);
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
        private final FluxSink<Sentence> target;

        private NMEAListener(final SerialPort source, FluxSink<Sentence> target) {
            this.source = source;
            this.target = target;

            configure(source);
            if (!source.openPort()) {
                throw new IllegalArgumentException(String.format("Cannot connect to input port [%s]", source.getDescriptivePortName()));
            }
            if (source.addDataListener(this)) {
                target.onDispose(this);
            } else throw new IllegalArgumentException(String.format("Input port [%s] is already bound", source.getDescriptivePortName()));
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
                FeatureProcessor.read(text).ifPresent(target::next);
            } catch (RuntimeException e) {
                target.error(e);
            }
        }

        @Override
        public void dispose() {
            try (
                    Closeable doNotListenAnymore = () -> source.removeDataListener();
                    Closeable closePort = () -> source.closePort()
            ) {} catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
