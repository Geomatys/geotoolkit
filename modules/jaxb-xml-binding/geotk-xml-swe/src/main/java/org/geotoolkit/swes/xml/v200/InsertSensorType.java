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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.Phenomenon;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.geotoolkit.swes.xml.InsertSensor;
import org.geotoolkit.swes.xml.ObservationTemplate;
import org.opengis.observation.Observation;


/**
 * <p>Java class for InsertSensorType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InsertSensorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swes/2.0}ExtensibleRequestType">
 *       &lt;sequence>
 *         &lt;element name="procedureDescriptionFormat" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="procedureDescription">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="observableProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
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
 *         &lt;element name="metadata" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/swes/2.0}InsertionMetadata"/>
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
@XmlType(name = "InsertSensorType", propOrder = {
    "procedureDescriptionFormat",
    "procedureDescription",
    "observableProperty",
    "relatedFeature",
    "metadata"
})
public class InsertSensorType extends ExtensibleRequestType implements InsertSensor {

    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private String procedureDescriptionFormat;
    @XmlElement(required = true)
    private ProcedureDescription procedureDescription;
    @XmlElement(required = true)
    @XmlSchemaType(name = "anyURI")
    private List<String> observableProperty;
    private List<InsertSensorType.RelatedFeature> relatedFeature;
    private List<InsertSensorType.Metadata> metadata;

    public InsertSensorType() {

    }

    public InsertSensorType(final String version, final Object sensorDescription, final String procedureDescriptionFormat) {
        super(version, "SOS");
        if (sensorDescription != null) {
            this.procedureDescription = new ProcedureDescription(sensorDescription);
        }
        this.procedureDescriptionFormat = procedureDescriptionFormat;
    }

    /**
     * Gets the value of the procedureDescriptionFormat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getProcedureDescriptionFormat() {
        return procedureDescriptionFormat;
    }

    /**
     * Sets the value of the procedureDescriptionFormat property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcedureDescriptionFormat(String value) {
        this.procedureDescriptionFormat = value;
    }

    /**
     * Gets the value of the procedureDescription property.
     *
     * @return
     *     possible object is
     *     {@link InsertSensorType.ProcedureDescription }
     *
     */
    public ProcedureDescription getProcedureDescription() {
        return procedureDescription;
    }

    @Override
    public Object getSensorDescription() {
        if (procedureDescription != null) {
            return procedureDescription.getAny();
        }
        return null;
    }

    /**
     * Sets the value of the procedureDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link InsertSensorType.ProcedureDescription }
     *
     */
    public void setProcedureDescription(ProcedureDescription value) {
        this.procedureDescription = value;
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
     * {@link InsertSensorType.RelatedFeature }
     *
     */
    public List<InsertSensorType.RelatedFeature> getRelatedFeature() {
        if (relatedFeature == null) {
            relatedFeature = new ArrayList<InsertSensorType.RelatedFeature>();
        }
        return this.relatedFeature;
    }

    /**
     * Gets the value of the metadata property.
     *
     */
    public List<InsertSensorType.Metadata> getMetadata() {
        if (metadata == null) {
            metadata = new ArrayList<InsertSensorType.Metadata>();
        }
        return this.metadata;
    }

    @Override
    public InsertionMetadataType getInsertionMetadata() {
        if (metadata != null && !metadata.isEmpty()) {
            return metadata.get(0).getInsertionMetadata();
        }
        return null;
    }

    @Override
    public ObservationTemplate getObservationTemplate() {
        return new ObservationTemplateType(observableProperty);
    }

    /**
     * Used to retro compatibility with SOS 1.0.0
     */
    public static class ObservationTemplateType implements ObservationTemplate {

        private String name;

        private String procedure;

        private List<String> observableProperty;

        public ObservationTemplateType(final List<String> properties) {
            this.observableProperty = properties;
        }

        @Override
        public String getProcedure() {
            return procedure;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isTemplateSpecified() {
            return true;
        }

        @Override
        public void setProcedure(final String process) {
            this.procedure = process;
        }

        @Override
        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public List<String> getObservedProperties() {
            return observableProperty;
        }

        @Override
        public String getFeatureOfInterest() {
            return null;
        }

        @Override
        public Observation getObservation() {
            return null;
        }

        @Override
        public List<PhenomenonProperty> getFullObservedProperties() {
            final List<PhenomenonProperty> results = new ArrayList<>();
            if (observableProperty != null) {
                for (final String phen : observableProperty) {
                    results.add(new PhenomenonProperty() {
                        @Override
                        public String getHref() {
                            return phen;
                        }
                        @Override
                        public void setToHref() {
                            // do nothing
                        }
                        @Override
                        public Phenomenon getPhenomenon() {
                            return null;
                        }
                    });
                }
            }
            return results;
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
     *         &lt;element ref="{http://www.opengis.net/swes/2.0}InsertionMetadata"/>
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
        "insertionMetadata"
    })
    public static class Metadata {

        @XmlElementRef(name = "InsertionMetadata", namespace = "http://www.opengis.net/swes/2.0", type = JAXBElement.class)
        private JAXBElement<? extends InsertionMetadataType> insertionMetadata;

        public InsertionMetadataType getInsertionMetadata() {
            if (insertionMetadata != null) {
                return insertionMetadata.getValue();
            }
            return null;
        }

        /**
         * Gets the value of the insertionMetadata property.
         *
         * @return
         *     possible object is
         *     {@link JAXBElement }{@code <}{@link SosInsertionMetadataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link InsertionMetadataType }{@code >}
         *
         */
        public JAXBElement<? extends InsertionMetadataType> getJbInsertionMetadata() {
            return insertionMetadata;
        }

        /**
         * Sets the value of the insertionMetadata property.
         *
         * @param value
         *     allowed object is
         *     {@link JAXBElement }{@code <}{@link SosInsertionMetadataType }{@code >}
         *     {@link JAXBElement }{@code <}{@link InsertionMetadataType }{@code >}
         *
         */
        public void setInsertionMetadata(JAXBElement<? extends InsertionMetadataType> value) {
            this.insertionMetadata = ((JAXBElement<? extends InsertionMetadataType> ) value);
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
