/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.style.StyleConstants;

import org.opengis.feature.Feature;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * Implementation of "Categorize" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a color map
 * <li>Literal: lookup value
 * <li>Literal: threshold 1
 * <li>Literal: value 1
 * <li>Literal: threshold 2
 * <li>Literal: value 2
 * <li>Literal: (Optional) succeeding or preceding
 * </ol>
 * In reality any expression will do.
 * @author Jody Garnett
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultCategorize extends AbstractExpression implements Categorize {
    
    private final Comparator<Expression> comparator = new Comparator<Expression>() {

        @Override
        public int compare(Expression exp1, Expression exp2) {
            if(exp1.equals(StyleConstants.CATEGORIZE_LESS_INFINITY)){
                //categorize less is always first
                return -1;
            }else if(exp2.equals(StyleConstants.CATEGORIZE_LESS_INFINITY)){
                //categorize less is always first
                return +1;
            }else{
                final double d1 = exp1.evaluate(null, Double.class);
                final double d2 = exp2.evaluate(null, Double.class);
                final double diff = d1-d2;

                if(diff < 0){
                    return -1;
                }else if(diff > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        }
    };
    
    private final Expression lookup;
    private final TreeMap<Expression,Expression> values = new TreeMap<Expression, Expression>(comparator);
    private final ThreshholdsBelongTo belongTo;
    private final Literal fallback;
    
    
    /**
     * Make the instance of FunctionName available in
     * a consistent spot.
     */
    public static final FunctionName NAME = new Name();

    /**
     * Describe how this function works.
     * (should be available via FactoryFinder lookup...)
     */
    public static class Name implements FunctionName {

        @Override
        public int getArgumentCount() {
            return 2; // indicating unbounded, 2 minimum
        }

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Value",
                        "Threshold 1", "Value 1",
                        "Threshold 2", "Value 2",
                        "succeeding or preceding"
                    });
        }

        @Override
        public String getName() {
            return "Categorize";
        }
    };

    /**
     *
     * @param LookUpValue
     * @param values map with threadholds keys.
     * @param belongs
     * @param fallback
     */
    public DefaultCategorize(final Expression LookUpValue, final Map<Expression,Expression> values, 
            ThreshholdsBelongTo belongs, Literal fallback){
                
        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Values can't be empty");
        }
        
        this.lookup = (LookUpValue == null || LookUpValue == NIL) ?  DEFAULT_CATEGORIZE_LOOKUP : LookUpValue;
        this.values.putAll(values);
        this.belongTo = (belongs == null) ? ThreshholdsBelongTo.SUCCEEDING :belongs;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
        
        if(this.values.keySet().iterator().next() != CATEGORIZE_LESS_INFINITY){
            throw new  IllegalArgumentException("Values must hold at least one key : CATEGORIZE_LESS_INFINITY");
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLookupValue(){
        return lookup;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Expression,Expression> getThresholds() {
        return Collections.unmodifiableMap(values);
    }
        
    /**
     * {@inheritDoc }
     */
    @Override
    public ThreshholdsBelongTo getBelongTo(){
        return belongTo;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return NAME.getName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Expression> getParameters() {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(lookup);
        return params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object evaluate(Object object) {

        if(object instanceof Feature){
            final Feature f = (Feature)object;
            final Double value = lookup.evaluate(f,Double.class);
            final Expression exp = new DefaultLiteral<Double>(value);

            final boolean b = this.belongTo == belongTo.SUCCEEDING;

            final Expression closest = values.headMap(exp,!b).lastEntry().getValue();
            return closest.evaluate(f);
        }
        
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Literal getFallbackValue() {
        return fallback;
    }
    
}
