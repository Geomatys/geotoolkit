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
package org.geotoolkit.s52.lookuptable.instruction;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.s52.symbolizer.FormatExpression;
import org.opengis.filter.expression.Expression;

/**
 * S-52 Annex A Part I p.47  7.1.1
 *
 * @author Johann Sorel (Geomatys)
 */
public class AlphanumericText extends Text{

    public AlphanumericText() {
        super("TX");
    }

    public Expression getText(){
        //S-52 Annex A Part I p.49 7.1.2.2
        if(text[0].startsWith("\'")){
            //literal
            return new FormatExpression(
                    new Expression[]{
                        GO2Utilities.FILTER_FACTORY.literal(""),
                        GO2Utilities.FILTER_FACTORY.literal(text[0].substring(1, text[0].length()-1))
                    });

        }else{
            //attribute value
            return new FormatExpression(
                    new Expression[]{
                    GO2Utilities.FILTER_FACTORY.literal(""),
                    GO2Utilities.FILTER_FACTORY.property(text[0])
                    });
        }
    }

}
