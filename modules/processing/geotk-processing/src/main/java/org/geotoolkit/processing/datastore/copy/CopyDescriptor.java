/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.datastore.copy;

import java.util.Date;
import org.apache.sis.parameter.ParameterBuilder;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Parameters description of Copy process.
 * name of the process : "copy"
 * inputs :
 * <ul>
 *     <li>SOURCE_STORE "source_datastore"</li>
 *     <li>TARGET_STORE "target_datastore"</li>
 *     <li>TARGET_SESSION "target_session"</li>
 *     <li>ERASE "erase" the data provider will be erased if true</li>
 *     <li>NEW_VERSION "new_version" the data provider will create a new version if true</li>
 *     <li>TYPE_NAME "type_name" feature type name to copy. Support a several type name separated by a comma.</li>
 *     <li>QUERY "query" query to use to retrieve the feature collection. Support wildcard '*'
 *     for retrieve all FeatureType of input FeatureStore and another wildcard '*' in BBOX filter query
 *     to use default geometry name to filter features.</li>
 * </ul>
 * outputs : none
 * <ul>
 *     <li>VERSION "version" date of the new version created if output store support versioning.</li>
 * </ul>
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public final class CopyDescriptor extends VectorDescriptor {

    /**
     * Process name : copy
     */
    public static final String NAME = "copy";

    /**
     * Mandatory - Source datastore.
     */
    public static final ParameterDescriptor<FeatureStore> SOURCE_STORE = new ParameterBuilder()
            .addName("source_datastore")
            .setRemarks("The source datastore")
            .setRequired(true)
            .create(FeatureStore.class, null);

    /**
     * Optional - Target datastore. 
     */
    public static final ParameterDescriptor<FeatureStore> TARGET_STORE = new ParameterBuilder()
            .addName("target_datastore")
            .setRemarks("The target datastore")
            .setRequired(false)
            .create(FeatureStore.class, null);

    /**
     * Optional - Target datastore.
     */
    public static final ParameterDescriptor<Session> TARGET_SESSION = new ParameterBuilder()
            .addName("target_session")
            .setRemarks("The target session of a datastore")
            .setRequired(false)
            .create(Session.class, null);
    
    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE = new ParameterBuilder()
            .addName("erase")
            .setRemarks("Erase type if it already exist before insertion.")
            .setRequired(true)
            .create(Boolean.class, false);

    /**
     * Mandatory - create a new version or append to last version.
     */
    public static final ParameterDescriptor<Boolean> NEW_VERSION = new ParameterBuilder()
            .addName("new_version")
            .setRemarks("Create a new version or append to last version.")
            .setRequired(true)
            .create(Boolean.class, false);
    
    /**
     * Mandatory - Feature type names to  copy. several names can be passed separated by commas.
     */
    public static final ParameterDescriptor<String> TYPE_NAME = new ParameterBuilder()
            .addName("type_name")
            .setRemarks("Name of the feature type to copy. '*' for all. Support a several type name separated by a comma.")
            .setRequired(true)
            .create(String.class, "*");

    /**
     * Optional - Query to use to retrieve FeatureCollection during the process.
     */
    public static final ParameterDescriptor<Query> QUERY = new ParameterBuilder()
            .addName("query")
            .setRemarks("Query used to get the FeatureCollection during the process. Support wildcard '*' "
                    + "for retrieve all FeatureType of input FeatureStore and another wildcard '*' in BBOX filter query "
                    + "to use default geometry name to filter features.")
            .setRequired(false)
            .create(Query.class, null);

    /**
     * Input Parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(
                    SOURCE_STORE, TARGET_STORE, TARGET_SESSION, ERASE, NEW_VERSION, TYPE_NAME, QUERY);

    /**
     * Optional - create version
     */
    public static final ParameterDescriptor<Date> VERSION =  new ParameterBuilder()
            .addName("version")
            .setRemarks("Version date in target FeatureStore created "
                + "during copy if supported.")
            .setRequired(false)
            .create(Date.class, null);
    
    /**
     * Output Parameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(VERSION);

    public static final ProcessDescriptor INSTANCE = new CopyDescriptor();

    private CopyDescriptor() {
        super(NAME, "Copy features from one featurestore to another.", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Copy(input);
    }
}
