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
package org.geotoolkit.gui.javafx.render2d.navigation;

import org.geotoolkit.gui.javafx.render2d.AbstractNavigationHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.javafx.render2d.FXPanMouseListen;

/**
 * Panoramic handler
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPanHandler extends AbstractNavigationHandler {

    //we could use this cursor, but java do not handle translucent cursor correctly on every platform
    private static final Cursor CUR_ZOOM_PAN = Cursor.MOVE;
    private final FXPanMouseListen mouseInputListener = new FXPanMouseListen(this);

    public FXPanHandler(boolean infoOnRightClick) {
        super();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void install(final FXMap component) {
        component.addEventHandler(MouseEvent.ANY, mouseInputListener);
        component.addEventHandler(ScrollEvent.ANY, mouseInputListener);
        super.install(component);
        map.setCursor(CUR_ZOOM_PAN);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean uninstall(final FXMap component) {
        super.uninstall(component);
        component.removeEventHandler(MouseEvent.ANY, mouseInputListener);
        component.removeEventHandler(ScrollEvent.ANY, mouseInputListener);
//        map.setCursor(null);
        return true;
    }

    private static class InformationVisitor implements GraphicVisitor {

        private final List<org.opengis.display.primitive.Graphic> graphics = new ArrayList<>();
        private RenderingContext2D ctx = null;
        private SearchAreaJ2D area = null;

        @Override
        public void startVisit() {
        }

        @Override
        public void endVisit() {
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }

        @Override
        public void visit(org.opengis.display.primitive.Graphic graphic, RenderingContext context, SearchArea area) {
            this.graphics.add(graphic);
            this.ctx = (RenderingContext2D) context;
            this.area = (SearchAreaJ2D) area;
        }
    }


}
