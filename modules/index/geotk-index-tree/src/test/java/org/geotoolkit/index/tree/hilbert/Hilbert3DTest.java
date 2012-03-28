/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rmarech
 */
public class Hilbert3DTest {
    final int dimension = 3;
    boolean[][][] validPath;
    int length;

    public Hilbert3DTest() {
    }

    @Test
    public void order1Test(){
        final int[] path = Hilbert.createPath(3, 1);
        length = 2;
        validPath = new boolean[length][length][length];
        followPath(path);
        validPath(2);
    }

    @Test
    public void order2Test(){
        final int[] path = Hilbert.createPath(3, 2);
        length = 2<<1;
        validPath = new boolean[length][length][length];
        followPath(path);
        validPath(4);
    }

    @Test
    public void order3Test(){
        final int[] path = Hilbert.createPath(3, 3);
        length = 2<<2;
        validPath = new boolean[length][length][length];
        followPath(path);
        validPath(8);
    }

    @Test
    public void order4Test(){
        final int[] path = Hilbert.createPath(3, 4);
        length = 2<<3;
        validPath = new boolean[length][length][length];
        followPath(path);
        validPath(16);
    }

    @Test
    public void order5Test(){
        final int[] path = Hilbert.createPath(3, 5);
        length = 2<<4;
        validPath = new boolean[length][length][length];
        followPath(path);
        validPath(32);
    }

    private void followPath(int[]path){
        for(int i = 0; i<=path.length-dimension;i+=dimension){
            validPath[path[i]][path[i+1]][path[i+2]] = true;
        }
    }

    private void validPath(int length){
        for(int k = 0;k<length;k++){
            for(int j = 0;j<length;j++){
                for(int i = 0;i<length;i++){
                    assertTrue(validPath[i][j][k]);
                }
            }
        }
    }
}
