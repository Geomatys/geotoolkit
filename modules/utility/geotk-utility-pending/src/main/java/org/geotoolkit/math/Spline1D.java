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
 *	Table avec interpolations cubiques B-Spline.
 *
 * @author Numerical Recipes in C
 * @author Martin Desruisseaux
 * @version 1.0
 *
 * @see Linear1D
 * @see Polynomial1D
 * @module pending
 */
public class Spline1D extends Search1D {

    /**
     * Différence entre les <var>x</var> de part et d'autres de la donnée interpolée.
     * Par exemple si le vecteur des <var>x</var> est [2 4 7 8] et que vous demandez
     * la valeur de <var>y</var> interpolée à <var>x</var>=5, alors le champs <var>dx</var>
     * aura la valeur 3.
     */
    public double dx;
    /**
     * Valeur de la dérivée première au premier et dernier point de la courbe.
     * Peut être NaN si l'on veut poser que la dérivée deuxième doit être nulle
     * aux extrémités.
     */
    private final double yp1,  ypn;
    /**
     * Vecteur de dérivés deuxième. Ce vecteur ne sera
     * construit qu'au besoin, le plus tard possible.
     */
    private double y2[];
    /**
     * Vecteur sentinelle. Avant la construction de <var>y2</var> (c'est-à-dire lorsque
     * <var>y2</var> est nul), ce champs est égal à <var>y</var>. Après la construction
     * de <var>y2</var>, il sera égal à <var>y2</var>.
     */
    private double sentry[];

    /**
     * Construit un interpolateur avec les paramètres par défaut.
     */
    public Spline1D() {
        yp1 = ypn = Double.NaN;
    }

    /**
     * Construit un interpolateur qui utilisera les valeurs spécifiées de la
     * dérivé première au premier et dernier point. Si une ou les deux dérivées premières
     * ne sont pas connues, vous pouvez spécifier NaN. Notez que ces dérivées premières
     * seront appliquées au premiers et derniers points valides des vecteurs <var>x</var>
     * et <var>y</var>. Si ces vecteurs contiennent des NaN, ceux-ci seront ignorés avant
     * l'application de ces dérivées premières.
     *
     * @param yp1 valeur de la dérivée première au premier point.
     * @param ypn valeur de la dérivée première au dernier point.
     */
    public Spline1D(double yp1, double ypn) {
        this.yp1 = yp1;
        this.ypn = ypn;
    }

    @Override
    public void clear() {
        super.clear();
        y2 = null;
        sentry = y;
    }

    /**
     * Signale à la table que des données du vecteurs des <var>x</var> ou du vecteur des
     * <var>y</var> ont été modifiées. L'appel de cette méthode signalera à cet objet qu'il
     * devra reconstruire son vecteur de dérivées deuxièmes avant la prochaine interpolation.
     */
    @Override
    public void recompute() {
        y2 = null;
        sentry = y;
    }

    /**
     * Renvoie la donnée <var>y</var> interpolée au <var>xi</var> spécifié. Les index
     * {@link #klo} et {@link #khi} doivent avoir été trouvés avant l'appel de cette méthode.
     *
     * @param xi			Valeur de <var>x</var> pour laquelle on désire une
     *						valeur <var>y</var> interpolée.
     * @param reUseIndex	<code>true</code> s'il faut réutiliser les même
     *						index que ceux de la dernière interpolation.
     * @return				Valeur <var>y</var> interpolée.
     */
    @Override
    protected double interpolate(double xi, boolean reUseIndex) throws ExtrapolationException {
        if (ignoreYNaN && !reUseIndex) {
            validateIndex(sentry);
        }
        if (khi != klo) {
            if (y2 == null) {
                constructY2();
            }
            /*~******************************************************************
             *																	*
             * NUMERICAL RECIPES - splint (section 3.3)							*
             *																	*
             *	Given the arrays x1[0..n-1] and ya[0..n-1], wich tabulate a		*
             *	function (with the xa_i's in order) and given the array			*
             *	y2a[0..n-1] wich is the output from spline above, and given a	*
             *	value of x_i, this routine returns a cubic-spline interpolated	*
             *	value y.														*
             *																	*
             ********************************************************************/
            double a = x[khi];
            double b = x[klo];
            dx = a - b;
            a = (a - xi) / dx;
            b = (xi - b) / dx;
            return (float) (a * y[klo] + b * y[khi] + ((a * a * a - a) * y2[klo] + (b * b * b - b) * y2[khi]) * (dx * dx) / 6.0);
        } else {
            return y[khi];
        }
    }

    /**
     * Construit le vecteur de dérivées deuxièmes.
     * Ce vecteur est nécessaire au fonctionnement
     * de la méthode <code>interpolate</code>.
     */
    private final void constructY2() {
        /*~**********************************************************************************
         *																					*
         * NUMERICAL RECIPES - spline (section 3.3)											*
         *																					*
         *	Given arrays x[0..n-1] and y[0..n-1] containing a tabulated fonction,			*
         *	i.e. y=f(x_i), with x_1 < x_2 < x_N, and given yp1 and ypn for the first		*
         *	derivative of the interpolating function at points 1 and n respectively,		*
         *	this routine returns an array y2[0..n-1] that contains the second derivatives	*
         *	of the interpolating function at the tabulated points x_i. If yp1 and/or yp2	*
         *	are equal to 1E+30 or larger, the routine is signaled to set the corresponding	*
         *	boundary condition for a natural spline, with zero second derivative on that	*
         *	boundary.																		*
         *																					*
         *	Adapté du C par Martin Desruisseaux.											*
         ************************************************************************************/
        sentry = y2 = new double[y.length];
        final double u[] = new double[y.length];

        // Search first valid index.
        int previous = 0;
        while (true) {
            if (previous >= y.length) {
                throwArrayIndexOutOfBoundsException(2);
            }
            if (Double.isNaN(x[previous]) || Double.isNaN(y[previous])) {
                y2[previous++] = Double.NaN;
            } else {
                break;
            }
        }

        // Search current valid index.
        int i = previous;
        while (true) {
            if (++i >= y.length) {
                throwArrayIndexOutOfBoundsException(2);
            }
            if (Double.isNaN(x[i]) || Double.isNaN(y[i])) {
                y2[i] = Double.NaN;
            } else {
                break;
            }
        }

        // Left boundary condition.
        if (!Double.isNaN(yp1)) {
            final double dx = x[i] - x[previous];
            y2[previous] = -0.5f;
            u[previous] = (float) ((3.0 / dx) * ((y[i] - y[previous]) / dx - yp1));
        } else {
            y2[previous] = u[previous] = 0;
        }

        // Compute Y2 using next valid index.
        int next = i;
        while (++next < y.length) {
            if (!Double.isNaN(x[next]) && !Double.isNaN(y[next])) {
                final double xp = x[previous];
                final double xi = x[i];
                final double xn = x[next];
                final double sig = (xi - xp) / (xn - xp);
                final double p = sig * y2[previous] + 2.0;
                y2[i] = (float) ((sig - 1.0) / p);
                u[i] = (float) ((y[next] - y[i]) / (xn - xi) - (y[i] - y[previous]) / (xi - xp));
                u[i] = (float) ((6.0 * u[i] / (xn - xp) - sig * u[previous]) / p);
                previous = i;
                i = next;
            } else {
                y2[next] = Float.NaN;
            }
        }

        // Right boundary condition
        if (!Double.isNaN(ypn)) {
            final double dx = x[i] - x[previous];
            final double un = (3.0 / dx) * (ypn - (y[i] - y[previous]) / dx);
            y2[i] = (float) ((un - 0.5 * u[previous]) / (0.5 * y2[previous] + 1.0));
        } else {
            y2[i] = 0;
        }

        // Finish Y2 computation
        do {
            if (!Double.isNaN(y2[previous])) {
                y2[previous] = y2[previous] * y2[i] + u[previous];
                i = previous;
            }
        } while (--previous >= 0);
    }

    /**
     * Interpole les NaN trouvés dans le vecteur des <var>y</var>, en les remplaçant directement
     * dans le vecteur des <var>y</var> sans créer de vecteur temporaire. Voyez la description de
     * la méthode de la classe de base pour plus de détails.
     *
     * @param dxStart	Plage minimal des <var>x</var> qu'il doit y avoir de chaque côté d'un NaN pour l'interpoler.
     * @param dxStop	Plage maximal des <var>x</var> couvert par les données manquantes pour qu'elles puissent être interpolées.
     * @return			Le tableau des <var>y</var>.
     */
    @Override
    public double[] interpolateNaN(double dxStart, double dxStop) {
        return interpolateNaN(dxStart, dxStop, y);
    }
}
