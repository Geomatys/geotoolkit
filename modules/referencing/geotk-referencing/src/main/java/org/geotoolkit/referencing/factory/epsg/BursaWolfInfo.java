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
package org.geotoolkit.referencing.factory.epsg;



/**
 * Private structure for {@link DirectEpsgFactory#createBursaWolfParameters} usage.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class BursaWolfInfo {
    /**
     * The value of {@code CO.COORD_OP_CODE}.
     */
    final int operation;

    /**
     * The value of {@code CO.COORD_OP_METHOD_CODE}.
     */
    final int method;

    /**
     * The value of {@code CRS1.DATUM_CODE}.
     */
    final String target;

    /**
     * Fills a structure with the specified values.
     */
    BursaWolfInfo(final int operation, final int method, final String target) {
        this.operation = operation;
        this.method    = method;
        this.target    = target;
    }

    /**
     * MUST returns the operation code. This is required by {@link DirectEpsgFactory#sort}.
     */
    @Override
    public String toString() {
        return String.valueOf(operation);
    }
}
