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

import java.util.List;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Transaction {

    private final List<IdentifiedElement> elements;
    private final TransactionType type;

    public Transaction(TransactionType type, List<IdentifiedElement> elements){
        IdentifiedElement[] ele = elements.toArray(new IdentifiedElement[elements.size()]);
        this.elements = UnmodifiableArrayList.wrap(ele);
        this.type = type;
    }

    public List<IdentifiedElement> getElements(){
        return elements;
    }

    public static Transaction create(List<IdentifiedElement> elements){
        return new Transaction(TransactionType.CREATE, elements);
    }

    public static Transaction modify(List<IdentifiedElement> elements){
        return new Transaction(TransactionType.MODIFY, elements);
    }

    public static Transaction delete(List<IdentifiedElement> elements){
        return new Transaction(TransactionType.DELETE, elements);
    }
    
}
