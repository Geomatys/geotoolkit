/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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


/**
 * Interface of classes for which deprecated instances may exist. Deprecated instances exist in some
 * {@linkplain org.opengis.referencing.AuthorityFactory authority factories} like the EPSG database.
 * Some example of deprecated instances are:
 * <p>
 * <ul>
 *   <li>An {@link org.geotoolkit.referencing.AbstractIdentifiedObject} (typically a CRS)
 *       which has been built from a deprecated EPSG code.</li>
 *   <li>A {@link org.geotoolkit.referencing.NamedIdentifier} containing the legacy name
 *       of an object which has been renamed.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.util.Deprecable}.
 */
@Deprecated
public interface Deprecable extends org.apache.sis.util.Deprecable {
    /**
     * Returns {@code true} if this instance is deprecated.
     *
     * @return {@code true} if this instance is deprecated.
     */
    boolean isDeprecated();
}
