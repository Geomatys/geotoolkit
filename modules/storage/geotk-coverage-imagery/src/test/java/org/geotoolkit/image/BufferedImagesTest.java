/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BufferedImagesTest {

    @Test
    public void testPointStream() {

        final Stream<Point> stream = BufferedImages.pointStream(new Rectangle(10, 20, 3, 4));

        Iterator<Point> ite = stream.sequential().iterator();
        Assert.assertEquals(new Point(10, 20), ite.next());
        Assert.assertEquals(new Point(11, 20), ite.next());
        Assert.assertEquals(new Point(12, 20), ite.next());
        Assert.assertEquals(new Point(10, 21), ite.next());
        Assert.assertEquals(new Point(11, 21), ite.next());
        Assert.assertEquals(new Point(12, 21), ite.next());
        Assert.assertEquals(new Point(10, 22), ite.next());
        Assert.assertEquals(new Point(11, 22), ite.next());
        Assert.assertEquals(new Point(12, 22), ite.next());
        Assert.assertEquals(new Point(10, 23), ite.next());
        Assert.assertEquals(new Point(11, 23), ite.next());
        Assert.assertEquals(new Point(12, 23), ite.next());
        Assert.assertFalse(ite.hasNext());

    }

}
