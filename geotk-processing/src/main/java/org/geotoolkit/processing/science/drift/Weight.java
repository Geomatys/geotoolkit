/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.processing.science.drift;

/**
 * @author Alexis Manin (Geomatys)
 */
public final class Weight implements Comparable<Weight> {
    /**
     * Multiplication factor to apply on oceanic current and to wind speed when computing the drift speed.
     * Note that the {@code current} + {@code wind} sum does not need to be 1.
     */
    public final double current, wind;

    /**
     * The probability that the current and wind scales defined by this {@code Weight} instance happen.
     */
    public final double probability;

    /**
     * Specifies weights to give to oceanic current and wind speed.
     * Note that the {@code current} + {@code wind} sum does not need to be 1.
     *
     * @param current      multiplication factor to apply on oceanic current when computing the drift speed.
     * @param wind         multiplication factor to apply on wind speed when computing the drift speed.
     * @param probability  the probability that the current and wind scales defined by this {@code Weight} instance happen.
     */
    public Weight(final double current, final double wind, final double probability) {
        this.current     = current;
        this.wind        = wind;
        this.probability = probability;
    }

    /**
     * Sorts largest weights first.
     */
    @Override
    public int compareTo(final Weight o) {
        if (probability < o.probability) return +1;
        if (probability > o.probability) return -1;
        return 0;
    }
}
