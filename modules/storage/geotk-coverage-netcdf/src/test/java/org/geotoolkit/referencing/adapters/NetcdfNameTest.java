/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.referencing.adapters;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.wrapper.netcdf.NetcdfTransformFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.metadata.Citations;
import org.geotoolkit.referencing.operation.DefaultMathTransformFactory;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Compares the Geotk NetCDF parameter names with the GeoAPI-NetCDF parameter names.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfNameTest {
    /**
     * Scans all operation methods provided by Geotk, and compares the projection and
     * parameter names with the one declared in the {@code geoapi-netcdf} module. We
     * rely on the GeoAPI test suite for ensuring that the {@code geoapi-netcdf} names
     * are corrects.
     *
     * @throws NoSuchIdentifierException If the NetCDF name of a Geotk projection is
     *         unknown to the {@code geoapi-netcdf} factory.
     */
    @Test
    public void compareNames() throws NoSuchIdentifierException {
        /*
         * Extra-parameters declared by Geotk while not handled by the NetCDF library.  Those
         * parameters can be handled by Geotk, so we keep them. However we maintain this list
         * so we know what those parameters are, and in order to be notified if the situation
         * change in a future NetCDF version  (in which case the Geotk library may need to be
         * updated).
         */
        final Map<String,Set<String>> extraParameterNames = new HashMap<>();
        assertNull(extraParameterNames.put("Mercator", new HashSet<>(Arrays.asList(
                "latitude_of_projection_origin"))));
        assertNull(extraParameterNames.put("Orthographic", new HashSet<>(Arrays.asList(
                "false_easting", "false_northing"))));
        /*
         * 'netcdfParameterNames' will contain the names provided by the NetcdfTransformFactory.
         * We use this factory as the reference implementation.
         */
        final Set<String> netcdfParameterNames = new HashSet<>();
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(
                new Hints(Hints.MATH_TRANSFORM_FACTORY, DefaultMathTransformFactory.class));
        final MathTransformFactory netcdfFactory = new NetcdfTransformFactory();
        for (final OperationMethod method : factory.getAvailableMethods(null)) {
            final String methodName = IdentifiedObjects.getName(method, Citations.NETCDF);
            if (methodName != null) {
                /*
                 * The Geotk operation parameter names were assigned on the assumption that the
                 * NetCDF Stereographic projection uses USGS formulas rather than EPSG ones.
                 * However the GeoAPI NetCDF wrappers still declare the EPSG parameter names
                 * as the closest available match. Since Geotk supports both formulas as two
                 * different kind of projections ("Stereographic" and "ObliqueStereographic")
                 * we can not compare the EPSG parameter names since the set of NetCDF and EPSG
                 * parameter names are non-empty in different Geotk providers.
                 */
                final boolean skipEPSG = methodName.equals("Stereographic");
                final ParameterValueGroup netcdfParam = netcdfFactory.getDefaultParameters(methodName);
                assertNameEquals(methodName, netcdfParam.getDescriptor(), method, skipEPSG);
                netcdfParameterNames.clear();
                for (final GeneralParameterValue netcdfValue : netcdfParam.values()) {
                    assertTrue(methodName, netcdfParameterNames.add(IdentifiedObjects.getName(netcdfValue.getDescriptor(), Citations.NETCDF)));
                }
                for (final GeneralParameterDescriptor param : method.getParameters().descriptors()) {
                    final String name = IdentifiedObjects.getName(param, Citations.NETCDF);
                    if (name != null) {
                        if (!name.equals("semi_major_axis") && !name.equals("semi_minor_axis")) try {
                            final ParameterValue<?> netcdfValue = netcdfParam.parameter(name);
                            assertTrue(methodName, netcdfParameterNames.remove(IdentifiedObjects.getName(netcdfValue.getDescriptor(), Citations.NETCDF)));
                            assertNameEquals(methodName, netcdfValue.getDescriptor(), param, skipEPSG);
                        } catch (ParameterNotFoundException exception) {
                            /*
                             * If the parameter has not been found, ensure that this exception
                             * is known. The set of known exceptions is defined at the begining
                             * of this method.
                             */
                            final Set<String> expected = extraParameterNames.get(methodName);
                            if (expected == null || !expected.remove(name)) {
                                throw exception;
                            }
                            if (expected.isEmpty()) {
                                assertSame(expected, extraParameterNames.remove(methodName));
                            }
                        }
                    }
                }
                assertTrue(netcdfParameterNames.remove("earth_radius"));
                assertTrue(netcdfParameterNames.toString(), netcdfParameterNames.isEmpty());
            }
        }
        assertTrue("Some parameters were not expected to be supported by the NetCDF library:\n"
                + extraParameterNames, extraParameterNames.isEmpty());
    }

    /**
     * Ensures that the NetCDF, OGC and EPSG names of the given objects are equal.
     *
     * @param message The message to show in case of failure.
     * @param netcdf  The NetCDF object from which to get the expected names.
     * @param geotk   The Geotk object from which to get the names.
     */
    private static void assertNameEquals(final String message, final IdentifiedObject netcdf,
            final IdentifiedObject geotk, final boolean skipEPSG)
    {
        assertNameEquals(message, netcdf, geotk, Citations.NETCDF, false);
        assertNameEquals(message, netcdf, geotk, Citations.OGC,    false);
        assertNameEquals(message, netcdf, geotk, Citations.EPSG,   skipEPSG);
    }

    /**
     * Ensures that the name of the given objects, for the given authority, are equal.
     *
     * @param message   The message to show in case of failure.
     * @param netcdf    The NetCDF object from which to get the expected names.
     * @param geotk     The Geotk object from which to get the names.
     * @param authority The authority of the names to compare.
     */
    private static void assertNameEquals(final String message, final IdentifiedObject netcdf,
            final IdentifiedObject geotk, final Citation authority, final boolean skipEPSG)
    {
        final Set<String> n1 = IdentifiedObjects.getNames(netcdf, authority);
        final Set<String> n2 = IdentifiedObjects.getNames(geotk,  authority);
        assertFalse (message, n1.isEmpty());
        assertEquals(message, n2.isEmpty(), skipEPSG);
        if (!skipEPSG) {
            assertTrue(message + ": expected some or all of " + n1 + " but got " + n2, n2.containsAll(n1));
        }
    }
}
