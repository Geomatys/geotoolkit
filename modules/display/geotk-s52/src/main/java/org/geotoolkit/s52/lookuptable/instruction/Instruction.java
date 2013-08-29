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

/**
 * S-52 rendering instruction.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class Instruction {

    private final String code;

    public Instruction(String code) {
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    /**
     * Read parameters from given string.
     * @param str
     */
    public final void read(String str) throws IOException{
        if(str.startsWith(code)){
            if(str.charAt(code.length()) != '(' || str.charAt(str.length()-1) != ')'){
                throw new IOException("Uncorrect instruction, missing () : "+str);
            }
            str = str.substring(code.length()+1,str.length()-1);
        }else{
            throw new IOException("Uncorrect instruction, does not start by "+code + "  : "+str);
        }
        readParameters(str);
    }


    /**
     * Read instruction parameters from given string.
     * @param str
     */
    protected abstract void readParameters(String str) throws IOException;

    /**
     * Create a new instance of this instruction.
     * @return Instruction
     */
    public Instruction newInstance(){
        try {
            return (Instruction)this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to create a new instance of : "+this.getClass().getName());
        }
    }

}
