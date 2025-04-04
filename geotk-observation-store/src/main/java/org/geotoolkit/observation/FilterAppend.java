/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.observation;

/**
 * Contains informations about a filter append in a filter chain. It could has
 * been only a boolean explaining if the filter has been append or not, but some
 * implementations require more information
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FilterAppend {

    public boolean append;

    public FilterAppend() {
        this.append = false;
    }

    public FilterAppend(boolean append) {
        this.append = append;
    }

    public FilterAppend merge(FilterAppend fa) {
        if (fa == null) throw new IllegalArgumentException("can nt merge a null filterAppend");
        this.append = this.append || fa.append;
        return this;
    }
}
