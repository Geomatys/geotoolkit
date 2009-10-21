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
import java.util.List;

import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Implementation of "Recode" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a colour map
 * <li>Literal: lookup value
 * <li>Literal: MapItem : data 1
 * <li>Literal: MapItem : value 1
 * <li>Literal: MapItem : data 2
 * <li>Literal: MapItem : value 2
 * </ol>
 * In reality any expression will do.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class RecodeFunction implements Function {
    
    /**
     * Use as a PropertyName when defining a color map.
     * The "Raterdata" is expected to apply to only a single band;
     */
    public static final String RASTER_DATA = "Rasterdata";
    
    private final List<Expression> parameters;
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
                        "Data 2", "Value 2"
                    });
        }

        public String getName() {
            return "Recode";
        }
    };

    public RecodeFunction() {
        this( new ArrayList<Expression>(), null);
    }

    public RecodeFunction(List<Expression> parameters, Literal fallback) {
        this.parameters = parameters;
        this.fallback = fallback;
    }

    public String getName() {
        return "Recode";
    }

    public List<Expression> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    public Object evaluate(Object object) {
        return evaluate(object, Object.class);
    }

    public <T> T evaluate(Object object, Class<T> context) {
        final Expression lookupExp = parameters.get(0);
        Expression currentExp = parameters.get(1);

        final List<Expression> splits;
        if (parameters.size() == 2) {
            return currentExp.evaluate(object, context);
        } else if (parameters.size() % 2 == 1) {
            splits = parameters.subList(1, parameters.size());
        } else {
            //this should not happen
            splits = parameters.subList(1, parameters.size() - 1);
        }
        
        for (int i = 0; i < splits.size(); i += 2) {
            Expression dataExp = splits.get(i);
            Expression resultExp = splits.get(i + 1);
            
            
            String lookupValue = lookupExp.evaluate(object, String.class);
            
            //we deal with a raster data
            if(lookupValue.equalsIgnoreCase(RASTER_DATA)){
                Double bandValue = new Double(object.toString());
                Double compareValue = dataExp.evaluate(object, Double.class);
                
                if(bandValue == compareValue){
                    return resultExp.evaluate(object, context);
                }
                
            }
            //we deal with something else, a mistake ? can it happen ?
            else{
            }
            
        }
        return currentExp.evaluate(object, context);
    }


    public Literal getFallbackValue() {
        return fallback;
    }
    
}
