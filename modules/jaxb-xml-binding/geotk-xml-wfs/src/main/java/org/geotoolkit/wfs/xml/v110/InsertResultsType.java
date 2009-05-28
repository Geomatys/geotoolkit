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
package org.geotoolkit.wfs.xml.v110;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Reports the list of identifiers of all features created by a transaction request.
 * New features are created using the Insert action and the list of idetifiers must be
 * presented in the same order as the Insert actions were encountered in the transaction request.
 * Features may optionally be correlated with identifiers using the handle attribute (if it was specified on the Insert element).
 *          
 * 
 * <p>Java class for InsertResultsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="InsertResultsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Feature" type="{http://www.opengis.net/wfs}InsertedFeatureType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InsertResultsType", propOrder = {
    "feature"
})
public class InsertResultsType {

    @XmlElement(name = "Feature", required = true)
    private List<InsertedFeatureType> feature;

    /**
     * Gets the value of the feature property.
     */
    public List<InsertedFeatureType> getFeature() {
        if (feature == null) {
            feature = new ArrayList<InsertedFeatureType>();
        }
        return this.feature;
    }

}
