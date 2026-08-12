/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.observation;

import org.geotoolkit.ogcwxs.request.RequestParameters;

/**
 * To retrieve a description of the type of an observed property.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DescribeObservationType extends RequestParameters {

    private String observedProperty;

    /**
     * Observed property whose type is requested. (required)
     *
     * @return the observedProperty
     */
    public String getObservedProperty() {
        return observedProperty;
    }

    public void setObservedProperty(String observedProperty) {
        this.observedProperty = observedProperty;
    }

    public DescribeObservationType observedProperty(String observedProperty) {
        setObservedProperty(observedProperty);
        return this;
    }

}
