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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.data.kml.model.Alias;
import org.geotoolkit.data.kml.model.EnumAltitudeMode;
import org.geotoolkit.data.kml.model.IdAttributes;
import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.Link;
import org.geotoolkit.data.kml.model.Location;
import org.geotoolkit.data.kml.model.Model;
import org.geotoolkit.data.kml.model.Orientation;
import org.geotoolkit.data.kml.model.RefreshMode;
import org.geotoolkit.data.kml.model.ResourceMap;
import org.geotoolkit.data.kml.model.Scale;
import org.geotoolkit.data.kml.xml.KmlReader;
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
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 */
public class ModelTest {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/model.kml";
    private static final FeatureFactory FF = FactoryFinder.getFeatureFactory(
            new Hints(Hints.FEATURE_FACTORY, LenientFeatureFactory.class));


    public ModelTest() {
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
    public void modelReadTest() throws IOException, XMLStreamException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals("Colorado", placemark.getProperty(KmlModelConstants.ATT_NAME.getName()).getValue());
        assertTrue(placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue() instanceof Model);

        final Model model = (Model) placemark.getProperty(KmlModelConstants.ATT_PLACEMARK_GEOMETRY.getName()).getValue();
        assertEquals("khModel543", model.getIdAttributes().getId());
        assertEquals(EnumAltitudeMode.RELATIVE_TO_GROUND, model.getAltitudeMode());

        final Location location = model.getLocation();
        assertEquals(39.55375305703105, location.getLongitude(), DELTA);
        assertEquals(-18.9813220168456, location.getLatitude(), DELTA);
        assertEquals(1223, location.getAltitude(), DELTA);

        final Orientation orientation = model.getOrientation();
        assertEquals(45, orientation.getHeading(), DELTA);
        assertEquals(10, orientation.getTilt(), DELTA);
        assertEquals(0.5, orientation.getRoll(), DELTA);

        final Scale scale = model.getScale();
        assertEquals(4, scale.getX(), DELTA);
        assertEquals(2, scale.getY(), DELTA);
        assertEquals(3, scale.getZ(), DELTA);

        final Link link = model.getLink();
        assertEquals("house.dae", link.getHref());
        assertEquals(RefreshMode.ON_EXPIRE, link.getRefreshMode());

        final ResourceMap resourceMap = model.getRessourceMap();
        assertEquals(3, resourceMap.getAliases().size());

        final Alias alias0 = resourceMap.getAliases().get(0);
        assertEquals(new URI("../files/CU-Macky---Center-StairsnoCulling.jpg"), alias0.getTargetHref());
        assertEquals(new URI("CU-Macky---Center-StairsnoCulling.jpg"), alias0.getSourceHref());

        final Alias alias1 = resourceMap.getAliases().get(1);
        assertEquals(new URI("../files/CU-Macky-4sideturretnoCulling.jpg"), alias1.getTargetHref());
        assertEquals(new URI("CU-Macky-4sideturretnoCulling.jpg"), alias1.getSourceHref());

        final Alias alias2 = resourceMap.getAliases().get(2);
        assertEquals(new URI("../files/CU-Macky-Back-NorthnoCulling.jpg"), alias2.getTargetHref());
        assertEquals(new URI("CU-Macky-Back-NorthnoCulling.jpg"), alias2.getSourceHref());
    }

    @Test
    public void modelWriteTest() throws KmlException, IOException, XMLStreamException, ParserConfigurationException, SAXException, URISyntaxException{
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final Alias alias0 = kmlFactory.createAlias();
        alias0.setTargetHref(new URI("../files/CU-Macky---Center-StairsnoCulling.jpg"));
        alias0.setSourceHref(new URI("CU-Macky---Center-StairsnoCulling.jpg"));

        final Alias alias1 = kmlFactory.createAlias();
        alias1.setTargetHref(new URI("../files/CU-Macky-4sideturretnoCulling.jpg"));
        alias1.setSourceHref(new URI("CU-Macky-4sideturretnoCulling.jpg"));

        final Alias alias2 = kmlFactory.createAlias();
        alias2.setTargetHref(new URI("../files/CU-Macky-Back-NorthnoCulling.jpg"));
        alias2.setSourceHref(new URI("CU-Macky-Back-NorthnoCulling.jpg"));

        final ResourceMap resourceMap = kmlFactory.createResourceMap();
        resourceMap.setAliases(Arrays.asList(alias0, alias1, alias2));

        final Link link = kmlFactory.createLink();
        link.setHref("house.dae");
        link.setRefreshMode(RefreshMode.ON_EXPIRE);

        final Scale scale = kmlFactory.createScale();
        scale.setX(4);
        scale.setY(2);
        scale.setZ(3);

        final Orientation orientation = kmlFactory.createOrientation();
        orientation.setHeading(45);
        orientation.setTilt(10);
        orientation.setRoll(0.5);

        final Location location = kmlFactory.createLocation();
        location.setLongitude(39.55375305703105);
        location.setLatitude(-18.9813220168456);
        location.setAltitude(1223);

        final Model model = kmlFactory.createModel();
        final IdAttributes idAttributes = kmlFactory.createIdAttributes("khModel543", null);
        model.setIdAttributes(idAttributes);
        model.setAltitudeMode(EnumAltitudeMode.RELATIVE_TO_GROUND);
        model.setLocation(location);
        model.setOrientation(orientation);
        model.setScale(scale);
        model.setLink(link);
        model.setRessourceMap(resourceMap);

        final Feature placemark = kmlFactory.createPlacemark();
        final Collection<Property> placemarkProperties = placemark.getProperties();
        placemarkProperties.add(FF.createAttribute("Colorado", KmlModelConstants.ATT_NAME, null));
        placemarkProperties.add(FF.createAttribute(model, KmlModelConstants.ATT_PLACEMARK_GEOMETRY, null));


        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("testModel",".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(
                 new File(pathToTestFile), temp);

    }

}