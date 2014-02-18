/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.metadata.cbers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.metadata.Metadata;
import org.xml.sax.SAXException;

/**
 * Test read of CBERS xml nodes.
 * 
 * @author Alexis Manin (Geomatys)
 * @module pending
 */
public class CBERSTest {
    
    @Test
    public void CBERSTest() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {

        final File f = new File(CBERSTest.class.getResource("/org/geotoolkit/metadata/cbers/CBERS_2B_CCD1XS_20080708_094_109_L2_BAND2.xml").toURI());
        final Metadata meta = CBERS.toMetadata(f);
        
        assertNotNull(meta);
        System.out.println(meta.toString());
    }
    
}
