/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.atom.xml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.geotoolkit.atom.AtomFactory;
import org.geotoolkit.atom.DefaultAtomFactory;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.atom.model.AtomPersonConstruct;
import org.geotoolkit.xml.StaxStreamReader;

import static org.geotoolkit.atom.xml.AtomConstants.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class AtomReader extends StaxStreamReader {

    private static AtomFactory ATOM_FACTORY;

    public AtomReader() {
        ATOM_FACTORY = DefaultAtomFactory.getInstance();
    }

    public AtomReader(AtomFactory atomFactory){
        ATOM_FACTORY = atomFactory;
    }

    public XMLStreamReader getReader() {
        return this.reader;
    }

    /**
     *
     * @return
     * @throws XMLStreamException
     */
    public AtomPersonConstruct readAuthor() throws XMLStreamException {
        final List<Object> params = new ArrayList<Object>();

        boucle:
        while (reader.hasNext()) {

            switch (reader.next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getLocalName();
                    final String eUri = reader.getNamespaceURI();

                    if (URI_ATOM.equals(eUri)) {
                        if (TAG_NAME.equals(eName)) {
                            params.add(reader.getElementText());
                        } else if (TAG_URI.equals(eName)) {
                            params.add(URI.create(reader.getElementText()));
                        } else if (TAG_EMAIL.equals(eName)) {
                            params.add(ATOM_FACTORY.createAtomEmail(reader.getElementText()));
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (TAG_AUTHOR.equals(reader.getLocalName())
                            && URI_ATOM.contains(reader.getNamespaceURI())) {
                        break boucle;
                    }
                    break;
            }

        }

        return AtomReader.ATOM_FACTORY.createAtomPersonConstruct(params);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public AtomLink readLink() throws XMLStreamException {
        final String href       = reader.getAttributeValue(null, ATT_HREF);
        final String rel        = reader.getAttributeValue(null, ATT_REL);
        final String type       = reader.getAttributeValue(null, ATT_TYPE);
        final String hreflang   = reader.getAttributeValue(null, ATT_HREFLANG);
        final String title      = reader.getAttributeValue(null, ATT_TITLE);
        final String length     = reader.getAttributeValue(null, ATT_LENGTH);

        return AtomReader.ATOM_FACTORY.createAtomLink(
                href, rel, type, hreflang, title, length);
    }
}
