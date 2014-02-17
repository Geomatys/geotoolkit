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
package org.geotoolkit.gui.swing.propertyedit.styleproperty.simple;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JColorPane;
import org.geotoolkit.gui.swing.style.JExternalGraphicPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.AnchorPoint;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.Fill;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;

/**
 * Fill editor pane. A form can be filled by a Color or a GraphicFill.
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JFillPane extends StyleElementEditor<Fill> {

    private final JColorPane guiColorChooser = new JColorPane();
    private final JExternalGraphicPane guiExternalGraphicPane = new JExternalGraphicPane();
    private final JFillMarkPane guiMarkPane = new JFillMarkPane();
    private final JTabbedPane guiTabbedPane = new JTabbedPane();
    private MapLayer layer = null;

    /**
     * Creates new form JFillPane
     */
    public JFillPane() {
        this(null);
    }

    public JFillPane(Color color) {
        super(new BorderLayout(),Fill.class);

        guiColorChooser.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
        guiColorChooser.setColor(color);
        
        guiMarkPane.setBorder(new LineBorder(new Color(102, 102, 102), 1, true));
        guiExternalGraphicPane.setBorder(new LineBorder(new Color(102, 102, 102), 1, true));
        
        guiTabbedPane.addTab(MessageBundle.getString("plainColor"), guiColorChooser);
        guiTabbedPane.addTab(MessageBundle.getString("predefinedShape"), guiMarkPane);
        guiTabbedPane.addTab(MessageBundle.getString("image"), guiExternalGraphicPane);

        add(guiTabbedPane, java.awt.BorderLayout.CENTER);
    }

    public Color getColor() {
        return guiColorChooser.getColor();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * This method parses a Fill object. A fill can contains many Graphical
     * Symbols but here we considered that only one is used. {@inheritDoc }
     */
    @Override
    public void parse(final Fill fill) {

        if (fill != null) {

            final Expression targetColor = fill.getColor();
            final GraphicFill targetGraphic = fill.getGraphicFill();

            // Graphic parsing
            if (targetGraphic != null) {
                //Parsing the first graphic symbol
                Iterator<GraphicalSymbol> iterGraphic = targetGraphic.graphicalSymbols().iterator();

                if (iterGraphic.hasNext()) {
                    GraphicalSymbol gs = iterGraphic.next();

                    if (gs instanceof Mark) {
                        guiTabbedPane.setSelectedComponent(guiMarkPane);
                        guiMarkPane.parse((Mark) gs);

                    } else if (gs instanceof ExternalGraphic) {
                        guiTabbedPane.setSelectedComponent(guiExternalGraphicPane);
                        guiExternalGraphicPane.parse((ExternalGraphic) gs);
                    } else {
                        guiTabbedPane.setSelectedComponent(guiMarkPane);
                        guiMarkPane.parse(getStyleFactory().getSquareMark());
                    }
                }

            } else if (targetColor != null) {
                //Color parsing
                guiTabbedPane.setSelectedComponent(guiColorChooser);
                if (FilterUtilities.isStatic(targetColor)) {
                    final Color color = targetColor.evaluate(null, Color.class);
                    if (color != null) {

                        guiColorChooser.setColor(color);
                    } else {
                        guiColorChooser.setColor(Color.RED);
                    }
                } else {
                    guiColorChooser.setColor(Color.RED);
                }
            }
        }
    }

    /**
     * This method creates a Fill object from the selection. If none selection
     * can be extracted with return a default fill. {@inheritDoc }
     */
    @Override
    public Fill create() {
        final Object obj = guiTabbedPane.getSelectedComponent();
        final Expression offset = StyleConstants.LITERAL_ONE_FLOAT;
        final Expression size = getFilterFactory().literal(12);
        final Expression opacity = StyleConstants.LITERAL_ONE_FLOAT;
        final Expression rotation = StyleConstants.LITERAL_ONE_FLOAT;
        final AnchorPoint anchor = StyleConstants.DEFAULT_ANCHOR_POINT;
        final Displacement disp = StyleConstants.DEFAULT_DISPLACEMENT;

        if (obj instanceof JColorPane) {
            //Create Fill with Color
            return getStyleFactory().fill(
                    getStyleFactory().literal(guiColorChooser.getColor()),
                    getFilterFactory().literal((double) guiColorChooser.getColor().getAlpha() / 255.d));
        } else if (obj instanceof JFillMarkPane) {
            // Create fill with Mark
            final GraphicFill graphicFill = getStyleFactory().graphicFill(Collections.singletonList(
                    (GraphicalSymbol)guiMarkPane.create()), opacity, size, rotation, anchor, disp);
            return getStyleFactory().fill(graphicFill, offset, offset);
        } else if (obj instanceof JExternalGraphicPane) {
            // Create fill with External Graphic
            final GraphicFill graphicFill = getStyleFactory().graphicFill(Collections.singletonList(
                    (GraphicalSymbol)guiExternalGraphicPane.create()), opacity, size, rotation, anchor, disp);
            return getStyleFactory().fill(graphicFill, offset, offset);
        } else {
            return getStyleFactory().fill();
        }

    }
}
