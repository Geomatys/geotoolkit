/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.jdbc;

import org.geotoolkit.data.query.QueryCapabilities;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A default QueryCapabilities implementation for JDBCFeatureSource.
 * 
 * @author Gabriel Roldan (TOPP)
 * @author Andrea Aime (OpenGeo)
 * @version $Id$
 * @since 2.5.4
 */
class JDBCQueryCapabilities extends QueryCapabilities {

    final JDBCFeatureSource source;

    public JDBCQueryCapabilities(final JDBCFeatureSource source) {
        this.source = source;
    }

    @Override
    public boolean supportsSorting(final SortBy[] sortAttributes) {
        if (super.supportsSorting(sortAttributes)) {
            return true;
        }
        for (SortBy sortBy : sortAttributes) {
            if (SortBy.NATURAL_ORDER == sortBy || SortBy.REVERSE_ORDER == sortBy) {
                // we do only if we have a non null primary key
                return !(source.getPrimaryKey() instanceof NullPrimaryKey);
            }
            if (!supportsPropertySorting(sortBy.getPropertyName(), sortBy.getSortOrder())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks for sorting support in the given sort order for a specific attribute type, 
     * given by a PropertyName expression.
     * <p>
     * This default implementation assumes both orders are supported as long as the property
     * name corresponds to the name of one of the attribute types in the complete FeatureType,
     * and that the attribute is not a geometry.
     * </p>
     * 
     * @param propertyName the expression holding the property name to check for sortability
     *            support
     * @param sortOrder the order, ascending or descending, to check for sortability support
     *            over the given property name.
     * @return true if propertyName refers to one of the FeatureType attributes
     */
    protected boolean supportsPropertySorting(final PropertyName propertyName, final SortOrder sortOrder) {
        AttributeDescriptor descriptor = (AttributeDescriptor) propertyName.evaluate(source.getSchema());
        if (descriptor == null) {
            final String attName = propertyName.getPropertyName();
            descriptor = source.getSchema().getDescriptor(attName);
        }
        return descriptor != null && !(Geometry.class.isAssignableFrom(descriptor.getType().getBinding()));
    }
    
    /**
     * Consults the fid mapper for the feature source, if the null feature map reliable fids
     * not supported. 
     */
    @Override
    public boolean isReliableFIDSupported() {
        return !(source.getPrimaryKey() instanceof NullPrimaryKey);
    }
    
    @Override
    public boolean isOffsetSupported() {
        return source.getDataStore().getSQLDialect().isLimitOffsetSupported();
    }
}
