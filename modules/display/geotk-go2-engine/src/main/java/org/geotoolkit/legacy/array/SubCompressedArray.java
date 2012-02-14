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

import org.geotoolkit.resources.Errors;


/**
 * Classe enveloppant une portion seulement d'un tableau {@link CompressedArray}.
 *
 * @version $Id: SubCompressedArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
final class SubCompressedArray extends CompressedArray {
    /**
     * Num�ro de s�rie (pour compatibilit� avec des versions ant�rieures).
     */
    private static final long serialVersionUID = 4702506646824251468L;

    /**
     * Plage des donn�es valides du tableau {@link #array}.
     */
    protected final int lower, upper;

    /**
     * Construit un sous-tableau � partir d'un autre tableau compress�.
     *
     * @param  other Tableau source.
     * @param  lower Index de la premi�re coordonn�es <var>x</var> �
     *         prendre en compte dans le tableau <code>other</code>.
     * @param  upper Index suivant celui de la derni�re coordonn�e <var>y</var> �
     *         prendre en compte dans le tableau <code>other</code>. La diff�rence
     *         <code>upper-lower</code> doit obligatoirement �tre paire.
     */
    SubCompressedArray(final CompressedArray other, final int lower, final int upper) {
        super(other, lower);
        this.lower  = lower;
        this.upper  = upper;
        if (upper-lower < 2) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_RANGE_$2,
                                               new Integer(lower), new Integer(upper)));
        }
        if (((upper-lower)&1) !=0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ODD_ARRAY_LENGTH_$1,
                                               new Integer(upper-lower)));
        }
        if (lower < 0) {
            throw new ArrayIndexOutOfBoundsException(lower);
        }
        if (upper > other.array.length) {
            throw new ArrayIndexOutOfBoundsException(upper);
        }
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
     * Returns an estimation of memory usage in bytes. This method returns the same value
     * than {@link CompressedArray#getMemoryUsage} plus 8 bytes for the internal fields
     * (the {@link #lower} and {@link #upper} fields).
     */
    @Override
    public long getMemoryUsage() {
        return super.getMemoryUsage() + 8;
    }
}
