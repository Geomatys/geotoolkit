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
import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CoveragePresenter implements InformationPresenter{

    @Override
    public JComponent createComponent(final Object graphic, final RenderingContext2D context, final SearchAreaJ2D area) {
        if(graphic instanceof ProjectedCoverage){
            return createComponent((ProjectedCoverage)graphic, context, area);
        }
        return null;
    }

    private JComponent createComponent(final ProjectedCoverage coverage, final RenderingContext2D context, final SearchAreaJ2D area){
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

    protected List<Entry<GridSampleDimension,Object>> visit(final ProjectedCoverage projectedCoverage, final RenderingContext2D context, final SearchAreaJ2D queryArea) {
        return Bridge.readCoverageValues(projectedCoverage, context, queryArea);
    }

    private abstract static class Bridge extends AbstractGraphicVisitor{
        public static List<Entry<GridSampleDimension,Object>> readCoverageValues(final ProjectedCoverage projectedCoverage,
                            final RenderingContext2D context, final SearchAreaJ2D queryArea){
            return getCoverageValues(projectedCoverage, context, queryArea);
        }
    }

}
