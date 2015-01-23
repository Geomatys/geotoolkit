/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import javax.vecmath.Point3d;


/**
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.math.Plane}.
 */
@Deprecated
public class Plane extends org.apache.sis.math.Plane {
    /**
     * Computes the plane's coefficients from the specified points.  Three points
     * are enough for determining exactly the plan, providing that the points are
     * not colinear.
     *
     * @param P1 The first point.
     * @param P2 The second point.
     * @param P3 The third point.
     * @throws ArithmeticException If the three points are colinear.
     */
    public void setPlane(final Point3d P1, final Point3d P2, final Point3d P3)
            throws ArithmeticException
    {
        final double m00 = P2.x*P3.y - P3.x*P2.y;
        final double m01 = P3.x*P1.y - P1.x*P3.y;
        final double m02 = P1.x*P2.y - P2.x*P1.y;
        final double det = m00 + m01 + m02;
        if (det == 0) {
            throw new ArithmeticException("Points are colinear.");
        }
        setEquation(
                ((P2.y-P3.y)*P1.z + (P3.y-P1.y)*P2.z + (P1.y-P2.y)*P3.z) / det,
                ((P3.x-P2.x)*P1.z + (P1.x-P3.x)*P2.z + (P2.x-P1.x)*P3.z) / det,
                ((   m00   )*P1.z + (   m01   )*P2.z + (   m02   )*P3.z) / det);
    }
}
