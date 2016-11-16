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
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.kml.model.Kml;
import org.geotoolkit.data.kml.model.KmlException;
import org.geotoolkit.data.kml.model.KmlModelConstants;
import org.geotoolkit.data.kml.model.LatLonAltBox;
import org.geotoolkit.data.kml.model.Lod;
import org.geotoolkit.data.kml.model.Region;
import org.geotoolkit.data.kml.xml.KmlReader;
import org.geotoolkit.data.kml.xml.KmlWriter;
import org.geotoolkit.xml.DomCompare;

import org.junit.Test;

import org.opengis.feature.Feature;
import org.geotoolkit.data.kml.xml.KmlConstants;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;

/**
 *
 * @author Samuel Andrés
 * @module
 */
public class RegionTest extends org.geotoolkit.test.TestBase {

    private static final double DELTA = 0.000000000001;
    private static final String pathToTestFile = "src/test/resources/org/geotoolkit/data/kml/region.kml";

    @Test
    public void regionReadTest() throws IOException, XMLStreamException, KmlException, URISyntaxException {

        final KmlReader reader = new KmlReader();
        reader.setInput(new File(pathToTestFile));
        final Kml kmlObjects = reader.read();
        reader.dispose();

        final Feature placemark = kmlObjects.getAbstractFeature();
        assertEquals(KmlModelConstants.TYPE_PLACEMARK, placemark.getType());
        assertEquals("Colorado", placemark.getPropertyValue(KmlConstants.TAG_NAME));
        final Region region = (Region) placemark.getPropertyValue(KmlConstants.TAG_REGION);
        final LatLonAltBox latLonAltBox = region.getLatLonAltBox();
        assertEquals(50.625, latLonAltBox.getNorth(), DELTA);
        assertEquals(45, latLonAltBox.getSouth(), DELTA);
        assertEquals(28.125, latLonAltBox.getEast(), DELTA);
        assertEquals(22.5, latLonAltBox.getWest(), DELTA);
        assertEquals(10, latLonAltBox.getMinAltitude(), DELTA);
        assertEquals(50, latLonAltBox.getMaxAltitude(), DELTA);
        final Lod lod = region.getLod();
        assertEquals(128, lod.getMinLodPixels(), DELTA);
        assertEquals(1024, lod.getMaxLodPixels(), DELTA);
        assertEquals(128, lod.getMinFadeExtent(), DELTA);
        assertEquals(128, lod.getMaxFadeExtent(), DELTA);
    }

    @Test
    public void regionWriteTest() throws KmlException, IOException, XMLStreamException,
            ParserConfigurationException, SAXException
    {
        final KmlFactory kmlFactory = DefaultKmlFactory.getInstance();

        final LatLonAltBox latLonAltBox = kmlFactory.createLatLonAltBox();
        latLonAltBox.setNorth(50.625);
        latLonAltBox.setSouth(45);
        latLonAltBox.setEast(28.125);
        latLonAltBox.setWest(22.5);
        latLonAltBox.setMinAltitude(10);
        latLonAltBox.setMaxAltitude(50);

        final Lod lod = kmlFactory.createLod();
        lod.setMinLodPixels(128);
        lod.setMaxLodPixels(1024);
        lod.setMinFadeExtent(128);
        lod.setMaxFadeExtent(128);

        final Region region = kmlFactory.createRegion(null, null, latLonAltBox, lod, null, null);

        final Feature placemark = kmlFactory.createPlacemark();
        placemark.setPropertyValue(KmlConstants.TAG_REGION, region);
        placemark.setPropertyValue(KmlConstants.TAG_NAME, "Colorado");

        final Kml kml = kmlFactory.createKml(null, placemark, null, null);

        final File temp = File.createTempFile("testRegion", ".kml");
        temp.deleteOnExit();

        final KmlWriter writer = new KmlWriter();
        writer.setOutput(temp);
        writer.write(kml);
        writer.dispose();

        DomCompare.compare(new File(pathToTestFile), temp);
    }
}
