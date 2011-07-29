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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.media.jai.JAI;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRecycler;

import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.container.statefull.StatefullCoverageLayerJ2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.CollectionMapLayer;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.ItemListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

import org.opengis.display.primitive.Graphic;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StatelessMapItemJ2D<T extends MapItem> extends AbstractGraphicJ2D implements ItemListener{

    private static final TileRecycler TILE_RECYCLER = (TileRecycler)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_RECYCLER);
    private static final TileFactory TILE_FACTORY = (TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY);
    private static final Point pt = new Point(0, 0);

    private final ItemListener.Weak weakListener = new ItemListener.Weak(this);

    //childs
    private final Map<MapItem, GraphicJ2D> itemGraphics = new HashMap<MapItem, GraphicJ2D>();

    protected final T item;

    public StatelessMapItemJ2D(final J2DCanvas canvas, final T item){
        //do not use layer crs here, to long to calculate
        super(canvas, canvas.getObjectiveCRS2D());
        //super(canvas, layer.getBounds().getCoordinateReferenceSystem());
        this.item = item;

        try{
            //generic map items do not have an envelope, they act as containers
            final GeneralEnvelope env = new GeneralEnvelope(canvas.getObjectiveCRS2D());
            env.setRange(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            env.setRange(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            setEnvelope(env);
        }catch(TransformException ex){
            getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }

        parseItem(this.item);
        weakListener.registerSource(item);
    }

    @Override
    public T getUserObject() {
        return item;
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

    private void parseItem(final MapItem candidate){
        final List<MapItem> childs = candidate.items();
        for(int i=0,n=childs.size(); i<n; i++){
            final MapItem child = childs.get(i);
            final GraphicJ2D gj2d = parseChild(child, i);
            itemGraphics.put(child, gj2d);
        }

    }

    protected GraphicJ2D parseChild(final MapItem child, final int index){

        //TODO simplify
        final StatelessMapItemJ2D g2d;
        if (child instanceof FeatureMapLayer){
            g2d = new StatelessFeatureLayerJ2D(getCanvas(), (FeatureMapLayer)child);
        }else if (child instanceof CollectionMapLayer){
            g2d = new StatelessCollectionLayerJ2D(getCanvas(), (CollectionMapLayer)child);
        }else if (child instanceof CoverageMapLayer){
            g2d = new StatefullCoverageLayerJ2D(getCanvas(), (CoverageMapLayer)child);
        }else if(child instanceof MapLayer){
            g2d = new StatelessMapLayerJ2D(getCanvas(), (MapLayer)child);
        }else{
            g2d = new StatelessMapItemJ2D(getCanvas(), child);
        }

        g2d.setParent(this);
        g2d.setZOrderHint(index);
        return g2d;
    }

    @Override
    public void paint(final RenderingContext2D renderingContext) {

        //we abort painting if the item is not visible.
        if (!item.isVisible()) return;

        for(final MapItem child : item.items()){
            if(renderingContext.getMonitor().stopRequested()) break;
            final GraphicJ2D gra = itemGraphics.get(child);
            if(gra != null){
                gra.paint(renderingContext);
            }else{
                getLogger().log(Level.WARNING, "GrahicContextJ2D, paint method : strange, no graphic object affected to layer :{0}", child.getName());
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Graphic> getGraphicAt(final RenderingContext rdcontext, final SearchArea mask, final VisitFilter filter, List<Graphic> graphics) {

        //we abort painting if the item is not visible.
        if (!item.isVisible()) return graphics;

        final List<MapItem> children = item.items();
        for(int i=children.size()-1; i>=0; i--){
            final MapItem child = children.get(i);
            final GraphicJ2D gra = itemGraphics.get(child);
            graphics = gra.getGraphicAt(rdcontext,mask,filter,graphics);
        }
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
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gj2d = parseChild(child, item.items().indexOf(child));
                itemGraphics.put(child, gj2d);
                parseChild(child, item.items().indexOf(child));
            }
            //change other layers indexes
            final List<MapItem> children = item.items();
            for(int i=0,n=children.size(); i<n; i++){
                final MapItem layer = children.get(i);
                final GraphicJ2D gra = itemGraphics.get(layer);
                if(gra != null){
                    gra.setZOrderHint(i);
                }
            }
            //TODO should call a repaint only on this graphic
            if(getCanvas().getController().isAutoRepaint()){
                getCanvas().getController().repaint();
            }
        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(final MapItem child : event.getItems()){
                final GraphicJ2D gra = itemGraphics.get(child);
                if(gra != null){
                    gra.dispose();
                }
                //remove the graphic
                itemGraphics.remove(child);
            }
            //change other layers indexes
            final List<MapItem> children = item.items();
            for(int i=0,n=children.size(); i<n; i++){
                final MapItem child = children.get(i);
                final GraphicJ2D gra = itemGraphics.get(child);
                if(gra != null){
                    gra.setZOrderHint(i);
                }
            }
            //TODO should call a repaint only on this graphic
            if(getCanvas().getController().isAutoRepaint()){
                getCanvas().getController().repaint();
            }
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
