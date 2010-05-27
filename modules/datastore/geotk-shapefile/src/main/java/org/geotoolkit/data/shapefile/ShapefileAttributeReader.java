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
import java.util.List;

import org.geotoolkit.data.AbstractPropertyReader;
import org.geotoolkit.data.shapefile.dbf.DbaseFileHeader;
import org.geotoolkit.data.shapefile.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.util.Converters;

import org.opengis.feature.type.AttributeDescriptor;

/**
 * An AttributeReader implementation for Shapefile. Pretty straightforward.
 * <BR/>The default geometry is at position 0, and all dbf columns follow.
 * <BR/>The dbf file may not be necessary, if not, just pass null as the
 * DbaseFileReader
 * @module pending
 */
public class ShapefileAttributeReader extends AbstractPropertyReader {

    protected final boolean[] narrowing;
    protected ShapefileReader shp;
    protected DbaseFileReader dbf;
    protected DbaseFileReader.Row row;
    protected ShapefileReader.Record record;

    public ShapefileAttributeReader(List<AttributeDescriptor> atts,
            ShapefileReader shp, DbaseFileReader dbf) {
        this(atts.toArray(new AttributeDescriptor[atts.size()]), shp, dbf);
    }

    /**
     * Create the shapefile reader
     * 
     * @param atts - the attributes that we are going to read.
     * @param shp - the shapefile reader, required
     * @param dbf - the dbf file reader. May be null, in this case no
     *              attributes will be read from the dbf file
     */
    public ShapefileAttributeReader(AttributeDescriptor[] atts,
            ShapefileReader shp, DbaseFileReader dbf) {
        super(atts);
        this.shp = shp;
        this.dbf = dbf;

        //the attribut descriptor might define types that are mare restrictive
        //then what the readers can do.
        narrowing = new boolean[atts.length];
        if(dbf != null){
            final DbaseFileHeader header = dbf.getHeader();
            for(int i=1;i<atts.length;i++){
                narrowing[i] = (atts[i].getType().getBinding() != header.getFieldClass(i-1));
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        try {
            if (shp != null) {
                shp.close();
            }

            if (dbf != null) {
                dbf.close();
            }
        } finally {
            row = null;
            record = null;
            shp = null;
            dbf = null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
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
    @Override
    public void next() throws IOException {
        record = shp.nextRecord();

        if (dbf != null) {
            row = dbf.readRow();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object read(int param) throws IOException,IndexOutOfBoundsException {

        if(param == 0){
            return record.shape();
        }else{
            if (row != null) {
                if(narrowing[param-1]){
                    //must procede to a retype
                    return Converters.convert(row.read(param - 1), metaData[param].getType().getBinding());
                }else{
                    return row.read(param - 1);
                }
            }
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void read(Object[] buffer) throws IOException {
        buffer[0] = record.shape();
        for(int i=0,n=getPropertyCount()-1; i<n; i++){
            if(narrowing[i+1]){
                //must procede to a retype
                buffer[i+1] = Converters.convert(row.read(i), metaData[i+1].getType().getBinding());
            }else{
                buffer[i+1] = row.read(i);
            }
        }
    }
}
