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
package org.geotoolkit.data.kml;

import com.vividsolutions.jts.geom.Coordinate;
import java.net.URISyntaxException;
import org.geotoolkit.data.kml.xml.KmlReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Coordinates;
import org.geotoolkit.data.kml.model.Create;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.NetworkLinkControl;
import org.geotoolkit.data.kml.model.Point;
import org.geotoolkit.data.kml.model.Update;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.LenientFeatureFactory;
import org.geotoolkit.xml.DomCompare;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.Property;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuel Andrés
 */
public class CreateTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/create.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));
    
    public CreateTest() {
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
    public void createReadTest() throws IOException, XMLStreamException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final NetworkLinkControl networkLinkControl = kmlObjects.getNetworkLinkControl();
        final Update update = networkLinkControl.getUpdate();
        final URI targetHref = update.getTargetHref();
        assertEquals("http://myserver.com/Point.kml", targetHref.toString());

        assertEquals(1, update.getUpdates().size());
        assertTrue(update.getUpdates().get(0) instanceof Create);
        Create create = (Create) update.getUpdates().get(0);

        assertEquals(1, create.getContainers().size());
        assertTrue(create.getContainers().get(0) instanceof Feature);
        assertTrue(((Feature) create.getContainers().get(0)).getType().equals(KmlModelConstants.TYPE_DOCUMENT));
        final Feature document = (Feature) create.getContainers().get(0);
        assertEquals("region24",((IdAttributes) document.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()).getValue()).getTargetId());

        assertEquals(1, document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).size());
        Iterator i;
        i = document.getProperties(KmlModelConstants.ATT_DOCUMENT_FEATURES.getName()).iterator();
        if(i.hasNext()){
            final Object object = ((Property) i.next()).getValue();
            assertTrue(object instanceof Feature);
            final Feature placemark = (Feature) object;
            assertTrue(placemark.getType().equals(KmlModelConstants.TYPE_PLACEMARK));
            assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Point);

            assertEquals("placemark891",((IdAttributes) placemark.getProperty(KmlModelConstants.ATT_ID_ATTRIBUTES.getName()).getValue()).getId());
            final Point point = (Point) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();

            final Coordinates coordinates = point.getCoordinateSequence();
            assertEquals(1, coordinates.size());

            final Coordinate coordinate = coordinates.getCoordinate(0);
            assertEquals(-95.48, coordinate.x, DELTA);
            assertEquals(40.43, coordinate.y, DELTA);
            assertEquals(0, coordinate.z, DELTA);
        }
    }

    @Test
    public void createWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException {
        final KmlFactory kmlFactory = new DefaultKmlFactory();

        Coordinate coordinate = kmlFactory.createCoordinate(-95.48, 40.43, 0);
        Coordinates coordinates = kmlFactory.createCoordinates(Arrays.asList(coordinate));

        Point point = kmlFactory.createPoint(coordinates);

        Feature placemark = kmlFactory.createPlacemark();
        Collection<Property> placemarkProperties = placemark.getProperties();
        IdAttributes placemarkIdAttributes = kmlFactory.createIdAttributes("placemark891", null);
        placemarkProperties.add(FF.createAttribute(placemarkIdAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));
        placemarkProperties.add(FF.createAttribute(point, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));

        Feature document = kmlFactory.createDocument();
        Collection<Property> documentProperties = document.getProperties();
        documentProperties.add(FF.createAttribute(placemark, KmlModelConstants.ATT_DOCUMENT_FEATURES, null));
        IdAttributes documentIdAttributes = kmlFactory.createIdAttributes(null, "region24");
        documentProperties.add(FF.createAttribute(documentIdAttributes, KmlModelConstants.ATT_ID_ATTRIBUTES, null));

        Create create = kmlFactory.createCreate();
        create.setContainers(Arrays.asList(document));

        URI targetHref = new URI("http://myserver.com/Point.kml");

        Update update = kmlFactory.createUpdate();
        update.setUpdates(Arrays.asList((Object) create));
        update.setTargetHref(targetHref);

        NetworkLinkControl networkLinkControl = kmlFactory.createNetworkLinkControl();
        networkLinkControl.setUpdate(update);


        final Kml kml = kmlFactory.createKml(networkLinkControl, null, null, null);

        File temp = File.createTempFile("testCreate", ".kml");
        temp.deleteOnExit();

        KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                new File(pathToTestFile), temp);

    }
}
