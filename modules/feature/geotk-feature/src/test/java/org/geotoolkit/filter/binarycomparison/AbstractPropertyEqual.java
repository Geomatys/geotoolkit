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
package org.geotoolkit.filter.binarycomparison;

import java.util.Calendar;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.StringUtilities;
import org.opengis.filter.MatchAction;
import org.opengis.filter.expression.Expression;

/**
 * Abstract "property equal" filter, used by isEqual and isNotEqual subclass to avoid
 * duplicaton the same test in both classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractPropertyEqual extends AbstractBinaryComparisonOperator<Expression,Expression> {

    private static final double EPS = 1E-12;

    public AbstractPropertyEqual(final Expression left, final Expression right, final boolean match, final MatchAction matchAction) {
        super(left,right,match,matchAction);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluateOne(Object value1, Object value2) {

        if (value1 == value2) {
            // Includes the (value1 == null && value2 == null) case.
            return true;
        }
        if (value1 == null || value2 == null) {
            // No need to check for (value2 != null) or (value1 != null).
            // If they were null, the previous check would have caugh them.
            return false;
        }

        //quick resolving to avoid using converters-----------------------------
        if(value1.getClass() == value2.getClass()){
            //same class, return the equal value directly.

            if(!match && value1 instanceof String ){
                //special case if we are in case insensitive
                return ((String)value1).equalsIgnoreCase((String)value2);
            }else{
                return value1.equals(value2);
            }

        }else if(value1 instanceof Number && value2 instanceof Number){
            //test number case
            return numberEqual((Number)value1, (Number)value2);
        }else if(value1.equals(value2)){
            //test standard equal
            //but classes are not the same, so will have to use the converters
            //to ensure a proper compare
            return true;
        }

        //we rely on converters to ensure proper compare oparations
        try {
            Object converted1 = ObjectConverters.convert(value1, value2.getClass());
            if(converted1 != null){
                if(equalOrNumberEqual(value2, converted1)){
                    return true;
                }
            }
        } catch (UnconvertibleObjectException | UnsupportedOperationException e) {
            // TODO: temporary fix, catch UnsupportedOperationException in the case of
            // a converter found which does not override the inverse() method
            // To fix in apache-sis
            Logging.recoverableException(null, AbstractPropertyEqual.class, "evaluate", e);
            // TODO - do we really want to ignore?
        }

        try {
            Object converted2 = ObjectConverters.convert(value2, value1.getClass());
            if (value1 instanceof java.sql.Date && converted2 != null) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime((java.sql.Date)value1);
                int expYear  = cal1.get(Calendar.YEAR);
                int expMonth = cal1.get(Calendar.MONTH);
                int expDay   = cal1.get(Calendar.DAY_OF_MONTH);

                cal1.setTime((java.sql.Date)converted2);
                return cal1.get(Calendar.YEAR) == expYear &&
                       cal1.get(Calendar.MONTH) == expMonth &&
                       cal1.get(Calendar.DAY_OF_MONTH) == expDay;
            }

            if(converted2 != null){
                if(equalOrNumberEqual(value1, converted2)){
                    return true;
                }
            }
        } catch (UnconvertibleObjectException e) {
            Logging.recoverableException(null, AbstractPropertyEqual.class, "evaluate", e);
            // TODO - do we really want to ignore?
        }

        //no comparison matches
        return false;
    }

    private boolean equalOrNumberEqual(final Object value1, final Object value2){

        //test general equal case
        if(!match && value1 instanceof String && value2 instanceof String){
            if( ((String)value1).equalsIgnoreCase((String)value2)){
                return true;
            }
        }

        if(value1.equals(value2)){
            return true;
        }

        //test number case
        if(value1 instanceof Number && value2 instanceof Number){
            return numberEqual((Number)value1, (Number)value2);
        }

        return false;
    }

    private static boolean numberEqual(final Number value1, final Number value2){
        final Number n1 = (Number) value1;
        final Number n2 = (Number) value2;

        if( (n1 instanceof Float) || (n1 instanceof Double)
         || (n2 instanceof Float) || (n2 instanceof Double)){
            final double d1 = n1.doubleValue();
            final double d2 = n2.doubleValue();
            if (Double.doubleToLongBits(d1) == Double.doubleToLongBits(d2)) {
                return true;
            }
            if (Math.abs(d1 - d2) < EPS * Math.max(Math.abs(d1), Math.abs(d2))) {
                return true;
            }
        } else {
            return n1.longValue() == n2.longValue();
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyEqual (matchcase=");
        sb.append(match).append(")");
        sb.append(StringUtilities.toStringTree(left,right));
        return sb.toString();
    }

}
