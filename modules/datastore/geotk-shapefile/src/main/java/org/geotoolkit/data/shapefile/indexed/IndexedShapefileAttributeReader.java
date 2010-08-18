/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.indexed;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.data.shapefile.ShapefileAttributeReader;
import org.geotoolkit.data.dbf.IndexedDbaseFileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;

import org.opengis.feature.type.PropertyDescriptor;

/**
 * An AttributeReader implementation for shape. Pretty straightforward. <BR/>The
 * default geometry is at position 0, and all dbf columns follow. <BR/>The dbf
 * file may not be necessary, if not, just pass null as the DbaseFileReader
 * @module pending
 */
public class IndexedShapefileAttributeReader extends ShapefileAttributeReader
        implements RecordNumberTracker {

    protected Iterator<Data> goodRecs;

    private int recno;

    private Data next;

    private CloseableCollection<Data> closeableCollection;

    public IndexedShapefileAttributeReader( List<? extends PropertyDescriptor> attributes,
            ShapefileReader shp, IndexedDbaseFileReader dbf,
            CloseableCollection<Data> col, Iterator<Data> goodRecs) {
        this(attributes.toArray(new PropertyDescriptor[attributes.size()]), shp, dbf, col, goodRecs);
    }

    /**
     * Create the shape reader
     * 
     * @param atts - the attributes that we are going to read.
     * @param shp - the shape reader, required
     * @param dbf - the dbf file reader. May be null, in this case no
     *              attributes will be read from the dbf file
     * @param goodRecs Collection of good indexes that match the query.
     */
    public IndexedShapefileAttributeReader(PropertyDescriptor[] atts,
            ShapefileReader shp, IndexedDbaseFileReader dbf,
            CloseableCollection<Data> col, Iterator<Data> goodRecs) {
        super(atts, shp, dbf);
        this.goodRecs = goodRecs;
        this.closeableCollection = col;
        this.recno = 0;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if( closeableCollection!=null ){
                closeableCollection.closeIterator(goodRecs);
                closeableCollection.close();
            }
            goodRecs = null;
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        return hasNextInternal();
    }

    private boolean hasNextInternal() throws IOException{
        if (this.goodRecs != null) {
            if (next != null)
                return true;
            if (this.goodRecs.hasNext()) {

                next = (Data) goodRecs.next();
                this.recno = ((Integer) next.getValue(0)).intValue();
                return true;
            }
            return false;
        }

        return super.hasNext();
    }

    @Override
    public void next() throws IOException {
        if (!hasNextInternal())
            throw new IndexOutOfBoundsException("No more features in reader");
        if (this.goodRecs != null) {
            this.recno = ((Integer) next.getValue(0)).intValue();

            if (dbf != null) {
                ((IndexedDbaseFileReader) dbf).goTo(this.recno);
            }

            Long l = (Long) next.getValue(1);
            shp.goTo((int) l.longValue());
            next = null;
        } else {
            this.recno++;
        }

        super.next();
    }

    @Override
    public int getRecordNumber() {
        return this.recno;
    }

}
