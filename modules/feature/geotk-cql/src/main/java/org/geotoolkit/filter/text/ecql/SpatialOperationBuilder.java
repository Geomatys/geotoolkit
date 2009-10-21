/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.ecql;

import org.geotoolkit.filter.text.commons.BuildResultStack;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;


/**
 * Builds an instance of one {@link BinarySpatialOperator} subclass.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
class SpatialOperationBuilder {

    private final BuildResultStack resultStack;
    private final FilterFactory2 filterFactory;

    public SpatialOperationBuilder(final BuildResultStack resultStack, final FilterFactory filterFactory) {
        assert resultStack != null;
        assert filterFactory != null;

        this.resultStack = resultStack;
        this.filterFactory = (FilterFactory2) filterFactory;
    }

    protected final BuildResultStack getResultStack() {
        return resultStack;
    }

    protected final FilterFactory2 getFilterFactory() {
        return filterFactory;
    }

    /**
     * Retrieve the parameters of spatial operation from stack result
     *
     * @return Expression array with the parameters in the natural order
     * @throws CQLException
     */
    private Expression[] buildParameters() throws CQLException {
        return new Expression[] {
            resultStack.popExpression(),
            resultStack.popExpression()
        };
    }

    protected BinarySpatialOperator buildFilter(final Expression expr1, final Expression expr2) {
        throw new UnsupportedOperationException("must be implemented");
    }

    /**
     * @return new instance of {@link Contains} operation
     * @throws CQLException
     */
    protected Contains buildContains() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().contains(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Equals} operation
     * @throws CQLException
     */
    public Equals buildEquals() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().equal(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Disjoint} operation
     * @throws CQLException
     */
    public Disjoint buildDisjoint() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().disjoint(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Intersects} operation
     * @throws CQLException
     */
    public Intersects buildIntersects() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().intersects(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Touches} operation
     * @throws CQLException
     */
    public Touches buildTouches() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().touches(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Crosses} operation
     * @throws CQLException
     */
    public Crosses buildCrosses() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().crosses(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Within} operation
     * @throws CQLException
     */
    public Within buildWithin() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().within(params[0], params[1]);
    }

    /**
     * @return new instance of {@link Within} operation
     * @throws CQLException
     */
    public Overlaps buildOverlaps() throws CQLException {
        final Expression[] params = buildParameters();
        return getFilterFactory().overlaps(params[0], params[1]);
    }

    /**
     * Builds a bbox using the stack subproducts
     *
     * @return {@link BBOX}}
     * @throws CQLException
     */
    public BBOX buildBBoxWithCRS() throws CQLException {
        final String crs = getResultStack().popStringValue();
        assert crs != null;
        return buildBBox(crs);
    }

    /**
     * Builds a bbox using the stack subproducts
     *
     * @return {@link BBOX}}
     * @throws CQLException
     */
    public BBOX buildBBox() throws CQLException {
        return buildBBox(null);
    }

    /**
     * build a bbox using the stack subproducts and the crs parameter
     * @param crs
     * @return {@link BBOX}}
     * @throws CQLException
     */
    private BBOX buildBBox(final String crs) throws CQLException {
        final double maxY = getResultStack().popDoubleValue();
        final double maxX = getResultStack().popDoubleValue();
        final double minY = getResultStack().popDoubleValue();
        final double minX = getResultStack().popDoubleValue();

        final Expression expr = getResultStack().popExpression();

        final FilterFactory2 ff = (FilterFactory2) getFilterFactory();

        return ff.bbox(expr, minX, minY, maxX, maxY, crs);
    }
}
