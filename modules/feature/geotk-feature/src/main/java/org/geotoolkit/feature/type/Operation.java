/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.DefaultProperty;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@Deprecated
public class Operation extends DefaultProperty<Object, OperationDescriptor>{


    public Operation(OperationDescriptor descriptor) {
        super(null, descriptor);
    }

    public Object getValue(ComplexAttribute feature) {
        final OperationType opType = descriptor.getType();
        final ParameterDescriptorGroup params = opType.getParameters();
        if(params==null || params.descriptors().isEmpty()){
            return opType.invokeGet(feature, null);
        }else{
            throw new IllegalArgumentException("Operation requieres arguments.");
        }
    }

    public void setValue(ComplexAttribute feature, Object value) {
        final OperationType opType = descriptor.getType();
        opType.invokeSet(feature, value);
    }

}
