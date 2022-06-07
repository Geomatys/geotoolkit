/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2014, Geomatys
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
package org.geotoolkit.filter.function.geometry;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;


/**
 * Factory registering the various functions.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class GeometryFunctionFactory extends AbstractFunctionFactory {

    public static final String BUFFERGEO    = "bufferGeo";
    public static final String ALLPOINTS    = "allPoints";
    public static final String STARTANGLE   = "startAngle";
    public static final String ENDANGLE     = "endAngle";

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put(BUFFERGEO,    BufferGeoFunction.class);
        FUNCTIONS.put(ALLPOINTS,    AllPointsFunction.class);
        FUNCTIONS.put(STARTANGLE,   StartAngleFunction.class);
        FUNCTIONS.put(ENDANGLE,     EndAngleFunction.class);
    }

    public GeometryFunctionFactory() {
        super("geometry", FUNCTIONS);
    }

}
