/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.db.postgres;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.Features;
import org.apache.sis.util.Version;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.FilterUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.filter.ValueReference;

/**
 * Convert filters and expressions in SQL.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PostgresFilterToSQL extends FilterToSQL {

    private final Version pgVersion;

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.db.postgres");

    public PostgresFilterToSQL(final FeatureType featureType, final PrimaryKey pkey, final Version pgVersion) {
        super(featureType, pkey);
        this.pgVersion = pgVersion;
    }

    /**
     * Set the current srid, extract it from feature type.
     * Required when encoding geometry.
     */
    @Override
    protected ValueReference setSRID(ValueReference property) {
        currentsrid = -1;
        if (featureType != null) {
            final AttributeType descriptor;
            final Object propObj = property.apply(featureType);
            if (propObj instanceof Operation) {
                final Operation op = (Operation) propObj;
                descriptor = Features.toAttribute(op).orElse(null);
                if (descriptor != null) {
                    try {
                        featureType.getProperty(descriptor.getName().tip().toString());// throw exception if not found
                        property = FilterUtilities.FF.property(descriptor.getName().tip().toString());
                    } catch (PropertyNotFoundException ex) {
                        LOGGER.log(Level.FINE, "Unsupported Operation property", ex);
                    }
                }
            } else if (propObj instanceof AttributeType) {
                descriptor = (AttributeType) property.apply(featureType);
            } else {
                descriptor = null;
            }
            if (descriptor != null && Geometry.class.isAssignableFrom(descriptor.getValueClass())) {
                Integer srid = (Integer) FeatureExt.getCharacteristicValue(descriptor, JDBCFeatureStore.JDBC_PROPERTY_SRID.getName().toString(), null);
                if (srid != null) {
                    currentsrid = srid;
                }
            }
        }
        return property;
    }

    @Override
    protected boolean emptyAsNull(Geometry geom) {
        // empty geometries are interpreted as Geometrycollection in postgis < 2
        // this breaks the column geometry type constraint so we replace those by null
        return geom.isEmpty() && ((Number) pgVersion.getMajor()).intValue() < 2;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }
}
