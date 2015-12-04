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
package org.geotoolkit.processing.jts.buffer;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
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
public class BufferDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : buffer */
    public static final String NAME = "buffer";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM = new ParameterBuilder()
            .addName("geom")
            .setRemarks("Geometry JTS")
            .setRequired(true)
            .create(Geometry.class, null);
    public static final ParameterDescriptor<Double> DISTANCE = new ParameterBuilder()
            .addName("distance")
            .setRemarks("Distance used to make buffer.")
            .setRequired(true)
            .create(Double.class, null);
    public static final ParameterDescriptor<Integer> SEGMENTS = new ParameterBuilder()
            .addName("segments")
            .setRemarks("Number of segments used to represent a quadrant of a circle.")
            .setRequired(false)
            .create(Integer.class, 0);
    public static final ParameterDescriptor<Integer> ENDSTYLE = new ParameterBuilder()
            .addName("endstyle")
            .setRemarks("The end cap style used. 1 -> Round, 2 -> Flat, 3 -> Square.")
            .setRequired(false)
            .create(Integer.class, 2);
   
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(GEOM,DISTANCE,SEGMENTS,ENDSTYLE);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Geometry> RESULT_GEOM = new ParameterBuilder()
            .addName("result_geom")
            .setRemarks("Buffered geometry result")
            .setRequired(true)
            .create(Geometry.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_GEOM);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new BufferDescriptor();

    private BufferDescriptor() {
        super(NAME, JTSProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Apply JTS buffer to a geometry."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new BufferProcess(input);
    }
    
}
