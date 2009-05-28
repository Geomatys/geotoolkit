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

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Class for review: initial implementation for a normal categorize function; 
 * note problem with variable number of agruments (from commit at r30500).
 *
 * @author Jody Garnett
 */
public class FallbackFunction implements Function {
	private List<Expression> parameters;
	private Literal fallback;
	private String name;
	
	public FallbackFunction( String name, List<Expression> parameters, Literal fallback ){
		this.name = name;
		this.parameters = parameters;
		this.fallback = fallback;
	}
	public String getName() {
		return name;
	}

	public List<Expression> getParameters() {
		return Collections.unmodifiableList( parameters );
	}

	public Object accept(ExpressionVisitor visitor, Object extraData) {
		return visitor.visit( this, extraData );
	}

	public Object evaluate(Object object) {
		return fallback.evaluate(object);
	}

	public <T> T evaluate(Object object, Class<T> context) {
		return fallback.evaluate(object,context);
	}	
	public Literal getFallbackValue() {
		return fallback;
	}

}
