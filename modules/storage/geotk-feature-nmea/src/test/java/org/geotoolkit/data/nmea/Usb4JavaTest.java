package org.geotoolkit.data.nmea;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPort;
import javax.usb.UsbServices;

import org.junit.Ignore;
import org.junit.Test;

import static org.geotoolkit.data.nmea.NMEAStore.LOGGER;

/**
 * A test to check Usb4java API. Please delete only if you're sure it's not a requirement for future use.
 * TODO: This has been explored in january 2020 in the context of a client project. If not integrated midterm
 * of the same year, we can consider this obsolete and remove that code and test dependency it rely on.
 */
public class Usb4JavaTest {

    @Ignore
    @Test
    public void testDiscovery() throws UsbException, UnsupportedEncodingException {
        final UsbServices services = UsbHostManager.getUsbServices();
        final UsbHub hub = services.getRootUsbHub();
        LOGGER.info("Root hub: "+hub.toString());
        LOGGER.info("Port list: ");
        final List<UsbPort> ports = (List<UsbPort>) hub.getUsbPorts();
        for (UsbPort port : ports) {
            LOGGER.info("Port "+((int)port.getPortNumber())+" attached to "+(port.getUsbDevice() == null? "None" : port.getUsbDevice().toString()));
        }

        LOGGER.info("Currently connected devices: ");
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            LOGGER.info("Device "+device.toString()+ " on port "+device.getParentUsbPort().getPortNumber());
            final UsbConfiguration deviceConf = device.getActiveUsbConfiguration();
            if (deviceConf == null) {
                LOGGER.info("\tDevice not configured !");
            } else {
                LOGGER.info("\tInterfaces: "+ listInterfaces(deviceConf));
                try {
                    final UsbInterface usbInet = deviceConf.getUsbInterface((byte) 0);
                    if (usbInet == null) LOGGER.info("A NULL INTERFACE WAS RETURNED");
                    else LOGGER.info("SUCCESSFULLY ACQUIRED INTERFACE");
                    usbInet.claim();
                    LOGGER.info("CLAIM SUCCESS");
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Cannot claim USB interface", e);
                }
            }
        }
    }

    private static String listInterfaces(final UsbConfiguration conf) {
        final List<UsbInterface> is = conf.getUsbInterfaces();
        return is.stream().map(i -> Byte.toString(i.getActiveSettingNumber()))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
