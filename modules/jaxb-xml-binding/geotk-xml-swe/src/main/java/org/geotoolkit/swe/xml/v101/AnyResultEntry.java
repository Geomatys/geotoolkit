/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.swe.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.ReferenceEntry;
import org.geotoolkit.swe.xml.AnyResult;
import org.geotoolkit.util.Utilities;

/**
 * Enregistrement permettant de regrouper plusieur type de resultat en un meme type.
 * (implementation decrivant une classe union) hormis l'identifiant, 
 * il ne doit y avoir qu'un attribut differend de {@code null}. 
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Any")
public class AnyResultEntry implements AnyResult {
    
    /**
     * The result identifier.
     */
    @XmlAttribute
    private String id;
    
    /**
     * The result can be a reference.
     */
    private ReferenceEntry reference;
    
    /**
     * The result can be an array.
     */
    private DataArrayPropertyType array;
    
    /**
     * Constructor used by jaxB
     */
    public AnyResultEntry(){}
    
    /**
     * build a new result with the specified reference.
     *
     * @param The result identifier.
     * @param reference The reference identifier.
     */
    public AnyResultEntry(String id, ReferenceEntry reference) {
        this.id = id;
        this.reference = reference;
    }
    
    /**
     * build a new result with the specified array of data.
     *
     * @param The result identifier.
     * @param reference The reference identifier.
     */
    public AnyResultEntry(String id, DataArrayEntry array) {
        this.id = id;
        this.array = new DataArrayPropertyType(array);
    }

    /**
     * Return the result identifier.
     */
    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return id;
    }

    /**
     * return a result of type reference if it is, {@code null} else.
     */
    public ReferenceEntry getReference() {
        return reference;
    }

    /**
     * return a result of type dataArray if it is, {@code null} else.
     */
    @Override
    public DataArrayEntry getArray() {
        if (array != null) {
            return array.getDataArray();
        }
        return null;
    }
    
    /**
     * return a result of type dataArray if it is, {@code null} else.
     */
    @Override
    public DataArrayPropertyType getPropertyArray() {
        return array;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AnyResultEntry) {
            final AnyResultEntry that = (AnyResultEntry) object;

            return Utilities.equals(this.array,     that.array)    &&
                   Utilities.equals(this.id,        that.id)          &&
                   Utilities.equals(this.reference, that.reference);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 13 * hash + (this.reference != null ? this.reference.hashCode() : 0);
        hash = 13 * hash + (this.array != null ? this.array.hashCode() : 0);
        return hash;
    }

    /**
     * return a String describing the result (debug).
     */
    @Override
    public String toString() {
        String res;
        if (reference == null)
            res = array.toString();
        else
            res = reference.toString();
        
        return "id = " + id + " value/idref: " + res;
    }
}
