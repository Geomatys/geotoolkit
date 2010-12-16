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
 * Define a TiePoint.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class TiePoint {

    final double rasterI;
    final double rasterJ;
    final double rasterK;
    final double coverageX;
    final double coverageY;
    final double coverageZ;

    /**
     * 2 dimensions tie point.
     * rasterK and coverageZ are set to 0;
     */
    public TiePoint(double rasterI, double rasterJ, double coverageX, double coverageY) {
        this(rasterI,rasterJ,0,coverageX,coverageY,0);
    }

    /**
     * 3 dimensions tie point.
     */
    public TiePoint(double rasterI, double rasterJ, double rasterK, double coverageX, double coverageY, double coverageZ) {
        this.rasterI = rasterI;
        this.rasterJ = rasterJ;
        this.rasterK = rasterK;
        this.coverageX = coverageX;
        this.coverageY = coverageY;
        this.coverageZ = coverageZ;
    }

}
