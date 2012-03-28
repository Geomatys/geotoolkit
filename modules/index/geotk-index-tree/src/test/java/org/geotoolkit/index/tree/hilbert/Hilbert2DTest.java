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
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**Test Hilbert curve creation in dimension 2.
 *
 * @author Rémi Marechal(Géomatys).
 */
public class Hilbert2DTest {

    final int dimension = 2;
    boolean[][] validPath;
    public Hilbert2DTest() {
    }

    @Test
    public void order1Test(){
        final int[] path = Hilbert.createPath(2, 1);
        validPath = new boolean[2][2];
        followPath(path);
        validPath(2);
    }

    @Test
    public void order2Test(){
        final int[] path = Hilbert.createPath(2, 2);
        validPath = new boolean[4][4];
        followPath(path);
        validPath(4);
    }

    @Test
    public void order3Test(){
        final int[] path = Hilbert.createPath(2, 3);
        validPath = new boolean[8][8];
        followPath(path);
        validPath(8);
    }

    @Test
    public void order4Test(){
        final int[] path = Hilbert.createPath(2, 4);
        validPath = new boolean[16][16];
        followPath(path);
        validPath(16);
    }

    @Test
    public void order5Test(){
        final int[] path = Hilbert.createPath(2, 5);
        validPath = new boolean[32][32];
        followPath(path);
        validPath(32);
    }

    private void followPath(int[]path){
        for(int i = 0; i<=path.length-dimension;i+=dimension){
            validPath[path[i]][path[i+1]] = true;
        }
    }

    private void validPath(int length){
        for(int j = 0;j<length;j++){
            for(int i = 0;i<length;i++){
                assertTrue(validPath[i][j]);
            }
        }
    }
}
