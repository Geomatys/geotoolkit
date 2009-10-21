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
 * Table avec interpolations linéaires. Notez que la classe <code>Polynomial1D</code> peut
 * faire le même travail lorsque construit avec l'argument 2, mais cette classe-ci sera
 * plus rapide.
 *
 * @version 1.0
 * @author Martin Desruisseaux
 *
 * @see Polynomial1D
 * @see Spline1D
 * @module pending
 */
public class Linear1D extends Search1D {

    /**
     * Renvoie la donnée <var>y</var> interpolée linéairement au <var>xi</var> spécifié.
     * Les index <code>klo</code> et <code>khi</code> doivent avoir été trouvés avant
     * l'appel de cette méthode.
     *
     * @param xi			valeur de <var>x</var> pour laquelle on désire une
     *						valeur <var>y</var> interpolée.
     * @param reUseIndex	<code>true</code> s'il faut réutiliser les même
     *						index que ceux de la dernière interpolation.
     * @return				Valeur <var>y</var> interpolée.
     */
    @Override
    protected double interpolate(double xi, boolean reUseIndex) throws ExtrapolationException {
        if (ignoreYNaN && !reUseIndex) {
            validateIndex(y);
        }
        if (khi != klo) {
            final double yklo = y[klo];
            final double xklo = x[klo];
            return (y[khi] - yklo) / (float) ((x[khi] - xklo) * (xi - xklo)) + yklo;
        } else {
            return y[khi];
        }
    }

    /**
     *	Interpole les NaN trouvés dans le vecteur des <var>y</var>, en les remplaçant directement
     *	dans le vecteur des <var>y</var> sans créer de vecteur temporaire. Voyez la description de
     *	la méthode de la classe de base pour plus de détails.
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
