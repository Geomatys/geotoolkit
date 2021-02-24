/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2021, Geomatys
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
package org.geotoolkit.util;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Guilhem legal (Geomatys)
 */
public interface DeltaComparable {

    public boolean equals(Object o, float delta);

    public static boolean equals(List<? extends DeltaComparable> o1, List<? extends DeltaComparable> o2, float delta) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 != null && o2 != null && o1.size() == o2.size()) {
            for (int i = 0; i < o1.size(); i++) {
                if (!equals(o1.get(i), o2.get(i), delta)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean equals(Object o1, Object o2, float delta) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 != null && o2 != null) {
            if (o1 instanceof DeltaComparable && o2 instanceof DeltaComparable) {
                return ((DeltaComparable) o1).equals(o2, delta);
            } else {
                return Objects.equals(o1, o2);
            }
        }
        return false;
    }

    public static boolean arrayEquals(double[] d1, double[] d2, float eps) {
        if (d1 == null && d2 == null) {
            return true;
        } else if (d1 != null && d2 != null && d1.length == d2.length) {
            for (int i = 0; i < d1.length; i++) {
                double v1 = d1[i];
                double v2 = d2[i];
                if (!equals(v1, v2, eps)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean arrayEquals(double[][] d1, double[][] d2, float eps) {
        if (d1 == null && d2 == null) {
            return true;
        } else if (d1 != null && d2 != null && d1.length == d2.length) {
            for (int i = 0; i < d1.length; i++) {
                if (!arrayEquals(d1[i], d2[i], eps)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean arrayEquals(double[][][] d1, double[][][] d2, float eps) {
        if (d1 == null && d2 == null) {
            return true;
        } else if (d1 != null && d2 != null && d1.length == d2.length) {
            for (int i = 0; i < d1.length; i++) {
                if (!arrayEquals(d1[i], d2[i], eps)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean arrayEquals(double[][][][] d1, double[][][][] d2, float eps) {
        if (d1 == null && d2 == null) {
            return true;
        } else if (d1 != null && d2 != null && d1.length == d2.length) {
            for (int i = 0; i < d1.length; i++) {
                if (!arrayEquals(d1[i], d2[i], eps)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean equals(double d1, double d2, float epsilon) {
        return Math.abs(d1 - d2) < epsilon;
    }
}
