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
package org.geotoolkit.data.kml.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultChange implements Change {

    private List<Object> objects;

    /**
     * 
     */
    public DefaultChange() {
        this.objects = EMPTY_LIST;
    }

    /**
     *
     * @param objects
     */
    public DefaultChange(List<Object> objects) {
        this.objects = (objects == null) ? EMPTY_LIST : objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getObjects() {
        return this.objects;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setObjects(List<Object> objects) {
        this.objects = (objects == null) ? EMPTY_LIST : objects;
    }
}
