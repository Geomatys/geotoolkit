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
package org.geotoolkit.processing.script;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.utility.parameter.ExtendedParameterDescriptor;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * This process allows you to run a condition written in a script language and retrieve the result.
 *
 * @author Christophe Mourette (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public final class ScriptProcess extends AbstractProcess {

    public ScriptProcess(final ParameterValueGroup input) {
        super(Descriptor.INSTANCE,input);
    }

    @Override
    protected void execute() throws ProcessException {

        final String language = inputParameters.getMandatoryValue(Descriptor.LANGUAGE);
        final Map<String,Object> variables = inputParameters.getMandatoryValue(Descriptor.VARIABLES);
        final String script = inputParameters.getMandatoryValue(Descriptor.SCRIPT);
        final String behavior = inputParameters.getMandatoryValue(Descriptor.BEHAVIOR);

        if (Descriptor.LANGUAGE_GROOVY.equals(language)) {
            final Binding binding = new Binding();
            final GroovyShell shell = new GroovyShell(binding);
            final Set<String> keys = variables.keySet();
            for (String key : keys){
                shell.setVariable(key, variables.get(key));
            }
            Object result = shell.evaluate(script);
            if ("EXCEPTION".equals(behavior)) {
                if (result != null && result instanceof Boolean && !((Boolean) result)) {
                    throw new ProcessException("Groovy expression failed."+script, this, null);
                }
            }
            outputParameters.getOrCreate(Descriptor.RESULT).setValue(result);
        } else if (Descriptor.LANGUAGE_LUA.equals(language)) {

            final Globals bindings = JsePlatform.standardGlobals();
            for (Entry<String,Object> entry : variables.entrySet()) {
                final LuaValue v = CoerceJavaToLua.coerce(entry.getValue());
                bindings.set(entry.getKey(), v);
            }
            final LuaValue result = bindings.load(script).call();
            Object r = CoerceLuaToJava.coerce(result, Object.class);
            outputParameters.getOrCreate(Descriptor.RESULT).setValue(r);
        } else {
            throw new ProcessException("Unsupported script language : " + language, this);
        }
    }

    /**
    * Definition of the process allows you to run a condition written in in ascript language and retrieve the result.
    */
   public static final class Descriptor extends AbstractProcessDescriptor{

       /**Process name : evaluate */
       public static final String NAME = "script:evaluate";

       public static final String BEHAVIOR_EXCEPTION = "EXCEPTION";
       public static final String BEHAVIOR_RESULT = "RESULT";
       public static final String LANGUAGE_GROOVY = "GROOVY";
       public static final String LANGUAGE_LUA = "LUA";

       /**
        * Input parameters
        */
       public static final ParameterDescriptor<String> LANGUAGE = new ParameterBuilder()
               .addName("language")
               .setRemarks("language")
               .setRequired(true)
               .createEnumerated(String.class, new String[]{LANGUAGE_GROOVY, LANGUAGE_LUA}, null);
       public static final ParameterDescriptor<String> SCRIPT = new ParameterBuilder()
               .addName("expression")
               .setRemarks("Script")
               .setRequired(true)
               .create(String.class, null);
       public static final ParameterDescriptor<Map> VARIABLES = new ParameterBuilder()
               .addName("variables")
               .setRemarks("Map of binding script variable")
               .setRequired(true)
               .create(Map.class, null);

       public static final ParameterDescriptor<String> BEHAVIOR =
               new ExtendedParameterDescriptor<String>("behavior", "Behavior of the process. Could be 'EXCEPTION' or 'RESULT'",
               String.class, new String[]{BEHAVIOR_EXCEPTION, BEHAVIOR_RESULT}, BEHAVIOR_RESULT, null, null, null, true, null);

       public static final ParameterDescriptorGroup INPUT_DESC =
               new ParameterBuilder().addName("InputParameters").createGroup(LANGUAGE, SCRIPT, VARIABLES, BEHAVIOR);

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
       public static final ProcessDescriptor INSTANCE = new Descriptor();

       private Descriptor() {
           super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                   new SimpleInternationalString("Evaluate expression given in parameter w"),
                   INPUT_DESC, OUTPUT_DESC);
       }

       @Override
       public org.geotoolkit.process.Process createProcess(final ParameterValueGroup input) {
           return new ScriptProcess(input);
       }

   }
}
