/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Johann Sorel
 *    (C) 2009 - 2013, Geomatys
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
package org.geotoolkit.gui.swing.render2d.decoration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.jdesktop.swingx.JXBusyLabel;

/**
 * Default information decoration
 *
 * @author Johann Sorel (Puzzle-GIS/Geomatys)
 * @module
 */
public class DefaultInformationDecoration extends JPanel implements InformationDecoration {

    private static final ImageIcon ICO_ERROR = IconBuilder.createIcon(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.RED);
    private static final ImageIcon ICO_WARNING = IconBuilder.createIcon(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.YELLOW);
    private static final ImageIcon ICO_INFO = IconBuilder.createIcon(FontAwesomeIcons.ICON_EXCLAMATION_TRIANGLE, 16, Color.BLUE);

    private JMap2D map = null;
    
    private static final Paint gp = new Color(0, 0, 0, 0.5f);
    private final JPanel messagesPanel = new JPanel(new GridLayout(-1, 1)){
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2d = (Graphics2D) g;
            final Dimension dim = messagesPanel.getSize();
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, dim.width, dim.height);
        }
        
    };
    private final JXBusyLabel guiPainting = new JXBusyLabel();
    private boolean lowlevel = true;

    public DefaultInformationDecoration() {
        super(new BorderLayout());
        
        guiPainting.setHorizontalAlignment(SwingConstants.CENTER);
        guiPainting.setVisible(false);
        
        add(BorderLayout.CENTER,guiPainting);
        add(BorderLayout.SOUTH,messagesPanel);
        setOpaque(false);
        messagesPanel.setOpaque(false);
    }

    @Override
    public void setPaintingIconVisible(final boolean b) {
        guiPainting.setVisible(b);
        guiPainting.setBusy(b);
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setMap2D(final JMap2D map) {
        this.map = map;
    }

    @Override
    public JMap2D getMap2D() {
        return map;
    }

    @Override
    public boolean isPaintingIconVisible() {
        return guiPainting.isVisible();
    }

    @Override
    public void displayMessage(final String text, final int time, final LEVEL level) {
        
        if(!lowlevel){
            if (level != LEVEL.ERROR && level != LEVEL.WARNING) {
                return;
            }
        }
        
        final JLabel label = new JLabel();
        label.setForeground(Color.WHITE);
        switch(level){
            case NORMAL :   label.setIcon(ICO_INFO); break;
            case INFO :     label.setIcon(ICO_INFO); break;
            case WARNING :  label.setIcon(ICO_WARNING); break;
            case ERROR :    label.setIcon(ICO_ERROR); break;
        }
        label.setText(text);
        messagesPanel.add(label);

        final Timer tim = new Timer(time, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagesPanel.remove(label);
                messagesPanel.revalidate();
            }
        });
        tim.setRepeats(false);
        tim.start();
    }

    @Override
    public void displayLowLevelMessages(final boolean display) {
        lowlevel = display;
    }

    @Override
    public boolean isDisplayingLowLevelMessages() {
        return lowlevel;
    }

    @Override
    public void dispose() {
    }
    
}
