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

package org.geotoolkit.sos.xml;

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.gml.xml.Envelope;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface ObservationOffering {
    
    String getId();
    
    String getName();
    
    String getDescription();
    /*
     * 1.0.0
     */
    List<String> getSrsName();
    
    TemporalGeometricPrimitive getTime();
            
    Envelope getObservedArea();
    /*
     * 1.0.0
     */
    List<QName> getResultModel();
    
    List<String> getProcedures();
    
    List<String> getFeatureOfInterestIds();
    
    List<String> getObservedProperties();
    
    List<String> getResponseFormat();
}
