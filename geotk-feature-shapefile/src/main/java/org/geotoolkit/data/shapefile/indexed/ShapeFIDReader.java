/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.data.shapefile.FeatureIDReader;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.FeatureType;

/**
 * Reader that returns FeatureIds in a quick fashion.
 *
 * @author Tommaso Nolli
 * @module
 */
public class ShapeFIDReader implements FeatureIDReader {
    protected static final String CLOSE_MESG = "Close has already been called on this FIDReader";
    private boolean opened;
    private IndexedShapefileAttributeReader reader;
    private int len;
    protected StringBuffer buffer;

    public ShapeFIDReader(final String typeName,
            final IndexedShapefileAttributeReader reader) {
        buffer = new StringBuffer(typeName);
        buffer.append('.');
        len = typeName.length() + 1;
        this.opened = true;
        this.reader = reader;
    }

    public ShapeFIDReader(final FeatureType featureType,
            final IndexedShapefileAttributeReader reader) {
        this(featureType.getName().tip().toString(), reader);
    }

    /**
     * Release any resources associated with this reader
     */
    @Override
    public void close() {
        this.opened = false;
    }

    /**
     * This method always returns true, since it is built with a
     * <code>ShapefileDataStore.Reader</code> you have to call
     * <code>ShapefileDataStore.Reader.hasNext()</code>
     *
     * @return always return <code>true</code>
     * @throws IOException If closed
     */
    @Override
    public boolean hasNext() throws DataStoreException {
        if (!this.opened) {
            throw new DataStoreException(CLOSE_MESG);
        }

        /*
         * In DefaultFIDReader this is always called after
         * atttributesReader.hasNext so, as we use the same attributeReader,
         * we'll return true
         */
        return true;
    }

    /**
     * Read the feature id.
     *
     * @return the Feature Id
     * @throws IOException If closed
     */
    @Override
    public String next() throws DataStoreException {
        if (!this.opened) {
            throw new DataStoreException(CLOSE_MESG);
        }

        buffer.delete(len, buffer.length());
        buffer.append(reader.getRecordNumber() - 1);

        return buffer.toString();
    }
}
