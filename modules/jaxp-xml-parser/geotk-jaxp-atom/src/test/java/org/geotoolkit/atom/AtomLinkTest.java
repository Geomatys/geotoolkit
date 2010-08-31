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
package org.geotoolkit.atom;

import org.geotoolkit.atom.xml.AtomReader;
import org.geotoolkit.atom.xml.AtomWriter;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.atom.xml.AtomConstants;
import org.geotoolkit.atom.model.AtomLink;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class AtomLinkTest {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/atom/link.atom";

    public AtomLinkTest() {
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
    public void atomLinkReadTest() throws IOException, XMLStreamException {
        final AtomReader reader = new AtomReader();
        reader.setInput(new File(pathToTestFile));
        AtomLink link = null;
        boucle:
        while (reader.getReader().hasNext()) {

            switch (reader.getReader().next()) {
                case XMLStreamConstants.START_ELEMENT:
                    final String eName = reader.getReader().getLocalName();
                    final String eUri = reader.getReader().getNamespaceURI();

                    if (AtomConstants.URI_ATOM.equals(eUri)) {
                        if (AtomConstants.TAG_LINK.equals(eName)) {
                            link= reader.readLink();
                        }
                    }
            }
        }
        reader.dispose();

        assertEquals(link.getHref(),"HREF");
        assertEquals(link.getRel(),"REL");
        assertEquals(link.getType(),"TYPE/TYPE");
        assertEquals(link.getHreflang(),"HREFLANG-hqls5");
        assertEquals(link.getTitle(),"TITLE");
        assertEquals(link.getLength(),"LENGTH");
     
     }

     @Test
     public void atomLinkWriteTest() throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
        final AtomFactory atomFactory = DefaultAtomFactory.getInstance();

        final AtomLink link = atomFactory.createAtomLink();
        link.setHref("HREF");
        link.setRel("REL");
        link.setType("TYPE/TYPE");
        link.setHreflang("HREFLANG-hqls5");
        link.setTitle("TITLE");
        link.setLength("LENGTH");

        File temp = File.createTempFile("link",".atom");
        temp.deleteOnExit();

        AtomWriter writer = new AtomWriter();
        writer.setOutput(temp);
        writer.getWriter().writeStartDocument("UTF-8", "1.0");
        writer.getWriter().setDefaultNamespace(AtomConstants.URI_ATOM);

        writer.writeLink(link);

        writer.getWriter().writeEndDocument();
        writer.getWriter().flush();
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

     }


}