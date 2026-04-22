package org.geotoolkit.processing.regridding;

import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.junit.Test;
import org.opengis.parameter.ParameterValueGroup;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link SwathRegridderProcess}.
 */
public class SwathRegridderProcessTest {

    @Test
    public void testDescriptorDiscovery() throws Exception {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                GeotkProcessingRegistry.NAME, SwathRegridderDescriptor.NAME);
        assertNotNull("Swath regridding descriptor not found", desc);
    }
    
    @Test
    public void testCreateProcess() throws Exception {
        final ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(
                GeotkProcessingRegistry.NAME, SwathRegridderDescriptor.NAME);

        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        
        // Populate minimal required inputs
        Path[] files = new Path[] { Paths.get("dummy.nc") };
        input.parameter(SwathRegridderDescriptor.FILES_NAME).setValue(files);
        input.parameter(SwathRegridderDescriptor.OUTPUT_PATH_NAME).setValue(Paths.get("output.nc"));
        input.parameter(SwathRegridderDescriptor.RESOLUTION_NAME).setValue(0.1);
        input.parameter(SwathRegridderDescriptor.RADIUS_OF_INFLUENCE_NAME).setValue(50000.0);
        input.parameter(SwathRegridderDescriptor.RESAMPLE_METHOD_NAME).setValue("NEAREST");
        
        final Process process = desc.createProcess(input);
        assertNotNull("Failed to create regridding process", process);
    }
}
