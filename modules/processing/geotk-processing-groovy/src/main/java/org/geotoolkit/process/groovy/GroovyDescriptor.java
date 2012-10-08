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
package org.geotoolkit.process.groovy;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.util.HashMap;

/**
 * @author Christophe Mourette (Geomatys)
 * @module pending
 */
public class GroovyDescriptor extends AbstractProcessDescriptor{

    /**Process name : addition */
    public static final String NAME = "boolean";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<String> SCRIPT =
            new DefaultParameterDescriptor("expression", "script groovy", String.class, null, true);
    public static final ParameterDescriptor<HashMap<String,Object>> VARIABLES =
            new DefaultParameterDescriptor("variables", "map of binding script variable", HashMap.class, null, true);
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{SCRIPT,VARIABLES});

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Boolean> RESULT =
            new DefaultParameterDescriptor("result", "evaluation of condition result", Boolean.class, null, true);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{RESULT});

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new GroovyDescriptor();

    private GroovyDescriptor() {
        super(NAME, GroovyProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Evaluate expression given in parameter w"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new GroovyProcess(input);
    }
    
}
