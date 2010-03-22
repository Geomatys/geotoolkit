/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Transaction {

    private final List<IdentifiedElement> elements;
    private final TransactionType type;
    private final String version;
    private final String generator;

    public Transaction(TransactionType type, List<IdentifiedElement> elements, String version, String generator){

        if(elements instanceof ArrayList){
            this.elements = (List<IdentifiedElement>) ((ArrayList)elements).clone();
        }else{
            this.elements = new ArrayList<IdentifiedElement>(elements);
        }
        this.type = type;
        this.version = version;
        this.generator = generator;
    }

    public TransactionType getType(){
        return type;
    }

    public List<IdentifiedElement> getElements(){
        return elements;
    }

    public String getGenerator() {
        return generator;
    }

    public String getVersion() {
        return version;
    }
    
}
