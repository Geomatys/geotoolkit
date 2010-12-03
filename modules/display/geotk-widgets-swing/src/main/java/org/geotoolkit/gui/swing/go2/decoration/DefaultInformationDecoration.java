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

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.Timer;
import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 * Default information decoration
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class DefaultInformationDecoration extends JPanel implements InformationDecoration {

    private JMap2D map = null;;
    private final Map<String, LEVEL> messages = new LinkedHashMap<String, LEVEL>();
    private boolean lowlevel = true;



    public DefaultInformationDecoration() {
        super(null);
        OverlayLayout layout = new OverlayLayout(this);
        setLayout(layout);
        setOpaque(false);
    }


    @Override
    public void setPaintingIconVisible(boolean b) {
        revalidate();
        repaint();
    }

    @Override
    public void refresh() {
        repaint();
    }

    @Override
    public JComponent geComponent() {
        return this;
    }

    @Override
    public void setMap2D(JMap2D map) {
        this.map = map;
    }

    @Override
    public JMap2D getMap2D() {
        return map;
    }

    @Override
    public boolean isPaintingIconVisible() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void displayMessage(final String text, int time, LEVEL level) {

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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//
//        if (drawing) {
//        }
//
//        Set<String> keys = messages.keySet();
//        Object[] ite = keys.toArray();
//        List<String> msgs = new ArrayList<String>();
//
//        int height = 0;
//        if (!lowlevel) {
//            for (int n = 0; n < ite.length; n++) {
//                LEVEL lvl = messages.get(ite[n]);
//                if (lvl == LEVEL.ERROR || lvl == LEVEL.WARNING) {
//                    msgs.add((String) ite[n]);
//                    height++;
//                }
//            }
//        } else {
//            for (int n = 0; n < ite.length; n++) {
//                msgs.add((String) ite[n]);
//                height++;
//            }
//        }
//
//        height = (height > 0) ? (height) * 20 + 5 : 0;
//        Paint gp = new Color(0, 0, 0, 0.5f);
//        g2.setPaint(gp);
//        g2.fillRect(0, getHeight() - height, getWidth(), height);
//
//        g2.setPaint(Color.WHITE);
//
//        int i = getHeight() - 22;
//
//        for (int n = 0; n < msgs.size(); n++) {
//            String text = msgs.get(n);
//            LEVEL lvl = messages.get(text);
//            switch (lvl) {
//                case ERROR:
//                    g2.drawImage(ICO_ERROR.getImage(), 3, i + 2, this);
//                    g2.drawString((String) text, 20, i + 15);
//                    break;
//                case WARNING:
//                    g2.drawImage(ICO_WARNING.getImage(), 3, i + 2, this);
//                    g2.drawString((String) text, 20, i + 15);
//                    break;
//                case INFO:
//                    g2.drawImage(ICO_INFO.getImage(), 3, i + 2, this);
//                    g2.drawString((String) text, 20, i + 15);
//                    break;
//                case NORMAL:
//                    g2.drawString((String) text, 3, i + 15);
//                    break;
//            }
//
//            i -= 20;
//        }

    }

    @Override
    public void displayLowLevelMessages(boolean display) {
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
