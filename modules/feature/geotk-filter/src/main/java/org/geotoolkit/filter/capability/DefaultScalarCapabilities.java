/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 */
public class DefaultScalarCapabilities implements ScalarCapabilities {

    private final boolean logical;
    private final ComparisonOperators comparisons;
    private final ArithmeticOperators arithmetics;

    public DefaultScalarCapabilities(boolean logical, ComparisonOperators comparisons, ArithmeticOperators arithmetics) {
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

}
