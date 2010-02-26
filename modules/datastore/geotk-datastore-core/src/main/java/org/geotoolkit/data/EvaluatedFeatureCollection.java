/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.data;

import java.util.AbstractCollection;

import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.data.query.Selector;
import org.geotoolkit.data.query.Source;
import org.geotoolkit.data.session.Session;

import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class EvaluatedFeatureCollection extends AbstractCollection<Feature> implements FeatureCollection<Feature> {

    public static FeatureCollection evaluate(Query query){
        return evaluate(query, null);
    }

    public static FeatureCollection evaluate(Query query, Session session){
        query = QueryUtilities.makeAbsolute(query, session);

        final Source s = query.getSource();

        if(s instanceof Selector){

        }

        return null;
    }

    private EvaluatedFeatureCollection(){}


}
