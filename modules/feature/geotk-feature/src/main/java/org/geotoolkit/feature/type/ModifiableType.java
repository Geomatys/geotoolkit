/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.List;

/**
 * ComplexType giving access to a modifiable list of descriptors.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface ModifiableType extends ComplexType {

    void changeProperty(int index, PropertyDescriptor desc);

    void changeParent(AttributeType parent);

    @Override
    List<PropertyDescriptor> getDescriptors();

    void rebuildPropertyMap();

}
