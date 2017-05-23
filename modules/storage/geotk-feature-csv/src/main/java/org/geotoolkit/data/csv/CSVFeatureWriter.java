/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2015, Geomatys
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
package org.geotoolkit.data.csv;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.identity.Identifier;
import org.apache.sis.internal.feature.AttributeConvention;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.geotoolkit.data.csv.CSVFeatureStore.UTF8_ENCODING;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
class CSVFeatureWriter extends CSVFeatureReader implements FeatureWriter {

    private final WKTWriter wktWriter = new WKTWriter(2);
    private final Writer writer;
    private final Path writeFile;
    private Feature edited = null;
    private Feature lastWritten = null;
    private boolean appendMode = false;
    protected final Set<Identifier> deletedIds = new HashSet<>();
    protected final Set<Identifier> updatedIds = new HashSet<>();
    protected final Set<Identifier> addedIds = new HashSet<>();
    private final ReadWriteLock tempLock = new ReentrantReadWriteLock();
    private boolean closed = false;

    CSVFeatureWriter(CSVFeatureStore store, FeatureType featureType, ReadWriteLock lock) throws DataStoreException {
        super(store, featureType, false, lock);
        tempLock.writeLock().lock();
        try {
            writeFile = store.createWriteFile();
            writer = Files.newBufferedWriter(writeFile, UTF8_ENCODING, CREATE, WRITE);
            final String firstLine = store.createHeader(featureType);
            writer.write(firstLine);
            writer.write('\n');
            writer.flush();
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        } finally {
            tempLock.writeLock().unlock();
        }
    }

    @Override
    public Feature next() throws FeatureStoreRuntimeException {
        try {
            write();
            edited = super.next();
            appendMode = false;
        } catch (FeatureStoreRuntimeException ex) {
            //we reach append mode
            appendMode = true;
            edited = CSVUtils.defaultFeature(featureType, Integer.toString(inc++));
        }
        return edited;
    }

    @Override
    public void remove() {
        if (edited == null) {
            throw new FeatureStoreRuntimeException("No feature selected.");
        }
        deletedIds.add(FeatureExt.getId(edited));
        // mark the current feature as null, this will result in it not
        // being rewritten to the stream
        edited = null;
    }

    @Override
    public void write() throws FeatureStoreRuntimeException {
        if (edited == null || lastWritten == edited) {
            return;
        }
        lastWritten = edited;
        tempLock.writeLock().lock();
        try {
            int i = 0;
            int n = atts.length;
            for (PropertyType att : atts) {
                final Object value = edited.getPropertyValue(att.getName().toString());
                String str;
                if (value == null) {
                    str = "";
                } else if (AttributeConvention.isGeometryAttribute(att)) {
                    str = wktWriter.write((Geometry) value);
                } else {
                    if (value instanceof Date) {
                        str = TemporalUtilities.toISO8601((Date) value);
                    } else if (value instanceof Boolean) {
                        str = value.toString();
                    } else if (value instanceof CharSequence) {
                        str = value.toString();
                        //escape strings
                    } else {
                        str = ObjectConverters.convert(value, String.class);
                    }
                    if (str != null) {
                        final boolean escape = str.indexOf('"') >= 0 || str.indexOf(';') >= 0 || str.indexOf('\n') >= 0;
                        if (escape) {
                            str = "\"" + str.replace("\"", "\"\"") + "\"";
                        }
                    }
                }
                writer.append(str);
                if (i != n - 1) {
                    writer.append(store.getSeparator());
                }
                i++;
            }
            writer.append('\n');
            writer.flush();
            if (appendMode) {
                addedIds.add(FeatureExt.getId(edited));
            } else {
                updatedIds.add(FeatureExt.getId(edited));
            }
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } finally {
            tempLock.writeLock().unlock();
        }
    }

    @Override
    public void close() {
        if (closed) return;
        try {
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        }
        //close read iterator
        super.close();
        //flip files
        fileLock.writeLock().lock();
        tempLock.writeLock().lock();
        try {
            Files.move(writeFile, store.getFile(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new FeatureStoreRuntimeException(ex);
        } finally {
            fileLock.writeLock().unlock();
            tempLock.writeLock().unlock();
        }
        // Fire content change events only if we succeed replacing original file.
        store.fireDataChangeEvents(addedIds, updatedIds, deletedIds);
        closed = true;
    }

}
