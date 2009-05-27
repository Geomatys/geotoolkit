/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.contexttree;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transfer Class used for drag and drop purpose
 * 
 * @author Johann Sorel
 */
final class DADContextTreeTransferable implements Transferable {
        
    private Object data;   
    private static final DataFlavor[] flavors = new DataFlavor[1];
    
    
    static {
        flavors[0] = DataFlavor.stringFlavor;
    }
        
    
    /**
     *  Transfer Class used for drag and drop purpose
     * @param data the draged object
     */
    DADContextTreeTransferable(Object data) {        
        super();
        this.data = data;
    }
    
    /**
     * ...
     * @return table of flavors
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    /**
     * ...
     * @param flavor target flavor
     * @return true if supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }
    
    /**
     * ...
     * @param flavor target flavor
     * @return the data contain by the flavor
     * @throws java.awt.datatransfer.UnsupportedFlavorException error
     * @throws java.io.IOException error
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
        
}
