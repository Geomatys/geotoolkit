/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.util.logging.Logger;

import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.opengis.filter.Literal;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import java.util.logging.Level;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.filter.visitor.FunctionNames;
import org.apache.sis.referencing.CRS;
import org.opengis.filter.Filter;
import org.opengis.filter.SpatialOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.referencing.operation.TransformException;

/**
 * Extract a maximal envelope from the provided Filter.
 * <p>
 * The maximal envelope is generated from:
 * <ul>
 * <li>all the literal geometry instances involved if spatial operations - using
 * geom.getEnvelopeInternal().
 * <li>Filter.EXCLUDES will result in <code>null</code>
 * <li>Filter.INCLUDES will result in a "world" envelope with range Double.NEGATIVE_INFINITY to
 * Double.POSITIVE_INFINITY for each axis.
 * </ul>
 * Since geometry literals do not contains CRS information we can only produce a ReferencedEnvelope
 * without CRS information. You can call this function with an existing ReferencedEnvelope
 * or with your data CRS to correct for this limitation.
 * ReferencedEnvelope example:<pre><code>
 * ReferencedEnvelope bbox = (ReferencedEnvelope)
 *     filter.accepts(new ExtractBoundsFilterVisitor(), dataCRS );
 * </code></pre>
 * You can also call this function with an existing Envelope; if you are building up bounds based on
 * several filters.
 * <p>
 * This is a replacement for FilterConsumer.
 *
 * @author Jody Garnett
 * @author Johann Sorel (Geomatys)
 *
 * @deprecated Unnecessarily complicated for the new filter API.
 */
@Deprecated
public class ExtractBoundsFilterVisitor extends NullFilterVisitor<Envelope> {
    private static NullFilterVisitor BOUNDS_VISITOR = new ExtractBoundsFilterVisitor();

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.filter.visitor");

    public static Envelope bbox(final Filter filter, final Envelope e) {
        Container<Envelope> c = new Container<Envelope>();
        c.data = (e != null) ? e : new JTSEnvelope2D();
        BOUNDS_VISITOR.visit(filter, c);
        return c.data;
    }

    /**
     * This FilterVisitor is stateless - use ExtractBoundsFilterVisitor.BOUNDS_VISITOR.
     * <p>
     * You may also subclass in order to reuse this functionality in your own
     * FilterVisitor implementation.
     */
    protected ExtractBoundsFilterVisitor() {
        setFilterHandler(Filter.exclude().getOperatorType(), (f, c) -> c.data = null);
        setFilterHandler(Filter.include().getOperatorType(), (f, c) -> {
            if (c.data == null) return;
            JTSEnvelope2D bbox = bbox(c.data);

            // also consider making use of CRS extent?
            Envelope world = new Envelope(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            bbox.expandToInclude(world);
            c.data = bbox;
        });
        setFilterHandler(SpatialOperatorName.BBOX, (f, c) -> {
            final SpatialOperator<Object> filter = (SpatialOperator<Object>) f;
            if (c.data == null) return;
            JTSEnvelope2D bbox = bbox(c.data);
            org.opengis.geometry.Envelope bb = (org.opengis.geometry.Envelope)
                    ((Literal<Object,?>) filter.getExpressions().get(1)).getValue();
            final CoordinateReferenceSystem sourceCRS = bb.getCoordinateReferenceSystem();
            final CoordinateReferenceSystem targetCRS = bbox.getCoordinateReferenceSystem();
            if (sourceCRS != null && targetCRS != null && !CRS.equivalent(targetCRS, sourceCRS)) {
                try {
                    // reproject bbox
                    bb = Envelopes.transform(bb, targetCRS);
                } catch (TransformException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            bbox.expandToInclude(new JTSEnvelope2D(bb));
            c.data = bbox;
        });
        // Note: we are only visiting literals involved in spatial operations.
        setExpressionHandler(FunctionNames.Literal, (e, c) -> {
            final Literal<Object,?> expression = (Literal<Object,?>) e;
            if (c.data == null) return;
            final JTSEnvelope2D bbox = bbox(c.data);
            final Object value = expression.getValue();
            if (value instanceof Geometry) {
                final Geometry geometry = (Geometry) value;
                final Envelope bounds = geometry.getEnvelopeInternal();
                bbox.expandToInclude(bounds);
            } else {
                LOGGER.finer("LiteralExpression ignored!");
            }
            c.data = bbox;
        });
    }

    /**
     * Produce an ReferencedEnvelope from the provided data parameter.
     */
    private static JTSEnvelope2D bbox(final Object data) {
        if (data == null) {
            return null;
        } else if (data instanceof JTSEnvelope2D) {
            return (JTSEnvelope2D) data;
        } else if (data instanceof Envelope) {
            return new JTSEnvelope2D((Envelope) data, null);
        } else if (data instanceof CoordinateReferenceSystem) {
            return new JTSEnvelope2D((CoordinateReferenceSystem) data);
        }
        throw new ClassCastException("Could not cast data to ReferencedEnvelope");
    }
}
