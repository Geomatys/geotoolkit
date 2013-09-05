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

import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedObject;
import org.geotoolkit.s52.S52Context;
import org.geotoolkit.s52.S52Palette;
import org.geotoolkit.s52.lookuptable.LookupRecord;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.lookuptable.TxtLookupRecord;
import org.geotoolkit.s52.lookuptable.instruction.ComplexLine;
import org.geotoolkit.s52.lookuptable.instruction.Instruction;
import org.geotoolkit.s52.lookuptable.instruction.Symbol;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SLCONS03 extends Procedure{

    private static final Symbol POINT_INST = new Symbol("LOWACC01",null);
    private static final ComplexLine LINE_INST = new ComplexLine("LOWACC21");
    private static final LookupTable LOOKUPTABLE = new LookupTable();
    static {
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","",        "LS(SOLD,2,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","CONDTN1", "LS(DASH,1,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","CONDTN2", "LS(DASH,1,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","CATSLC6", "LS(SOLD,4,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","CATSLC15","LS(SOLD,4,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","CATSLC16","LS(SOLD,4,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","WATLEV2", "LS(SOLD,2,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","WATLEV3", "LS(DASH,2,CSTLN)"));
        LOOKUPTABLE.getRecords().add(new TxtLookupRecord("SLCONS","WATLEV4", "LS(DASH,2,CSTLN)"));
    }

    public SLCONS03() {
        super("SLCONS03");
    }

    @Override
    public void render(RenderingContext2D ctx, S52Context context, S52Palette colorTable, ProjectedObject graphic, S52Context.GeoType geotype) throws PortrayalException {
        final Feature feature = (Feature) graphic.getCandidate();

        if(geotype == S52Context.GeoType.POINT){
            final Object value = (feature.getProperty("QUAPOS")==null) ? null : feature.getProperty("QUAPOS").getValue();

            if(value != null){
                int val = Integer.valueOf(value.toString());
                if(val > 1 && val < 10){
                    POINT_INST.render(ctx, context, colorTable, graphic, geotype);
                }
            }

        }else if(geotype == S52Context.GeoType.LINE){
            //TODO for each spatial component
            final Object value = (feature.getProperty("QUAPOS")==null) ? null : feature.getProperty("QUAPOS").getValue();
            if(value != null){
                int val = Integer.valueOf(value.toString());
                if(val > 1 && val < 10){
                    LINE_INST.render(ctx, context, colorTable, graphic, geotype);
                    //TODO next geometry
                }else{
                    final LookupRecord rec = LookupTable.getActiveRecord(LOOKUPTABLE.getRecords(), feature);
                    for(Instruction inst : rec.getInstruction()){
                        inst.render(ctx, context, colorTable, graphic, geotype);
                    }
                }
            }else{
                final LookupRecord rec = LookupTable.getActiveRecord(LOOKUPTABLE.getRecords(), feature);
                for(Instruction inst : rec.getInstruction()){
                    inst.render(ctx, context, colorTable, graphic, geotype);
                }
            }

        }else if(geotype == S52Context.GeoType.AREA){
            //TODO for each spatial component
            final Object value = (feature.getProperty("QUAPOS")==null) ? null : feature.getProperty("QUAPOS").getValue();
            if(value != null){
                int val = Integer.valueOf(value.toString());
                if(val > 1 && val < 10){
                    LINE_INST.render(ctx, context, colorTable, graphic, geotype);
                    //TODO next geometry
                }else{
                    final LookupRecord rec = LookupTable.getActiveRecord(LOOKUPTABLE.getRecords(), feature);
                    for(Instruction inst : rec.getInstruction()){
                        inst.render(ctx, context, colorTable, graphic, geotype);
                    }
                }
            }else{
                final LookupRecord rec = LookupTable.getActiveRecord(LOOKUPTABLE.getRecords(), feature);
                for(Instruction inst : rec.getInstruction()){
                    inst.render(ctx, context, colorTable, graphic, geotype);
                }
            }
        }

    }

}
