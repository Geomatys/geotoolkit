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
package org.geotoolkit.citygml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureCollectionType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;


/**
 * Type describing the "root" element of any city model file.
 * It is a collection whose members are restricted to be features of a city model.
 * All features are included as cityObjectMember.
 * 
 * <p>Java class for CityModelType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CityModelType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureCollectionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/citygml/1.0}_GenericApplicationPropertyOfCityModel" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CityModelType", propOrder = {
    "cityObjectMember",
    "genericApplicationPropertyOfCityModel"
})
@XmlRootElement(name = "CityModel")
public class CityModelType extends AbstractFeatureCollectionType {

    @XmlElement(name = "_GenericApplicationPropertyOfCityModel")
    private List<Object> genericApplicationPropertyOfCityModel;
    
    private List<FeaturePropertyType> cityObjectMember;
    
    /**
     * Gets the value of the genericApplicationPropertyOfCityModel property.
     */
    public List<Object> getGenericApplicationPropertyOfCityModel() {
        if (genericApplicationPropertyOfCityModel == null) {
            genericApplicationPropertyOfCityModel = new ArrayList<Object>();
        }
        return this.genericApplicationPropertyOfCityModel;
    }
    
    /**
     * @return the cityObjectMember
     */
    public List<FeaturePropertyType> getCityObjectMember() {
        if (cityObjectMember == null) {
            cityObjectMember = new ArrayList<FeaturePropertyType>();
        }
        return cityObjectMember;
    }

    /**
     * @param cityObjectMember the cityObjectMember to set
     */
    public void setCityObjectMember(List<FeaturePropertyType> cityObjectMember) {
        this.cityObjectMember = cityObjectMember;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (cityObjectMember != null && cityObjectMember.size() > 0) {
            s.append("cityObjectMember:").append('\n');
            for (FeaturePropertyType fp : cityObjectMember) {
                s.append(fp).append('\n');
            }
        }
        if (genericApplicationPropertyOfCityModel != null && genericApplicationPropertyOfCityModel.size() > 0) {
            s.append("genericApplicationPropertyOfCityModel:").append('\n');
            for (Object fp : genericApplicationPropertyOfCityModel) {
                s.append(fp).append('\n');
            }
        }
        return s.toString();
    }
}
