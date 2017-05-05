/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wfs.xml.IdentifierGenerationOptionType;
import org.geotoolkit.wfs.xml.InsertElement;


/**
 * <p>Java class for InsertType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/wfs/2.0}AbstractTransactionActionType">
 *       &lt;sequence>
 *         &lt;any namespace='##other' maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/wfs/2.0}StandardInputParameters"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertType", propOrder = {
    "any"
})
public class InsertType extends AbstractTransactionActionType implements InsertElement {

    @XmlAnyElement(lax = true)
    private List<Object> any;
    @XmlAttribute
    private String inputFormat;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    public InsertType() {

    }

    public InsertType(String inputFormat, String srsName, Object any) {
        this.inputFormat = inputFormat;
        this.srsName = srsName;
        if (any != null) {
            this.any = Arrays.asList(any);
        }
    }
    /**
     * Gets the value of the any property.
     *
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<>();
        }
        return this.any;
    }

    @Override
    public List getFeature() {
        return getAny();
    }

    /**
     * Gets the value of the inputFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getInputFormat() {
        if (inputFormat == null) {
            return "application/gml+xml; version=3.2";
        } else {
            return inputFormat;
        }
    }

    /**
     * Sets the value of the inputFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInputFormat(String value) {
        this.inputFormat = value;
    }

    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSrsName(String value) {
        this.srsName = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[InsertType]\n");
        if (inputFormat != null) {
            sb.append("inputFormat").append(inputFormat).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName").append(srsName).append('\n');
        }
        if (any != null) {
            sb.append("feature:\n");
            for (Object q : any) {
                sb.append(q).append("\nclass:").append(q.getClass().getName());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof InsertType) {
            InsertType that = (InsertType) obj;
            return Objects.equals(this.any, that.any) &&
                   Objects.equals(this.inputFormat, that.inputFormat) &&
                   Objects.equals(this.srsName, that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.any != null ? this.any.hashCode() : 0);
        hash = 17 * hash + (this.inputFormat != null ? this.inputFormat.hashCode() : 0);
        hash = 17 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }

    public IdentifierGenerationOptionType getIdgen() {
        return null; // not implemented in 2.0.0
    }
}
