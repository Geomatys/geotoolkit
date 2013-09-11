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
import org.geotoolkit.s52.S52Utilities;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.geotoolkit.s52.symbolizer.S52Graphic;

/**
 * S-52 Annex A Part I p.203 (12.2.21)
 *
 * @author Johann Sorel (Geomatys)
 */
public class SNDFRM03 extends Procedure{

    public SNDFRM03() {
        super("SNDFRM03");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic) throws PortrayalException {
        System.out.println("Procedure "+getName()+" not implemented yet");
    }

    public Symbol[] render(RenderingContext2D ctx, S52Context context, S52Palette colorTable,
            List<S52Graphic> all, S52Graphic graphic, double depthVal) {
        final List<Symbol> symbols = new ArrayList<>();

        final double safetyDepth = context.getSafetyDepth();

        final String prefix;
        if(depthVal <= safetyDepth){
            prefix = "SOUNDS";
        }else{
            prefix = "SOUNDG";
        }

        final String[] tecsou = (String[])graphic.feature.getProperty("TECSOU").getValue();
        if(S52Utilities.containsAny(tecsou, "6")){
            symbols.add(new Symbol(prefix+"B1", null));
        }

        final String[] quasou = (String[]) graphic.feature.getProperty("QUASOU").getValue();
        final String[] status = (String[]) graphic.feature.getProperty("STATUS").getValue();

        if(S52Utilities.containsAny(quasou, "3","4","5","8","9") || S52Utilities.containsAny(status, "18")){
            symbols.add(new Symbol(prefix+"C2", null));
        }else{
            //TODO get spatial object
            final Object value = (graphic.feature.getProperty("QUAPOS")==null) ? null : graphic.feature.getProperty("QUAPOS").getValue();
            if(value != null){
                int val = Integer.valueOf(value.toString());
                if(val > 1 && val < 10){
                    symbols.add(new Symbol(prefix+"C2", null));
                }
            }
        }

        // Continuation A
        if(depthVal < 0){
            symbols.add(new Symbol(prefix+"A1", null));
        }

        final long leadingDigits = Math.abs((long)depthVal);
        final String valStr = Long.toString(leadingDigits);
        final double fractionDigits = depthVal - leadingDigits;
        final char firstDigit = valStr.charAt(0);
        if(depthVal < 10){
            final long fl = (long)fractionDigits*10;
            symbols.add(new Symbol(prefix+"1"+firstDigit, null));
            symbols.add(new Symbol(prefix+"5"+fl, null));
        }else if(depthVal < 31 && fractionDigits != 0){
            final char secondDigit = valStr.charAt(1);
            final long fl = (long)fractionDigits*10;
            symbols.add(new Symbol(prefix+"2"+firstDigit, null));
            symbols.add(new Symbol(prefix+"1"+secondDigit, null));
            symbols.add(new Symbol(prefix+"5"+fl, null));
        }
        // Continuation B
        else if(depthVal < 100){
            final char secondDigit = valStr.charAt(1);
            symbols.add(new Symbol(prefix+"1"+firstDigit, null));
            symbols.add(new Symbol(prefix+"0"+secondDigit, null));
        }else if(depthVal < 1000){
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            //warning : 80 symbols not present in default dai file.
            symbols.add(new Symbol(prefix+"2"+firstDigit, null));
            symbols.add(new Symbol(prefix+"1"+secondDigit, null));
            symbols.add(new Symbol(prefix+"0"+thirdDigit, null));
        }else if(depthVal < 10000){
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            final char fourthDigit = valStr.charAt(3);
            symbols.add(new Symbol(prefix+"2"+firstDigit, null));
            symbols.add(new Symbol(prefix+"1"+secondDigit, null));
            symbols.add(new Symbol(prefix+"0"+thirdDigit, null));
            symbols.add(new Symbol(prefix+"4"+fourthDigit, null));
        }else{
            final char secondDigit = valStr.charAt(1);
            final char thirdDigit = valStr.charAt(2);
            final char fourthDigit = valStr.charAt(3);
            final char fifthDigit = valStr.charAt(4);
            symbols.add(new Symbol(prefix+"3"+firstDigit, null));
            symbols.add(new Symbol(prefix+"2"+secondDigit, null));
            symbols.add(new Symbol(prefix+"1"+thirdDigit, null));
            symbols.add(new Symbol(prefix+"0"+fourthDigit, null));
            symbols.add(new Symbol(prefix+"4"+fifthDigit, null));
        }

        return symbols.toArray(new Symbol[symbols.size()]);
    }

}
