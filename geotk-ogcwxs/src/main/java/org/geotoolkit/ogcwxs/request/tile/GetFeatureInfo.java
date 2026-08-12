/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.tile;

/**
 * @author Quentin Bialota (Geomatys)
 */
public final class GetFeatureInfo extends GetTile {

    private Integer i;
    private Integer j;
    private String infoFormat;

    /**
     * @return the column index, in pixels, of the point on the tile for which feature information is desired
     */
    public Integer getI() {
        return i;
    }

    /**
     * @param i the i to set
     */
    public void setI(Integer i) {
        this.i = i;
    }

    /**
     * @param i the i to set
     * @return this
     */
    public GetFeatureInfo i(Integer i) {
        this.i = i;
        return this;
    }

    /**
     * @return the row index, in pixels, of the point on the tile for which feature information is desired
     */
    public Integer getJ() {
        return j;
    }

    /**
     * @param j the j to set
     */
    public void setJ(Integer j) {
        this.j = j;
    }

    /**
     * @param j the j to set
     * @return this
     */
    public GetFeatureInfo j(Integer j) {
        this.j = j;
        return this;
    }

    /**
     * @return the infoFormat
     */
    public String getInfoFormat() {
        return infoFormat;
    }

    /**
     * @param infoFormat the infoFormat to set
     */
    public void setInfoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
    }

    /**
     * @param infoFormat the infoFormat to set
     * @return this
     */
    public GetFeatureInfo infoFormat(String infoFormat) {
        this.infoFormat = infoFormat;
        return this;
    }

}
