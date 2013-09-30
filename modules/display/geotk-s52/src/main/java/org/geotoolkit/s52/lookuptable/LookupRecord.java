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
package org.geotoolkit.s52.lookuptable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import static org.geotoolkit.display2d.GO2Utilities.FILTER_FACTORY;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.lookuptable.instruction.AlphanumericText;
import org.geotoolkit.s52.lookuptable.instruction.ColorFill;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.ConditionalSymbolProcedure;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.geotoolkit.s52.lookuptable.instruction.NumericText;
import org.geotoolkit.s52.lookuptable.instruction.PatternFill;
import org.geotoolkit.s52.lookuptable.instruction.SimpleLine;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.opengis.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class LookupRecord {

    private static final Map<String,Instruction> INSTS = new HashMap<>();
    static {
        final Instruction[] array = new Instruction[]{
            new AlphanumericText(),
            new SimpleLine(),
            new ComplexLine(),
            new ConditionalSymbolProcedure(),
            new ColorFill(),
            new NumericText(),
            new PatternFill(),
            new Symbol()
        };
        for(Instruction df : array){
            INSTS.put(df.getCode(), df);
        }
    }

    public static enum Radar{
        O("O"),
        S("S"),
        NULL("");

        public String code;

        private Radar(String code) {
            this.code = code;
        }

        public static Radar fromCode(String code){
            if("O".equals(code)) return O;
            if("S".equals(code)) return S;
            if(code.isEmpty()) return NULL;
            throw new IllegalArgumentException("No rader for code : '"+code+"'");
        }
    }

    public abstract String getObjectClass();

    public abstract Map<String,String> getAttributeCombinaison();

    public abstract String getSymbolInstructions();

    public abstract Integer getPriority();

    public abstract IMODisplayCategory getDisplayCategory();

    public abstract Radar getRadar();

    public abstract Integer getViewingGroup();

    // cache
    // filter build from attributeCombination
    private Filter filter;
    private Instruction[] instruction;

    public Filter getFilter() {
        if(filter == null){
            final Map<String,String> map = getAttributeCombinaison();
            // S-52 Annex A part I p.66  8.3.3.4
            if(map == null || map.isEmpty()){
                //all match
                filter = Filter.INCLUDE;
            }else{
                final List<Filter> parts = new ArrayList<>();
                for(final Entry<String,String> entry : map.entrySet()){
                    final String attName = entry.getKey();
                    String str = entry.getValue();
                    final char val = (str.length()>0) ? str.charAt(0) : 'A';
                    if(val == '?'){
                        //not a number, means value must be null
                        parts.add(FILTER_FACTORY.isNull(FILTER_FACTORY.property(attName)));
                    }else if(Character.isDigit(val)){
                        int start = 0;
                        int end = 0;
                        boolean hasNextValue = false;
                        do{
                            for(int i=start,n=str.length();i<n;i++){
                                if(Character.isDigit(str.charAt(i))){
                                    end = i;
                                }else{
                                    break;
                                }
                            }
                            final int value = Integer.valueOf(str.substring(start, end+1));
                            parts.add(FILTER_FACTORY.equals(FILTER_FACTORY.property(attName),FILTER_FACTORY.literal(value)));
                            str = str.substring(end+1);

                            //check for more values
                            hasNextValue = str.startsWith(",");
                            if(hasNextValue){
                                str = str.substring(1);
                                start = 0;
                                end = 0;
                            }
                        }while(hasNextValue);
                    }else{
                        //not a number, means value must not be null
                        parts.add(FILTER_FACTORY.not(FILTER_FACTORY.isNull(FILTER_FACTORY.property(attName))));
                    }
                }

                if(parts.size() == 1){
                    filter = parts.get(0);
                }else{
                    filter = FILTER_FACTORY.and(parts);
                }
            }
        }
        return filter;
    }

    public Instruction[] getInstruction() {
        final String symbolInstruction = getSymbolInstructions();
        if(instruction == null){
            if(symbolInstruction.isEmpty()){
                instruction = new Instruction[0];
            }else{
                final String[] parts = symbolInstruction.split(";");
                instruction = new Instruction[parts.length];
                try{
                    for(int i=0;i<parts.length;i++){
                        final int index = parts[i].indexOf('(');
                        final String code = parts[i].substring(0, index);
                        Instruction inst = INSTS.get(code);
                        if(inst != null){
                            inst = inst.newInstance();
                            inst.read(parts[i]);
                            instruction[i] = inst;
                        }else{
                            System.out.println("Unknowned instruction "+ code);
                        }
                    }
                }catch(IOException ex){
                    S52Context.LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    instruction = new Instruction[0];
                }
            }
        }
        return instruction;
    }

}
