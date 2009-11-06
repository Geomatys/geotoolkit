/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.process.coverage;

import org.geotoolkit.util.NumberRange;

/**
 *
 * @author sorel
 */
public class Block {

    public NumberRange range;
    public int startX;
    public int endX;
    public int y;
    public Boundary boundary;

    public void reset(){
        range = null;
        startX = -1;
        endX = -1;
        y = -1;
        boundary = null;
    }

}
