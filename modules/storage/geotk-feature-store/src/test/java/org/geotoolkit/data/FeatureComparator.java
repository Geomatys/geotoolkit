/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.data;

import org.apache.sis.util.ArgumentChecks;
import org.junit.Assert;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * Tool to compare feature and feature types.
 *
 * TODO : add configuration parameters.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureComparator {

    private final Object obj1;
    private final Object obj2;

    public FeatureComparator(Feature obj1, Feature obj2) {
        ArgumentChecks.ensureNonNull("obj1", obj1);
        ArgumentChecks.ensureNonNull("obj2", obj2);
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public FeatureComparator(FeatureType obj1, FeatureType obj2) {
        ArgumentChecks.ensureNonNull("obj1", obj1);
        ArgumentChecks.ensureNonNull("obj2", obj2);
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    /**
     * Compare the features or feature types specified at construction time.
     */
    public void compare() {
        if (!obj1.equals(obj2)) {
            Assert.fail("Objects not equal");
        }
    }

}
