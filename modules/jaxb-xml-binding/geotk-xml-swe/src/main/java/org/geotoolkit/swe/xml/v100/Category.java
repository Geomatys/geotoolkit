/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.swe.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataComponentType">
 *       &lt;sequence>
 *         &lt;element name="codeSpace" type="{http://www.opengis.net/swe/1.0}CodeSpacePropertyType" minOccurs="0"/>
 *         &lt;element name="constraint" type="{http://www.opengis.net/swe/1.0}AllowedTokensPropertyType" minOccurs="0"/>
 *         &lt;element name="quality" type="{http://www.opengis.net/swe/1.0}QualityPropertyType" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}token" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/swe/1.0}SimpleComponentAttributeGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "codeSpace",
    "constraint",
    "quality",
    "value"
})
@XmlRootElement(name = "Category")
public class Category extends AbstractDataComponentType {

    private CodeSpacePropertyType codeSpace;
    private AllowedTokensPropertyType constraint;
    private QualityPropertyType quality;
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String value;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private String axisID;

    public Category() {

    }

    public Category(String definition, String value) {
        super(definition);
        this.value = value;
    }
    
    /**
     * Gets the value of the codeSpace property.
     */
    public CodeSpacePropertyType getCodeSpace() {
        return codeSpace;
    }

    /**
     * Sets the value of the codeSpace property.
     */
    public void setCodeSpace(CodeSpacePropertyType value) {
        this.codeSpace = value;
    }

    /**
     * Gets the value of the constraint property.
     */
    public AllowedTokensPropertyType getConstraint() {
        return constraint;
    }

    /**
     * Sets the value of the constraint property.
     */
    public void setConstraint(AllowedTokensPropertyType value) {
        this.constraint = value;
    }

    /**
     * Gets the value of the quality property.
     */
    public QualityPropertyType getQuality() {
        return quality;
    }

    /**
     * Sets the value of the quality property.
     */
    public void setQuality(QualityPropertyType value) {
        this.quality = value;
    }

    /**
     * Gets the value of the value property.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     */
    public void setValue(String value) {
        this.value = value;
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
     * Sets the value of the axisID property.
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
        if (object instanceof Category) {
            final Category that = (Category) object;

            return Utilities.equals(this.axisID, that.axisID)                 &&
                   Utilities.equals(this.codeSpace, that.codeSpace)           &&
                   Utilities.equals(this.constraint, that.constraint)         &&
                   Utilities.equals(this.quality, that.quality)               &&
                   Utilities.equals(this.referenceFrame, that.referenceFrame) &&
                   Utilities.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.codeSpace != null ? this.codeSpace.hashCode() : 0);
        hash = 89 * hash + (this.constraint != null ? this.constraint.hashCode() : 0);
        hash = 89 * hash + (this.quality != null ? this.quality.hashCode() : 0);
        hash = 89 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 89 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 89 * hash + (this.axisID != null ? this.axisID.hashCode() : 0);
        return hash;
    }

}
