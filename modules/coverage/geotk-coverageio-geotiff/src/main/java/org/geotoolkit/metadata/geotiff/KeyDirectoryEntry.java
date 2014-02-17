/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.metadata.geotiff;

/**
 * Entries in the GeoKeyDirectory are always a set of 4 values.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class KeyDirectoryEntry {

    final int valueKey;
    final int valuelocation;
    final int valueNb;
    final int valueOffset;

    public KeyDirectoryEntry(final int valueKey, final int valuelocation, final int valueNb, final int valueOffset) {
        this.valueKey = valueKey;
        this.valuelocation = valuelocation;
        this.valueNb = valueNb;
        this.valueOffset = valueOffset;
    }

}
