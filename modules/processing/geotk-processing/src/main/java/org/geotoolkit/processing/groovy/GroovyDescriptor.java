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
package org.geotoolkit.processing.groovy;

import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Map;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;

/**
 * Definition of the process allows you to run a condition written in groovy and retrieve the result.
 * @author Christophe Mourette (Geomatys)
 * @module
 */
public class GroovyDescriptor extends AbstractProcessDescriptor{

    /**Process name : addition */
    public static final String NAME = "groovy:condition";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<String> SCRIPT = new ParameterBuilder()
            .addName("expression")
            .setRemarks("Script groovy")
            .setRequired(true)
            .create(String.class, null);
    public static final ParameterDescriptor<Map> VARIABLES = new ParameterBuilder()
            .addName("variables")
            .setRemarks("Map of binding script variable")
            .setRequired(true)
            .create(Map.class, null);

    public static final String[] BEHAVIOR_KEYS = new String[] { "EXCEPTION", "RESULT"};
    public static final ParameterDescriptor<String> BEHAVIOR =
            new ExtendedParameterDescriptor<String>("behavior", "Behavior of the process. Could be 'EXCEPTION' or 'RESULT'",
            String.class, BEHAVIOR_KEYS, "RESULT", null, null, null, true, null);

    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(SCRIPT, VARIABLES, BEHAVIOR);

    /**
     * OutputParameters
     */
    public static final ParameterDescriptor<Object> RESULT = new ParameterBuilder()
            .addName("result")
            .setRemarks("Result of the expression")
            .setRequired(true)
            .create(Object.class, null);
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(RESULT);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new GroovyDescriptor();

    private GroovyDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Evaluate expression given in parameter w"),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new GroovyConditionProcess(input);
    }

}
