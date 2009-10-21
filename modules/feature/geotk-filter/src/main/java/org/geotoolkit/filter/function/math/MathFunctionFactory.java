/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.filter.function.math;

import org.geotoolkit.filter.function.FunctionFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

/**
 * Factory registering the commun mathematical functions.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MathFunctionFactory implements FunctionFactory{

    public static final String ABS                  = "abs";
    public static final String ACOS                 = "acos";
    public static final String ASIN                 = "asin";
    public static final String ATAN2                = "atan2";
    public static final String ATAN                 = "atan";
    public static final String CEIL                 = "ceil";
    public static final String COS                  = "cos";
    public static final String EXP                  = "exp";
    public static final String FLOOR                = "floor";
    public static final String IEEE_REMAINDER       = "IEEEremainder";
    public static final String LOG                  = "log";
    public static final String MAX                  = "max";
    public static final String MIN                  = "min";
    public static final String PI                   = "pi";
    public static final String POW                  = "pow";
    public static final String RANDOM               = "random";
    public static final String RINT                 = "rint";
    public static final String ROUND                = "round";
    public static final String SIN                  = "sin";
    public static final String SQRT                 = "sqrt";
    public static final String TAN                  = "tan";
    public static final String TO_DEGREES           = "toDegrees";
    public static final String TO_RADIANS           = "toRadians";

    private static final String[] NAMES;

    static{
        NAMES = new String[]{
         ABS,
         ACOS,
         ASIN,
         ATAN2,
         ATAN,
         CEIL,
         COS,
         EXP,
         FLOOR,
         IEEE_REMAINDER,
         LOG,
         MAX,
         MIN,
         PI,
         POW,
         RANDOM,
         RINT,
         ROUND,
         SIN,
         SQRT,
         TAN,
         TO_DEGREES,
         TO_RADIANS};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getNames() {
        return NAMES;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Function createFunction(String name, Literal fallback, Expression... parameters) throws IllegalArgumentException {

        if(name.equals(ABS))                    return new AbsFunction(parameters[0]);
        else if(name.equals(ACOS))              return new AcosFunction(parameters[0]);
        else if(name.equals(ASIN))              return new AsinFunction(parameters[0]);
        else if(name.equals(ATAN2))             return new Atan2Function(parameters[0], parameters[1]);
        else if(name.equals(ATAN))              return new AtanFunction(parameters[0]);
        else if(name.equals(CEIL))              return new CeilFunction(parameters[0]);
        else if(name.equals(COS))               return new CosFunction(parameters[0]);
        else if(name.equals(EXP))               return new ExpFunction(parameters[0]);
        else if(name.equals(FLOOR))             return new FloorFunction(parameters[0]);
        else if(name.equals(IEEE_REMAINDER))    return new IEEERemainderFunction(parameters[0],parameters[1]);
        else if(name.equals(LOG))               return new LogFunction(parameters[0]);
        else if(name.equals(MAX))               return new MaxFunction(parameters[0],parameters[1]);
        else if(name.equals(MIN))               return new MinFunction(parameters[0],parameters[1]);
        else if(name.equals(PI))                return new PiFunction();
        else if(name.equals(POW))               return new PowFunction(parameters[0],parameters[1]);
        else if(name.equals(RANDOM))            return new RandomFunction();
        else if(name.equals(RINT))              return new RintFunction(parameters[0]);
        else if(name.equals(ROUND))             return new RoundFunction(parameters[0]);
        else if(name.equals(SIN))               return new SinFunction(parameters[0]);
        else if(name.equals(SQRT))              return new SqrtFunction(parameters[0]);
        else if(name.equals(TAN))               return new TanFunction(parameters[0]);
        else if(name.equals(TO_DEGREES))        return new ToDegreesFunction(parameters[0]);
        else if(name.equals(TO_RADIANS))        return new ToRadiansFunction(parameters[0]);

        throw new IllegalArgumentException("Unknowed function name : "+ name);
    }

}
