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

import org.geotoolkit.data.DataStore;
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
 *     <li>TYPE_NAME "type_name" feature type name to copy</li>
 *     <li>QUERY "query" query to use to retrieve the feature collection</li>
 * </ul>
 * outputs : none
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
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
    public static final ParameterDescriptor<DataStore> SOURCE_STORE =
            new DefaultParameterDescriptor("source_datastore", "The source datastore",
            DataStore.class, null, true);

    /**
     * Mandatory - Target datastore.
     */
    public static final ParameterDescriptor<DataStore> TARGET_STORE =
            new DefaultParameterDescriptor("target_datastore", "The target datastore",
            DataStore.class, null, true);

    /**
     * Mandatory - drop before insertion or not.
     */
    public static final ParameterDescriptor<Boolean> ERASE =
            new DefaultParameterDescriptor("erase", "Erase type if it already exist before insertion.",
            Boolean.class, false, true);

    /**
     * Mandatory - Feature type names to  copy. several names can be passed separated by commas.
     */
    public static final ParameterDescriptor<String> TYPE_NAME =
            new DefaultParameterDescriptor("type_name", "Name of the feature type to copy. '*' for all.",
            String.class, "*", true);

    /**
     * Optional - Query to use to retrieve FeatureCollection during the process.
     */
    public static final ParameterDescriptor<Query> QUERY =
            new DefaultParameterDescriptor("query", "Query used to get the FeatureCollection during the process.",
            Query.class, null, false);

    /**
     * Input Parameters
     */
    public static final ParameterDescriptorGroup INPUT_DESC =
            new DefaultParameterDescriptorGroup("InputParameters",
            new GeneralParameterDescriptor[]{SOURCE_STORE, TARGET_STORE, ERASE, TYPE_NAME, QUERY});

    /**
     * Output Parameters
     */
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new DefaultParameterDescriptorGroup("OutputParameters",
            new GeneralParameterDescriptor[]{});

    public static final ProcessDescriptor INSTANCE = new CopyDescriptor();

    private CopyDescriptor() {
        super(NAME, "Copy features from one datastore to another.", INPUT_DESC, OUTPUT_DESC);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new Copy(input);
    }
}
