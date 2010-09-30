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
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.converter.Classes;
import org.opengis.filter.expression.Expression;

/**
 * Abstract "property equal" filter, used by isEqual and isNotEqual subclass to avoid
 * duplicaton the same test in both classes.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractPropertyEqual extends AbstractBinaryComparisonOperator<Expression,Expression> {

    private static final double EPS = 1E-12;

    public AbstractPropertyEqual(Expression left, Expression right, boolean match) {
        super(left,right,match);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object candidate) {
        final Object value1 = left.evaluate(candidate);
        final Object value2 = right.evaluate(candidate);

        if (value1 == value2) {
            // Includes the (value1 == null && value2 == null) case.
            return true;
        }
        if (value1 == null || value2 == null) {
            // No need to check for (value2 != null) or (value1 != null).
            // If they were null, the previous check would have caugh them.
            return false;
        }

        if(equalOrNumberEqual(value1, value2)){
            return true;
        }

        //use converters to make comparison
        Object converted1 = Converters.convert(value1, value2.getClass());
        if(converted1 != null){
            if(equalOrNumberEqual(value2, converted1)){
                return true;
            } 
        }

        Object converted2 = Converters.convert(value2, value1.getClass());
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

        //no comparison matches
        return false;
    }

    private boolean equalOrNumberEqual(Object value1, Object value2){

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
            final Number n1 = (Number) value1;
            final Number n2 = (Number) value2;

            if(Classes.isInteger(n1.getClass()) && Classes.isInteger(n2.getClass())){
                final long l1 = n1.longValue();
                final long l2 = n2.longValue();
                return l1 == l2;
            }else {
                final double d1 = n1.doubleValue();
                final double d2 = n2.doubleValue();

                if (Math.abs(d1 - d2) < EPS * Math.max(Math.abs(d1), Math.abs(d2))) {
                    return true;
                }
                if (Double.isNaN(d1) && Double.isNaN(d2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PropertyEqual (matchcase=");
        sb.append(match).append(")\n");
        sb.append(StringUtilities.toStringTree(left,right));
        return sb.toString();
    }

}
