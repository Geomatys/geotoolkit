/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.filter.function.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.function.AbstractFunction;
import org.geotoolkit.filter.function.javascript.JavaScriptFunction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;

/**
 * Groovy function.
 * First parameter is the function equation, following parameters hold the list of all
 * requiered feature properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GroovyFunction extends AbstractFunction {

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
        END_CHARACTERS.add('.');
    }

    private final String groovy;
    private Script compiled;

    public GroovyFunction(final Expression expression) {
        super(GroovyFunctionFactory.GROOVY, prepare(expression), null);
        groovy = expression.evaluate(null, String.class);
    }

    /**
     * Map groovy parameters to PropertyNames.
     * @param gvFunction
     * @return Expression[]
     */
    private static Expression[] prepare(final Expression gvFunction){
        final String str = gvFunction.evaluate(null, String.class);

        final List<Expression> properties = new ArrayList<Expression>();
        properties.add(gvFunction);

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

    private Script getCompiled() throws CompilationFailedException {
        if(compiled == null){
            final GroovyShell shell = new GroovyShell(this.getClass().getClassLoader());
            compiled = shell.parse(groovy);
        }
        return compiled;
    }
    
    @Override
    public Object evaluate(final Object feature) {

        Binding bindings = new Binding();

        for(int i=1,n=parameters.size(); i<n; i++){
            final PropertyName property = (PropertyName) parameters.get(i);
            final Object value = property.evaluate(feature);
            bindings.setVariable(VAR_CHARACTER+property.getPropertyName(), value);
        }

        try {
            final Script script = getCompiled();
            script.setBinding(bindings);
            return script.run();
        } catch (CompilationFailedException ex) {
            Logger.getLogger(JavaScriptFunction.class.getName()).log(Level.WARNING, null, ex);
        }

        return "";
    }



}
