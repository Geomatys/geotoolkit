/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.jts.centroid;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.jts.JTSProcessingRegistry;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class CentroidDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : centroid */
    public static final String NAME = "centroid";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM =
            new DefaultParameterDescriptor("geom", "Geometry JTS", Geometry.class, null, true);
    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{GEOM});
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Point> RESULT_GEOM =
            new DefaultParameterDescriptor("result_geom", "Geometry centroid result", Point.class, null, true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_GEOM});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new CentroidDescriptor();

    private CentroidDescriptor() {
        super(NAME, JTSProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Return the centroid point of an input JTS geometry"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new CentroidProcess(input);
    }
    
}
