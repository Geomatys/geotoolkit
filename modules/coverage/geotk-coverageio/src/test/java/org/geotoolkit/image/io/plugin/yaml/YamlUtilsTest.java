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
import java.util.Collections;
import java.util.List;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlBuilder;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlCategory;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlImageInfo;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlReaderBuilder;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleCategory;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlSampleDimension;
import org.geotoolkit.image.io.plugin.yaml.internal.YamlWriterBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.SampleDimension;

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
        
        final String dumpResult = "version: '1.0'\n" +
"sampleDimension:\n" +
"- description: band_0\n" +
"  categories:\n" +
"  - name: Absence de données\n" +
"    value: 0.0\n" +
"  - name: data\n" +
"    minSampleValue: 1.0\n" +
"    isMinInclusive: true\n" +
"    maxSampleValue: 255.0\n" +
"    isMaxInclusive: true\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n";
        
        
        final File fil = File.createTempFile("yamlTest", "txt");
        
        Category[] catsb0 = new Category[2];
        final Category noDataCatb0 = SampleDimensionUtils.buildSingleNoDataCategory(Double.class, 0);
        final Category dataCatb0   = SampleDimensionUtils.buildCategory("data", Double.class, null, 1, true, 255, true, 1, 0);
        catsb0[0] = noDataCatb0;
        catsb0[1] = dataCatb0;
        final SampleDimension sampDimb0 = new GridSampleDimension("band_0", catsb0, null);
        
        
        YamlWriterBuilder yamBuild = YamlFiles.getBuilder();
        
        yamBuild.setSampleDimensions(Collections.singletonList(sampDimb0));
        
        
//        System.out.println(YamlFiles.dump(yamBuild));
        
        Assert.assertEquals(dumpResult, YamlFiles.dump(yamBuild));
        
        
        YamlFiles.write(fil, yamBuild);
        
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
        
        final String dumpResult = "version: '1.0'\n" +
"sampleDimension:\n" +
"- description: band_0\n" +
"  categories:\n" +
"  - name: Absence de données\n" +
"    value: 0.0\n" +
"  - name: data\n" +
"    minSampleValue: 1.0\n" +
"    isMinInclusive: true\n" +
"    maxSampleValue: 255.0\n" +
"    isMaxInclusive: true\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n" +
"- description: band_1\n" +
"  categories:\n" +
"  - name: data\n" +
"    minSampleValue: 0.0\n" +
"    isMinInclusive: true\n" +
"    maxSampleValue: 125.0\n" +
"    isMaxInclusive: true\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n" +
"  - name: Absence de données\n" +
"    minSampleValue: 125.0\n" +
"    isMinInclusive: false\n" +
"    maxSampleValue: 254.0\n" +
"    isMaxInclusive: true\n" +
"  - name: data\n" +
"    value: 255.0\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n" +
"- description: band_2\n" +
"  categories:\n" +
"  - name: data\n" +
"    minSampleValue: 0.0\n" +
"    isMinInclusive: true\n" +
"    maxSampleValue: 254.0\n" +
"    isMaxInclusive: false\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n" +
"  - name: Absence de données\n" +
"    value: 254.0\n" +
"  - name: data\n" +
"    value: 255.0\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n";
        
        final File fil = File.createTempFile("yamlTest", "txt");
        
        //-- Sample dimension band 0
        Category[] catsb0 = new Category[2];
        final Category noDataCatb0 = SampleDimensionUtils.buildSingleNoDataCategory(Double.class, 0);
        final Category dataCatb0   = SampleDimensionUtils.buildCategory("data", Double.class, null, 1, true, 255, true, 1, 0);
        catsb0[0] = noDataCatb0;
        catsb0[1] = dataCatb0;
        final SampleDimension sampDimb0 = new GridSampleDimension("band_0", catsb0, null);
        
        //-- Sample dimension band 1
        Category[] catsb1 = new Category[3];
        final Category noDataCatb1 = SampleDimensionUtils.buildNoDataCategory(Double.class, 125, false, 254, true);
        final Category dataCatb10   = SampleDimensionUtils.buildCategory("data", Double.class, null, 0, true, 125, true, 1, 0);
        final Category dataCatb11   = SampleDimensionUtils.buildCategory("data", Double.class, null, 255, true, 255, true, 1, 0);
        catsb1[0] = noDataCatb1;
        catsb1[1] = dataCatb10;
        catsb1[2] = dataCatb11;
        final SampleDimension sampDimb1 = new GridSampleDimension("band_1", catsb1, null);
        
        //-- Sample dimension band 1
        Category[] catsb2 = new Category[3];
        final Category noDataCatb2 = SampleDimensionUtils.buildSingleNoDataCategory(Double.class, 254);
        final Category dataCatb2   = SampleDimensionUtils.buildCategory("data",  Double.class, null, 0, true, 254, false, 1, 0);
        final Category dataCatb21   = SampleDimensionUtils.buildCategory("data", Double.class, null, 255, true, 255, true, 1, 0);
        catsb2[0] = noDataCatb2;
        catsb2[1] = dataCatb2;
        catsb2[2] = dataCatb21;
        final SampleDimension sampDimb2 = new GridSampleDimension("band_2", catsb2, null);
        
        List<SampleDimension> sampDims = new ArrayList<>();
        sampDims.add(sampDimb0);
        sampDims.add(sampDimb1);
        sampDims.add(sampDimb2);
        
        final YamlWriterBuilder yamBuild = YamlFiles.getBuilder();
        yamBuild.setSampleDimensions(sampDims);
        
        YamlFiles.write(fil, yamBuild);
//        System.out.println(YamlFiles.dump(yamBuild));
        
        Assert.assertEquals(dumpResult, YamlFiles.dump(yamBuild));
        
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
        
        final String dumpResult = "version: '2.0'\n" +
"sampleDimension:\n" +
"- description: band_0\n" +
"  categories:\n" +
"  - name: Absence de données\n" +
"    value: 0.0\n" +
"  - name: data\n" +
"    minSampleValue: 1.0\n" +
"    isMinInclusive: true\n" +
"    maxSampleValue: 255.0\n" +
"    isMaxInclusive: true\n" +
"    scale: 1.0\n" +
"    offset: 0.0\n";
        
        try {
            final List<SampleDimension> sDim = YamlFiles.load(dumpResult, Double.class);
            Assert.fail("test should have fail for bad version reason.");
        } catch (IllegalStateException ex) {
            //-- expected comportement 
            Assert.assertEquals(ex.getMessage(), "Current file version does not match expected : 1.0. Found : 2.0");
        }
    }
}
