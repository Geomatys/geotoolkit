/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.display.container;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.geotoolkit.display.canvas.AbstractReferencedCanvas2D;
import org.geotoolkit.display.primitive.AbstractReferencedGraphic2D;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

import org.opengis.display.container.ContainerEvent;
import org.opengis.display.primitive.Graphic;

/**
 * Abstract Container 2D extends Abstract container by providing a convinient method
 * to grab a sorted list of graphic sorted on Z order.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Johann Sorel (Geomatys)
 *
 * @module pending
 * @since 2.5
 */
public abstract class AbstractContainer2D extends AbstractContainer{
    /**
     * A comparator for sorting {@link Graphic} objects by increasing <var>z</var> order.
     */
    private static final Comparator<Graphic> COMPARATOR = new Comparator<Graphic>() {
        @Override
        public int compare(final Graphic graphic1, final Graphic graphic2) {
            if(graphic1 instanceof AbstractReferencedGraphic2D && graphic2 instanceof AbstractReferencedGraphic2D){
                return Double.compare(((AbstractReferencedGraphic2D)graphic1).getZOrderHint(), ((AbstractReferencedGraphic2D)graphic2).getZOrderHint());
            }else{
                return 0;
            }

        }
    };

    /**
     * The set of {@link Graphic}s to display, sorted in increasing <var>z</var> value. If
     * {@code null}, then {@code Collections.sort(graphics, COMPARATOR)} need to be invoked
     * and its content copied into {@code sortedGraphics}.
     *
     * @see #getGraphics
     */
    private transient List<Graphic> sortedGraphics;


    /**
     * Create a Default Abstract 2D renderer.
     */
    protected AbstractContainer2D(final AbstractReferencedCanvas2D canvas){
        super(canvas);
    }


    /**
     * {@inheritDoc}
     * <p>
     * Prepare the sorted graphic list if event is a Z order change.
     * or fire events if it's a visibility or display bounds event.
     * </p>
     */
    @Override
    protected void graphicPropertyChanged(final Graphic graphic, final PropertyChangeEvent event){
        super.graphicPropertyChanged(graphic, event);
        final String propertyName = event.getPropertyName();

        if (propertyName.equals(AbstractReferencedGraphic2D.Z_ORDER_HINT_PROPERTY)) {
            sortedGraphics = null; // Will force a new sorting according z-order.
        }else if(propertyName.equals(AbstractReferencedGraphic2D.DISPLAY_BOUNDS_PROPERTY)){
            ContainerEvent containerEvent = new DefaultContainerEvent(this, graphic);
            fireGraphicDisplayChanged(containerEvent);
        }

    }

    @Override
    public AbstractReferencedCanvas2D getCanvas() {
        return (AbstractReferencedCanvas2D) super.getCanvas();
    }


    
    //------------ graphic methods ---------------------------------------------
    /**
     * The returned list is sorted in increasing
     * {@linkplain Graphic#getZOrderHint z-order}: element at index 0 contains the first
     * graphic to be drawn.
     *
     * @return The sorted graphic list by Z order
     */
    public synchronized List<Graphic> getSortedGraphics(){
        if (sortedGraphics == null) {
            final Set<Graphic> keys = graphics.keySet();
            final Graphic[] list = keys.toArray(new Graphic[keys.size()]);
            Arrays.sort(list, COMPARATOR);
            sortedGraphics = UnmodifiableArrayList.wrap(list);
        }
        assert sortedGraphics.size() == graphics.size();
        assert graphics.keySet().containsAll(sortedGraphics);

        return sortedGraphics;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A call to this method will set to null the sorted graphic list.
     * The list will be recreated on the first call to {@link #getSortedGraphics() }.
     * <p>
     */
    @Override
    public synchronized Graphic add(Graphic graphic) throws IllegalArgumentException {
        final List<Graphic> oldGraphics = sortedGraphics; // May be null.
        graphic = super.add(graphic);
        sortedGraphics = null;
        assert oldGraphics == null || graphics().containsAll(oldGraphics) : oldGraphics;
        return graphic;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A call to this method will set to null the sorted graphic list.
     * The list will be recreated on the first call to {@link #getSortedGraphics() }.
     * <p>
     */
    @Override
    public synchronized void remove(final Graphic graphic) throws IllegalArgumentException {
        final List<Graphic> oldGraphics = sortedGraphics; // May be null.
        super.remove(graphic);
        sortedGraphics = null;
        assert oldGraphics==null || oldGraphics.containsAll(graphics()) : oldGraphics;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A call to this method will set to null the sorted graphic list.
     * The list will be recreated on the first call to {@link #getSortedGraphics() }.
     * <p>
     */
    @Override
    protected synchronized void removeAll() {
        super.removeAll();
        sortedGraphics = null;
        clearCache();
    }


    /**
     * Returns a rectangle that completly encloses all {@linkplain ReferencedGraphic#getEnvelope
     * graphic envelopes} managed by this canvas. Note that there is no guarantee that the returned
     * rectangle is the smallest bounding box that encloses the canvas, only that the canvas lies
     * entirely within the indicated rectangle.
     * <p>
     * This envelope is different from
     * {@link org.geotoolkit.display.canvas.map.DefaultMapState#getEnvelope}, since the later returns
     * an envelope that encloses only the <em>visible</em> canvas area and is scale-dependent. This
     * {@code ReferencedCanvas2D.getEnvelope2D()} method is scale-independent. Both envelopes are
     * equal if the {@linkplain #getScale scale} is choosen in such a way that all graphics can fit
     * in the {@linkplain #getDisplayBounds canvas visible area}.
     *
     * @return The envelope for this canvas in terms of {@linkplain #getObjectiveCRS objective CRS}.
     *
     * @see #getEnvelope
     * @see org.geotoolkit.display.canvas.map.DefaultMapState#getEnvelope
     */
    public abstract Rectangle2D getGraphicsEnvelope2D();

}
