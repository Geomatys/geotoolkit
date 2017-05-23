/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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
package org.geotoolkit.feature.op;

import java.util.Collections;
import java.util.Map;
import static org.apache.sis.feature.AbstractIdentifiedType.NAME_KEY;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Property;
import org.opengis.filter.expression.Expression;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Operation which evaluates a filter expression.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ExpressionOperation extends AbstractOperation {

    private static final ParameterDescriptorGroup EMPTY_PARAMS = CalculateLineStringOperation.parameters("Expression", 1);

    private static final AttributeType<Object> TYPE = new DefaultAttributeType<>(
            Collections.singletonMap(NAME_KEY, NamesExt.create("Object")),Object.class,1,1,null);

    private final Expression expression;

    public ExpressionOperation(GenericName name, Expression expression) {
        this(Collections.singletonMap(DefaultAttributeType.NAME_KEY, name),expression);
    }

    public ExpressionOperation(Map<String, ?> identification, Expression expression) {
        super(identification);
        this.expression = expression;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return EMPTY_PARAMS;
    }

    @Override
    public IdentifiedType getResult() {
        return TYPE;
    }

    @Override
    public Property apply(Feature feature, ParameterValueGroup parameters) {
        final Attribute<Object> att = TYPE.newInstance();
        att.setValue(expression.evaluate(feature, null));
        return att;
    }
}
