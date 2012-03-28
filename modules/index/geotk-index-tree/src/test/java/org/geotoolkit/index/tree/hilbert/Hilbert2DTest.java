/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
/**
 *
 * @author rmarech
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
