/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.cs.AxisDirection;

import org.apache.sis.xml.MarshallerPool;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.apache.sis.metadata.iso.ImmutableIdentifier;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.crs.DefaultVerticalCRS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.cs.DefaultEllipsoidalCS;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.apache.sis.referencing.datum.DefaultGeodeticDatum;
import org.apache.sis.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.geotoolkit.test.TestData;

import org.junit.*;

import static org.geotoolkit.referencing.Assert.*;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import javax.xml.bind.JAXBContext;


/**
 * Tests the marshalling and unmarshalling of a few CRS objects.
 * <p>
 * <ul>
 *   <li>{@linkplain DefaultMetadata Metadata} object containing a
 *       {@linkplain DefaultVerticalCRS vertical CRS}</li>
 *   <li>{@linkplain GeographicCRS Geographic CRS}</li>
 * </ul>
 *
 * @author Cédric Briançon (Geomatys)
 * @author Guilhem Legal (Geomatys)
 *
 * @version 3.19
 *
 * @since 3.04
 */
public final strictfp class ReferencingMarsallingTest extends LocaleDependantTestBase {
    /**
     * The resource file which contains an XML representation of a
     * {@linkplain DefaultMetadata metadata} object, with a {@link GeographicCRS}.
     */
    private static final String GEOGRAPHIC_CRS_XML = "GeographicCRS.xml";

    /**
     * Tests the marshalling of a {@linkplain DefaultGeographicCRS geographic crs} object
     * compared to an XML file containing the gml representation of this object.
     *
     * @throws JAXBException if the marshalling process fails.
     * @throws IOException if an error occurs while trying to read data from the resource file.
     */
    @Test
    public void testGeographicCRSMarshalling() throws JAXBException, IOException {
        final GeographicCRS     crs = createGeographicCRS();
        final StringWriter       sw = new StringWriter();
        final MarshallerPool   pool = new MarshallerPool(JAXBContext.newInstance(DefaultGeographicCRS.class), null);
        final Marshaller marshaller = pool.acquireMarshaller();
        marshaller.marshal(crs, sw);
        pool.recycle(marshaller);
        final String result = sw.toString();
        final String expected = TestData.readText(this, GEOGRAPHIC_CRS_XML);
        assertXmlEquals(expected, result, "xmlns:*", "xsi:schemaLocation");
    }

    /**
     * Tests the unmarshalling of a {@linkplain DefaultGeographicCRS geographic crs} object
     * from an XML file.
     *
     * @throws JAXBException if the unmarshalling process fails.
     * @throws IOException if an error occurs while trying to read data from the resource file.
     */
    @Test
    public void testGeographicCRSUnmarshalling() throws JAXBException, IOException {
        final MarshallerPool pool = new MarshallerPool(JAXBContext.newInstance(DefaultGeographicCRS.class), null);
        final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
        final Object obj;
        try (InputStream in = TestData.openStream(this, GEOGRAPHIC_CRS_XML)) {
            obj = unmarshaller.unmarshal(in);
        } finally {
            pool.recycle(unmarshaller);
        }
        assertTrue(obj instanceof DefaultGeographicCRS);
        final DefaultGeographicCRS result = (DefaultGeographicCRS) obj;
        final DefaultGeographicCRS expected = createGeographicCRS();
        // Here we are not able to check the equality on these two geographic CRS,
        // because some default values are set at creation-time, and they are not
        // unmarshalled. So those objects are not equals, just their values are.
        assertEqualsIgnoreMetadata(expected, result, false);
        assertEquals(expected.getName(),                       result.getName());
        assertEquals(expected.getDatum().getName(),            result.getDatum().getName());
        assertEquals(expected.getCoordinateSystem().getName(), result.getCoordinateSystem().getName());
    }

    /**
     * Creates a Geographic CRS for testing purpose.
     */
    private static DefaultGeographicCRS createGeographicCRS() {
        final Map<String,Object> properties = new HashMap<>();
        /*
         * Build the datum.
         */
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "Greenwich"));
        final DefaultPrimeMeridian primeMeridian = new DefaultPrimeMeridian(properties, 0.0, NonSI.DEGREE_ANGLE);

        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "WGS84"));
        final DefaultEllipsoid ellipsoid = DefaultEllipsoid.createFlattenedSphere(properties, 6378137.0, 298.257223563, SI.METRE);

        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "World Geodetic System 1984"));
        properties.put(DefaultGeodeticDatum.IDENTIFIERS_KEY,
                new ImmutableIdentifier(Citations.fromName("EPSG"), "EPSG", "6326"));
        final DefaultGeodeticDatum datum = new DefaultGeodeticDatum(properties, ellipsoid, primeMeridian);
        /*
         * Build the coordinate system.
         */
        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "Geodetic latitude"));
        final DefaultCoordinateSystemAxis axisLat = new DefaultCoordinateSystemAxis(properties, "\u03C6",
                AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "Geodetic longitude"));
        final DefaultCoordinateSystemAxis axisLon = new DefaultCoordinateSystemAxis(properties, "\u03BB",
                AxisDirection.EAST, NonSI.DEGREE_ANGLE);

        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "Géodésique 2D"));
        final DefaultEllipsoidalCS cs = new DefaultEllipsoidalCS(properties, axisLon, axisLat);

        properties.clear();
        properties.put(NAME_KEY, new ImmutableIdentifier(null, null, "WGS84(DD)"));

        return new DefaultGeographicCRS(properties, datum, cs);
    }
}
