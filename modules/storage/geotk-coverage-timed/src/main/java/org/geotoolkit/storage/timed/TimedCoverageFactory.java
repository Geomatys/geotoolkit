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

import java.net.URI;
import java.util.Collections;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import static org.geotoolkit.storage.AbstractDataStoreFactory.createFixedIdentifier;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A coverage store which aims to harvest a directory filled with georeferenced
 * image files, each one containing a date in its name. All the images are
 * exposed as a single coverage with a time dimension simulated using dates
 * found in file names.
 *
 * @author Alexis Manin (Geomatys)
 */
public class TimedCoverageFactory extends AbstractCoverageStoreFactory {

    /** factory identification **/
    public static final String NAME = "timed-files";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .setRemarks("The path to the folder to read data from.")
            .setRequired(true)
            .create(URI.class, null);

    public static final ParameterDescriptor<String> NAME_PATTERN = new ParameterBuilder()
            .addName("pattern")
            .setRemarks("A regex to use to find date-time text into file names.")
            .setRequired(true)
            .create(String.class, null);

    public static final ParameterDescriptor<Integer> TIME_INDEX = new ParameterBuilder()
            .addName("time-index")
            .setRemarks("The group index to use to extract date-time text from a file name using \"pattern\" regex.")
            .setRequired(true)
            .createBounded(0, Integer.MAX_VALUE, 0);

    public static final ParameterDescriptor<String> TIME_FORMAT = new ParameterBuilder()
            .addName("time-format")
            .setRemarks("The pattern to use for date and time parsing. Must be compatible with Java 8 DateTimeFormatter.")
            .setRequired(true)
            .create(String.class, null);

    public static final ParameterDescriptor<Long> DELAY = new ParameterBuilder()
            .addName("delay")
            .setRemarks("Delay to apply before inserting an image, in milliseconds.")
            .setRequired(true)
            .createBounded(Long.class, 0l, Long.MAX_VALUE, 5000l);

    public static final ParameterDescriptor<Long> DEFAULT_MILLI_OF_DAY = new ParameterBuilder()
            .addName("milli-of-day")
            .setRemarks("When parsing the time, if only a date is found (no time), we'll use the start of day as default. You can override this behavior by passing the millisecond of the day to use instead of defaulting to start of day.")
            .setRequired(false)
            .createBounded(Long.class, 0l, Long.MAX_VALUE, 0l);

    public static final ParameterDescriptor<Long> DEFAULT_OFFSET_SECONDS = new ParameterBuilder()
            .addName("offset-seconds")
            .setRemarks("When parsing the time, if neither zone id nor offset is provided, we'll use a default offset (unit = seconds). You can override the default value using the current parameter.")
            .setRequired(false)
            .createBounded(Long.class, 0l, Long.MAX_VALUE, 0l);

    public static final ParameterDescriptorGroup PARAMETERS = new ParameterBuilder()
            .addName("timed-file-parameters")
            .createGroup(IDENTIFIER, PATH, NAME_PATTERN, TIME_INDEX, TIME_FORMAT, DELAY, DEFAULT_MILLI_OF_DAY, DEFAULT_OFFSET_SECONDS);

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.COVERAGE, true, false, false);
    }

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return "A coverage store which aims to harvest a directory filled with "
                + "georeferenced image files, each one containing a date in its "
                + "name. All the images are exposed as a single coverage with a "
                + "time dimension simulated using dates found in file names.";
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS;
    }

    @Override
    public DataStore open(ParameterValueGroup params) throws DataStoreException {
        return new TimedCoverageStore(params);
    }

    @Override
    public DataStore create(ParameterValueGroup params) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
