/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.sos.netcdf;

import org.geotoolkit.observation.OMUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.geotoolkit.observation.model.Field;
import org.geotoolkit.observation.model.FieldType;
import org.geotoolkit.swe.xml.Phenomenon;
import org.geotoolkit.swe.xml.v101.CompositePhenomenonType;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (geomatys)
 */
public class OMUtilsTest {

    @Test
    public void getPhenomenonTest() throws Exception {

        Field PRES = new Field(1, FieldType.QUANTITY, "PRES", "Pression",    "urn:ogc:def:phenomenon:GEOM:pres", "Pa");
        Field PSAL = new Field(2, FieldType.QUANTITY, "PSAL", "Salinity",    "urn:ogc:def:phenomenon:GEOM:psal", "mg/l");
        Field TEMP = new Field(3, FieldType.QUANTITY, "TEMP", "Temperature", "urn:ogc:def:phenomenon:GEOM:TEMP", "°c");

        List<Field> phenomenons = Arrays.asList(PRES, PSAL, TEMP);
        final Set<PhenomenonType> components = new LinkedHashSet<>();
        for (Field phen : phenomenons) {
            components.add(new PhenomenonType(phen.name, phen.label, phen.name, phen.description));
        }
        String phenomenonIdBase = "urn:ogc:phenomenon:";
        final String compositeId = "composite" + UUID.randomUUID().toString();
        final String compositeName = phenomenonIdBase + compositeId;
        CompositePhenomenonType expResult = new CompositePhenomenonType(compositeId, compositeName, null, null, null, components);

        Set<org.opengis.observation.Phenomenon> existingPhens = new HashSet<>();
        existingPhens.add(expResult);

        List<Field> newPhens = new ArrayList<>();
        newPhens.add(PRES);
        newPhens.add(PSAL);
        newPhens.add(TEMP);

        Phenomenon result = OMUtils.getPhenomenon("1.0.0", newPhens, existingPhens);

        Assert.assertEquals(expResult.getId(), result.getId());
        Assert.assertEquals(expResult,         result);

        // order matters
        newPhens = new ArrayList<>();
        newPhens.add(PSAL);
        newPhens.add(PRES);
        newPhens.add(TEMP);

        result = OMUtils.getPhenomenon("1.0.0", newPhens, existingPhens);

        Assert.assertNotEquals(expResult.getId(), result.getId());
        Assert.assertNotEquals(expResult,         result);
    }
}
