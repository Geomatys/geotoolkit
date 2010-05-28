/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.feature.calculated;

import org.geotoolkit.feature.DefaultAttribute;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.identity.Identifier;

/**
 * A calculated attribute which value is determinated by the give expression.
 * The expression will be evaluated each time a getValue is made.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedExpressionAttribute extends DefaultAttribute<Object, AttributeDescriptor, Identifier> {

    private final Expression exp;
    private ComplexAttribute related;

    /**
     *
     * @param desc : the descriptor of this attribute
     * @param exp : the expression to evaluate.
     */
    public CalculatedExpressionAttribute(AttributeDescriptor desc, Expression exp) {
        super(null, desc, null);
        this.exp = exp;
    }
    
    public void setRelated(ComplexAttribute relatedFeature) {
        this.related = relatedFeature;
    }

    public ComplexAttribute getRelated() {
        return related;
    }

    @Override
    public Object getValue() {
        return exp.evaluate(related,getType().getBinding());
    }

}
