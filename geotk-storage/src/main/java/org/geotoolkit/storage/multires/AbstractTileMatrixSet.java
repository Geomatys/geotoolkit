/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2018, Geomatys
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
package org.geotoolkit.storage.multires;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.Classes;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.util.StringUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Abstract MatrixSet
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class AbstractTileMatrixSet implements TileMatrixSet {

    protected final GenericName id;
    protected final CoordinateReferenceSystem crs;

    public AbstractTileMatrixSet(CoordinateReferenceSystem crs) {
        this(null,crs);
    }

    public AbstractTileMatrixSet(GenericName id, CoordinateReferenceSystem crs) {
        this.crs = crs;
        if(id == null){
            this.id = Names.createLocalName(null, null, UUID.randomUUID().toString());
        }else{
            this.id = id;
        }
    }

    @Override
    public GenericName getIdentifier() {
        return id;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    @Override
    public String toString(){
        return toString(this);
    }

    /**
     * Pretty print output of given pyramid.
     * @param pyramid not null
     */
    public static String toString(TileMatrixSet pyramid) {
        final List<String> elements = new ArrayList<>();
        elements.add("id : " + pyramid.getIdentifier());
        elements.add("crs : " + IdentifiedObjects.getIdentifierOrName(pyramid.getCoordinateReferenceSystem()));
        elements.add(StringUtilities.toStringTree("mosaics", pyramid.getTileMatrices().values()));
        return StringUtilities.toStringTree(Classes.getShortClassName(pyramid), elements);
    }
}
