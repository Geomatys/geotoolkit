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

package org.geotoolkit.sampling.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureEntry;
import org.geotoolkit.gml.xml.v311.MeasureType;
import org.geotoolkit.gml.xml.v311.ReferenceEntry;
import org.geotoolkit.gml.xml.v311.TimePrimitivePropertyType;


/**
 * Specialized procedure related to surveying positions and locations.
 * 
 * <p>Java class for SurveyProcedureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SurveyProcedureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="operator" type="{http://www.opengis.net/sampling/1.0}AnyOrReferenceType" minOccurs="0"/>
 *         &lt;element name="elevationDatum" type="{http://www.opengis.net/gml}ReferenceType" minOccurs="0"/>
 *         &lt;element name="elevationMethod" type="{http://www.opengis.net/om/1.0}ProcessPropertyType" minOccurs="0"/>
 *         &lt;element name="elevationAccuracy" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="geodeticDatum" type="{http://www.opengis.net/gml}ReferenceType" minOccurs="0"/>
 *         &lt;element name="positionMethod" type="{http://www.opengis.net/om/1.0}ProcessPropertyType"/>
 *         &lt;element name="positionAccuracy" type="{http://www.opengis.net/gml}MeasureType" minOccurs="0"/>
 *         &lt;element name="projection" type="{http://www.opengis.net/gml}ReferenceType" minOccurs="0"/>
 *         &lt;element name="surveyTime" type="{http://www.opengis.net/gml}TimePrimitivePropertyType" minOccurs="0"/>
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
@XmlType(name = "SurveyProcedureType", propOrder = {
    "operator",
    "elevationDatum",
    //"elevationMethod",
    "elevationAccuracy",
    "geodeticDatum",
    //"positionMethod",
    "positionAccuracy",
    "projection",
    "surveyTime"
})
public class SurveyProcedureType extends AbstractFeatureEntry {

    private AnyOrReferenceType operator;
    private ReferenceEntry elevationDatum;
    //private ProcessPropertyType elevationMethod;
    private MeasureType elevationAccuracy;
    private ReferenceEntry geodeticDatum;
    @XmlElement(required = true)
    //private ProcessPropertyType positionMethod;
    private MeasureType positionAccuracy;
    private ReferenceEntry projection;
    private TimePrimitivePropertyType surveyTime;

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link AnyOrReferenceType }
     *     
     */
    public AnyOrReferenceType getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnyOrReferenceType }
     *     
     */
    public void setOperator(AnyOrReferenceType value) {
        this.operator = value;
    }

    /**
     * Gets the value of the elevationDatum property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceEntry }
     *     
     */
    public ReferenceEntry getElevationDatum() {
        return elevationDatum;
    }

    /**
     * Sets the value of the elevationDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceEntry }
     *     
     */
    public void setElevationDatum(ReferenceEntry value) {
        this.elevationDatum = value;
    }

    /**
     * Gets the value of the elevationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessPropertyType }
     *     
    
    public ProcessPropertyType getElevationMethod() {
        return elevationMethod;
    }

    /**
     * Sets the value of the elevationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessPropertyType }
     *     
     
    public void setElevationMethod(ProcessPropertyType value) {
        this.elevationMethod = value;
    }*/

    /**
     * Gets the value of the elevationAccuracy property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getElevationAccuracy() {
        return elevationAccuracy;
    }

    /**
     * Sets the value of the elevationAccuracy property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setElevationAccuracy(MeasureType value) {
        this.elevationAccuracy = value;
    }

    /**
     * Gets the value of the geodeticDatum property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceEntry }
     *     
     */
    public ReferenceEntry getGeodeticDatum() {
        return geodeticDatum;
    }

    /**
     * Sets the value of the geodeticDatum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceEntry }
     *     
     */
    public void setGeodeticDatum(ReferenceEntry value) {
        this.geodeticDatum = value;
    }

    /**
     * Gets the value of the positionMethod property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessPropertyType }
     *     
     
    public ProcessPropertyType getPositionMethod() {
        return positionMethod;
    }

    /**
     * Sets the value of the positionMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessPropertyType }
     *     
     
    public void setPositionMethod(ProcessPropertyType value) {
        this.positionMethod = value;
    }*/

    /**
     * Gets the value of the positionAccuracy property.
     * 
     * @return
     *     possible object is
     *     {@link MeasureType }
     *     
     */
    public MeasureType getPositionAccuracy() {
        return positionAccuracy;
    }

    /**
     * Sets the value of the positionAccuracy property.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasureType }
     *     
     */
    public void setPositionAccuracy(MeasureType value) {
        this.positionAccuracy = value;
    }

    /**
     * Gets the value of the projection property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceEntry }
     *     
     */
    public ReferenceEntry getProjection() {
        return projection;
    }

    /**
     * Sets the value of the projection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceEntry }
     *     
     */
    public void setProjection(ReferenceEntry value) {
        this.projection = value;
    }

    /**
     * Gets the value of the surveyTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimePrimitivePropertyType }
     *     
     */
    public TimePrimitivePropertyType getSurveyTime() {
        return surveyTime;
    }

    /**
     * Sets the value of the surveyTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePrimitivePropertyType }
     *     
     */
    public void setSurveyTime(TimePrimitivePropertyType value) {
        this.surveyTime = value;
    }

}
