/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.legacy.array;

import java.awt.geom.Point2D;


/**
 * It�rateur balayant les donn�es d'un tableau {@link DefaultArray}.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/array/DefaultIterator.java $
 * @version $Id: DefaultIterator.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
final class DefaultIterator extends PointIterator {
    /**
     * Tableau de donn�es � balayer.
     */
    private final float[] array;

    /**
     * Index suivant celui de la derni�re donn�e � balayer.
     */
    private final int upper;

    /**
     * Index de la prochaine donn�e � retourner.
     */
    private int index;

    /**
     * Construit un it�rateur qui balaiera la plage sp�cifi�e d'un tableau de donn�es.
     */
    public DefaultIterator(float[] array, int lower, int upper) {
        this.array = array;
        this.index = lower;
        this.upper = upper;
        assert (index & 1) == 0;
    }

    /**
     * Indique si les m�thodes {@link #next} peuvent retourner d'autres donn�es.
     */
    @Override
    public boolean hasNext() {
        return index < upper;
    }

    /**
     * Retourne la valeur de la longitude courante. Avant d'appeller
     * une seconde fois cette m�thode, il faudra <g>obligatoirement</g>
     * avoir appel� {@link #nextY}.
     */
    @Override
    public float nextX() {
        assert (index & 1) == 0;
        return array[index++];
    }

    /**
     * Retourne la valeur de la latitude courante, puis avance au point
     * suivant. Chaque appel de cette m�thode doit <g>obligatoirement</g>
     * avoir �t� pr�c�d�e d'un appel � la m�thode {@link #nextX}.
     */
    @Override
    public float nextY() {
        assert (index & 1) != 0;
        return array[index++];
    }

    /**
     * Retourne la valeur du point courant dans un objet {@link Point2D},
     * puis avance au point suivant. Cette m�thode combine un appel de
     * {@link #nextX} suivit de {@link #nextY}.
     */
    @Override
    public Object next() {
        assert (index & 1) == 0;
        return new Point2D.Float(array[index++], array[index++]);
    }
}
