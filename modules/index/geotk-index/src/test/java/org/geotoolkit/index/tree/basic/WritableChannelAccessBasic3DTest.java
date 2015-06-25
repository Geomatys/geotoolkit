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
public class WritableChannelAccessBasic3DTest extends WritableChannelAccessBasicTest {

    public WritableChannelAccessBasic3DTest() throws IOException, StoreIndexException {
        super(PredefinedCRS.CARTESIAN_3D);
    }
    
}
