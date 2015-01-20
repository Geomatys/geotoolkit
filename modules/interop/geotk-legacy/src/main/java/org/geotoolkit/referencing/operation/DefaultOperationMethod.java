/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.io.ObjectStreamException;
import org.opengis.referencing.operation.Formula;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.referencing.AbstractIdentifiedObject;


/**
 * @deprecated Moved to {@link org.apache.sis.referencing.operation}.
 * @module
 */
@Deprecated
public class DefaultOperationMethod extends AbstractIdentifiedObject implements OperationMethod {
    private static final long serialVersionUID = -8181774670648793964L;

    private final Formula formula;
    private final Integer sourceDimension;
    private final Integer targetDimension;
    private final ParameterDescriptorGroup parameters;

    public DefaultOperationMethod(final Map<String,?> properties,
                                  final Integer sourceDimension,
                                  final Integer targetDimension,
                                  final ParameterDescriptorGroup parameters)
    {
        super(properties);
        this.formula         = (Formula) properties.get(FORMULA_KEY);
        this.parameters      = parameters;
        this.sourceDimension = sourceDimension;
        this.targetDimension = targetDimension;
    }

    @Override
    public Class<? extends OperationMethod> getInterface() {
        return OperationMethod.class;
    }

    @Override
    public Formula getFormula() {
        return formula;
    }

    @Override
    public Integer getSourceDimensions() {
        return sourceDimension;
    }

    @Override
    public Integer getTargetDimensions() {
        return targetDimension;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return (parameters != null) ? parameters : Parameters.EMPTY_GROUP;
    }

    protected Object writeReplace() throws ObjectStreamException {
        return org.apache.sis.referencing.operation.DefaultOperationMethod.castOrCopy(this);
    }

    protected Object readResolve() throws ObjectStreamException {
        return org.apache.sis.referencing.operation.DefaultOperationMethod.castOrCopy(this);
    }
}
