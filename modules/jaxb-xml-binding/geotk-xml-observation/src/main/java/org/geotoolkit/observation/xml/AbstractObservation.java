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
package org.geotoolkit.observation.xml;

import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys).
 */
public interface AbstractObservation extends Observation {
 
    @Override
    Process getProcedure();
    
    String getId();
    
    void setId(final String id);
    
    void setName(final String name);
    
    void setProcedure(final String procedureID);
    
    void setResult(final Object result);
    
    void emptySamplingTime();
    
    void setSamplingTimePeriod(final Period period);
    
    void extendSamplingTime(final String newEndBound);
    
    boolean matchTemplate(final Observation template);
    
    PhenomenonProperty getPropertyObservedProperty();
    
    FeatureProperty getPropertyFeatureOfInterest();
    
    AbstractObservation getTemporaryTemplate(final String temporaryName, TemporalGeometricPrimitive time);
}
