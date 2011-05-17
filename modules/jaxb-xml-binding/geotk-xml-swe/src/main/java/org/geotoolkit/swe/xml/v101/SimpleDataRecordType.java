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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AnyScalar;
import org.geotoolkit.swe.xml.SimpleDataRecord;
import org.geotoolkit.util.ComparisonMode;

/**
 * Liste de valeur scalaire ou textuelle utilisé dans le resultat d'une observation.
 * 
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleDataRecord")
public class SimpleDataRecordType extends AbstractDataRecordType implements SimpleDataRecord {
    
    /**
     * The datablock identifier containing this data record.
     */
    @XmlTransient
    private String blockId;
    
    /**
     * Textual or scalar value List.
     */
    private List<AnyScalarPropertyType> field;
   
    /**
     *  Constructor used by jaxB.
     */
    public SimpleDataRecordType() {}

    public SimpleDataRecordType(final SimpleDataRecord record) {
        super(record);
        if (record != null && record.getField() != null) {
            this.field = new ArrayList<AnyScalarPropertyType>();
            for (AnyScalar a : record.getField()) {
                this.field.add(new AnyScalarPropertyType(a));
            }
        }

    }
    
    /** 
     * Build a new Textual or scalar value List.
     */
    public SimpleDataRecordType(final String blockId, final String id, final String definition, final boolean fixed,
            final Collection<AnyScalarPropertyType> fields) {
        super(id, definition, fixed);
        this.blockId = blockId;
        if (fields != null) {
            this.field = new ArrayList<AnyScalarPropertyType>(fields);
        }
    }

     public SimpleDataRecordType(final List<AnyScalarPropertyType> field) {
        this.field = field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<AnyScalarPropertyType> getField() {
        if (field == null) {
            field = new ArrayList<AnyScalarPropertyType>();
        }
        return field;
    }


    public void addField(final AnyScalarPropertyType field) {
        if (field != null) {
            this.field.add(field);
        }
    }

    public void addOrderedField(final AnyScalarPropertyType field, final int delta) {
        if (field != null) {
            if (this.field.isEmpty()) {
                this.field.add(field);
            } else {
                if (delta > this.field.size()) {
                    throw new IllegalArgumentException("delta must be < field size");
                }
                String newId = field.getIdentifier();
                for (int i = delta; i < this.field.size(); i++) {
                    String currentID = this.field.get(i).getIdentifier();
                    if (newId.compareTo(currentID) < 0) {

                        this.field.add(i, field);
                        return;
                    }
                }
                this.field.add(field);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsField(final String fieldName) {
        if (field != null) {
            for (AnyScalarPropertyType f : field) {
                if (f.getName().equals(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setField(final Collection<AnyScalarPropertyType> field) {
        if (field != null) {
            this.field = new ArrayList<AnyScalarPropertyType>(field);
        } else {
            this.field = null;
        }
    }

    /**
     * Return the block identifier containing this data record.
     */
    public String getBlockId() {
        return blockId;
    }

    /**
     * set the block identifier containing this data record.
     */
    public void setBlockId(final String blockId) {
        this.blockId = blockId;
    }

    /**
     * Verify that the object is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof SimpleDataRecordType && super.equals(object, mode)) {
            final SimpleDataRecordType that = (SimpleDataRecordType) object;
            if (this.getField().size() != that.getField().size())
                return false;
        
            Iterator<AnyScalarPropertyType> i = field.iterator();
            while (i.hasNext()) {
                if (!that.field.contains(i.next()))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int idHash = 7;
        if (getId() != null) {
            idHash = getId().hashCode();
        }
        int idBlockHash = 3;
        if (getBlockId() != null) {
            idBlockHash = this.getBlockId().hashCode();
        }
        return idHash + 37 * idBlockHash;
    }
    
    /**
     * Return a String representation of the objet (debug).
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(super.toString());
        final String lineSeparator = System.getProperty("line.separator", "\n");
        buffer.append(lineSeparator).append("blockId = ").append(blockId).append(lineSeparator);
        appendTo(buffer, "", lineSeparator);
        return buffer.toString();
    }
    
    /**
     * Ajoute la description des composants du dataBlock definition.
     */
    private void appendTo(final StringBuilder buffer, final String margin, final String lineSeparator) {
        int fieldSize = 0;
        if (field != null) {
            fieldSize = field.size();
            buffer.append("nb fields ").append(fieldSize).append(" :").append(lineSeparator);
            int i = 0;
            for (final AnyScalarPropertyType a : getField()) {
                buffer.append(margin).append("field[").append(i).append(']').append(a.toString());
                i++;
            }
        }
    }
    
    
}
