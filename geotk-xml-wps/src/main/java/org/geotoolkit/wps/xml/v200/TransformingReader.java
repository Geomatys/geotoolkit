/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.wps.xml.v200;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Namespace;
import org.apache.sis.util.collection.BackingStoreException;

import static javax.xml.stream.XMLStreamConstants.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;


/**
 * A XML reader replacing the namespaces found in XML documents by the namespaces expected by SIS at unmarshalling time.
 * This class forwards every method calls to the wrapped {@link XMLEventReader}, but with some {@code namespaceURI}
 * modified before being transfered. This class uses a dictionary for identifying the XML namespaces expected by JAXB
 * implementation. This is needed when a single namespace in a legacy schema has been splitted into many namespaces
 * in the newer schema. This happen for example in the upgrade from ISO 19139:2007 to ISO 19115-3.
 * In such cases, we need to check which attribute is being mapped in order to determine the new namespace.
 *
 * @author  Cullen Rombach (Image Matters)
 * @author  Martin Desruisseaux (Geomatys)
 * @version 1.0
 * @since   1.0
 * @module
 */
final class TransformingReader extends LegacyTransformer implements XMLEventReader {

    /**
     * The reader from which to read events.
     */
    private final XMLEventReader in;

    /**
     * The next event to return after a call to {@link #peek()}. This is used for avoiding to recompute
     * the same object many times when {@link #peek()} is invoked before a call to {@link #nextEvent()}.
     * This is also required for avoiding to duplicate additions and removals of elements in the
     * {@code outerElements} list.
     */
    private XMLEvent nextEvent;

    /**
     * Creates a new reader for the given version of the standards.
     */
    TransformingReader(final XMLEventReader in) {
        this.in = in;
    }

    /**
     * Checks if there are more events.
     */
    @Override
    public boolean hasNext() {
        return (nextEvent != null) || in.hasNext();
    }

    /**
     * Checks the next {@code XMLEvent} without removing it from the stream.
     */
    @Override
    public XMLEvent peek() throws XMLStreamException {
        if (nextEvent == null) {
            final XMLEvent event = in.peek();
            if (event != null) {
                nextEvent = convert(event);
            }
        }
        return nextEvent;
    }

    /**
     * Returns the next element. Use {@link #nextEvent()} instead.
     */
    @Override
    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * Forwards the call and keep trace of the XML elements opened up to this point.
     */
    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        final XMLEvent event = in.nextEvent();
        final XMLEvent next  = nextEvent;
        if (next != null) {
            nextEvent = null;
            return next;
        }
        return convert(event);
    }

    /**
     * Forwards the call and keep trace of the XML elements opened up to this point.
     */
    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        final XMLEvent event = in.nextTag();
        final XMLEvent next  = nextEvent;
        if (next != null) {
            nextEvent = null;
            switch (event.getEventType()) {
                case START_ELEMENT:
                case END_ELEMENT: {
                    return event;
                }
            }
        }
        return convert(event);
    }

    /**
     * Keeps trace of XML elements opened up to this point and imports the given event.
     * This method replaces the namespaces used in XML document by the namespace used by JAXB annotations.
     * It is caller's responsibility to ensure that this method is invoked exactly once for each element,
     * or at least for each {@code START_ELEMENT} and {@code END_ELEMENT}.
     *
     * @param  event  the event read from the underlying event reader.
     * @return the converted event (may be the same instance).
     */
    @SuppressWarnings("unchecked")      // TODO: remove on JDK9
    private XMLEvent convert(XMLEvent event) throws XMLStreamException {
        switch (event.getEventType()) {
//            case ATTRIBUTE: {
//                event = convert((Attribute)event);
//                break;
//            }
            case NAMESPACE: {
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

        return event;
    }

    private Attribute convert(Attribute event) {
        final QName originName = event.getName();
        final String ns = originName.getNamespaceURI();
        final String newNS = importNS(ns);
        if (!Objects.equals(newNS, ns)) {
            final QName name = new QName(newNS, originName.getLocalPart(), originName.getPrefix());
            eventFactory.setLocation(event.getLocation());
            event = eventFactory.createAttribute(name, event.getValue());
        }
        return event;
    }

    private Namespace convert(Namespace event) {
        final String newNS = importNS(event.getNamespaceURI());
        if (!Objects.equals(newNS, event.getNamespaceURI())) {
            eventFactory.setLocation(event.getLocation());
            event = eventFactory.createNamespace(event.getPrefix(), newNS);
        }
        return event;
    }

    private EndElement convert(EndElement event) {
        final QName originalName = event.getName();

        final QName newName = importName(originalName);
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

        final QName newName = importName(originalName);
        if (!Objects.equals(newName, originalName)) {
            eventFactory.setLocation(event.getLocation());
            // HACK : to manage processVersion attribute, we have to convert
            // attributes of the modified WPS 1 start element.
            final Spliterator attrSplit = Spliterators.spliteratorUnknownSize(event.getAttributes(), Spliterator.DISTINCT + Spliterator.IMMUTABLE);
            Iterator<Attribute> modifiedAttrs = StreamSupport.stream(attrSplit, false)
                    .map(evt -> convert((Attribute)evt))
                    .iterator();
            event = eventFactory.createStartElement(newName, modifiedAttrs, event.getNamespaces());
        }

        context.add(originalLocalName);

        return event;
    }

    /**
     * Reads the content of a text-only element. Forwards from the underlying reader as-is.
     *
     * @todo Untested. In particular, it is not clear how to update {@code outerElements}.
     *       By chance, JAXB does not seem to invoke this method.
     */
    @Override
    public String getElementText() throws XMLStreamException {
        return in.getElementText();
    }

    /**
     * Get the value of a feature/property from the underlying implementation.
     */
    @Override
    public Object getProperty​(final String name) {
        return in.getProperty(name);
    }

    /**
     * Frees any resources associated with this reader.
     * This method does not close the underlying input source.
     */
    @Override
    public void close() throws XMLStreamException {
        in.close();
    }
}
