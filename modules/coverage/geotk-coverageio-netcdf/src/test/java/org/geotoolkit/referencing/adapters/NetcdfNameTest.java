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

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.wrapper.netcdf.NetcdfTransformFactory;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.metadata.iso.citation.Citations;
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
        final MathTransformFactory factory = FactoryFinder.getMathTransformFactory(
                new Hints(Hints.MATH_TRANSFORM_FACTORY, DefaultMathTransformFactory.class));
        final MathTransformFactory netcdfFactory = new NetcdfTransformFactory();
        for (final OperationMethod method : factory.getAvailableMethods(null)) {
            String name = IdentifiedObjects.getName(method, Citations.NETCDF);
            if (name != null) {
                final ParameterValueGroup netcdfParam = netcdfFactory.getDefaultParameters(name);
                assertNameEquals(name, netcdfParam.getDescriptor(), method);
                for (final GeneralParameterDescriptor param : method.getParameters().descriptors()) {
                    name = IdentifiedObjects.getName(param, Citations.NETCDF);
                    if (name != null) try {
                        final ParameterValue<?> netcdfValue = netcdfParam.parameter(name);
                        assertNameEquals(name, netcdfValue.getDescriptor(), param);
                    } catch (ParameterNotFoundException e) {
                        System.out.println("WARNING: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    /**
     * Ensures that the NetCDF, OGC and EPSG names of the given objects are equal.
     */
    private static void assertNameEquals(final String name, final IdentifiedObject expected,
            final IdentifiedObject actual)
    {
        assertNameEquals(name, expected, actual, Citations.NETCDF);
        assertNameEquals(name, expected, actual, Citations.OGC);
//      assertNameEquals(name, expected, actual, Citations.EPSG);
    }

    /**
     * Ensures that the name of the given objects, for the given authority, are equal.
     */
    private static void assertNameEquals(final String name, final IdentifiedObject expected,
            final IdentifiedObject actual, final Citation authority)
    {
        final Set<String> n1 = IdentifiedObjects.getNames(expected, authority);
        final Set<String> n2 = IdentifiedObjects.getNames(actual,   authority);
        assertFalse(name, n1.isEmpty());
        assertFalse(name, n2.isEmpty());
        assertTrue (name + " " + n1 + " " + n2, n2.containsAll(n1));
    }
}
