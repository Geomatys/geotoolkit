/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.query;

import org.apache.sis.storage.FeatureQuery;
import org.geotoolkit.observation.model.OMEntity;
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractObservationQuery extends FeatureQuery {

    private final OMEntity entityType;

    public AbstractObservationQuery(OMEntity entityType) {
        this.entityType = entityType;
    }

    public AbstractObservationQuery(OMEntity entityType, Filter selection) {
        this.entityType = entityType;
        if (selection != null) {
            this.setSelection(selection);
        }
    }

    public OMEntity getEntityType() {
        return entityType;
    }

    public abstract AbstractObservationQuery noPaging();

    protected void applyFeatureAttributes(AbstractObservationQuery query) {
        query.setProjection(this.getProjection());
        query.setLinearResolution(this.getLinearResolution());
        query.setSelection(this.getSelection());
        query.setSortBy(this.getSortBy());
    }
}
