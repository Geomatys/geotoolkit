/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.metadata.netcdf;

import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.io.IOException;
import javax.imageio.IIOException;
import ucar.nc2.NetcdfFile;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.identification.DataIdentification;
import org.opengis.metadata.extent.TemporalExtent;
import org.opengis.metadata.maintenance.ScopeCode;
import org.opengis.temporal.Instant;
import org.opengis.wrapper.netcdf.NetcdfMetadataTest;

import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;

import org.geotoolkit.image.io.plugin.NetcdfImageReader;
import org.junit.*;
import static org.opengis.test.Assert.*;
import static org.geotoolkit.test.Commons.getSingleton;


/**
 * Tests using the {@link NetcdfMetadataReader} class. This class inherits the tests from the
 * {@code geoapi-netcdf} module, and adds a some additional assertions for attributes not
 * parsed by the GeoAPI demo code.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfMetadataReaderTest extends NetcdfMetadataTest {
    /**
     * {@code true} if this instance is running an integration test.
     * <p>
     * <ul>
     *   <li>If {@code false} (the usual value), the metadata will be constructed directly by a
     *       {@link NetcdfMetadata} instance.</li>
     *   <li>If {@code true}, the metadata will be constructed by an {@link ImageCoverageReader},
     *       which will itself use a {@link NetcdfMetadata} under the hood and will add more
     *       information.</li>
     * </ul>
     */
    private boolean integrationTest;

    /**
     * Wraps the given NetCDF file into the Metadata object to be tested.
     * This method is invoked by the tests inherited from the {@code geoapi-test} module.
     *
     * @param  file The NetCDF file to wrap.
     * @return A metadata implementation created from the attributes found in the given file.
     * @throws IOException If an error occurred while wrapping the given NetCDF file.
     */
    @Override
    protected Metadata wrap(final NetcdfFile file) throws IOException {
        if (integrationTest) try {
            final NetcdfImageReader imageReader = new NetcdfImageReader(null);
            imageReader.setInput(file);
            final ImageCoverageReader coverageReader = new ImageCoverageReader();
            coverageReader.setInput(imageReader);
            final Metadata metadata = coverageReader.getMetadata();
            coverageReader.dispose();
            return metadata;
        } catch (CoverageStoreException e) {
            throw new IIOException(e.getLocalizedMessage(), e);
        }
        // Bellow is the "normal" test.
        final NetcdfMetadataReader ncISO = new NetcdfMetadataReader(file, null);
        return ncISO.read();
    }

    /**
     * Adds a set of common property values expected by every tests in this class.
     */
    private static void addCommonProperties(final Map<String,Object> expected, final boolean contact) {
        assertNull(expected.put("metadataStandardName", "ISO 19115-2 Geographic Information - Metadata Part 2 Extensions for imagery and gridded data"));
        assertNull(expected.put("metadataStandardVersion", "ISO 19115-2:2009(E)"));
        if (contact) {
            assertNull(expected.put("identificationInfo.pointOfContact.role", Role.POINT_OF_CONTACT));
            assertNull(expected.put("contact.role", Role.POINT_OF_CONTACT));
        }
    }

    /**
     * Tests a file that contains THREDDS metadata. This method inherits the tests defined in
     * GeoAPI, and adds some additional tests for attributes parsed by Geotk but not GeoAPI.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testTHREDDS() throws IOException {
        final Map<String,Object> expected = expectedProperties;
        addCommonProperties(expected, true);
        assertNull(expected.put("identificationInfo.citation.title",           "crm_v1.grd"));
        assertNull(expected.put("identificationInfo.citation.identifier.code", "crm_v1"));
        assertNull(expected.put("contentInfo.dimension.sequenceIdentifier",    "z"));
        super.testTHREDDS();
        assertEquals("hierarchyLevel", new HashSet<>(Arrays.asList(ScopeCode.DATASET, ScopeCode.SERVICE)),
                metadata.getHierarchyLevels());
        /*
         * In the Geotk case, the Metadata/Contact and Metadata/Identification/PointOfContact
         * proprties are not just equals - they are expected to be the exact same instance.
         */
        assertSame("identificationInfo.pointOfContact", getSingleton(metadata.getContacts()),
                getSingleton(getSingleton(metadata.getIdentificationInfo()).getPointOfContacts()));
        /*
         * We expect every properties to have been processed.
         */
        assertTrue(expectedProperties.toString(), expectedProperties.isEmpty());
        assertTrue(actualProperties  .toString(), actualProperties  .isEmpty());
    }

    /**
     * Tests a NetCDF binary file. This method inherits the tests defined in GeoAPI,
     * and adds some additional tests for attributes parsed by Geotk but not GeoAPI.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testNCEP() throws IOException {
        addCommonProperties(expectedProperties, true);
        super.testNCEP();
        assertSame("hierarchyLevel", ScopeCode.DATASET, getSingleton(metadata.getHierarchyLevels()));
        /*
         * In the Geotk case, the Metadata/Contact and Metadata/Identification/PointOfContact
         * proprties are not just equals - they are expected to be the exact same instance.
         */
        assertSame("identificationInfo.pointOfContact", getSingleton(metadata.getContacts()),
                getSingleton(getSingleton(metadata.getIdentificationInfo()).getPointOfContacts()));
        /*
         * Metadata / Data Identification / Temporal Extent.
         */
        final DataIdentification identification = (DataIdentification) getSingleton(metadata.getIdentificationInfo());
        final TemporalExtent text = getSingleton(getSingleton(identification.getExtents()).getTemporalElements());
        final Instant instant = (Instant) text.getExtent();
        // Can not test at this time, since it requires the geotk-temporal module.
        /*
         * Every properties found in the metadata should have been verified by this test case.
         * However the set of expected values may contains more entries than what we found in
         * the metadata object, because some values are found only in the "integrated" tests.
         * Consequently the set of expected values will be tested for emptiness only in the
         * "integrated" test case.
         */
        assertTrue(actualProperties.toString(), actualProperties.isEmpty());
    }

    /**
     * Same test than {@link #testNCEP()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test, with some additional metadata which were added by the coverage
     * reader.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testIntegratedNCEP() throws IOException, CoverageStoreException {
        integrationTest = true;
        testNCEP();
        // See the comment in testNCEP()
        assertTrue(expectedProperties.toString(), expectedProperties.isEmpty());
    }

    /**
     * Tests the Landsat file (binary format).
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testLandsat() throws IOException {
        addCommonProperties(expectedProperties, false);
        super.testLandsat();
        assertSame("hierarchyLevel", ScopeCode.DATASET, getSingleton(metadata.getHierarchyLevels()));
        /*
         * We expect every properties to have been processed.
         */
        assertTrue(expectedProperties.toString(), expectedProperties.isEmpty());
        assertTrue(actualProperties  .toString(), actualProperties  .isEmpty());
    }

    /**
     * Tests the "Current Icing Product" file (binary format).
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    @Override
    public void testCIP() throws IOException {
        final Map<String,Object> expected = expectedProperties;
        addCommonProperties(expected, true);
        super.testCIP();
        assertSame("hierarchyLevel", ScopeCode.DATASET, getSingleton(metadata.getHierarchyLevels()));
        /*
         * In the Geotk case, the Metadata/Contact and Metadata/Identification/PointOfContact
         * proprties are not just equals - they are expected to be the exact same instance.
         */
        assertSame("identificationInfo.pointOfContact", getSingleton(metadata.getContacts()),
                getSingleton(getSingleton(metadata.getIdentificationInfo()).getPointOfContacts()));
        /*
         * Every properties found in the metadata should have been verified by this test case.
         * However the set of expected values may contains more entries than what we found in
         * the metadata object, because some values are found only in the "integrated" tests.
         * Consequently the set of expected values will be tested for emptiness only in the
         * "integrated" test case.
         */
        assertTrue(actualProperties.toString(), actualProperties.isEmpty());
    }

    /**
     * Same test than {@link #testCIP()}, but now reading through a {@link ImageCoverageReader}.
     * This is an integration test.
     *
     * @throws IOException If the test file can not be read.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testIntegratedCIP() throws IOException, CoverageStoreException {
        integrationTest = true;
        testCIP();
        // See the comment in testCIP()
        assertTrue(expectedProperties.toString(), expectedProperties.isEmpty());
    }
}
