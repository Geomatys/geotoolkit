/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.NumberFormat;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.apache.sis.geometry.DirectPosition2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCoordinateBar extends BorderPane {
    
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
    
    private final FXMap map;
    
    private final ToolBar barLeft = new ToolBar();
    private final ToolBar barCenter = new ToolBar();
    private final ToolBar barRight = new ToolBar();
    
    private final TextField coordText = new TextField("");

    public FXCoordinateBar(FXMap map) {
        this.map = map;
        barLeft.setMinSize(0, 0);
        barLeft.setPrefSize(0, 0);
        barRight.setMinSize(0, 0);
        barRight.setPrefSize(0, 0);
        setLeft(barLeft);
        setCenter(barCenter);
        setRight(barRight);
        
        barCenter.getItems().add(coordText);
        
        map.addEventHandler(MouseEvent.ANY, new myListener());
    }
    
    
    private class myListener implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            
            final Point2D pt = new Point2D.Double(event.getX(), event.getY());
            Point2D coord = new DirectPosition2D();
            try {
                coord = map.getCanvas().getObjectiveToDisplay().inverseTransform(pt, coord);
            } catch (NoninvertibleTransformException ex) {
                coordText.setText("");
                return;
            }

            final CoordinateReferenceSystem crs = map.getCanvas().getObjectiveCRS();

            final StringBuilder sb = new StringBuilder("  ");
            sb.append(crs.getCoordinateSystem().getAxis(0).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getX()));
            sb.append("   ");
            sb.append(crs.getCoordinateSystem().getAxis(1).getAbbreviation());
            sb.append(" : ");
            sb.append(NUMBER_FORMAT.format(coord.getY()));
            coordText.setText(sb.toString());
        }

    }
    
}
