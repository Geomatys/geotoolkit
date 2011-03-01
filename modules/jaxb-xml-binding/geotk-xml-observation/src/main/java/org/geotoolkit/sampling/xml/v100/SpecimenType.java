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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.CodeType;
import org.geotoolkit.gml.xml.v311.LocationPropertyType;
import org.geotoolkit.gml.xml.v311.MeasureType;
import org.geotoolkit.gml.xml.v311.ReferenceType;
import org.geotoolkit.gml.xml.v311.TimePrimitivePropertyType;


/**
 * An object on which measurements may be made, often ex-situ.
 * Note that if this specimen is a "processed" version of another (e.g. by grinding, sieving, etc) then 
 * the predecessor (if known) may be recorded as a relatedSamplingFeature
 * 
 * <p>Java class for SpecimenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpecimenType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sampling/1.0}SamplingFeatureType">
 *       &lt;sequence>
 *         &lt;element name="materialClass" type="{http://www.opengis.net/gml}CodeType"/>
 *         &lt;element name="currentLocation" type="{http://www.opengis.net/sampling/1.0}LocationPropertyType" minOccurs="0"/>
 *         &lt;element name="size" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.opengis.net/gml>MeasureType">
 *                 &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="samplingMethod" type="{http://www.opengis.net/gml}ReferenceType" minOccurs="0"/>
 *         &lt;element name="samplingTime" type="{http://www.opengis.net/gml}TimePrimitivePropertyType"/>
 *         &lt;element name="processingDetails" type="{http://www.opengis.net/gml}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SpecimenType", propOrder = {
    "materialClass",
    "currentLocation",
    "size",
    "samplingMethod",
    "samplingTime",
    "processingDetails"
})
@XmlSeeAlso({
    LocatedSpecimenType.class
})
public class SpecimenType extends SamplingFeatureType {

    @XmlElement(required = true)
    private CodeType materialClass;
    private LocationPropertyType currentLocation;
    private SpecimenType.Size size;
    private ReferenceType samplingMethod;
    @XmlElement(required = true)
    private TimePrimitivePropertyType samplingTime;
    private List<ReferenceType> processingDetails;

    /**
     * Gets the value of the materialClass property.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getMaterialClass() {
        return materialClass;
    }

    /**
     * Sets the value of the materialClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setMaterialClass(final CodeType value) {
        this.materialClass = value;
    }

    /**
     * Gets the value of the currentLocation property.
     * 
     * @return
     *     possible object is
     *     {@link LocationPropertyType }
     *     
     */
    public LocationPropertyType getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Sets the value of the currentLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationPropertyType }
     *     
     */
    public void setCurrentLocation(final LocationPropertyType value) {
        this.currentLocation = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link SpecimenType.Size }
     *     
     */
    public SpecimenType.Size getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecimenType.Size }
     *     
     */
    public void setSize(final SpecimenType.Size value) {
        this.size = value;
    }

    /**
     * Gets the value of the samplingMethod property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getSamplingMethod() {
        return samplingMethod;
    }

    /**
     * Sets the value of the samplingMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setSamplingMethod(final ReferenceType value) {
        this.samplingMethod = value;
    }

    /**
     * Gets the value of the samplingTime property.
     * 
     * @return
     *     possible object is
     *     {@link TimePrimitivePropertyType }
     *     
     */
    public TimePrimitivePropertyType getSamplingTime() {
        return samplingTime;
    }

    /**
     * Sets the value of the samplingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimePrimitivePropertyType }
     *     
     */
    public void setSamplingTime(final TimePrimitivePropertyType value) {
        this.samplingTime = value;
    }

    /**
     * Gets the value of the processingDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the processingDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcessingDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceType }
     * 
     * 
     */
    public List<ReferenceType> getProcessingDetails() {
        if (processingDetails == null) {
            processingDetails = new ArrayList<ReferenceType>();
        }
        return this.processingDetails;
    }


    /**
     * Xlink attributes added to enable rich annotation, e.g. size-type
     * 
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.opengis.net/gml>MeasureType">
     *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Size extends MeasureType {

        @XmlAttribute(namespace = "http://www.opengis.net/gml")
        @XmlSchemaType(name = "anyURI")
        private String remoteSchema;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String type;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String href;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String role;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        @XmlSchemaType(name = "anyURI")
        private String arcrole;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String title;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String show;
        @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
        private String actuate;

        /**
         * Gets the value of the remoteSchema property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRemoteSchema() {
            return remoteSchema;
        }

        /**
         * Sets the value of the remoteSchema property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRemoteSchema(final String value) {
            this.remoteSchema = value;
        }

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            if (type == null) {
                return "simple";
            } else {
                return type;
            }
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(final String value) {
            this.type = value;
        }

        /**
         * Gets the value of the href property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHref() {
            return href;
        }

        /**
         * Sets the value of the href property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHref(final String value) {
            this.href = value;
        }

        /**
         * Gets the value of the role property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRole() {
            return role;
        }

        /**
         * Sets the value of the role property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRole(final String value) {
            this.role = value;
        }

        /**
         * Gets the value of the arcrole property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getArcrole() {
            return arcrole;
        }

        /**
         * Sets the value of the arcrole property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setArcrole(final String value) {
            this.arcrole = value;
        }

        /**
         * Gets the value of the title property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTitle() {
            return title;
        }

        /**
         * Sets the value of the title property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTitle(final String value) {
            this.title = value;
        }

        /**
         * Gets the value of the show property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getShow() {
            return show;
        }

        /**
         * Sets the value of the show property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setShow(final String value) {
            this.show = value;
        }

        /**
         * Gets the value of the actuate property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getActuate() {
            return actuate;
        }

        /**
         * Sets the value of the actuate property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setActuate(final String value) {
            this.actuate = value;
        }

    }

}
