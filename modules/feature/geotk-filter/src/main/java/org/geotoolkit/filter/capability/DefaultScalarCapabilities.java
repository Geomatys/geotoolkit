/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter.capability;

import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.ScalarCapabilities;

/**
 * Immutable scalar capabilities.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultScalarCapabilities implements ScalarCapabilities {

    private final boolean logical;
    private final ComparisonOperators comparisons;
    private final ArithmeticOperators arithmetics;

    public DefaultScalarCapabilities(final boolean logical, final ComparisonOperators comparisons, final ArithmeticOperators arithmetics) {
        this.logical = logical;
        this.comparisons = comparisons;
        this.arithmetics = arithmetics;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasLogicalOperators() {
        return logical;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComparisonOperators getComparisonOperators() {
        return comparisons;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ArithmeticOperators getArithmeticOperators() {
        return arithmetics;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultScalarCapabilities other = (DefaultScalarCapabilities) obj;
        if (this.logical != other.logical) {
            return false;
        }
        if (this.comparisons != other.comparisons && (this.comparisons == null || !this.comparisons.equals(other.comparisons))) {
            return false;
        }
        if (this.arithmetics != other.arithmetics && (this.arithmetics == null || !this.arithmetics.equals(other.arithmetics))) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.logical ? 1 : 0);
        hash = 59 * hash + (this.comparisons != null ? this.comparisons.hashCode() : 0);
        hash = 59 * hash + (this.arithmetics != null ? this.arithmetics.hashCode() : 0);
        return hash;
    }

}
