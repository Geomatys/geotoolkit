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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;


/**
 * Denotes the relation of a _CityObject resp. CityModel to its appearances. The AppearancePropertyType
 *                 element must either carry a reference to a Appearance object or contain a Appearance object inline, but neither
 *                 both nor none.
 * 
 * <p>Java class for AppearancePropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AppearancePropertyType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}FeaturePropertyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="Appearance" type="{http://www.opengis.net/citygml/appearance/1.0}AppearanceType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppearancePropertyType", propOrder = {
    "appearance"
})
public class AppearancePropertyType extends FeaturePropertyType {


    @XmlElement(name = "Appearance")
    private AppearanceType appearance;

    /**
     * Gets the value of the appearance property.
     * 
     * @return
     *     possible object is
     *     {@link AppearanceType }
     *     
     */
    public AppearanceType getAppearance() {
        return appearance;
    }

    /**
     * Sets the value of the appearance property.
     * 
     * @param value
     *     allowed object is
     *     {@link AppearanceType }
     *     
     */
    public void setAppearance(AppearanceType value) {
        this.appearance = value;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (appearance != null) {
            s.append("appearance:").append(appearance).append('\n');
        }
        return s.toString();
    }
}
