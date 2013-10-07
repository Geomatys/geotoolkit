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

package org.geotoolkit.display2d.container.stateless;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.media.jai.JAI;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRecycler;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.PyramidalCoverageReference;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.VisitFilter;
import org.geotoolkit.display.primitive.SceneNode;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.CollectionMapLayer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.display.primitive.Graphic;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatelessMapItemJ2D<T extends MapItem> extends GraphicJ2D implements ItemListener{

    private static final TileRecycler TILE_RECYCLER = (TileRecycler)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_RECYCLER);
    private static final TileFactory TILE_FACTORY = (TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY);
    private static final Point pt = new Point(0, 0);

    private final ItemListener.Weak weakListener = new ItemListener.Weak(this);

    //childs
    private final Map<MapItem, GraphicJ2D> itemGraphics = new HashMap<>();

    protected final T item;

    public StatelessMapItemJ2D(final J2DCanvas canvas, final T item, boolean allowChildren){
        super(canvas,allowChildren);
        this.item = item;

        //build children nodes
        final List<MapItem> childs = item.items();
        for(int i=0,n=childs.size(); i<n; i++){
            final MapItem child = childs.get(i);
            final GraphicJ2D gj2d = parseChild(child);
            itemGraphics.put(child, gj2d);
            getChildren().add(gj2d);
        }

        //listen to mapitem changes
        weakListener.registerSource(item);
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
        if(item instanceof MapContext){
            try {
                return ((MapContext)item).getBounds();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, ex.getMessage(), ex);
                return null;
            }
        }else if(item instanceof MapLayer){
            return ((MapLayer)item).getBounds();
        }
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        weakListener.dispose();

        for(GraphicJ2D graphic : itemGraphics.values()){
            graphic.dispose();
        }
        itemGraphics.clear();
    }

    // create graphics ---------------------------------------------------------

    protected GraphicJ2D parseChild(final MapItem child){

        //TODO simplify
        final StatelessMapItemJ2D g2d;
        if (child instanceof FeatureMapLayer){
            g2d = new StatelessFeatureLayerJ2D(getCanvas(), (FeatureMapLayer)child);
        }else if (child instanceof CollectionMapLayer){
            g2d = new StatelessCollectionLayerJ2D(getCanvas(), (CollectionMapLayer)child);
        }else if (child instanceof CoverageMapLayer){
            final CoverageMapLayer layer = (CoverageMapLayer) child;
            final CoverageReference ref = layer.getCoverageReference();
            if(ref != null && ref instanceof PyramidalCoverageReference){
                //pyramidal model, we can improve rendering
                g2d = new StatelessPyramidalCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)child);
            }else{
                //normal coverage
                g2d = new StatelessCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)child);
            }
        }else if(child instanceof MapLayer){
            g2d = new StatelessMapLayerJ2D(getCanvas(), (MapLayer)child, false);
        }else{
            g2d = new StatelessMapItemJ2D(getCanvas(), child, true);
        }

        return g2d;
    }

    @Override
    public void paint(final RenderingContext2D renderingContext) {
        //do not render children
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {
        //do not loop on children
        return graphics;
    }


    // item listener ----------------------------------------------

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if(getCanvas().getController().isAutoRepaint()){
            final String propName = event.getPropertyName();
            if(MapItem.VISIBILITY_PROPERTY.equals(propName)){
                //TODO should call a repaint only on this graphic
                getCanvas().getController().repaint();
            }
        }
    }

    @Override
    public void itemChange(final CollectionChangeEvent<MapItem> event) {
        final int type = event.getType();

        if(CollectionChangeEvent.ITEM_ADDED == type){
            final NumberRange range = event.getRange();
            int index = (int) range.getMinDouble();
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gj2d = parseChild(child);
                getChildren().add(index,(SceneNode)gj2d);
                itemGraphics.put(child, gj2d);
                index++;
            }

            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();

        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(final MapItem child : event.getItems()){
                //remove the graphic
                final GraphicJ2D gra = itemGraphics.remove(child);
                if(gra != null){
                    getChildren().remove(gra);
                    gra.dispose();
                }
            }

            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
        }

    }

    //tyling utilities ---------------------------------------------------------

    protected static BufferedImage createBufferedImage(final ColorModel cm, final SampleModel model){
        final WritableRaster raster = TILE_FACTORY.createTile(model, pt);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }

    protected static void recycleBufferedImage(final BufferedImage img){
        if(img != null){
            TILE_RECYCLER.recycleTile(img.getRaster());
        }
    }

}
