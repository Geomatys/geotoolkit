/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.math;


/**
 * An enumeration for a few common number set (Natural, real).
 * This enumeration is ordered: each set contains fully the set declared before.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
public enum NumberSet {
    /**
     * The set of positive integers (\u2155), including zero.
     */
    NATURAL('\u2155'),

    /**
     * The set of positive and negative integers (\u2124).
     */
    INTEGER('\u2124'),

    /**
     * The set of rational numbers (\u211A).
     */
    RATIONAL('\u211A'),

    /**
     * The set of real numbers (\u211D).
     */
    REAL('\u211D');

    /**
     * The symbol for this set (\u2155, \u2124, \u211A or \u211D).
     */
    public final char symbol;

    /**
     * Creates a new instance for the given symbol.
     */
    private NumberSet(final char symbol) {
        this.symbol = symbol;
    }
}
