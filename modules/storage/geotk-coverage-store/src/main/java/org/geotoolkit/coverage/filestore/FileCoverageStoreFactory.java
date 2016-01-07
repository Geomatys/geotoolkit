/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.AbstractCoverageStoreFactory;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;
import org.geotoolkit.storage.coverage.Bundle;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage Store which rely on standard java readers and writers.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FileCoverageStoreFactory extends AbstractCoverageStoreFactory{

    /** factory identification **/
    public static final String NAME = "coverage-file";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    /**
     * Mandatory - the folder path
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.path))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.path_remarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Mandatory - the image reader type.
     * Use AUTO if type should be detected automatically.
     */
    public static final ParameterDescriptor<String> TYPE;
    static{
        final LinkedList<String> validValues = new LinkedList<>(getReaderTypeList());
        validValues.add("AUTO");
        Collections.sort(validValues);

        TYPE = new ParameterBuilder()
            .addName("type")
            .addName(Bundle.formatInternational(Bundle.Keys.type))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.type_remarks))
            .setRequired(true)
            .createEnumerated(String.class, validValues.toArray(new String[validValues.size()]), "AUTO");
    }

    public static final ParameterDescriptor<String> PATH_SEPARATOR = new ParameterBuilder()
            .addName("pathSeparator")
            .addName(Bundle.formatInternational(Bundle.Keys.pathSeparator))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.pathSeparator_remarks))
            .setRequired(false)
            .create(String.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("FileCoverageStoreParameters").createGroup(
                IDENTIFIER,PATH,TYPE,NAMESPACE, PATH_SEPARATOR);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileTitle);
    }

    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public CoverageStore open(ParameterValueGroup params) throws DataStoreException {
        if(!canProcess(params)){
            throw new DataStoreException("Can not process parameters.");
        }
        try {
            return new FileCoverageStore(params);
        } catch (IOException | URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public CoverageStore create(ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    /**
     * ONLY FOR INTERNAL USE.
     * 
     * List all available formats.
     */
    public static LinkedList<String> getReaderTypeList() {
        ImageIO.scanForPlugins();
        final LinkedList<String> formatsDone = new LinkedList<>();
        for (String format : ImageIO.getReaderFormatNames()) {
            formatsDone.add(format);
        }

        return formatsDone;
    }

    /**
     * Selects the longest format string. If two of them
     * have the same length, favor the one in upper case.
     *
     * @param current    The previous longest format string, or {@code null} if none.
     * @param candidate  The format string which may be longer than the previous one.
     * @return The format string which is the longest one up to date.
     */
    private static String longest(final String current, final String candidate) {
        if (current != null) {
            final int dl = candidate.length() - current.length();
            if (dl < 0 || (dl == 0 && candidate.compareTo(current) >= 0)) {
                return current;
            }
        }
        return candidate;
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.GRID, true, true, true);
    }
    
}
