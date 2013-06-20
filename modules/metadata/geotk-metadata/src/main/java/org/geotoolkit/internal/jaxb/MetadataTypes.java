/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb;

import java.util.Collection;
import org.apache.sis.metadata.iso.DefaultMetadata;


/**
 * Declares the classes of objects to be marshalled using a default {@code MarshallerPool}. This class
 * is declared in the {@code META-INF/services/org.geotoolkit.internal.jaxb.RegisterableTypes} file.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class MetadataTypes implements RegisterableTypes {
    /**
     * Adds to the given collection the metadata types that should be given to
     * the initial JAXB context.
     */
    @Override
    public void getTypes(final Collection<Class<?>> addTo) {
        addTo.add(DefaultMetadata.class);
    }
}
