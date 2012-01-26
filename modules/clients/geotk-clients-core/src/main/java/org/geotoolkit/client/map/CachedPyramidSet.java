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
package org.geotoolkit.client.map;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.DefaultPyramidSet;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.collection.Cache;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class CachedPyramidSet extends DefaultPyramidSet{
    
    protected static final Logger LOGGER = Logging.getLogger(CachedPyramidSet.class);
    
    /**
     * Cache the last queried tiles
     */
    private final Cache<String,RenderedImage> tileCache = new Cache<String, RenderedImage>(4, 10, false);
    
    protected abstract InputStream download(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException;
    
    private String toId(GridMosaic mosaic, int col, int row, Map hints){
        final String pyramidId = mosaic.getPyramid().getId();
        final String mosaicId = mosaic.getId();
        
        final StringBuilder sb = new StringBuilder(pyramidId).append('_').append(mosaicId)
                .append('_').append(col).append('_').append(row);
        
        return sb.toString();
    }
    
    public InputStream getTileStream(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException{
        return download(mosaic,col,row, hints);
    }
    
    public RenderedImage getTile(GridMosaic mosaic, int col, int row, Map hints) throws DataStoreException{
        
        final String tileId = toId(mosaic, col, row, hints);
        
        //use the cache if available        
        RenderedImage value = tileCache.peek(tileId);
        if (value == null) {
            Cache.Handler<RenderedImage> handler = tileCache.lock(tileId);
            try {
                value = handler.peek();
                if (value == null) {
                    final InputStream stream = download(mosaic, col, row, hints);
                    if(stream != null){
                        try{
                            value = ImageIO.read(stream);
                        }catch (IOException ex) {
                            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                        }finally{
                            try {
                                stream.close();
                            } catch (IOException ex) {
                                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                            }
                        }
                    }
                }
            } finally {
                handler.putAndUnlock(value);
            }
        }
        return value;
    }
    
    
    
}
