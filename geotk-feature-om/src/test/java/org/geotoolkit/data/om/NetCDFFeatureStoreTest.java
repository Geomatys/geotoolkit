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


package org.geotoolkit.data.om;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.DefaultParameterValueGroup;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStore;
import org.geotoolkit.data.om.netcdf.NetcdfObservationStore;
import org.geotoolkit.storage.AbstractReadingTests;
import org.geotoolkit.data.om.netcdf.NetcdfObservationStoreFactory;
import org.geotoolkit.data.om.netcdf.NetcdfObservationStore;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.feature.OMFeatureTypes;
import org.geotoolkit.util.NamesExt;
import org.junit.BeforeClass;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NetCDFFeatureStoreTest extends AbstractReadingTests {

    private static DataStore store;
    private static final Set<GenericName> names = new HashSet<>();
    private static final List<AbstractReadingTests.ExpectedResult> expecteds = new ArrayList<>();

    @BeforeClass
    public static void init() throws Exception {
        final Path f = IOUtilities.getResourceAsPath("org/geotoolkit/sql/test-trajectories.nc");

        DefaultParameterValueGroup parameters = (DefaultParameterValueGroup) NetcdfObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        parameters.getOrCreate(NetcdfObservationStoreFactory.IDENTIFIER).setValue("observationFile");
        parameters.getOrCreate(NetcdfObservationStoreFactory.FILE_PATH).setValue(f.toUri().toURL());

        store = new NetcdfObservationStore(parameters);
        
        final GenericName name = NamesExt.create(OMFeatureTypes.OM_NAMESPACE, "test-trajectories");
        names.add(name);

        int size = 4;
        GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:27582"));
        env.setRange(0, -51.78333, 27.816);
        env.setRange(1, -19.802, 128.6);

        FeatureType type = OMFeatureTypes.buildSamplingFeatureFeatureType(name);
        final AbstractReadingTests.ExpectedResult res = new AbstractReadingTests.ExpectedResult(name,
                type, size, env);
        expecteds.add(res);
    }

    @Override
    protected DataStore getDataStore() {
        return store;
    }

    @Override
    protected Set<GenericName> getExpectedNames() {
        return names;
    }

    @Override
    protected List<AbstractReadingTests.ExpectedResult> getReaderTests() {
        return expecteds;
    }
}
