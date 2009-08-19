/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.operation.transform;


/**
 * Whatever the grid values backing a {@link GridTransform} are directly the target coordinates
 * or offsets to apply on the source coordinates. If grid values are offsets, they may be added
 * directly to the source coordinates ({@link #OFFSET}) or they may be pre-multiplied by some
 * factor before to be added to source coordinates ({@link #NADCON}).
 *
 * {@note The special offset case (<code>NADCON</code>) is not strictly necessary since it
 *        would be possible to pre-multiply the scale factor straight in the grid. However
 *        the approach taken by <code>GridTransform</code> is to keep major formats unchanged.
 *        This approach allows for example a <code>RenderedImage</code> to share the same
 *        <code>DataBuffer</code> than the one used by <code>GridTransform</code>, and compare
 *        the visual aspect with publications.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public enum GridType {
    /**
     * The grid values give directly the target coordinates. The source coordinates
     * are discarted after the interpolation.
     */
    LOCALIZATION,

    /**
     * The grid values are offsets to add to source coordinates. The source and target
     * coordinates can be in any CRS. In the typical case where to source or the target
     * CRS is {@linkplain org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84 WGS84},
     * then the offsets are in decimal degrees with longitude offset positive toward east
     * and latitude offset positive toward north.
     */
    OFFSET,

    /**
     * The grid values are offsets to apply on source coordinates in seconds of angle.
     * Longitude offset is positive toward <strong>west</strong> and latitude offset is
     * positive toward north.
     */
    NADCON
}
