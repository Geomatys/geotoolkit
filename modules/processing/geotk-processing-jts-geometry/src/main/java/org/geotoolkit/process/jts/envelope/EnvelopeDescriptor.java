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
package org.geotoolkit.process.jts.envelope;

import com.vividsolutions.jts.geom.Geometry;
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
public class EnvelopeDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : envelope */
    public static final String NAME = "envelope";
    
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
    public static final ParameterDescriptor<Geometry> RESULT_GEOM =
            new DefaultParameterDescriptor("result_geom", "Envelope geometry result", Geometry.class, null, true);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT_GEOM});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new EnvelopeDescriptor();

    private EnvelopeDescriptor() {
        super(NAME, JTSProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Returns this Geometry bounding box."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new EnvelopeProcess(input);
    }
    
}
