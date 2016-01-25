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
package org.geotoolkit.data.mapinfo;

import org.apache.sis.internal.util.Constants;
import org.geotoolkit.referencing.operation.provider.UniversalParameters;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.util.FactoryException;

import java.util.*;

/**
 * Reference the needed parameters used for MapInfo projections.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 14/03/13
 */
public class ProjectionParameters {

    /** A list of the different possible parameters in MapInfo */
    static final List<String> PARAMETER_LIST = Collections.unmodifiableList(Arrays.asList(
            Constants.CENTRAL_MERIDIAN,   // 0
            "latitude of origin",         // 1
            Constants.STANDARD_PARALLEL_1,// 2
            Constants.STANDARD_PARALLEL_2,// 3
            "azimuth",                    // 4
            Constants.SCALE_FACTOR,       // 5
            Constants.FALSE_EASTING,      // 6
            Constants.FALSE_NORTHING      // 7
    ));

    /** A map whose key is the MapInfo projection code, and value is a list of the possible parameters for it (their indice in the previous list). */
    private static final Map<Integer, int[]> PROJECTION_PARAMETERS = new HashMap<Integer, int[]>();
    static {
        PROJECTION_PARAMETERS.put(1, new int[0]);              //Longitude / Latitude
        PROJECTION_PARAMETERS.put(9,  new int[]{0,1,2,3,6,7}); //Albers
        PROJECTION_PARAMETERS.put(17, new int[]{0});           //Gall
        PROJECTION_PARAMETERS.put(7,  new int[]{0,1,4,5,6,7}); //Hotine oblique mercator
        PROJECTION_PARAMETERS.put(4,  new int[]{0,1});         //Lambert Azimuthal Equal–Area
        PROJECTION_PARAMETERS.put(3,  new int[]{0,1,2,3,6,7}); //Lambert Conformal Conic (2SP)
        PROJECTION_PARAMETERS.put(19, new int[]{0,1,2,3,6,7}); //Lambert Conformal Conic (modified for Belgium 1972)
        PROJECTION_PARAMETERS.put(10, new int[]{0});           //Mercator
        PROJECTION_PARAMETERS.put(11, new int[]{0});           //Miller Cylindrical
        PROJECTION_PARAMETERS.put(13, new int[]{0});           //Mollweide
        PROJECTION_PARAMETERS.put(18, new int[]{0,1});         //New Zealand Map Grid
        PROJECTION_PARAMETERS.put(27, new int[]{0,1,6,7});     //Polyconic
        PROJECTION_PARAMETERS.put(26, new int[]{0,2});         //Regional Mercator
        PROJECTION_PARAMETERS.put(20, new int[]{0,1,5,6,7});   //Stereographic
        PROJECTION_PARAMETERS.put(8,  new int[]{0,1,5,6,7});   //Transverse Mercator (Gauss–Kruger)
    }

    /**
     * Get a descriptor list of needed parameters for the projection matching the given code.
     * @param projectionCode The MapInfo code of the projection we want.
     * @return a list of {@link ParameterDescriptor} to fill to build the projection.
     * @throws FactoryException if we can't find the projection pointed by the given code.
     */
    public static String[] getProjectionParameters(int projectionCode) throws FactoryException {
        final int[] parameterIndices = PROJECTION_PARAMETERS.get(projectionCode);
        if(parameterIndices != null) {
            String[] parameters = new String[parameterIndices.length];
            for(int i = 0 ; i < parameterIndices.length ; i++) {
                parameters[i] = PARAMETER_LIST.get(parameterIndices[i]);
            }
            return parameters;
        }

        throw new FactoryException("We're not able to retrieve any parameter for the given code.");
    }

}
