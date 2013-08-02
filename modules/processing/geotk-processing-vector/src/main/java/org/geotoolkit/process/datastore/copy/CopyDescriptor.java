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
package org.geotoolkit.process.datastore.copy;

import java.util.Date;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.vector.VectorDescriptor;
import org.opengis.parameter.GeneralParameterDescriptor;
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
 * @module pending
 */
public final class CopyDescriptor extends VectorDescriptor {

    /**
     * Process name : copy
     */
    public static final String NAME = "copy";

    /**
     * Mandatory - Source datastore.
     */
    public static final ParameterDescriptor<FeatureStore> SOURCE_STORE =
            new DefaultParameterDescriptor("source_datastore", "The source datastore",
            FeatureStore.class, null, true);

    /**
     * Mandatory - Target datastore.
     */
    public static final ParameterDescriptor<FeatureStore> TARGET_STORE =
            new DefaultParameterDescriptor("target_datastore", "The target datastore",
            FeatureStore.class, null, true);

    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE =
            new DefaultParameterDescriptor("erase", "Erase type if it already exist before insertion.",
            Boolean.class, false, true);

    /**
     * Mandatory - create a new version or append to last version.
     */
    public static final ParameterDescriptor<Boolean> NEW_VERSION =
            new DefaultParameterDescriptor("new_version", "Create a new version or append to last version.",
            Boolean.class, false, true);
    
    /**
     * Mandatory - Feature type names to  copy. several names can be passed separated by commas.
     */
    public static final ParameterDescriptor<String> TYPE_NAME =
            new DefaultParameterDescriptor("type_name", "Name of the feature type to copy. '*' for all. Support a several type name separated by a comma.",
            String.class, "*", true);

    /**
     * Optional - Query to use to retrieve FeatureCollection during the process.
     */
    public static final ParameterDescriptor<Query> QUERY =
            new DefaultParameterDescriptor("query", "Query used to get the FeatureCollection during the process. Support wildcard '*' "
                    + "for retrieve all FeatureType of input FeatureStore and another wildcard '*' in BBOX filter query "
                    + "to use default geometry name to filter features.",
            Query.class, null, false);

    /**
     * Input Parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{SOURCE_STORE, TARGET_STORE, ERASE, NEW_VERSION, TYPE_NAME, QUERY});

    /**
     * Optional - create version
     */
    public static final ParameterDescriptor<Date> VERSION = 
            new DefaultParameterDescriptor("version", "Version date in target FeatureStore created "
            + "during copy if supported.", Date.class, null, false);
    
    /**
     * Output Parameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{VERSION});

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
