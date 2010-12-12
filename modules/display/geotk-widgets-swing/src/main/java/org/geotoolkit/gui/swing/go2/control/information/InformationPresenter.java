/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing.go2.control.information;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Insets;
import java.util.List;
import java.util.Map.Entry;
import javax.measure.unit.Unit;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.geotoolkit.coverage.GridSampleDimension;

import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;

import org.opengis.feature.Property;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class InformationPresenter {

    /**
     * Create a user interface component to display the given object.
     * @param candidate , object to display
     * @return JComponent or null if no component appropriate.
     */
    public JComponent createComponent(Object graphic, RenderingContext2D context, SearchAreaJ2D area){

        if(graphic instanceof ProjectedCoverage){
            return createComponent((ProjectedCoverage)graphic, context, area);
        } else if (graphic instanceof GraphicJ2D){
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

    private JComponent createComponent(ProjectedCoverage coverage, RenderingContext2D context, SearchAreaJ2D area){
        final List<Entry<GridSampleDimension,Object>> results = Bridge.readCoverageValues(coverage, context, area);

        final StringBuilder builder = new StringBuilder();
        for (Entry<GridSampleDimension,Object> entry : results) {
            final Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            builder.append(value);
            final Unit unit = entry.getKey().getUnits();
            if (unit != null) {
                builder.append(' ').append(unit.toString());
            }
        }

        final JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.setMargin(new Insets(0, 0, 0, 0));
        textPane.setText(builder.toString());
        final JScrollPane scroll = new JScrollPane(textPane);
        scroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.setBackground(new Color(0f,0f,0f,0f));
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBorder(null);
        return scroll;
    }


    protected List<Entry<GridSampleDimension,Object>> visit(ProjectedCoverage projectedCoverage, RenderingContext2D context, SearchAreaJ2D queryArea) {
        return Bridge.readCoverageValues(projectedCoverage, context, queryArea);
    }

    private abstract static class Bridge extends AbstractGraphicVisitor{
        public static List<Entry<GridSampleDimension,Object>> readCoverageValues(ProjectedCoverage projectedCoverage,
                            RenderingContext2D context, SearchAreaJ2D queryArea){
            return getCoverageValues(projectedCoverage, context, queryArea);
        }
    }

}
