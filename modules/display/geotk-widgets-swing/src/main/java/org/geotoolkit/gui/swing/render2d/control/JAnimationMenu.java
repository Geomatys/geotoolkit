/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2009, Johann Sorel
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

package org.geotoolkit.gui.swing.render2d.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel
 * @module
 */
public abstract class JAnimationMenu extends JMenu{

    private final ImageIcon ICON_PLAY = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLAY, 16, FontAwesomeIcons.DEFAULT_COLOR);;
    private final ImageIcon ICON_PLAY_OVER = IconBuilder.createIcon(FontAwesomeIcons.ICON_PLAY, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private final ImageIcon ICON_PAUSE = IconBuilder.createIcon(FontAwesomeIcons.ICON_STOP, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private final ImageIcon ICON_PAUSE_OVER = IconBuilder.createIcon(FontAwesomeIcons.ICON_STOP, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private final JMenuItem run = new JMenuItem();
    private final JSpinner factor = new JSpinner(new SpinnerNumberModel(1000d, 0.1d, Double.POSITIVE_INFINITY, 10d));
    private final JSpinner refresh = new JSpinner(new SpinnerNumberModel(500, 100, 60000, 100));
    private final JCheckBoxMenuItem backward = new JCheckBoxMenuItem(MessageBundle.format("backward"));

    private WeakReference<JMap2D> map = null;

    private volatile boolean running = false;

    private final Timer timer = new Timer(200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(map != null){
                    final JMap2D mp = map.get();
                    if(mp != null){
                        double step = (Double)factor.getValue();
                        if(backward.isSelected()){
                            step = -step;
                        }
                        update(mp,step);
                    }
                }
            }
        }){
            @Override
            public int getDelay() {
                return (Integer)refresh.getValue();
            }

        };

    public JAnimationMenu(){
        super(MessageBundle.format("animation"));

        final JPanel panFactor = new JPanel(new BorderLayout());
        panFactor.setOpaque(false);
        panFactor.add(BorderLayout.WEST,new JLabel(MessageBundle.format("temp_factor")));
        panFactor.add(BorderLayout.CENTER,factor);

        final JPanel panRefresh = new JPanel(new BorderLayout());
        panRefresh.setOpaque(false);
        panRefresh.add(BorderLayout.WEST,new JLabel(MessageBundle.format("temp_refresh")));
        panRefresh.add(BorderLayout.CENTER,refresh);

        add(run);
        addSeparator();
        add(panFactor);
        add(panRefresh);
        add(backward);

        run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
        run.setText( MessageBundle.format((running)?"stop":"run"));

        run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = !running;
                run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
                run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);
                run.setText( MessageBundle.format((running)?"stop":"run"));
                if(running){
                    timer.start();
                }else{
                    timer.stop();
                }
            }
        });

    }

    public void setSpeedFactor(final double d){
        factor.setValue(d);
    }

    public void setRefreshInterval(final double d){
        refresh.setValue(d);
    }

    public Integer getRefreshInterval(){
        return (Integer)refresh.getValue();
    }

    protected abstract void update(JMap2D map, double step);

    @Override
    public boolean isEnabled() {
        return getMap() != null;
    }

    public JMap2D getMap() {
        if(map != null){
            return map.get();
        }
        return null;
    }

    public void setMap(final JMap2D map) {
        this.map = new WeakReference<JMap2D>(map);
    }

}
