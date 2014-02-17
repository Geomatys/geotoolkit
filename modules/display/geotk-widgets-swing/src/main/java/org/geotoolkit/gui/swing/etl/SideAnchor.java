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

import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class SideAnchor extends Anchor{

    private final boolean left;

    public SideAnchor(Widget relatedWidget, boolean left) {
        super(relatedWidget);
        this.left = left;
    }

    @Override
    public Result compute (final Entry entry) {

        final Widget widget = getRelatedWidget();
        final Rectangle bounds = widget.convertLocalToScene(widget.getBounds());
        final Point center = new Point( (int)(bounds.getMinX()+bounds.getMaxX())/2 , (int)(bounds.getMinY()+bounds.getMaxY())/2 );

        if (left) {
            return new Anchor.Result(new Point(bounds.x, center.y), Direction.LEFT);
        } else {
            return new Anchor.Result(new Point(bounds.x + bounds.width, center.y), Direction.RIGHT);
        }
    }
}
