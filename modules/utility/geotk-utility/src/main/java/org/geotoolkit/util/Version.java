/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import java.io.Serializable;
import net.jcip.annotations.Immutable;


/**
 * Holds a version number as a sequence of strings separated by either a dot or a dash.
 * The first three strings, usually numbers, are called respectively {@linkplain #getMajor major},
 * {@linkplain #getMinor minor} and {@linkplain #getRevision revision}. For example an
 * EPSG database version code, such as {@code "6.11.2"}, will have major number 6, minor
 * number 11 and revision number 2. Alternatively a Maven version code, such as
 * {@code "3.18-SNAPSHOT"}, will have major version number 3, minor version number 18
 * and revision string "SNAPSHOT".
 * <p>
 * This class provides methods for performing comparisons of {@code Version} objects where
 * major, minor and revision parts are compared as numbers when possible, or as strings otherwise.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Versioned
 *
 * @since 2.4
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Version}.
 */
@Immutable
@Deprecated
public class Version extends org.apache.sis.util.Version implements CharSequence, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6793384507333713771L;

    /**
     * The version of this Geotoolkit.org distribution.
     *
     * @deprecated Moved to {@link Utilities#VERSION.
     */
    @Deprecated
    public static final Version GEOTOOLKIT = new Version("4.00-SNAPSHOT");

    /**
     * The separator characters between {@linkplain #getMajor major}, {@linkplain #getMinor minor}
     * and {@linkplain #getRevision revision} components. Any character in this string fits.
     */
    public static final String SEPARATORS = ".-";

    /**
     * Creates a new version object from the supplied string.
     *
     * @param version The version as a string.
     */
    public Version(final String version) {
        super(version);
    }
}
