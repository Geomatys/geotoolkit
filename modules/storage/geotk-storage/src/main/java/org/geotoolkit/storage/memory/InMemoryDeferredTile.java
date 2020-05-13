/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.storage.memory;

import java.awt.Point;
import org.apache.sis.internal.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.storage.multires.DeferredTile;
import org.geotoolkit.storage.multires.Tile;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class InMemoryDeferredTile extends AbstractResource implements Tile, DeferredTile {

    private final Point position;
    private final Resource fs;

    public InMemoryDeferredTile(Point position, Resource fs) {
        super(null);
        this.position = position;
        this.fs = fs;
    }

    @Override
    public Resource open() throws DataStoreException {
        return fs;
    }

    @Override
    public Point getPosition() {
        return position;
    }

}
