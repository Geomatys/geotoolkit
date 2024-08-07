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
package org.geotoolkit.ows.xml.v110;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * An Exception element describes one detected error that a server chooses to convey to the client.
 *
 * <p>Java class for ExceptionType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExceptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExceptionText" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="exceptionCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="locator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *  @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExceptionType", propOrder = {
    "exceptionText"
})
public class ExceptionType implements Serializable, org.geotoolkit.ows.xml.ExceptionType {

    @XmlElement(name = "ExceptionText")
    private List<String> exceptionText;
    @XmlAttribute(required = true)
    private String exceptionCode;
    @XmlAttribute
    private String locator;

    /**
     * Empty constructor used by JAXB.
     */
    public ExceptionType() {}

    /**
     *  build a new Exception code.
     *
     * @param exceptionText
     * @param exceptionCode
     */
    public ExceptionType(final String exceptionText, final String exceptionCode, final String locator) {
        this.exceptionText = new ArrayList<>();
        this.exceptionText.add(exceptionText);
        this.exceptionCode = exceptionCode;
        this.locator       = locator;
    }

    /**
     * Gets the value of the exceptionText property.
     */
    @Override
    public List<String> getExceptionText() {
        return Collections.unmodifiableList(exceptionText);
    }

    /**
     * Gets the value of the exceptionCode property.
     */
    @Override
    public String getExceptionCode() {
        return exceptionCode;
    }

    /**
     * Gets the value of the locator property.
     */
    public String getLocator() {
        return locator;
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ExceptionType) {
            final ExceptionType that = (ExceptionType) object;

            return Objects.equals(this.exceptionCode, that.exceptionCode)    &&
                   Objects.equals(this.exceptionText, that.exceptionText)  &&
                   Objects.equals(this.locator,       that.locator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.exceptionText != null ? this.exceptionText.hashCode() : 0);
        hash = 37 * hash + (this.exceptionCode != null ? this.exceptionCode.hashCode() : 0);
        hash = 37 * hash + (this.locator != null ? this.locator.hashCode() : 0);
        return hash;
    }

     /**
     * Return a String representation of the exception.
     *
     * @return A String representation of the exception.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[ExceptionType]:").append('\n');
        s.append("code: ").append(exceptionCode).append('\n');
        s.append("locator: ").append(locator).append('\n');
        if (exceptionText != null) {
            int i = 0;
            for (String ex: exceptionText) {
                s.append(i).append(": Text: ").append(ex).append('\n');
                i++;
            }
        }
        return s.toString();
    }
}
