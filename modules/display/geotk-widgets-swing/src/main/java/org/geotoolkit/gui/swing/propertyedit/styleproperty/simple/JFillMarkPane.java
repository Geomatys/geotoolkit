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
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.gui.swing.resource.MessageBundle;
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
 * Filling a form with mark.
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JFillMarkPane extends StyleElementEditor<Mark> {

    private final JColorPane guiMarkColorChooser = new JColorPane();
    private final StyleBank model = StyleBank.getInstance();
    private MapLayer layer = null;
    private final JBankView<Mark> guiMarkPane = new JBankView<>(Mark.class);

    /**
     * Creates new form JFillMarkPane
     */
    public JFillMarkPane() {
        super(new BorderLayout(),Mark.class);
        
        guiMarkColorChooser.setBorder(BorderFactory.createTitledBorder(MessageBundle.getString("color")));

        guiMarkPane.setCandidates(model.getCandidates(new StyleBank.ByClassComparator(Mark.class)));
        guiMarkPane.setBorder(new TitledBorder("Forme"));
        
        add(guiMarkPane, BorderLayout.NORTH);
        add(guiMarkColorChooser, BorderLayout.CENTER);
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
            final Expression targetColor = mark.getFill().getColor();
            Color color = Color.RED;
            if (targetColor != null && FilterUtilities.isStatic(targetColor)) {
                color = targetColor.evaluate(null, Color.class);
            }
            guiMarkColorChooser.setColor(color);
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
                getFilterFactory().literal((double) guiMarkColorChooser.getColor().getAlpha() / 255d));

        if (mark != null) {
            return getStyleFactory().mark(mark.getWellKnownName(), markFill, mark.getStroke());
        } else {
            return getStyleFactory().mark(StyleConstants.MARK_SQUARE, markFill, getStyleFactory().stroke());
        }

    }
}
