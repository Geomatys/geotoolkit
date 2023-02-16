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
public class ObservedPropertyQuery extends AbstractObservationQuery {

    private boolean noCompositePhenomenon = false;

    public ObservedPropertyQuery() {
        super(OMEntity.OBSERVED_PROPERTY);
    }

    public ObservedPropertyQuery(boolean noCompositePhenomenon) {
        super(OMEntity.OBSERVED_PROPERTY);
        this.noCompositePhenomenon = noCompositePhenomenon;
    }

    public boolean isNoCompositePhenomenon() {
        return noCompositePhenomenon;
    }

    public void setNoCompositePhenomenon(boolean noCompositePhenomenon) {
        this.noCompositePhenomenon = noCompositePhenomenon;
    }

    @Override
    public ObservedPropertyQuery noPaging() {
        ObservedPropertyQuery query = new ObservedPropertyQuery(noCompositePhenomenon);
        applyFeatureAttributes(query);
        return query;
    }
}
