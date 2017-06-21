/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.processing.vector;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import java.util.Objects;
import org.geotoolkit.data.FeatureCollection;
import org.opengis.util.NoSuchIdentifierException;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.geotoolkit.processing.GeotkProcessingRegistry;

import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.feature.Feature;
import org.opengis.feature.PropertyType;
/**
 * Abstract JUnit test for vector process
 * @author Quentin Boileau
 * @module
 */
public abstract class AbstractProcessTest extends org.geotoolkit.test.TestBase {

    private static final String factory = GeotkProcessingRegistry.NAME;
    private String process;


    protected AbstractProcessTest(final String process){
        this.process = process;
    }

    @Test
    public void findProcessTest() throws NoSuchIdentifierException{
        ProcessDescriptor desc = ProcessFinder.getProcessDescriptor(factory, process);
        assertNotNull(desc);
    }


    /**
     * More tolerant collection compare operation which makes topologic geometry equality
     * test.
     *
     * @param expected
     * @param result
     */
    public static void compare(FeatureCollection expected, FeatureCollection result){
        assertEquals(expected.getType(), result.getType());
        assertEquals(expected.getID(), result.getID());
        assertEquals(expected.size(), result.size());
        loop:
        for(Feature f : expected){
            if(result.contains(f)){
                continue;
            }
            for(Feature r : result){
                if(equalsGeometryTopo(f, r)) continue loop;
            }
            fail("feature not found :\n"+f);
        }
    }

    /**
     * JTS do not consider geometries as equals if the coordinates order are different.
     * This test checks for a topology equality instead.
     *
     * @param expected
     * @param result
     * @return
     */
    public static boolean equalsGeometryTopo(final Feature expected, final Feature result) {
        if (result != expected) {
            if (result == null || result.getClass() != expected.getClass()) {
                return false;
            }
            if (!expected.getType().equals(result.getType())) {
                return false;
            }
            for (final PropertyType pt : expected.getType().getProperties(true)) {
                final String name = pt.getName().toString();
                final Object expectedValue = expected.getPropertyValue(name);
                final Object resultValue = expected.getPropertyValue(name);
                if(expectedValue instanceof Geometry && resultValue instanceof Geometry){
                    if(expectedValue.getClass() == GeometryCollection.class
                      ||resultValue.getClass() == GeometryCollection.class){
                        //special case, JTS do not like equalsTopo with GeometryCollection
                        //why not ? mistery
                        return expectedValue.equals(resultValue);
                    }
                    if (!((Geometry)expectedValue).equalsTopo((Geometry) resultValue)) {
                        return false;
                    }
                }else{
                    if (!Objects.equals(expectedValue, resultValue)) {
                        return false;
                    }
                }

            }
        }
        return true;
    }
}
