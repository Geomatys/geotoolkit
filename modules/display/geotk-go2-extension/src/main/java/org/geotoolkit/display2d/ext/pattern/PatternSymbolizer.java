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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.measure.unit.NonSI;

import org.geotoolkit.style.AbstractExtensionSymbolizer;

import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PatternSymbolizer extends AbstractExtensionSymbolizer{

    public static final String NAME = "Pattern";

    private final Map<Expression, List<Symbolizer>> thredholds =
            new HashMap<Expression, List<Symbolizer>>();

    private final Expression channel;

    private final ThreshholdsBelongTo belongTo;

    public PatternSymbolizer(Expression channel, Map<Expression,List <Symbolizer>> ranges, ThreshholdsBelongTo belong) {
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

}
