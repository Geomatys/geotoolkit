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
 * <p>Java class for GNC_Training_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_Training_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mdweb-project.org/files/xsd}GNC_Product_Type">
 *       &lt;sequence>
 *         &lt;element name="duration" type="{http://www.mdweb-project.org/files/xsd}GNC_TrainingDurationCode_PropertyType"/>
 *         &lt;element name="typeOfTraining" type="{http://www.mdweb-project.org/files/xsd}GNC_TrainingTypeCode_PropertyType"/>
 *         &lt;element name="trainingOn" type="{http://www.mdweb-project.org/files/xsd}GNC_Software_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GNC_Training_Type", propOrder = {
    "duration",
    "typeOfTraining",
    "trainingOn"
})
@XmlRootElement(name = "GNC_Training", namespace = "http://www.mdweb-project.org/files/xsd")
public class GNC_Training extends GNC_Product implements org.opengis.metadata.geonetcab.GNC_Training {

    @XmlElement(required = true)
    private GNC_TrainingDurationCode duration;
    @XmlElement(required = true)
    private GNC_TrainingTypeCode typeOfTraining;
    private List<GNC_Software> trainingOn;

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link GNCTrainingDurationCodePropertyType }
     *     
     */
    public GNC_TrainingDurationCode getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCTrainingDurationCodePropertyType }
     *     
     */
    public void setDuration(final GNC_TrainingDurationCode value) {
        this.duration = value;
    }

    /**
     * Gets the value of the typeOfTraining property.
     * 
     * @return
     *     possible object is
     *     {@link GNCTrainingTypeCodePropertyType }
     *     
     */
    public GNC_TrainingTypeCode getTypeOfTraining() {
        return typeOfTraining;
    }

    /**
     * Sets the value of the typeOfTraining property.
     * 
     * @param value
     *     allowed object is
     *     {@link GNCTrainingTypeCodePropertyType }
     *     
     */
    public void setTypeOfTraining(final GNC_TrainingTypeCode value) {
        this.typeOfTraining = value;
    }

    /**
     * Gets the value of the trainingOn property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link GNCSoftwarePropertyType }
     * 
     * 
     */
    @Override
    public List<GNC_Software> getTrainingOn() {
        if (trainingOn == null) {
            trainingOn = new ArrayList<GNC_Software>();
        }
        return this.trainingOn;
    }

    public void setTrainingOn(List<GNC_Software> trainingOn) {
        this.trainingOn = trainingOn;
    }

    public void setTrainingOn(GNC_Software trainingOn) {
        if (this.trainingOn == null) {
            this.trainingOn = new ArrayList<GNC_Software>();
        }
        this.trainingOn.add(trainingOn);
    }

}
