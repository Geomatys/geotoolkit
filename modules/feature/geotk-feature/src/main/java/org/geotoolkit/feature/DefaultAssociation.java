/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 Geomatys
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

import org.opengis.feature.Association;
import org.opengis.feature.Attribute;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeType;

/**
 * Default implementation of an association.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultAssociation extends DefaultProperty<Attribute,AssociationDescriptor> implements Association {

    protected DefaultAssociation(final Attribute value, final AssociationDescriptor descriptor) {
        super(value, descriptor);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getRelatedType() {
        return descriptor.getType().getRelatedType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AssociationType getType() {
        return descriptor.getType();
    }
    
}
