/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.visitor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.sis.internal.filter.FunctionNames;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.util.GenericName;
import org.opengis.filter.ValueReference;


/**
 * A simple visitor that extracts every attribute used by a filter or an expression
 */
public class FilterAttributeExtractor extends DefaultFilterVisitor<Set<GenericName>> {
    /**
     * Last set visited
     */
    private final Set<GenericName> attributeNames = new HashSet<>();

    /**
     * Just extract the property names; don't check against a feature type.
     */
    public FilterAttributeExtractor() {
        this(null);
    }

    /**
     * Use the provided feature type as a sanity check when extracting
     * property names.
     */
    public FilterAttributeExtractor(final FeatureType featureType) {
        setExpressionHandler(FunctionNames.ValueReference, (e, data) -> {
            final ValueReference<Object,?> expression = (ValueReference<Object,?>) e;
            if (data != null && data != attributeNames) {
                attributeNames.addAll(data);
            }
            if (featureType != null) {
                // evaluate against the feature type instead of using straight name
                // since the path from the property name may be an xpath or a
                // namespace prefixed string
                PropertyType type = (PropertyType) expression.apply(featureType);
                if (type != null) {
                   attributeNames.add( type.getName() );
                } else {
                   attributeNames.add(NamesExt.valueOf(expression.getXPath()));
                }
            } else {
                attributeNames.add(NamesExt.valueOf(expression.getXPath()));
            }
        });
    }

    /**
     * @return an unmofiable set of the attribute names found so far during the visit
     */
    public Set<GenericName> getAttributeNameSet() {
        return Collections.unmodifiableSet(attributeNames);
    }

    /**
     * @return an array of the attribute names found so far during the visit
     */
    public GenericName[] getAttributeNames() {
        return attributeNames.toArray(new GenericName[attributeNames.size()]);
    }

    /**
     * Resets the attributes found so that a new attribute search can be performed
     */
    public void clear() {
        attributeNames.clear();
    }
}
