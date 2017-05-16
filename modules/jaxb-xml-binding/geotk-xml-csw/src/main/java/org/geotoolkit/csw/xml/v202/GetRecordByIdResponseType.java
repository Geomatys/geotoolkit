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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.GetRecordByIdResponse;

/**
 * Returns a representation of the matching entry. If there is no
 *          matching record, the response message must be empty.
 *
 * <p>Java class for GetRecordByIdResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GetRecordByIdResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/cat/csw/2.0.2}AbstractRecord" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;any/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordByIdResponseType", propOrder = {
    "any"
})
@XmlRootElement(name = "GetRecordByIdResponse")
public class GetRecordByIdResponseType implements GetRecordByIdResponse {

    @XmlAnyElement(lax = true)
    private List<? extends Object> any;

    /**
     * An empty constructor used by JAXB
     */
    GetRecordByIdResponseType() {
    }

    /**
     * Build a new response to a getRecordById request.
     * one of the two list mustn't be null
     */
    public GetRecordByIdResponseType(final List<? extends Object> others) {
        this.any = others;
    }

    /**
     * Gets the value of the any property.
     * (unmodifiable)
     */
    @Override
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return Collections.unmodifiableList(any);
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();

        if (any != null && !any.isEmpty()) {
            s.append("records:").append('\n');
            for (Object obj : any) {
                s.append(obj.toString()).append('\n');
            }
        }

        return s.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetRecordByIdResponseType) {
            final GetRecordByIdResponseType that = (GetRecordByIdResponseType) object;
            return Objects.equals(this.any,            that.any);
        }
        return false;

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.any != null ? this.any.hashCode() : 0);
        return hash;
    }
}
