package org.geotoolkit.feature.xml;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * An exception whose aim is to describe an OGC Exception from OGC ExceptionReport.
 * Its a fallback for the Jaxp module which does not have necessary JAXB bindings.
 *
 * @author Alexis Manin (Geomatys)
 */
public class ExceptionReport extends RuntimeException {

    public final String exceptionCode;
    public final String locator;
    public final String exceptionText;

    public ExceptionReport() {
        this(null, null, null);
    }

    public ExceptionReport(final String message) {
        super(message);
        exceptionCode = exceptionText = locator = null;
    }

    public ExceptionReport(final String message, final Exception cause) {
        super(message, cause);
        exceptionCode = exceptionText = locator = null;
    }

    public ExceptionReport(final String exceptionCode, final String locator, final String exceptionText) {
        this(exceptionCode, locator, exceptionText, null);
    }

    public ExceptionReport(final String exceptionCode, final String locator, final String exceptionText, final Exception cause) {
        super(buildMessage(exceptionCode, locator, exceptionText), cause);
        this.exceptionCode = exceptionCode;
        this.locator = locator;
        this.exceptionText = exceptionText;

    }

    /**
     * Read data from given stream, expecting it to be an exception or exception
     * report (see OGC service standards).
     *
     * @param reader Xml source describing error. We expect it to point on the
     * exception report or its inner exception markup.
     * @return An exception embedding information about the parsed OGC exception.
     * @throws XMLStreamException If an error occurs while gathering exception
     * report information from the stream.
     */
    public static ExceptionReport readException(XMLStreamReader reader) throws XMLStreamException {
        String errorCode = null;
        String errorLocator = null;
        String errorText = null;
        int next = reader.nextTag();
        if (next == START_ELEMENT) {
            QName name = reader.getName();
            if (name.getLocalPart().equalsIgnoreCase("ExceptionReport")) {
                do {
                    next = reader.nextTag();
                } while (next != START_ELEMENT);
                name = reader.getName();
            }
            if (name.getLocalPart().equalsIgnoreCase("Exception")) {
                if (reader.getAttributeCount() > 0) {
                    errorCode = reader.getAttributeValue(null, "exceptionCode");
                    errorLocator = reader.getAttributeValue(null, "locator");
                }
                next = reader.nextTag();
                if (next == START_ELEMENT) {
                    name = reader.getName();
                    if (name.getLocalPart().equalsIgnoreCase("ExceptionText")) {
                        errorText = reader.getElementText();
                    }
                }
            }
        }

        return new ExceptionReport(errorCode, errorLocator, errorText);
    }

    private static String buildMessage(final String exceptionCode, final String locator, final String exceptionText) {
        if (exceptionCode == null && locator == null && exceptionText == null) {
            return "Input document describe an error, but we're not capable of providing details.";
        } else {
            return String.format(
                    "Exception report%nException code: %s%nLocator: %s%nError text: %s",
                    valueOr(exceptionCode, "undefined"), valueOr(locator, "undefined"), valueOr(exceptionText, "undefined")
            );
        }
    }

    private static String valueOr(final String value, final String defaultValue) {
        return value == null? defaultValue : value;
    }
}
