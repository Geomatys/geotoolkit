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
package org.geotoolkit.filter.visitor;

import org.opengis.filter.And;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.capability.ArithmeticOperators;
import org.opengis.filter.capability.ComparisonOperators;
import org.opengis.filter.capability.FilterCapabilities;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.capability.Functions;
import org.opengis.filter.capability.ScalarCapabilities;
import org.opengis.filter.capability.SpatialCapabilities;
import org.opengis.filter.capability.SpatialOperators;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;


/**
 * This visitor will return Boolean.TRUE if the provided filter
 * is supported by the the FilterCapabilities.
 * <p>
 * This method will look up the right information in the provided
 * FilterCapabilities instance for you depending on the type of filter
 * provided.
 * <p>
 * Example:<pre><code>
 * boolean yes = filter.accepts( IsSupportedFilterVisitor( capabilities ), null );
 * </code></pre>
 *
 * Please consider IsSupportedFilterVisitor if you need to be sure of the
 * entire Filter.
 *
 * @author Jody Garnett (Refractions Research)
 * @module pending
 */
public class IsSupportedFilterVisitor implements FilterVisitor, ExpressionVisitor {

    private final FilterCapabilities capabilities;

    public IsSupportedFilterVisitor( final FilterCapabilities capabilities ){
        this.capabilities = capabilities;
    }

    /** INCLUDE and EXCLUDE are never supported */
    @Override
    public Object visit( final ExcludeFilter filter, final Object extraData ) {
        return false;
    }
    /** INCLUDE and EXCLUDE are never supported */
    @Override
    public Object visit( final IncludeFilter filter, final Object extraData ) {
        return false;
    }

    @Override
    public Object visit( final And filter, final Object extraData ) {
        return capabilities.getScalarCapabilities() != null &&
               capabilities.getScalarCapabilities().hasLogicalOperators();
    }

    @Override
    public Object visit( final Id filter, final Object extraData ) {
        return capabilities.getIdCapabilities() != null &&
               ( capabilities.getIdCapabilities().hasFID() ||
                 capabilities.getIdCapabilities().hasEID() );
    }

    @Override
    public Object visit( final Not filter, final Object extraData ) {
        return capabilities.getScalarCapabilities() != null &&
        capabilities.getScalarCapabilities().hasLogicalOperators();
    }

    @Override
    public Object visit( final Or filter, final Object extraData ) {
        return capabilities.getScalarCapabilities() != null &&
        capabilities.getScalarCapabilities().hasLogicalOperators();
    }

    @Override
    public Object visit( final PropertyIsBetween filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsBetween.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsEqualTo filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsEqualTo.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsNotEqualTo filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsNotEqualTo.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsGreaterThan filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsGreaterThan.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsGreaterThanOrEqualTo filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsGreaterThanOrEqualTo.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsLessThan filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsLessThan.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsLessThanOrEqualTo filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsLessThanOrEqualTo.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsLike filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsLike.NAME ) != null;
    }

    @Override
    public Object visit( final PropertyIsNull filter, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ComparisonOperators operators = scalar.getComparisonOperators();
        if( operators == null ) return false;

        return operators.getOperator( PropertyIsNull.NAME ) != null;
    }

    @Override
    public Object visit( final BBOX filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( BBOX.NAME ) != null;
    }

    @Override
    public Object visit( final Beyond filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Beyond.NAME ) != null;
    }

    @Override
    public Object visit( final Contains filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Contains.NAME ) != null;
    }

    @Override
    public Object visit( final Crosses filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Crosses.NAME ) != null;
    }

    @Override
    public Object visit( final Disjoint filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Disjoint.NAME ) != null;
    }

    @Override
    public Object visit( final DWithin filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( DWithin.NAME ) != null;
    }

    @Override
    public Object visit( final Equals filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Equals.NAME ) != null;
    }

    @Override
    public Object visit( final Intersects filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Intersects.NAME ) != null;
    }

    @Override
    public Object visit( final Overlaps filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Overlaps.NAME ) != null;
    }

    @Override
    public Object visit( final Touches filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Touches.NAME ) != null;
    }

    @Override
    public Object visit( final Within filter, final Object extraData ) {
        final SpatialCapabilities spatial = capabilities.getSpatialCapabilities();
        if( spatial == null ) return false;

        final SpatialOperators operators = spatial.getSpatialOperators();
        if( operators == null ) return false;

        return operators.getOperator( Within.NAME ) != null;
    }

    @Override
    public Object visitNullFilter( final Object extraData ) {
        return false;
    }

    //
    // Expressions
    //
    /** NilExpression is a placeholder and is never supported */
    @Override
    public Object visit( final NilExpression expression, final Object extraData ) {
        return false;
    }

    @Override
    public Object visit( final Add expression, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ArithmeticOperators operators = scalar.getArithmeticOperators();
        if( operators == null ) return false;

        return operators.hasSimpleArithmetic();
    }

    @Override
    public Object visit( final Divide expression, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ArithmeticOperators operators = scalar.getArithmeticOperators();
        if( operators == null ) return false;

        return operators.hasSimpleArithmetic();
    }

    @Override
    public Object visit( final Function function, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ArithmeticOperators operators = scalar.getArithmeticOperators();
        if( operators == null ) return false;

        final Functions functions = operators.getFunctions();
        if( functions == null ) return false;

        // Note that only function name is checked here
        final FunctionName found = functions.getFunctionName( function.getName() );
        // And that's enough to assess if the function is supported
        return found != null;
    }

    @Override
    public Object visit( final Literal expression, final Object extraData ) {
        return true;
    }

    @Override
    public Object visit( final Multiply expression, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ArithmeticOperators operators = scalar.getArithmeticOperators();
        if( operators == null ) return false;

        return operators.hasSimpleArithmetic();
    }

    /**
     * You can override this to perform a sanity check against a provided
     * FeatureType.
     */
    @Override
    public Object visit( final PropertyName expression, final Object extraData ) {
        return true;
    }

    @Override
    public Object visit( final Subtract expression, final Object extraData ) {
        final ScalarCapabilities scalar = capabilities.getScalarCapabilities();
        if( scalar == null ) return false;

        final ArithmeticOperators operators = scalar.getArithmeticOperators();
        if( operators == null ) return false;

        return operators.hasSimpleArithmetic();
    }
}
