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
package org.geotoolkit.referencing.dggs;

/**
 * Orientation parameters of the base polyhedron.
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.ogc.org/as/20-040r3/20-040r3.html#toc34
 * @see https://docs.ogc.org/DRAFTS/21-038r1.html#annex-dggrs-def
 */
public final class PolyhedronOrientation {

    private final double latitude;
    private final double longitude;
    private final double azimuth;
    private final String description;

    public PolyhedronOrientation(double latitude, double longitude, double azimuth, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimuth = azimuth;
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public String getDescription() {
        return description;
    }

}
