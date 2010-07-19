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
package org.geotoolkit.data.atom.xml;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.data.atom.AtomFactory;
import org.geotoolkit.data.atom.DefaultAtomFactory;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.xml.StaxStreamReader;
import static org.geotoolkit.data.atom.xml.AtomConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class AtomReader extends StaxStreamReader {

    private static final AtomFactory atomFactory = new DefaultAtomFactory();

    public AtomReader() {
        super();
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
        List<Object> params = new ArrayList<Object>();

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
                            params.add(atomFactory.createAtomEmail(reader.getElementText()));
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

        return AtomReader.atomFactory.createAtomPersonConstruct(params);
    }

    /**
     * 
     * @return
     * @throws XMLStreamException
     */
    public AtomLink readLink() throws XMLStreamException {
        String href = reader.getAttributeValue(null, ATT_HREF);
        String rel = reader.getAttributeValue(null, ATT_REL);
        String type = reader.getAttributeValue(null, ATT_TYPE);
        String hreflang = reader.getAttributeValue(null, ATT_HREFLANG);
        String title = reader.getAttributeValue(null, ATT_TITLE);
        String length = reader.getAttributeValue(null, ATT_LENGTH);

        return AtomReader.atomFactory.createAtomLink(
                href, rel, type, hreflang, title, length);
    }
}
