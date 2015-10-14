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
package org.geotoolkit.gui.swing.style;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.ChannelSelection;
import org.opengis.style.SelectedChannelType;

/**
 * Channel selection panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JChannelSelectionPane extends StyleElementEditor<ChannelSelection> implements PropertyChangeListener {

    private MapLayer layer = null;

    /** 
     * Creates new form JFillPanel 
     */
    public JChannelSelectionPane() {
        super(ChannelSelection.class);
        initComponents();
        lock();
        guiGray.addPropertyChangeListener(this);
        guiRed.addPropertyChangeListener(this);
        guiGreen.addPropertyChangeListener(this);
        guiBlue.addPropertyChangeListener(this);
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        guiGray.setLayer(layer);
        guiRed.setLayer(layer);
        guiGreen.setLayer(layer);
        guiBlue.setLayer(layer);
    }

    @Override
    public MapLayer getLayer(){
        return layer;
    }
    
    @Override
    public void parse(final ChannelSelection channel) {
        guiGray.parse(null);
        guiRed.parse(null);
        guiGreen.parse(null);
        guiBlue.parse(null);
            
        if (channel == null) {
            guiCheckNative.setSelected(true);
        }else{
            final SelectedChannelType gray = channel.getGrayChannel();
            final SelectedChannelType[] rgb = channel.getRGBChannels();
            if(gray!=null){
                guiChkGray.setSelected(true);
                guiGray.parse(gray);
            }else if(rgb!=null){
                guiChkRGB.setSelected(true);
                guiRed.parse(rgb[0]);
                guiGreen.parse(rgb[1]);
                guiBlue.parse(rgb[2]);
            }else{
                //native
                guiCheckNative.setSelected(true);
            }
        }
        
        lock();
    }

    @Override
    public ChannelSelection create() {

        if(guiCheckNative.isSelected()){
            //native image colors
            return null;
        }else if(guiChkRGB.isSelected()){
            final SelectedChannelType redtype = guiRed.create();
            final SelectedChannelType greentype = guiGreen.create();
            final SelectedChannelType bluetype = guiBlue.create();
            return getStyleFactory().channelSelection(redtype,greentype,bluetype);
        }else if(guiChkGray.isSelected()){
            final SelectedChannelType graytype = guiGray.create();
            return getStyleFactory().channelSelection(graytype);
        }
        
        return null;
    }
    
    private void lock(){
        
        if(guiCheckNative.isSelected()){
            guiRed.setEnabled(false);
            guiGreen.setEnabled(false);
            guiBlue.setEnabled(false);
            guiGray.setEnabled(false);
            guiLblRed.setEnabled(false);
            guiLblGreen.setEnabled(false);
            guiLblBlue.setEnabled(false);
            guiLblGray.setEnabled(false);
        }else if(guiChkRGB.isSelected()){
            guiRed.setEnabled(true);
            guiGreen.setEnabled(true);
            guiBlue.setEnabled(true);
            guiGray.setEnabled(false);
            guiLblRed.setEnabled(true);
            guiLblGreen.setEnabled(true);
            guiLblBlue.setEnabled(true);
            guiLblGray.setEnabled(false);
        }else if(guiChkGray.isSelected()){
            guiRed.setEnabled(false);
            guiGreen.setEnabled(false);
            guiBlue.setEnabled(false);
            guiGray.setEnabled(true);
            guiLblRed.setEnabled(false);
            guiLblGreen.setEnabled(false);
            guiLblBlue.setEnabled(false);
            guiLblGray.setEnabled(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(PROPERTY_UPDATED, null, create());
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{guiRed,guiGreen,guiBlue,guiGray};
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group = new ButtonGroup();
        jPanel1 = new JPanel();
        guiCheckNative = new JRadioButton();
        guiChkRGB = new JRadioButton();
        guiChkGray = new JRadioButton();
        jPanel2 = new JPanel();
        jPanel4 = new JPanel();
        guiRed = new JSelectedChannelTypePane();
        guiGreen = new JSelectedChannelTypePane();
        guiBlue = new JSelectedChannelTypePane();
        guiLblRed = new JLabel();
        guiLblGreen = new JLabel();
        guiLblBlue = new JLabel();
        jPanel3 = new JPanel();
        guiGray = new JSelectedChannelTypePane();
        guiLblGray = new JLabel();

        group.add(guiCheckNative);
        guiCheckNative.setSelected(true);
        guiCheckNative.setText(MessageBundle.format("style_channelselection_native")); // NOI18N
        guiCheckNative.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiCheckNativeActionPerformed(evt);
            }
        });
        jPanel1.add(guiCheckNative);

        group.add(guiChkRGB);
        guiChkRGB.setText(MessageBundle.format("rgb")); // NOI18N
        guiChkRGB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiChkRGBActionPerformed(evt);
            }
        });
        jPanel1.add(guiChkRGB);

        group.add(guiChkGray);
        guiChkGray.setText(MessageBundle.format("single")); // NOI18N
        guiChkGray.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiChkGrayActionPerformed(evt);
            }
        });
        jPanel1.add(guiChkGray);

        jPanel2.setLayout(new GridLayout());

        guiLblRed.setFont(guiLblRed.getFont().deriveFont(guiLblRed.getFont().getStyle() | Font.BOLD));
        guiLblRed.setForeground(new Color(255, 0, 51));
        guiLblRed.setText(MessageBundle.format("style_channelSelection_redchannel")); // NOI18N

        guiLblGreen.setFont(guiLblGreen.getFont().deriveFont(guiLblGreen.getFont().getStyle() | Font.BOLD));
        guiLblGreen.setForeground(new Color(0, 204, 0));
        guiLblGreen.setText(MessageBundle.format("style_channelSelection_greenchannel")); // NOI18N

        guiLblBlue.setFont(guiLblBlue.getFont().deriveFont(guiLblBlue.getFont().getStyle() | Font.BOLD));
        guiLblBlue.setForeground(new Color(51, 51, 255));
        guiLblBlue.setText(MessageBundle.format("style_channelSelection_bluechannel")); // NOI18N

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiRed, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(guiLblRed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiLblGreen, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                    .addComponent(guiGreen, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(guiLblBlue, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiBlue, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiLblRed)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiRed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiLblGreen)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiGreen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiLblBlue)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiBlue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4);

        guiLblGray.setFont(guiLblGray.getFont().deriveFont(guiLblGray.getFont().getStyle() | Font.BOLD));
        guiLblGray.setForeground(new Color(153, 153, 153));
        guiLblGray.setText(MessageBundle.format("style_channelSelection_graychannel")); // NOI18N

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiLblGray, GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .addComponent(guiGray, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiLblGray)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(guiGray, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 490, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(220, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void guiChkGrayActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiChkGrayActionPerformed
    lock();
}//GEN-LAST:event_guiChkGrayActionPerformed

private void guiChkRGBActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiChkRGBActionPerformed
    lock();
}//GEN-LAST:event_guiChkRGBActionPerformed

    private void guiCheckNativeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiCheckNativeActionPerformed
        lock();
    }//GEN-LAST:event_guiCheckNativeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup group;
    private JSelectedChannelTypePane guiBlue;
    private JRadioButton guiCheckNative;
    private JRadioButton guiChkGray;
    private JRadioButton guiChkRGB;
    private JSelectedChannelTypePane guiGray;
    private JSelectedChannelTypePane guiGreen;
    private JLabel guiLblBlue;
    private JLabel guiLblGray;
    private JLabel guiLblGreen;
    private JLabel guiLblRed;
    private JSelectedChannelTypePane guiRed;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    // End of variables declaration//GEN-END:variables
}
