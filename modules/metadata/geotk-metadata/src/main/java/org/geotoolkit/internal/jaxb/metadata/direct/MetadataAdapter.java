/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.metadata.direct;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Base class for adapters from GeoAPI interfaces to their Geotk implementation.
 *
 * @param <BoundType> The GeoAPI interface being adapted.
 * @param <ValueType> The Geotk class implementing the interface.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public abstract class MetadataAdapter<BoundType, ValueType extends BoundType>
        extends XmlAdapter<ValueType,BoundType>
{
    /**
     * Empty constructor for subclasses only.
     */
    protected MetadataAdapter() {
    }

    /**
     * Returns the given object unchanged, to be marshalled directly.
     *
     * @param  value The metadata value.
     * @return The value to marshall (which is the same).
     */
    @Override
    public final BoundType unmarshal(final ValueType value) {
        return value;
    }
}
