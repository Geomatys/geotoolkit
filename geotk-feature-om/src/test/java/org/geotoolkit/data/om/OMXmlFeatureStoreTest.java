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

import java.io.InputStream;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.DefaultParameterValueGroup;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStore;
import org.geotoolkit.data.om.xml.XmlObservationStore;
import org.geotoolkit.storage.AbstractReadingTests;
import org.geotoolkit.data.om.xml.XmlObservationStoreFactory;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.observation.feature.OMFeatureTypes;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OMXmlFeatureStoreTest extends AbstractReadingTests{

    private static DataStore store;
    private static final Set<GenericName> names = new HashSet<>();
    private static final List<AbstractReadingTests.ExpectedResult> expecteds = new ArrayList<>();
    static{
        try{

            final Path f = IOUtilities.getResourceAsPath("org/geotoolkit/sql/observation1.xml");
            DefaultParameterValueGroup parameters = (DefaultParameterValueGroup) XmlObservationStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
            parameters.getOrCreate(XmlObservationStoreFactory.IDENTIFIER).setValue("observationXmlFile");
            parameters.getOrCreate(XmlObservationStoreFactory.FILE_PATH).setValue(f.toUri().toURL());

            store = new XmlObservationStore(parameters);

            final String nsOM = "http://www.opengis.net/sampling/1.0";
            final GenericName name = NamesExt.create(nsOM, "observation1");
            names.add(name);

            int size = 1;
            GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:27582"));
            env.setRange(0, 65400.0, 65400.0);
            env.setRange(1, 1731368.0, 1731368.0);

            FeatureType type = OMFeatureTypes.buildSamplingFeatureFeatureType(name);

            final AbstractReadingTests.ExpectedResult res = new AbstractReadingTests.ExpectedResult(name,
                    type, size, env);
            expecteds.add(res);

        }catch(Exception ex){
            ex.printStackTrace();
        }
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

    public static InputStream getResourceAsStream(final String url) {
        final ClassLoader cl = getContextClassLoader();
        return cl.getResourceAsStream(url);
    }

    public static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
}
