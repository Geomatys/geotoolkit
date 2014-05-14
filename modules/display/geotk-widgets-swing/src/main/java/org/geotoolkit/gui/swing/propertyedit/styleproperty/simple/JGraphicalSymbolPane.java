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
import javax.swing.JTabbedPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JBankView;
import org.geotoolkit.gui.swing.style.JExternalGraphicPane;
import org.geotoolkit.gui.swing.style.StyleBank;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Mark;

/**
 * Graphical symbol editor.
 *
 * @author Fabien Rétif (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class JGraphicalSymbolPane extends StyleElementEditor<GraphicalSymbol> {

    private final JBankView<Mark> guiMarkPane = new JBankView<>(Mark.class);
    private final JExternalGraphicPane guiExternalGraphicPane = new JExternalGraphicPane();;
    private final JTabbedPane guiTabs = new JTabbedPane();
    
    private MapLayer layer = null;

    /**
     * Creates new form JGraphicalSymbolPane
     */
    public JGraphicalSymbolPane() {
        super(new BorderLayout(), GraphicalSymbol.class);

        guiMarkPane.setCandidates(StyleBank.getInstance().getCandidates(new StyleBank.ByClassComparator(Mark.class)));
        
        guiTabs.addTab(MessageBundle.getString("wellknownedform"), guiMarkPane);
        guiTabs.addTab(MessageBundle.getString("external"), guiExternalGraphicPane);
        guiTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                guiMarkPane.parse(null);
                guiExternalGraphicPane.parse(null);
            }
        });

        add(guiTabs, BorderLayout.CENTER);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiExternalGraphicPane.setLayer(layer);
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
     * This method parses a GraphicalSymbol object and sets up all graphic
     * components in order to display it. If the object can not be parse, we
     * display a square mark
     */
    @Override
    public void parse(final GraphicalSymbol graphicalSymbol) {

        if (graphicalSymbol instanceof Mark) {
            guiTabs.setSelectedComponent(guiMarkPane);
            guiMarkPane.parse((Mark) graphicalSymbol);
        } else if (graphicalSymbol instanceof ExternalGraphic) {
            guiTabs.setSelectedComponent(guiExternalGraphicPane);
            guiExternalGraphicPane.parse((ExternalGraphic) graphicalSymbol);
        } else {
            guiTabs.setSelectedComponent(guiMarkPane);
            guiMarkPane.parse(getStyleFactory().getSquareMark());
        }
    }

    /**
     * {@inheritDoc }
     * This method creates a Graphical Symbol object from the user selection.
     */
    @Override
    public GraphicalSymbol create() {
        GraphicalSymbol symbol = guiExternalGraphicPane.create();
        if (symbol == null) {
            symbol = guiMarkPane.create();
        }
        return symbol;
    }
    
    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }

}
