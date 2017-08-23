/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.Resource;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.CoverageType;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A coverage store based on a folder of timed images. See {@link TimedCoverageFactory}
 * for a more complete description.
 *
 * @author Alexis Manin (Geomatys)
 */
public class TimedCoverageStore extends AbstractCoverageStore {

    final Path rootDir;
    final Function<Path, TemporalAccessor> fileNameParser;

    final TimedResource resource;

    public TimedCoverageStore(final ParameterValueGroup group) throws DataStoreException {
        super(group);
        final URI path = parameters.getMandatoryValue(TimedCoverageFactory.PATH);
        try {
            rootDir = IOUtilities.toPath(path);
        } catch (IOException e) {
            throw new DataStoreException("Cannot initialize harvesting directory", e);
        }

        if (!Files.isDirectory(rootDir)) {
            throw new IllegalArgumentException("Given path is not directory : "+rootDir);
        }

        if (!Files.isReadable(rootDir)) {
            throw new IllegalArgumentException("We cannot read data from given directory : "+rootDir);
        }

        if (!Files.isWritable(rootDir)) {
            throw new IllegalArgumentException("We cannot edit source directory content : "+rootDir);
        }

        fileNameParser = extractParser(parameters);

        resource = new TimedResource(this, rootDir, parameters.getMandatoryValue(TimedCoverageFactory.DELAY));
    }

    @Override
    public DataStoreFactory getFactory() {
        return DataStores.getFactoryById(TimedCoverageFactory.NAME);
    }

    @Override
    public void close() throws DataStoreException {
        try {
            resource.close();
        } catch (IOException ex) {
            throw new DataStoreException("Cannot stop directory survey properly", ex);
        }
    }

    @Override
    public Resource getRootResource() throws DataStoreException {
        return resource;
    }

    @Override
    public CoverageType getType() {
        return CoverageType.GRID;
    }

    static Function<Path, TemporalAccessor> extractParser(final Parameters params) {
        final Pattern regex = Pattern.compile(
                params.getMandatoryValue(TimedCoverageFactory.NAME_PATTERN)
        );
        final int timeIndex = params.intValue(TimedCoverageFactory.TIME_INDEX);
        final String timePattern = params.getValue(TimedCoverageFactory.TIME_FORMAT);

        final Long defaultMilliOfDay = params.getValue(TimedCoverageFactory.DEFAULT_MILLI_OF_DAY);
        final Long defaultOffset = params.getValue(TimedCoverageFactory.DEFAULT_OFFSET_SECONDS);

        // Created format defaults time and zone in order to manage local dates.
        final DateTimeFormatter timeFormat = new DateTimeFormatterBuilder()
                .appendPattern(timePattern)
                .parseDefaulting(ChronoField.MILLI_OF_DAY, defaultMilliOfDay)
                .parseDefaulting(ChronoField.OFFSET_SECONDS, defaultOffset)
                .toFormatter();

        return input -> {
            final String fileName = input.getFileName().toString();
            final Matcher match = regex.matcher(fileName);
            if (!match.find()) {
                throw new IllegalArgumentException(String.format("Input file name (%s) does not respect expected pattern (%s).", fileName, regex.pattern()));
            }

            String time = match.group(timeIndex);
            if (time == null || (time = time.trim()).isEmpty()) {
                throw new IllegalArgumentException(String.format("Given file name (%s) does not contain any valid identifier", fileName));
            }

            return timeFormat.parse(time);
        };
    }
}
