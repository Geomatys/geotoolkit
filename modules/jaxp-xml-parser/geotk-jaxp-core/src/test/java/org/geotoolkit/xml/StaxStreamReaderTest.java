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

import java.io.File;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.geotoolkit.xml.MockReader.Person;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sorel
 */
public class StaxStreamReaderTest {

    public StaxStreamReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
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
        final XMLInputFactory XMLfactory = XMLInputFactory.newFactory();
        final XMLStreamReader reader = XMLfactory.createXMLStreamReader(
                StaxStreamReaderTest.class.getResourceAsStream("/org/geotoolkit/xml/sample.xml"));

        final MockReader instance = new MockReader();
        instance.setInput(reader);
        validate(instance.read());
        instance.dispose();
    }

    private void validate(Person person){
        assertNotNull(person);
        assertEquals(person.name, "Jean-Pierre");
    }

}