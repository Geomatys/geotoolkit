/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.ComparisonOperatorName;

import org.opengis.filter.Filter;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.ResourceId;

/**
 * Takes a filter and returns a simplified, equivalent one. At the moment the filter simplifies out
 * {@link Filter#INCLUDE} and {@link Filter#EXCLUDE} and deal with FID filter validation.
 * <p>
 * FID filter validation is meant to wipe out non valid feature ids from {@link Id} filters. This is
 * so in order to avoid sending feature ids down to DataStores that are not valid as per the
 * specific FeatureType fid structure. Since this is structure is usually FeatureStore specific, some
 * times being a strategy based on how the feature type primary key is generated, fid validation is
 * abstracted out to the {@link FIDValidator} interface so when a FeatureStore is about to send a query
 * down to the backend it van provide this visitor with a validator specific for the feature type
 * fid structure being queried.
 * </p>
 * <p>
 * By default all feature ids are valid. DataStores that want non valid fids to be wiped out should
 * set a {@link FIDValidator} through the {@link #setFIDValidator(FIDValidator)} method.
 * </p>
 *
 * @author Andrea Aime - OpenGeo
 * @author Gabriel Roldan (OpenGeo)
 * @module
 * @since 2.5.x
 *
 * @deprecated Use {@link org.apache.sis.filter.Optimization} instead.
 */
@Deprecated
public class SimplifyingFilterVisitor extends DuplicatingFilterVisitor {
    /**
     * Defines a simple means of assessing whether a feature id in an {@link Id} filter is
     * structurally valid and hence can be send down to the backend with confidence it will not
     * cause trouble, the most common one being filtering by pk number even if the type name prefix
     * does not match.
     */
    public static interface FIDValidator {
        public boolean isValid(String fid);
    }

    /**
     * A 'null-object' fid validator that assumes any feature id in an {@link Id} filter is valid
     */
    public static final FIDValidator ANY_FID_VALID = new FIDValidator() {
        @Override
        public boolean isValid(String fid) {
            return true;
        }
    };

    /**
     * A FID validator that matches the fids with a given regular expression to determine the fid's
     * validity.
     *
     * @author Gabriel Roldan (OpenGeo)
     */
    public static class RegExFIDValidator implements FIDValidator {

        private Pattern pattern;

        /**
         * @param regularExpression
         *            a regular expression as used by the {@code java.util.regex} package
         */
        public RegExFIDValidator(final String regularExpression) {
            pattern = Pattern.compile(regularExpression);
        }

        @Override
        public boolean isValid(final String fid) {
            return pattern.matcher(fid).matches();
        }
    }

    /**
     * A convenient fid validator for the common case of a feature id being a composition of a
     * {@code <typename>.<number>}
     */
    public static class TypeNameDotNumberFidValidator extends RegExFIDValidator {
        /**
         * @param typeName
         *            the typename that will be used for a regular expression match in the form of
         *            {@code <typename>.<number>}
         */
        public TypeNameDotNumberFidValidator(final String typeName) {
            super(typeName + "\\.\\d+");
        }
    }

    private FIDValidator fidValidator = ANY_FID_VALID;

    public void setFIDValidator(final FIDValidator validator) {
        this.fidValidator = validator == null ? ANY_FID_VALID : validator;
    }

    public static final SimplifyingFilterVisitor INSTANCE = new SimplifyingFilterVisitor();

    protected SimplifyingFilterVisitor() {
        setFilterHandler(LogicalOperatorName.AND, (f) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            // scan, clone and simplify the children
            final List<Filter<? super Object>> children = filter.getOperands();
            final List<Filter<Object>> newChildren = new ArrayList<>(children.size());
            for (Filter<? super Object> child : children) {
                final Filter cloned = (Filter) visit(child);

                // if any of the child filters is exclude,
                // the whole chain of AND is equivalent to EXCLUDE
                if (cloned == Filter.exclude()) return Filter.exclude();

                // these can be skipped
                if (cloned == Filter.include()) continue;
                newChildren.add(cloned);
            }

            // we might end up with an empty list
            if (newChildren.isEmpty()) return Filter.include();

            // remove the logic we have only one filter
            if (newChildren.size() == 1) return newChildren.get(0);

            // else return the cloned and simplified up list
            return ff.and(newChildren);
        });
        setFilterHandler(LogicalOperatorName.OR, (f) -> {
            final LogicalOperator<Object> filter = (LogicalOperator<Object>) f;
            // scan, clone and simplify the children
            final List<Filter<? super Object>> children = filter.getOperands();
            final List<Filter<Object>> newChildren = new ArrayList<>(children.size());
            for (Filter<? super Object> child : children) {
                final Filter cloned = (Filter) visit(child);

                // if any of the child filters is include,
                // the whole chain of OR is equivalent to INCLUDE
                if( cloned == Filter.include()) return Filter.include();

                // these can be skipped
                if (cloned == Filter.exclude()) continue;
                newChildren.add(cloned);
            }
            // we might end up with an empty list
            if (newChildren.isEmpty()) return Filter.exclude();

            // remove the logic we have only one filter
            if (newChildren.size() == 1) return newChildren.get(0);

            // else return the cloned and simplified up list
            return ff.or(newChildren);
        });
        /*
         * Uses the current {@link FIDValidator} to wipe out illegal feature ids from the returned filters.
         * Returns a filter containing only valid fids as per the current {@link FIDValidator},
         * may be {@link Filter#EXCLUDE} if none matches or the filter is already empty.
         */
        setFilterHandler(RESOURCEID_NAME, (f) -> {
            final ResourceId<Object> filter = (ResourceId<Object>) f;
            final String id = filter.getIdentifier();
            if (fidValidator.isValid(id)) {
                return ff.resourceId(id, filter.getVersion().orElse(null), filter.getStartTime().orElse(null), filter.getEndTime().orElse(null));
            } else {
                return Filter.exclude();
            }
        });
        final Function<Filter<Object>, Object> previous = getFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO);
        setFilterHandler(ComparisonOperatorName.PROPERTY_IS_EQUAL_TO, (f) -> {
            final BinaryComparisonOperator<Object> filter = (BinaryComparisonOperator<Object>) f;
            if (  filter.getOperand1() instanceof Literal
               && filter.getOperand2() instanceof Literal){
                //we can preevaluate this one
                return (filter.test(null)) ? Filter.include() : Filter.exclude();
            } else {
                return previous.apply(f);
            }
        });
    }
}
