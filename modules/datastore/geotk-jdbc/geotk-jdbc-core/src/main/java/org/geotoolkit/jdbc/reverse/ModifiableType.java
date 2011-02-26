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
package org.geotoolkit.jdbc.reverse;

import java.util.List;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * ComplexType giving access to a modifiable list of descriptors.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
interface ModifiableType extends ComplexType {

    void changeProperty(final int index, PropertyDescriptor desc);

    @Override
    List<PropertyDescriptor> getDescriptors();
    
}
