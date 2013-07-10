/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.jaxb.referencing;

import org.opengis.referencing.cs.AxisDirection;
import org.apache.sis.internal.jaxb.gml.CodeListAdapter;


/**
 * JAXB adapter for {@link AxisDirection}, in order to integrate the value in an element
 * complying with OGC/ISO standard.
 *
 * @author Guilhem Legal (Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class CS_AxisDirection extends CodeListAdapter<AxisDirection> {
    /**
     * Ensures that the adapted code list class is loaded.
     */
    static {
        ensureClassLoaded(AxisDirection.class);
    }

    /**
     * Empty constructor for JAXB only.
     */
    public CS_AxisDirection() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<AxisDirection> getCodeListClass() {
        return AxisDirection.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCodeSpace() {
        return "EPSG";
    }
}
