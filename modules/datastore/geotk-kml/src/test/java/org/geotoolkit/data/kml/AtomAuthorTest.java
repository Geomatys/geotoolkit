package org.geotoolkit.data.kml;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.model.AtomFactory;
import org.geotoolkit.data.model.AtomFactoryDefault;
import org.geotoolkit.data.model.AtomModelConstants;
import org.geotoolkit.data.model.atom.AtomEmail;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
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
public class AtomAuthorTest {

    public AtomAuthorTest() {
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
     public void atomAuthorReadTest() throws IOException, XMLStreamException {
        final AtomReader reader = new AtomReader();
        reader.setInput(new File("src/test/resources/org/geotoolkit/data/kml/author.atom"));
        final AtomPersonConstruct author = reader.readAuthor();
        reader.dispose();

        final List<Object> params = author.getParams();

        assertEquals(params.get(0),"Samuel");
        assertEquals(params.get(1).toString(),"c:est:une:uri");
        assertEquals(params.get(2),"Prénom Nom");
        assertEquals(((AtomEmail) params.get(3)).getAddress(),"mon.email@serveur.net");
     }

     @Test
     public void atomAuthorWriteTest() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        final AtomFactory atomFactory = new AtomFactoryDefault();

        final List<Object> params = new ArrayList<Object>();
        params.add("Samuel");
        params.add(URI.create("c:est:une:uri"));
        params.add("Prénom Nom");
        params.add(atomFactory.createAtomEmail("mon.email@serveur.net"));
        
        AtomPersonConstruct author = atomFactory.createAtomPersonConstruct(params);

        File temp = File.createTempFile("testAtomAuthor",".atom");
        temp.deleteOnExit();

        AtomWriter writer = new AtomWriter();
        writer.setOutput(temp);
        writer.getWriter().writeStartDocument("UTF-8", "1.0");
        writer.getWriter().setDefaultNamespace(AtomModelConstants.URI_ATOM);

        writer.writeAuthor(author);

        writer.getWriter().writeEndDocument();
        writer.getWriter().flush();
        writer.dispose();

        DomCompare.compare(
                new File("src/test/resources/org/geotoolkit/data/kml/author.atom"), temp);
     }

}