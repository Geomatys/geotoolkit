/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.function.javascript;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;

/**
 * Javascript function.
 * First parameter is the function equation, following parameters hold the list of all
 * requiered feature properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JavaScriptFunction extends AbstractFunction {

    private static final char VAR_CHARACTER = '$';

    private static final Set<Character> END_CHARACTERS = new HashSet<Character>();

    static{
        //commun formating characters
        END_CHARACTERS.add(' ');
        END_CHARACTERS.add('\t');
        END_CHARACTERS.add('\r');

        //math caracters
        END_CHARACTERS.add('+');
        END_CHARACTERS.add('-');
        END_CHARACTERS.add('/');
        END_CHARACTERS.add('*');
        END_CHARACTERS.add('%');
        END_CHARACTERS.add(',');
        END_CHARACTERS.add(';');
        END_CHARACTERS.add(':');
        END_CHARACTERS.add('<');
        END_CHARACTERS.add('>');

        //others
        END_CHARACTERS.add('(');
        END_CHARACTERS.add(')');
        END_CHARACTERS.add('[');
        END_CHARACTERS.add(']');
        END_CHARACTERS.add('{');
        END_CHARACTERS.add('}');
    }

    private final ScriptEngine engine;
    private final CompiledScript compiled;

    public JavaScriptFunction(final Expression expression) throws ScriptException {
        super(JavaScriptFunctionFactory.JAVASCRIPT, prepare(expression), null);

        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");

        compiled = ((Compilable)engine).compile(expression.evaluate(null, String.class));
    }

    private static Expression[] prepare(Expression jsFunction){
        final String str = jsFunction.evaluate(null, String.class);

        final List<Expression> properties = new ArrayList<Expression>();
        properties.add(jsFunction);

        String current = null;

        for(int i=0,n=str.length(); i<n; i++){
            char c = str.charAt(i);
            if(current != null){
                if(END_CHARACTERS.contains(c)){
                    if(!current.isEmpty()){
                        properties.add(new DefaultPropertyName(current));
                    }
                    current = null;
                }else{
                    current += c;
                }
            }else{
                if(c == VAR_CHARACTER){
                    current = "";
                }
            }
        }

        if(current != null && !current.isEmpty()){
            properties.add(new DefaultPropertyName(current));
        }

        return properties.toArray(new Expression[properties.size()]);
    }

    @Override
    public Object evaluate(Object feature) {

        final Bindings bindings = engine.createBindings();

        for(int i=1,n=parameters.size(); i<n; i++){
            final PropertyName property = (PropertyName) parameters.get(i);
            final Object value = property.evaluate(feature);
            bindings.put(VAR_CHARACTER+property.getPropertyName(), value);
        }

        try {
            return compiled.eval(bindings);
        } catch (ScriptException ex) {
            Logger.getLogger(JavaScriptFunction.class.getName()).log(Level.WARNING, null, ex);
        }

        return "";
    }



}
