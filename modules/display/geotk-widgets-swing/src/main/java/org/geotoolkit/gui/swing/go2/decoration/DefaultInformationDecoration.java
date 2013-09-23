/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Timer;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;

/**
 * Default information decoration
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultInformationDecoration extends JPanel implements InformationDecoration {

    private static final ImageIcon ICO_ERROR = IconBuilder.createIcon(FontAwesomeIcons.ICON_WARNING_SIGN, 16, Color.RED);
    private static final ImageIcon ICO_WARNING = IconBuilder.createIcon(FontAwesomeIcons.ICON_WARNING_SIGN, 16, Color.YELLOW);
    private static final ImageIcon ICO_INFO = IconBuilder.createIcon(FontAwesomeIcons.ICON_WARNING_SIGN, 16, Color.BLUE);
    private static final ImageIcon ICO_PAINTING = IconBuilder.createIcon(FontAwesomeIcons.ICON_COGS, 24, Color.DARK_GRAY);

    private JMap2D map = null;;
    private final Map<String, LEVEL> messages = new LinkedHashMap<>();
    private boolean lowlevel = true;
    private boolean icon = false;



    public DefaultInformationDecoration() {
        super(null);
        OverlayLayout layout = new OverlayLayout(this);
        setLayout(layout);
        setOpaque(false);
    }


    @Override
    public void setPaintingIconVisible(final boolean b) {
        icon = b;
        revalidate();
        repaint();
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
        return icon;
    }

    @Override
    public void displayMessage(final String text, final int time, final LEVEL level) {

        messages.put(text, level);

        repaint();

        Timer tim = new Timer(time, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                messages.remove(text);
                repaint();
            }
        });
        tim.setRepeats(false);
        tim.start();

    }

    public String getLastDisplayedMessage() {
        return null;
    }

    @Override
    public void paintComponent(final Graphics g) {
//        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;

        if(icon){
            final Image image = ICO_PAINTING.getImage();
            int x = (getWidth() - image.getWidth(null)) / 2;
            int y = (getHeight() - image.getHeight(null)) / 2;
            g2.drawImage(ICO_PAINTING.getImage(), x, y, this);
        }

        final Set<String> keys = messages.keySet();
        final Object[] ite = keys.toArray();
        final List<String> msgs = new ArrayList<>();

        int height = 0;
        if (!lowlevel) {
            for (int n = 0; n < ite.length; n++) {
                LEVEL lvl = messages.get(ite[n]);
                if (lvl == LEVEL.ERROR || lvl == LEVEL.WARNING) {
                    msgs.add((String) ite[n]);
                    height++;
                }
            }
        } else {
            for (int n = 0; n < ite.length; n++) {
                msgs.add((String) ite[n]);
                height++;
            }
        }

        height = (height > 0) ? (height) * 20 + 5 : 0;
        Paint gp = new Color(0, 0, 0, 0.5f);
        g2.setPaint(gp);
        g2.fillRect(0, getHeight() - height, getWidth(), height);

        g2.setPaint(Color.WHITE);

        int i = getHeight() - 22;

        for (int n = 0; n < msgs.size(); n++) {
            String text = msgs.get(n);
            LEVEL lvl = messages.get(text);
            switch (lvl) {
                case ERROR:
                    g2.drawImage(ICO_ERROR.getImage(), 3, i + 2, this);
                    g2.drawString((String) text, 20, i + 15);
                    break;
                case WARNING:
                    g2.drawImage(ICO_WARNING.getImage(), 3, i + 2, this);
                    g2.drawString((String) text, 20, i + 15);
                    break;
                case INFO:
                    g2.drawImage(ICO_INFO.getImage(), 3, i + 2, this);
                    g2.drawString((String) text, 20, i + 15);
                    break;
                case NORMAL:
                    g2.drawString((String) text, 3, i + 15);
                    break;
            }

            i -= 20;
        }

    }

    @Override
    public void displayLowLevelMessages(final boolean display) {
        lowlevel = display;
        repaint();
    }

    @Override
    public boolean isDisplayingLowLevelMessages() {
        return lowlevel;
    }

    @Override
    public void dispose() {
    }
}
