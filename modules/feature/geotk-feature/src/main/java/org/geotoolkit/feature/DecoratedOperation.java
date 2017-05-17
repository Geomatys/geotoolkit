/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature;

import org.apache.sis.feature.AbstractOperation;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.Property;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DecoratedOperation extends AbstractOperation {

    private final Operation base;

    public DecoratedOperation(Operation base) {
        super(DecoratedFeatureType.properties(base));
        this.base = base;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return base.getParameters();
    }

    @Override
    public IdentifiedType getResult() {
        return base.getResult();
    }

    @Override
    public Property apply(Feature feature, ParameterValueGroup parameters) {
        if (feature instanceof DecoratedFeature) {
            return base.apply(((DecoratedFeature) feature).getDecoratedFeature(), parameters);
        } else {
            throw new IllegalArgumentException("Invalid input feature, was expecting a feature of type DecoratedFeature");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj instanceof DecoratedOperation){
            final DecoratedOperation op = (DecoratedOperation) obj;
            return op.base.equals(this.base);
        }
        return false;
    }

}
