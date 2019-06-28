/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.geometry;

import org.opengis.geometry.coordinate.PointArray;


/**
 * Geometry represented by control points.
 * They are typically line strings.
 */
public interface SampledByPoints {
    /**
     * Returns an ordered array of point values that lie on the curve.
     * In most cases, these will be related to control points used in the construction of the segment.
     *
     * <div class="note"><b>Note:</b>
     * The control points of a curve segment are used to control its shape, and are not always on the
     * curve segment itself. For example in a spline curve, the curve segment is given as a weighted
     * vector sum of the control points. Each weight function will have a maximum within the
     * constructive parameter interval, which will roughly correspond to the point on the curve
     * where it passes closest that the corresponding control point. These points, the values of
     * the curve at the maxima of the weight functions, will be the sample points for the curve
     * segment.
     * </div>
     *
     * @return the control points.
     */
    PointArray getSamplePoints();
}
