package org.geotoolkit.data.nmea;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Showcase that provides an example of how to extract messages from a serial port.
 */
public class RawSerialPortUsageTest extends AbstractSerialPortTest {

    @Test
    public void readTest() throws IOException {
        final SerialPort readable = env.readable;
        SerialPortConfig.NMEA_0183.accept(readable);
        if (!readable.openPort()) throw new RuntimeException("Cannot open port");

        Flux<String> messages = readRawMessages(env.readable);

        final StepVerifier verifier = StepVerifier.create(messages)
                .expectNext("hello\r\n")
                .expectNoEvent(Duration.ofMillis(100))
                .thenCancel()
                .verifyLater();

        final SerialPort writeable = env.writeable;
        SerialPortConfig.NMEA_0183.accept(writeable);
        if (!writeable.openPort()) throw new RuntimeException("Cannot open port");
        try {
            final byte[] messageBytes = "hello\r\n".getBytes(US_ASCII);
            try (OutputStream out = writeable.getOutputStream()) {
                out.write(messageBytes);
                out.flush();
            }
        } finally {
            writeable.closePort();
        }

        verifier.verify(Duration.ofSeconds(2));
    }

    /**
     * Debug utility to print messages received by the datasource as they arrive.
     */
    static Flux<String> readRawMessages(final SerialPort datasource) {
        return Flux.using(
                () -> {
                    Assert.assertTrue("Cannot open serial port. Might be busy.", datasource.openPort());
                    return datasource;
                },
                port -> Flux.push(sink -> {
                    if (!port.addDataListener(new RawDataListener(sink))) {
                        sink.error(new RuntimeException("Cannot listen port. It might be busy"));
                    }
                }),
                port -> port.closePort()
        );
    }

    private static class RawDataListener implements SerialPortMessageListener {
        final FluxSink<String> target;

        private RawDataListener(FluxSink<String> target) {
            this.target = target;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return SerialPortConfig.NMEA_MESSAGE_DELIMITER;
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
                final String text = new String(message, US_ASCII);
                target.next(text);
            } catch (RuntimeException e) {
                target.error(e);
            }
        }
    }
}
