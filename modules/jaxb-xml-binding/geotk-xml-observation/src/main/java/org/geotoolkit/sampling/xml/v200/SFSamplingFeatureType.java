/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.sampling.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractFeatureType;
import org.geotoolkit.gml.xml.v321.FeaturePropertyType;
import org.geotoolkit.gml.xml.v321.ReferenceType;
import org.geotoolkit.observation.xml.v200.NamedValuePropertyType;
import org.geotoolkit.observation.xml.v200.OMObservationPropertyType;
import org.geotoolkit.observation.xml.v200.OMObservationType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.observation.sampling.SamplingFeatureRelation;
import org.opengis.observation.sampling.SurveyProcedure;

/**
 * A "SamplingFeature" is a feature used primarily for taking
 * 				observations.
 * 
 * <p>Java class for SF_SamplingFeatureType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SF_SamplingFeatureType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/sampling/2.0}SF_CommonProperties"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SF_SamplingFeatureType", propOrder = {
    "type",
    "sampledFeature",
    "lineage",
    "relatedObservation",
    "relatedSamplingFeature",
    "parameter"
})
public class SFSamplingFeatureType extends AbstractFeatureType implements SamplingFeature {

    private ReferenceType type;
    @XmlElement(required = true, nillable = true)
    private FeaturePropertyType sampledFeature;
    private Lineage lineage;
    private List<OMObservationPropertyType> relatedObservation;
    private List<SamplingFeatureComplexPropertyType> relatedSamplingFeature;
    private List<NamedValuePropertyType> parameter;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setType(ReferenceType value) {
        this.type = value;
    }

    /**
     * Gets the value of the sampledFeature property.
     * 
     * @return
     *     possible object is
     *     {@link FeaturePropertyType }
     *     
     */
    public FeaturePropertyType getSampledFeatureProperty() {
        return sampledFeature;
    }

    @Override
    public List<AnyFeature> getSampledFeature() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Sets the value of the sampledFeature property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeaturePropertyType }
     *     
     */
    public void setSampledFeature(FeaturePropertyType value) {
        this.sampledFeature = value;
    }

    /**
     * Gets the value of the lineage property.
     * 
     * @return
     *     possible object is
     *     {@link LILineagePropertyType }
     *     
     */
    public Lineage getLineage() {
        return lineage;
    }

    /**
     * Sets the value of the lineage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LILineagePropertyType }
     *     
     */
    public void setLineage(Lineage value) {
        this.lineage = value;
    }

    /**
     * Gets the value of the relatedObservation property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link OMObservationPropertyType }
     * 
     * 
     */
    @Override
    public List<Observation> getRelatedObservation() {
        final List<Observation> result = new ArrayList<Observation>();
        if (relatedObservation != null) {
            for (OMObservationPropertyType op : relatedObservation) {
                if (op.getOMObservation() != null) {
                    result.add(op.getOMObservation());
                }
            }
        }
        return result;
    }
    
    /**
     * Gets the value of the relatedObservation property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link OMObservationPropertyType }
     * 
     * 
     */
    public List<OMObservationPropertyType> getRelatedObservationProperty() {
        if (relatedObservation == null) {
            relatedObservation = new ArrayList<OMObservationPropertyType>();
        }
        return this.relatedObservation;
    }

    /**
     * Gets the value of the relatedSamplingFeature property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link SamplingFeatureComplexPropertyType }
     * 
     * 
     */
    public List<SamplingFeatureComplexPropertyType> getRelatedSamplingFeatureProperty() {
        if (relatedSamplingFeature == null) {
            relatedSamplingFeature = new ArrayList<SamplingFeatureComplexPropertyType>();
        }
        return this.relatedSamplingFeature;
    }
    
    @Override
    public List<SamplingFeatureRelation> getRelatedSamplingFeature() {
        final List<SamplingFeatureRelation> result = new ArrayList<SamplingFeatureRelation>();
        if (relatedSamplingFeature != null) {
            for (SamplingFeatureComplexPropertyType sc : relatedSamplingFeature) {
                result.add(sc.getSamplingFeatureComplex());
            }
        }
        return result;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link NamedValuePropertyType }
     * 
     * 
     */
    public List<NamedValuePropertyType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<NamedValuePropertyType>();
        }
        return this.parameter;
    }

    @Override
    public SurveyProcedure getSurveyDetail() {
        throw new UnsupportedOperationException("Not supported in O&M 2.0.0");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (lineage != null) {
            sb.append("lineage: ").append(lineage).append('\n');
        }
        if (sampledFeature != null) {
            sb.append("sampledFeature: ").append(sampledFeature).append('\n');
        }
        if (type != null) {
            sb.append("type: ").append(type).append('\n');
        }
        if (parameter != null) {
            sb.append("parameter:\n");
            for (NamedValuePropertyType param : parameter) {
                sb.append(param).append('\n');
            }
        }
        if (relatedObservation != null) {
            sb.append("relatedObservation:\n");
            for (OMObservationPropertyType process : relatedObservation) {
                sb.append(process).append('\n');
            }
        }
        if (relatedSamplingFeature != null) {
            sb.append("relatedSamplingFeature:\n");
            for (SamplingFeatureComplexPropertyType process : relatedSamplingFeature) {
                sb.append(process).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SFSamplingFeatureType && super.equals(object, mode)) {
            final SFSamplingFeatureType that = (SFSamplingFeatureType) object;

            return Utilities.equals(this.lineage,                that.lineage)               &&
                   Utilities.equals(this.parameter,              that.parameter)             &&
                   Utilities.equals(this.relatedObservation,     that.relatedObservation)    &&
                   Utilities.equals(this.relatedSamplingFeature, that.relatedSamplingFeature)&&
                   Utilities.equals(this.sampledFeature,         that.sampledFeature)        &&
                   Utilities.equals(this.type,                   that.type);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + super.hashCode();
        hash = 83 * hash + (this.lineage != null ? this.lineage.hashCode() : 0);
        hash = 83 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        hash = 83 * hash + (this.relatedObservation != null ? this.relatedObservation.hashCode() : 0);
        hash = 83 * hash + (this.relatedSamplingFeature != null ? this.relatedSamplingFeature.hashCode() : 0);
        hash = 83 * hash + (this.sampledFeature != null ? this.sampledFeature.hashCode() : 0);
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }
}
