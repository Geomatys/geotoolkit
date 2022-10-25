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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.observation.OMUtils;
import org.geotoolkit.observation.model.OMEntity;
import org.geotoolkit.observation.model.ResponseMode;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationQueryUtilities {

    private static final FilterFactory ff = FilterUtilities.FF;

    public static AbstractObservationQuery getQueryForEntityType(OMEntity entityType) {
        return switch (entityType) {
            case FEATURE_OF_INTEREST -> new SamplingFeatureQuery();
            case OFFERING            -> new OfferingQuery();
            case HISTORICAL_LOCATION -> new HistoricalLocationQuery();
            case LOCATION            -> new LocationQuery();
            case OBSERVED_PROPERTY   -> new ObservedPropertyQuery();
            case PROCEDURE           -> new ProcedureQuery();
            case OBSERVATION         -> new ObservationQuery(OMUtils.OBSERVATION_QNAME, ResponseMode.INLINE, null);
            case RESULT              -> new ResultQuery(OMUtils.OBSERVATION_QNAME, ResponseMode.INLINE, null, null);
            default                  -> throw new IllegalArgumentException("Entity type must not be null.");
        };
    }

    public static AbstractObservationQuery buildQueryForIdentifier(OMEntity entityType, String id) {
        final AbstractObservationQuery query = getQueryForEntityType(entityType);
        Filter filter = ff.resourceId(id);
        query.setSelection(filter);
        return query;
    }

    public static AbstractObservationQuery buildQueryForSensor(OMEntity entityType, String sensorId) {
        return buildQueryForSensors(entityType, Arrays.asList(sensorId));
    }

    public static AbstractObservationQuery buildQueryForSensors(OMEntity entityType, List<String> sensorIds) {
        final AbstractObservationQuery query = getQueryForEntityType(entityType);
        List<Filter> filters = new ArrayList<>();
        for (String sensorId : sensorIds) {
            filters.add(buildFilterForSensor(sensorId));
        }
        Filter filter = null;
        if (filters.size() == 1) {
            filter = filters.get(0);
        } else if (!filters.isEmpty()) {
            filter = ff.or(filters);
        }
        query.setSelection(filter);
        return query;
    }

    public static BinaryComparisonOperator buildFilterForSensor(String sensorId) {
        return ff.equal(ff.property("procedure"), ff.literal(sensorId));
    }
}
