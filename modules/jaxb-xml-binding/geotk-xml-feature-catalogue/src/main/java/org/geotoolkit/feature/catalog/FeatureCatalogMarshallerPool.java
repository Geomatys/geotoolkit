/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 * 
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.feature.catalog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.feature.catalog.util.MultiplicityImpl;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class FeatureCatalogMarshallerPool {
    
    private static final MarshallerPool INSTANCE;
    static {
        try {
            INSTANCE = new MarshallerPool(JAXBContext.newInstance(
                    DefaultMetadata.class,
                    AssociationRoleImpl.class, BindingImpl.class, BoundFeatureAttributeImpl.class,
                    ConstraintImpl.class, DefinitionReferenceImpl.class, DefinitionSourceImpl.class,
                    FeatureAssociationImpl.class, FeatureAttributeImpl.class, FeatureCatalogueImpl.class,
                    FeatureOperationImpl.class, FeatureTypeImpl.class, InheritanceRelationImpl.class,
                    ListedValueImpl.class, PropertyTypeImpl.class, MultiplicityImpl.class), null);
        } catch (JAXBException ex) {
            throw new AssertionError(ex); // Should never happen, unless we have a build configuration problem.
        }
    }

    private FeatureCatalogMarshallerPool() {}

    public static MarshallerPool getInstance() {
        return INSTANCE;
    }
}
