/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree.hilbert;
import java.util.BitSet;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**Test Hilbert curve creation in dimension n at order 5.
 *
 * @author Rémi Marechal(Géomatys).
 */
public abstract class HilbertTest {

    final int dimension;
    BitSet validPath;
    public HilbertTest(final int dimension) {
        this.dimension = dimension;
    }

    @Test
    public void order1Test(){
        orderNTest(1);
    }

    @Test
    public void order2Test(){
        orderNTest(2);
    }

    @Test
    public void order3Test(){
        orderNTest(3);
    }

    @Test
    public void order4Test(){
        orderNTest(4);
    }

    @Test
    public void order5Test(){
        orderNTest(5);
    }

    private void orderNTest(final int order){
        final int[] path = Hilbert.createPath(dimension, order);
        validPath = new BitSet(2<<dimension*order-1);
        followPath(path, 2<<order-1);
        validPath();
    }

    private void followPath(int[]path, int length){
        for(int i = 0; i<=path.length-dimension;i+=dimension){
            int val = path[i];
            for(int j = 1;j<dimension;j++){
                val+=path[i+j]*length;
            }
            validPath.set(val, true);
        }
    }

    private void validPath(){
        for(int j = 0,l=validPath.length();j<l;j++){
            assertTrue(validPath.get(j));
        }
    }
}
