/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.filter.binarycomparison;

import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.expression.Expression;

/**
 * Immutable abstract "binary comparison operator".
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 */
public abstract class AbstractBinaryComparisonOperator<E extends Expression,F extends Expression> implements BinaryComparisonOperator{

    protected final E left;
    protected final F right;
    protected final boolean match;

    public AbstractBinaryComparisonOperator(E left, F right, boolean match) {
        if(left == null || right == null){
            throw new NullPointerException("Expressions can not be null");
        }
        this.left = left;
        this.right = right;
        this.match = match;
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

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isMatchingCase() {
        return match;
    }

}
