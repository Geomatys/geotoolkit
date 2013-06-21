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
package org.geotoolkit.display2d.ext.cellular;

import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import org.geotoolkit.style.AbstractExtensionSymbolizer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Rule;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CellSymbolizer extends AbstractExtensionSymbolizer {

    public static final String NAME = "Cell";
    
    private final int cellSize;
    private List<? extends Rule> rules;

    public CellSymbolizer(int cellSize, List<? extends Rule> rules){
        super(NonSI.PIXEL,"","Cell",StyleConstants.DEFAULT_DESCRIPTION);
        this.cellSize = cellSize;
        this.rules = rules;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    public int getCellSize() {
        return cellSize;
    }

    public List<? extends Rule> getRules() {
        return rules;
    }
    
}
