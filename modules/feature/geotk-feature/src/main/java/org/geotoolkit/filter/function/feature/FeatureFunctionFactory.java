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

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;

/**
 * Factory registering the various functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureFunctionFactory extends AbstractFunctionFactory {

    public static final String FEATURETYPENAME = "featureTypeName";

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static {
        FUNCTIONS.put(FEATURETYPENAME, FeatureTypeNameFunction.class);
    }

    public FeatureFunctionFactory() {
        super("feature", FUNCTIONS);
    }
}
