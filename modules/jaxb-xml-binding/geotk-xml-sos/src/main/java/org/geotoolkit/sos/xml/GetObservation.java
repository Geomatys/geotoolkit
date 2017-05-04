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
import org.geotoolkit.ows.xml.RequestBase;
import org.opengis.filter.Filter;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface GetObservation extends RequestBase {

    List<String> getProcedure();

    List<String> getObservedProperty();

    List<String> getOfferings();

    String getResponseFormat();

    QName getResultModel();

    String getResponseMode();

    String getSrsName();

    List<String> getFeatureIds();

    Filter getSpatialFilter();

    Filter getComparisonFilter();

    List<Filter> getTemporalFilter();
}
