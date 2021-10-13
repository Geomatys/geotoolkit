/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.nio.charset.Charset;
import java.util.Iterator;

import org.geotoolkit.data.shapefile.ShapefileAttributeReader;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.index.CloseableCollection;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.AttributeType;

/**
 * An AttributeReader implementation for shape. Pretty straightforward. <BR/>The
 * default geometry is at position 0, and all dbf columns follow. <BR/>The dbf
 * file may not be necessary, if not, just pass null as the DbaseFileReader
 * @module
 */
public class IndexedShapefileAttributeReader <T extends Iterator<ShpData>> extends ShapefileAttributeReader
        implements RecordNumberTracker {

    protected final T goodRecs;
    private final CloseableCollection<ShpData> closeableCollection;
    private int recno;
    private ShpData next;

    /**
     * Create the shape reader
     *
     * @param locker - to aquiere different readers and writers.
     * @param atts - the attributes that we are going to read.
     * @param read3D - for shp reader, read 3d coordinate or not.
     * @param memoryMapper - for shp and dbf reader
     * @param resample - for shp reader, decimate coordinates while reading
     * @param readDBF - true to open a dbf reader
     * @param charset - for dbf reader
     * @param estimateRes - avoid reading geometry if under this resolution,
     *                      while return an approximate geometry
     * @param goodRecs Collection of good indexes that match the query.
     */
    public IndexedShapefileAttributeReader(final AccessManager locker,
            final AttributeType[] atts, final boolean read3D, final boolean memoryMapped,
            final double[] resample, final boolean readDBF, final Charset charset,
            final double[] estimateRes, final CloseableCollection<ShpData> col, final T goodRecs)
            throws IOException, DataStoreException {
        super(locker,atts,read3D,memoryMapped,resample,readDBF,charset,estimateRes);
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
                next = goodRecs.next();
                recno = next.v1;
                return true;
            }
            return false;
        }

        return super.hasNext();
    }

    @Override
    public void next() throws IOException {
        moveToNextShape();
        moveToNextDbf();
    }

    protected void moveToNextShape() throws IOException{
        if (!hasNextInternal()){
            throw new IndexOutOfBoundsException("No more features in reader");
        }

        if (this.goodRecs != null) {
            shp.goTo((int) next.v2);
            next = null;
            nextShape();
        } else {
            this.recno++;
            super.next();
        }

    }

    protected void moveToNextDbf() throws IOException{
        if (this.goodRecs != null && dbf != null) {
            dbf.goTo(this.recno);
            nextDbf();
        }
    }

    @Override
    public int getRecordNumber() {
        return this.recno;
    }

}
