/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.internal.storage.geojson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import java.util.Comparator;
import org.junit.Assert;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JSONComparator implements Comparator<JsonNode>
{

    @Override
    public int compare(JsonNode o1, JsonNode o2) {
        if (o1.equals(o2)) {
            return 0;
        }
        if ((o1 instanceof NumericNode) && (o2 instanceof NumericNode)){
            Double d1 = ((NumericNode) o1).asDouble();
            Double d2 = ((NumericNode) o2).asDouble();
            Assert.assertEquals(d1, d2, 0.0001);
            return 0;
        }
        // TODO
        if (o1.isContainerNode() && o2 != null && o2.isContainerNode()) {
            ContainerNode c1 = (ContainerNode) o1;
            ContainerNode c2 = (ContainerNode) o2;
        }
        throw new AssertionError("expected:" + o1 +" but was " + o2);
    }
}

