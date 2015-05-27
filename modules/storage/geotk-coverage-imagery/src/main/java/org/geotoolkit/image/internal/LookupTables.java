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
package org.geotoolkit.image.internal;

import javax.media.jai.LookupTableJAI;
import org.geotoolkit.lang.Static;


/**
 * Utility methods related to {@link LookupTableJAI}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class LookupTables extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private LookupTables() {
    }

    /**
     * Returns {@code true} if a lookup table built from the given data would
     * be an identity table.
     *
     * @param data The data to check for identities.
     * @return {@code true} if building a table from the given data would be a no-op.
     */
    public static boolean isIdentity(final byte[] data) {
        // Iterates in reverse order so that if data.length > 255, the test fails immediately.
        for (int i=data.length; --i>=0;) {
            if ((data[i] & 0xFF) != i) {
                return false;
            }
        }
        return true;
    }
}
