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
import org.opengis.feature.Feature;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultCreate implements Create {

    private List<Feature> containers;

    /**
     *
     */
    public DefaultCreate() {
        this.containers = EMPTY_LIST;
    }

    /**
     * 
     * @param containers
     */
    public DefaultCreate(List<Feature> containers) {
        this.containers = (containers == null) ? EMPTY_LIST : containers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Feature> getContainers() {
        return this.containers;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setContainers(List<Feature> containers) {
        this.containers = (containers == null) ? EMPTY_LIST : containers;
    }
}
