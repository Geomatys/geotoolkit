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

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.io.TableAppender;
import org.opengis.feature.Feature;

/**
 * S-52 lookup table.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LookupTable {

    private final String name;
    private final List<LookupRecord> records = new ArrayList<>();

    public LookupTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<LookupRecord> getRecords() {
        return records;
    }

    /**
     * Get records for given object class name.
     *
     * @param objClassName
     * @return LookupRecord[]
     */
    public List<LookupRecord> getRecords(String objClassName){
        final List<LookupRecord> match = new ArrayList<>();
        for(int i=0,n=records.size();i<n;i++){
            final LookupRecord rec = records.get(i);
            if(rec.getObjectClass().equals(objClassName)){
                match.add(rec);
            }
        }
        return match;
    }

    /**
     * Find the lookup record which apply to this feature.
     * @param records
     * @param feature
     * @return
     */
    public static LookupRecord getActiveRecord(List<LookupRecord> records, Feature feature){
        final int size = records.size();
        // 0 is the fail safe record (p.66 8.3.3.3)
        LookupRecord validRec = records.get(0);
        if(size == 1){
            // Annex A part I p.65 8.3.3.2
            // If only a single line is found, field 2 of that line shall be empty
            // and the object is always shown with the same symbology.
            return records.get(0);
        }

        for(int i=1;i<size;i++){
            //filter on fields
            final LookupRecord rec = records.get(i);
            if(rec.getFilter().evaluate(feature)){
                return rec;
            }
        }

        return validRec;
    }

    @Override
    public String toString() {
        final TableAppender writer = new TableAppender();
        writer.setMultiLinesCells(true);
        writer.writeHorizontalSeparator();
        writer.append("Object Class");              writer.nextColumn();
        writer.append("Atttribute Combination");    writer.nextColumn();
        writer.append("Symbol Instruction");        writer.nextColumn();
        writer.append("Display Priority");          writer.nextColumn();
        writer.append("Radar");                     writer.nextColumn();
        writer.append("IMO Display Category");      writer.nextColumn();
        writer.append("Viewing Group");             writer.nextColumn();
        writer.writeHorizontalSeparator();

        for(LookupRecord rec : records){
            writer.nextLine();
            writer.append(rec.getObjectClass());                                      writer.nextColumn();
            writer.append(rec.getAttributeCombinaison().toString());                  writer.nextColumn();
            writer.append(rec.getSymbolInstructions());                               writer.nextColumn();
            writer.append(rec.getPriority()==null?"":""+rec.getPriority());           writer.nextColumn();
            writer.append(""+rec.getRadar());                                         writer.nextColumn();
            writer.append(""+rec.getDisplayCategory());                                writer.nextColumn();
            writer.append(rec.getViewingGroup()==null ? "":""+rec.getViewingGroup()); writer.nextColumn();
        }
        writer.writeHorizontalSeparator();
        return writer.toString();

    }

}
