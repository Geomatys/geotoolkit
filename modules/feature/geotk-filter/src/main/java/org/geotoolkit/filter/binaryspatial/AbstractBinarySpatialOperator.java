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
package org.geotoolkit.filter.binaryspatial;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;

/**
 * Immutable abstract binary spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 */
public abstract class AbstractBinarySpatialOperator<E extends Expression,F extends Expression> implements BinarySpatialOperator {

    protected final E left;
    protected final F right;

    protected AbstractBinarySpatialOperator(E left, F right){
        if(left == null || right == null){
            throw new NullPointerException("Left and right expressions can not be null");
        }
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
