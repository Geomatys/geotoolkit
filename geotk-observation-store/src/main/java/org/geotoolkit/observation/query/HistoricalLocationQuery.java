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
public class HistoricalLocationQuery extends AbstractObservationQuery {

    private Integer decimationSize;

    public HistoricalLocationQuery() {
        super(OMEntity.HISTORICAL_LOCATION);
    }

    public HistoricalLocationQuery(Integer decimationSize) {
        super(OMEntity.HISTORICAL_LOCATION);
        this.decimationSize = decimationSize;
    }

    public Integer getDecimationSize() {
        return decimationSize;
    }

    public void setDecimationSize(Integer decimationSize) {
        this.decimationSize = decimationSize;
    }

    @Override
    public AbstractObservationQuery noPaging() {
        HistoricalLocationQuery query = new HistoricalLocationQuery(decimationSize);
        applyFeatureAttributes(query);
        return query;
    }

}
