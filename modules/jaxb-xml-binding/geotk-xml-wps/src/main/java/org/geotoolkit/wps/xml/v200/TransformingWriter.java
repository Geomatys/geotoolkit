package org.geotoolkit.wps.xml.v200;

import java.util.Set;
import java.util.Objects;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.Namespace;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.apache.sis.util.resources.Errors;

import static javax.xml.stream.XMLStreamConstants.*;

final class TransformingWriter extends LegacyTransformer implements XMLEventWriter {

    /**
     * Where events are sent.
     */
    private final XMLEventWriter out;

    /**
     * Creates a new writer for the given version of the standards.
     */
    TransformingWriter(final XMLEventWriter out) {
        this.out = out;
    }

    /**
     * Converts an event from the namespaces used in JAXB annotations to the namespaces used in the XML document
     * to write. This method may wrap the given event into another event for changing the namespace and prefix,
     * or use the event as-is if no change is needed.
     *
     * @param  event  the event using JAXB namespaces.
     */
    @Override
    @SuppressWarnings("unchecked")      // TODO: remove on JDK9
    public void add(XMLEvent event) throws XMLStreamException {
        if (FilterByVersion.isV1()) {
            switch (event.getEventType()) {
                case NAMESPACE: {
                    // TODO : replace Namespace by prefix.
                    event = convert((Namespace) event);
                    break;
                }
                case END_ELEMENT: {
                    event = convert(event.asEndElement());
                    break;
                }
                case START_ELEMENT: {
                    event = convert(event.asStartElement());
                    break;
                }
            }
        }

        out.add(event);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        out.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        out.setDefaultNamespace(uri);
    }

    private Namespace convert(Namespace event) {
        final String newNS = exportNS(event.getNamespaceURI());
        if (!Objects.equals(newNS, event.getNamespaceURI())) {
            eventFactory.setLocation(event.getLocation());
            event = eventFactory.createNamespace(event.getPrefix(), newNS);
        }
        return event;
    }

    private EndElement convert(EndElement event) {
        final QName originalName = event.getName();

        final QName newName = exportName(originalName);
        if (!Objects.equals(newName, originalName)) {
            eventFactory.setLocation(event.getLocation());
            event = eventFactory.createEndElement(newName, event.getNamespaces());
        }

        context.removeLast();

        return event;
    }

    private StartElement convert(StartElement event) {
        final QName originalName = event.getName();
        final String originalLocalName = originalName.getLocalPart();

        final QName newName = exportName(originalName);
        if (!Objects.equals(newName, originalName)) {
            eventFactory.setLocation(event.getLocation());
            event = eventFactory.createStartElement(newName, event.getAttributes(), event.getNamespaces());
        }

        context.add(originalLocalName);

        return event;
    }

    /**
     * A sentinel value in the {@link TransformingWriter#deferred} queue meaning that after reaching this point,
     * we need to reevaluate if the remaining elements should be written immediately of deferred again.
     * This happen when some elements to move are interleaved. For example in {@code MD_DataIdentification}:
     *
     * <ol>
     *   <li>{@code topicCategory} needs to move before {@code environmentDescription}</li>
     *   <li>{@code extent} needs to move before {@code supplementalInformation}</li>
     *   <li>{@code graphicOverviews}</li>
     *   <li>{@code resourceFormats}</li>
     *   <li><i>etc.</i>
     *   <li>{@code environmentDescription}</li>
     *   <li>{@code supplementalInformation}</li>
     * </ol>
     *
     * This class is for handling the {@code extent} case in such scenario.
     */
    private static final class NewDeferred {
        /** The value to assign to {@link TransformingWriter#deferred} after we reach this point. */
        final Set<QName> toSkip;

        /** Creates a new sentinel value for a reevaluation point. */
        NewDeferred(final Set<QName> toSkip) {
            this.toSkip = toSkip;
        }
    }

    /**
     * Adds an entire stream to an output stream.
     */
    @Override
    public void add(final XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            add(reader.nextEvent());
        }
    }

    /**
     * Gets the prefix the URI is bound to. Since our (imported URI) ⟶ (exported URI) transformation
     * is not bijective, implementing this method could potentially result in the same prefix for different URIs,
     * which is illegal for a XML document and potentially dangerous. Thankfully JAXB seems to never invoke this
     * method in our tests.
     */
    @Override
    public String getPrefix(final String uri) throws XMLStreamException {
        throw new XMLStreamException(Errors.format(Errors.Keys.UnsupportedOperation_1, "getPrefix"));
    }

    /**
     * Sets the current namespace context for prefix and URI bindings.
     * This method unwraps the original context and forwards the call.
     *
     * <p>Implemented as a matter of principle, but JAXB did not invoked this method in our tests.</p>
     */
    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
        out.setNamespaceContext(TransformingNamespaces.asXML(context));
    }

    /**
     * Returns a naming context suitable for consumption by JAXB marshallers.
     * The {@link XMLEventWriter} wrapped by this {@code TransformingWriter} has been created for writing in a file.
     * Consequently its naming context manages namespaces used in the XML document. But the JAXB marshaller using
     * this {@code TransformingWriter} facade expects the namespaces declared in JAXB annotations. Consequently this
     * method returns an adapter that converts namespaces on the fly.
     *
     * @see Event#getNamespaceContext()
     */
    @Override
    public NamespaceContext getNamespaceContext() {
        return TransformingNamespaces.asJAXB(out.getNamespaceContext());
    }


    /**
     * Writes any cached events to the underlying output mechanism.
     */
    @Override
    public void flush() throws XMLStreamException {
        out.flush();
    }

    /**
     * Frees any resources associated with this writer.
     */
    @Override
    public void close() throws XMLStreamException {
        out.close();
    }
}
