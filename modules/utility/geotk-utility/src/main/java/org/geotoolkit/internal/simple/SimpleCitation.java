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
package org.geotoolkit.internal.simple;


/**
 * A trivial implementation of {@link Citation}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 *
 * @deprecated Moved to {@link org.apache.sis.internal.simple.SimpleCitation}.
 */
@Deprecated
public class SimpleCitation extends org.apache.sis.internal.simple.SimpleCitation {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1490465918703910949L;

    /**
     * Creates a new object for the given name.
     *
     * @param title The title to be returned by {@link #getTitle()}.
     */
    public SimpleCitation(final String title) {
        super(title);
    }
}
