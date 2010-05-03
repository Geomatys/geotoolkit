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
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MockReader extends StaxStreamReader{

    private static final String TAG_PERSON = "person";
    private static final String TAG_NAME = "name";
    
    public Person read() throws XMLStreamException{
        while(reader.hasNext()){
            final int type = reader.next();
            if(type == XMLStreamReader.START_ELEMENT &&
                    reader.getLocalName().equalsIgnoreCase(TAG_PERSON)){
                return readPerson();
            }
        }
        
        throw new XMLStreamException("Person tag not found");
    }

    private Person readPerson() throws XMLStreamException{
        while(reader.hasNext()){
            final int type = reader.next();
            if(type == XMLStreamReader.START_ELEMENT &&
                    reader.getLocalName().equalsIgnoreCase(TAG_NAME)){
                Person person = new Person();
                person.name = reader.getElementText();
                toTagEnd(TAG_PERSON);
                return person;
            }
        }

        throw new XMLStreamException("Name tag not found");
    }

    public static final class Person{
        public String name;
    }

}
