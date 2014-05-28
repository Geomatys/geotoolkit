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

package org.geotoolkit.feature.type;

import java.util.Arrays;
import java.util.List;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.OperationType;
import org.opengis.filter.Filter;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultOperationType extends DefaultPropertyType<OperationType> implements OperationType{

    private final AttributeType targetType;
    private final AttributeType resultType;
    private final List<AttributeType> parameters;

    public DefaultOperationType(final Name name, final Class<?> binding,
            final InternationalString description, AttributeType[] parameters) {
        super(name,binding,false,null,null,description);
        this.targetType = null;
        this.resultType = new DefaultAttributeType(name, binding, false, false, null, null, description);
        if(parameters == null) parameters = new AttributeType[0];
        this.parameters = Arrays.asList(parameters);
    }

    public DefaultOperationType(final Name name, final Class<?> binding, final boolean isAbstract,
            final List<Filter> restrictions, final OperationType superType, final InternationalString description,
            AttributeType targetType, AttributeType resultType, AttributeType[] parameters) {
        super(name,binding,isAbstract,restrictions,superType,description);
        this.targetType = targetType;
        this.resultType = resultType;
        this.parameters = Arrays.asList(parameters);
    }

    @Override
    public AttributeType getTarget() {
        return targetType;
    }

    @Override
    public AttributeType getResult() {
        return resultType;
    }

    @Override
    public List<AttributeType> getParameterTypes() {
        return parameters;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
