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
package org.geotoolkit.swes.xml;

import java.util.List;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.observation.Observation;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationTemplate {
 
    String getProcedure();
    
    void setProcedure(final String process);
    
    void setName(final String name);
    
    List<String> getObservedProperties();
    
    List<PhenomenonProperty> getFullObservedProperties();
    
    String getFeatureOfInterest();
    
    boolean isComplete();
    
    boolean isTemplateSpecified();
    
    Observation getObservation();
    
}
