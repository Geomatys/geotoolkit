/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.image.io.plugin.yaml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlCategory;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlImageInfo;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleDimension;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform1D;


/**
 * Test reading / writing yaml image informations.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 * @see YamlFiles
 * @see YamlBuilder
 * @see YamlCategory
 * @see YamlImageInfo
 * @see YamlReaderBuilder
 * @see YamlWriterBuilder
 * @see YamlSampleCategory
 * @see YamlSampleDimension
 */
public class YamlUtilsTest {

    /**
     * Simply yaml binding of only one {@link SampleDimension}.
     *
     * @throws IOException if problem during yaml file writing.
     */
    @Test
    public void oneSampleDimensionTest() throws IOException {

        final String dumpResult =   "---\n"+
                                    "version: \"1.0\"\n" +
                                    "sampleDimension:\n" +
                                    "- description: \"band_0\"\n" +
                                    "  categories:\n" +
                                    "  - name: \"No data\"\n" +
                                    "    value: 0.0\n" +
                                    "  - name: \"data\"\n" +
                                    "    minSampleValue: 1.0\n" +
                                    "    isMinInclusive: true\n" +
                                    "    maxSampleValue: 255.0\n" +
                                    "    isMaxInclusive: true\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n";


        final File fil = File.createTempFile("yamlTest", "txt");

        final SampleDimension.Builder b = new SampleDimension.Builder();
        b.addQualitative(null, 0.0);
        b.addQuantitative("data", NumberRange.create(1d, true, 255d, true), (MathTransform1D) MathTransforms.identity(1), null);
        final SampleDimension sampDimb0 = b.setName("band_0").build();

        final List<SampleDimension> dims = new ArrayList<>();
        dims.add(sampDimb0);

        Assert.assertEquals(dumpResult, YamlFiles.write(dims));

        YamlFiles.write(dims, fil.toPath());

        List<SampleDimension> lsd = YamlFiles.read(fil, Double.class);
        Assert.assertEquals(lsd.get(0), sampDimb0);

        fil.delete();
    }

    /**
     * Test binding of multi {@link SampleDimension} with multi {@link Category} build.
     *
     * @throws IOException if problem during yaml file writing.
     */
    @Test
    public void multiSampleDimensionTest() throws IOException {

        final String dumpResult =   "---\n" +
                                    "version: \"1.0\"\n" +
                                    "sampleDimension:\n" +
                                    "- description: \"band_0\"\n" +
                                    "  categories:\n" +
                                    "  - name: \"No data\"\n" +
                                    "    value: 0.0\n" +
                                    "  - name: \"data\"\n" +
                                    "    minSampleValue: 1.0\n" +
                                    "    isMinInclusive: true\n" +
                                    "    maxSampleValue: 255.0\n" +
                                    "    isMaxInclusive: true\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n" +
                                    "- description: \"band_1\"\n" +
                                    "  categories:\n" +
                                    "  - name: \"data\"\n" +
                                    "    minSampleValue: 0.0\n" +
                                    "    isMinInclusive: true\n" +
                                    "    maxSampleValue: 125.0\n" +
                                    "    isMaxInclusive: true\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n" +
                                    "  - name: \"No data\"\n" +
                                    "    minSampleValue: 125.0\n" +
                                    "    isMinInclusive: false\n" +
                                    "    maxSampleValue: 254.0\n" +
                                    "    isMaxInclusive: true\n" +
                                    "  - name: \"data\"\n" +
                                    "    value: 255.0\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n" +
                                    "- description: \"band_2\"\n" +
                                    "  categories:\n" +
                                    "  - name: \"data\"\n" +
                                    "    minSampleValue: 0.0\n" +
                                    "    isMinInclusive: true\n" +
                                    "    maxSampleValue: 254.0\n" +
                                    "    isMaxInclusive: false\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n" +
                                    "  - name: \"No data\"\n" +
                                    "    value: 254.0\n" +
                                    "  - name: \"data\"\n" +
                                    "    value: 255.0\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n";

        final File fil = File.createTempFile("yamlTest", "txt");
        final MathTransform1D identity = (MathTransform1D) MathTransforms.identity(1);

        //-- Sample dimension band 0
        final SampleDimension.Builder b = new SampleDimension.Builder();
        b.addQualitative(null, 0.0);
        b.addQuantitative("data", NumberRange.create(1d, true, 255d, true), identity, null);
        final SampleDimension sampDimb0 = b.setName("band_0").build();

        //-- Sample dimension band 1
        b.clear();
        b.addQualitative(null, NumberRange.create(125d, false, 254d, true));
        b.addQuantitative("data", NumberRange.create(0d, true, 125d, true), identity, null);
        b.addQuantitative("data", NumberRange.create(255d, true, 255d, true), identity, null);
        final SampleDimension sampDimb1 = b.setName("band_1").build();

        //-- Sample dimension band 1
        b.clear();
        b.addQualitative(null, 254d);
        b.addQuantitative("data", NumberRange.create(0d, true, 254d, false), identity, null);
        b.addQuantitative("data", NumberRange.create(255d, true, 255d, true), identity, null);
        final SampleDimension sampDimb2 = b.setName("band_2").build();

        List<SampleDimension> sampDims = new ArrayList<>();
        sampDims.add(sampDimb0);
        sampDims.add(sampDimb1);
        sampDims.add(sampDimb2);

        YamlFiles.write(sampDims, fil.toPath());
//        System.out.println(YamlFiles.dump(yamBuild));

        Assert.assertEquals(dumpResult, YamlFiles.write(sampDims));

        List<SampleDimension> lsd = YamlFiles.read(fil, Double.class);
        int i = 0;
        for (SampleDimension lsd1 : lsd) {
            Assert.assertEquals(lsd.get(i++), lsd1);
        }
        fil.delete();
    }

    /**
     * Teets yaml read / load with bad version file.
     *
     * @throws IOException if problem during yaml file writing.
     */
    @Test
    public void badVersionTest() throws IOException {

        final String dumpResult =   "version: \"2.0\"\n" +
                                    "sampleDimension:\n" +
                                    "- description: \"band_0\"\n" +
                                    "  categories:\n" +
                                    "  - name: \"No data\"\n" +
                                    "    value: 0.0\n" +
                                    "  - name: \"data\"\n" +
                                    "    minSampleValue: 1.0\n" +
                                    "    isMinInclusive: true\n" +
                                    "    maxSampleValue: 255.0\n" +
                                    "    isMaxInclusive: true\n" +
                                    "    scale: 1.0\n" +
                                    "    offset: 0.0\n";

        try {
            final List<SampleDimension> sDim = YamlFiles.read(dumpResult, Double.class);
            Assert.fail("test should have fail for bad version reason.");
        } catch (IllegalStateException ex) {
            //-- expected comportement
            Assert.assertEquals(ex.getMessage(), "Current file version does not match expected : 1.0. Found : 2.0");
        }
    }
}
