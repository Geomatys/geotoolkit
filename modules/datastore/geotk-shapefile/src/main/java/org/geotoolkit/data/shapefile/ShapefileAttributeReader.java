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

import org.geotoolkit.data.AbstractAttributeIO;
import org.geotoolkit.data.AttributeReader;
import org.geotoolkit.data.shapefile.dbf.DbaseFileReader;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * An AttributeReader implementation for Shapefile. Pretty straightforward.
 * <BR/>The default geometry is at position 0, and all dbf columns follow.
 * <BR/>The dbf file may not be necessary, if not, just pass null as the
 * DbaseFileReader
 */
public class ShapefileAttributeReader extends AbstractAttributeIO implements
        AttributeReader {

    protected ShapefileReader shp;
    protected DbaseFileReader dbf;
    protected DbaseFileReader.Row row;
    protected ShapefileReader.Record record;
    int cnt;

    public ShapefileAttributeReader(List<AttributeDescriptor> atts,
            ShapefileReader shp, DbaseFileReader dbf) {
        this(atts.toArray(new AttributeDescriptor[0]), shp, dbf);
    }

    /**
     * Create the shapefile reader
     * 
     * @param atts -
     *                the attributes that we are going to read.
     * @param shp -
     *                the shapefile reader, required
     * @param dbf -
     *                the dbf file reader. May be null, in this case no
     *                attributes will be read from the dbf file
     */
    public ShapefileAttributeReader(AttributeDescriptor[] atts,
            ShapefileReader shp, DbaseFileReader dbf) {
        super(atts);
        this.shp = shp;
        this.dbf = dbf;
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
        int n = shp.hasNext() ? 1 : 0;

        if (dbf != null) {
            n += (dbf.hasNext() ? 2 : 0);
        }

        if ((n == 3) || ((n == 1) && (dbf == null))) {
            return true;
        }

        if (n == 0) {
            return false;
        }

        throw new IOException(((n == 1) ? "Shp" : "Dbf") + " has extra record");
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
    public Object read(int param) throws IOException,
            java.lang.ArrayIndexOutOfBoundsException {
        switch (param) {
        case 0:
            return record.shape();

        default:

            if (row != null) {
                return row.read(param - 1);
            }

            return null;
        }
    }
}
