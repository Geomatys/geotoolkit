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
package org.geotoolkit.s52.procedure;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.194 (12.2.18)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SAFCON01 extends Procedure{

    public SAFCON01() {
        super("SAFCON01");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    public Symbol[] eval(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic, double depthval){
        final List<Symbol> symbols = new ArrayList<>();

        String symbolprefix = "SAFCON";

        if(depthval < 0 || depthval > 99999){
            //coutour symbol can not be determined
            return new Symbol[0];
        }

        final long leadingDigits = Math.abs((long)depthval);
        final String valStr = Long.toString(leadingDigits);
        final double fractionDigits = depthval - leadingDigits;
        final char firstDigit = valStr.charAt(0);
        if(depthval < 10 && fractionDigits != 0){
            final long fl = (long)fractionDigits*10;
            symbols.add(new Symbol(symbolprefix+"0"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"6"+fl, null));
        }else if(depthval < 10){
            symbols.add(new Symbol(symbolprefix+"0"+firstDigit, null));
        }else if(depthval < 31 && fractionDigits != 0){
            final char secondDigit = valStr.charAt(1);
            final long fl = (long)fractionDigits*10;
            symbols.add(new Symbol(symbolprefix+"2"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"1"+secondDigit, null));
            symbols.add(new Symbol(symbolprefix+"5"+fl, null));
        }else if(depthval < 100){
            final char secondDigit = valStr.charAt(1);
            symbols.add(new Symbol(symbolprefix+"2"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"1"+secondDigit, null));
        }else if(depthval < 1000){
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            //warning : 80 symbols not present in default dai file.
            symbols.add(new Symbol(symbolprefix+"8"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"0"+secondDigit, null));
            symbols.add(new Symbol(symbolprefix+"9"+thirdDigit, null));
        }else if(depthval < 10000){
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            final char fourthDigit = valStr.charAt(3);
            symbols.add(new Symbol(symbolprefix+"3"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"2"+secondDigit, null));
            symbols.add(new Symbol(symbolprefix+"1"+thirdDigit, null));
            symbols.add(new Symbol(symbolprefix+"7"+fourthDigit, null));
        }else if(depthval < 100000){
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            final char fourthDigit = valStr.charAt(3);
            final char fifthDigit = valStr.charAt(4);
            symbols.add(new Symbol(symbolprefix+"4"+firstDigit, null));
            symbols.add(new Symbol(symbolprefix+"3"+secondDigit, null));
            symbols.add(new Symbol(symbolprefix+"2"+thirdDigit, null));
            symbols.add(new Symbol(symbolprefix+"1"+fourthDigit, null));
            symbols.add(new Symbol(symbolprefix+"7"+fifthDigit, null));
        }

        return symbols.toArray(new Symbol[symbols.size()]);
    }

}
