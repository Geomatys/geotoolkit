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
package org.geotoolkit.process.mapfile;

import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;

import org.opengis.filter.expression.Expression;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MapfileFilterToOGCFilterDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : MFFilterToOGCFilter */
    public static final String NAME = "MFFilterToOGCFilter";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<String> IN_TEXT = new ParameterBuilder()
            .addName("text")
            .setRemarks("mapfile expression")
            .setRequired(true)
            .create(String.class, null);
    public static final ParameterDescriptor<Expression> IN_REFERENCE = new ParameterBuilder()
            .addName("reference")
            .setRemarks("Expression might be linked to another value.")
            .setRequired(false)
            .create(Expression.class, null);
  
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(IN_TEXT,IN_REFERENCE);
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Object> OUT_OGC = new ParameterBuilder()
            .addName("expression")
            .setRemarks("Result OGC filter or expression")
            .setRequired(false)
            .create(Object.class, null);
    
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(OUT_OGC);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new MapfileFilterToOGCFilterDescriptor();

    private MapfileFilterToOGCFilterDescriptor() {
        super(NAME, MapfileProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Transform a Mapfile Expression to OGC Filter/Expression"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new MapfileFilterToOGCFilterProcess(input);
    }
    
}
