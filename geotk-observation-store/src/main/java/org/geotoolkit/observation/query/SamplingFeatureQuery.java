/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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

/**
 *
 * @author guilhem
 */
public class SamplingFeatureQuery extends AbstractObservationQuery {

    public SamplingFeatureQuery() {
        super(OMEntity.FEATURE_OF_INTEREST);
    }

    @Override
    public SamplingFeatureQuery noPaging() {
        SamplingFeatureQuery query = new SamplingFeatureQuery();
        applyFeatureAttributes(query);
        return query;
    }
}
