/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020L, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.geotoolkit.observation.model.CompositePhenomenon;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldDataType;
import org.geotoolkit.observation.model.Phenomenon;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test on utility class OMUtils.
 * 
 * @author Guilhem Legal (geomatys)
 */
public class OMUtilsTest {

    @Test
    public void getPhenomenonTest() throws Exception {

        Field PRES = new Field(1, FieldDataType.QUANTITY, "PRES", "Pression",    "urn:ogc:def:phenomenon:GEOM:pres", "Pa");
        Field PSAL = new Field(2, FieldDataType.QUANTITY, "PSAL", "Salinity",    "urn:ogc:def:phenomenon:GEOM:psal", "mg/l");
        Field TEMP = new Field(3, FieldDataType.QUANTITY, "TEMP", "Temperature", "urn:ogc:def:phenomenon:GEOM:TEMP", "°c");

        List<Field> phenomenons = Arrays.asList(PRES, PSAL, TEMP);
        var components = phenomenons.stream().map( phen -> new Phenomenon(phen.name, phen.label, phen.name, phen.description, null)).toList();

        String phenomenonIdBase = "urn:ogc:phenomenon:";
        final String compositeId = "composite" + UUID.randomUUID().toString();
        final String compositeName = phenomenonIdBase + compositeId;
        CompositePhenomenon expResult = new CompositePhenomenon(compositeId, compositeName, null, null, null, components);

        Set<Phenomenon> existingPhens = new HashSet<>();
        existingPhens.add(expResult);

        List<Field> newPhens = new ArrayList<>();
        newPhens.add(PRES);
        newPhens.add(PSAL);
        newPhens.add(TEMP);

        Phenomenon result = OMUtils.getPhenomenonModels(null, newPhens, "urn:ogc:def:phenomenon:GEOM:", existingPhens);

        Assert.assertEquals(expResult.getId(), result.getId());
        Assert.assertEquals(expResult,         result);

        // order matters
        newPhens = new ArrayList<>();
        newPhens.add(PSAL);
        newPhens.add(PRES);
        newPhens.add(TEMP);

        result = OMUtils.getPhenomenonModels(null, newPhens, "urn:ogc:def:phenomenon:GEOM:", existingPhens);

        Assert.assertNotEquals(expResult.getId(), result.getId());
        Assert.assertNotEquals(expResult,         result);
    }

    @Test
    public void applyPostPaginationListTest() throws Exception {
        List<String> input = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        List<String> results = OMUtils.applyPostPagination(input, null, null);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, 0L, 10L);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, null, 10L);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, 0L, null);
        Assert.assertEquals(input, results);

        List<String> expected = Arrays.asList("1", "2", "3", "4", "5");

        results = OMUtils.applyPostPagination(input, 0L, 5L);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, null, 5L);
        Assert.assertEquals(expected, results);

        expected = Arrays.asList("6", "7", "8", "9", "10");

        results = OMUtils.applyPostPagination(input, 5L, 5L);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, 5L, null);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, 5L, 20L);
        Assert.assertEquals(expected, results);

        expected = Arrays.asList("3", "4", "5", "6", "7", "8");

        results = OMUtils.applyPostPagination(input, 2L, 6L);
        Assert.assertEquals(expected, results);
    }

    @Test
    public void applyPostPaginationMapTest() throws Exception {

        Map<String, List<String>> input = new LinkedHashMap<>();
        List<String> l1 = Arrays.asList("1", "2");
        List<String> l2 = Arrays.asList("3", "4");
        List<String> l3 = Arrays.asList("5", "6");
        List<String> l4 = Arrays.asList("7", "8");
        List<String> l5 = Arrays.asList("9", "10");
        input.put("1", l1);
        input.put("2", l2);
        input.put("3", l3);
        input.put("4", l4);
        input.put("5", l5);

        Map<String, List<String>> results = OMUtils.applyPostPagination(input, null, null);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, null, 10L);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, 0L, null);
        Assert.assertEquals(input, results);

        results = OMUtils.applyPostPagination(input, 0L, 10L);
        Assert.assertEquals(input, results);
        
        Map<String, List<String>> expected = new LinkedHashMap<>();
        expected.put("1", l1);
        expected.put("2", l2);

        results = OMUtils.applyPostPagination(input, 0L, 2L);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, null, 2L);
        Assert.assertEquals(expected, results);

        expected = new LinkedHashMap<>();
        expected.put("4", l4);
        expected.put("5", l5);

        results = OMUtils.applyPostPagination(input, 3L, 2L);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, 3L, null);
        Assert.assertEquals(expected, results);

        results = OMUtils.applyPostPagination(input, 3L, 10L);
        Assert.assertEquals(expected, results);

        expected = new LinkedHashMap<>();
        expected.put("2", l2);
        expected.put("3", l3);
        expected.put("4", l4);

        results = OMUtils.applyPostPagination(input, 1L, 3L);
        Assert.assertEquals(expected, results);

    }
}
