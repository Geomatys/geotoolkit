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
import org.opengis.style.Description;
import org.opengis.style.StyleVisitor;
import org.opengis.util.InternationalString;

/**
 * Immutable implementation of Types description.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultDescription implements Description{

    private final InternationalString title;

    private final InternationalString desc;

    /**
     * Create a default immutable description.
     *
     * @param title : can be null
     * @param desc : can be null
     */
    public DefaultDescription(final InternationalString title, final InternationalString desc){
        this.title = title;
        this.desc = desc;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getTitle() {
        return title;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public InternationalString getAbstract() {
        return desc;
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
    public boolean equals(final Object obj) {

        if(this == obj){
            return true;
        }

        if(obj == null || !this.getClass().equals(obj.getClass()) ){
            return false;
        }

        DefaultDescription other = (DefaultDescription) obj;

        return Objects.equals(this.title, other.title)
                && Objects.equals(this.desc, other.desc);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 1;
        if(title != null) hash += title.hashCode();
        if(desc != null) hash += desc.hashCode();
        return hash ;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[Description : Title=");
        builder.append(title);
        builder.append(" Abstract=");
        builder.append(desc);
        builder.append(']');
        return builder.toString();
    }

}
