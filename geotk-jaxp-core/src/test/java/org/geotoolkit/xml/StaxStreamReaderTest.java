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

import com.ctc.wstx.stax.WstxInputFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.geotoolkit.xml.MockReader.Person;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StaxStreamReaderTest extends org.geotoolkit.test.TestBase {

    public StaxStreamReaderTest() {
    }

    @Test
    public void testReadingFromFile() throws Exception {
        final MockReader instance = new MockReader();
        instance.setInput(new File("src/test/resources/org/geotoolkit/xml/sample.xml"));
        validate(instance.read());
        instance.dispose();
    }

    @Test
    public void testReadingFromInputStream() throws Exception {
        final MockReader instance = new MockReader();
        instance.setInput(StaxStreamReaderTest.class.getResourceAsStream("/org/geotoolkit/xml/sample.xml"));
        validate(instance.read());
        instance.dispose();
    }

    @Test
    public void testReadingFromURL() throws Exception {
        final MockReader instance = new MockReader();
        instance.setInput(StaxStreamReaderTest.class.getResource("/org/geotoolkit/xml/sample.xml"));
        validate(instance.read());
        instance.dispose();
    }

    @Test
    public void testReadingFromURI() throws Exception {
        final MockReader instance = new MockReader();
        instance.setInput(StaxStreamReaderTest.class.getResource("/org/geotoolkit/xml/sample.xml").toURI());
        validate(instance.read());
        instance.dispose();
    }

    @Test
    public void testReadingFromStaxReader() throws Exception {
        final XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = XMLfactory.createXMLStreamReader(
                StaxStreamReaderTest.class.getResourceAsStream("/org/geotoolkit/xml/sample.xml"));

        final MockReader instance = new MockReader();
        instance.setInput(reader);
        validate(instance.read());
        instance.dispose();
    }

    @Test
    public void testReadingFromDom() throws Exception {

        final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        final Document document = constructeur.parse(StaxStreamReaderTest.class.getResourceAsStream("/org/geotoolkit/xml/sample.xml"));
        final Source src = new DOMSource(document);

        //this test requiere and advanced Stax library, here we use WoodStox stream reader.
        final XMLInputFactory XMLfactory = new WstxInputFactory();
        final XMLStreamReader reader = XMLfactory.createXMLStreamReader(src);

        final MockReader instance = new MockReader();
        instance.setInput(reader);
        validate(instance.read());
        instance.dispose();
    }


    public static void validate(final Person person){
        assertNotNull(person);
        assertEquals(person.name, "Jean-Pierre");
        assertEquals(person.age, 13.5, 0.000000001);
        assertEquals(person.male, true);
    }

}
