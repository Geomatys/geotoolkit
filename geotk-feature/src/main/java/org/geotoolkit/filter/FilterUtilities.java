/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.filter;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.filter.DefaultFilterFactory;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.filter.visitor.IsStaticExpressionVisitor;
import org.geotoolkit.filter.visitor.PrepareFilterVisitor;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SortOrder;
import org.opengis.filter.ValueReference;


/**
 * Utility methods for filters.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FilterUtilities {
    /**
     * Default filter factory for features.
     *
     * @todo parameterized type should be {@code <Feature, Object, Object>}, but it breaks some Geotk code.
     * The problem is in Geotk, which uses raw types in too many places.
     */
    public static final FilterFactory FF = DefaultFilterFactory.forFeatures();

    /**
     * Avoid instanciation.
     */
    private FilterUtilities() {}

    /**
     * Prepare a filter against a given class.
     * @param filter : filter to optimize
     * @param objectClazz : target class against which to optimize
     * @return optimized filter
     */
    public static Filter prepare(final Filter filter, final Class objectClazz,final FeatureType expectedType){
        if(filter == null) return null;
        final PrepareFilterVisitor visitor = new PrepareFilterVisitor(objectClazz,expectedType);
        return (Filter) visitor.visit(filter);
    }

    /**
     * Generates a property name which caches the value accessor.
     * the returned PropertyName should not be used against objects of a different
     * class, the result will be unpredictable.
     *
     * @param exp : the property name to prepare
     * @param objectClazz : the target class against which this prepared property
     *      will be used.
     * @return prepared property name expression.
     */
    public static ValueReference prepare(final ValueReference exp, final Class objectClazz, final FeatureType expectedType){
        String xPath = exp.getXPath();
        //TODO : to remove when sis contains better xpath support then geotk
        if (xPath.contains("/") && !xPath.startsWith("Q{")) {
            return new CachedPropertyName(exp.getXPath(), objectClazz,expectedType);
        }
        else return exp;
    }

    /**
     * Test if an expression is static.
     * Static is the way no expressions use the candidate object for evaluation.
     *
     * @return true if expression is static
     */
    public static boolean isStatic(final Expression exp){
        ensureNonNull("expression", exp);
        return IsStaticExpressionVisitor.VISITOR.visit(exp);
    }

    /**
     * Convert a logic OR in and AND filter.
     *
     * (a OR b) =  NOT (NOT a AND NOT b)
     *
     * @return Not filter
     */
    public static LogicalOperator orToAnd(final LogicalOperator filter, FilterFactory ff) {
        if(ff==null) ff = FF;

        final List<Filter> children = filter.getOperands();
        final int size = children.size();
        final List<Filter> newChildren = new ArrayList<>(size);
        for(int i=0;i<size;i++) {
            Filter f = children.get(i);
            f = (f.getOperatorType() == LogicalOperatorName.NOT) ? ((LogicalOperator<?>) f).getOperands().get(0) : ff.not(f);
            newChildren.add(f);
        }
        return ff.not(ff.and(newChildren));
    }

    /**
     * Returns the sort order enumeration value for the given name.
     * This method recognizes de SQL "ASC" and "DESC" names in addition
     * to the enumeration names.
     */
    public static SortOrder sortOrder(final String name) {
        if (name == null) return null;
        if (name.equalsIgnoreCase("ASC"))  return SortOrder.ASCENDING;
        if (name.equalsIgnoreCase("DESC")) return SortOrder.DESCENDING;
        return SortOrder.valueOf(name);
    }
}
