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
import org.geotoolkit.ignrm.parser.TokenParser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TokenParserTest {
    
    @Test
    public void testReader() throws XMLStreamException, IOException{
        
        final TokenParser parser = new TokenParser(null, null);
        parser.setInput(TokenParserTest.class.getResource("/org/geotoolkit/ignrm/token.xml"));
        final Token token = parser.read();

        assertEquals("gppkey",token.getName());
        assertEquals("gC2UZGUr_JJi5Nm7QKN5hQG06pEAAAAAAAH",token.value);
    }
       
}
