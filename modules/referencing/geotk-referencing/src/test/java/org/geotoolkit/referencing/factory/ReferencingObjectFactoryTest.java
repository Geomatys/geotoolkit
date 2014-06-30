/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.awt.geom.AffineTransform;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.AuthorityFactoryFinder;

import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.metadata.iso.ImmutableIdentifier;

import org.geotoolkit.test.referencing.ReferencingTestBase;

import org.junit.*;
import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests the creation of {@link CoordinateReferenceSystem} objects and dependencies through
 * factories (not authority factories).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class ReferencingObjectFactoryTest extends ReferencingTestBase {
    /**
     * Convenience method creating a map with only the "{@code name"} property.
     * This is the only mandatory property for object creation.
     */
    private static Map<String,?> name(final String name) {
        return Collections.singletonMap("name", name);
    }

    /**
     * Tests step-by-step the creation of new coordinate reference systems.
     * This tests create every objects itself. Practical application may use
     * existing constants declared in default implementation classes instead.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    @Ignore
    public void testCreation() throws FactoryException {
        /*
         * Factories to be used in this test.
         */
        final DatumFactory         datumFactory = FactoryFinder.getDatumFactory        (null);
        final CSFactory            csFactory    = FactoryFinder.getCSFactory           (null);
        final CRSFactory           crsFactory   = FactoryFinder.getCRSFactory          (null);
        final MathTransformFactory mtFactory    = FactoryFinder.getMathTransformFactory(null);
        /*
         * Objects to be created in this test.
         */
        final Unit<Length>         linearUnit;
        final Unit<Angle>          angularUnit;
        final Ellipsoid            ellipsoid;
        final PrimeMeridian        meridian;
        final GeodeticDatum        datum;
        final CoordinateSystemAxis longitude, latitude, easting, northing;
        final EllipsoidalCS        geographicCS;
        final GeographicCRS        geographicCRS;
        final ParameterValueGroup  parameters;
        final Conversion           projection;
        final CartesianCS          projectedCS;
        final ProjectedCRS         projectedCRS;
        /*
         * Creates the prime meridian.
         */
        angularUnit = NonSI.DEGREE_ANGLE;
        meridian = datumFactory.createPrimeMeridian(name("Greenwich"), 0, angularUnit);
        assertWktEquals(meridian,
                "PRIMEM[“Greenwich”, 0.0]");
        /*
         * Creates the ellipsoid.
         */
        linearUnit = SI.METRE;
        ellipsoid = datumFactory.createEllipsoid(name("Airy1830"), 6377563.396, 6356256.910, linearUnit);
        assertWktEquals(ellipsoid,
                "SPHEROID[“Airy1830”, 6377563.396, 299.3249753150345]");
        /*
         * Creates the geodetic datum.
         */
        datum = datumFactory.createGeodeticDatum(name("Airy1830"), ellipsoid, meridian);
        assertWktEquals(datum,
                "DATUM[“Airy1830”,\n" +
                "  SPHEROID[“Airy1830”, 6377563.396, 299.3249753150345]]");
        /*
         * Creates the coordinate system for the base CRS.
         */
        longitude = csFactory.createCoordinateSystemAxis(name("Longitude"), "long", AxisDirection.EAST,  angularUnit);
        latitude  = csFactory.createCoordinateSystemAxis(name("Latitude"),  "lat",  AxisDirection.NORTH, angularUnit);
        geographicCS = csFactory.createEllipsoidalCS(name("Ellipsoidal"), longitude, latitude);
        // There is no WKT for CoordinateSystem objects.
        /*
         * Creates the base CRS.
         */
        geographicCRS = crsFactory.createGeographicCRS(name("Airy1830"), datum, geographicCS);
        assertWktEquals(geographicCRS,
                "GEOGCS[“Airy1830”,\n" +
                "  DATUM[“Airy1830”,\n" +
                "    SPHEROID[“Airy1830”, 6377563.396, 299.3249753150345]],\n" +
                "  PRIMEM[“Greenwich”, 0.0],\n" +
                "  UNIT[“degree”, 0.017453292519943295],\n" +
                "  AXIS[“Longitude”, EAST],\n" +
                "  AXIS[“Latitude”, NORTH]]");
        /*
         * Creates the coordinate system for the projected CRS.
         */
        easting  = csFactory.createCoordinateSystemAxis(name("Easting"),  "x", AxisDirection.EAST,  linearUnit);
        northing = csFactory.createCoordinateSystemAxis(name("Northing"), "y", AxisDirection.NORTH, linearUnit);
        projectedCS = csFactory.createCartesianCS(name("Cartesian"), easting, northing);
        // There is no WKT for CoordinateSystem objects.
        /*
         * Creates the defining conversion.
         */
        parameters = mtFactory.getDefaultParameters("Transverse_Mercator");
        parameters.parameter("semi_major")        .setValue(ellipsoid.getSemiMajorAxis());
        parameters.parameter("semi_minor")        .setValue(ellipsoid.getSemiMinorAxis());
        parameters.parameter("central_meridian")  .setValue(     49);
        parameters.parameter("latitude_of_origin").setValue(     -2);
        parameters.parameter("false_easting")     .setValue( 400000);
        parameters.parameter("false_northing")    .setValue(-100000);
        projection = new DefiningConversion("GBN grid", parameters);
        /*
         * Creates the projected CRS.
         */
        projectedCRS = crsFactory.createProjectedCRS(
                name("Great_Britian_National_Grid"), geographicCRS, projection, projectedCS);
        assertWktEquals(projectedCRS,
                "PROJCS[“Great_Britian_National_Grid”,\n" +
                "  GEOGCS[“Airy1830”,\n" +
                "    DATUM[“Airy1830”,\n" +
                "      SPHEROID[“Airy1830”, 6377563.396, 299.3249753150345]],\n" +
                "    PRIMEM[“Greenwich”, 0.0],\n" +
                "    UNIT[“degree”, 0.017453292519943295],\n" +
                "    AXIS[“Longitude”, EAST],\n" +
                "    AXIS[“Latitude”, NORTH]],\n" +
                "  PROJECTION[“Transverse_Mercator”],\n" +
                "  PARAMETER[“central_meridian”, 49.0],\n" +
                "  PARAMETER[“latitude_of_origin”, -2.0],\n" +
                "  PARAMETER[“scale_factor”, 1.0],\n" +
                "  PARAMETER[“false_easting”, 400000.0],\n" +
                "  PARAMETER[“false_northing”, -100000.0],\n" +
                "  UNIT[“metre”, 1.0],\n" +
                "  AXIS[“Easting”, EAST],\n" +
                "  AXIS[“Northing”, NORTH]]");
    }

    /**
     * Tests all map projection creation.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testMapProjections() throws FactoryException {
        /*
         * Factories to be used in this test.
         */
        final CRSFactory           crsFactory = FactoryFinder.getCRSFactory          (null);
        final MathTransformFactory mtFactory  = FactoryFinder.getMathTransformFactory(null);
        /*
         * Gets all map projections and creates a projection using the WGS84 ellipsoid
         * and default parameter values.
         */
        final Collection<OperationMethod> methods = mtFactory.getAvailableMethods(Projection.class);
        final Map<String,?> dummyName = Collections.singletonMap("name", "Test");
        for (final OperationMethod method : methods) {
            final String classification = method.getName().getCode();
            ParameterValueGroup param = mtFactory.getDefaultParameters(classification);
            param.parameter("semi_major").setValue(6377563.396);
            param.parameter("semi_minor").setValue(6356256.909237285);
            final MathTransform mt;
            try {
                mt = mtFactory.createParameterizedTransform(param);
            } catch (FactoryException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof IllegalArgumentException || cause instanceof IllegalStateException) {
                    // This happen if the projection has mandatory parameter that we didn't supplied.
                    continue;
                }
                throw e;
            }
            /*
             * Tests map projection properties. Note that the Equirectangular projection
             * has been optimized as an affine transform, which we skip.
             */
            if (mt instanceof AffineTransform) {
                continue;
            }
            assertTrue(classification, mt instanceof AbstractMathTransform);
            final AbstractMathTransform amt = (AbstractMathTransform) mt;
            if (!((ImmutableIdentifier) method.getName()).isDeprecated()) {
                assertEquals(classification, amt.getParameterDescriptors().getName().getCode());
            }
            param = amt.getParameterValues();
            assertEquals(classification, 6377563.396,       param.parameter("semi_major").doubleValue(), 1E-4);
            assertEquals(classification, 6356256.909237285, param.parameter("semi_minor").doubleValue(), 1E-4);
            /*
             * Creates a ProjectedCRS from the map projection.
             */
            final ProjectedCRS projCRS = crsFactory.createProjectedCRS(dummyName,
                    DefaultGeographicCRS.WGS84,
                    new DefiningConversion(dummyName, method, mt),
                    DefaultCartesianCS.PROJECTED);
            final Conversion conversion = projCRS.getConversionFromBase();
            assertSame(classification, mt, conversion.getMathTransform());
            final OperationMethod projMethod = conversion.getMethod();
            assertEquals(classification, projMethod.getName().getCode());
        }
    }

    /**
     * Tests datum aliases. Note: ellipsoid and prime meridian are dummy values
     * (not conform to the usage in real world) just for testing purpose.
     *
     * @throws FactoryException If a CRS can not be created.
     */
    @Test
    public void testDatumAliases() throws FactoryException {
        final String           name0 = "Nouvelle Triangulation Francaise (Paris)";
        final String           name1 = "Nouvelle_Triangulation_Francaise_Paris";
        final String           name2 = "NTF (Paris meridian)";
        final Ellipsoid    ellipsoid = DefaultEllipsoid.WGS84;
        final PrimeMeridian meridian = DefaultPrimeMeridian.GREENWICH;
        DatumFactory         factory = new ReferencingObjectFactory();
        final Map<String,?> properties = Collections.singletonMap("name", name1);
        GeodeticDatum datum = factory.createGeodeticDatum(properties, ellipsoid, meridian);
        assertTrue(datum.getAlias().isEmpty());

        for (int i=0; i<3; i++) {
            switch (i) {
                case  0: factory = new DatumAliases(factory);                    break;
                case  1: factory = AuthorityFactoryFinder.getDatumFactory(null); break;
                case  2: ((DatumAliases) factory).freeUnused();                  break;
                default: throw new AssertionError(); // Should not occurs.
            }
            final String pass = "Pass #"+i;
            datum = factory.createGeodeticDatum(properties, ellipsoid, meridian);
            final GenericName[] aliases = datum.getAlias().toArray(new GenericName[0]);
            assertEquals(pass, 3, aliases.length);
            assertEquals(pass, name0, aliases[0].tip().toString());
            assertEquals(pass, name1, aliases[1].tip().toString());
            assertEquals(pass, name2, aliases[2].tip().toString());
        }

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Tokyo"), ellipsoid, meridian);
        Collection<GenericName> aliases = datum.getAlias();
        assertEquals(4, aliases.size());

        ((DatumAliases) factory).freeUnused();
        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "_toKyo  _"), ellipsoid, meridian);
        assertEquals(4, datum.getAlias().size());
        assertTrue(aliases.equals(datum.getAlias()));

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "D_Tokyo"), ellipsoid, meridian);
        assertEquals(4, datum.getAlias().size());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Luxembourg 1930"), ellipsoid, meridian);
        assertEquals(3, datum.getAlias().size());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "Dummy"), ellipsoid, meridian);
        assertTrue("Non existing datum should have no alias.", datum.getAlias().isEmpty());

        datum = factory.createGeodeticDatum(Collections.singletonMap("name", "WGS 84"), ellipsoid, meridian);
        assertTrue (IdentifiedObjects.isHeuristicMatchForName(datum, "WGS 84"));
        assertTrue (IdentifiedObjects.isHeuristicMatchForName(datum, "WGS_1984"));
        assertTrue (IdentifiedObjects.isHeuristicMatchForName(datum, "World Geodetic System 1984"));
        assertFalse(IdentifiedObjects.isHeuristicMatchForName(datum, "WGS 72"));
    }
}
