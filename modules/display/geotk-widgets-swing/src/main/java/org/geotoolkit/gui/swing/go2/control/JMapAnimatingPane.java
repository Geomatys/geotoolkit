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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class JMapAnimatingPane extends JToolBar{

    private final ImageIcon ICON_PLAY = IconBundle.getInstance().getIcon("32_play");
    private final ImageIcon ICON_PLAY_OVER = IconBundle.getInstance().getIcon("32_play_over");
     private final ImageIcon ICON_PAUSE = IconBundle.getInstance().getIcon("32_pause");
     private final ImageIcon ICON_PAUSE_OVER = IconBundle.getInstance().getIcon("32_pause_over");

    private final JButton run = new JButton();
    private final JSpinner factor = new JSpinner(new SpinnerNumberModel(1000d, 0.1d, Double.POSITIVE_INFINITY, 10d));
    private final JSpinner refresh = new JSpinner(new SpinnerNumberModel(500, 100, 60000, 100));

    private volatile boolean running = false;

    private final Timer timer = new Timer(200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(map != null && map.get() != null){
                    double step = (Double)factor.getValue() * (Integer)refresh.getValue();
                    Map2D m = map.get();
                    Date[] range = m.getCanvas().getController().getTemporalRange().clone();

                    if(range[0] != null){
                        range[0] = new Date(range[0].getTime() + (long)step);
                    }
                    if(range[1] != null){
                        range[1] = new Date(range[1].getTime() + (long)step);
                    }

                    m.getCanvas().getController().setTemporalRange(range[0], range[1]);
                }
            }
        }){

            @Override
            public int getDelay() {
                return (Integer)refresh.getValue();
            }

        };


    private WeakReference<Map2D> map = null;

    public JMapAnimatingPane(){
        setFloatable(false);
        setLayout(new BorderLayout());

        JPanel panFactor = new JPanel(new BorderLayout());
        panFactor.setOpaque(false);
        panFactor.add(BorderLayout.WEST,new JLabel(MessageBundle.getString("temp_factor")));
        panFactor.add(BorderLayout.CENTER,factor);

        JPanel panRefresh = new JPanel(new BorderLayout());
        panRefresh.setOpaque(false);
        panRefresh.add(BorderLayout.WEST,new JLabel(MessageBundle.getString("temp_refresh")));
        panRefresh.add(BorderLayout.CENTER,refresh);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setOpaque(false);
        bottom.add(panFactor);
        bottom.add(panRefresh);

        add(BorderLayout.CENTER,run);
        add(BorderLayout.SOUTH,bottom);

        run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
        run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);
        run.setBorder(null);
        run.setBorderPainted(false);
        run.setContentAreaFilled(false);
        run.setOpaque(false);

        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                running = !running;
                run.setIcon((running)? ICON_PAUSE : ICON_PLAY);
                run.setRolloverIcon((running)? ICON_PAUSE_OVER : ICON_PLAY_OVER);
                if(running){
                    timer.start();
                }else{
                    timer.stop();
                }
            }
        });

    }

    public Map2D getMap() {
        if(map != null){
            return map.get();
        }
        return null;
    }

    public void setMap(Map2D map) {
        this.map = new WeakReference<Map2D>(map);
    }

}
