/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.Component;
import javax.swing.ImageIcon;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.gui.swing.image.IIOMetadataPanel;
import org.geotoolkit.map.CoverageMapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoverageMetaDataPanel extends IIOMetadataPanel implements PropertyPane{

    @Override
    public void setTarget(Object target) {
        if(target instanceof CoverageMapLayer){
            final CoverageMapLayer layer = (CoverageMapLayer) target;
            final GridCoverageReader reader = layer.getCoverageReader();
            

            //how do we recover metadatas from here ?

        }else{
            clear();
        }

    }

    @Override
    public void apply() {
        //just metadata display, do not change anything
    }

    @Override
    public void reset() {
    }

    @Override
    public String getTitle() {
        return "MetaData";
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getToolTip() {
        return "Coverage metadatas";
    }

    @Override
    public Component getComponent() {
        return this;
    }

}
