/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson;

import org.apache.sis.metadata.iso.DefaultIdentifier;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultServiceIdentification;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFileFeatureStoreFactory;
import org.geotoolkit.data.FileFeatureStoreFactory;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.util.Collections;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.storage.DataType;
import org.geotoolkit.storage.DefaultFactoryMetadata;
import org.geotoolkit.storage.FactoryMetadata;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class GeoJSONFeatureStoreFactory extends AbstractFileFeatureStoreFactory implements FileFeatureStoreFactory {

    /** factory identification **/
    public static final String NAME = "geojson";
    public static final DefaultServiceIdentification IDENTIFICATION;
    static {
        IDENTIFICATION = new DefaultServiceIdentification();
        final Identifier id = new DefaultIdentifier(NAME);
        final DefaultCitation citation = new DefaultCitation(NAME);
        citation.setIdentifiers(Collections.singleton(id));
        IDENTIFICATION.setCitation(citation);
    }
    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

    public static final String ENCODING = "UTF-8";

    /**
     * Optional
     */
    public static final ParameterDescriptor<Integer> COORDINATE_ACCURACY = new ParameterBuilder()
            .addName("coordinate_accuracy")
            .addName(Bundle.formatInternational(Bundle.Keys.coordinate_accuracy))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.coordinate_accuracy_remarks))
            .setRequired(false)
            .create(Integer.class, 7);


    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName("GeoJSONParameters").createGroup(
                IDENTIFIER, PATH, COORDINATE_ACCURACY);

    @Override
    public Identification getIdentification() {
        return IDENTIFICATION;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.datastoreTitle);
    }

    /**
     * Describes the type of data the datastore returned by this factory works
     * with.
     *
     * @return String a human readable description of the type of restore
     *         supported by this datastore.
     */
    @Override
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.datastoreDescription);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getFileExtensions() {
        return new String[] {".json", ".geojson", ".topojson"};
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeoJSONFeatureStore open(final ParameterValueGroup params) throws DataStoreException {
        ensureCanProcess(params);
        return new GeoJSONFeatureStore(params);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeoJSONFeatureStore create(final ParameterValueGroup params) throws DataStoreException {
        return open(params);
    }

    @Override
    public FactoryMetadata getMetadata() {
        return new DefaultFactoryMetadata(DataType.VECTOR, true, true, true, false, GEOMS_ALL);
    }

}
