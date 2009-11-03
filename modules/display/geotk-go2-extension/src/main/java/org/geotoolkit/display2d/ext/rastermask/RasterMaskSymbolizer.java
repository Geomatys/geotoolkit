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

package org.geotoolkit.display2d.ext.rastermask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.measure.unit.NonSI;

import org.geotoolkit.style.AbstractExtensionSymbolizer;

import org.opengis.filter.expression.Expression;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class RasterMaskSymbolizer extends AbstractExtensionSymbolizer{

    public static final String NAME = "RasterMask";

    private final Map<Expression, List<Symbolizer>> thredholds =
            new HashMap<Expression, List<Symbolizer>>();

    public RasterMaskSymbolizer(Map<Expression,List <Symbolizer>> thredholds) {
        super(NonSI.PIXEL, "", "", null);
        this.thredholds.putAll(thredholds);
    }

    public Map<Expression, List<Symbolizer>> getThredholds() {
        return thredholds;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

}
