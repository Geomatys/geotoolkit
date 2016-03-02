/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geotoolkit.referencing.factory.epsg;

import java.io.IOException;
import java.io.BufferedReader;
import java.util.ServiceLoader;
import org.apache.sis.setup.InstallationResources;
import org.apache.sis.test.TestUtilities;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Test {@link ScriptProvider}.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class ScriptProviderTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests fetching the resources.
     *
     * @throws IOException if an error occurred while reading a resource.
     */
    @Test
    public void testResources() throws IOException {
        final ScriptProvider provider = (ScriptProvider) TestUtilities.getSingleton(
                ServiceLoader.load(InstallationResources.class));

        assertTrue(provider.getLicense("EPSG", null, "text/plain").contains("IOGP"));
        assertTrue(provider.getLicense("EPSG", null, "text/html" ).contains("IOGP"));

        final String[] names = provider.getResourceNames("EPSG");
        assertArrayEquals(new String[] {"Prepare", "Tables.sql", "Data.sql", "FKeys.sql", "Finish"}, names);
        for (int i=0; i<names.length; i++) {
            try (final BufferedReader in = provider.openScript("EPSG", i)) {
                // Just verify that we can read.
                assertFalse(in.readLine().isEmpty());
            }
        }
    }
}
