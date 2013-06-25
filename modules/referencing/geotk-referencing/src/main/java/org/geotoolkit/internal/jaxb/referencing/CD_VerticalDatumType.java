/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.referencing;

import org.opengis.referencing.datum.VerticalDatumType;
import org.apache.sis.internal.jaxb.gml.CodeListAdapter;


/**
 * JAXB adapter for {@link VerticalDatumType}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public final class CD_VerticalDatumType extends CodeListAdapter<VerticalDatumType> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(VerticalDatumType.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public CD_VerticalDatumType() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<VerticalDatumType> getCodeListClass() {
        return VerticalDatumType.class;
    }
}
