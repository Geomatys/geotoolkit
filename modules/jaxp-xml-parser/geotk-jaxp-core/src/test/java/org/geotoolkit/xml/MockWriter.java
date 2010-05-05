/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.xml;

import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MockWriter extends StaxStreamWriter{

    public void write() throws XMLStreamException{
        final String namespace = "http://www.sample.net/person";
        writer.setPrefix("prs", namespace);
        writer.setDefaultNamespace(namespace);

        writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement(namespace, "Person");
                writer.writeDefaultNamespace(namespace);            
                writeSimpleTag(namespace, "Name", "Jean-Pierre");
            writer.writeEndElement();
        writer.writeEndDocument();
    }

}
