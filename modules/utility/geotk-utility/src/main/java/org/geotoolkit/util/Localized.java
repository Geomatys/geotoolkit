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
package org.geotoolkit.util;

import java.util.Locale;


/**
 * Interface of localized objects or services.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.Localized}.
 */
@Deprecated
public interface Localized extends org.apache.sis.util.Localized {
    /**
     * Returns the locale of the implemented service. Some implementations may return
     * {@code null} if no locale is explicitly defined. In such case, the locale to use
     * is typically the {@linkplain Locale#getDefault() default} locale.
     *
     * @return The locale, or {@code null} if not explicitly defined.
     */
    @Override
    Locale getLocale();
}
