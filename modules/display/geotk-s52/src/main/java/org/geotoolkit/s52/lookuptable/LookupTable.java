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

/**
 * S-52 lookup table.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LookupTable {

    private final List<LookupRecord> records = new ArrayList<>();

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
            if(rec.objectClass.equals(objClassName)){
                match.add(rec);
            }
        }
        return match;
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
            writer.append(rec.objectClass);                                     writer.nextColumn();
            writer.append(rec.attributeCombination);                           writer.nextColumn();
            writer.append(rec.symbolInstruction);                               writer.nextColumn();
            writer.append(rec.displayPriority==null?"":""+rec.displayPriority); writer.nextColumn();
            writer.append(""+rec.radar);                                        writer.nextColumn();
            writer.append(""+rec.imoDisplayCategory);                           writer.nextColumn();
            writer.append(rec.viewingGroup==null ? "":""+rec.viewingGroup);     writer.nextColumn();
        }
        writer.writeHorizontalSeparator();
        return writer.toString();

    }

}
