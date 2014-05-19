/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.JPreview;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import static org.geotoolkit.gui.swing.style.StyleElementEditor.PROPERTY_UPDATED;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Stroke;

/**
 * Stroke controller editor
 *
 * @author Fabien Rétif
 */
public class JStrokeControlPane extends StyleElementEditor<Stroke> {

    private final AbstractAction strokeChange = new AbstractAction(MessageBundle.getString("change")) {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(JStrokeControlPane.this, paneStrokeChooser, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)) {
                final Stroke created = create();
                parse(created);
                JStrokeControlPane.this.firePropertyChange(PROPERTY_UPDATED, null, created);
            }
        }
    };
    
    private final JButton guiStrokeButton = new JButton(strokeChange);
    private final JPreview guiStrokeLabel = new JPreview();
    private final JStrokePane paneStrokeChooser = new JStrokePane();
    private MapLayer layer = null;

    /**
     * Creates new form JStrokeControlPane
     */
    public JStrokeControlPane() {
        super(new BorderLayout(8,8),Stroke.class);

        guiStrokeLabel.setPreferredSize(new Dimension(32, 32));
        guiStrokeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                strokeChange.actionPerformed(new ActionEvent(JStrokeControlPane.this, evt.getID(), PROPERTY_UPDATED));
            }
        });
        
        add(guiStrokeLabel, BorderLayout.WEST);
        add(guiStrokeButton, BorderLayout.EAST);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        paneStrokeChooser.setLayer(layer);
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
     */
    @Override
    public void parse(final Stroke stroke) {
        if (stroke != null) {
            //Create the icon image            
            guiStrokeLabel.parse(stroke);
            // Set the tool pane
            paneStrokeChooser.parse(stroke);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Stroke create() {
        return paneStrokeChooser.create();
    }

    public void setActive(boolean bool) {
        guiStrokeButton.setEnabled(bool);
        guiStrokeLabel.setVisible(bool);
    }
    
    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{};
    }

}
