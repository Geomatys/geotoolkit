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
package org.geotoolkit.sos.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v311.BoundingShapeType;
import org.geotoolkit.gml.xml.v311.ReferenceType;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.geotoolkit.swe.xml.v101.PhenomenonPropertyType;
import org.geotoolkit.swe.xml.v101.TimeGeometricPrimitivePropertyType;
import org.geotoolkit.util.Utilities;


/**
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationOfferingType", propOrder = {
    "intendedApplication",
    "time",
    "procedure",
    "observedProperty",
    "featureOfInterest",
    "responseFormat",
    "resultModel",
    "responseMode"
})
@XmlRootElement(name = "ObservationOffering")
public class ObservationOfferingType extends AbstractFeatureType {

    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    private List<String> intendedApplication;
    private TimeGeometricPrimitivePropertyType time;
    @XmlElement(required = true)
    private List<ReferenceType> procedure;
    @XmlElement(required = true)
    private List<PhenomenonPropertyType> observedProperty;
    @XmlElement(required = true)
    private List<ReferenceType> featureOfInterest;
    @XmlElement(required = true)
    private List<String> responseFormat;
    private List<QName> resultModel;
    private List<ResponseModeType> responseMode;

    /**
     *  An empty constructor used by jaxB
     */ 
    ObservationOfferingType(){}
    
    /**
     *  Build a new offering.
     */ 
    public ObservationOfferingType(final String id, final String name, final String description, final ReferenceType descriptionReference,
            final BoundingShapeType boundedBy, final List<String> srsName, final AbstractTimeGeometricPrimitiveType time, final List<ReferenceType> procedure,
            final List<PhenomenonPropertyType> observedProperty, final List<ReferenceType> featureOfInterest,
            final List<String> responseFormat, final List<QName> resultModel, final List<ResponseModeType> responseMode) {
        
        super(id, name, description, descriptionReference, boundedBy, srsName);
        this.procedure         = procedure;
        this.observedProperty = observedProperty;
        this.featureOfInterest = featureOfInterest;
        this.responseFormat    = responseFormat;
        this.resultModel       = resultModel;
        this.responseMode      = responseMode;
        this.time              = new TimeGeometricPrimitivePropertyType(time);
    }

    /**
     *  Build a new offering.
     */
    public ObservationOfferingType(final String id, final String name, final String description, final List<String> srsName, final AbstractTimeGeometricPrimitiveType time, final ReferenceType procedure,
            final PhenomenonType observedProperty, final ReferenceType featureOfInterest,
            final List<String> responseFormat, final List<QName> resultModel, final List<ResponseModeType> responseMode) {

        super(id, name, description, null, null, srsName);
        this.procedure         = Arrays.asList(procedure);
        if(observedProperty != null){
            this.observedProperty = Arrays.asList(new PhenomenonPropertyType(observedProperty));
        }
        if (featureOfInterest != null) {
            this.featureOfInterest = Arrays.asList(featureOfInterest);
        }
        this.responseFormat    = responseFormat;
        this.resultModel       = resultModel;
        this.responseMode      = responseMode;
        this.time              = new TimeGeometricPrimitivePropertyType(time);
    }
    
    /**
     * Return the value of the intendedApplication property.
     * 
     */
    public List<String> getIntendedApplication() {
        if (intendedApplication == null) {
            intendedApplication = new ArrayList<String>();
        }
        return intendedApplication;
    }

    /**
     * Return the value of the eventTime property.
     * 
     */
    public AbstractTimeGeometricPrimitiveType getTime() {
       return time.getTimeGeometricPrimitive();
    }

    /**
     * Sets the value of the eventTime property.
     */
    public void setTime(final AbstractTimeGeometricPrimitiveType value) {
        if (time != null)
            this.time.setTimeGeometricPrimitive(value);
        else
            this.time = new TimeGeometricPrimitivePropertyType(value);
    }

    /**
     *  Return an unmodifiable list of the procedures
     */
    public List<ReferenceType> getProcedure() {
        if (procedure == null) {
            procedure = new ArrayList<ReferenceType>();
        }
        return procedure;
    }
    
    
    /**
     * Return an unmodifiable list of the observedProperty.
     */
    public List<PhenomenonType> getObservedProperty() {
        if (observedProperty == null){
           return new ArrayList<PhenomenonType>();

        } else {
            List<PhenomenonType> result = new ArrayList<PhenomenonType>();
            for (PhenomenonPropertyType pp:observedProperty){
                result.add(pp.getPhenomenon());
            }
            return result;
        }
    }
    
    /**
     * Return an unmodifiable list of the observedProperty.
     */
    public List<PhenomenonPropertyType> getRealObservedProperty() {
        if (observedProperty == null){
           return new ArrayList<PhenomenonPropertyType>();

        } else {
            return observedProperty;
        }
    }

    /**
     * Return an unmodifiable list of the featureOfInterest.
     * 
     */
    public List<ReferenceType> getFeatureOfInterest() {
        if (featureOfInterest == null){
            featureOfInterest = new ArrayList<ReferenceType>();
        }
        return featureOfInterest;
    }

   
    /**
     * Return the value of the resultFormat property.
     * 
     */
    public List<String> getResponseFormat() {
        if (responseFormat == null){
            responseFormat = new ArrayList<String>();
        }
        return responseFormat;
    }

    /**
     * Return the value of the resultModel property.
     * 
     */
    public List<QName> getResultModel() {
        if (resultModel == null){
            resultModel = new ArrayList<QName>();
        }
        return resultModel;
    }

    /**
     * Return the value of the responseMode property.
     * 
     */
    public List<ResponseModeType> getResponseMode() {
       if (responseMode == null){
            responseMode = new ArrayList<ResponseModeType>();
        }
       return responseMode;
    }
    
    /**
     * Verifie si cette entree est identique a l'objet specifie.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ObservationOfferingType && super.equals(object)) {
            final ObservationOfferingType that = (ObservationOfferingType) object;
            return Utilities.equals(this.time,                that.time)                &&
                   Utilities.equals(this.featureOfInterest,   that.featureOfInterest)   &&
                   Utilities.equals(this.intendedApplication, that.intendedApplication) && 
                   Utilities.equals(this.observedProperty,    that.observedProperty)    &&
                   Utilities.equals(this.procedure,           that.procedure)           &&
                   Utilities.equals(this.responseFormat,      that.responseFormat)      &&
                   Utilities.equals(this.responseMode,        that.responseMode)        &&
                   Utilities.equals(this.resultModel,         that.resultModel);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.intendedApplication != null ? this.intendedApplication.hashCode() : 0);
        hash = 67 * hash + (this.time != null ? this.time.hashCode() : 0);
        hash = 67 * hash + (this.procedure != null ? this.procedure.hashCode() : 0);
        hash = 67 * hash + (this.observedProperty != null ? this.observedProperty.hashCode() : 0);
        hash = 67 * hash + (this.featureOfInterest != null ? this.featureOfInterest.hashCode() : 0);
        hash = 67 * hash + (this.responseFormat != null ? this.responseFormat.hashCode() : 0);
        hash = 67 * hash + (this.resultModel != null ? this.resultModel.hashCode() : 0);
        hash = 67 * hash + (this.responseMode != null ? this.responseMode.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString()).append('\n');
        s.append("time=" + time ).append('\n');
        if (intendedApplication != null){
            s.append('\n').append("intendedApplication:").append('\n');
            for (String ss:intendedApplication){
                s.append(ss);
            }
        }
        if (responseFormat != null){
            s.append('\n').append("responseFormat:").append('\n');
            for (String ss:responseFormat){
                s.append(ss).append('\n');
            }
        }
        if (responseMode != null){
            s.append('\n').append("response mode:").append('\n');
            for (ResponseModeType ss:responseMode){
                s.append(ss.value()).append('\n');
            }
        }
         if (resultModel != null){
            s.append('\n').append("result model:").append('\n');
            for (QName ss:resultModel){
                s.append(ss.toString()).append('\n');
            }
        }
        if (featureOfInterest != null){
           s.append('\n').append("feature of interest:").append('\n');
           for (ReferenceType ref:featureOfInterest){
                s.append(ref.toString());
            } 
        }
        if (procedure != null){
           s.append('\n').append("procedure:").append('\n');
           for (ReferenceType ref:procedure){
                s.append(ref.toString());
            } 
        }
        if (observedProperty != null){
           s.append('\n').append("observedProperty:").append('\n');
           for (PhenomenonPropertyType phen:observedProperty){
                s.append(phen);
            } 
        }
        return s.toString();
    }


}
