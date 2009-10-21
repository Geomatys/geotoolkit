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

import org.geotoolkit.skos.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

//Junit dependencies
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.xml.MarshallerPool;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class SkosXmlBindingTest {

    private Logger       logger = Logging.getLogger("org.geotoolkit.skos");

    private MarshallerPool pool;
    private Unmarshaller unmarshaller;
    private Marshaller   marshaller;

    @Before
    public void setUp() throws JAXBException {
        pool = new MarshallerPool("org.geotoolkit.skos.xml");
        marshaller = pool.acquireMarshaller();
        unmarshaller = pool.acquireUnmarshaller();
    }

    @After
    public void tearDown() {
        if (marshaller != null) {
            pool.release(marshaller);
        }
        if (unmarshaller != null) {
            pool.release(unmarshaller);
        }
    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RDFMarshalingTest() throws JAXBException {
        List<Concept> concepts = new ArrayList<Concept>();
        Concept c1 = new Concept("http://www.geomatys.com/test/bonjour", null, "bonjour", "salut", "Un terme de politesse pour saluer son interlocuteur.", null);
        Concept c2 = new Concept("http://www.geomatys.com/test/pluie", null, "pluie", Arrays.asList("averse", "précipitation"), "Un evenement meteorologique qui fais tomber de l'eau sur la terre.", null);
        Concept c3 = new Concept("http://www.geomatys.com/test/livre", null, "livre", Arrays.asList("bouquin", "ouvrage"), "Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.", null);
        concepts.add(c1);
        concepts.add(c2);
        concepts.add(c3);
        RDF rdf = new RDF(concepts);

        StringWriter sw = new StringWriter();
        marshaller.marshal(rdf, sw);

        String result = sw.toString();
        //we remove the xmlmns
        result = removeXmlns(result);

        String expResult =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                            + '\n' +
        "<rdf:RDF >"                                                                                                               + '\n' +
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

        logger.finer("result" + result);
        logger.finer("expected" + expResult);
        assertEquals(expResult, result);

    }

    /**
     *
     * @throws java.lang.Exception
     */
    @Test
    public void RDFUnMarshalingTest() throws JAXBException {

        String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"                                                            + '\n' +
        "<rdf:RDF xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"                                                                                                               + '\n' +
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
        Concept c2 = new Concept("http://www.geomatys.com/test/pluie", null, "pluie", Arrays.asList("averse", "précipitation"), "Un evenement meteorologique qui fais tomber de l'eau sur la terre.", null);
        Concept c3 = new Concept("http://www.geomatys.com/test/livre", null, "livre", Arrays.asList("bouquin", "ouvrage"), "Une reliure de papier avec des chose plus ou moins interesante ecrite dessus.", null);
        concepts.add(c1);
        concepts.add(c2);
        concepts.add(c3);
        RDF expResult = new RDF(concepts);

        assertEquals(expResult.getConcept(), result.getConcept());
        assertEquals(expResult, result);
    }

    private String removeXmlns(String xml) {

        String s = xml;
        s = s.replaceAll("xmlns=\"[^\"]*\" ", "");

        s = s.replaceAll("xmlns=\"[^\"]*\"", "");

        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\" ", "");

        s = s.replaceAll("xmlns:[^=]*=\"[^\"]*\"", "");


        return s;
    }
}
