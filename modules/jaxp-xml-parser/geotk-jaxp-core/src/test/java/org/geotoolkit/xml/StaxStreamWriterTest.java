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

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import java.beans.Expression;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import org.geotoolkit.util.DomUtilities;

import org.geotoolkit.xml.MockReader.Person;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaxStreamWriterTest {

    public StaxStreamWriterTest() {
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
    public void testWritingToFile() throws Exception {
        final File file = new File("src/test/resources/org/geotoolkit/xml/sampleOutput.xml");
        if(file.exists()) file.delete();

        final MockWriter instance = new MockWriter();
        instance.setOutput(file);
        instance.write();
        instance.dispose();

        validate(file);
        if(file.exists()) file.delete();
    }

    @Test
    public void testWritingToOutputStream() throws Exception {
        final File file = new File("src/test/resources/org/geotoolkit/xml/sampleOutput.xml");
        if(file.exists()) file.delete();

        final MockWriter instance = new MockWriter();
        instance.setOutput(new FileOutputStream(file));
        instance.write();
        instance.dispose();

        validate(file);
        if(file.exists()) file.delete();
    }
    
    @Test
    public void testWritingToDom() throws Exception {
        //this test requiere and advanced Stax library, here we use WoodStox stream reader.
        final DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        final DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        final Document document = constructeur.newDocument();

        final File file = new File("src/test/resources/org/geotoolkit/xml/sampleOutput.xml");
        if(file.exists()) file.delete();

        final Result res = new DOMResult(document);
        
        final MockWriter instance = new MockWriter();
        instance.setOutput(res);
        instance.write();
        instance.dispose();

        
        //check by reading it back
        final Source src = new DOMSource(document);
        
        final XMLInputFactory XMLfactory = XMLInputFactory.newInstance();
        final XMLStreamReader reader = XMLfactory.createXMLStreamReader(src);

        final MockReader mr = new MockReader();
        mr.setInput(reader);
        StaxStreamReaderTest.validate(mr.read());
        mr.dispose();
    }
    
    @Test
    public void testWritingFromStaxWriter() throws Exception {
        final File file = new File("src/test/resources/org/geotoolkit/xml/sampleOutput.xml");
        if(file.exists()) file.delete();

        final MockWriter instance = new MockWriter();

        final XMLOutputFactory XMLfactory = XMLOutputFactory.newInstance();
        final XMLStreamWriter writer = XMLfactory.createXMLStreamWriter(new FileOutputStream(file));
        instance.setOutput(writer);
        instance.write();
        instance.dispose();

        validate(file);
        if(file.exists()) file.delete();
    }

    private void validate(final File f) throws Exception{
        final MockReader instance = new MockReader();
        instance.setInput(f);
        Person person = instance.read();
        assertNotNull(person);
        assertEquals(person.name, "Jean-Pierre");
        instance.dispose();
    }

}
