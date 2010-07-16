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
 */
public class DefaultDelete implements Delete {

    private List<AbstractFeature> features;

    /**
     * 
     */
    public DefaultDelete() {
        this.features = EMPTY_LIST;
    }

    /**
     * 
     * @param features
     */
    public DefaultDelete(List<AbstractFeature> features) {
        this.features = (features == null) ? EMPTY_LIST : features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractFeature> getFeatures() {
        return this.features;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFeatures(List<AbstractFeature> features) {
        this.features = features;
    }
}
