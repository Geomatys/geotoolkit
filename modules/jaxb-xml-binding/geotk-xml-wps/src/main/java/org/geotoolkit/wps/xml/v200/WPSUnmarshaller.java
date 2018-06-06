package org.geotoolkit.wps.xml.v200;

import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import org.apache.sis.internal.jaxb.Context;
import org.geotoolkit.wps.xml.UnmarshallerProxy;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WPSUnmarshaller extends UnmarshallerProxy {

    public WPSUnmarshaller(Unmarshaller toWrap) {
        super(toWrap);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
        try {
            return unmarshal(XML_FACTORY.createXMLEventReader(source), declaredType);
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public Object unmarshal(Source source) throws JAXBException {
        try {
            return unmarshal(XML_FACTORY.createXMLEventReader(source));
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
        try {
            return unmarshal(XML_FACTORY.createXMLEventReader(new DOMSource(node)), declaredType);
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public Object unmarshal(Node node) throws JAXBException {
        try {
            return unmarshal(XML_FACTORY.createXMLEventReader(new DOMSource(node)));
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public Object unmarshal(InputSource source) throws JAXBException {
        try {
            return unmarshal(XML_FACTORY.createXMLEventReader(new SAXSource(source)));
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
        try {
            checkServiceVersion(reader);
            return super.unmarshal(new TransformingReader(reader), declaredType);
        } finally {
            FilterByVersion.IS_LEGACY.remove();
        }
    }

    @Override
    public Object unmarshal(XMLEventReader reader) throws JAXBException {
        try {
            checkServiceVersion(reader);
            return super.unmarshal(new TransformingReader(reader));
        } finally {
            FilterByVersion.IS_LEGACY.remove();
        }
    }

    private void checkServiceVersion(final XMLEventReader reader) {
        try {
            XMLEvent event = reader.peek();
            if (event.isStartElement()) {
                Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                String service = null, version = null;
                while (attributes.hasNext()) {
                    final Attribute next = attributes.next();
                    final String attName = next.getName().getLocalPart();
                    if (attName.equals("service")) {
                        service = next.getValue();
                    } else if (attName.equals("version")) {
                        version = next.getValue();
                    }
                }

                if (service != null && version != null && "WPS".equalsIgnoreCase(service)) {
                    if ("1".equals(version) || version.startsWith("1.")) {
                        FilterByVersion.IS_LEGACY.set(Boolean.TRUE);
                    } else if ("2".equals(version) || version.startsWith("2.")) {
                        FilterByVersion.IS_LEGACY.set(Boolean.FALSE);
                    }
                }
            }
        } catch (Exception e) {
            Context.LOGGER.log(Level.FINE, "Cannot check service version from input WPS document.", e);
        }
    }
}
