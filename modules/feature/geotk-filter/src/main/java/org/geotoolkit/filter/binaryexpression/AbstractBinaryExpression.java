/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binaryexpression;

import org.geotoolkit.filter.AbstractExpression;

import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Expression;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Immutable abstract binary expression.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 * @module pending
 */
public abstract class AbstractBinaryExpression<E extends Expression,F extends Expression> 
                                    extends AbstractExpression implements BinaryExpression{

    protected final E left;
    protected final F right;

    protected AbstractBinaryExpression(final E left, final F right){
        ensureNonNull("left", left);
        ensureNonNull("right", right);
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E getExpression1() {
        return left;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F getExpression2() {
        return right;
    }

}
