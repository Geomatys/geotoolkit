/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sos.xml.v100;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import org.geotoolkit.ogc.xml.v110modified.BBOXType;
import org.geotoolkit.ogc.xml.v110modified.BinaryComparisonOpType;
import org.geotoolkit.ogc.xml.v110modified.BinarySpatialOpType;
import org.geotoolkit.ogc.xml.v110modified.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110modified.DistanceBufferType;
import org.geotoolkit.ogc.xml.v110modified.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110modified.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110modified.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110modified.SpatialOpsType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="offering" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="eventTime" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ogc}temporalOps"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="procedure" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observedProperty" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *         &lt;element name="featureOfInterest" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
 *                   &lt;element name="ObjectID" type="{http://www.w3.org/2001/XMLSchema}anyURI" maxOccurs="unbounded"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="result" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="responseFormat" type="{http://www.opengis.net/ows/1.1}MimeType"/>
 *         &lt;element name="resultModel" type="{http://www.w3.org/2001/XMLSchema}QName" minOccurs="0"/>
 *         &lt;element name="responseMode" type="{http://www.opengis.net/sos/1.0}responseModeType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 *  @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetObservation", propOrder = {
    "offering",
    "eventTime",
    "procedure",
    "observedProperty",
    "featureOfInterest",
    "result",
    "responseFormat",
    "resultModel",
    "responseMode"
})
@XmlRootElement(name = "GetObservation")
public class GetObservation extends RequestBaseType {

    @XmlSchemaType(name = "anyURI")
    private String offering;
    private List<EventTime> eventTime;
    @XmlSchemaType(name = "anyURI")
    private List<String> procedure;
    @XmlSchemaType(name = "anyURI")
    private List<String> observedProperty;
    private GetObservation.FeatureOfInterest featureOfInterest;
    private GetObservation.Result result;
    private String responseFormat;
    private QName resultModel;
    private String responseMode;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;

    /**
     * Empty constructor used by JAXB
     */
    GetObservation(){
        
    }
    
    /**
     * Build a new full GetObservation request
     */
    public GetObservation(String version, String offering, List<EventTime> eventTime, List<String> procedure,
            List<String> observedProperty, GetObservation.FeatureOfInterest featureOfInterest, 
            GetObservation.Result result, String responseFormat, QName resultModel, ResponseModeType responseMode,
            String srsName){
        super(version);
        this.eventTime         = eventTime;
        this.featureOfInterest = featureOfInterest;
        this.observedProperty  = observedProperty;
        this.offering          = offering;
        this.procedure         = procedure;
        this.responseFormat    = responseFormat;
        this.responseMode      = responseMode.value();
        this.result            = result;
        this.resultModel       = resultModel;
        this.srsName           = srsName;
        
    }
    
    /**
     * Gets the value of the offering property.
     */
    public String getOffering() {
        return offering;
    }

    /**
     * Gets the value of the eventTime property.
     */
    public List<EventTime> getEventTime() {
        if (eventTime == null) {
            eventTime = new ArrayList<EventTime>();
        }
        return Collections.unmodifiableList(eventTime);
    }

    /**
     * Gets the value of the procedure property.
     * (unmodifiable)
     */
    public List<String> getProcedure() {
        if (procedure == null) {
            procedure = new ArrayList<String>();
        }
        return Collections.unmodifiableList(procedure);
    }

    /**
     * Gets the value of the observedProperty property.
     * (unmodifiable)
     */
    public List<String> getObservedProperty() {
        if (observedProperty == null) {
            observedProperty = new ArrayList<String>();
        }
        return Collections.unmodifiableList(observedProperty);
    }

    /**
     * Gets the value of the featureOfInterest property.
     */
    public GetObservation.FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * Gets the value of the result property.
     */
    public GetObservation.Result getResult() {
        return result;
    }

    /**
     * Gets the value of the responseFormat property.
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * Gets the value of the resultModel property.
     */
    public QName getResultModel() {
        return resultModel;
    }

    /**
     * Gets the value of the responseMode property.
     */
    public String getResponseMode() {
        return responseMode;
    }

    /**
     * Gets the value of the srsName property.
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * Verify if this entry is identical to�the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof GetObservation && super.equals(object)) {
            final GetObservation that = (GetObservation) object;
            return Utilities.equals(this.eventTime,         that.eventTime)         &&
                   Utilities.equals(this.featureOfInterest, that.featureOfInterest) &&
                   Utilities.equals(this.observedProperty,  that.observedProperty)  &&
                   Utilities.equals(this.offering,          that.offering)          &&
                   Utilities.equals(this.procedure,         that.procedure)         &&
                   Utilities.equals(this.responseFormat,    that.responseFormat)    &&
                   Utilities.equals(this.responseMode,      that.responseMode)      &&
                   Utilities.equals(this.result,            that.result)            &&
                   Utilities.equals(this.resultModel,       that.resultModel)       &&
                   Utilities.equals(this.srsName,           that.srsName);
        } 
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.offering != null ? this.offering.hashCode() : 0);
        hash = 73 * hash + (this.eventTime != null ? this.eventTime.hashCode() : 0);
        hash = 73 * hash + (this.procedure != null ? this.procedure.hashCode() : 0);
        hash = 73 * hash + (this.observedProperty != null ? this.observedProperty.hashCode() : 0);
        hash = 73 * hash + (this.featureOfInterest != null ? this.featureOfInterest.hashCode() : 0);
        hash = 73 * hash + (this.result != null ? this.result.hashCode() : 0);
        hash = 73 * hash + (this.responseFormat != null ? this.responseFormat.hashCode() : 0);
        hash = 73 * hash + (this.resultModel != null ? this.resultModel.hashCode() : 0);
        hash = 73 * hash + (this.responseMode != null ? this.responseMode.hashCode() : 0);
        hash = 73 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("GetObservation:");
        s.append('\n').append("offering=").append(offering).append('\n');
        s.append("Response format:").append(responseFormat).append('\n');
        s.append("SRS name:").append(srsName).append('\n');
        if (featureOfInterest != null)
            s.append("featureOfInterest:").append('\n').append(featureOfInterest.toString()).append('\n');
        if (responseMode != null)
             s.append("response mode:").append('\n').append(responseMode.toString()).append('\n');
        if (result != null) 
             s.append("result:").append('\n').append(result.toString()).append('\n');
        if (resultModel != null) 
             s.append("result model:").append('\n').append(resultModel.toString()).append('\n');
        
        s.append("observed properties:").append('\n');
        for (String ss:observedProperty) {
            s.append(ss).append('\n');
        }
        s.append("procedures:").append('\n');
        for (String ss:procedure) {
            s.append(ss).append('\n');
        }
        s.append("eventTime:").append('\n');
        for (EventTime ss:eventTime) {
            s.append(ss.toString()).append('\n');
        }
        return s.toString();
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
     *         &lt;choice>
     *           &lt;element ref="{http://www.opengis.net/ogc}spatialOps"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Intersects"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Touches"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}DWithin"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Disjoint"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Crosses"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Contains"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Beyond"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Equals"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Overlaps"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}BBOX"/>
     *           &lt;element ref="{http://www.opengis.net/ogc}Within"/>
     *         &lt;/choice>
     *         &lt;element name="ObjectID" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * @author Guilhem Legal
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "spatialOps",
        "intersects",
        "touches",
        "dWithin",
        "disjoint",
        "crosses",
        "contains",
        "beyond",
        "equals",
        "overlaps",
        "bbox",
        "within",
        "objectID"
    })
    public static class FeatureOfInterest {

        @XmlElement(namespace = "http://www.opengis.net/ogc")
        private SpatialOpsType spatialOps;
        @XmlElement(name = "Intersects", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType intersects;
        @XmlElement(name = "Touches", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType touches;
        @XmlElement(name = "DWithin", namespace = "http://www.opengis.net/ogc")
        private DistanceBufferType dWithin;
        @XmlElement(name = "Disjoint", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType disjoint;
        @XmlElement(name = "Crosses", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType crosses;
        @XmlElement(name = "Contains", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType contains;
        @XmlElement(name = "Beyond", namespace = "http://www.opengis.net/ogc")
        private DistanceBufferType beyond;
        @XmlElement(name = "Equals", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType equals;
        @XmlElement(name = "Overlaps", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType overlaps;
        @XmlElement(name = "BBOX", namespace = "http://www.opengis.net/ogc")
        private BBOXType bbox;
        @XmlElement(name = "Within", namespace = "http://www.opengis.net/ogc")
        private BinarySpatialOpType within;
        @XmlElement(name = "ObjectID")
        private List<String> objectID = new ArrayList<String>();

        public FeatureOfInterest() {

        }

        public FeatureOfInterest(List<String> objectID) {
            this.objectID = objectID;
        }

        public FeatureOfInterest(BBOXType bbox) {
            this.bbox = bbox;
        }

        /**
         * Return the spatial operator whitch is not null.If there is not return null.
         * SO your can only use this method to access all the spatial operator
         * (there must be just one not null).
         * @return
         *     possible object is
         *     {@link SpatialOpsType }
         *     
         */
        public SpatialOpsType getSpatialOps() {
            if (bbox != null)
                return bbox;
            else if (intersects != null)
                return intersects;
            else if (touches != null)
                return touches;
            else if (dWithin != null)
                return dWithin;
            else if (disjoint != null)
                return disjoint;
            else if (crosses != null)
                return crosses;
            else if (contains != null)
                return contains;
            else if (beyond != null)
                return beyond;
            else if (equals != null)
                return equals;
            else if (overlaps != null)
                return overlaps;
            else if (within != null)
                return within;
            
            return spatialOps;
        }

        /**
         * Gets the value of the intersects property.
         */
        public BinarySpatialOpType getIntersects() {
            return intersects;
        }

        /**
         * Gets the value of the touches property.
         */
        public BinarySpatialOpType getTouches() {
            return touches;
        }

        /**
         * Gets the value of the dWithin property.
         */
        public DistanceBufferType getDWithin() {
            return dWithin;
        }

        /**
         * Gets the value of the disjoint property.
         */
        public BinarySpatialOpType getDisjoint() {
            return disjoint;
        }

        /**
         * Gets the value of the crosses property.
         */
        public BinarySpatialOpType getCrosses() {
            return crosses;
        }

        /**
         * Gets the value of the contains property.
         */
        public BinarySpatialOpType getContains() {
            return contains;
        }

        /**
         * Gets the value of the beyond property.
         */
        public DistanceBufferType getBeyond() {
            return beyond;
        }

        /**
         * Gets the value of the equals property.
         */
        public BinarySpatialOpType getEquals() {
            return equals;
        }

        /**
         * Gets the value of the overlaps property.
         */
        public BinarySpatialOpType getOverlaps() {
            return overlaps;
        }

        /**
         * Gets the value of the bbox property.
         */
        public BBOXType getBBOX() {
            return bbox;
        }

        /**
         * Gets the value of the within property.
         */
        public BinarySpatialOpType getWithin() {
            return within;
        }

        /**
         * Gets the value of the objectID property.
         * (unmodifiable)
         */
        public List<String> getObjectID() {
            return Collections.unmodifiableList(objectID);
        }
        
    /**
     * Verify if this entry is identical to�the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FeatureOfInterest) {
        final FeatureOfInterest that = (FeatureOfInterest) object;
            return Utilities.equals(this.bbox,       that.bbox)       &&
                   Utilities.equals(this.beyond,     that.beyond)     &&
                   Utilities.equals(this.contains,   that.contains)   &&
                   Utilities.equals(this.crosses,    that.crosses)    &&
                   Utilities.equals(this.dWithin,    that.dWithin )   &&
                   Utilities.equals(this.disjoint,   that.disjoint)   &&
                   Utilities.equals(this.equals,     that.equals)     &&
                   Utilities.equals(this.intersects, that.intersects) &&
                   Utilities.equals(this.objectID,   that.objectID)   &&
                   Utilities.equals(this.overlaps,   that.overlaps)   &&
                   Utilities.equals(this.spatialOps, that.spatialOps) &&
                   Utilities.equals(this.touches,    that.touches)    &&
                   Utilities.equals(this.within,     that.within);
        }
        return false;
    }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this.spatialOps != null ? this.spatialOps.hashCode() : 0);
            hash = 23 * hash + (this.intersects != null ? this.intersects.hashCode() : 0);
            hash = 23 * hash + (this.touches != null ? this.touches.hashCode() : 0);
            hash = 23 * hash + (this.dWithin != null ? this.dWithin.hashCode() : 0);
            hash = 23 * hash + (this.disjoint != null ? this.disjoint.hashCode() : 0);
            hash = 23 * hash + (this.crosses != null ? this.crosses.hashCode() : 0);
            hash = 23 * hash + (this.contains != null ? this.contains.hashCode() : 0);
            hash = 23 * hash + (this.beyond != null ? this.beyond.hashCode() : 0);
            hash = 23 * hash + (this.equals != null ? this.equals.hashCode() : 0);
            hash = 23 * hash + (this.overlaps != null ? this.overlaps.hashCode() : 0);
            hash = 23 * hash + (this.bbox != null ? this.bbox.hashCode() : 0);
            hash = 23 * hash + (this.within != null ? this.within.hashCode() : 0);
            hash = 23 * hash + (this.objectID != null ? this.objectID.hashCode() : 0);
            return hash;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.opengis.net/ogc}comparisonOps"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsLessThan"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsGreaterThanOrEqualTo"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsEqualTo"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsNotEqualTo"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsLessThanOrEqualTo"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsLike"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsBetween"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsGreaterThan"/>
     *         &lt;element ref="{http://www.opengis.net/ogc}PropertyIsNull"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * @author Guilhem Legal
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "comparisonOps",
        "propertyIsLessThan",
        "propertyIsGreaterThanOrEqualTo",
        "propertyIsEqualTo",
        "propertyIsNotEqualTo",
        "propertyIsLessThanOrEqualTo",
        "propertyIsLike",
        "propertyIsBetween",
        "propertyIsGreaterThan",
        "propertyIsNull"
    })
    public static class Result {

        @XmlElement(namespace = "http://www.opengis.net/ogc")
        private ComparisonOpsType comparisonOps;
        @XmlElement(name = "PropertyIsLessThan", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsLessThan;
        @XmlElement(name = "PropertyIsGreaterThanOrEqualTo", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsGreaterThanOrEqualTo;
        @XmlElement(name = "PropertyIsEqualTo", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsEqualTo;
        @XmlElement(name = "PropertyIsNotEqualTo", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsNotEqualTo;
        @XmlElement(name = "PropertyIsLessThanOrEqualTo", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsLessThanOrEqualTo;
        @XmlElement(name = "PropertyIsLike", namespace = "http://www.opengis.net/ogc")
        private PropertyIsLikeType propertyIsLike;
        @XmlElement(name = "PropertyIsBetween", namespace = "http://www.opengis.net/ogc")
        private PropertyIsBetweenType propertyIsBetween;
        @XmlElement(name = "PropertyIsGreaterThan", namespace = "http://www.opengis.net/ogc")
        private BinaryComparisonOpType propertyIsGreaterThan;
        @XmlElement(name = "PropertyIsNull", namespace = "http://www.opengis.net/ogc")
        private PropertyIsNullType propertyIsNull;

        /**
         * Gets the value of the comparisonOps property.
         */
        public ComparisonOpsType getComparisonOps() {
            return comparisonOps;
        }

        /**
         * Gets the value of the propertyIsLessThan property.
         */
        public BinaryComparisonOpType getPropertyIsLessThan() {
            return propertyIsLessThan;
        }

        /**
         * Gets the value of the propertyIsGreaterThanOrEqualTo property.
         */
        public BinaryComparisonOpType getPropertyIsGreaterThanOrEqualTo() {
            return propertyIsGreaterThanOrEqualTo;
        }

        /**
         * Gets the value of the propertyIsEqualTo property.
         */
        public BinaryComparisonOpType getPropertyIsEqualTo() {
            return propertyIsEqualTo;
        }

        /**
         * Gets the value of the propertyIsNotEqualTo property.
         */
        public BinaryComparisonOpType getPropertyIsNotEqualTo() {
            return propertyIsNotEqualTo;
        }

        /**
         * Gets the value of the propertyIsLessThanOrEqualTo property.
         */
        public BinaryComparisonOpType getPropertyIsLessThanOrEqualTo() {
            return propertyIsLessThanOrEqualTo;
        }

        /**
         * Gets the value of the propertyIsLike property.
         */
        public PropertyIsLikeType getPropertyIsLike() {
            return propertyIsLike;
        }

        /**
         * Gets the value of the propertyIsBetween property.
         */
        public PropertyIsBetweenType getPropertyIsBetween() {
            return propertyIsBetween;
        }

        /**
         * Gets the value of the propertyIsGreaterThan property.
         */
        public BinaryComparisonOpType getPropertyIsGreaterThan() {
            return propertyIsGreaterThan;
        }

        /**
         * Gets the value of the propertyIsNull property.
         */
        public PropertyIsNullType getPropertyIsNull() {
            return propertyIsNull;
        }

        
        /**
         * Verify if this entry is identical to�the specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Result) {
                final Result that = (Result) object;
                return Utilities.equals(this.comparisonOps,                  that.comparisonOps)                  &&
                       Utilities.equals(this.propertyIsBetween,              that.propertyIsBetween)              &&
                       Utilities.equals(this.propertyIsEqualTo,              that.propertyIsEqualTo)              &&
                       Utilities.equals(this.propertyIsGreaterThan,          that.propertyIsGreaterThan)          &&
                       Utilities.equals(this.propertyIsGreaterThanOrEqualTo, that.propertyIsGreaterThanOrEqualTo) &&
                       Utilities.equals(this.propertyIsLessThan,             that.propertyIsLessThan)             &&
                       Utilities.equals(this.propertyIsLessThanOrEqualTo,    that.propertyIsLessThanOrEqualTo)    &&
                       Utilities.equals(this.propertyIsLike,                 that.propertyIsLike)                 &&
                       Utilities.equals(this.propertyIsNotEqualTo,           that.propertyIsNotEqualTo)           &&
                       Utilities.equals(this.propertyIsNull,                 that.propertyIsNull);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 73 * hash + (this.comparisonOps != null ? this.comparisonOps.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsLessThan != null ? this.propertyIsLessThan.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsGreaterThanOrEqualTo != null ? this.propertyIsGreaterThanOrEqualTo.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsEqualTo != null ? this.propertyIsEqualTo.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsNotEqualTo != null ? this.propertyIsNotEqualTo.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsLessThanOrEqualTo != null ? this.propertyIsLessThanOrEqualTo.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsLike != null ? this.propertyIsLike.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsBetween != null ? this.propertyIsBetween.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsGreaterThan != null ? this.propertyIsGreaterThan.hashCode() : 0);
            hash = 73 * hash + (this.propertyIsNull != null ? this.propertyIsNull.hashCode() : 0);
            return hash;
        }
    }
}
