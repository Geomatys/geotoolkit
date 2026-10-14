/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.processing.resource;

import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.storage.memory.InMemoryGridCoverageResource;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;

import static org.junit.Assert.*;

/**
 * Tests the {@link org.geotoolkit.storage.ResourceProcessor} processes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResourceProcessTest {

    /**
     * Check resource processes are available in the registry.
     */
    @Test
    public void testRegistered() throws Exception {
        final GeotkProcessingRegistry registry = new GeotkProcessingRegistry();
        assertSame(CoverageAggregateDescriptor.INSTANCE, registry.getDescriptor(CoverageAggregateDescriptor.NAME));
        assertSame(FeatureSetConcatenateDescriptor.INSTANCE, registry.getDescriptor(FeatureSetConcatenateDescriptor.NAME));
    }

    /**
     * Concatenate two FeatureSet and expect the new feature set to contains all features.
     */
    @Test
    public void testConcatenate() throws Exception {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(String.class).setName("name");
        final FeatureType type = ftb.build();
        final Feature f1 = type.newInstance();
        f1.setPropertyValue("name", "a");
        final Feature f2 = type.newInstance();
        f2.setPropertyValue("name", "b");
        final FeatureSet fs1 = new InMemoryFeatureSet(type, List.of(f1));
        final FeatureSet fs2 = new InMemoryFeatureSet(type, List.of(f2));

        final ParameterValueGroup input = FeatureSetConcatenateDescriptor.INPUT_DESC.createValue();
        Parameters.castOrWrap(input).getOrCreate(FeatureSetConcatenateDescriptor.SOURCES).setValue(new FeatureSet[]{fs1, fs2});

        final ParameterValueGroup output = FeatureSetConcatenateDescriptor.INSTANCE.createProcess(input).call();
        final FeatureSet result = Parameters.castOrWrap(output).getValue(FeatureSetConcatenateDescriptor.RESULT);
        assertFalse(result.getIdentifier().isPresent());
        try (var stream = result.features(false)) {
            assertEquals(2, stream.count());
        }
    }

    /**
     * Check the ResourceProcessor properly set the new resource identifier.
     */
    @Test
    public void testResampleWithIdentifier() throws Exception {
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, 0, 10);
        env.setRange(1, 0, 10);
        final GridCoverage coverage = new GridCoverageBuilder()
                .setValues(new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_GRAY))
                .setDomain(env)
                .build();
        final GridCoverageResource source = new InMemoryGridCoverageResource(coverage);
        final GridGeometry target = new GridGeometry(new GridExtent(5, 5), env, org.apache.sis.coverage.grid.GridOrientation.HOMOTHETY);

        final Parameters input = Parameters.castOrWrap(CoverageResampleDescriptor.INPUT_DESC.createValue());
        input.getOrCreate(CoverageResampleDescriptor.SOURCE).setValue(source);
        input.getOrCreate(CoverageResampleDescriptor.TARGET).setValue(target);
        input.getOrCreate(CoverageResampleDescriptor.IDENTIFIER).setValue("resampled");

        final ParameterValueGroup output = CoverageResampleDescriptor.INSTANCE.createProcess(input).call();
        final GridCoverageResource result = Parameters.castOrWrap(output).getValue(CoverageResampleDescriptor.RESULT);
        assertEquals("resampled", result.getIdentifier().orElseThrow().toString());
        assertEquals(target.getExtent(), result.getGridGeometry().getExtent());
    }
}
