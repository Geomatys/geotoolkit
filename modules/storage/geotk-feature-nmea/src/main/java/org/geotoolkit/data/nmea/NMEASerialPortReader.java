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
package org.geotoolkit.data.nmea;

import static org.geotoolkit.data.nmea.NMEAFeatureStore.NMEA_TYPE;
import static org.geotoolkit.data.nmea.NMEAFeatureStore.TYPE_NAME;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.SentenceValidator;
import net.sf.marineapi.provider.event.ProviderListener;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.memory.MemoryFeatureStore;

/**
 * Scan serial ports to find GPS data, and then initialize a reader on matching stream.
 * For further details on how data is read, see {@link NMEABuilder}.
 *
 * @author Alexis Manin (Geomatys)
 */
public class NMEASerialPortReader implements ProviderListener<NMEABuilder.FeatureEvent> {

    private static final Logger LOGGER = Logging.getLogger(NMEASerialPortReader.class);

    /** A timeout used for connexion request on ports */
    public static final int CONNEXION_TIMEOUT = 100;
    public static final int SCAN_TIMEOUT = 5000;
    public static final int PORT_DSR_TIMEOUT = 30000;
    public static final int PORT_DSR_PAUSE = 500;

    private final EventListenerList listeners = new EventListenerList();

    private final CommPort port;
    private InputStream input = null;

    private WeakReference<MemoryFeatureStore> store = null;

    private SentenceReader reader = null;
    private NMEABuilder builder = null;

    private boolean isReading = false;

    /**
     * Scan serial ports in search of NMEA data. If found, a reader is initialized on the found port.
     * @throws IOException if no NMEA data can be found on any port.
     * @throws Exception If unexpected problem happend while scanning ports (native libs failed to load, etc.)
     */
    public NMEASerialPortReader() throws Exception {
        this(scanSerialPorts());
    }

    /**
     * Initialize reader to listen on given port. If port don't provide NMEA data
     * when {@link NMEASerialPortReader#read() } method is called, an exception will
     * be thrown.
     * @param port The port to read on.
     */
    public NMEASerialPortReader(CommPort port) {
        ArgumentChecks.ensureNonNull("Port to get data from", port);
        this.port = port;
    }

    /**
     * Read directly data from given input stream. No port scanning nor port use is done when
     * the reader is instantiated from this constructor.
     * @param stream
     */
    public NMEASerialPortReader(InputStream stream) {
        ArgumentChecks.ensureNonNull("Input stream", stream);
        port = null;
        input = stream;
    }

    /**
     * Scan all available ports on the machine to find one which send readable GPS data.
     * @return The port which give readable data, or throws an exception otherwise.
     * @throws IOException If we cannot find any port providing NMEA data
     */
    private static CommPort scanSerialPorts() throws IOException {
        CommPort result = null;

        final Enumeration e = CommPortIdentifier.getPortIdentifiers();

        SerialPort port;
        Future<Boolean> testResult;
        ExecutorService es = Executors.newFixedThreadPool(1);
        while (e.hasMoreElements()) {
            final CommPortIdentifier portId = (CommPortIdentifier) e.nextElement();
            try {
                if (portId.getPortType() != CommPortIdentifier.PORT_SERIAL) {
                    continue;
                }

                port = (SerialPort) portId.open("Geotk reader", CONNEXION_TIMEOUT);
                port.setSerialPortParams(4800, SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                LOGGER.log(Level.INFO, "Scanning port {0}", port.getName());
                // Check if we can find GPS data on current port. If true, then we return this port.
                testResult = es.submit(new PortScanner(port));
                if (testResult.get(SCAN_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    result = port;
                    LOGGER.log(Level.FINE, "GPS device have been found on port {0}", port.getName());
                    break;
                }
            } catch (Exception ex) {
                LOGGER.log(Level.INFO, "Port scan failed.", ex);
                continue;
            }
        }

        es.shutdown();
        if (result == null) {
            throw new IOException("No NMEA data can be found on scanned serial ports.");
        }
        return result;
    }

    /**
     * Start reading data on input port or stream.
     * @return The {@link MemoryFeatureStore} in which read data will be stored.
     * @throws IOException If a problem occurs when opening port input stream.
     */
    public MemoryFeatureStore read() throws IOException {
        // Start GPS measure reading
        if (input == null && port != null) {
            input = port.getInputStream();
        }
        reader = new SentenceReader(input);

        final MemoryFeatureStore fStore = new MemoryFeatureStore(NMEA_TYPE, true);
        store = new WeakReference<>(fStore);

        builder = new NMEABuilder(reader);
        builder.addListener(this);

        reader.start();
        isReading = true;

        return fStore;
    }

    /**
     *
     * @return True if the reader is still listening for data on input. False otherwise.
     */
    public boolean isReading() {
        return isReading;
    }

    /**
     * Close the reader and port / input we've read on.
     */
    public void dispose() {
        // We use this check because only current method should put the isReading attribute to false.
        if (!isReading) {
            return;
        }
        LOGGER.log(Level.INFO, "Stop reading on port {0}", port.getName());
        builder.removeListener(this);

        reader.stop();
        isReading = false;

        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }
        if (port != null) {
            ((SerialPort)port).close();
            LOGGER.log(Level.INFO, "Scanned port successfully released.");
        }
        fireStop();
    }

    /**
     * The {@link NMEABuilder} Send us data as it read / interpret it. We try to
     * add it to output {@link FeatureStore}. If the store has been deleted by user,
     * we just dispose the reader.
     * As we can't use properly {@link SentenceListener} because of concurrent modification
     * exceptions, we use {@link NMEABuilder.FeatureEvent} propagation to know if reader is still
     * running. if an event (or its data) is null, it means reader has paused or stop. In this case,
     * we just dispose the reader.
     * TODO : Maybe try a reconnection pass in the case we are listening to a {@link SerialPort}.
     * @param evt The event containing data to store.
     */
    @Override
    public void providerUpdate(NMEABuilder.FeatureEvent evt) {
        if (store.get() != null && evt != null && evt.getData() != null) {
            try {
                store.get().addFeatures(TYPE_NAME, Collections.singleton(evt.getData()));
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        } else {
            dispose();
        }
    }

    /**
     * TODO : Correct the following method to allow reconnection to GPS input.
     * Disfunction seems to originate from the fact that nor DTR nor DSR states are
     * sufficient to detect that a terminal have been properly reconnected to the listened port.
     * It seems they are states which come after, so we need to find the state which precisely
     * describe reconnection.
     */
    public void tryReconnect() {
//        if (port instanceof SerialPort) {
//            final SerialPort sp = (SerialPort) port;
//            LOGGER.log(Level.INFO, "Port connexion interrupted, wait for reconnexion.");
//            final Thread DSRChecker = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (!sp.isDTR()) {
//                        try {
//                            Thread.sleep(PORT_DSR_PAUSE);
//                        } catch (InterruptedException ex) {
//                            // Nothing to do, thread can't wait any longer, and
//                            // we consider connexion is definitely broken.
//                        }
//                    }
//                }
//            });
//            DSRChecker.start();
//            try {
//                DSRChecker.join(SCAN_TIMEOUT);
//                DSRChecker.interrupt();
//            } catch (InterruptedException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            // Connexion recovered.
//            if (sp.isDSR()) {
//                LOGGER.log(Level.INFO, "Port reconnexion recovered.");
//                reader.start();
//                return;
//            }
//        }
        // If no serial port connexion can be recovered, just stop the reader.
        LOGGER.log(Level.INFO, "Port connexion have been lost. Dispose reader.");
        dispose();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        listeners.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        listeners.remove(PropertyChangeListener.class, listener);
    }

    private void fireStop(){
        final PropertyChangeListener[] lsts = listeners.getListeners(PropertyChangeListener.class);
        for(PropertyChangeListener lst : lsts){
            lst.propertyChange(new PropertyChangeEvent(this, "stop", true, false));
        }

    }

    /**
     * An utility class for testing repeatedly a given port, searching fo NMEA data.
     * Operation performed by this thread can be BLOCKING, because of buffered reader
     * which wait for data from input stream. In consequence, it should be used with
     * a timeout trigger.
     */
    private static class PortScanner implements Callable<Boolean> {

        private final CommPort port;

        public PortScanner(final CommPort toScan) {
            port = toScan;
        }

        @Override
        public Boolean call() throws Exception {
            try (BufferedReader bReader = new BufferedReader(new InputStreamReader(port.getInputStream()))) {
                // Repeat few times, because at first try, we could get bad data.
                for (int tryCount = 0; tryCount < 7; tryCount++) {
                    String data = bReader.readLine();
                    if (SentenceValidator.isValid(data)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e.getLocalizedMessage());
            }
            return false;
        }
    }

}
