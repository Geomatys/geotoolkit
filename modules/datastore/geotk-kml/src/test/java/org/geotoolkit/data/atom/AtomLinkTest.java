/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.data.atom;

import org.geotoolkit.data.atom.xml.AtomReader;
import org.geotoolkit.data.atom.xml.AtomWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.atom.AtomFactory;
import org.geotoolkit.data.atom.DefaultAtomFactory;
import org.geotoolkit.data.atom.xml.AtomModelConstants;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
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
 * @author samuel
 */
public class AtomLinkTest {

    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/atom/link.atom";

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

                    if (AtomModelConstants.URI_ATOM.equals(eUri)) {
                        if (AtomModelConstants.TAG_LINK.equals(eName)) {
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
        final AtomFactory atomFactory = new DefaultAtomFactory();

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
         System.out.println("WRITER : "+writer.getWriter());
        writer.getWriter().writeStartDocument("UTF-8", "1.0");
        writer.getWriter().setDefaultNamespace(AtomModelConstants.URI_ATOM);

        writer.writeLink(link);

        writer.getWriter().writeEndDocument();
        writer.getWriter().flush();
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

     }


}