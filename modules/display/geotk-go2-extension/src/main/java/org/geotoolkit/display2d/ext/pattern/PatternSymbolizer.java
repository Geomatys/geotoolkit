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

package org.geotoolkit.display2d.ext.pattern;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.measure.unit.NonSI;

import org.geotoolkit.style.AbstractExtensionSymbolizer;

import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternSymbolizer extends AbstractExtensionSymbolizer{

    public static final String NAME = "Pattern";

    private final Map<Expression, List<Symbolizer>> thredholds =
            new TreeMap<Expression, List<Symbolizer>>(new ExpComparator());

    private static final class ExpComparator implements Comparator<Expression>{

        @Override
        public int compare(final Expression t, final Expression t1) {
            final Literal left = (Literal) t;
            final Literal right = (Literal) t1;

            if(left == null || left.getValue() == null){
                return -1;
            }else if(right == null || right.getValue() == null){
                return +1;
            }else{
                final Number leftN = left.evaluate(null,Number.class);
                final Number righN = right.evaluate(null,Number.class);
                final double res = leftN.doubleValue() - righN.doubleValue();
                if(res < 0){
                    return -1;
                }else if(res > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        }

    }


    private final Expression channel;

    private final ThreshholdsBelongTo belongTo;

    public PatternSymbolizer(final Expression channel, final Map<Expression,List <Symbolizer>> ranges, final ThreshholdsBelongTo belong) {
        super(NonSI.PIXEL, "", "", null);
        this.channel = channel;
        this.thredholds.putAll(ranges);
        this.belongTo = belong;
    }

    public Expression getChannel(){
        return channel;
    }
    
    public ThreshholdsBelongTo getBelongTo(){
        return belongTo;
    }

    public Map<Expression, List<Symbolizer>> getRanges() {
        return thredholds;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pattern symbolizer \n");

        for(Map.Entry<Expression,List<Symbolizer>> entry : thredholds.entrySet()){
            System.out.println(" - " + entry.getKey());
            for(Symbolizer s : entry.getValue()){
                System.out.println(" - - " + s);
            }
        }

        return sb.toString();
    }

}
