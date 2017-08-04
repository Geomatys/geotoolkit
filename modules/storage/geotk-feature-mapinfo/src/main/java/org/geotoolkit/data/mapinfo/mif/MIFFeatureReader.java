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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
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
        final StringBuilder patternBuilder = new StringBuilder("\\s*");
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

    final Function[] converters;
    final Map<String, Integer> propertiesToRead;

    public MIFFeatureReader(final MIFManager parent, final FeatureType ft) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Parent reader", parent);
        master = parent;
        readType = ft;
        final Collection<? extends PropertyType> properties = master.getMIDType().getProperties(true);

        int i = 0;
        propertiesToRead = new HashMap<>(properties.size());
        converters = new Function[properties.size()];
        for (final PropertyType pt : properties) {
            final String pName = pt.getName().toString();
            try {
                converters[i] = FeatureExt.castOrUnwrap(readType.getProperty(pName))
                        .map(AttributeType::getValueClass)
                        .map(MIFFeatureReader::findConverter)
                        .orElse(Function.identity());
                propertiesToRead.put(pName, i);
            } catch (PropertyNotFoundException e) {
                LOGGER.log(Level.FINER, e, () -> "Filtered property " + pName);
            }
            i++;
        }

        readMid = !properties.isEmpty();

        try {
            final PropertyType defaultGeometry = FeatureExt.getDefaultGeometry(ft);
            readMif = true;
        } catch (PropertyNotFoundException | IllegalStateException e) {
            LOGGER.log(Level.FINER, "Geometries will be ignored", e);
            readMif = false;
        }
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
        try {
            checkScanners();
        } catch (DataStoreException | IOException ex) {
            throw new FeatureStoreRuntimeException("Cannot read data", ex);
        }

        Feature resFeature = null;

        // We check the MIF file first, because it will define the feature count to reach the next good typed data.
        if (readMif) {
            String currentPattern;
            while (mifScanner.hasNextLine()) {
                currentPattern = mifScanner.findInLine(GEOMETRY_ID_PATTERN);
                if (currentPattern == null) {
                    // Empty or non-compliant line. We just ignore it.
                    LOGGER.log(Level.FINE, "Invalid line : "+mifScanner.findInLine(".*"));
                } else {
                    final MIFUtils.GeometryType geomType = MIFUtils.getGeometryType(currentPattern);
                    if (geomType == null) {
                        LOGGER.log(Level.WARNING, "Unrecognized geometry flag in MIF file : " + currentPattern);
                    } else {
                        resFeature = geomType.binding.buildType(master.getMifCRS(), readType).newInstance();
                        try {
                            geomType.readGeometry(mifScanner, resFeature, master.getTransform());
                        } catch (Exception ex) {
                            LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
                        }
                        break;
                        // We must check if we're on a Geometry naming line to increment the counter of past geometries.
                    }
                }
                mifScanner.nextLine();
            }
        }

        // No geometry has been read.
        if (resFeature == null) {
            resFeature = readType.newInstance();
        }

        if (readMid) {
            //parse MID line.
            while (midCounter < mifCounter) {
                midScanner.nextLine();
                midCounter++;
            }
            final String line = midScanner.nextLine();
            final CharSequence[] split = CharSequences.split(line, master.mifDelimiter);
            for (final Map.Entry<String, Integer> entry : propertiesToRead.entrySet()) {
                final Integer idx = entry.getValue();
                try {
                    resFeature.setPropertyValue(entry.getKey(), converters[idx].apply(split[idx]));
                } catch (RuntimeException e) {
                    Logging.recoverableException(LOGGER, MIFFeatureReader.class, "next", e);
                }
            }
            midCounter++;
        }

        if (readMif) {
            mifCounter++;
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
            checkScanners();
        } catch (IOException e) {
            // If we can't access source files, maybe we're in creation mode, so we just say we can't find next.
            return false;
        } catch (DataStoreException e) {
            throw new FeatureStoreRuntimeException(e);
        }

        if (readMif) {
            // Check the MapInfo geometry typename to see if there's some next in the file.
            mifNext = mifScanner.hasNext(GEOMETRY_ID_PATTERN);
        }

        // Once we know the number of the next geometry data, we can check if we can go as far in the mid file.
        if (readMid) {
            for (; midCounter < mifCounter; midCounter++) {
                if (midScanner.hasNextLine()) {
                    midScanner.nextLine();
                } else {
                    break;
                }
            }
            midNext = midScanner.hasNextLine();
        }

        if (readMid && !readMif) {
            return midNext;
        } else if (readMif && !readMid) {
            return mifNext;
        } else
            return (midNext && mifNext);
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
                mifScanner = new Scanner(mifStream, master.getCharset().name());
                repositionMIFReader();
            }
        }

        if(readMid) {
            if(midStream == null) {
                midStream = IOUtilities.open(master.getMIDPath());
            }
            if(midScanner == null) {
                midScanner = new Scanner(midStream, master.getCharset().name());

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

    private static Function<String, ?> findConverter(final Class dataType) {
        if (String.class.isAssignableFrom(dataType))
            return Function.identity();
        else if (Date.class.isAssignableFrom(dataType)) {
            // We don't use geotoolkit to parse date, because we have to use a specific date pattern.
            return new DateConverter();
        } else if (LocalDate.class.isAssignableFrom(dataType)) {
            return new TemporalConverter<>(TemporalQueries.localDate());
        } else if (LocalTime.class.isAssignableFrom(dataType)) {
            return new TemporalConverter<>(TemporalQueries.localTime());
        } else if (LocalDateTime.class.isAssignableFrom(dataType)) {
            return new TemporalConverter<>(LocalDateTime::from);
        } else {
            return ObjectConverters.find(String.class, dataType);
        }
    }

    private static class DateConverter implements Function<String, Date> {

        final SimpleDateFormat format = new SimpleDateFormat();
        int previousLength = -1;

        @Override
        public Date apply(String t) {
            final int length = t.length();
            if (previousLength != length) {
                previousLength = length;
                adaptFormat(length);
            }

            try {
                return format.parse(t);
            } catch (ParseException ex) {
                Logging.recoverableException(LOGGER, MIFFeatureReader.class, "next", ex);
                return null;
            }
        }

        private void adaptFormat(final int patternLength) {
            if (patternLength > 14) {
                format.applyPattern("yyyyMMddHHmmss.SSS");
            } else if (patternLength == 14) {
                format.applyPattern("yyyyMMddHHmmss");
            } else {
                format.applyPattern("yyyyMMdd");
            }
        }
    }

    private static class TemporalConverter<T> implements Function<String, T> {

        final DateTimeFormatter format = new DateTimeFormatterBuilder().appendPattern("yyyyMMdd[HHmmss.SSS]")
                .parseLenient()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
                .toFormatter();

        final TemporalQuery<T> query;

        TemporalConverter(final TemporalQuery query) {
            this.query = query;
        }

        @Override
        public T apply(String t) {
            return format.parse(t, query);
        }
    }
}
