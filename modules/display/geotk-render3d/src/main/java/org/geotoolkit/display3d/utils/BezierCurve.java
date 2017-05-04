/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.utils;

import javax.vecmath.Vector3d;
import java.util.List;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public final class BezierCurve {

    private BezierCurve(){}

    public static double factoriel(int i){
        double f = 1.0;
        for (int j=2; j<=i; j++){
            f *= j;
        }
        return f;
    }

    public static int[] pascalCoef(int ordre){

        if (ordre < 0) return null;

        final int[] tmp1 = new int[ordre+1];
        final int[] tmp2 = new int[ordre+1];
        tmp1[0] = tmp2[0] = 1;

        for (int i=1; i <= ordre; i++){
            for (int j=1; j<=i; j++){
                tmp1[j] = tmp2[j];
                tmp2[j] = tmp1[j-1]+tmp1[j];
            }
        }
        return tmp2;
    }

    public static double bezierCoef(final int i, final int n, final double u){
        return (factoriel(n)/(factoriel(i)*factoriel(n-i))) * Math.pow(u, i) * Math.pow(1-u, n-i);
    }

    public static Vector3d bezierCurve(final List<Vector3d> Pi, final double u){
        Vector3d point = new Vector3d(0.0, 0.0, 0.0);
        final int n = Pi.size()-1;
        if (n >= 0){
            final int[] pascalCoef = BezierCurve.pascalCoef(n);
            for (int i=0; i<=n; i++){
                final double coef = pascalCoef[i] * Math.pow(u, i) * Math.pow(1-u, n-i);

                point.x += coef*Pi.get(i).x;
                point.y += coef*Pi.get(i).y;
                point.z += coef*Pi.get(i).z;
            }
        }
        return point;
    }

    public static Vector3d bezierDerivativeCurve(final List<Vector3d> Pi, final double u){
        Vector3d point = new Vector3d(0.0, 0.0, 0.0);
        final int n = Pi.size()-1;
        final int order = n-1;
        if (n >= 0){
            final int[] pascalCoef = BezierCurve.pascalCoef(order);
            for (int i=0; i<=order; i++){
                final double coef = pascalCoef[i] * Math.pow(u, i) * Math.pow(1-u, order-i);

                final Vector3d Qi = new Vector3d();
                Qi.sub(Pi.get(i+1), Pi.get(i));
                Qi.scale(n);

                point.x += coef*Qi.x;
                point.y += coef*Qi.y;
                point.z += coef*Qi.z;
            }
        }
        return point;
    }

    public static Vector3d bezierSecondCurve(final List<Vector3d> Pi, final double u){
        Vector3d point = new Vector3d(0.0, 0.0, 0.0);
        final int n = Pi.size()-1;
        final int order = n-2;
        if (n >= 0){
            final int[] pascalCoef = BezierCurve.pascalCoef(order);
            for (int i=0; i<=order; i++){
                final double coef = pascalCoef[i] * Math.pow(u, i) * Math.pow(1-u, order-i);

                final Vector3d Qi0 = new Vector3d();
                Qi0.sub(Pi.get(i+1), Pi.get(i));
                Qi0.scale(n);

                final Vector3d Qi1 = new Vector3d();
                Qi1.sub(Pi.get(i+2), Pi.get(i+1));
                Qi1.scale(n);

                final Vector3d Ri = new Vector3d();
                Ri.sub(Qi1, Qi0);
                Ri.scale(n);

                point.x += coef*Ri.x;
                point.y += coef*Ri.y;
                point.z += coef*Ri.z;
            }
        }
        return point;
    }

}
