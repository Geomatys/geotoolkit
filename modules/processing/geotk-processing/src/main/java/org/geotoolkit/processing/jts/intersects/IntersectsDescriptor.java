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
package org.geotoolkit.processing.jts.intersects;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.jts.JTSProcessingRegistry;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class IntersectsDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : intersects */
    public static final String NAME = "intersects";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM1 =
            new DefaultParameterDescriptor("geom1", "Geometry JTS source", Geometry.class, null, true);
    public static final ParameterDescriptor<Geometry> GEOM2 =
            new DefaultParameterDescriptor("geom2", "Geometry JTS", Geometry.class, null, true);
    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(GEOM1,GEOM2);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Boolean> RESULT =
            new DefaultParameterDescriptor("result", "Intersects result", Boolean.class, null, true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new IntersectsDescriptor();

    private IntersectsDescriptor() {
        super(NAME, JTSProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Returns true if this geometry source (geom1) intersects the specified geometry (geom2)."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new IntersectsProcess(input);
    }
    
}
