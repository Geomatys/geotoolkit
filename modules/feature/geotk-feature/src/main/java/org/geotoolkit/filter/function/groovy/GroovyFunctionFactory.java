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

import java.util.Collections;
import org.geotoolkit.filter.function.AbstractFunctionFactory;

/**
 * Factory registering groovy functions.
 * Groovy dependency is heavy, so it is provided scope.
 * Explicitly add the dependency if you which groovy functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GroovyFunctionFactory extends AbstractFunctionFactory {

    public static final String GROOVY = "groovy";

    public GroovyFunctionFactory() {
        super(GROOVY, Collections.singletonMap(GROOVY, (Class)GroovyFunction.class));
    }

    /**
     * Test if groovy shell is available.
     * @return true if available.
     */
    private static boolean isAvailable(){
        try{
            Class.forName("groovy.lang.GroovyShell");
            return true;
        }catch(ClassNotFoundException ex){
            return false;
        }
    }

}
