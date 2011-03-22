/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.util.Set;
import java.util.Collection;
import org.geotoolkit.metadata.iso.extent.DefaultExtent;


/**
 * An ISO 19115 (@code Extent} object where each elements is a singleton.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 3.18
 * @module
 */
final class UniqueExtents extends DefaultExtent {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7784229364828123287L;

    /**
     * Creates a uninitialized instance.
     */
    UniqueExtents() {
    }

    /**
     * Requires the collections to be instances of {@link Set} rather than list.
     * By doing so, we avoid duplicated values. Note however that it is a violation
     * of the principle that {@code Set} should contain only immutable objects.
     */
    @Override
    @SuppressWarnings({"unchecked","rawtypes"})
    protected <E> Class<? extends Collection<E>> collectionType(final Class<E> elementType) {
        return (Class) Set.class;
    }
}
