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

import java.io.IOException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.s52.symbolizer.FormatExpression;
import org.opengis.filter.expression.Expression;

/**
 * S-52 Annex A Part I p.47  7.1.1
 *
 * @author Johann Sorel (Geomatys)
 */
public class NumericText extends Text{

    public NumericText() {
        super("TE");
    }

    public String format;

    @Override
    protected void readParameters(String str) throws IOException {
        final int index = str.indexOf(',');
        format = str.substring(1, index-1); // remove quotes
        str = str.substring(index+1);
        super.readParameters(str);
    }

    public Expression getText(){
        //S-52 Annex A Part I p.49 7.1.2.2

        final Expression[] values = new Expression[text.length+1];
        values[0] = GO2Utilities.FILTER_FACTORY.literal(format);

        for(int i=0;i<text.length;i++){
            //always attributs
            if(text[i].startsWith("\'")){
                values[i+1] = GO2Utilities.FILTER_FACTORY.property(text[i].substring(1, text[i].length()-1));
            }else{
                values[i+1] = GO2Utilities.FILTER_FACTORY.property(text[i]);
            }
        }

        return new FormatExpression(values);
    }

}
