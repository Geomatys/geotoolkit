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
public class Hilbert5DTest {

    final int dimension = 5;
    int length;
    boolean[][][][][] validPath;

    public Hilbert5DTest() {
    }

    @Test
    public void order1Test(){
        final int[] path = Hilbert.createPath(dimension, 1);
        length = 2;
        validPath = new boolean[length][length][length][length][length];
        followPath(path);
        validPath();
    }

    @Test
    public void order2Test(){
        final int[] path = Hilbert.createPath(dimension, 2);
        length = 2<<1;
        validPath = new boolean[length][length][length][length][length];
        followPath(path);
        validPath();
    }
//
    @Test
    public void order3Test(){
        final int[] path = Hilbert.createPath(dimension, 3);
        length = 2<<2;
        validPath = new boolean[length][length][length][length][length];
        followPath(path);
        validPath();
    }

    @Test
    public void order4Test(){
        final int[] path = Hilbert.createPath(dimension, 4);
        length = 2<<3;
        validPath = new boolean[length][length][length][length][length];
        followPath(path);
        validPath();
    }

    @Test
    public void order5Test(){
        final int[] path = Hilbert.createPath(dimension, 5);
        length = 2<<4;
        validPath = new boolean[length][length][length][length][length];
        followPath(path);
        validPath();
    }

    private void followPath(int[]path){
        for(int i = 0; i<=path.length-dimension;i+=dimension){
            validPath[path[i]][path[i+1]][path[i+2]][path[i+3]][path[i+4]] = true;
        }
    }

    private void validPath(){
        for(int m = 0;m<length;m++){
            for(int l = 0;l<length;l++){
                for(int k = 0;k<length;k++){
                    for(int j = 0;j<length;j++){
                        for(int i = 0;i<length;i++){
                            assertTrue(validPath[i][j][k][l][m]);
                        }
                    }
                }
            }
        }
    }
}
