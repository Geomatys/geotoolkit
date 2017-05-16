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
package org.geotoolkit.csw.xml.v202;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.InsertResult;


/**
 *
 *             Returns a "brief" view of any newly created catalogue records.
 *             The handle attribute may reference a particular statement in
 *             the corresponding transaction request.
 *
 *
 * <p>Java class for InsertResultType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertResultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}BriefRecord" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="handleRef" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertResultType", propOrder = {
    "briefRecord"
})
public class InsertResultType implements InsertResult {

    @XmlAnyElement(lax = true)
    private List<Object> briefRecord;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String handleRef;

    public InsertResultType() {

    }

    /**
     * build a new Inserted result record view.
     * briefRecord partameter is set to Object for JAXBContext purpose but it must be a BriefRecordType List
     * (or Node List containing brief records) in order to pass XML validation
     *
     * @param briefRecord must be a BriefRecordTypeList
     * @param handleRef
     */
    public InsertResultType(final List<Object> briefRecord, final String handleRef) {
        this.briefRecord = briefRecord;
        this.handleRef   = handleRef;
    }

    /**
     * Gets the value of the briefRecord property.
     */
    public List<Object> getBriefRecord() {
        if (briefRecord == null) {
            briefRecord = new ArrayList<>();
        }
        return briefRecord;
    }

    /**
     * Gets the value of the handleRef property.
     */
    public String getHandleRef() {
        return handleRef;
    }

    /**
     * Gets the value of the handleRef property.
     */
    public void setHandleRef(final String handleRef) {
        this.handleRef = handleRef;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof InsertResultType) {
            final InsertResultType that = (InsertResultType) object;
            return Objects.equals(this.briefRecord, that.briefRecord) &&
                   Objects.equals(this.handleRef,   that.handleRef);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.briefRecord != null ? this.briefRecord.hashCode() : 0);
        hash = 11 * hash + (this.handleRef != null ? this.handleRef.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[InsertResultType]\n");

        if (briefRecord != null) {
            s.append(briefRecord.size()).append(" briefRecord: ").append('\n');
            for (Object ins : briefRecord) {
                s.append(ins).append('\n');
            }
        }
        if (handleRef != null) {
            s.append("handleRef: ").append(handleRef).append('\n');
        }
        return s.toString();
    }
}
