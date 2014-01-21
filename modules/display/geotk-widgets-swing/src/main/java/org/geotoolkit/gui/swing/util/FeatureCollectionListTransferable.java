/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2014, Geomatys
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

package org.geotoolkit.gui.swing.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import org.geotoolkit.data.FeatureCollection;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureCollectionListTransferable implements Transferable{

    private static final String MIME = "geotk/featurecollectionList";
    private static final DataFlavor FLAVOR = new DataFlavor(List.class,MIME);
    private final List<FeatureCollection> selections;

    public FeatureCollectionListTransferable(List<FeatureCollection> selections) {
        this.selections = selections;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return FLAVOR.match(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return selections;
    }

    public List<FeatureCollection> getSelections() {
        return selections;
    }
    
}
