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
import java.nio.charset.StandardCharsets;
import net.sf.marineapi.nmea.sentence.Sentence;

/**
 * @author Alexis Manin (Geomatys)
 */
public class SerialPortConfig implements Discovery.Configuration<SerialPort> {

    public static final byte[] NMEA_MESSAGE_DELIMITER = Sentence.TERMINATOR.getBytes(StandardCharsets.US_ASCII);
    /**
     * Configure port to read NMEA from. Based on details provided
     * <a href="https://en.wikipedia.org/wiki/NMEA_0183#Serial_configuration_(data_link_layer)">in this article</a>.
     */
    public static final SerialPortConfig NMEA_0183 = new SerialPortConfig(4800, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

    private final int baudRate;
    private final int dataBits;
    private final int stopBits;
    private final int parity;

    public SerialPortConfig(int baudRate, int dataBits, int stopBits, int parity) {
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getNumDataBits() {
        return dataBits;
    }

    public int getNumStopBits() {
        return stopBits;
    }

    public int getParity() {
        return parity;
    }

    @Override
    public void accept(SerialPort port) {
        port.setComPortParameters(baudRate, dataBits, stopBits, parity);
    }
}
