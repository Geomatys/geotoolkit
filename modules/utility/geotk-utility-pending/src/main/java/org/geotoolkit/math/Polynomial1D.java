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
 * Table avec interpolations polynomiales.
 *
 * @author Numerical Recipes in C
 * @author Martin Desruisseaux pour l'adaptation au Java
 * @version 1.0
 *
 * @see Linear1D
 * @see Spline1D
 */
public class Polynomial1D extends Search1D {

    /**
     * Estimation de l'erreur lors
     * de la dernière interpolation.
     */
    public double dy;
    /**
     * Ces deux vecteurs seront utilisées comme
     * buffers internes par <code>polint</code>.
     */
    private final double[] c,  d;
    /**
     * Index des données à utiliser pour l'interpolation.
     * Ce champs est utilisé comme buffer interne.
     */
    private final int[] index;

    /**
     * Construit un objet servant aux interpolations polynomiales. Ce constructeur reçoit en
     * argument l'ordre des interpolations. Une interpolation d'ordre 2 est une interpolation
     * linéaire. Les interpolations d'ordre trois ou quatre conviennent généralement bien. Il
     * n'est pas conseillé d'utiliser un ordre trop élevé, tel que 8 ou 9.
     *
     * @param n ordre de l'interpolation.
     */
    public Polynomial1D(int n) {
        index = new int[n];
        c = new double[n];
        d = new double[n];
    }

    /**
     * Renvoie la donnée <var>y</var> interpolée au <var>xi</var> spécifié. Les index
     * {@link #klo} et {@link #khi} doivent avoir été trouvés avant l'appel de cette méthode.
     *
     * @param xi			valeur de <var>x</var> pour laquelle on désire une
     *						valeur <var>y</var> interpolée.
     * @param reUseIndex	<code>true</code> s'il faut réutiliser les même
     *						index que ceux de la dernière interpolation.
     * @return				Valeur <var>y</var> interpolée.
     */
    @Override
    protected double interpolate(double xi, boolean reUseIndex) throws ExtrapolationException {
        if (!reUseIndex) {
            copyIndexInto(index);
            if (ignoreYNaN) {
                validateIndex(index, y);
            }
        }

        /*~**************************************************************************************
         *																						*
         *	NUMERICAL RECIPES - polint (section 3.1)											*
         *																						*
         *	Given arrays xa[0..n-1] and ya[0..n-1], and given value x, this	routine returns		*
         *	a value y and an error estimate dy. If P(x) is the polynomial of degree N-1 such	*
         *	that P(xa_i)=ya_i, i=1,...,n, then the returned value y=P(x).						*
         *																						*
         ****************************************************************************************/
        int ns = 0;
        int vi = index[0];
        c[0] = d[0] = y[vi];
        double dif = Math.abs(xi - x[vi]);
        for (int i = 1; i < index.length; i++) {
            vi = index[i];
            double dift = Math.abs(xi - x[vi]);
            if (dift < dif) {
                ns = i;
                dif = dift;
            }
            c[i] = d[i] = y[vi];
        }
        double yi = y[index[ns]];
        for (int m = 1; m < index.length; m++) {
            for (int i = 0; i < index.length - m; i++) {
                final double ho = x[index[i]] - xi;
                final double hp = x[index[i + m]] - xi;
                final double w = c[i + 1] - d[i];
                final double den = ho - hp;
                final double deny = w / den;
                /*~*****************************************************
                 * WARNING: division par 0 dans la ligne précédente    *
                 *			si deux valeurs de x sont trop semblables. *
                 *******************************************************/
                d[i] = hp * deny;
                c[i] = ho * deny;
            }
            dy = (float) (((ns << 1) < (index.length - m)) ? c[ns] : d[--ns]);
            yi += dy;
        }
        return yi;
    }

    /**
     * Indique si la donnée spécifiée en argument est à l'intérieur d'un tableau (c'est-à-dire si
     * elle n'impliquerait pas d'extrapolation). Cette méthode n'exige pas que les données soient
     * dans un ordre quelconque. Toutefois, elle est optimisée pour des données en ordre croissant
     * ou décroissant. Les éventuels NaN ne seront pas pris en compte.
     *
     * @param x tableau de données dans un ordre quelconque.
     * @param xi valeur dont on veut vérifier si elle est dans les limites du tableau.
     * @return <code>false</code> si la valeur de <var>xi</var> est NaN ou en dehors du tableau <var>x</var>.
     */
    public final static boolean isInRangeOf(float x[], float xi) {
        /*
         * Cette boucle n'est pas aussi inutile qu'elle en a l'air,
         * car elle permet de tenir compte des NaN. Ces derniers
         * renvoient toujours <code>false</code> quelque soit la
         * comparaison.
         */
        for (int j = 0; j < x.length; j++) {
            if (xi >= x[j]) {
                /*
                 * Si on trouve un x plus grand, cherche imédiatement un x plus
                 * petit. Si on en trouve un, il s'agit bien d'une interpolation.
                 */
                int i = x.length;
                do {
                    if (xi <= x[--i]) {
                        return true;
                    }
                } while (i > j);
                return false;
            } else if (xi <= x[j]) {
                /*
                 * Si on trouve un x plus petit, cherche imédiatement un x plus
                 * grand. Si on en trouve un, il s'agit bien d'une interpolation.
                 */
                int i = x.length;
                do {
                    if (xi >= x[--i]) {
                        return true;
                    }
                } while (i > j);
                return false;
            }
        }
        return false;
    }

    /**
     * Interpole les NaN trouvés dans le vecteur des <var>y</var>, en les remplaçant directement
     * dans le vecteur des <var>y</var> si possible. Voyez la description de la méthode de la
     * classe de base pour plus de détails.
     *
     * @param dxStart	Plage minimal des <var>x</var> qu'il doit y avoir de chaque côté d'un NaN pour l'interpoler.
     * @param dxStop	Plage maximal des <var>x</var> couvert par les données manquantes pour qu'elles puissent être interpolées.
     * @return			Le tableau des <var>y</var>.
     */
    @Override
    public double[] interpolateNaN(double dxStart, double dxStop) {
        return interpolateNaN(dxStart, dxStop, index.length >= 2 ? null : y);
    }
}
