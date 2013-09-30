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
package org.geotoolkit.s52.dai;

import java.util.Map;
import org.geotoolkit.s52.lookuptable.IMODisplayCategory;
import org.geotoolkit.s52.lookuptable.LookupRecord;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DAILookupRecord extends LookupRecord{

    public LookupTableEntryIdentifier identifier;
    public LookupComment comment;
    public Instruction instruction;
    public AttributeCombination attributes;
    public DisplayCategory category;

    @Override
    public String getObjectClass() {
        return identifier.OBCL;
    }

    @Override
    public Map<String,String> getAttributeCombinaison() {
        return attributes.map;
    }

    @Override
    public String getSymbolInstructions() {
        return instruction.SINS;
    }

    @Override
    public Integer getPriority() {
        return identifier.DPRI;
    }

    @Override
    public IMODisplayCategory getDisplayCategory() {
        return IMODisplayCategory.getOrCreate(category.DSCN);
    }

    @Override
    public Radar getRadar() {
        return Radar.fromCode(identifier.RPRI);
    }

    @Override
    public Integer getViewingGroup() {
        //TODO
        return 0;
    }

}
