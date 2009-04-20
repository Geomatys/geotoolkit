/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.style;

import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Immutable implementation of GeoAPI contrast enhancement.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class DefaultContrastEnhancement implements ContrastEnhancement{

    private final Expression gamma;
    
    private final ContrastMethod type;
    
    /**
     * Create a default immutable contrast enhancement.
     * 
     * @param type : if null will be replaced by ContrastMethod.NONE
     * @param gamma : if null or Expression.NIL will be replaced by default value.
     */
    public DefaultContrastEnhancement(ContrastMethod type, Expression gamma){
        this.type = (type == null) ? ContrastMethod.NONE : type;
        this.gamma = (gamma == null || gamma == NIL) ? DEFAULT_CONTRAST_ENHANCEMENT_GAMMA : gamma;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(StyleVisitor visitor, Object extraData) {
        return visitor.visit(this,extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ContrastMethod getMethod() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getGammaValue() {
        return gamma;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultContrastEnhancement other = (DefaultContrastEnhancement) obj;

        return Utilities.equals(this.gamma, other.gamma)
                && Utilities.equals(this.type, other.type);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = gamma.hashCode();
        if(type != null) hash += type.hashCode();
        
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[ContrastEnhancement : Gamma=");
        builder.append(gamma.toString());
        builder.append(" Method=");
        builder.append(type.toString());
        builder.append(']');
        return builder.toString();
    }
    
}
