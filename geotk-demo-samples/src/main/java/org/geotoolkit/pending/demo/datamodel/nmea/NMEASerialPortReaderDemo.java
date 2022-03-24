/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.pending.demo.datamodel.nmea;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.geotoolkit.data.nmea.Discovery;
import org.geotoolkit.data.nmea.FlowableFeatureSet;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

/**
 * Connect and activate a GPS on COM/USB port first.
 * Ensure you have the proper rights to read such external device.
 * On linux, user must be in group dialout. For more information about permission problems, see README file in NMEA
 * module (modules/storage/geotk-feature-nmea).
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEASerialPortReaderDemo {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.pending.demo.datamodel.nmea");

    public static void main(String[] args) throws Exception {
        // This component allows for serial port scan
        final Discovery<?> serial = Discovery.serial();

        // On trigger, a scan of serial ports will be started. A dataset can be returned for each found port emiting nmea messages.
        final Disposable subscriber = serial.discover()
                // For demo/simplicity, we just keep the first encountered communication channel
                .next()
                .flatMapMany(dataset -> Flux.using(() -> dataset, FlowableFeatureSet::flow, FlowableFeatureSet::close))
                .subscribe(
                        // On port acquisition, we can define what to do with received messages. Here, just log them.
                        feature -> LOGGER.info(String.format("Next message:%n%s", feature)),
                        error -> LOGGER.log(Level.WARNING, "A problem has occurred while scanning port", error)
                );

        // Do not forget to dispose your resources on shutdown.
        subscriber.dispose();
        LOGGER.log(Level.INFO, "Port reading ended.");
    }
}
