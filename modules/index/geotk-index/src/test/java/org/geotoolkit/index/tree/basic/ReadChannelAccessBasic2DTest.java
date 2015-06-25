/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.basic;

import java.io.IOException;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.referencing.crs.PredefinedCRS;

/**
 *
 * @author rmarechal
 */
public class ReadChannelAccessBasic2DTest extends ReadChannelAccessBasicTest {

    public ReadChannelAccessBasic2DTest() throws IOException, StoreIndexException, ClassNotFoundException {
        super(PredefinedCRS.CARTESIAN_2D, true);
    }
    
}
