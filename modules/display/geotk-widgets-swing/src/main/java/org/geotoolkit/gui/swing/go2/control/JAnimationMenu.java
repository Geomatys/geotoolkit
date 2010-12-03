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

package org.geotoolkit.gui.swing.go2.control;

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

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel
 * @module pending
 */
public abstract class JAnimationMenu extends JMenu{

    private final ImageIcon ICON_PLAY = IconBundle.getInstance().getIcon("16_play");
    private final ImageIcon ICON_PLAY_OVER = IconBundle.getInstance().getIcon("16_play");
    private final ImageIcon ICON_PAUSE = IconBundle.getInstance().getIcon("16_stop");
    private final ImageIcon ICON_PAUSE_OVER = IconBundle.getInstance().getIcon("16_stop");

    private final JMenuItem run = new JMenuItem();
    private final JSpinner factor = new JSpinner(new SpinnerNumberModel(1000d, 0.1d, Double.POSITIVE_INFINITY, 10d));
    private final JSpinner refresh = new JSpinner(new SpinnerNumberModel(500, 100, 60000, 100));
    private final JCheckBoxMenuItem backward = new JCheckBoxMenuItem(MessageBundle.getString("backward"));

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
        super(MessageBundle.getString("animation"));

        final JPanel panFactor = new JPanel(new BorderLayout());
        panFactor.setOpaque(false);
        panFactor.add(BorderLayout.WEST,new JLabel(MessageBundle.getString("temp_factor")));
        panFactor.add(BorderLayout.CENTER,factor);

        final JPanel panRefresh = new JPanel(new BorderLayout());
        panRefresh.setOpaque(false);
        panRefresh.add(BorderLayout.WEST,new JLabel(MessageBundle.getString("temp_refresh")));
        panRefresh.add(BorderLayout.CENTER,refresh);

        add(run);
        addSeparator();
        add(panFactor);
        add(panRefresh);
        add(backward);

        run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
        run.setText( MessageBundle.getString((running)?"stop":"run"));

        run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = !running;
                run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
                run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);
                run.setText( MessageBundle.getString((running)?"stop":"run"));
                if(running){
                    timer.start();
                }else{
                    timer.stop();
                }
            }
        });

    }

    public void setSpeedFactor(double d){
        factor.setValue(d);
    }

    public void setRefreshInterval(double d){
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

    public void setMap(JMap2D map) {
        this.map = new WeakReference<JMap2D>(map);
    }

}
