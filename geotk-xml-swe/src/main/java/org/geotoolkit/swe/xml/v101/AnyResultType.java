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

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.ReferenceType;
import org.geotoolkit.internal.sql.Entry;
import org.geotoolkit.swe.xml.AnyResult;

/**
 * Enregistrement permettant de regrouper plusieur type de resultat en un meme type.
 * (implementation decrivant une classe union) hormis l'identifiant,
 * il ne doit y avoir qu'un attribut differend de {@code null}.
 *
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Any")
public class AnyResultType implements AnyResult, Entry {

    /**
     * The result identifier.
     */
    @XmlAttribute
    private String id;

    /**
     * The result can be a reference.
     */
    private ReferenceType reference;

    /**
     * The result can be an array.
     */
    private DataArrayPropertyType array;

    /**
     * Constructor used by jaxB
     */
    public AnyResultType(){}

    /**
     * build a new result with the specified reference.
     *
     * @param The result identifier.
     * @param reference The reference identifier.
     */
    public AnyResultType(final String id, final ReferenceType reference) {
        this.id = id;
        this.reference = reference;
    }

    /**
     * build a new result with the specified array of data.
     *
     * @param The result identifier.
     * @param reference The reference identifier.
     */
    public AnyResultType(final String id, final DataArrayType array) {
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

     public String getIdentifier() {
        return id;
    }

    public String getName() {
        return id;
    }

    /**
     * return a result of type reference if it is, {@code null} else.
     */
    public ReferenceType getReference() {
        return reference;
    }

    /**
     * return a result of type dataArray if it is, {@code null} else.
     */
    @Override
    public DataArrayType getArray() {
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
        if (object instanceof AnyResultType) {
            final AnyResultType that = (AnyResultType) object;

            return Objects.equals(this.array,     that.array)    &&
                   Objects.equals(this.id,        that.id)          &&
                   Objects.equals(this.reference, that.reference);
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
