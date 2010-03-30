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
package org.geotoolkit.swe.xml.v100;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractDataRecord;


/**
 * <p>Java class for AbstractDataRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDataRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataComponentType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDataRecordType")
@XmlSeeAlso({
    ConditionalDataType.class,
    DataRecordType.class,
    NormalizedCurveType.class,
    SimpleDataRecordType.class,
    AbstractConditionalType.class,
    AbstractVectorType.class
})
public abstract class AbstractDataRecordType extends AbstractDataComponentType implements AbstractDataRecord {

    public AbstractDataRecordType() {

    }

    public AbstractDataRecordType(URI definition) {
        super(definition);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * super-constructor called by sub-classes.
     */
    public AbstractDataRecordType(final AbstractDataRecord record) {
        super(record);
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        return (object instanceof AbstractDataRecordType && super.equals(object));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
