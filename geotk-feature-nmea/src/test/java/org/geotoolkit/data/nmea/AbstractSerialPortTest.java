package org.geotoolkit.data.nmea;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;

/**
 * Base class for tests involving (virtual) serial ports. Inherit this to build tests that read or write data from a
 * serial port.
 * Notes:
 * <ul>
 *     <li>A simple example that read plain text messages is available in {@link RawSerialPortUsageTest}.</li>
 *     <li>A dedicated pair of virtual ports is created for each test. Access them through {@link #env}.</li>
 *     <li>Limitation: the {@link #env virtual port pair} can be setup only for linux hosts that provide
 *     <code>socat</code> command. In any other case, the test is skipped.</li>
 * </ul>
 */
public class AbstractSerialPortTest {

    protected VirtualEnvironment env;

    @Before
    public void initPort() {
        try {
            env = forLinux();
            Assume.assumeNotNull(env);
        } catch (Exception e) {
            Assume.assumeNoException("Cannot initialize a virtual serial port", e);
        }
    }

    @After
    public void destroyPort() {
        if (env != null) env.close();
    }

    private static String link(final String dir, final String type, final String uid) {
        return String.format("%s%sgeotk-nmea-test-%s_%s", dir, FileSystems.getDefault().getSeparator(), type, uid);
    }

    private static VirtualEnvironment forLinux() throws IOException, InterruptedException {
        final String dir  = System.getProperty("java.io.tmpdir");
        final String uuid = UUID.randomUUID().toString();

        final String writeLink = link(dir, "write", uuid);
        final String readLink = link(dir, "read", uuid);
        final String addressSpec = "pty,raw,echo=0,link=";
        final ProcessBuilder cmd = new ProcessBuilder("socat", addressSpec + writeLink, addressSpec + readLink);

        final Process socat = cmd.start();
        NMEAStore.LOGGER.warning(() -> String.format(
                "An external 'socat' process has been started for unit tests." +
                        "%nIf any problem arise, check if it must be killed manually. PID: %d",
                socat.pid()));
        try {
            final boolean exited = socat.waitFor(200, TimeUnit.MILLISECONDS);
            if (exited) throw new IOException("socat process ended prematurily with return code: "+socat.exitValue());
        } catch (RuntimeException | InterruptedException e) {
            try {
                socat.destroy();
            } catch (Exception bis) {
                e.addSuppressed(bis);
            }
            throw e;
        }

        final SerialPort writeable = SerialPort.getCommPort(writeLink);
        final SerialPort readable  = SerialPort.getCommPort(readLink);
        return new VirtualEnvironment(writeable, readable, socat::destroy);
    }

    protected static class VirtualEnvironment implements AutoCloseable {

        final SerialPort writeable;
        final SerialPort readable;
        private final Runnable closeAction;

        private VirtualEnvironment(SerialPort writeable, SerialPort readable, Runnable closeAction) {
            this.writeable = writeable;
            this.readable = readable;
            this.closeAction = closeAction;
        }

        @Override
        public void close() {
            closeAction.run();
        }
    }
}
