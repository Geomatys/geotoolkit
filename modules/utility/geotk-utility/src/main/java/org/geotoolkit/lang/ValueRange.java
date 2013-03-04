/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.lang;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;


/**
 * The range of values that a method can return. This is used mostly for metadata objects
 * performing runtime checks.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 * @module
 *
 * @deprecated Replaced by the Apache SIS {@link org.apache.sis.measure.ValueRange}.
 */
@Deprecated
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueRange {
    /**
     * Returns the minimal value that a method can return. The default value is
     * {@linkplain Double#NEGATIVE_INFINITY negative infinity}, which means that
     * there is no minimal value.
     *
     * @return The minimal value.
     */
    double minimum() default Double.NEGATIVE_INFINITY;

    /**
     * Returns the maximal value that a method can return. The default value is
     * {@linkplain Double#POSITIVE_INFINITY positive infinity}, which means that
     * there is no maximal value.
     *
     * @return The maximal value.
     */
    double maximum() default Double.POSITIVE_INFINITY;

    /**
     * {@code true} if the {@linkplain #minimum() minimal} value is inclusive, or {@code false}
     * if it is exclusive. By default the minimum value is inclusive.
     *
     * @return {@code true} if the minimum value is inclusive.
     */
    boolean isMinIncluded() default true;

    /**
     * {@code true} if the {@linkplain #maximum() maximal} value is inclusive, or {@code false}
     * if it is exclusive. By default the maximum value is <strong>inclusive</strong>.
     *
     * @return {@code true} if the maximum value is inclusive.
     */
    boolean isMaxIncluded() default true;
}
