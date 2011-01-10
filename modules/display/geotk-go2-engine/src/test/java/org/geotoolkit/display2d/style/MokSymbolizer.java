/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.display2d.style;

import java.util.Collections;
import java.util.Map;
import javax.measure.unit.NonSI;
import org.geotoolkit.style.AbstractSymbolizer;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;

/**
 * Test that symbolizer renderer are properly called and only once.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MokSymbolizer extends AbstractSymbolizer implements ExtensionSymbolizer{

    public MokSymbolizer(){
        super(NonSI.PIXEL,null,"mok",new DefaultDescription(
                new SimpleInternationalString(""),
                new SimpleInternationalString("")));
    }

    @Override
    public String getExtensionName() {
        return "Mok";
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.emptyMap();
    }

    @Override
    public Object accept(final StyleVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

}
