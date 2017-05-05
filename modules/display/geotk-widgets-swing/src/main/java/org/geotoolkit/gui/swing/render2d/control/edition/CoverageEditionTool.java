/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.render2d.control.edition;

import java.util.logging.Level;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.CoverageMapLayer;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;

/**
 * Coverage editor tool.
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageEditionTool extends AbstractEditionTool{

    public CoverageEditionTool() {
        super(2000, "coverageEditor",
                MessageBundle.formatInternational(MessageBundle.Keys.editor),
                MessageBundle.formatInternational(MessageBundle.Keys.editor),
                null,
                CoverageMapLayer.class);
    }

    @Override
    public EditionDelegate createDelegate(JMap2D map, Object candidate) {
        return new CoverageEditionDelegate(map,(CoverageMapLayer)candidate);
    }

    @Override
    public boolean canHandle(Object candidate) {
        boolean supported = super.canHandle(candidate);
        if(!supported) return false;

        final CoverageMapLayer layer = (CoverageMapLayer) candidate;
        final CoverageReference ref = layer.getCoverageReference();
        if(ref == null) return false;
        try{
            supported = ref.isWritable();
        }catch(DataStoreException ex){
            Logging.getLogger("org.geotoolkit.gui.swing.render2d.control.edition").log(Level.INFO, "Coverage not writable : "+ex.getMessage());
            return false;
        }

        return supported;
    }

}
