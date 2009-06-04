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
 * Classe enveloppant une portion seulement d'un tableau <code>float[]</code>.
 * Des instances de cette classes sont retourn�es par {@link DefaultArray#subarray}.
 * L'impl�mentation par d�faut de cette classe est imutable. Toutefois, certaines
 * classes d�riv�es (notamment {@link DynamicArray}) ne le seront pas forc�ment.
 *
 * @version $Id: SubArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
class SubArray extends DefaultArray {
    /**
     * Num�ro de s�rie (pour compatibilit� avec des versions ant�rieures).
     */
    private static final long serialVersionUID = 5172936367826790633L;

    /**
     * Plage des donn�es valides du tableau {@link #array}.
     */
    protected int lower, upper;

    /**
     * Enveloppe une partie d'un tableau de <code>float[]</code>.
     *
     * @param  array Tableau de coordonn�es (<var>x</var>,<var>y</var>).
     * @param  lower Index de la premi�re coordonn�es <var>x</var> �
     *         prendre en compte dans le tableau <code>array</code>.
     * @param  upper Index suivant celui de la derni�re coordonn�e <var>y</var> �
     *         prendre en compte dans le tableau <code>array</code>. La diff�rence
     *         <code>upper-lower</code> doit obligatoirement �tre paire.
     */
    public SubArray(final float[] array, final int lower, final int upper) {
        super(array);
        this.lower = lower;
        this.upper = upper;
        checkRange(array, lower, upper);
    }

    /**
     * Retourne l'index de la premi�re coordonn�e valide.
     */
    @Override
    protected final int lower() {
        return lower;
    }

    /**
     * Retourne l'index suivant celui de la derni�re coordonn�e valide.
     */
    @Override
    protected final int upper() {
        return upper;
    }

    /**
     * Returns the point at the specified index.
     *
     * @param  index The index from 0 inclusive to {@link #count} exclusive.
     * @return The point at the given index.
     * @throws IndexOutOfBoundsException if <code>index</code> is out of bounds.
     */
    @Override
    public final Point2D getValue(int index) throws IndexOutOfBoundsException {
        if (index >= 0) {
            index = 2*index + lower;
            if (index < upper) {
                return new Point2D.Float(array[index], array[index+1]);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns an estimation of memory usage in bytes. This method returns the same value
     * than {@link DefaultArray#getMemoryUsage} plus 8 bytes for the internal fields (the
     * {@link #lower} and {@link #upper} fields).
     */
    @Override
    public long getMemoryUsage() {
        return super.getMemoryUsage() + 8;
    }
}
