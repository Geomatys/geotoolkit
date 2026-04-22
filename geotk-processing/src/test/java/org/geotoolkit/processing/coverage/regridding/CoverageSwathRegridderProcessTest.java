package org.geotoolkit.processing.coverage.regridding;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.netcdf.NetcdfStore;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for {@link CoverageSwathRegridderProcess}.
 * Opens three swath NetCDF files as GridCoverageResource and regrids them
 * onto a regular WGS84 lat/lon grid.
 */
public class CoverageSwathRegridderProcessTest {

    private static final String[] TEST_FILES = {"data_1.nc", "data_2.nc", "data_3.nc"};

    @Test
    public void testRegrid() throws Exception {
        List<DataStore> stores = new ArrayList<>();
        List<GridCoverage> coverages = new ArrayList<>();

        try {
            // Open each NetCDF file and collect one GridCoverageResource per file
            for (String filename : TEST_FILES) {
                URL url = CoverageSwathRegridderProcessTest.class.getResource(filename);
                assertNotNull("Test resource not found: " + filename, url);

                DataStore store = DataStores.open(new StorageConnector(url));
                stores.add(store);

                NetcdfStore ncStore;
                if (store instanceof NetcdfStore) {
                    ncStore = (NetcdfStore) store;
                } else {
                    throw new IllegalStateException("Expected NetcdfStore but got " + store.getClass().getName());
                }

                Collection<Resource> resourcesInStore = ncStore.components();
                Collection<GridCoverageResource> tileResources = new ArrayList<>();
                for (Resource res : resourcesInStore) {
                    if (res instanceof GridCoverageResource) {
                        tileResources.add((GridCoverageResource) res);
                    }
                }
                assertFalse("No GridCoverageResource found in " + filename, tileResources.isEmpty());

                // Take the first resource from each file (one tile per file)
                coverages.add(tileResources.iterator().next().read(null));
            }

            // Look up and configure the process
            ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                    GeotkProcessingRegistry.NAME, CoverageSwathRegridderDescriptor.NAME);
            assertNotNull("CoverageSwathRegridder descriptor not found in registry", desc);

            ParameterValueGroup input = desc.getInputDescriptor().createValue();
            input.parameter(CoverageSwathRegridderDescriptor.COVERAGE_RESOURCES_NAME).setValue(coverages.toArray(new GridCoverage[0]));
            input.parameter(CoverageSwathRegridderDescriptor.RESOLUTION_NAME).setValue(0.05);
            input.parameter(CoverageSwathRegridderDescriptor.RADIUS_OF_INFLUENCE_NAME).setValue(50000.0);
            input.parameter(CoverageSwathRegridderDescriptor.RESAMPLE_METHOD_NAME).setValue("NEAREST");

            // Execute
            Process process = desc.createProcess(input);
            ParameterValueGroup output = process.call();

            // Verify result
            GridCoverage result = (GridCoverage) output
                    .parameter(CoverageSwathRegridderDescriptor.OUT_COVERAGE_NAME).getValue();
            assertNotNull("Regridded coverage must not be null", result);

            GridGeometry gg = result.getGridGeometry();
            assertNotNull("Result must have a grid geometry", gg);
            assertTrue("Result must have at least 1 column", gg.getExtent().getSize(0) > 0);
            assertTrue("Result must have at least 1 row",    gg.getExtent().getSize(1) > 0);

        } finally {
            for (DataStore store : stores) {
                store.close();
            }
        }
    }
}
