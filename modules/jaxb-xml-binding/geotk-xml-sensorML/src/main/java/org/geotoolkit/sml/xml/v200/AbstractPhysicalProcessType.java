/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.sml.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.swe.xml.v200.TimePropertyType;


/**
 * <p>Java class for AbstractPhysicalProcessType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractPhysicalProcessType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorml/2.0}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;element name="attachedTo" type="{http://www.opengis.net/gml/3.2}ReferenceType" minOccurs="0"/>
 *         &lt;element name="localReferenceFrame" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}SpatialFrame"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="localTimeFrame" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/sensorml/2.0}TemporalFrame"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="position" type="{http://www.opengis.net/sensorml/2.0}PositionUnionPropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="timePosition" type="{http://www.opengis.net/swe/2.0}TimePropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractPhysicalProcessType", propOrder = {
    "attachedTo",
    "localReferenceFrame",
    "localTimeFrame",
    "position",
    "timePosition"
})
@XmlSeeAlso({
    PhysicalComponentType.class,
    PhysicalSystemType.class
})
public abstract class AbstractPhysicalProcessType
    extends AbstractProcessType
{

    protected ReferenceType attachedTo;
    protected List<AbstractPhysicalProcessType.LocalReferenceFrame> localReferenceFrame;
    protected List<AbstractPhysicalProcessType.LocalTimeFrame> localTimeFrame;
    protected List<PositionUnionPropertyType> position;
    protected List<TimePropertyType> timePosition;

    /**
     * Gets the value of the attachedTo property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getAttachedTo() {
        return attachedTo;
    }

    /**
     * Sets the value of the attachedTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setAttachedTo(ReferenceType value) {
        this.attachedTo = value;
    }

    /**
     * Gets the value of the localReferenceFrame property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localReferenceFrame property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalReferenceFrame().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractPhysicalProcessType.LocalReferenceFrame }
     * 
     * 
     */
    public List<AbstractPhysicalProcessType.LocalReferenceFrame> getLocalReferenceFrame() {
        if (localReferenceFrame == null) {
            localReferenceFrame = new ArrayList<AbstractPhysicalProcessType.LocalReferenceFrame>();
        }
        return this.localReferenceFrame;
    }

    /**
     * Gets the value of the localTimeFrame property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localTimeFrame property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalTimeFrame().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractPhysicalProcessType.LocalTimeFrame }
     * 
     * 
     */
    public List<AbstractPhysicalProcessType.LocalTimeFrame> getLocalTimeFrame() {
        if (localTimeFrame == null) {
            localTimeFrame = new ArrayList<AbstractPhysicalProcessType.LocalTimeFrame>();
        }
        return this.localTimeFrame;
    }

    /**
     * Gets the value of the position property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the position property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PositionUnionPropertyType }
     * 
     * 
     */
    public List<PositionUnionPropertyType> getPosition() {
        if (position == null) {
            position = new ArrayList<PositionUnionPropertyType>();
        }
        return this.position;
    }

    /**
     * Gets the value of the timePosition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timePosition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimePosition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TimePropertyType }
     * 
     * 
     */
    public List<TimePropertyType> getTimePosition() {
        if (timePosition == null) {
            timePosition = new ArrayList<TimePropertyType>();
        }
        return this.timePosition;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}SpatialFrame"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "spatialFrame"
    })
    public static class LocalReferenceFrame {

        @XmlElement(name = "SpatialFrame", required = true)
        protected SpatialFrameType spatialFrame;

        /**
         * Gets the value of the spatialFrame property.
         * 
         * @return
         *     possible object is
         *     {@link SpatialFrameType }
         *     
         */
        public SpatialFrameType getSpatialFrame() {
            return spatialFrame;
        }

        /**
         * Sets the value of the spatialFrame property.
         * 
         * @param value
         *     allowed object is
         *     {@link SpatialFrameType }
         *     
         */
        public void setSpatialFrame(SpatialFrameType value) {
            this.spatialFrame = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element ref="{http://www.opengis.net/sensorml/2.0}TemporalFrame"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "temporalFrame"
    })
    public static class LocalTimeFrame {

        @XmlElement(name = "TemporalFrame", required = true)
        protected TemporalFrameType temporalFrame;

        /**
         * Gets the value of the temporalFrame property.
         * 
         * @return
         *     possible object is
         *     {@link TemporalFrameType }
         *     
         */
        public TemporalFrameType getTemporalFrame() {
            return temporalFrame;
        }

        /**
         * Sets the value of the temporalFrame property.
         * 
         * @param value
         *     allowed object is
         *     {@link TemporalFrameType }
         *     
         */
        public void setTemporalFrame(TemporalFrameType value) {
            this.temporalFrame = value;
        }

    }

}
