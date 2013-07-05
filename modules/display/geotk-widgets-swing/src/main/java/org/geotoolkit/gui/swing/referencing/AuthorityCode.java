/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.referencing;

import java.util.Locale;
import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.apache.sis.util.Classes;


/**
 * An element in a {@link AuthorityCodeList}. This element stores the {@linkplain #code code value}.
 * The description name will be fetched when first needed and returned by {@link #toString}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @module
 */
final class AuthorityCode {
    /**
     * The sequential index.
     */
    final int index;

    /**
     * The authority code.
     */
    final String code;

    /**
     * The CRS object description for the {@linkplain #code}.
     * Will be extracted only when first needed.
     */
    private String name;

    /**
     * The authority factory to use for fetching the name. Will be set to {@code null} after
     * {@linkplain #name} has been made available, in order to allow the garbage collector
     * to do its work if possible.
     */
    private AuthorityFactory factory;

    /**
     * Before {@link #toString()} is invoked, this is the locale in which to render the
     * name. After {@code toString()}, this is a {@code null} on success, or any non-null
     * value on failure.
     */
    private Object state;

    /**
     * Creates a temporary code with a prototype value.
     *
     * {@note An example of long name found in the EPSG database is:
     * "Unknown datum based upon the Average Terrestrial System 1977 ellipsoid".}
     */
    AuthorityCode() {
        index = 0;
        code  = "00000000";
        name  = "00000000";
    }

    /**
     * Creates a code from the specified value.
     *
     * @param factory The authority factory.
     * @param code The authority code.
     */
    AuthorityCode(final AuthorityFactory factory, final String code, final int index, final Locale locale) {
        this.factory = factory;
        this.code    = code;
        this.index   = index;
        this.state   = locale;
    }

    /**
     * Returns the name for this code.
     */
    @Override
    public String toString() {
        String name = this.name;
        if (name == null) {
            try {
                name = factory.getDescriptionText(code).toString((Locale) state);
                state = null;
            } catch (FactoryException e) {
                name = e.getLocalizedMessage();
                if (name == null) {
                    name = Classes.getShortClassName(e);
                }
                state = Boolean.FALSE;
            }
            factory = null;
            this.name = name;
        }
        return name;
    }

    /**
     * Returns {@code true} if the call to the {@link #toString()} method failed.
     */
    boolean failure() {
        return state != null;
    }
}
