/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ignrm.parser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.ignrm.TokenInformation;
import org.geotoolkit.xml.StaxStreamReader;

/**
 * Stax parser to read token information from a GetConfig request.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TokenInformationParser extends StaxStreamReader {

    private static final String TAG_CONFIG = "config";
    private static final String TAG_TIMEOUT = "tokenTimeOut";

    public TokenInformationParser() {
    }
    
    public TokenInformation read() throws XMLStreamException {
        while (reader.hasNext()) {
            final int type = reader.next();
            if (type == XMLStreamReader.START_ELEMENT
                    && reader.getLocalName().equalsIgnoreCase(TAG_CONFIG)) {
                return readInformation();
            }
        }

        throw new XMLStreamException("config tag not found");
    }

    private TokenInformation readInformation() throws XMLStreamException {

        long timeout = 600;
        while (reader.hasNext()) {
            final int type = reader.next();
            if (type == XMLStreamReader.START_ELEMENT
                    && reader.getLocalName().equalsIgnoreCase(TAG_TIMEOUT)) {
                timeout = (long) parseDouble(reader.getElementText());
                toTagEnd(TAG_CONFIG);
            }
        }
        
        
        return new TokenInformation(timeout);
    }
    
}
