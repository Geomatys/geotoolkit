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
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.map.MapLayer;
import org.jdesktop.swingx.JXTitledSeparator;
import org.opengis.style.ChannelSelection;
import org.opengis.style.SelectedChannelType;

/**
 * Channel selection panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JChannelSelectionPane extends StyleElementEditor<ChannelSelection>{

    private MapLayer layer = null;

    /** 
     * Creates new form JFillPanel 
     */
    public JChannelSelectionPane() {
        super(ChannelSelection.class);
        initComponents();
        lock();
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
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        group = new ButtonGroup();
        guiGray = new JSelectedChannelTypePane();
        guiRed = new JSelectedChannelTypePane();
        guiLblGray = new JXTitledSeparator();
        guiLblRed = new JXTitledSeparator();
        guiGreen = new JSelectedChannelTypePane();
        guiLblGreen = new JXTitledSeparator();
        guiLblBlue = new JXTitledSeparator();
        guiBlue = new JSelectedChannelTypePane();
        jSeparator1 = new JSeparator();
        jPanel1 = new JPanel();
        guiCheckNative = new JRadioButton();
        guiChkRGB = new JRadioButton();
        guiChkGray = new JRadioButton();
        jSeparator2 = new JSeparator();

        guiLblGray.setForeground(new Color(102, 102, 102));
        guiLblGray.setFont(guiLblGray.getFont().deriveFont(guiLblGray.getFont().getStyle() | Font.BOLD));
        guiLblGray.setTitle(MessageBundle.getString("style.channelSelection.graychannel")); // NOI18N

        guiLblRed.setForeground(new Color(204, 0, 0));
        guiLblRed.setFont(guiLblRed.getFont().deriveFont(guiLblRed.getFont().getStyle() | Font.BOLD));
        guiLblRed.setTitle(MessageBundle.getString("style.channelSelection.redchannel")); // NOI18N

        guiLblGreen.setForeground(new Color(0, 204, 0));
        guiLblGreen.setFont(guiLblGreen.getFont().deriveFont(guiLblGreen.getFont().getStyle() | Font.BOLD));
        guiLblGreen.setTitle(MessageBundle.getString("style.channelSelection.greenchannel")); // NOI18N

        guiLblBlue.setForeground(new Color(0, 0, 204));
        guiLblBlue.setFont(guiLblBlue.getFont().deriveFont(guiLblBlue.getFont().getStyle() | Font.BOLD));
        guiLblBlue.setTitle(MessageBundle.getString("style.channelSelection.bluechannel")); // NOI18N

        jSeparator1.setOrientation(SwingConstants.VERTICAL);

        group.add(guiCheckNative);
        guiCheckNative.setSelected(true);
        guiCheckNative.setText(MessageBundle.getString("style.channelselection.native")); // NOI18N
        guiCheckNative.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiCheckNativeActionPerformed(evt);
            }
        });
        jPanel1.add(guiCheckNative);

        group.add(guiChkRGB);
        guiChkRGB.setText(MessageBundle.getString("rgb")); // NOI18N
        guiChkRGB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiChkRGBActionPerformed(evt);
            }
        });
        jPanel1.add(guiChkRGB);

        group.add(guiChkGray);
        guiChkGray.setText(MessageBundle.getString("single")); // NOI18N
        guiChkGray.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiChkGrayActionPerformed(evt);
            }
        });
        jPanel1.add(guiChkGray);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(guiLblRed, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guiRed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(guiLblGreen, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(guiLblBlue, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiGreen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiBlue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 7, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(guiLblGray, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiGray, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
            .addComponent(jSeparator2)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiBlue, guiGray, guiGreen, guiLblBlue, guiLblGray, guiLblGreen, guiLblRed, guiRed});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(jSeparator1, Alignment.LEADING)
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(guiLblRed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiRed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiLblGreen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiGreen, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiLblBlue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiBlue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(guiLblGray, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(guiGray, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
    private JXTitledSeparator guiLblBlue;
    private JXTitledSeparator guiLblGray;
    private JXTitledSeparator guiLblGreen;
    private JXTitledSeparator guiLblRed;
    private JSelectedChannelTypePane guiRed;
    private JPanel jPanel1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    // End of variables declaration//GEN-END:variables
}
