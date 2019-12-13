/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.filter.function.lua;

import java.util.Collections;
import org.geotoolkit.filter.function.AbstractFunctionFactory;

/**
 * Factory registering lua functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LuaFunctionFactory extends AbstractFunctionFactory {

    public static final String LUA = "lua";

    public LuaFunctionFactory() {
        super(LUA, Collections.singletonMap(LUA, (Class)LuaFunction.class));
    }

}
