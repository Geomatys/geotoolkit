package org.geotoolkit.wps.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.wps.xml.v200.WriterWrapper;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class MarshallerProxy implements Marshaller {


    protected static final XMLOutputFactory XML_FACTORY = XMLOutputFactory.newInstance();

    final Marshaller wrapped;

    public MarshallerProxy(Marshaller wrapped) {
        ArgumentChecks.ensureNonNull("Marshaller to wrap", wrapped);
        this.wrapped = wrapped;
    }

    @Override
    public void marshal(Object jaxbElement, Result result) throws JAXBException {
        wrapped.marshal(jaxbElement, result);
    }

    @Override
    public void marshal(Object jaxbElement, OutputStream os) throws JAXBException {
        marshal(jaxbElement, new OutputStreamWriter(os));
    }

    @Override
    public void marshal(Object jaxbElement, File output) throws JAXBException {
        try (final FileWriter writer = new FileWriter(output)) {
            marshal(jaxbElement, writer);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public void marshal(Object jaxbElement, Writer writer) throws JAXBException {
        try {
            marshal(jaxbElement, XML_FACTORY.createXMLEventWriter(new WriterWrapper(writer)));
        } catch (XMLStreamException ex) {
            throw new JAXBException(ex);
        }
    }

    @Override
    public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException {
        wrapped.marshal(jaxbElement, handler);
    }

    @Override
    public void marshal(Object jaxbElement, Node node) throws JAXBException {
        wrapped.marshal(jaxbElement, node);
    }

    @Override
    public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException {
        wrapped.marshal(jaxbElement, writer);
    }

    @Override
    public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException {
        wrapped.marshal(jaxbElement, writer);
    }

    @Override
    public Node getNode(Object contentTree) throws JAXBException {
        return wrapped.getNode(contentTree);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        wrapped.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws PropertyException {
        return wrapped.getProperty(name);
    }

    @Override
    public void setEventHandler(ValidationEventHandler handler) throws JAXBException {
        wrapped.setEventHandler(handler);
    }

    @Override
    public ValidationEventHandler getEventHandler() throws JAXBException {
        return wrapped.getEventHandler();
    }

    @Override
    public void setAdapter(XmlAdapter adapter) {
        wrapped.setAdapter(adapter);
    }

    @Override
    public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
        wrapped.setAdapter(type, adapter);
    }

    @Override
    public <A extends XmlAdapter> A getAdapter(Class<A> type) {
        return wrapped.getAdapter(type);
    }

    @Override
    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        wrapped.setAttachmentMarshaller(am);
    }

    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return wrapped.getAttachmentMarshaller();
    }

    @Override
    public void setSchema(Schema schema) {
        wrapped.setSchema(schema);
    }

    @Override
    public Schema getSchema() {
        return wrapped.getSchema();
    }

    @Override
    public void setListener(Listener listener) {
        wrapped.setListener(listener);
    }

    @Override
    public Listener getListener() {
        return wrapped.getListener();
    }
}
