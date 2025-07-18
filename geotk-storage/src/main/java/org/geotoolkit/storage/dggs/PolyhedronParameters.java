/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs;

/**
 * Parameters of the base polyhedron.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class PolyhedronParameters {

    private final PolyhedronOrientation orientation;

    public PolyhedronParameters(PolyhedronOrientation orientation) {
        this.orientation = orientation;
    }

    /**
     * @return polyhedron orientation parameters
     */
    public PolyhedronOrientation getOrientation() {
        return orientation;
    }

}
