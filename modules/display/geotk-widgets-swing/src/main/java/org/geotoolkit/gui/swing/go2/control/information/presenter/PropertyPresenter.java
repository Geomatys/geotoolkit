/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Johann Sorel
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.information.presenter;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Property;

/**
 * JComponent for Features and Properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PropertyPresenter implements InformationPresenter{

    @Override
    public JComponent createComponent(Object graphic, RenderingContext2D context, SearchAreaJ2D area) {

         if (graphic instanceof GraphicJ2D){
            final GraphicJ2D gra = (GraphicJ2D) graphic;
            final Object userObj = gra.getUserObject();

            if(userObj instanceof Property){
                final JFeatureOutLine outline = new JFeatureOutLine();
                outline.setEdited((Property) userObj);
                final JScrollPane pane = new JScrollPane(outline);
                pane.setBorder(null);
                return pane;
            }
        }

        return null;
    }

}
