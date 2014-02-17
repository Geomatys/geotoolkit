/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.citygml.xml.v100.appearance;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;


/**
 * Base class for textures and material. Contains only isFront-flag.
 * 
 * <p>Java class for AbstractSurfaceDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractSurfaceDataType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="isFront" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfSurfaceData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSurfaceDataType", propOrder = {
    "isFront",
    "genericApplicationPropertyOfSurfaceData"
})
@XmlSeeAlso({
    X3DMaterialType.class,
    AbstractTextureType.class
})
public class AbstractSurfaceDataType extends AbstractFeatureType {

    @XmlElement(defaultValue = "true")
    private Boolean isFront;
    @XmlElement(name = "_GenericApplicationPropertyOfSurfaceData")
    private List<Object> genericApplicationPropertyOfSurfaceData;

    /**
     * Gets the value of the isFront property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsFront() {
        return isFront;
    }

    /**
     * Sets the value of the isFront property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsFront(Boolean value) {
        this.isFront = value;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfSurfaceData property.
     */
    public List<Object> getGenericApplicationPropertyOfSurfaceData() {
        if (genericApplicationPropertyOfSurfaceData == null) {
            genericApplicationPropertyOfSurfaceData = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfSurfaceData;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (isFront != null) {
            s.append("isFront:").append(isFront).append('\n');
        }
        if (genericApplicationPropertyOfSurfaceData != null && genericApplicationPropertyOfSurfaceData.size() > 0) {
            s.append("genericApplicationPropertyOfSurfaceData:").append('\n');
            for (Object fp : genericApplicationPropertyOfSurfaceData) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }

}
