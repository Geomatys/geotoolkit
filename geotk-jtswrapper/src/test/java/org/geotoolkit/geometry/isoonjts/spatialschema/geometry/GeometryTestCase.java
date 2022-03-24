/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry;


import org.opengis.geometry.Geometry;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * This class represents the part of the JTS test XML file
 * that is wrapped with the "case" tags. It contains two
 * geometry objects and then one or more tests to apply
 * to those geometries.
 *
 * @author Jody Garnett
 * @author Joel Skelton
 * @module
 */
public class GeometryTestCase {

    private static final Logger LOG = Logger.getLogger("org.geotoolkit.geometry.isoonjts.spatialschema.geometry");
    private List operationList;
    private Geometry geomA;
    private Geometry geomB;
    private String description;

    /**
     * Constructor
     */
    public GeometryTestCase() {
        this.operationList = new ArrayList();
        this.geomA = null;
        this.geomB = null;
        description = "No description";
    }

    /**
     * Sets the geometry specified by the A tag
     */
    public void setGeometryA(final Geometry a) {
        geomA = a;
    }

    /**
     * Sets the geometry specified by the b tag
     */
    public void setGeometryB(final Geometry b) {
        geomB = b;
    }

    /**
     * Adds in a test operation that will be run on the given
     * A and B geometries.
     */
    public void addTestOperation(final GeometryTestOperation op) {
        operationList.add(op);
    }

    /**
     * Sets the description text string for this test case. The
     * description is used for logging results.
     */
    public void setDescription(final String desc) {
        description = desc;
    }

    /**
     * Run any test operations stored for this test case
     */
    public boolean runTestCases() {
        boolean result = true;
        LOG.info("Running test:" + description);
        for (Iterator i = operationList.iterator(); i.hasNext();) {
            GeometryTestOperation op = (GeometryTestOperation) i.next();
            LOG.info("Running test case:" + op);
            if (!op.run(geomA, geomB)) {
                LOG.info(op.toString() + " failed");
                result = false;
            }
        }
        return result;
    }

    @Test
    public void test(){

    }

}
