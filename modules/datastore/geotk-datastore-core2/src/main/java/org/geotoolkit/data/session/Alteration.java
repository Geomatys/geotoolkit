/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.data.session;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.query.Query;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface Alteration {

    Query alterate(Query query);

    FeatureReader alterate(FeatureReader reader);
    
    long alterate(long count);

    Envelope alterate(Envelope env);

    void apply(DataStore store);

    void dispose();

}
