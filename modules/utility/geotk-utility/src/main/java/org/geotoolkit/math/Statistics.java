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
package org.geotoolkit.math;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.sis.util.ArgumentChecks;

import static java.lang.Math.*;
import static java.lang.Double.isNaN;


/**
 * @deprecated Moved to Apache SIS {@link org.apache.sis.math.Statistics}.
 */
@Deprecated
public final class Statistics {
    private Statistics() {
    }

    /**
     * Configures the given formatter for writing a set of data described by this statistics.
     * This method applies the same heuristic rules than {@link #getNumberFormat(Locale)}.
     *
     * @param format The format to configure.
     */
    public static void configure(final org.apache.sis.math.Statistics stats, final NumberFormat format) {
        ArgumentChecks.ensureNonNull("format", format);
        final double min = stats.minimum();
        final double max = stats.maximum();
        final double extremum = max(abs(min), abs(max));
        if (extremum >= 1E+10 || extremum <= 1E-4) {
            /*
             * The above threshold is high so that geocentric and projected coordinates in metres
             * are not formatted with scientific notation (a threshold of 1E+7 is not enough).
             *
             * Below we arbitrarily keep 5 fraction digits (so a total of 6 digits). If this
             * choice is modified, then we should also change the number 5 below in this method.
             */
            if (format instanceof DecimalFormat) {
                ((DecimalFormat) format).applyPattern("0.00000E00");
            }
        } else {
            /*
             * Computes a representative range of values. We take 2 standard deviations away
             * from the mean. Assuming that data have a gaussian distribution, this is 97.7%
             * of data. If the data have a uniform distribution, then this is 100% of data.
             */
            final double mean  = stats.mean();
            final double delta = 2 * stats.standardDeviation(true);
            final double range = Math.min(max, mean+delta) - Math.max(min, mean-delta);
            /*
             * Gets the order of magnitude of the range, as the number of decimal after the first digit
             * (negative if range < 1). We arbitrarily choose to keep 5 decimals after the first digit
             * (if this choice is modified, then we should change also the exponential notation above).
             * Note that it make a difference only if at least one decimal is a fraction digit (we don't
             * substitute integer digits by 0).
             */
            final double magnitude = floor(log10(range));
            if (!isNaN(magnitude)) {
                final int digits = max(5 - (int) magnitude, 0);
                format.setMinimumFractionDigits(digits);
                format.setMaximumFractionDigits(digits);
            }
        }
    }
}
