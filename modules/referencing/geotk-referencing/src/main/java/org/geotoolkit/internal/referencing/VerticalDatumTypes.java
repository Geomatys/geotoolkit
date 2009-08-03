/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.datum.VerticalDatumType;


/**
 * Extensions to the standard set of {@link VerticalDatumType}. Those constants are not in
 * public API because they were intentionaly omitted from ISO 19111, and the ISO experts
 * said that they should really not be public.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
public final class VerticalDatumTypes {
    /**
     * Do not allow instantiation of this class.
     */
    private VerticalDatumTypes() {
    }

    /**
     * A vertical datum for ellipsoidal heights that are measured along the
     * normal to the ellipsoid used in the definition of horizontal datum.
     * <p>
     * Identifier {@code CS_DatumType.CS_VD_Ellipsoidal}
     *
     * @see http://jira.codehaus.org/browse/GEO-133
     */
    public static final VerticalDatumType ELLIPSOIDAL = VerticalDatumType.valueOf("ELLIPSOIDAL");

    /**
     * A vertical datum for orthometric heights that are measured along the plumb line.
     * <p>
     * Identifier {@code CS_DatumType.CS_VD_Orthometric}
     */
    public static final VerticalDatumType ORTHOMETRIC = VerticalDatumType.valueOf("ORTHOMETRIC");

    /**
     * Returns the list of {@code VerticalDatumType}s. This method delegates to
     * {@link VerticalDatumType#values()}, but is declared in this class in order
     * to ensure that the constants declared above are included in the returned set.
     * In other words, this is for making sure that class initialization has been
     * done before the {@code values()} method is invoked.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static VerticalDatumType[] values() {
        return VerticalDatumType.values();
    }
}
