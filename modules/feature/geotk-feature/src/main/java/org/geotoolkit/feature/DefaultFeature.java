/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.util.ArrayList;
import java.util.Collection;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;

/**
 * Default feature implementation.
 *
 * @author jdeolive
 * @author jgarnett
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 */
public class DefaultFeature extends AbstractFeature<Collection<Property>> {

    /**
     * Create a Feature with the following content.
     *
     * @param properties Collectio of Properties (aka Attributes and/or Associations)
     * @param desc Nested descriptor
     * @param id Feature ID
     */
    public DefaultFeature(final Collection<? extends Property> properties, final AttributeDescriptor desc, final FeatureId id) {
        super(desc, id);
        value = new ArrayList<Property>(properties);
    }

    /**
     * Create a Feature with the following content.
     *
     * @param properties Collectio of Properties (aka Attributes and/or Associations)
     * @param type Type of feature to be created
     * @param id Feature ID
     */
    public DefaultFeature(final Collection<Property> properties, final FeatureType type, final FeatureId id) {
        super(type,id);
        value = new ArrayList<Property>(properties);
    }

}
