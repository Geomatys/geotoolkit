/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2013, Geomatys
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
package org.geotoolkit.db.mysql;


import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.util.Version;
import org.geotoolkit.db.DefaultJDBCFeatureStore;
import org.geotoolkit.db.FilterToSQL;
import org.geotoolkit.db.reverse.PrimaryKey;
import org.geotoolkit.feature.FeatureExt;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.ValueReference;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MySQLFilterToSQL extends FilterToSQL {

    private final MySQLDialect dialect;
    private final Version msVersion;

    public MySQLFilterToSQL(final MySQLDialect dialect, final FeatureType featureType, final PrimaryKey pkey, final Version msVersion) {
        super(featureType, pkey);
        this.dialect = dialect;
        this.msVersion = msVersion;
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO,     new Comparison("="));      // No "ilike" support.
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO, new Comparison("<>"));
    }

    /**
     * Set the current srid, extract it from feature type.
     * Required when encoding geometry.
     */
    @Override
    protected ValueReference setSRID(ValueReference property) {
        currentsrid = -1;
        if (featureType != null) {
            final AttributeType descriptor = (AttributeType) property.apply(featureType);
            if (AttributeConvention.isGeometryAttribute(descriptor)) {
                currentsrid = (Integer)FeatureExt.getCharacteristicValue(descriptor,DefaultJDBCFeatureStore.JDBC_PROPERTY_SRID.getName().toString(),null);
            }
        }
        return property;
    }

    @Override
    protected boolean emptyAsNull(Geometry geom) {
        return false;
    }

    @Override
    protected void appendArrayElement(final StringBuilder sb, final String escaped) {
        sb.append(escaped);
    }
}
