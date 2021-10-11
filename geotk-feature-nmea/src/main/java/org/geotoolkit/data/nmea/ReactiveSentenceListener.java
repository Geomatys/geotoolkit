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
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListenerWithExceptions;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import net.sf.marineapi.nmea.sentence.Sentence;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;
import static org.geotoolkit.data.nmea.SerialPortConfig.NMEA_MESSAGE_DELIMITER;

/**
 * Listens to NMEA sentences received on a serial port, broadcasting (multicast) in a reactive stream.
 *
 * Note for maintainers:
 * As of version 2.6.2 of jSerialComm, once a serial port is opened, only a single listener can be attached,
 * and it uses a single dedicated thread to event listening/building. Therefore, we are assured that signals
 * will be emitted in a sequential fashion, and we can use the "unsafe" sink for better performance and
 * easier management of signal emission.
 * Note: in case you modify the sink parameters, please re-assert cases of signal emission. See methods
 * catchException(Exception) and emit(Sentence).
 *
 * @author Alexis Manin (Geomatys)
 */
final class ReactiveSentenceListener implements SerialPortMessageListenerWithExceptions, Disposable {

    final Sinks.Many<Sentence> sink;
    private volatile boolean isDisposed = false;

    ReactiveSentenceListener() {
        // Be careful if you change the sink configuration: emission methods must be reviewed according to the new
        // behavior of the sink.
        sink = Sinks.unsafe()
                .many()
                .replay()
                .latest();
    }

    @Override
    public byte[] getMessageDelimiter() {
        return NMEA_MESSAGE_DELIMITER;
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
            FeatureProcessor.read(text.trim()).ifPresent(this::emit);
        } catch (RuntimeException e) {
            catchException(e);
        }
    }

    public void emit(Sentence message) {
        switch (sink.tryEmitNext(message)) {
            case OK:
                return;
            case FAIL_ZERO_SUBSCRIBER: // With replay sink, this should never happen.
                LOGGER.warning("Emitting NMEA sentence from listener has failed. Check if the sink configuration is still in mode 'replay'");
                break;
            case FAIL_CANCELLED:
            case FAIL_TERMINATED:
                LOGGER.warning("An NMEA sentence has been emitted, but the sink has already been closed.");
                break;
            case FAIL_NON_SERIALIZED:
                LOGGER.warning("Unsafe sink should never emit this result. Check sink configuration");
                break;
            case FAIL_OVERFLOW:
                // According to the doc, the entire flux/sink should have been cancelled with an error.
                LOGGER.warning("NMEA data stream overflow. The stream might stop in a failing state.");
                break;
        }
    }

    @Override
    public void catchException(Exception e) {
        switch (sink.tryEmitError(e)) {
            case OK:
                return;
            case FAIL_OVERFLOW:
            case FAIL_ZERO_SUBSCRIBER: // If sink is changed (not changed from replay to something else), re-assert this case
            case FAIL_CANCELLED:
            case FAIL_TERMINATED:
            case FAIL_NON_SERIALIZED: // If sink is not unsafe anymore, we have to change how this case is handled
                // Safe cases for a multicast / unsafe sink as we've setup it in listener constructor.
                // The sink should be already closed in any of the above case.
                LOGGER.log(Level.FINE, "An error happened while listening to the port, but it cannot be emitted downstream", e);
        }
    }

    @Override
    public void dispose() {
        final Sinks.EmitResult result = sink.tryEmitComplete();
        switch (result) {
            case OK:
                // Only case we're sure the sink is properly closed.
                isDisposed = true;
                return;
            case FAIL_TERMINATED:
            case FAIL_CANCELLED:
                LOGGER.finer("An attempt to close a data stream has been made, but it is already closed");
                break;
            case FAIL_OVERFLOW:
            case FAIL_ZERO_SUBSCRIBER:
                LOGGER.log(Level.WARNING, "An invalid signal has been received upon completion: {0}", result);
                break;
            case FAIL_NON_SERIALIZED:
                LOGGER.warning("Unsafe sink should never emit this result. Check sink configuration");
                break;
        }
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
