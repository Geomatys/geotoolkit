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
package org.geotoolkit.style;

import java.util.Objects;
import org.opengis.filter.Expression;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.StyleVisitor;

import static org.geotoolkit.style.StyleConstants.*;

/**
 * Immutable implementation of Types contrast enhancement.
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
    public DefaultContrastEnhancement(final ContrastMethod type, final Expression gamma){
        this.type = (type == null) ? ContrastMethod.NONE : type;
        this.gamma = (gamma == null) ? DEFAULT_CONTRAST_ENHANCEMENT_GAMMA : gamma;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final StyleVisitor visitor, final Object extraData) {
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
    public boolean equals(final Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultContrastEnhancement other = (DefaultContrastEnhancement) obj;

        return Objects.equals(this.gamma, other.gamma)
                && Objects.equals(this.type, other.type);
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
        final StringBuilder builder = new StringBuilder();
        builder.append("[ContrastEnhancement : Gamma=");
        builder.append(gamma);
        builder.append(" Method=");
        builder.append(type);
        builder.append(']');
        return builder.toString();
    }
}
