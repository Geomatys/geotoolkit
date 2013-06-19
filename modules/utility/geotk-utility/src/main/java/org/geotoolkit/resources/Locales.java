/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.resources;

import java.util.Locale;
import java.util.MissingResourceException;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.logging.Logging;


/**
 * Utilities methods working with {@link Locale} instances.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.04
 *
 * @since 2.4
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.xml.ValueConverter#toLanguageCode}.
 */
@Deprecated
public final class Locales extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Locales() {
    }

    /**
     * Returns the 3-letters ISO language code if available, or the 2-letters code otherwise.
     *
     * @param  locale The locale for which we want the language.
     * @return The language code, 3 letters if possible or 2 letters otherwise.
     *
     * @since 3.04
     *
     * @deprecated Moved to {@link org.apache.sis.xml.ValueConverter#toLanguageCode}.
     */
    @Deprecated
    public static String getLanguage(final Locale locale) {
        try {
            return locale.getISO3Language();
        } catch (MissingResourceException e) {
            Logging.recoverableException(Locales.class, "getLanguage", e);
            return locale.getLanguage();
        }
    }
}
