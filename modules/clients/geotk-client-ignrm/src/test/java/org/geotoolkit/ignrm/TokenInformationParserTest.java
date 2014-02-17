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
package org.geotoolkit.ignrm;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.ignrm.parser.TokenInformationParser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TokenInformationParserTest {
    
    @Test
    public void testReader() throws XMLStreamException, IOException{
        
        final TokenInformationParser parser = new TokenInformationParser();
        parser.setInput(TokenParserTest.class.getResource("/org/geotoolkit/ignrm/config.xml"));
        final TokenInformation token = parser.read();

        assertEquals(1200,token.getTokenTimeOut());
        
    }
    
}
