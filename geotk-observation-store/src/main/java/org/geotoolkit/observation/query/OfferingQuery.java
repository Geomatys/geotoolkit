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
public class OfferingQuery extends AbstractObservationQuery {

    public OfferingQuery() {
        super(OMEntity.OFFERING);
    }

    public OfferingQuery(Filter selection) {
        super(OMEntity.OFFERING, selection);
    }

    @Override
    public AbstractObservationQuery noPaging() {
        OfferingQuery query = new OfferingQuery();
        applyFeatureAttributes(query);
        return query;
    }
}
