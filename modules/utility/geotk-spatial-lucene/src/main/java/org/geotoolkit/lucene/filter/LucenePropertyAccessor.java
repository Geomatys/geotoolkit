/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.lucene.filter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.document.Document;

import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.geometry.jts.SRIDGenerator;


/**
 * Simple accessor for lucene documents.
 *
 * This class is not thread safe.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LucenePropertyAccessor implements PropertyAccessor {
    private static final GeometryFactory GF = new GeometryFactory();

    private static final ThreadLocal<WKBReader> THREAD_LOCAL = new ThreadLocal<WKBReader>(){

        @Override
        protected WKBReader initialValue() {
            return new WKBReader(GF);
        }

    };

    LucenePropertyAccessor() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean canHandle(Object object, String xpath, Class target) {
        return object instanceof Document;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object get(Object object, String xpath, Class target) throws IllegalArgumentException {
        Document doc = (Document) object;

        if(xpath.equals(LuceneOGCFilter.GEOMETRY_FIELD_NAME)){

            //if the requested field is the geometry we must grab the crs field too
            //to generate the geometry
            final byte[] compact = doc.getBinaryValue(LuceneOGCFilter.GEOMETRY_FIELD_NAME);
            if (compact == null) {
                return null;
            }
            final int srid = SRIDGenerator.toSRID(compact, 0);

            //skip the 5 crs byte;
            final ByteArrayInStream stream = new ByteArrayInStream(compact);
            stream.read(new byte[5]);
            
            try {
                final Geometry geom = THREAD_LOCAL.get().read(stream);
                geom.setSRID(srid);
                return geom;
            } catch (IOException ex) {
                Logger.getLogger(LucenePropertyAccessor.class.getName()).log(Level.WARNING, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(LucenePropertyAccessor.class.getName()).log(Level.WARNING, null, ex);
            }
        }

        return doc.get(xpath);
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public void set(Object object, String xpath, Object value, Class target) throws IllegalArgumentException {
        throw new UnsupportedOperationException("You are not allowed to change a property value on lucene document.");
    }

}
