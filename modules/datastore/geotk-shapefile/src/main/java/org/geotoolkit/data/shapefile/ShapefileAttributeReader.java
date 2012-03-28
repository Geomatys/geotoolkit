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
package org.geotoolkit.data.shapefile;

import java.io.IOException;
import java.nio.charset.Charset;
import org.geotoolkit.data.dbf.DbaseFileHeader;
import org.geotoolkit.data.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.Converters;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * An AttributeReader implementation for Shapefile. Pretty straightforward.
 * <BR/>The default geometry is at position 0, and all dbf columns follow.
 * <BR/>The dbf file may not be necessary, if not, just pass null as the
 * DbaseFileReader
 * @module pending
 */
public class ShapefileAttributeReader {

    private final AccessManager locker;
    
    protected final PropertyDescriptor[] metaData;
    protected final boolean[] narrowing;
    protected final int[] attributIndex;
    protected ShapefileReader shp;
    protected DbaseFileReader dbf;
    protected DbaseFileReader.Row row;
    protected ShapefileReader.Record record;
    private boolean closed = false;

    //feature bbox must be bigger than this, otherwise shape geometry is only estimated
    private final boolean estimateRes;
    private final double estimateX;
    private final double estimateY;

    /**
     * Create the shapefile attribute reader
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
     */
    public ShapefileAttributeReader(final AccessManager locker,
            final PropertyDescriptor[] atts, final boolean read3D, final boolean memoryMapped,
            final double[] resample, final boolean readDBF, final Charset charset,
            final double[] estimateRes) throws IOException, DataStoreException {
        ArgumentChecks.ensureNonNull("locker", locker);
        this.locker = locker;
        this.metaData = atts;
        this.shp = locker.getSHPReader(true, memoryMapped, read3D, resample);
        this.dbf = locker.getDBFReader(memoryMapped, charset);
        if(estimateRes != null){
            this.estimateRes = true;
            this.estimateX = estimateRes[0];
            this.estimateY = estimateRes[1];
        }else{
            this.estimateRes = false;
            this.estimateX = 0;
            this.estimateY = 0;
        }

        //the attribut descriptor might define types that are mare restrictive
        //then what the readers can do.
        narrowing = new boolean[atts.length];
        attributIndex = new int[atts.length];
        if(dbf != null){
            final DbaseFileHeader header = dbf.getHeader();
            attLoop:
            for(int i=0;i<atts.length;i++){
                final String attName = atts[i].getName().getLocalPart();
                //attribut field
                for(int k=0;k<header.getNumFields();k++){
                    final String fieldName = header.getFieldName(k);
                    if(fieldName.equals(attName)){
                        narrowing[i] = (atts[i].getType().getBinding() != header.getFieldClass(k));
                        attributIndex[i] = k;
                        continue attLoop;
                    }
                }
                //geom field
                attributIndex[i] = -1;
            }
        }else{
            if(atts.length != 1){
                throw new IllegalStateException("Reader has been asked to read "+atts.length+" attributs, but no dbf reader given.");
            }
            //geom field
            attributIndex[0] = -1;
        }
    }

    public AccessManager getLocker() {
        return locker;
    }

    /**
     * {@inheritDoc }
     */
    public void close() throws IOException {
        if(!closed){
            closed = true;
            try {
                locker.disposeReaderAndWriters();
            } finally {
                row = null;
                record = null;
                shp = null;
                dbf = null;
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    public boolean hasNext() throws IOException {

        if(shp.hasNext()){
            if(dbf != null){
                if(dbf.hasNext()){
                    return true;
                }else{
                    //shape files has more data then the dbf ? file or reader is corrupted
                    throw new IOException("Shp has extra record");
                }
            }else{
                //no attributs, only shapes
                return true;
            }
        }else{
            if(dbf != null){
                if(!dbf.hasNext()){
                    return false;
                }else{
                    //dbf has more data then the shape ? file or reader is corrupted
                    throw new IOException("Dbf has extra record");
                }
            }else{
                //no attributs, only shapes
                return false;
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    public void next() throws IOException {
        nextShape();
        nextDbf();
    }

    protected void nextShape() throws IOException {
        record = shp.nextRecord();
    }

    protected void nextDbf() throws IOException {
        if (dbf != null) {
            row = dbf.next();
        }
    }

    /**
     * {@inheritDoc }
     */
    public Object read(final int param) throws IOException,IndexOutOfBoundsException {

        final int index = attributIndex[param];
        if(index == -1){
            if(estimateRes &&
               !(estimateX <= (record.maxX - record.minX) || estimateY <= (record.maxY - record.minY))){
                //read estimated shape
                return record.estimatedShape();
            }else{
                //read full shape
                return record.shape();
            }
        }else if(row != null) {
            if(narrowing[param]){
                //must procede to a retype
                return Converters.convert(row.read(index), metaData[param].getType().getBinding());
            }else{
                return row.read(index);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    public void read(final Object[] buffer) throws IOException {
        for(int i=0;i<metaData.length;i++){
            buffer[i] = read(i);
        }
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return metaData;
    }

    public int getPropertyCount() {
        return metaData.length;
    }
}
