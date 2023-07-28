package org.geotoolkit.wps.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import javax.xml.parsers.ParserConfigurationException;
import org.geotoolkit.test.xml.DocumentComparator;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.xml.MarshallerPool;
import org.junit.Assert;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class XMLBindingTestBuilder<T> {

    public static final Logger LOGGER = Logger.getLogger("org.geotoolkit.wps.xml");

    private static final ErrorLogger ERROR_LOGGER = new ErrorLogger();

    private Supplier<InputStream> input;
    private Class<T> expectedBindingType;
    private Consumer<T> additionalTests;
    private MarshallerPool pool;

    public void test() throws JAXBException, IOException, ParserConfigurationException, SAXException {
        ArgumentChecks.ensureNonNull("Input data to test", input);
        ArgumentChecks.ensureNonNull("Marshaller pool", pool);

        final Unmarshaller um = pool.acquireUnmarshaller();
        um.setEventHandler(ERROR_LOGGER);
        final Object unmarshalled;
        try (final InputStream in = input.get()) {
            final Object tmpObj = um.unmarshal(in);
            if (tmpObj instanceof JAXBElement) {
                unmarshalled = ((JAXBElement)tmpObj).getValue();
            } else {
                unmarshalled = tmpObj;
            }
        }
        pool.recycle(um);

        Assert.assertNotNull("Unmarshalled capabilities should not be null", unmarshalled);

        if (expectedBindingType != null) {
            Assert.assertTrue(
                    "Read capabilities is of invalid type. Expected: " + expectedBindingType + ", but was: " + unmarshalled.getClass(),
                    expectedBindingType.isInstance(unmarshalled)
            );
        }

        if (additionalTests != null) {
            additionalTests.accept( (T) unmarshalled );
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Marshaller marsh = pool.acquireMarshaller();
        marsh.setEventHandler(ERROR_LOGGER);
        marsh.marshal(unmarshalled, out);
        pool.recycle(marsh);

        final byte[] outputArray = out.toByteArray();
        LOGGER.info(() -> "Generated Document:" + System.lineSeparator() + new String(outputArray, StandardCharsets.UTF_8));

        final DocumentComparator comparator = new DocumentComparator(input.get(), new ByteArrayInputStream(outputArray));
        comparator.ignoredAttributes.add("http://www.w3.org/2000/xmlns:*");
        comparator.ignoredAttributes.add("http://www.w3.org/2001/XMLSchema-instance:schemaLocation");
        comparator.ignoreComments = true;
        comparator.compare();
    }

    public XMLBindingTestBuilder setInput(Supplier<InputStream> inputSupplier) {
        this.input = inputSupplier;
        return this;
    }

    public XMLBindingTestBuilder setInput(String resourceLocation, final ClassLoader resourceLoader) {
        ArgumentChecks.ensureNonNull("resource location", resourceLocation);
        ArgumentChecks.ensureNonNull("resource loader", resourceLoader);
        this.input = () -> resourceLoader.getResourceAsStream(resourceLocation);
        return this;
    }

    public XMLBindingTestBuilder setInput(String resourceUri) {
        return setInput(resourceUri, this.getClass().getClassLoader());
    }

    public XMLBindingTestBuilder setExpectedBindingType(Class<T> expectedBindingType) {
        this.expectedBindingType = expectedBindingType;
        return this;
    }

    public XMLBindingTestBuilder setPool(MarshallerPool pool) {
        this.pool = pool;
        return this;
    }

    public XMLBindingTestBuilder setAdditionalTests(Consumer<T> additionalTests) {
        this.additionalTests = additionalTests;
        return this;
    }

    public static void test(final String resourceLocation, final Class<?> expectedBinding) throws JAXBException, IOException, ParserConfigurationException, SAXException {
        new XMLBindingTestBuilder<>()
                .setInput(resourceLocation, expectedBinding.getClassLoader())
                .setExpectedBindingType(expectedBinding)
                .setPool(WPSMarshallerPool.getInstance())
                .test();
    }

    private static class ErrorLogger implements ValidationEventHandler {

        @Override
        public boolean handleEvent(ValidationEvent event) {
            LOGGER.log(Level.WARNING, event.getLinkedException(), () -> {
                final String errorLevel;
                switch (event.getSeverity()) {
                    case ValidationEvent.WARNING:
                        errorLevel = "WARNING";
                        break;
                    case ValidationEvent.ERROR:
                        errorLevel = "ERROR";
                        break;
                    default:
                        errorLevel = "FATAL_ERROR";
                        break;
                }

                return String.format("%s: %s. Location:%n%s", errorLevel, event.getMessage(), event.getLocator());
            });

            return event.getSeverity() < 1;
        }

    }
}
