/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.filter.function.feature;

import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.feature.Feature;
import org.opengis.filter.Expression;

/**
 * Extract feature type name.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureTypeNameFunction extends AbstractFunction {

    public FeatureTypeNameFunction(final Expression expr1) {
        super("featureTypeName");
    }

    @Override
    public Object apply(final Object feature) {
        if (feature instanceof Feature) {
            return ((Feature)feature).getType().getName().toString();
        }
        return "";
    }
}
