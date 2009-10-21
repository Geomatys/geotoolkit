/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
import org.geotoolkit.style.StyleConstants;
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
public class DefaultCategorize implements Categorize {
    
    private final Comparator<Expression> comparator = new Comparator<Expression>() {

        public int compare(Expression exp1, Expression exp2) {
            if(exp1.equals(StyleConstants.CATEGORIZE_LESS_INFINITY)){
                //categorize less is always first
                return -1;
            }else{
                double d1 = exp1.evaluate(null, Double.class);
                double d2 = exp2.evaluate(null, Double.class);
                return (int) (d1 - d2);
            }
        }
    };
    
    private final Expression lookup;
    private final Map<Expression,Expression> values = new TreeMap<Expression, Expression>(comparator);
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

        public int getArgumentCount() {
            return 2; // indicating unbounded, 2 minimum
        }

        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Value",
                        "Threshold 1", "Value 1",
                        "Threshold 2", "Value 2",
                        "succeeding or preceding"
                    });
        }

        public String getName() {
            return "Categorize";
        }
    };

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
    public Expression getLookupValue(){
        return lookup;
    }
    
    /**
     * {@inheritDoc }
     */
    public Map<Expression,Expression> getThresholds() {
        return Collections.unmodifiableMap(values);
    }
        
    /**
     * {@inheritDoc }
     */
    public ThreshholdsBelongTo getBelongTo(){
        return belongTo;
    }
    
    /**
     * {@inheritDoc }
     */
    public String getName() {
        return NAME.getName();
    }

    /**
     * {@inheritDoc }
     */
    public List<Expression> getParameters() {
        //TODO to this cleanly. I'm still not sure a style function should behave
        //like a expression Function.
        final List<Expression> params = new ArrayList<Expression>();
        return params;
    }

    /**
     * {@inheritDoc }
     */
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    public Object evaluate(Object object) {
        return evaluate(object, Object.class);
    }

    /**
     * {@inheritDoc }
     */
    public <T> T evaluate(Object object, Class<T> context) {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    public Literal getFallbackValue() {
        return fallback;
    }
    
}
