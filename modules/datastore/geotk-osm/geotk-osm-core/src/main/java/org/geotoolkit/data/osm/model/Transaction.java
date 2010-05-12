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
 * Diff files are composed of transactions.
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

    /**
     * @return TransactionType
     */
    public TransactionType getType(){
        return type;
    }

    /**
     * @return List if element affected by this transaction.
     */
    public List<IdentifiedElement> getElements(){
        return elements;
    }

    /**
     * @return Name of the tool which generated this transaction.
     */
    public String getGenerator() {
        return generator;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Transaction ");
        sb.append(type.toString());
        sb.append(" version=").append(version);
        sb.append(" generator=").append(generator);
        for(IdentifiedElement ele : elements){
            sb.append('\n');
            sb.append(ele.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        if (this.elements != other.elements && (this.elements == null || !this.elements.equals(other.elements))) {
            return false;
        }
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if ((this.generator == null) ? (other.generator != null) : !this.generator.equals(other.generator)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.elements != null ? this.elements.hashCode() : 0);
        hash = 17 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 17 * hash + (this.generator != null ? this.generator.hashCode() : 0);
        return hash;
    }

}
