/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

import org.apache.sis.filter.visitor.FunctionNames;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;

/**
 * Simplify and prepare the filter against a given target class.
 * All propertyName expression will be prepared against it.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PrepareFilterVisitor extends SimplifyingFilterVisitor {

    final Class<?> resultClass;
    final FeatureType inputType;

    public PrepareFilterVisitor(final Class<?> clazz,final FeatureType expectedType) {
        this.resultClass = clazz;
        this.inputType = expectedType;

        setExpressionHandler(FunctionNames.ValueReference, this::handleValueReference);
        // Temporary workaround. Filters created from Filter Encoding XML can specify "PropertyName" instead of "Value reference".
        setExpressionHandler("PropertyName", this::handleValueReference);
    }

    private Object handleValueReference(Expression input) {
        if (!(input instanceof ValueReference)) throw new IllegalArgumentException(
                "An expression of type ValueReference is expected, but we received: "+
                        (input == null ? null : input.getClass().getCanonicalName()));
        final ValueReference expression = (ValueReference) input;
        return FilterUtilities.prepare(expression, resultClass, inputType);
    }
}
