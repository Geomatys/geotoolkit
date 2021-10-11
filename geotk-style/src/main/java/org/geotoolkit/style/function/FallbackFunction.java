/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.function;

import java.util.Collections;
import java.util.List;
import org.geotoolkit.filter.AbstractExpression;

import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.util.ScopedName;

/**
 * Class for review: initial implementation for a normal categorize function;
 * note problem with variable number of agruments (from commit at r30500).
 *
 * @author Jody Garnett
 */
public class FallbackFunction extends AbstractExpression implements Expression<Object,Object> {
    private List<Expression<Object,?>> parameters;
    private Literal fallback;
    private String name;

    public FallbackFunction( final String name, final List<Expression<Object,?>> parameters, final Literal fallback ){
        this.name = name;
        this.parameters = parameters;
        this.fallback = fallback;
    }

    public String getName() {
        return name;
    }

    @Override
    public ScopedName getFunctionName() {
        return createName(name);
    }

    public List<Expression<Object,?>> getParameters() {
        return Collections.unmodifiableList( parameters );
    }

    @Override
    public Object apply(final Object object) {
        return fallback.apply(object);
    }

    public Literal getFallbackValue() {
        return fallback;
    }
}
