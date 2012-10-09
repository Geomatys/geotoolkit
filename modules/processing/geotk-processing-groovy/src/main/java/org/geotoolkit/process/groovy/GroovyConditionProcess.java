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

import org.geotoolkit.process.AbstractProcess;
import org.opengis.parameter.ParameterValueGroup;

import java.util.HashMap;
import java.util.Set;

import static org.geotoolkit.process.groovy.GroovyDescriptor.*;

import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.parameter.Parameters.value;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * This process allows you to run a condition written in groovy and retrieve the result.
 *
 * @author Christophe Mourette (Geomatys)
 * @module pending
 */
public class GroovyConditionProcess extends AbstractProcess {

    public GroovyConditionProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    @Override
    protected void execute() {

        final HashMap variables = value(VARIABLES, inputParameters);
        final String expression = value(SCRIPT,inputParameters);
        final Binding binding = new Binding();
        final GroovyShell shell = new GroovyShell(binding);
        final Set<String> keys = variables.keySet();
        for (String key : keys){
            shell.setVariable(key, variables.get(key));
        }
        Object result = shell.evaluate(expression);

        getOrCreate(RESULT, outputParameters).setValue(result);
    }

}
