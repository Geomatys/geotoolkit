/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C)2010, Geomatys
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.measure.unit.SI;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotoolkit.display.canvas.CanvasController2D;
import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.navigator.DoubleNavigatorModel;
import org.geotoolkit.gui.swing.navigator.DoubleRenderer;
import org.geotoolkit.gui.swing.navigator.JNavigator;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JMapElevationLine extends JPanel implements PropertyChangeListener{

    private final SpinnerNumberModel modelHaut;
    private final SpinnerNumberModel modelBas;
    private final JButton reset;
    private final JNavigator<Double> guiNav = new JNavigator<Double>(new DoubleNavigatorModel());
    
    private volatile Map2D map = null;

    public JMapElevationLine(){
        super(new BorderLayout());

        guiNav.setModelRenderer(new DoubleRenderer());
        guiNav.setOrientation(SwingConstants.WEST);

        modelHaut = new SpinnerNumberModel();
        modelHaut.setStepSize(10);
        modelHaut.setMinimum(Double.NEGATIVE_INFINITY);
        modelHaut.setMaximum(Double.POSITIVE_INFINITY);
        modelHaut.setValue(Double.POSITIVE_INFINITY);

        modelBas = new SpinnerNumberModel();
        modelBas.setStepSize(10);
        modelBas.setMinimum(Double.NEGATIVE_INFINITY);
        modelBas.setMaximum(Double.POSITIVE_INFINITY);
        modelBas.setValue(Double.NEGATIVE_INFINITY);

        reset = new JButton("reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(map == null) return;
                map.getCanvas().getController().setElevationRange(null, null, SI.METRE);
            }
        });

        final JSpinner haut = new JSpinner(modelHaut);
        final JSpinner bas = new JSpinner(modelBas);

        ChangeListener lst = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if(map == null) return;

                Double vh = (Double) modelHaut.getValue();
                Double vb = (Double) modelBas.getValue();
                if(vh.isInfinite()) vh = null;
                if(vb.isInfinite()) vb = null;
                
                map.getCanvas().getController().setElevationRange(vb, vh, SI.METRE);

            }
        };


        modelBas.addChangeListener(lst);
        modelHaut.addChangeListener(lst);

        JPanel north = new JPanel();
        north.add(new JLabel("min"));
        north.add(bas);
        north.add(new JLabel("max"));
        north.add(haut);
        north.add(reset);

        add(BorderLayout.NORTH,north);
        add(BorderLayout.CENTER,guiNav);


    }
    
    public Map2D getMap() {
        return map;
    }

    public void setMap(Map2D map) {
        if(this.map != null){
            this.map.getCanvas().removePropertyChangeListener(this);
        }
        this.map = map;
        if(map != null){
            this.map.getCanvas().addPropertyChangeListener(this);
        }
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(CanvasController2D.ELEVATION_PROPERTY)){
            Double[] range = map.getCanvas().getController().getElevationRange();

            if(range == null){
                modelBas.setValue(Double.NEGATIVE_INFINITY);
                modelHaut.setValue(Double.POSITIVE_INFINITY);
            }else{
                if(range[0] != null){
                    modelBas.setValue(range[0]);
                }else{
                    modelBas.setValue(Double.NEGATIVE_INFINITY);
                }

                if(range[1] != null){
                    modelHaut.setValue(range[1]);
                }else{
                    modelHaut.setValue(Double.POSITIVE_INFINITY);
                }
                
            }

        }
    }

}
