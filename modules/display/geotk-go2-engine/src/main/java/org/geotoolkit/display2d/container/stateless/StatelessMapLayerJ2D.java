/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;

import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display.canvas.VisitFilter;
import org.geotoolkit.display.primitive.SearchArea;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.GraphicBuilder;
import org.geotoolkit.map.LayerListener;
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
public class StatelessMapLayerJ2D<T extends MapLayer> extends StatelessMapItemJ2D<T> {

    private final LayerListener ll = new LayerListener() {

        @Override
        public void styleChange(MapLayer source, EventObject event) {
            if(item.isVisible() && getCanvas().getController().isAutoRepaint()){
                //TODO should call a repaint only on this graphic
                getCanvas().getController().repaint();
            }
        }

        @Override
        public void itemChange(CollectionChangeEvent<MapItem> event) {
            //handle by parent
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if(getCanvas().getController().isAutoRepaint()){
                final String propName = event.getPropertyName();
                if(MapLayer.VISIBILITY_PROPERTY.equals(propName)){
                    //handle in StatelessMapItemJ2D
                } else if (item.isVisible() &&
                   (  MapLayer.STYLE_PROPERTY.equals(propName)
                   || MapLayer.SELECTION_FILTER_PROPERTY.equals(propName)
                   || MapLayer.OPACITY_PROPERTY.equals(propName)
                   || MapLayer.QUERY_PROPERTY.equals(propName) )){
                    //TODO should call a repaint only on this graphic
                    getCanvas().getController().repaint();
                }
            }
        }
    };

    private final LayerListener.Weak weakListener = new LayerListener.Weak(ll);

    public StatelessMapLayerJ2D(final J2DCanvas canvas, final T layer){
        this(canvas, layer, false);
    }

    public StatelessMapLayerJ2D(final J2DCanvas canvas, final T layer, final boolean useLayerEnv){
        //do not use layer crs here, to long to calculate
        super(canvas, layer);
        weakListener.registerSource(layer);

        try{
            if (useLayerEnv) {
                setEnvelope(layer.getBounds());
            }
        }catch(TransformException ex){
            getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        weakListener.dispose();
    }

    @Override
    public void paint(RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!item.isVisible()) return;

        //we abort if opacity is to low
        final double opacity = item.getOpacity();
        if(opacity < 1e-6) return;


        if(1-opacity < 1e-6){
            //we are very close to opacity one, no need to create a intermediate image
            paintLayer(context);
        }else{
            //create an intermediate layer which will be painted on the main context
            //after with the given opacity
            final Rectangle rect = context.getCanvasDisplayBounds();
            final BufferedImage inter = createBufferedImage(ColorModel.getRGBdefault(),
                    ColorModel.getRGBdefault().createCompatibleSampleModel(rect.width, rect.height));
            final Graphics2D g2d = inter.createGraphics();
            final RenderingContext2D interContext = context.create(g2d);
            paintLayer(interContext);

            //paint intermediate image
            final Graphics2D g = context.getGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity));
            g.drawImage(inter, 0, 0, null);
            recycleBufferedImage(inter);
        }

    }

    /**
     * Render layer, will only be painted if an appropriate graphic builder is attached
     * to it.
     */
    protected void paintLayer(RenderingContext2D context){

        final GraphicBuilder<? extends GraphicJ2D> builder = item.getGraphicBuilder(GraphicJ2D.class);

        if(builder != null){
            final Collection<? extends GraphicJ2D> graphics = builder.createGraphics(item, canvas);
            for(GraphicJ2D g : graphics){
                g.paint(context);
            }
        }
    }

    /**
     * pick layer, will only be picked if an appropriate graphic builder is attached
     * to it.
     */
    @Override
    public List<Graphic> getGraphicAt(RenderingContext context, SearchArea mask, VisitFilter filter, List<Graphic> graphics) {

        final GraphicBuilder<GraphicJ2D> builder = (GraphicBuilder<GraphicJ2D>) item.getGraphicBuilder(GraphicJ2D.class);
        if(builder != null){
            //this layer hasa special graphic rendering, use it instead of normal rendering
            final Collection<GraphicJ2D> gras = builder.createGraphics(item, canvas);
            for(final GraphicJ2D gra : gras){
                graphics = gra.getGraphicAt(context, mask, filter,graphics);
            }
            return graphics;
        }else{
            //since this is a custom layer, we have no way to find a child graphic.
            graphics.add(this);
            return graphics;
        }
    }

}
