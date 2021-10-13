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
package org.geotoolkit.db.oracle;

import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.JDBCFeatureStore;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.ValueReference;

/**
 * Convert filters and expressions in SQL.
 *
 * @author Johann Sorel (Geomatys)
 */
public class OracleFilterToSQL extends FilterToSQL {
    public OracleFilterToSQL(final FeatureType featureType, final PrimaryKey pkey) {
        super(featureType, pkey);
    }

    /**
     * Set the current srid, extract it from feature type.
     * Required when encoding geometry.
     */
    @Override
    protected ValueReference setSRID(ValueReference property) {
        currentsrid = -1;
        if (featureType != null) {
            final PropertyType descriptor = (PropertyType) property.apply(featureType);
            if (AttributeConvention.isGeometryAttribute(descriptor)) {
                Integer srid = (Integer) FeatureExt.getCharacteristicValue(descriptor, JDBCFeatureStore.JDBC_PROPERTY_SRID.toString(), null);
                if(srid!=null){
                    currentsrid = srid;
                }
            }
        }
        return property;
    }
}
