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
package org.geotoolkit.data.s57.iso8211;

import java.io.DataInput;
import java.io.IOException;
import static org.geotoolkit.data.s57.iso8211.ISO8211Reader.*;
import static org.geotoolkit.data.s57.iso8211.ISO8211Constants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FieldDescription {
    
    //field control
    private FieldDataStructure structure;
    private int type;
    private byte[] lexicalLevel;

    public FieldDescription() {
    }
    
    public void read(final DataInput ds) throws IOException{
        structure = FieldDataStructure.get(Integer.parseInt(new String(new byte[]{ds.readByte()})));
        type = Integer.parseInt(new String(new byte[]{ds.readByte()}));
        expect(ds, FC_CONTROL);
        lexicalLevel = new byte[3];
        ds.readFully(lexicalLevel);
    }

    /**
     * @return the structure
     */
    public FieldDataStructure getStructure() {
        return structure;
    }

    /**
     * @param structure the structure to set
     */
    public void setStructure(FieldDataStructure structure) {
        this.structure = structure;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the lexicalLevel
     */
    public byte[] getLexicalLevel() {
        return lexicalLevel;
    }

    /**
     * @param lexicalLevel the lexicalLevel to set
     */
    public void setLexicalLevel(byte[] lexicalLevel) {
        this.lexicalLevel = lexicalLevel;
    }
    
    
}
