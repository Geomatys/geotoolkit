/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.filter.binding.Bindings;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.PropertyNotFoundException;

/**
 * Map a Collection as a Jasper report data source.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CollectionDataSource implements JRDataSource {

    private final Collection col;
    private final Iterator iterator;
    private Object candidate;

    public CollectionDataSource(final Collection collection){
        ensureNonNull("collection", collection);
        this.col = collection;
        this.iterator = collection.iterator();
    }

    public CollectionDataSource(final Iterator iterator){
        ensureNonNull("iterator", iterator);
        this.col = null;
        this.iterator = iterator;
    }

    public CollectionDataSource cloneDataSource(){
        if(col!=null){
            return new CollectionDataSource(col);
        } else {
            throw new UnsupportedOperationException("This operation needs the data source has a feature collection.");
        }
    }

    @Override
    public boolean next() throws JRException {
        if(iterator.hasNext()){
            candidate = iterator.next();
            return true;
        }else{
            if(col != null && iterator instanceof Closeable){
                try {
                    //we created the iterator, we close it
                    ((Closeable)iterator).close();
                } catch (IOException ex) {
                    throw new JRException(ex.getMessage(), ex);
                }
            }
            return false;
        }
    }

    @Override
    public Object getFieldValue(final JRField jrf) throws JRException {

        //search for special fields
        if(candidate instanceof Feature){
            final Collection<JRFieldRenderer> renderers = JasperReportService.getFieldRenderers();
            for(JRFieldRenderer r : renderers){
                if(r.canHandle(jrf)){
                    return r.createValue(jrf,(Feature)candidate);
                }
            }
        }

        //casual field types
        final String name = jrf.getName();

        if(candidate instanceof Feature){
            try{
                final Property prop = ((Feature)candidate).getProperty(name);
                if(prop != null){
                    //just in case the type is not rigourously the same.
                    final Class clazz = jrf.getValueClass();
                    try {
                        return ObjectConverters.convert(prop.getValue(), clazz);
                    } catch (UnconvertibleObjectException e) {
                        Logging.recoverableException(null, CollectionDataSource.class, "getFieldValue", e);
                        // TODO - do we really want to ignore?
                    }
                }
            }catch(PropertyNotFoundException ex){
                return null;
            }

            //No field that match this name, looks like the feature type
            //used is not the exact one returned by the JasperReportservice.
            //This is not necessarly an error if for exemple someone ignore
            //some attribut from the template because he doesn't need them.
            return null;
        }else{
            return Bindings.resolve(candidate, name, jrf.getValueClass());
        }
    }

}
