/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.naming;

import java.util.Locale;
import org.opengis.util.InternationalString;
import org.geotoolkit.util.SimpleInternationalString;


/**
 * An international string delegating its work to an other international string, except for
 * the unlocalized name. The unlocalized name is the one with the "null" locale (rather than
 * an English one). This choice is particular to the Geotoolkit implementation of generic names
 * so we don't expose it publicly.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated To be deleted after we removed the deprecated methods from {@link org.opengis.util.NameFactory}.
 */
@Deprecated
final class InternationalName extends SimpleInternationalString {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1643452642020665230L;

    /**
     * The backing international string for every locales except {@code null}.
     */
    private final InternationalString localized;

    /**
     * Creates a new string with the given unlocalized and localized names.
     *
     * @param unlocalized The name in the {code null} locale.
     * @param localized   The names in all other locales.
     */
    public InternationalName(final String unlocalized, final InternationalString localized) {
        super(unlocalized);
        this.localized = localized;
    }

    /**
     * Returns this string in the given locale.
     *
     * @param  locale The desired locale for the string to be returned, or {@code null}.
     * @return The string in the given locale if available, or in the default locale otherwise.
     */
    @Override
    public String toString(final Locale locale) {
        return (locale != null) ? localized.toString(locale) : toString();
    }
}
