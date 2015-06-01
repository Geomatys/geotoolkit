/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.widget.ConnectionWidget;
import static org.geotoolkit.gui.swing.etl.ChainEditorConstants.*;
import org.geotoolkit.processing.chain.model.DataLink;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WDataLink extends ConnectionWidget {

    private final DataLink link;

    public WDataLink(final ChainScene scene, final DataLink link) {
        super(scene);
        this.link = link;
        getActions().addAction(ActionFactory.createReconnectAction(new ProviderParameterReconnect()));
        getActions().addAction(ActionFactory.createAddRemoveControlPointAction());
        getActions().addAction(ActionFactory.createFreeMoveControlPointAction());
        setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        setTargetAnchorShape(ChainShapes.ANCHOR_INPUT);
        setSourceAnchorShape(ChainShapes.ANCHOR_OUTPUT);
        setLineColor(DEFAULT_LINE_COLOR);
        getScene().validate();
    }

    public DataLink getLink() {
        return link;
    }

    @Override
    protected void paintWidget() {
        Graphics2D gr = getGraphics();
        gr.setColor(getForeground());

        Point firstControlPoint = getFirstControlPoint();
        Point lastControlPoint = getLastControlPoint();
        firstControlPoint = new Point(firstControlPoint);
        firstControlPoint.x += 4;
        lastControlPoint = new Point(lastControlPoint);
        lastControlPoint.x -= 4;


        if(firstControlPoint != null && lastControlPoint != null){
            final CubicCurve2D c = new CubicCurve2D.Double();
            final int ext = Math.abs((firstControlPoint.x - lastControlPoint.x)*2)/3;
            c.setCurve( firstControlPoint.x, firstControlPoint.y,
                        firstControlPoint.x+ext, firstControlPoint.y,
                        lastControlPoint.x-ext, lastControlPoint.y,
                        lastControlPoint.x, lastControlPoint.y);

            gr.setColor(getLineColor());
            gr.setStroke(getStroke());
            gr.draw(c);
        }


        final double firstControlPointRotation = 0.0;
        final double lastControlPointRotation = 0.0;

        AffineTransform previousTransform;

        if (firstControlPoint != null) {
            previousTransform = gr.getTransform();
            gr.translate(firstControlPoint.x, firstControlPoint.y);
            if (getSourceAnchorShape().isLineOriented()) {
                gr.rotate(firstControlPointRotation);
            }
            getSourceAnchorShape().paint(gr, true);
            gr.setTransform(previousTransform);
        }

        if (lastControlPoint != null) {
            previousTransform = gr.getTransform();
            gr.translate(lastControlPoint.x, lastControlPoint.y);
            if (getTargetAnchorShape().isLineOriented()) {
                gr.rotate(lastControlPointRotation);
            }
            getTargetAnchorShape().paint(gr, false);
            gr.setTransform(previousTransform);
        }
    }

    @Override
    protected Rectangle calculateClientArea() {
        final Rectangle rect = super.calculateClientArea();

        final Point firstControlPoint = getFirstControlPoint();
        final Point lastControlPoint = getLastControlPoint();


        // create new CubicCurve2D.Double
        if(firstControlPoint != null && lastControlPoint != null){
            final CubicCurve2D c = new CubicCurve2D.Double();
            final int ext = Math.abs((firstControlPoint.x - lastControlPoint.x)*2)/3 ;
            c.setCurve( firstControlPoint.x, firstControlPoint.y,
                        firstControlPoint.x+ext, firstControlPoint.y,
                        lastControlPoint.x-ext, lastControlPoint.y,
                        lastControlPoint.x, lastControlPoint.y);
            rect.add(c.getBounds());
        }

        return rect;
    }

}
