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
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;


/**
 *  Named container for all surface data (texture/material). All appearances of the same name ("theme")
 *                 within a CityGML file are considered a group. 
 * 
 * <p>Java class for AppearanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AppearanceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="theme" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surfaceDataMember" type="{http://www.opengis.net/citygml/appearance/1.0}SurfaceDataPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/citygml/appearance/1.0}_GenericApplicationPropertyOfAppearance" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppearanceType", propOrder = {
    "theme",
    "surfaceDataMember",
    "genericApplicationPropertyOfAppearance"
})
public class AppearanceType extends AbstractFeatureType {

    private String theme;
    private List<SurfaceDataPropertyType> surfaceDataMember;
    @XmlElement(name = "_GenericApplicationPropertyOfAppearance")
    private List<Object> genericApplicationPropertyOfAppearance;

    /**
     * Gets the value of the theme property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Sets the value of the theme property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheme(String value) {
        this.theme = value;
    }

    /**
     * Gets the value of the surfaceDataMember property.
     * 
     */
    public List<SurfaceDataPropertyType> getSurfaceDataMember() {
        if (surfaceDataMember == null) {
            surfaceDataMember = new ArrayList<SurfaceDataPropertyType>();
        }
        return this.surfaceDataMember;
    }

    /**
     * Gets the value of the genericApplicationPropertyOfAppearance property.
     * 
     */
    public List<Object> getGenericApplicationPropertyOfAppearance() {
        if (genericApplicationPropertyOfAppearance == null) {
            genericApplicationPropertyOfAppearance = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfAppearance;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        if (theme != null) {
            s.append("theme:").append(theme).append('\n');
        }
        if (surfaceDataMember != null && surfaceDataMember.size() > 0) {
            s.append("surfaceDataMember:").append('\n');
            for (SurfaceDataPropertyType fp : surfaceDataMember) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfAppearance != null && genericApplicationPropertyOfAppearance.size() > 0) {
            s.append("genericApplicationPropertyOfAppearance:").append('\n');
            for (Object fp : genericApplicationPropertyOfAppearance) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
