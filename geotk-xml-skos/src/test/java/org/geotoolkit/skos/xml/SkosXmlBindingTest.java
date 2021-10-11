/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.skos.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Junit dependencies
import org.apache.sis.util.logging.Logging;
import org.apache.sis.xml.MarshallerPool;
import org.junit.*;

import static org.apache.sis.test.MetadataAssert.*;


/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module
 */
public class SkosXmlBindingTest extends org.geotoolkit.test.TestBase {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.skos");

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool(JAXBContext.newInstance("org.geotoolkit.skos.xml:org.apache.sis.internal.jaxb.geometry"), null);
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (marshaller != null) {
            pool.recycle(marshaller);
        }
        if (unmarshaller != null) {
            pool.recycle(unmarshaller);
        }
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RDFMarshalingTest() throws JAXBException, Exception {
        List<Concept> concepts = new ArrayList<Concept>();
        Concept c1 = new Concept("http://www.geomatys.com/test/bonjour", null, "bonjour", "salut", "Un terme de politesse pour saluer son interlocuteur.", null);
        Concept c2 = new Concept("http://www.geomatys.com/test/pluie", null, new Value("pluie"), Arrays.asList(new Value("averse"), new Value("précipitation")), new Value("Un evenement meteorologique qui fais tomber de l'eau sur la terre."), null);
        Concept c3 = new Concept("http://www.geomatys.com/test/livre", null, new Value("livre"), Arrays.asList(new Value("bouquin"), new Value("ouvrage")), new Value("Une reliure de papier avec des chose plus ou moins interesante ecrite dessus."), null);
        concepts.add(c1);
        concepts.add(c2);
        concepts.add(c3);
        RDF rdf = new RDF(concepts);

        StringWriter sw = new StringWriter();
        marshaller.marshal(rdf, sw);

        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                            + '\n' +
        "<rdf:RDF xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"  + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/bonjour\">"                                                    + '\n' +
        "        <skos:prefLabel>bonjour</skos:prefLabel>"                                                                         + '\n' +
        "        <skos:altLabel>salut</skos:altLabel>"                                                                             + '\n' +
        "        <skos:definition>Un terme de politesse pour saluer son interlocuteur.</skos:definition>"                          + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/pluie\">"                                                      + '\n' +
        "        <skos:prefLabel>pluie</skos:prefLabel>"                                                                           + '\n' +
        "        <skos:altLabel>averse</skos:altLabel>"                                                                            + '\n' +
        "        <skos:altLabel>précipitation</skos:altLabel>"                                                                     + '\n' +
        "        <skos:definition>Un evenement meteorologique qui fais tomber de l'eau sur la terre.</skos:definition>"            + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/livre\">"                                                      + '\n' +
        "        <skos:prefLabel>livre</skos:prefLabel>"                                                                           + '\n' +
        "        <skos:altLabel>bouquin</skos:altLabel>"                                                                           + '\n' +
        "        <skos:altLabel>ouvrage</skos:altLabel>"                                                                           + '\n' +
        "        <skos:definition>Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.</skos:definition>" + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "</rdf:RDF>"                                                                                                               + '\n';

        LOGGER.finer("result" + result);
        LOGGER.finer("expected" + expResult);
        assertXmlEquals(expResult, result, "xmlns:*");

    }

    @Test
    public void RDFMultilangMarshalingTest() throws JAXBException, Exception {
        List<Concept> concepts = new ArrayList<Concept>();
        Concept c1 = new Concept("http://www.geomatys.com/test/bonjour", null, new Value("bonjour", "fr"), new Value("salut", "fr"), new Value("Un terme de politesse pour saluer son interlocuteur.", "fr"), null);
        Concept c2 = new Concept("http://www.geomatys.com/test/pluie", null, Arrays.asList(new Value("pluie", "fr"), new Value("rain", "en")), Arrays.asList(new Value("averse", "fr"), new Value("précipitation", "fr")), Arrays.asList(new Value("Un evenement meteorologique qui fais tomber de l'eau sur la terre.", "fr"), new Value("water falling from sky", "en")), null);
        Concept c3 = new Concept("http://www.geomatys.com/test/livre", null, new Value("livre", "fr"), Arrays.asList(new Value("bouquin", "fr"), new Value("ouvrage", "fr")), new Value("Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.", "fr"), null);
        concepts.add(c1);
        concepts.add(c2);
        concepts.add(c3);
        RDF rdf = new RDF(concepts);

        StringWriter sw = new StringWriter();
        marshaller.marshal(rdf, sw);

        String result = sw.toString();

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                            + '\n' +
        "<rdf:RDF xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"  + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/bonjour\">"                                                    + '\n' +
        "        <skos:prefLabel xml:lang=\"fr\">bonjour</skos:prefLabel>"                                                         + '\n' +
        "        <skos:altLabel xml:lang=\"fr\">salut</skos:altLabel>"                                                             + '\n' +
        "        <skos:definition xml:lang=\"fr\">Un terme de politesse pour saluer son interlocuteur.</skos:definition>"          + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/pluie\">"                                                      + '\n' +
        "        <skos:prefLabel xml:lang=\"fr\">pluie</skos:prefLabel>"                                                           + '\n' +
        "        <skos:prefLabel xml:lang=\"en\">rain</skos:prefLabel>"                                                            + '\n' +
        "        <skos:altLabel xml:lang=\"fr\">averse</skos:altLabel>"                                                            + '\n' +
        "        <skos:altLabel xml:lang=\"fr\">précipitation</skos:altLabel>"                                                     + '\n' +
        "        <skos:definition xml:lang=\"fr\">Un evenement meteorologique qui fais tomber de l'eau sur la terre.</skos:definition>" + '\n' +
        "        <skos:definition xml:lang=\"en\">water falling from sky</skos:definition>"                                        + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/livre\">"                                                      + '\n' +
        "        <skos:prefLabel xml:lang=\"fr\">livre</skos:prefLabel>"                                                           + '\n' +
        "        <skos:altLabel xml:lang=\"fr\">bouquin</skos:altLabel>"                                                           + '\n' +
        "        <skos:altLabel xml:lang=\"fr\">ouvrage</skos:altLabel>"                                                           + '\n' +
        "        <skos:definition xml:lang=\"fr\">Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.</skos:definition>" + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "</rdf:RDF>"                                                                                                               + '\n';

        LOGGER.finer("result" + result);
        LOGGER.finer("expected" + expResult);
        assertXmlEquals(expResult, result, "xmlns:*");

    }


    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RDFUnMarshalingTest() throws JAXBException {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                            + '\n' +
        "<rdf:RDF xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"  + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/bonjour\">"                                                    + '\n' +
        "        <skos:prefLabel>bonjour</skos:prefLabel>"                                                                         + '\n' +
        "        <skos:altLabel>salut</skos:altLabel>"                                                                             + '\n' +
        "        <skos:definition>Un terme de politesse pour saluer son interlocuteur.</skos:definition>"                          + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/pluie\">"                                                      + '\n' +
        "        <skos:prefLabel>pluie</skos:prefLabel>"                                                                           + '\n' +
        "        <skos:altLabel>averse</skos:altLabel>"                                                                            + '\n' +
        "        <skos:altLabel>précipitation</skos:altLabel>"                                                                     + '\n' +
        "        <skos:definition>Un evenement meteorologique qui fais tomber de l'eau sur la terre.</skos:definition>"            + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "    <skos:Concept rdf:about=\"http://www.geomatys.com/test/livre\">"                                                      + '\n' +
        "        <skos:prefLabel>livre</skos:prefLabel>"                                                                           + '\n' +
        "        <skos:altLabel>bouquin</skos:altLabel>"                                                                           + '\n' +
        "        <skos:altLabel>ouvrage</skos:altLabel>"                                                                           + '\n' +
        "        <skos:definition>Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.</skos:definition>" + '\n' +
        "    </skos:Concept>"                                                                                                      + '\n' +
        "</rdf:RDF>";

        StringReader sr = new StringReader(xml);

        Object unmarshalled = unmarshaller.unmarshal(sr);

        assertTrue(unmarshalled instanceof RDF);

        RDF result = (RDF) unmarshalled;

        List<Concept> concepts = new ArrayList<Concept>();
        Concept c1 = new Concept("http://www.geomatys.com/test/bonjour", null, "bonjour", "salut", "Un terme de politesse pour saluer son interlocuteur.", null);
        Concept c2 = new Concept("http://www.geomatys.com/test/pluie", null, new Value("pluie"), Arrays.asList(new Value("averse"), new Value("précipitation")), new Value("Un evenement meteorologique qui fais tomber de l'eau sur la terre."), null);
        Concept c3 = new Concept("http://www.geomatys.com/test/livre", null, new Value("livre"), Arrays.asList(new Value("bouquin"), new Value("ouvrage")), new Value("Une reliure de papier avec des chose plus ou moins interesante ecrite dessus."), null);
        concepts.add(c1);
        concepts.add(c2);
        concepts.add(c3);
        RDF expResult = new RDF(concepts);

        assertEquals(expResult.getConcept(), result.getConcept());
        assertEquals(expResult, result);
    }
}
