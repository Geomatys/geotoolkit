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
package org.geotoolkit.util;

import org.apache.sis.util.iso.Types;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;
import org.geotoolkit.lang.Static;


/**
 * Maps ISO identifiers to the GeoAPI types (interfaces or {@linkplain CodeList code lists}).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 *
 * @deprecated Moved to Apache SIS {@link Types}.
 */
@Deprecated
public final class GeoAPI extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private GeoAPI() {
    }

    /**
     * Returns the GeoAPI interface for the given ISO identifier, or {@code null} if none.
     * The identifier argument shall be the value documented in the {@link UML#identifier()}
     * annotation associated with the GeoAPI interface. Examples:
     * <p>
     * <ul>
     *   <li>{@code forUML("CI_Citation")} returns <code>{@linkplain org.opengis.metadata.citation.Citation}.class</code></li>
     *   <li>{@code forUML("CS_AxisDirection")} returns <code>{@linkplain org.opengis.referencing.cs.AxisDirection}.class</code></li>
     * </ul>
     * <p>
     * Only identifiers for the stable part of GeoAPI are recognized. This method does not handle
     * the identifiers for the {@code geoapi-pending} module.
     *
     * @param  identifier The ISO {@linkplain UML} identifier.
     * @return The GeoAPI interface, or {@code null} if the given identifier is unknown.
     *
     * @deprecated Moved to Apache SIS {@link Types#forStandardName(String)}.
     */
    @Deprecated
    public static Class<?> forUML(final String identifier) {
        return Types.forStandardName(identifier);
    }
}
