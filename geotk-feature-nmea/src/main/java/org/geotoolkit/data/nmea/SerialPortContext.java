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
import net.sf.marineapi.nmea.sentence.Sentence;
import reactor.core.publisher.Flux;

/**
 * @author Alexis Manin (Geomatys)
 */
class SerialPortContext implements AutoCloseable {
    final SerialPort port;
    final ReactiveSentenceListener eventListener;

    private SerialPortContext(SerialPort port, final ReactiveSentenceListener eventListener) {
        this.port = port;
        this.eventListener = eventListener;
    }

    @Override
    public void close() {
        try (
                final RuntimeCloseable removeListener = port::removeDataListener ;
                final RuntimeCloseable closePort = port::closePort;
                final RuntimeCloseable closeSink = eventListener::dispose
        ) {}
    }

    Flux<Sentence> flux() {
        return eventListener.sink.asFlux();
    }

    static SerialPortContext open(final SerialPort port, Consumer<SerialPort> conf) {
        if (port.isOpen()) {
            throw new IllegalStateException("Given port is already opened");
        }

        final ReactiveSentenceListener listener = new ReactiveSentenceListener();
        try {
            if (!port.addDataListener(listener)) {
                throw new IllegalStateException("Given port is already in use: " + port.getSystemPortName());
            }

            if (conf == null) conf = SerialPortConfig.NMEA_0183;
            conf.accept(port);

            if (!port.openPort()) {
                throw new RuntimeException("Cannot open serial port: " + port.getSystemPortName());
            }
        } catch (RuntimeException e) {
            try {
                listener.dispose();
            } catch (Exception bis) {
                e.addSuppressed(bis);
            }
            throw e;
        }

        return new SerialPortContext(port, listener);
    }

    private interface RuntimeCloseable extends AutoCloseable {
        @Override
        void close();
    }
}
