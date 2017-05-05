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

import org.geotoolkit.processing.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Set;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.Map;
import org.geotoolkit.process.ProcessException;

/**
 * This process allows you to run a condition written in groovy and retrieve the result.
 *
 * @author Christophe Mourette (Geomatys)
 * @module
 */
public class GroovyConditionProcess extends AbstractProcess {

    public GroovyConditionProcess(final ParameterValueGroup input) {
        super(GroovyDescriptor.INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {

        final Map variables = value(GroovyDescriptor.VARIABLES, inputParameters);
        final String expression = value(GroovyDescriptor.SCRIPT,inputParameters);
        final String behavior = value(GroovyDescriptor.BEHAVIOR,inputParameters);

        final Binding binding = new Binding();
        final GroovyShell shell = new GroovyShell(binding);
        final Set<String> keys = variables.keySet();
        for (String key : keys){
            shell.setVariable(key, variables.get(key));
        }
        Object result = shell.evaluate(expression);

        if ("EXCEPTION".equals(behavior)) {
            if (result != null && result instanceof Boolean && !((Boolean) result)) {
                throw new ProcessException("Groovy expression failed."+expression, this, null);
            }
        }


        getOrCreate(GroovyDescriptor.RESULT, outputParameters).setValue(result);
    }

}
