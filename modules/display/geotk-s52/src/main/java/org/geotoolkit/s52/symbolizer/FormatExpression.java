/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.s52.symbolizer;

import java.util.Arrays;
import java.util.UnknownFormatConversionException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

/**
 * S-52 text formatting for one or multiple values.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FormatExpression extends AbstractFunction {

    private static final Literal FALLBACK = GO2Utilities.FILTER_FACTORY.literal("");

    /**
     *
     * @param values first is format then values
     */
    public FormatExpression(Expression[] values) {
        super("S52TextFormat", values, FALLBACK);
    }

    @Override
    public Object evaluate(Object o) {
        final Literal formatExp = (Literal) parameters.get(0);
        String format = formatExp.getValue().toString();

        //replace formats not supported by java
        format = format.replaceAll("lf", "g");
        format = format.replaceAll("lg", "g");
        format = format.replaceAll("le", "g");


        final Object[] values = new Object[parameters.size()-1];
        for(int i=0;i<values.length;i++){
            values[i] = parameters.get(i+1).evaluate(o);
        }

        if(format.isEmpty()){
            //concatenate value with a space
            final StringBuilder sb = new StringBuilder();
            for(int i=0;i<values.length;i++){
                if(values[i]==null) continue;
                if(i>0) sb.append(' ');
                if(values[i] instanceof String[]){
                    final String[] strs = (String[]) values[i];
                    for(int k=0;k<strs.length;k++){
                        if(k>0) sb.append(',');
                        sb.append(strs[k]);
                    }
                }else{
                    sb.append(values[i]);
                }

            }
            return sb.toString();
        }else{
            //use provided format
            try{
                return String.format(format, values);
            }catch(Exception ex){
                System.out.println(">>>>>>>>" +format+ "    "+ Arrays.toString(values));
                ex.printStackTrace();
                return "";
            }
        }
    }

}
