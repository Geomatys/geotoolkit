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

package org.geotoolkit.display2d.container;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.media.jai.JAI;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRecycler;
import org.apache.sis.map.MapItem;
import org.apache.sis.map.MapLayer;
import org.apache.sis.map.MapLayers;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class MapItemJ2D<T extends MapItem> extends GraphicJ2D {

    private static final TileRecycler TILE_RECYCLER = (TileRecycler)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_RECYCLER);
    private static final TileFactory TILE_FACTORY = (TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY);
    private static final Point pt = new Point(0, 0);


    //childs
    private final Map<MapItem, GraphicJ2D> itemGraphics = new HashMap<>();

    protected final T item;

    public MapItemJ2D(final J2DCanvas canvas, final T item, boolean allowChildren) {
        super(canvas,allowChildren);
        this.item = item;

        //build children nodes
        if (item instanceof MapLayers) {
            final MapLayers mc = (MapLayers) item;
            final List<MapItem> childs = mc.getComponents();
            for (int i = 0, n = childs.size(); i < n; i++) {
                final MapItem child = childs.get(i);
                final GraphicJ2D gj2d = parseChild(child);
                itemGraphics.put(child, gj2d);
                getChildren().add(gj2d);
            }
        }

    }

    @Override
    public void setVisible(boolean visible) {
        item.setVisible(visible);
    }

    @Override
    public boolean isVisible() {
        return item.isVisible();
    }

    @Override
    public T getUserObject() {
        return item;
    }

    @Override
    public Envelope getEnvelope() {
        if (item != null) {
            try {
                return item.getEnvelope().orElse(null);
            } catch (DataStoreException ex) {
                getLogger().log(Level.WARNING, ex.getMessage(), ex);
                return null;
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();

        for(GraphicJ2D graphic : itemGraphics.values()){
            graphic.dispose();
        }
        itemGraphics.clear();
    }

    // create graphics ---------------------------------------------------------

    protected GraphicJ2D parseChild(final MapItem child){

        if (child instanceof MapLayer) {
            return new MapLayerJ2D(getCanvas(), (MapLayer) child);
        } else {
            return new MapItemJ2D(getCanvas(), child, true);
        }
    }

    @Override
    public boolean paint(final RenderingContext2D renderingContext) {
        //do not render children
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, List<Graphic> graphics) {
        //do not loop on children
        return graphics;
    }

    //tyling utilities ---------------------------------------------------------

    protected static BufferedImage createBufferedImage(final ColorModel cm, final SampleModel model) {
        final WritableRaster raster = TILE_FACTORY.createTile(model, pt);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }

    protected static void recycleBufferedImage(final BufferedImage img) {
        if (img != null) {
            TILE_RECYCLER.recycleTile(img.getRaster());
        }
    }
}
