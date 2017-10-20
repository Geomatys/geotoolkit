package org.geotoolkit.wms.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class DTDIgnoreUnmarshaller implements Unmarshaller {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wms.xml");

    private static final SAXParserFactory SAX_FACTORY;
    static {
        SAX_FACTORY = SAXParserFactory.newInstance();
        try {
            SAX_FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAX_FACTORY.setFeature("http://xml.org/sax/features/validation", false);
        } catch (Exception e) {
            // Well, shit. We will try to parse data anyway
            LOGGER.log(Level.WARNING, "Cannot configure unmarshaller to ignore DTD", e);
        }
    }

    final Unmarshaller source;
    public DTDIgnoreUnmarshaller(Unmarshaller toWrap) {
        ArgumentChecks.ensureNonNull("Source unmarshaller", toWrap);
        source = toWrap;
    }

    @Override
    public Object unmarshal(File f) throws JAXBException {
        try (final FileReader reader = new FileReader(f)) {
            return unmarshal(reader);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public Object unmarshal(InputStream is) throws JAXBException {
        return unmarshal(new InputStreamReader(is));
    }

    @Override
    public Object unmarshal(Reader reader) throws JAXBException {
        return unmarshal(new InputSource(reader));
    }

    @Override
    public Object unmarshal(URL url) throws JAXBException {
        try {
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            try (final InputStream stream = url.openStream();
                    final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return unmarshal(reader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Object unmarshal(InputSource source) throws JAXBException {
        try {
            return unmarshal(new SAXSource(SAX_FACTORY.newSAXParser().getXMLReader(), source));
        } catch (ParserConfigurationException|SAXException ex) {
            LOGGER.log(Level.WARNING, "Cannot configure reading to ignore DTD ", ex);
            return this.source.unmarshal(source);
        }
    }

    @Override
    public Object unmarshal(Node node) throws JAXBException {
        return source.unmarshal(node);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
        return source.unmarshal(node, declaredType);
    }

    @Override
    public Object unmarshal(Source source) throws JAXBException {
        return this.source.unmarshal(source);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException {
        return this.source.unmarshal(source, declaredType);
    }

    @Override
    public Object unmarshal(XMLStreamReader reader) throws JAXBException {
        return source.unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
        return source.unmarshal(reader, declaredType);
    }

    @Override
    public Object unmarshal(XMLEventReader reader) throws JAXBException {
        return source.unmarshal(reader);
    }

    @Override
    public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
        return source.unmarshal(reader, declaredType);
    }

    @Override
    public UnmarshallerHandler getUnmarshallerHandler() {
        return source.getUnmarshallerHandler();
    }

    @Override
    public void setValidating(boolean validating) throws JAXBException {
        source.setValidating(validating);
    }

    @Override
    public boolean isValidating() throws JAXBException {
        return source.isValidating();
    }

    @Override
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        source.setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return source.getEventHandler();
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        source.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws PropertyException {
        return source.getProperty(name);
    }

    @Override
    public void setSchema(Schema schema) {
        source.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return source.getSchema();
    }

    @Override
    public void setAdapter(XmlAdapter adapter) {
        source.setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        source.setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return source.getAdapter(type);
    }

    @Override
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
        source.setAttachmentUnmarshaller(au);
    }

    @Override
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return source.getAttachmentUnmarshaller();
    }

    @Override
    public void setListener(Listener listener) {
        source.setListener(listener);
    }

    @Override
    public Listener getListener() {
        return source.getListener();
    }

}
