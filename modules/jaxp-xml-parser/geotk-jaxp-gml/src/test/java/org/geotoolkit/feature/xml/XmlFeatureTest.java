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

package org.geotoolkit.feature.xml;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.io.FileInputStream;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.xml.DomCompare;

import org.junit.*;

import org.opengis.filter.sort.SortOrder;

import static org.junit.Assert.*;
import static org.geotoolkit.feature.xml.XmlTestData.*;
import org.geotoolkit.data.AbstractFeatureCollection;
import org.geotoolkit.feature.xml.jaxp.ElementFeatureWriter;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.DefaultPropertyName;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class XmlFeatureTest extends org.geotoolkit.test.TestBase {

    @Test
    public void testReadSimpleFeature() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeFull);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);

        Feature result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(simpleTypeFull);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);
    }

    @Test
    public void testReadSimpleFeatureWithAtts() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(typeWithAtts);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureWithAttribute.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);
        Feature result = (Feature) obj;
        assertEquals(featureWithAttributes, result);
    }

    @Test
    public void testReadSimpleFeatureEmpty() throws JAXBException, IOException, XMLStreamException{
        final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader(typeEmpty2);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureEmpty.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);
        Feature result = (Feature) obj;
        assertEquals(featureEmpty, result);
    }

    @Test
    public void testReadSimpleFeatureOldEnc() throws JAXBException, IOException, XMLStreamException{

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(simpleTypeFull);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        Object obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc.xml"));

        assertTrue(obj instanceof Feature);

        Feature result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        obj = readerGml.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc2.xml"));

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = readerGml.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc3.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        /*
         * Not working with JTSWrapper binding mode for JAXP Feature Writer
         *
         * working for Polygon
         * working for LineString
         * not for point
         */

        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeFull);
        reader.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "JTSWrapper");
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc.xml"));

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc2.xml"));

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertEquals(simpleFeatureFull, result);

        // adding lineString encoding
        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureOldEnc3.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);

        result = (Feature) obj;
        assertFalse(simpleFeatureFull.equals(result));
    }

    @Test
    public void testWriteSimpleFeature() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        writer.write(simpleFeatureFull, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testWriteSimpleFeature321() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.1.0", null);
        writer.write(simpleFeatureFull, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeature321.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        org.apache.sis.test.Assert.assertXmlEquals(expResult, result,
                "http://www.w3.org/2000/xmlns:*",
                "http://www.w3.org/2001/XMLSchema-instance:schemaLocation"
        );
    }

    @Test
    public void testWriteSimpleFeatureWithAtts() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.1.0", null);
        writer.write(featureWithAttributes, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureWithAttribute.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testWriteSimpleFeatureWithObjectProperty() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.1.0", null);
        writer.write(featureWithObject, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureWithObjectProperty.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testWriteSimpleFeaturePrimitiveWithAtts() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{

        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.1.0", null);
        writer.write(featureEmpty, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureEmpty.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");

        final Expression exp = new DefaultPropertyName("/identifier/_value");
        Object v = exp.evaluate(featureEmpty);
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testWriteFeatureWithNil() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{

        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.1.0", null);
        writer.write(featureNil, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/FeatureWithNil.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testWriteSimpleFeatureElement() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final ElementFeatureWriter writer = new ElementFeatureWriter();
        Element r = writer.write(simpleFeatureFull, false);

        Source source = new DOMSource(r.getOwnerDocument());

        // Prepare the output file
        Result resultxml = new StreamResult(temp);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, resultxml);


        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/SimpleFeatureDom.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testReadSimpleCollection() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(simpleTypeBasic);
        final Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimple.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        FeatureCollection result = (FeatureCollection) obj;
        try {
            String id = result.getID();
            result = result.subCollection(QueryBuilder.sorted(
                    result.getFeatureType().getName().toString(), FF.sort("attDouble", SortOrder.ASCENDING)));
            ((AbstractFeatureCollection)result).setId(id);
        } catch (DataStoreException ex) {
            Logging.getLogger("org.geotoolkit.feature.xml").log(Level.SEVERE, null, ex);
        }

        FeatureIterator resultIte = result.iterator();
        FeatureIterator expectedIte = collectionSimple.iterator();

        assertEquals(collectionSimple.size(), result.size());
        assertEquals(collectionSimple.getID(), result.getID());
        assertEquals(collectionSimple.getFeatureType(), result.getFeatureType());

        assertEquals(resultIte.next(), expectedIte.next());
        assertEquals(resultIte.next(), expectedIte.next());
        assertEquals(resultIte.next(), expectedIte.next());
        resultIte.close();
        expectedIte.close();
    }

    @Ignore
    @Test
    public void testReadSimpleCollectionEmbeddedFT() throws JAXBException, IOException, XMLStreamException{

        // verify that distant service is working properly

        URL url = new URL("http://www.ifremer.fr/services/wfs1?SERVICE=WFS&VERSION=1.1.0&REQUEST=DescribeFeatureType&TYPENAME=quadrige&OUTPUTFORMAT=text/xml;%20subtype=gml/3.1.1");
        final String response = getStringResponse(url.openConnection());
        if (response.contains("<!-- ERROR: Failed opening layer (null) -->")) {
            Logging.getLogger("org.geotoolkit.feature.xml").warning("Skipping embedded test. external service not responding correctly");
            return;
        }

        JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
        reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);

        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        reader = new JAXPStreamFeatureReader();
        reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);

        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT2.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);

        obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/featureCollectionEmbedFT3.xml"));
        reader.dispose();

        assertTrue(obj instanceof FeatureCollection);
    }


    @Test
    public void testWriteSimpleCollection() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final StringWriter temp = new StringWriter();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        writer.write(collectionSimple, temp);
        writer.dispose();

        String s = temp.toString();
        s = s.replaceAll("timeStamp=\"[^\"]*\" ", "timeStamp=\"\" ");
        DomCompare.compare(IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimple.xml")), s);
    }

    @Test
    public void testWriteSimplCollectionElement() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException{
        final StringWriter temp = new StringWriter();
        final ElementFeatureWriter writer = new ElementFeatureWriter();
        Element result = writer.write(collectionSimple, false);

        Source source = new DOMSource(result.getOwnerDocument());

        // Prepare the output file
        Result resultxml = new StreamResult(temp);

        // Write the DOM document to the file
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        xformer.transform(source, resultxml);


        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/CollectionSimpleDom.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);

        String s = temp.toString();
        s = s.replaceAll("timeStamp=\"[^\"]*\" ", "timeStamp=\"\" ");
        DomCompare.compare(expResult, s);
    }

    @Test
    public void testWriteComplexFeature() throws JAXBException, IOException, XMLStreamException,
            DataStoreException, ParserConfigurationException, SAXException{
        final File temp = File.createTempFile("gml", ".xml");
        temp.deleteOnExit();
        final XmlFeatureWriter writer = new JAXPStreamFeatureWriter();
        final StringWriter sw = new StringWriter();
        writer.write(featureComplex, temp);
        writer.dispose();

        String result    = IOUtilities.toString(new FileInputStream(temp));
        String expResult = IOUtilities.toString(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        expResult = expResult.replace("EPSG_VERSION", EPSG_VERSION);
        expResult = expResult.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        result    =    result.replaceAll("(?i)epsg\\:\\d+\\.\\d+\\:", "epsg::");
        DomCompare.compare(expResult, result);
    }

    @Test
    public void testReadComplexFeature() throws JAXBException, IOException, XMLStreamException{
        final XmlFeatureReader reader = new JAXPStreamFeatureReader(complexType);
        Object obj = reader.read(XmlFeatureTest.class
                .getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        reader.dispose();

        assertTrue(obj instanceof Feature);

        Feature result = (Feature) obj;

        assertEquals(featureComplex, result);

        final XmlFeatureReader readerGml = new JAXPStreamFeatureReader(complexType);
        readerGml.getProperties().put(JAXPStreamFeatureReader.BINDING_PACKAGE, "GML");
        obj = readerGml.read(XmlFeatureTest.class.getResourceAsStream("/org/geotoolkit/feature/xml/ComplexFeature.xml"));
        readerGml.dispose();

        assertTrue(obj instanceof Feature);

        result =  (Feature) obj;
        assertEquals(featureComplex, result);
    }

    protected static String getStringResponse(URLConnection conec) throws UnsupportedEncodingException, IOException {
        final StringWriter sw     = new StringWriter();
        final BufferedReader in   = new BufferedReader(new InputStreamReader(conec.getInputStream(), "UTF-8"));
        char [] buffer = new char[1024];
        int size;
        while ((size = in.read(buffer, 0, 1024)) > 0) {
            sw.append(new String(buffer, 0, size));
        }
        return sw.toString();
    }
}
