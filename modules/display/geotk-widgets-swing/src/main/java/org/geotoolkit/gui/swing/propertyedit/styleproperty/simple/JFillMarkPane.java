/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2011 Geomatys
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
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import org.geotoolkit.gui.swing.style.JBankView;
import org.geotoolkit.gui.swing.style.JColorPane;
import org.geotoolkit.gui.swing.style.StyleBank;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Fill;
import org.opengis.style.Mark;

/**
 * Filling a form with mark
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JFillMarkPane extends StyleElementEditor<Mark> {

    private final JColorPane guiMarkColorChooser;
    private MapLayer layer = null;
    private StyleBank model = StyleBank.getInstance();
    private JBankView<Mark> guiMarkPane = null;

    /**
     * Creates new form JFillMarkPane
     */
    public JFillMarkPane() {
        super(Mark.class);
        guiMarkColorChooser = new JColorPane();
        setLayout(new BorderLayout());
        guiMarkColorChooser.setBorder(BorderFactory.createTitledBorder("Couleur"));
        add(guiMarkColorChooser, java.awt.BorderLayout.CENTER);

        guiMarkPane = new JBankView<Mark>(Mark.class);
        guiMarkPane.setCandidates(model.getCandidates(new StyleBank.ByClassComparator(new Class[]{Mark.class})));

        guiMarkPane.setBorder(new TitledBorder("Forme"));
        this.add(BorderLayout.NORTH, guiMarkPane);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiMarkPane.setLayer(layer);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc }
     * This method parses the settled object. If none color can be extracted, we
     * select red color by default.
     */
    @Override
    public void parse(final Mark mark) {

        if (mark != null) {

            guiMarkPane.parse(mark);

            //Color parsing
            final Expression targetColor = mark.getFill().getColor();
            if (targetColor != null) {

                if (isStatic(targetColor)) {
                    final Color color = targetColor.evaluate(null, Color.class);
                    if (color != null) {

                        guiMarkColorChooser.setColor(color);
                    } else {
                        guiMarkColorChooser.setColor(Color.RED);
                    }
                } else {
                    guiMarkColorChooser.setColor(Color.RED);
                }
            }

        }
    }

    /**
     * {@inheritDoc }
     * This funtion creates a Mark object from the selection. In the graphic
     * interface, the opacity is given between 0 and 100 but in the object, we
     * have to fill it with value between 0 and 1. If no mark is selected, we
     * return mark square with default stroke and color.
     */
    @Override
    public Mark create() {
        final Mark mark = (Mark) guiMarkPane.create();

        final Fill markFill = getStyleFactory().fill(
                getStyleFactory().literal(guiMarkColorChooser.getColor()),
                getFilterFactory().literal((double) guiMarkColorChooser.getColor().getAlpha() / 100.d));

        if (mark != null) {
            return getStyleFactory().mark(mark.getWellKnownName(), markFill, mark.getStroke());
        } else {
            return getStyleFactory().mark(StyleConstants.MARK_SQUARE, markFill, getStyleFactory().stroke());
        }

    }
}
