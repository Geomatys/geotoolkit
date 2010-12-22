/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

package org.geotoolkit.geotnetcab;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GNC_Service_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Service_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_MaterialResource_Type">
 *       &lt;sequence>
 *         &lt;element name="typeOfService" type="{http://www.mdweb-project.org/files/xsd}GNC_ServicesTypeCode_PropertyType" maxOccurs="unbounded"/>
 *         &lt;element name="isBasedOn" type="{http://www.mdweb-project.org/files/xsd}GNC_Product_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Service_Type", propOrder = {
    "typeOfService",
    "isBasedOn"
})
@XmlRootElement(name = "GNC_Service", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Service extends GNC_MaterialResource implements org.opengis.metadata.geonetcab.GNC_Service {

    @XmlElement(required = true)
    private List<GNC_ServicesTypeCode> typeOfService;
    private List<GNC_Product> isBasedOn;

    /**
     * Gets the value of the typeOfService property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCServicesTypeCodePropertyType }
     * 
     * 
     */
    public List<GNC_ServicesTypeCode> getTypeOfService() {
        if (typeOfService == null) {
            typeOfService = new ArrayList<GNC_ServicesTypeCode>();
        }
        return this.typeOfService;
    }

    /**
     * Gets the value of the isBasedOn property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCProductPropertyType }
     * 
     * 
     */
    @Override
    public List<GNC_Product> getIsBasedOn() {
        if (isBasedOn == null) {
            isBasedOn = new ArrayList<GNC_Product>();
        }
        return this.isBasedOn;
    }

}
