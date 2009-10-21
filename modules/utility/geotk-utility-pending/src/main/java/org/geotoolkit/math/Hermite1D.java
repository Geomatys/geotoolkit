/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.math;

/**
 * Hermite Spline interpolation.
 *
 * To compute Hermite spline interpolation of a tabulated function. Hermite interpolation computes
 * the cubic polynomial that agrees with the tabulated function and its derivative at the two nearest 
 * tabulated points. It may be preferable to Lagrangian interpolation when either (1) the first
 * derivatives are known, or (2) one desires continuity of the first derivative of the interpolated
 * values. <code>HermiteSpline</code> will numerically compute the necessary derivatives, if they are
 * not supplied.<p>
 *
 *	NOTES:	The algorithm here is based on the FORTRAN code discussed by 
 *			Hill, G. 1982, Publ Dom. Astrophys. Obs., 16, 67. The original 
 *			FORTRAN source is U.S. Airforce. Surveys in Geophysics No 272.<p>
 *
 *			<code>HermiteSpline</code> will return an error if one tries to
 *			interpolate any values outside of the range of the input table<p>
 *
 *	REVISION HISTORY:<br>
 *      Written, B. Dorman (GSFC) Oct 1993, revised April 1996<br>
 *      Added FDERIV keyword,  W. Landsman (HSTX)  April 1996<br>
 *      Test for out of range values  W. Landsman (HSTX) May 1996<p>
 *
 *  EXAMPLES FROM THE ORIGINAL IDL CODE:<br>
 *      Interpolate the function 1/x at x = 0.45 using tabulated values
 *      with a spacing of 0.1
 *
 *  <blockquote><code>
 *          IDL> x = indgen(20)*0.1 + 0.1<br>
 *          IDL> y = 1/x<br>
 *          IDL> print,hermite(x,y,0.45)
 *  </code></blockquote>
 *
 *      This gives 2.2188 compared to the true value 1/0.45 = 2.2222
 *
 *  <blockquote><code>
 *      IDL> yprime = -/x^2
 *  </code></blockquote>
 *
 *      But in this case we know the first derivatives
 *
 *  <blockquote><code>
 *          IDL> print,hermite(x,y,0.45,fderiv = yprime)
 *          </code><blockquote>
 *					== 2.2219 and so can get a more accurate interpolation
 *          </blockquote>
 *  </blockquote>
 *
 * @author B. Dorman (GSFC) October 1993
 * @author W. Landsman (HSTX) revised April 1996.
 * @author Martin Desruisseaux adaptation from IDL to Java.
 * @version 1.0
 *
 * @see Spline1D
 * @see Polynomial1D
 * @module pending
 */
public class Hermite1D extends Search1D {

    /**
     * Différence entre les <var>x</var> de part et d'autres de la donnée interpolée.
     * Par exemple si le vecteur des <var>x</var> est [2 4 7 8] et que vous demandez
     * la valeur de <var>y</var> interpolée à x=5, alors le champs dx aura la valeur
     * 3.
     */
    public double dx;
    /**
     *	Vecteur de dérivée deuxième. Ce vecteur
     *	n'est pas obligatoire et peut rester nul.
     */
    private double y2[];

    @Override
    public void setData(double[] x, double[] y) {
        super.setData(x, y);
        y2 = null;
    }

    /**
     * Construit un interpolateur utilisant les vecteurs spécifiés.
     *
     * @param x		Vector giving tabulated <var>X</var> values of function to be interpolated.
     *				Must be either monotonic increasing or decreasing.
     * @param y		Tabuluated values of function, same number of elements as <var>x</var>.
     * @param y2	Function derivative values computed at <var>x</var>. If not supplied,
     *				then <code>HermiteSpline</code> will compute the derivatives numerically.
     *				The <var>y2</var> parameter is useful either when (1) the derivative
     *				values are (somehow) known to better accuracy than can be computed numerically,
     *				or (2) when <code>HermiteSpline</code> is called repeatedly with the same tabulated
     *				function, so that the derivatives need be computed only once.
     */
    public void setData(double[] x, double[] y, double[] y2) {
        super.setData(x, y);
        this.y2 = y2;
    }

    @Override
    public void clear() {
        super.clear();
        y2 = null;
    }

    /**
     * Renvoie la donnée <var>y</var> interpolée au <var>xi</var> spécifié. Les index
     * (@link #klo) et (@link #khi) doivent avoir été trouvés avant l'appel de cette méthode.
     *
     * @param xi			Scalar giving the <var>x</var> values at which to interpolate.
     * @param reUseIndex	<code>true</code> s'il faut réutiliser les même
     *						index que ceux de la dernière interpolation.
     * @return				Interpolated values of function.
     */
    @Override
    protected double interpolate(double xi, boolean reUseIndex) throws ExtrapolationException {
        if (ignoreYNaN && !reUseIndex) {
            validateIndex(y);
        }
        if (klo == khi) {
            return y[klo];
        }
        double dy_klo, dy_khi;
        if (y2 != null) {
            dy_klo = y2[klo];
            dy_khi = y2[khi];
        } else {
            /*
             *	Recherche l'index de la première donnée valide
             *	précèdant [klo]. Typiquement ce sera [klo-1].
             */
            int slo = klo;
            if (--slo < 0 || isNaN(slo)) {
                slo = klo;
            }
            /*
             *	Recherche l'index de la première donnée valide
             *	suivant [khi]. Typiquement ce sera [khi+1].
             */
            int shi = khi;
            if (++shi >= x.length || isNaN(shi)) {
                shi = khi;
            }
            /*
             *	If derivatives were not supplied, then compute numeric
             *	derivatives at the two closest knot points.
             */
            dy_klo = (y[khi] - y[slo]) / (x[khi] - x[slo]); // Dérivé autour de [klo].
            dy_khi = (y[shi] - y[klo]) / (x[shi] - x[klo]); // Dérivé autour de [khi].
        }
        /*
         *	Now finally the Hermite interpolation formula.
         */
        dx = x[khi] - x[klo];
        final double deltaX_klo = xi - x[klo];
        final double deltaX_khi = x[khi] - xi;
        final double rankX_klo = deltaX_klo / dx;
        final double rankX_khi = deltaX_khi / dx;
        return (float) ((y[klo] * (1.0 + 2.0 * rankX_klo) + dy_klo * deltaX_klo) * (rankX_khi * rankX_khi) + (y[khi] * (1.0 + 2.0 * rankX_khi) - dy_khi * deltaX_khi) * (rankX_klo * rankX_klo));
    }

    /**
     * Indique si le vecteur des <var>x</var> ou le vecteur des <var>y</var> contient un NaN
     * à l'index spécifié. Notez que se <code>ignoreYNaN</code> est à <code>false</code>, alors
     * les données du vecteur des <var>y</var> ne seront pas vérifiées.
     *
     * @param index index auquel on veut vérifier s'il y a des NaN.
     * @return <code>true</code> si le vecteur des <var>x</var>
     *         ou des <var>y</var> contient un NaN à cet index.
     */
    private final boolean isNaN(int index) {
        return Double.isNaN(x[index]) || (ignoreYNaN && Double.isNaN(y[index]));
    }
}
