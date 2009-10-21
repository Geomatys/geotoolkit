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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

import static org.geotoolkit.style.StyleConstants.*;
import static org.opengis.filter.expression.Expression.*;

/**
 * 
 * Implementation of "Interpolation" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 *
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a colour map
 * <li>Literal: lookup value
 * <li>Literal: InterpolationPoint : data 1
 * <li>Literal: InterpolationPoint : value 1
 * <li>Literal: InterpolationPoint : data 2
 * <li>Literal: InterpolationPoint : value 2
 * <li>Literal: Mode
 * <li>Literal: Method
 * </ol>
 * In reality any expression will do.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultInterpolate implements Interpolate {

    private final Expression lookup;
    private final List<InterpolationPoint> points;
    private final Method method;
    private final Mode mode;
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
            return -2; // indicating unbounded, 2 minimum
        }

        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Data 1", "Value 1",
                        "Data 2", "Value 2",
                        "linear, cosine or cubic",
                        "numeric or color"
                    });
        }

        public String getName() {
            return "Interpolate";
        }
    };

    
    public DefaultInterpolate(Expression LookUpValue, List<InterpolationPoint> values, 
           Method method, Mode mode,Literal fallback){
                
        if(values == null ){
            values = Collections.emptyList();
        }
        
        this.lookup = (LookUpValue == null || LookUpValue == NIL) ?  DEFAULT_CATEGORIZE_LOOKUP : LookUpValue;
        this.points = Collections.unmodifiableList(values);
        this.method = (method == null) ? Method.COLOR : method;
        this.mode = (mode == null) ? Mode.LINEAR : mode;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
    }
    

    public String getName() {
        return "Interpolate";
    }

    public List<Expression> getParameters() {
        //TODO to this cleanly
        return Collections.emptyList();
    }

    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    public Object evaluate(Object object) {
        return evaluate(object, Object.class);
    }

    public <T> T evaluate(Object object, Class<T> context) {
        return null;
    }

    public Literal getFallbackValue() {
        return fallback;
    }

    public Expression getLookupValue() {
        return lookup;
    }

    public List<InterpolationPoint> getInterpolationPoints() {
        return points;
    }

    public Mode getMode() {
        return mode;
    }

    public Method getMethod() {
        return method;
    }
    
}
