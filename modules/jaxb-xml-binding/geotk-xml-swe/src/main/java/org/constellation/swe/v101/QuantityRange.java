/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008, Geomatys
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

package org.constellation.swe.v101;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="uom" type="{http://www.opengis.net/swe/1.0.1}UomPropertyType" minOccurs="0"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0.1}AllowedValuesPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0.1}QualityPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.opengis.net/swe/1.0.1}decimalPair" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0.1}SimpleComponentAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "uom",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "QuantityRange")
public class QuantityRange extends AbstractDataComponentEntry {

    private UomPropertyType uom;
    private AllowedValuesPropertyType constraint;
    private List<QualityPropertyType> quality;
    @XmlList
    @XmlElement(type = Double.class)
    private List<Double> value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    public QuantityRange() {

    }

    public QuantityRange(UomPropertyType uom, List<Double> value) {
        this.uom   = uom;
        this.value = value;
    }
    
    /**
     * Gets the value of the uom property.
     */
    public UomPropertyType getUom() {
        return uom;
    }

    /**
     * Sets the value of the uom property.
     */
    public void setUom(UomPropertyType value) {
        this.uom = value;
    }

    /**
     * Gets the value of the value property.
     */
    public List<Double> getValue() {
        if (value == null) {
            value = new ArrayList<Double>();
        }
        return this.value;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the axisID property.
     */
    public String getAxisID() {
        return axisID;
    }

    /**
     */
    public void setAxisID(String value) {
        this.axisID = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof QuantityRange && super.equals(object)) {
            final QuantityRange that = (QuantityRange) object;

            return Utilities.equals(this.axisID,           that.axisID)         &&
                   Utilities.equals(this.constraint,       that.constraint)     &&
                   Utilities.equals(this.quality,          that.quality)        &&
                   Utilities.equals(this.referenceFrame,   that.referenceFrame) &&
                   Utilities.equals(this.uom,              that.uom)            &&
                   Utilities.equals(this.value,            that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.uom != null ? this.uom.hashCode() : 0);
        hash = 53 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 53 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 53 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 53 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 53 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        return hash;
    }
}
