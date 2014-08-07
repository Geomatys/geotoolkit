/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.image;

import java.util.List;
import java.util.Properties;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.RegistryElementDescriptor;

import org.geotoolkit.coverage.GridSampleDimension;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Setup}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public final strictfp class SetupTest {
    /**
     * Tests the deregistration using the internal API. We have to register
     * again after this test in order to allow other tests to run correctly.
     */
    @Test
    @Ignore("Setup class will be removed.")
    public void testInteral() {
        /*
         * In current Geotk implementation, the "SampleTranscoder" operation is registered
         * when the GridSampleDimension class is initialized. Create a dummy instance just
         * for making sure that this class has been initialized.
         */
        assertFalse(new GridSampleDimension("Dummy").toString().isEmpty());
        assertTrue(countGeotkOperations() != 0);
        final Setup setup = new Setup();
        setup.shutdown();
        assertTrue(countGeotkOperations() == 0);
        /*
         * Re-register the JAI operations, so tests depending on them can continue to work.
         */
        setup.initialize(null, true);
        assertTrue(countGeotkOperations() != 0);
    }

    /**
     * Same test than above, but using the public API.
     */
    @Test
    @Ignore("Setup class will be removed.")
    public void testPublic() {
        assertFalse(new GridSampleDimension("Dummy").toString().isEmpty());
        org.geotoolkit.lang.Setup.initialize(null);
        assertTrue(countGeotkOperations() != 0);
        org.geotoolkit.lang.Setup.shutdown();
        assertTrue(countGeotkOperations() == 0);
        try {
            org.geotoolkit.lang.Setup.initialize(null);
            fail("Reinitialization shall not be allowed without \"force=true\" argument.");
        } catch (IllegalStateException e) {
            // This the expected exception.
        }
        final Properties properties = new Properties();
        properties.put("force", "true");
        org.geotoolkit.lang.Setup.initialize(properties);
        assertTrue(countGeotkOperations() != 0);
    }

    /**
     * Counts the number of Geotk operations registered in the default JAI instance.
     */
    private static int countGeotkOperations() {
        int count = 0;
        final OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
        for (final String mode : registry.getRegistryModes()) {
            @SuppressWarnings("unchecked")
            final List<RegistryElementDescriptor> descriptors = registry.getDescriptors(mode);
            for (final RegistryElementDescriptor descriptor : descriptors) {
                final String operationName = descriptor.getName();
                if (operationName.startsWith("org.geotoolkit.")) {
                    count++;
                }
            }
        }
        return count;
    }
}
