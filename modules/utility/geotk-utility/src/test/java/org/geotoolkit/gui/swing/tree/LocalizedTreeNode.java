/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.gui.swing.tree;

import java.util.Locale;


/**
 * A tree node using a given locale.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.17
 */
final strictfp class LocalizedTreeNode extends DefaultMutableTreeNode {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -7415936590693379946L;

    /**
     * The locale, or {@code null} if unspecified.
     */
    Locale locale;

    /**
     * Creates a new tree node with the given user object.
     */
    public LocalizedTreeNode(final Object userObject) {
        super(userObject);
    }

    /**
     * Returns the locale, or {@code null} if unspecified.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }
}
