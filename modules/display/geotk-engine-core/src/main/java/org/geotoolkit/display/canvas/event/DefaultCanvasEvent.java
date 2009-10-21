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
package org.geotoolkit.display.canvas.event;

import org.opengis.display.canvas.Canvas;
import org.opengis.display.canvas.CanvasEvent;
import org.opengis.display.canvas.CanvasState;
import org.opengis.display.canvas.RenderingState;
import org.opengis.referencing.operation.MathTransform;

/**
 * Default canvas event.
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultCanvasEvent extends CanvasEvent{
    private static final long serialVersionUID = 1096308047301145181L;

    private final CanvasState oldCanvaState;
    private final CanvasState newCanvaState;
    private final MathTransform mathChange;
    private final RenderingState oldRenderingState;
    private final RenderingState newRenderingState;

    public DefaultCanvasEvent(Canvas canvas,CanvasState oldCanvaState, CanvasState newCanvaState,
        MathTransform mathChange, RenderingState oldRenderingState, RenderingState newRenderingState){
        super(canvas);
        this.oldCanvaState = oldCanvaState;
        this.newCanvaState = newCanvaState;
        this.mathChange = mathChange;
        this.oldRenderingState = oldRenderingState;
        this.newRenderingState = newRenderingState;
    }

    @Override
    public CanvasState getOldState() {
        return oldCanvaState;
    }

    @Override
    public CanvasState getNewState() {
        return newCanvaState;
    }

    @Override
    public MathTransform getChange() {
        return mathChange;
    }

    @Override
    public MathTransform getChange(CanvasState arg0) {
        return null;
    }

    @Override
    public RenderingState getOldRenderingstate() {
        return oldRenderingState;
    }

    @Override
    public RenderingState getNewRenderingstate() {
        return newRenderingState;
    }

}
