/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.nio.IOUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.util.logging.Logging;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;

/**
 * MIF reader which is designed to browse data AND ONLY data, it's to say geometry data from MIF file, and all data from
 * MID file.
 *
 * @author Alexis Manin (Geomatys)
 * @date : 22/02/13
 */
public class MIFFeatureReader implements FeatureReader {

    private final static Logger LOGGER = Logging.getLogger("org.geotoolkit.data.mapinfo.mif");

    private static final Pattern GEOMETRY_ID_PATTERN;
    static {
        final StringBuilder patternBuilder = new StringBuilder();
        final MIFUtils.GeometryType[] types = MIFUtils.GeometryType.values();
        patternBuilder.append(types[0].name());
        for(int i = 1 ; i < types.length; i++) {
            patternBuilder.append('|').append(types[i].name());
        }
        GEOMETRY_ID_PATTERN = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    /**
     * MIF/MID input connections.
     */
    private InputStream mifStream = null;
    private InputStream midStream = null;

    /**
     * MIF/MID file readers.
     */
    private Scanner mifScanner = null;
    private Scanner midScanner = null;

    /**
     * Counters : feature counter (Mid and mif lines are not equal for the same feature).
     */
    int mifCounter = 0;
    int midCounter = 0;

    /**
     * booleans to check if we just read mid file (feature type doesn't contain any geometry) or just MIF file
     * (geometries only), or both.
     */
    boolean readMid = false;
    boolean readMif = false;

    final MIFManager master;
    final FeatureType readType;
    final PropertyType[] baseTypeAtts;

    final MIFUtils.GeometryType geometryType;
    final String geometryId;
    final Pattern geometryPattern;

    public MIFFeatureReader(MIFManager parent, String typeName) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Parent reader", parent);
        master = parent;
        readType = master.getType(typeName);
        if(readType.equals(master.getBaseType()) || readType.getSuperTypes().contains(master.getBaseType())) {
            readMid = true;
        }

        if(FeatureExt.hasAGeometry(readType)) {
            readMif = true;
            geometryType = MIFUtils.identifyFeature(readType);
            geometryId = geometryType.name();
            geometryPattern = Pattern.compile(geometryId, Pattern.CASE_INSENSITIVE);
        } else {
            geometryType = null;
            geometryId = null;
            geometryPattern = null;
        }

        baseTypeAtts = master.getBaseType().getProperties(true).toArray(new PropertyType[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType() {
        return readType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Feature next() throws FeatureStoreRuntimeException {

        Feature resFeature = null;

        try {
            checkScanners();

            resFeature = readType.newInstance();

            // We check the MIF file first, because it will define the feature count to reach the next good typed data.
            if(readMif) {
                String currentPattern;
                while(mifScanner.hasNextLine()) {
                    currentPattern = mifScanner.findInLine(GEOMETRY_ID_PATTERN);
                    if(geometryId.equalsIgnoreCase(currentPattern)) {
                        //geometryType.readGeometry(mifScanner, resFeature, master.getTransform());
                        try{
                            geometryType.readGeometry(mifScanner, resFeature, master.getTransform());
                        }catch(Exception ex){
                            LOGGER.log(Level.WARNING,ex.getLocalizedMessage(),ex);
                        }
                        break;
                        // We must check if we're on a Geometry naming line to increment the counter of past geometries.
                    } else if(currentPattern != null) {
                        mifCounter++;
                    }
                    mifScanner.nextLine();
                }
            }

            if(readMid) {
                //parse MID line.
                while(midCounter < mifCounter) {
                    midScanner.nextLine();
                    midCounter++;
                }
                final String line = midScanner.nextLine();
                final CharSequence[] split = CharSequences.split(line, master.mifDelimiter);
                for (int i = 0; i < split.length; i++) {
                    //AttributeType att = baseType.getType(i);
                    AttributeType att = null;
                    try{
                        att = (AttributeType)baseTypeAtts[i];
                    }catch(Exception ex){
                        LOGGER.finer(ex.getMessage());
                    }
                    if(att == null) continue;
                    Object value = null;
                    try{
                        if (split[i].length() != 0) {
                            // We don't use geotoolkit to parse date, because we have to use a specific date pattern.
                            if(Date.class.isAssignableFrom(att.getValueClass())) {
                                SimpleDateFormat format = new SimpleDateFormat();
                                if(split[i].length() > 14) {
                                    format.applyPattern("yyyyMMddHHmmss.SSS");
                                } else if(split[i].length() == 14) {
                                    format.applyPattern("yyyyMMddHHmmss");
                                } else {
                                     format.applyPattern("yyyyMMdd");
                                }
                                value = format.parse(split[i].toString());
                            } else try {
                                value = ObjectConverters.convert(split[i], att.getValueClass());
                            } catch (UnconvertibleObjectException e) {
                                Logging.recoverableException(LOGGER, MIFFeatureReader.class, "next", e);
                                value = null;
                                // TODO - do we really want to ignore the problem?
                            }
                        }
                        resFeature.setPropertyValue(att.getName().toString(),value);
                    }catch(Exception ex){
                        LOGGER.finer(ex.getMessage());
                    }
                }
                midCounter++;
            }

            if(readMif) {
                mifCounter++;
            }
        } catch (Exception ex) {
            throw new FeatureStoreRuntimeException("Can't reach next feature with type name " + readType.getName().tip(), ex);
        }
        return resFeature;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() throws FeatureStoreRuntimeException {
        boolean midNext = false;
        boolean mifNext = false;

        try {
            try {
            checkScanners();
            } catch (IOException e) {
                // If we can't access source files, maybe we're in creation mode, so we just say we can't find next.
                return false;
            }

            if (readMif) {
                // Check the MapInfo geometry typename to see if there's some next in the file.
                while(mifScanner.hasNext()) {
                    if (mifScanner.hasNext(geometryPattern)) {
                        mifNext = true;
                        break;
                    } else {
                        // We must check if we're on a Geometry naming line to increment the counter of past geometries.
                        if(mifScanner.hasNext(GEOMETRY_ID_PATTERN)) {
                            mifCounter++;
                        }
                    }

                    mifScanner.next();
                }
            }

            // Once we know the number of the next geometry data, we can check if we can go as far in the mid file.
            if (readMid) {
                for( ; midCounter < mifCounter ; midCounter++) {
                    if(midScanner.hasNextLine()) {
                        midScanner.nextLine();
                    } else {
                        break;
                    }
                }
                midNext = midScanner.hasNextLine();
            }

        } catch (Exception ex) {
            throw new FeatureStoreRuntimeException(ex);
        }

        if (readMid && !readMif) {
            return midNext;
        } else if (readMif && !readMid) {
            return mifNext;
        } else return (midNext && mifNext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

        mifCounter = 0;
        midCounter = 0;

        if (mifScanner != null) {
            mifScanner.close();
            mifScanner = null;

        }
        if (midScanner != null) {
            midScanner.close();
            midScanner = null;

        }

        try {
            if (mifStream != null) {
                mifStream.close();
                mifStream = null;
            }

            if (midStream != null) {
                midStream.close();
                midStream = null;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Input connections to MIF/MID files can't be closed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("MIF feature iterator is for reading only.");
    }


    /**
     * Check if we have an open access to mif/mid files. If not, try to get one.
     *
     * @throws DataStoreException If we're unable to access files.
     */
    private void checkScanners() throws DataStoreException, IOException {
        if(readMif) {
            if(mifStream == null) {
                mifStream = IOUtilities.open(master.getMIFPath());
            }
            if(mifScanner == null) {
                mifScanner = new Scanner(mifStream, MIFUtils.DEFAULT_CHARSET);
                repositionMIFReader();
            }
        }

        if(readMid) {
            if(midStream == null) {
                midStream = IOUtilities.open(master.getMIDPath());
            }
            if(midScanner == null) {
                midScanner = new Scanner(midStream, MIFUtils.DEFAULT_CHARSET);

                // Reposition the scanner.
                int midPosition = 0;
                while (midPosition < midCounter) {
                    midScanner.nextLine();
                    midPosition++;
                }
            }
        }
    }

    /**
     * Check the current feature counter, and move the mif scanner according to that counter. It's useful if MIF scanner
     * have been reset but not iterator position.
     *
     * WARNING : YOU <b>MUST NOT</b> USE THIS FUNCTION IF SCANNERS ARE NOT EARLY PLACED IN THE INPUT FILE.
     */
    private void repositionMIFReader() throws DataStoreException {
         // Check for column pattern
        while (mifScanner.hasNextLine()) {
            if (mifScanner.hasNext("(?i)\\s*"+MIFUtils.HeaderCategory.COLUMNS.name())) {
                mifScanner.next();
                if (mifScanner.hasNextShort()) {
                    short mifColumnsCount = mifScanner.nextShort();
                    for (int i = 0 ; i < mifColumnsCount ; i++) {
                        mifScanner.nextLine();
                    }
                    break;
                } else {
                    throw new DataStoreException("MIF Columns has no attribute count specified.");
                }
            }
            mifScanner.nextLine();
        }

        // Go to the first feature.
        while (mifScanner.hasNextLine()) {
            if (mifScanner.hasNext(GEOMETRY_ID_PATTERN)) {
                break;
            }
            mifScanner.nextLine();
        }

        //Browse file until we're well placed.
        int mifPosition = 0;
        while (mifPosition < mifCounter) {
            while (mifScanner.hasNextLine()) {
                mifScanner.nextLine();
                if (mifScanner.hasNext(GEOMETRY_ID_PATTERN)) {
                    mifPosition++;
                    break;
                }
            }
        }
    }

}
