/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.s57;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransformFactory;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.operation.DefiningConversion;


/**
 * S-57 CRS
 *
 * @author Johann Sorel (Geomatys)
 */
public final class S57CoordinateReferenceSystems {
    /**
     * The parameter names for S57 projections.
     * We use OGC names rather than EPSG names since they are more stable.
     */
    private static final Map<String,String[]> PARAMETER_NAMES = new HashMap<String,String[]>(12);
    static {
        final String[] conicParameters = {
            "central_meridian", "standard_parallel_1", "standard_parallel_2", "latitude_of_origin"
        };
        add("ALA",  1, conicParameters);
        add("LCC",  6, conicParameters);
        add("HOM",  5, "longitude_of_center", "latitude_of_center");
        add("MER",  8, "central_meridian",    "standard_parallel_1", "latitude_of_origin");
        add("OME",  9, "longitude_of_center", "latitude_of_center",  "azimuth");
        add("ORT", 10, "central_meridian",    "latitude_of_origin");
        add("PST", 11, "central_meridian",    "standard_parallel_1");
        add("TME", 13, "central_meridian",    "scale_factor",        "latitude_of_origin");
        add("OST", 14, "central_meridian",    "latitude_of_origin",  "scale_factor");
    }

    /**
     * Adds an entry in the {@link #PARAMETER_NAMES} map.
     */
    private static void add(final String name, final int ordinal, final String... parameters) {
        if (PARAMETER_NAMES.put(name, parameters) != null ||
            PARAMETER_NAMES.put(Integer.toString(ordinal), parameters) != null)
        {
            throw new AssertionError(name);
        }
    }

    /**
     * The factory to use for creating {@link MathTransform} instances.
     */
    private final MathTransformFactory mtFactory;

    /**
     * The factory to use for creating {@link ProjectedCRS} instances.
     */
    private final CRSFactory crsFactory;

    /**
     * Creates a new factory.
     *
     * @param hints An optional set of hints for finding factories.
     */
    public S57CoordinateReferenceSystems(final Hints hints) {
        mtFactory  = FactoryFinder.getMathTransformFactory(hints);
        crsFactory = FactoryFinder.getCRSFactory(hints);
    }

    /**
     * Creates a new projection for the given name and parameters.
     *
     * @param  name           The S57 projection name or numerical value.
     * @param  parameters     The projection parameters, in degrees.
     * @param  falseEasting   The false easting in metres.
     * @param  falseNorthing  The false northing in metres.
     * @return The coordinate reference system.
     * @throws FactoryException If the specified projection can not be created.
     */
    public ProjectedCRS create(final String name, final double[] parameters,
            final double falseEasting, final double falseNorthing) throws FactoryException
    {
        final String[] mapping = PARAMETER_NAMES.get(name);
        if (mapping == null) {
            throw new NoSuchIdentifierException("Unsupported S57 projection: " + name, name);
        }
        if (parameters.length != mapping.length) {
            throw new FactoryException("Unexpected number of S57 parameters. Expected "
                    + mapping.length + " but got " + parameters.length + '.');
        }
        final ParameterValueGroup p = mtFactory.getDefaultParameters(name);
        for (int i=0; i<parameters.length; i++) {
            p.parameter(mapping[i]).setValue(parameters[i]);
        }
        p.parameter("false_easting") .setValue(falseEasting);
        p.parameter("false_northing").setValue(falseNorthing);
        return crsFactory.createProjectedCRS(Collections.singletonMap(ProjectedCRS.NAME_KEY, name),
                DefaultGeographicCRS.WGS84, new DefiningConversion(name, p), DefaultCartesianCS.PROJECTED);
    }
}
