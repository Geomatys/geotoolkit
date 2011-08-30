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
package org.geotoolkit.mapfile.process;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.filter.expression.Expression;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapfileFilterToOGCFilterDescriptor extends AbstractProcessDescriptor{
        
    /**Process name : MFFilterToOGCFilter */
    public static final String NAME = "MFFilterToOGCFilter";
    
    /**
     * Input parameters
     */
    public static final ParameterDescriptor<String> IN_TEXT =
            new DefaultParameterDescriptor("text", "mapfile expression", String.class, null, true);
    public static final ParameterDescriptor<Expression> IN_REFERENCE =
            new DefaultParameterDescriptor("reference", "Expression might be linked to another value.", Expression.class, null, false);
  
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{IN_TEXT,IN_REFERENCE});
    
    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Object> OUT_OGC =
            new DefaultParameterDescriptor("expression", "Result OGC filter or expression", Object.class, null, false);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",OUT_OGC);

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
