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


/**
 * Provides the mechanism for {@link Graduation} objects to return the values and labels of their
 * ticks one tick at a time. This interface returns tick values from some minimal value up to some
 * maximal value, using some increment value. Note that the increment value <strong>may not be
 * constant</strong>. For example, a graduation for the time axis may use a slightly variable
 * increment between differents months, since all months doesn't have the same number of days.
 * <p>
 * On construction or after a call to {@link #rewind}, the {@code TickIterator} is positionned
 * on the first minor or major tick (i.e. calls to {@link #currentValue} return the value of the
 * first tick). The method {@link #next} or {@link #nextMajor} must be invoked in order to move
 * to the next tick.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public interface TickIterator {
    /**
     * Tests if the iterator position is behind the latest valid tick. This method can be invoked
     * after {@code TickIterator} creation, or after a call to {@link #next} or {@link #nextMajor}
     * methods, for determining if the iteration is done.
     *
     * @return {@code false} if the iterator position is on a valid tick.
     */
    boolean isDone();

    /**
     * Tests if the current tick is a major one.
     *
     * @return {@code true} if current tick is a major tick, or {@code false} if it is a minor tick.
     */
    boolean isMajorTick();

    /**
     * Returns the position where to draw the current tick.  The position is scaled
     * from the graduation's minimum to maximum.    This is usually the same number
     * than {@link #currentValue}. The mean exception is for logarithmic graduation,
     * in which the tick position is not proportional to the tick value.
     *
     * @return The position where to draw the current tick.
     */
    double currentPosition();

    /**
     * Returns the value for current tick. The current tick may be major or minor.
     *
     * @return The value for the current tick.
     */
    double currentValue();

    /**
     * Returns the label for the current tick. This method is usually invoked only for major
     * ticks, but may be invoked for minor ticks as well. This method returns {@code null} if
     * it can't produces a label for the current tick.
     *
     * @return The label for the current tick.
     */
    String currentLabel();

    /**
     * Moves the iterator to the next minor or major tick.
     */
    void next();

    /**
     * Moves the iterator to the next major tick. This move ignore any minor ticks between
     * the current position and the next major tick.
     */
    void nextMajor();

    /**
     * Resets the iterator on its first tick.
     * All other properties are left unchanged.
     */
    void rewind();
}
