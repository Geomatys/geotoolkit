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

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.observation.model.OMEntity;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class IdentifierQuery extends AbstractObservationQuery {

    private final String identifier;

    public IdentifierQuery(OMEntity entityType, String identifier) {
        super(entityType);
        ArgumentChecks.ensureNonNull("identifier", identifier);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AbstractObservationQuery noPaging() {
        IdentifierQuery query = new IdentifierQuery(getEntityType(), identifier);
        applyFeatureAttributes(query);
        return query;
    }
}
