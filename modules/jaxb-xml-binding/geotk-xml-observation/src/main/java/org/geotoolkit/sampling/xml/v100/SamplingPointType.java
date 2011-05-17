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
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.PointPropertyType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.observation.xml.v100.ObservationType;
import org.geotoolkit.observation.xml.v100.SurveyProcedureType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.observation.sampling.SamplingPoint;

/**
 * Description of a station localised.
 *
 * @author Guilhem Legal
 * @module pending
 */
@XmlRootElement( name="SamplingPoint" )
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", namespace="http://www.opengis.net/sampling/1.0")
@XmlSeeAlso({PointType.class})
public class SamplingPointType extends SamplingFeatureType implements SamplingPoint {
    
    /**
     * the station position.
     */
    @XmlElement(required = true)
    private PointPropertyType position;
    
    /**
     * Constructor used by JAXB.
     */
    public SamplingPointType(){
    }
            
    /** 
     * Build a new station localised.
     */
    public SamplingPointType(final String                              identifier,
                              final String                              name,
                              final String                              remarks,
                              final List<SamplingFeatureRelationType>  relatedSamplingFeature,
                              final List<ObservationType >             relatedObservation,
                              final List<FeaturePropertyType>          sampledFeature,
                              final SurveyProcedureType                surveyDetail,
                              final PointPropertyType location)
    {
        super(identifier, name, remarks, relatedSamplingFeature, relatedObservation, sampledFeature, surveyDetail);
        this.position = location;
    }

    public SamplingPointType(final String identifier, final String name, final double x, final double y) {
        super(identifier, name,  null, new FeaturePropertyType(""));
        final DirectPositionType pos  = new DirectPositionType("urn:ogc:crs:espg:4326", 2, new ArrayList<Double>(Arrays.asList(y, x)));
        this.position = new PointPropertyType(new PointType(null, pos));

        final DirectPositionType pos2 = new DirectPositionType("urn:ogc:crs:espg:4326", 2, new ArrayList<Double>(Arrays.asList(y, x)));
        final DirectPositionType pos3 = new DirectPositionType("urn:ogc:crs:espg:4326", 2, new ArrayList<Double>(Arrays.asList(y, x)));
        final EnvelopeType envelope = new EnvelopeType(null, pos2, pos3, "urn:ogc:crs:espg:4326");
        this.setBoundedBy(envelope);
    }
    
     /** 
      * Build an entry to the identifier of the spécified station .
      * adapted for the BRGM model.
      * 
      */
    public SamplingPointType(final String               identifier,
                              final String               name,
                              final String               remarks,
                              final FeaturePropertyType  sampledFeature,
                              final PointPropertyType    location)
    {
        super(identifier, name, remarks, sampledFeature);
        this.position = location;
    }

    /**
      * Build an entry to the identifier of the spécified station .
      * adapted for the BRGM model.
      *
      */
    public SamplingPointType(final String              identifier,
                              final String              name,
                              final DirectPositionType  location) {
        super(identifier, name, null, new FeaturePropertyType(""));
        this.position = new PointPropertyType(new PointType(null, location));
    }

    /**
      * Build an entry to the identifier of the spécified station .
      * adapted for the BRGM model.
      *
      */
    public SamplingPointType(final String       identifier,
                              final String       name,
                              final String       srsName,
                              final List<Double> coord) {
        super(identifier, name, null, new FeaturePropertyType(""));
        this.position = new PointPropertyType(new PointType(null, new DirectPositionType(srsName, coord.size(), coord)));
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
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (object instanceof SamplingFeatureType && super.equals(object, mode)) {
            final SamplingPointType that = (SamplingPointType) object;
        
            return  Utilities.equals(this.position, that.position);
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
