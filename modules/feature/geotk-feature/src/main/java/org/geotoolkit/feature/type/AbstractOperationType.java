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

import org.opengis.feature.IdentifiedType;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 *
 * @deprecated To be replaced by {@link org.apache.sis.feature.DefaultOperation}.
 */
@Deprecated
public abstract class AbstractOperationType extends DefaultPropertyType<OperationType> implements OperationType{

    private final IdentifiedType targetType;
    private final ParameterDescriptorGroup parameters;

    public AbstractOperationType(final Name name, final InternationalString description,
            AttributeType resultType, ParameterDescriptorGroup parameters) {
        super(name,resultType.getBinding(),false,null,null,description);
        this.targetType = resultType;
        this.parameters = parameters;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return parameters;
    }

    @Override
    public IdentifiedType getResult() {
        return targetType;
    }
}
