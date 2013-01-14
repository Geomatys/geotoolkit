/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.swes.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractOfferingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractOfferingType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}AbstractSWESType">
 *       &lt;sequence>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="procedureDescriptionFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observableProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="relatedFeature" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swes/2.0}FeatureRelationship"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractOfferingType", propOrder = {
    "procedure",
    "procedureDescriptionFormat",
    "observableProperty",
    "relatedFeature"
})
/*@XmlSeeAlso({
    ObservationOfferingType.class
})*/
public abstract class AbstractOfferingType extends AbstractSWESType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String procedure;
    @XmlSchemaType(name = "anyURI")
    private List<String> procedureDescriptionFormat;
    @XmlSchemaType(name = "anyURI")
    private List<String> observableProperty;
    private List<AbstractOfferingType.RelatedFeature> relatedFeature;

    public AbstractOfferingType() {
        
    }
    
    public AbstractOfferingType(final String id, final String name, final String description, final String procedure,
            final List<String> observableProperty, final List<String> relatedFeature) {
        super(id, name, description);
        this.procedure = procedure;
        this.observableProperty = observableProperty;
    }
    
    /**
     * Gets the value of the procedure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Sets the value of the procedure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedure(String value) {
        this.procedure = value;
    }

    /**
     * Gets the value of the procedureDescriptionFormat property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getProcedureDescriptionFormat() {
        if (procedureDescriptionFormat == null) {
            procedureDescriptionFormat = new ArrayList<String>();
        }
        return this.procedureDescriptionFormat;
    }

    /**
     * Gets the value of the observableProperty property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     */
    public List<String> getObservableProperty() {
        if (observableProperty == null) {
            observableProperty = new ArrayList<String>();
        }
        return this.observableProperty;
    }

    /**
     * Gets the value of the relatedFeature property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link AbstractOfferingType.RelatedFeature }
     * 
     */
    public List<AbstractOfferingType.RelatedFeature> getRelatedFeature() {
        if (relatedFeature == null) {
            relatedFeature = new ArrayList<AbstractOfferingType.RelatedFeature>();
        }
        return this.relatedFeature;
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
     *         &lt;element ref="{http://www.opengis.net/swes/2.0}FeatureRelationship"/>
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
        "featureRelationship"
    })
    public static class RelatedFeature {

        @XmlElement(name = "FeatureRelationship", required = true)
        private FeatureRelationshipType featureRelationship;

        /**
         * Gets the value of the featureRelationship property.
         * 
         * @return
         *     possible object is
         *     {@link FeatureRelationshipType }
         *     
         */
        public FeatureRelationshipType getFeatureRelationship() {
            return featureRelationship;
        }

        /**
         * Sets the value of the featureRelationship property.
         * 
         * @param value
         *     allowed object is
         *     {@link FeatureRelationshipType }
         *     
         */
        public void setFeatureRelationship(FeatureRelationshipType value) {
            this.featureRelationship = value;
        }

    }

}
