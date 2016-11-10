/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.render2d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.logging.Level;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javax.measure.Unit;
import org.apache.sis.measure.Units;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.display2d.ext.scalebar.DefaultScaleBarTemplate;
import org.geotoolkit.display2d.ext.scalebar.J2DScaleBarUtilities;
import org.geotoolkit.display2d.ext.scalebar.ScaleBarTemplate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.util.logging.Logging;

/**
 * Decoration displaying a scale bar
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXScaleBarDecoration extends BorderPane implements FXMapDecoration {

    private final SimpleObjectProperty<FXMap> map2D = new SimpleObjectProperty<>();
    private final ImageView view = new ImageView();

    private ScaleBarTemplate templateMain = new DefaultScaleBarTemplate(null,
                            new Dimension(220,40),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            Units.KILOMETRE);
    private ScaleBarTemplate templateSecond = new DefaultScaleBarTemplate(null,
                            new Dimension(220,40),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            Units.METRE);

    private final BufferedImage buffer = new BufferedImage(400, 40, BufferedImage.TYPE_INT_ARGB);
    private CoordinateReferenceSystem lastObjCRS = null;
    private CoordinateReferenceSystem lastDisplayCRS = null;
    private Point2D lastCenter = null;

    public FXScaleBarDecoration(){
        setBottom(new BorderPane(null, null, null, null, view));


        DropShadow ds = new DropShadow();
        ds.setOffsetY(1.0);
        ds.setOffsetX(1.0);
        ds.setRadius(12);
        ds.setColor(javafx.scene.paint.Color.WHITE);
        ds.setSpread(0.5);
        view.setEffect(ds);

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void refresh() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setMap2D(FXMap map) {
        map2D.set(map);

        if(map!=null){
            map.getCanvas().addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(J2DCanvas.TRANSFORM_KEY.equals(evt.getPropertyName())){
                        update();
                    }
                }
            });
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FXMap getMap2D() {
        return map2D.get();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Node getComponent() {
        return this;
    }

    public void setMainUnit(final Unit unit) {
        templateMain = new DefaultScaleBarTemplate(null,
                            templateMain.getSize(),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            unit);
        update();
    }

    public Unit getMainUnit(){
        return templateMain.getUnit();
    }

    public void setSecondaryUnit(final Unit unit) {
        templateSecond = new DefaultScaleBarTemplate(null,
                            templateMain.getSize(),10,
                            false, 4, NumberFormat.getNumberInstance(),
                            Color.DARK_GRAY, Color.GRAY, Color.WHITE,
                            10,true,false, new Font("Serial", Font.BOLD, 10),true,
                            unit);
        update();
    }

    public Unit getSecondaryUnit(){
        return templateSecond.getUnit();
    }

    private void update() {

        FXMap map = map2D.getValue();
        if(map2D.getValue()==null) return;

        final double[] center;
        final Point2D centerPoint;
        final CoordinateReferenceSystem objCRS;
        final CoordinateReferenceSystem dispCRS;
        try {
            center = map.getCanvas().getObjectiveCenter().getCoordinate();
            centerPoint = new Point2D.Double(center[0], center[1]);
            objCRS = map.getCanvas().getObjectiveCRS();
            dispCRS = map.getCanvas().getDisplayCRS();
        } catch (NoninvertibleTransformException | TransformException ex) {
            Logging.getLogger("org.geotoolkit.gui.javafx.render2d").log(Level.WARNING, null, ex);
            return;
        }

        if(!centerPoint.equals(lastCenter) || !dispCRS.equals(lastDisplayCRS) || !objCRS.equals(lastObjCRS) ){

            final Graphics2D g2d = buffer.createGraphics();
            g2d.setBackground(new Color(0f,0f,0f,0f));
            g2d.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                final double l = J2DScaleBarUtilities.paint(objCRS, dispCRS, centerPoint, g2d, 10, 0, templateMain);
                if(l<1.0){
                    //use secondary unit
                    g2d.setBackground(new Color(0f,0f,0f,0f));
                    g2d.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    J2DScaleBarUtilities.paint(objCRS, dispCRS, centerPoint, g2d, 10, 0, templateSecond);

                }
            } catch (PortrayalException ex) {
                ex.printStackTrace();
            }

            lastCenter = centerPoint;
            lastObjCRS = objCRS;
            lastDisplayCRS = dispCRS;
            view.setImage(SwingFXUtils.toFXImage(buffer, null));
        }

    }



}
