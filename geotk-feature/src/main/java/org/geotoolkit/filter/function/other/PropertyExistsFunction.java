/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.function.other;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;

/**
 * A new function to check if a property exists.
 * @module
 */
public class PropertyExistsFunction extends AbstractFunction {

    public PropertyExistsFunction(final Expression parameter) {
        super(OtherFunctionFactory.PROPERTY_EXISTS, parameter);
    }

    private String getPropertyName() {
        Expression expr = (Expression) getParameters().get(0);
        return getPropertyName(expr);
    }

    private String getPropertyName(final Expression expr) {
        String propertyName;
        if (expr instanceof Literal) {
            propertyName = String.valueOf(((Literal) expr).getValue());
        } else if (expr instanceof ValueReference) {
            propertyName = ((ValueReference) expr).getXPath();
        } else {
            throw new IllegalStateException("Not a property name expression: " + expr);
        }
        return propertyName;
    }

    /**
     * @return {@link Boolean#TRUE} if the <code>feature</code>'s
     *         {@link FeatureType} contains an attribute named as the property
     *         name passed as this function argument, {@link Boolean#FALSE}
     *         otherwise.
     */
    public Object evaluate(final Feature feature) {
        String propName = getPropertyName();
        try{
            feature.getType().getProperty(propName);
            return true;
        }catch(PropertyNotFoundException ex){
            return false;
        }
    }

    /**
     * @return {@link Boolean#TRUE} if the Class of the object passed as
     *         argument defines a property names as the property name passed as
     *         this function argument, following the standard Java Beans naming
     *         conventions for getters. {@link Boolean#FALSE} otherwise.
     */
    @Override
    public Object apply(final Object bean) {
        if (bean instanceof Feature) {
            return evaluate((Feature) bean);
        }

        final String propName = getPropertyName();

        Boolean propertyExists = Boolean.TRUE;

        try {
            PropertyUtils.getProperty(bean, propName);
        } catch (NoSuchMethodException e) {
            propertyExists = Boolean.FALSE;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return propertyExists;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyExists('");
        sb.append(getPropertyName());
        sb.append("')");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PropertyExistsFunction)) {

            return false;
        }
        PropertyExistsFunction other = (PropertyExistsFunction) obj;

        if (other.getParameters().size() != this.getParameters().size()) {
            return false;
        }
        if (other.getParameters().size() > 0) {
            final String propName = getPropertyName();
            final Expression otherPropNameExpr = (Expression) other.getParameters().get(0);
            final String otherPropName = getPropertyName(otherPropNameExpr);

            return Objects.equals(propName, otherPropName);
        } else {
            return true;
        }
    }
}
