/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.report;

import java.util.Collection;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.util.Converters;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Map a FeatureCollection as a Jasper report data source.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeatureCollectionDataSource implements JRDataSource {

    private final FeatureCollection col;
    private final FeatureIterator iterator;
    private Feature feature;

    public FeatureCollectionDataSource(final FeatureCollection collection){
        ensureNonNull("collection", collection);
        this.col = collection;
        this.iterator = collection.iterator();
    }

    public FeatureCollectionDataSource(final FeatureIterator iterator){
        ensureNonNull("iterator", iterator);
        this.col = null;
        this.iterator = iterator;
    }

    @Override
    public boolean next() throws JRException {
        if(iterator.hasNext()){
            feature = iterator.next();
            return true;
        }else{
            if(col != null){
                //we created the iterator, we close it
                iterator.close();
            }
            return false;
        }
    }

    @Override
    public Object getFieldValue(final JRField jrf) throws JRException {
        
        //search for special fields
        final Collection<JRFieldRenderer> renderers = JasperReportService.getFieldRenderers();
        for(JRFieldRenderer r : renderers){
            if(r.canHandle(jrf)){
                return r.createValue(jrf,feature);
            }
        }

        //casual field types
        final String name = jrf.getName();
        final Property prop = feature.getProperty(name);
        if(prop != null){
            //just in case the type is not rigourously the same.
            final Class clazz = jrf.getValueClass();
            return Converters.convert(prop.getValue(), clazz);
        }

        //No field that match this name, looks like the feature type
        //used is not the exact one returned by the JasperReportservice.
        //This is not necessarly an error if for exemple someone ignore
        //some attribut from the template because he doesn't need them.
        return null;
    }

}
