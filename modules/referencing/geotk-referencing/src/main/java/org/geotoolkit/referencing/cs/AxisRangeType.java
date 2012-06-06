/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;


/**
 * High-level characteristics about the range of ordinate values expected in a coordinate system.
 * For example this enumeration provides a way to specify whatever the range of longitude values
 * is expected to be positive (typically [0 … 360]°) or if the range mixes positive and negative
 * values (typically [-180 … +180]°).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see DefaultEllipsoidalCS#shiftAxisRange(AxisRangeType)
 * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#shiftAxisRange(AxisRangeType)
 * @see org.geotoolkit.referencing.crs.DefaultCompoundCRS#shiftAxisRange(AxisRangeType)
 *
 * @since 3.20
 * @module
 */
public enum AxisRangeType {
    /**
     * The coordinate system uses positive and negative longitude values, typically
     * in the [-180 … +180]° range. The exact range and the angular units may vary.
     */
    SPANNING_ZERO_LONGITUDE,

    /**
     * The coordinate system uses positive longitude values, typically in the [0 … 360]° range.
     * The exact range and the angular units may vary.
     */
    POSITIVE_LONGITUDE

    /*
     * If more enum are added, please edit the {@code AXIS_RANGE_*} constants in the
     * org.geotoolkit.internal.referencing.CRSUtilities class.
     */
}
