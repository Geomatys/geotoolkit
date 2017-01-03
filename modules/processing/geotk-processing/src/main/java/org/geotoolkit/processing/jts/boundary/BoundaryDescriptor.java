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
package org.geotoolkit.processing.jts.boundary;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public class BoundaryDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : boundary */
    public static final String NAME = "jts:boundary";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Geometry> GEOM =new ParameterBuilder()
            .addName("geom")
            .setRemarks("Geometry JTS")
            .setRequired(true)
            .create(Geometry.class, null);
    
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(GEOM);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Geometry> RESULT_GEOM =new ParameterBuilder()
            .addName("result_geom")
            .setRemarks("Boundary geometry result")
            .setRequired(true)
            .create(Geometry.class, null);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT_GEOM);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new BoundaryDescriptor();

    private BoundaryDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Return boundarty geometry of an input JTS geometry"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new BoundaryProcess(input);
    }
    
}
