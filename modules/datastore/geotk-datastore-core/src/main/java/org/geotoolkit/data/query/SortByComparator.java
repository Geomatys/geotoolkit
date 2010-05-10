/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data.query;

import java.util.Arrays;
import java.util.Comparator;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.Strings;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Comparator to sort Features with a given array of query SortBy[].
 *
 * @author Johann Sorel (Geomatys)
 */
public class SortByComparator implements Comparator<Feature> {

    private final SortBy[] orders;

    public SortByComparator(SortBy[] orders) {
        if (orders == null || orders.length == 0) {
            throw new IllegalArgumentException("SortBy array can not be null or empty.");
        }

        this.orders = orders;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int compare(Feature f1, Feature f2) {

        for (final SortBy order : orders) {
            final PropertyName property = order.getPropertyName();
            final Comparable o1 = (Comparable) property.evaluate(f1);
            final Comparable o2 = (Comparable) property.evaluate(f2);

            final int result;
            if (order.getSortOrder() == SortOrder.ASCENDING) {
                result = o1.compareTo(o2);
            } else {
                result = o2.compareTo(o1);
            }

            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(' ');
        sb.append(StringUtilities.toCommaSeparatedValues(Arrays.asList(orders)));
        return sb.toString();
    }



}
