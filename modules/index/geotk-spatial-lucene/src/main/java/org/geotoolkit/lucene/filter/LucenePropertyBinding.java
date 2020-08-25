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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ByteArrayInStream;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.lucene.document.Document;
import org.apache.lucene.util.BytesRef;

import org.geotoolkit.filter.binding.AbstractBinding;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.apache.sis.util.logging.Logging;


/**
 * Simple accessor for lucene documents.
 *
 * This class is not thread safe.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LucenePropertyBinding extends AbstractBinding<Document> {
    private static final GeometryFactory GF = new GeometryFactory();

    private static final ThreadLocal<WKBReader> THREAD_LOCAL = new ThreadLocal<WKBReader>(){

        @Override
        protected WKBReader initialValue() {
            return new WKBReader(GF);
        }

    };

    public LucenePropertyBinding() {
        super(Document.class, 10);
    }

    @Override
    public boolean support(String xpath) {
        return true;
    }

    @Override
    public <T> T get(Document doc, String xpath, Class<T> target) throws IllegalArgumentException {

        if(xpath.equals(LuceneOGCSpatialQuery.GEOMETRY_FIELD_NAME)){

            //if the requested field is the geometry we must grab the crs field too
            //to generate the geometry
            final BytesRef compact = doc.getBinaryValue(LuceneOGCSpatialQuery.GEOMETRY_FIELD_NAME);
            if (compact == null) {
                return null;
            }
            final int srid = SRIDGenerator.toSRID(compact.bytes, 0);

            //skip the 5 crs byte;
            final ByteArrayInStream stream = new ByteArrayInStream(compact.bytes);
            stream.read(new byte[5]);

            try {
                final Geometry geom = THREAD_LOCAL.get().read(stream);
                geom.setSRID(srid);
                return (T) geom;
            } catch (IOException | ParseException ex) {
                Logging.getLogger("org.geotoolkit.lucene.filter").log(Level.WARNING, null, ex);
            }
        }

        return (T) doc.get(xpath);
    }

    @Override
    public void set(Document candidate, String xpath, Object value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("You are not allowed to change a property value on lucene document.");
    }

}
