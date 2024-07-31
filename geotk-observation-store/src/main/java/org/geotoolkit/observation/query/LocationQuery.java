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

import org.geotoolkit.observation.model.OMEntity;
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class LocationQuery extends AbstractObservationQuery {

    public LocationQuery() {
        super(OMEntity.LOCATION);
    }

    public LocationQuery(Filter filter) {
        super(OMEntity.LOCATION, filter);
    }

    @Override
    public AbstractObservationQuery noPaging() {
        LocationQuery query = new LocationQuery();
        applyFeatureAttributes(query);
        return query;
    }
}
