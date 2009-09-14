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

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureEntry;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.observation.xml.v100.ObservationEntry;
import org.geotoolkit.observation.xml.v100.SurveyProcedureEntry;
import org.geotoolkit.util.Utilities;
import org.opengis.observation.sampling.SamplingPoint;

/**
 * Description of a station localised.
 *
 * @author Guilhem Legal
 */
@XmlRootElement( name="SamplingPoint" )
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace="http://www.opengis.net/sampling/1.0")
@XmlSeeAlso({PointType.class})
public class SamplingPointEntry extends SamplingFeatureEntry implements SamplingPoint {
    
    /**
     * the station position.
     */
    @XmlElement(required = true)
    private PointPropertyType position;
    
    /**
     * Constructor used by JAXB.
     */
    public SamplingPointEntry(){
    }
            
    /** 
     * Build a new station localised.
     */
    public SamplingPointEntry(final String                              identifier,
                              final String                              name,
                              final String                              remarks,
                              final List<SamplingFeatureRelationEntry>  relatedSamplingFeature,
                              final List<ObservationEntry >             relatedObservation,
                              final List<FeaturePropertyType>          sampledFeature,
                              final SurveyProcedureEntry                surveyDetail,
                              final PointPropertyType location)
    {
        super(identifier, name, remarks, relatedSamplingFeature, relatedObservation, sampledFeature, surveyDetail);
        this.position = location;
    }
    
     /** 
      * Build an entry to the identifier of the spécified station .
      * adapted for the BRGM model.
      * 
      */
    public SamplingPointEntry(final String               identifier,
                              final String               name,
                              final String               remarks,
                              final FeaturePropertyType  sampledFeature,
                              final PointPropertyType    location)
    {
        super(identifier, name, remarks, sampledFeature);
        this.position = location;
    }
    
    /**
     * Return the station position.
     */
    @Override
    public PointType getPosition(){
        if (position != null)
            return position.getPoint();
        return null;
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SamplingFeatureEntry && super.equals(object)) {
            final SamplingPointEntry that = (SamplingPointEntry) object;
        
            return  Utilities.equals(this.position, that.position);
        } else {
            System.out.println("samplingFeature.equals=false");
        }
        return false;
    }

    @Override
    public int hashCode() {
        String id = getId();
        if (id != null) {
            return id.hashCode();
        } else  {
            return super.hashCode();
        }
    }
    
    /**
     * Return a String representing the station.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append('\n').append("Position: ").append(position) ;
        return s.toString();
    }

  
}
