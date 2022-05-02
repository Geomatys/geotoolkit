/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.filter.coverage;

import java.awt.Shape;
import java.util.List;
import java.util.function.BiConsumer;
import org.apache.sis.coverage.RegionOfInterest;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.filter.Optimization;
import org.apache.sis.internal.feature.jts.JTS;
import org.apache.sis.internal.filter.Visitor;
import org.locationtech.jts.geom.Geometry;
import org.opengis.coverage.GeometryValuePair;
import org.opengis.filter.Expression;
import org.opengis.filter.Filter;
import org.opengis.filter.InvalidFilterValueException;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.operation.TransformException;


/**
 * A filter expression "compiled" in a form that can be translated to {@link GridCoverageProcessor} method calls.
 * Operations applied by this class can modify pixel values but shall not modify grid geometry or sample dimensions.
 *
 * <h2>Limitations</h2>
 * Current implementation does not perform a fine distinction between intersect, overlap, cross or touch operations.
 * Operations are only interpreted as "some kind of interaction with the shape".
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class CompiledFilter {
    /**
     * The region of interest on which to apply the mask.
     */
    private RegionOfInterest roi;

    /**
     * Whether the next {@link #mask(String, Object)} operation should mask pixels inside or outside the geometry.
     * This is inverted when parsing the operands of a {@link #not()} operation.
     */
    private boolean maskInside;

    /**
     * Creates an initially empty "compiled" filter.
     */
    CompiledFilter() {
    }

    /**
     * Translates the given filter to {@link GridCoverageProcessor} operations.
     *
     * @param  filter  the condition for testing whether to include a pixel in the coverage.
     * @throws InvalidFilterValueException if the given filter is not supported.
     */
    @SuppressWarnings("unchecked")      // Okay for use with Parser.
    public void compile(Filter<GeometryValuePair> filter) {
        final Optimization opt = new Optimization();
        filter = (Filter<GeometryValuePair>) opt.apply(filter);
        Parser.INSTANCE.visit(filter, this);
    }

    /**
     * Applies filter on the given coverage.
     *
     * @param  processor  the processor to use for operations.
     * @param  coverage   the coverage to filter.
     * @return the filtered coverage.
     * @throws TransformException if ROI coordinates can not be transformed to grid coordinates.
     */
    public GridCoverage execute(final GridCoverageProcessor processor, GridCoverage coverage) throws TransformException {
        if (roi != null) {
            coverage = processor.mask(coverage, roi, maskInside);
        }
        return coverage;
    }

    /**
     * Invoked before to process the operands of a {@code NOT} operation.
     * The meaning of intersect, disjoint, <i>etc.</i> is reversed.
     * Invoking this method a second time restores the initial value.
     */
    private void not() {
        maskInside = !maskInside;
    }

    /**
     * Invoked when an {@code INTERSECT} filter operation is found.
     *
     * @param  xpath     band on which to apply the intersection, or {@code "*"} for all bands.
     * @param  geometry  the geometry of the mask.
     */
    private void mask(final String xpath, final Object geometry) {
        if (!BandReference.WILDCARD.equals(xpath)) {
            throw new InvalidFilterValueException("Currently only the " + BandReference.WILDCARD + " XPath is supported.");
        }
        if (roi != null) {
            // TODO: we could improve by supporting AND and OR operations as intersections and unions.
            throw new InvalidFilterValueException("Currently implementation does not support multiple ROI.");
        }
        if (geometry instanceof RegionOfInterest) {
            roi = (RegionOfInterest) geometry;
        } else if (geometry instanceof Geometry) {
            roi = new RegionOfInterest(JTS.asShape((Geometry) geometry), null);
        } else if (geometry instanceof Shape) {
            roi = new RegionOfInterest((Shape) geometry, null);
        } else {
            // TODO: Convert JTS geometries.
            throw new InvalidFilterValueException("Geometry must be a Java2D shape.");
        }
    }

    /**
     * The visitor for parsing a filter expressions and translate to calls to {@link GridCoverageProcessor}.
     * This class is thread safe.
     */
    private static final class Parser extends Visitor<GeometryValuePair,CompiledFilter> {
        /**
         * Unique instance of this visitor.
         */
        static final Parser INSTANCE = new Parser();

        /**
         * Creates the unique {@link #INSTANCE}. For thread-safety,
         * the visitor shall not be modified after construction
         */
        private Parser() {
            super(true, false);
            final BiConsumer<Filter<GeometryValuePair>, CompiledFilter> mask = (f,p) -> {
                final List<Expression<? super GeometryValuePair, ?>> expressions = f.getExpressions();
                int ci = 0;
                do {
                    /*
                     * This block is executed one ot two times: first with (coverage, literal) order,
                     * then if the first try did not work another try with (literal, coverage) order.
                     */
                    final Expression<?,?> coverage = expressions.get(ci);
                    if (coverage instanceof ValueReference<?,?>) {
                        final Expression<?,?> geometry = expressions.get(ci ^ 1);
                        if (geometry instanceof Literal<?,?>) {
                            p.mask(((ValueReference<?,?>) coverage).getXPath(), ((Literal<?,?>) geometry).getValue());
                            return;
                        }
                    }
                } while ((ci ^= 1) != 0);
                throw new InvalidFilterValueException("Unsupported operands. Expected a `ValueReference` and a `Literal`.");
            };
            setFilterHandler(SpatialOperatorName.INTERSECTS, mask);
            setFilterHandler(SpatialOperatorName.DISJOINT, (f,p) -> {
                p.not();
                mask.accept(f, p);
            });
            setFilterHandler(LogicalOperatorName.NOT, (f,p) -> {
                p.not();
                visitOperands((LogicalOperator<GeometryValuePair>) f, p);
            });
        }

        /**
         * Visits all operands of the given logical operator.
         */
        @SuppressWarnings("unchecked")      // Okay in the context of this class.
        private void visitOperands(final LogicalOperator<GeometryValuePair> filter, final CompiledFilter p) {
            for (final Filter<? super GeometryValuePair> operand : filter.getOperands()) {
                visit((Filter<GeometryValuePair>) operand, p);
            }
        }

        /**
         * Invoked when an expression is found. This implementation does nothing for accepting all expressions.
         * Unsupported expressions will be rejected later, for example in {@link #mask(String, Object)} execution.
         */
        protected void typeNotFound(final String type, final Expression<GeometryValuePair,?> expression, final CompiledFilter p) {
        }
    }
}
