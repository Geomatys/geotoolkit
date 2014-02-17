/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.style.bank;

import org.opengis.style.Description;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FilterNode extends AbstractElementNode implements ElementNode{

    private final ElementNode wrap;

    public FilterNode(final ElementNode wrap) {
        super(wrap.getName(),wrap.getDescription(),wrap.getType());
        this.wrap = wrap;
    }

    @Override
    public String getName() {
        return wrap.getName();
    }

    @Override
    public Description getDescription() {
        return wrap.getDescription();
    }

    @Override
    public ElementType getType() {
        return wrap.getType();
    }

    @Override
    public Object getUserObject() {
        return wrap.getUserObject();
    }

    @Override
    public String toString() {
        return wrap.toString();
    }

    @Override
    protected Object createUserObject() {
        throw new UnsupportedOperationException("should never be called");
    }

}
