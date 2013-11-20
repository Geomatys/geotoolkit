/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2013, Geomatys
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

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.filter.function.AbstractFunctionFactory;

/**
 * Factory registering the commun mathematical functions.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MathFunctionFactory extends AbstractFunctionFactory{

    public static final String ABS                  = "abs";
    public static final String ACOS                 = "acos";
    public static final String ASIN                 = "asin";
    public static final String ATAN2                = "atan2";
    public static final String ATAN                 = "atan";
    public static final String CEIL                 = "ceil";
    public static final String COS                  = "cos";
    public static final String EXP                  = "exp";
    public static final String FLOOR                = "floor";
    public static final String HYPOT                = "hypot";
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

    private static final Map<String,Class> FUNCTIONS = new HashMap<>();

    static{
        FUNCTIONS.put(ABS,              AbsFunction.class);
        FUNCTIONS.put(ACOS,             AcosFunction.class);
        FUNCTIONS.put(ASIN,             AsinFunction.class);
        FUNCTIONS.put(ATAN2,            Atan2Function.class);
        FUNCTIONS.put(ATAN,             AtanFunction.class);
        FUNCTIONS.put(CEIL,             CeilFunction.class);
        FUNCTIONS.put(COS,              CosFunction.class);
        FUNCTIONS.put(EXP,              ExpFunction.class);
        FUNCTIONS.put(FLOOR,            FloorFunction.class);
        FUNCTIONS.put(HYPOT,            HypotFunction.class);
        FUNCTIONS.put(IEEE_REMAINDER,   IEEERemainderFunction.class);
        FUNCTIONS.put(LOG,              LogFunction.class);
        FUNCTIONS.put(MAX,              MaxFunction.class);
        FUNCTIONS.put(MIN,              MinFunction.class);
        FUNCTIONS.put(PI,               PiFunction.class);
        FUNCTIONS.put(POW,              PowFunction.class);
        FUNCTIONS.put(RANDOM,           RandomFunction.class);
        FUNCTIONS.put(RINT,             RintFunction.class);
        FUNCTIONS.put(ROUND,            RoundFunction.class);
        FUNCTIONS.put(SIN,              SinFunction.class);
        FUNCTIONS.put(SQRT,             SqrtFunction.class);
        FUNCTIONS.put(TAN,              TanFunction.class);
        FUNCTIONS.put(TO_DEGREES,       ToDegreesFunction.class);
        FUNCTIONS.put(TO_RADIANS,       ToRadiansFunction.class);

    }

    public MathFunctionFactory() {
        super("math",FUNCTIONS);
    }

}
