/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display.axis;

import java.text.NumberFormat;
import org.apache.sis.math.MathFunctions;


/**
 * Itérateur balayant les barres et étiquettes de graduation d'un axe logarithmique.
 * Cet itérateur retourne les positions des graduations à partir de la valeur minimale
 * jusqu'à la valeur maximale.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
final class LogarithmicNumberIterator extends NumberIterator {
    /**
     * Scale and offset factors for {@link #currentPosition}
     */
    private double scale, offset;

    /**
     * Construit un itérateur par défaut. La méthode {@link #init}
     * <u>doit</u> être appelée avant que cet itérateur ne soit utilisable.
     */
    protected LogarithmicNumberIterator(final NumberFormat format) {
        super(format);
    }

    /**
     * Initialise l'itérateur.
     *
     * @param minimum
     *          Valeur minimale de la première graduation.
     * @param maximum
     *          Valeur limite des graduations. La dernière
     *          graduation n'aura pas nécessairement cette valeur.
     * @param visualLength
     *          Longueur visuelle de l'axe sur laquelle tracer la graduation.
     *          Cette longueur doit être exprimée en pixels ou en points.
     * @param visualTickSpacing
     *          Espace à laisser visuellement entre deux marques de graduation.
     *          Cet espace doit être exprimé en pixels ou en points (1/72 de pouce).
     */
    @Override
    protected void init(final double minimum,
                        final double maximum,
                        final float  visualLength,
                        final float  visualTickSpacing)
    {
        final double logMin = Math.log10(minimum);
        final double logMax = Math.log10(maximum);
        super.init(logMin, logMax, visualLength, visualTickSpacing);
        scale  = (maximum-minimum) / (logMax-logMin);
        offset = minimum - scale*logMin;
    }

    /**
     * Returns the position where to draw the current tick. The
     * position is scaled from the graduation's minimum to maximum.
     */
    @Override
    public double currentPosition() {
        return super.currentPosition() * scale + offset;
    }

    /**
     * Retourne la valeur de la graduation courante. Cette méthode
     * peut être appelée pour une graduation majeure ou mineure.
     */
    @Override
    public double currentValue() {
        return MathFunctions.pow10(super.currentValue());
    }
}
