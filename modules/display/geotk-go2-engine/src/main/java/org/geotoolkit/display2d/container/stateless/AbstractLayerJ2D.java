/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.beans.PropertyChangeEvent;
import java.util.EventObject;
import javax.media.jai.JAI;
import javax.media.jai.TileFactory;
import javax.media.jai.TileRecycler;
import org.geotoolkit.display2d.GO2Utilities;

import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.AbstractGraphicJ2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.util.collection.CollectionChangeEvent;

import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractLayerJ2D<T extends MapLayer> extends AbstractGraphicJ2D implements LayerListener{

    private static final TileRecycler TILE_RECYCLER = (TileRecycler)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_RECYCLER);
    private static final TileFactory TILE_FACTORY = (TileFactory)JAI.getDefaultInstance().getRenderingHint(JAI.KEY_TILE_FACTORY);
    private static final Point pt = new Point(0, 0);

    private final LayerListener.Weak weakListener = new LayerListener.Weak(this);

    protected final T layer;

    public AbstractLayerJ2D(final J2DCanvas canvas, final T layer){
        this(canvas, layer, false);
    }

    public AbstractLayerJ2D(final J2DCanvas canvas, final T layer, final boolean useLayerEnv){
        //do not use layer crs here, to long to calculate
        super(canvas, canvas.getObjectiveCRS2D());
        //super(canvas, layer.getBounds().getCoordinateReferenceSystem());
        this.layer = layer;

        weakListener.registerSource(layer);

        try{
            if (useLayerEnv) {
                setEnvelope(layer.getBounds());
            } else {
                final GeneralEnvelope env = new GeneralEnvelope(canvas.getObjectiveCRS2D());
                env.setRange(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                env.setRange(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                setEnvelope(env);
                //todo we do not use the layer envelope since it can be reallllly long to calculate
                //for exemple for postgrid coverage not yet loaded or huge vector bases like Open Street Map
                //setEnvelope(layer.getBounds());
            }
        }catch(TransformException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public T getUserObject() {
        return layer;
    }

    @Override
    public void dispose() {
        super.dispose();
        weakListener.dispose();
    }

    protected BufferedImage createBufferedImage(ColorModel cm, SampleModel model){
        final WritableRaster raster = TILE_FACTORY.createTile(model, pt);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }

    protected void recycleBufferedImage(BufferedImage img){
        if(img != null){
            TILE_RECYCLER.recycleTile(img.getRaster());
        }
    }

    // layer listener ----------------------------------------------

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(getCanvas().getController().isAutoRepaint()){
            final String propName = event.getPropertyName();
            if(MapLayer.VISIBILITY_PROPERTY.equals(propName)){
                //TODO should call a repaint only on this graphic
                getCanvas().getController().repaint();
            } else if (layer.isVisible() &&
               (  MapLayer.STYLE_PROPERTY.equals(propName)
               || MapLayer.SELECTION_FILTER_PROPERTY.equals(propName)
               || MapLayer.OPACITY_PROPERTY.equals(propName)
               || MapLayer.QUERY_PROPERTY.equals(propName) )){
                //TODO should call a repaint only on this graphic
                getCanvas().getController().repaint();
            }
        }
    }

    @Override
    public void styleChange(MapLayer source, EventObject event) {
        if(layer.isVisible() && getCanvas().getController().isAutoRepaint()){
            //TODO should call a repaint only on this graphic
            getCanvas().getController().repaint();
        }
    }

    @Override
    public void itemChange(CollectionChangeEvent<MapItem> event) {
    }

    @Override
    public final void paint(RenderingContext2D context) {

        //we abort painting if the layer is not visible.
        if (!layer.isVisible()) return;

        //we abort if opacity is to low
        final double opacity = layer.getOpacity();
        if(opacity < 1e-6) return;


        if(1-opacity < 1e-6){
            //we are very close to opacity one, no need to create a intermediate image
            paintLayer(context);
        }else{
            //create an intermediate layer which will be painted on the main context
            //after with the given opacity
            final Rectangle rect = context.getCanvasDisplayBounds();
            final BufferedImage inter = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g2d = inter.createGraphics();
            final RenderingContext2D interContext = context.create(g2d);
            paintLayer(interContext);

            //paint intermediate image
            final Graphics2D g = context.getGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)opacity));
            g.drawImage(inter, 0, 0, null);
        }

    }

    protected abstract void paintLayer(RenderingContext2D context);

}
