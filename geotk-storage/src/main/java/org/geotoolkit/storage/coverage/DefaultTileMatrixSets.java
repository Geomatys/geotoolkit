/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.storage.multires.TileMatrixSet;

/**
 * Default PyramidSet.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTileMatrixSets {

    private final List<TileMatrixSet> tileMatrixSets = new ArrayList<TileMatrixSet>();

    public Collection<TileMatrixSet> getTileMatrixSets() {
        return tileMatrixSets;
    }

    @Override
    public String toString(){
        return StringUtilities.toStringTree(Classes.getShortClassName(this), getTileMatrixSets());
    }

}
