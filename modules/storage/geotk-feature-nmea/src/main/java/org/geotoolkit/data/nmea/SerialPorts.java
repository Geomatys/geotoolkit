package org.geotoolkit.data.nmea;

import java.nio.charset.StandardCharsets;

import org.apache.sis.util.iso.Names;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import net.sf.marineapi.nmea.sentence.Sentence;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;

class SerialPorts implements Discovery {

    @Override
    public Flux<FluxFeatureSet> discover() {
        final SerialPort[] ports = SerialPort.getCommPorts();
        LOGGER.fine(() -> String.format("FAZELCAST found %d ports", ports.length));
        return Flux.fromArray(ports)
                .flatMap(this::checkPort)
                .map(this::features);
    }

    private Mono<SerialPort> checkPort(final SerialPort port) {
        LOGGER.fine(() -> "Trying port: "+port.getDescriptivePortName());
        configure(port);
        if (!port.openPort()) {
            LOGGER.fine("Cannot open port");
        }

        return Flux.<Sentence>create(sink -> new NMEAListener(port, sink)).next().map(onceConfirmed -> port);
    }

    private FluxFeatureSet features(final SerialPort port) {
        final Flux<Sentence> sentenceFlux = Flux.create(sink -> new NMEAListener(port, sink));
        return new FluxFeatureSet(new FeatureProcessor().emit(sentenceFlux), Names.createLocalName("Serial", ":", port.getDescriptivePortName()));
    }

    /**
     * Configure port to read NMEA from. Based on details provided
     * <a href="https://en.wikipedia.org/wiki/NMEA_0183#Serial_configuration_(data_link_layer)">in this article</a>.
     * @param port The serial port to check. We'll try to configure then open it.
     */
    private void configure(SerialPort port) {
        port.setComPortParameters(4800, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    }

    private class NMEAListener implements SerialPortMessageListener {

        final FluxSink<Sentence> target;

        private NMEAListener(final SerialPort source, FluxSink<Sentence> target) {
            this.target = target;
            source.addDataListener(this);
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
    }
}
