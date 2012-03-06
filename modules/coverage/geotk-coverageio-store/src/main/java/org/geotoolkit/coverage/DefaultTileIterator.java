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
package org.geotoolkit.coverage;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.image.io.mosaic.Tile;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.logging.Logging;

/**
 * Default Tile iterator from grid mosaic.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultTileIterator implements Iterator<Tile> {
    
    protected static final Logger LOGGER = Logging.getLogger(GridMosaic.class);
        
    private final GridMosaic mosaic;
    private final Iterator<? extends Point> ite;
    private final Map hints;
    private Tile next = null;

    public DefaultTileIterator(final GridMosaic mosaic,final Iterator<? extends Point> ite, final Map hints) {
        this.mosaic = mosaic;
        this.ite = ite;
        this.hints = hints;
    }

    @Override
    public boolean hasNext() {
        checkNext();
        return next != null;
    }

    @Override
    public Tile next() {
        checkNext();
        if (next == null) {
            throw new NoSuchElementException("No more elements.");
        }

        final Tile cp = next;
        next = null;
        return cp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private void checkNext() {
        if (next != null) {
            return;
        }

        if (ite.hasNext()) {
            final Point p = ite.next();
            try {
                final Tile t = mosaic.getTile(p.x, p.y, hints);
                next = t;
            } catch (DataStoreException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                return;
            }
        }

    }
}
