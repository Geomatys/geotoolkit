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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Stroke;

/**
 * Stroke panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JStrokePane extends StyleElementEditor<Stroke> {

    private MapLayer layer = null;
    private Stroke stroke = null;
    private GraphicFill graphicFill = null;
    private GraphicStroke graphicStroke = null;

    /** 
     * Creates new form JStrokePanel 
     */
    public JStrokePane() {
        super(Stroke.class);
        initComponents();
        init();

    }

    private void init() {
        butFill.setEnabled(false);
        butStroke.setEnabled(false);
        GuiStrokeWidth.setModel(1d, 0d, Double.MAX_VALUE, 1d);
        GuiStrokeAlpha.setModel(1d, 0d, 1d, 0.1d);
    }

    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
        GuiStrokeWidth.setLayer(layer);
        GuiStrokeColor.setLayer(layer);
        GuiStrokeAlpha.setLayer(layer);
        GuiStrokeLineCap.setLayer(layer);
        GuiStrokeLineJoin.setLayer(layer);
    }

    @Override
    public MapLayer getLayer() {
        return layer;
    }

    @Override
    public void parse(final Stroke stroke) {

        this.stroke = stroke;

        if (stroke != null) {
            GuiStrokeLineCap.parse(stroke.getLineCap());
            GuiStrokeLineJoin.parse(stroke.getLineJoin());
            GuiStrokeDashes.setDashes(stroke.getDashArray());
            GuiStrokeDashes.setOffset(stroke.getDashOffset());
            GuiStrokeWidth.parse(stroke.getWidth());
            GuiStrokeColor.parse(stroke.getColor());
            GuiStrokeAlpha.parse(stroke.getOpacity());

            if (stroke.getGraphicFill() != null) {
                guiGraphicFill.setSelected(true);
            } else if (stroke.getGraphicStroke() != null) {
                guiGraphicStroke.setSelected(true);
            } else {
                guiGraphicColor.setSelected(true);
            }
        }
    }

    @Override
    public Stroke create() {
        if (guiGraphicFill.isSelected() && graphicFill != null) {
            stroke = getStyleFactory().stroke(
                    graphicFill,
                    GuiStrokeColor.create(),
                    GuiStrokeAlpha.create(),
                    GuiStrokeWidth.create(),
                    GuiStrokeLineJoin.create(),
                    GuiStrokeLineCap.create(),
                    GuiStrokeDashes.getDashes(),
                    GuiStrokeDashes.getOffset());
        } else if (guiGraphicStroke.isSelected() && graphicStroke != null) {
            stroke = getStyleFactory().stroke(
                    graphicStroke,
                    GuiStrokeColor.create(),
                    GuiStrokeAlpha.create(),
                    GuiStrokeWidth.create(),
                    GuiStrokeLineJoin.create(),
                    GuiStrokeLineCap.create(),
                    GuiStrokeDashes.getDashes(),
                    GuiStrokeDashes.getOffset());
        } else {
            stroke = getStyleFactory().stroke(
                    GuiStrokeColor.create(),
                    GuiStrokeAlpha.create(),
                    GuiStrokeWidth.create(),
                    GuiStrokeLineJoin.create(),
                    GuiStrokeLineCap.create(),
                    GuiStrokeDashes.getDashes(),
                    GuiStrokeDashes.getOffset());
        }

        return stroke;
    }

    private void showGraphicFillDialog() {
        final JGraphicFillPane pane = new JGraphicFillPane();
        pane.setLayer(layer);

        if (stroke != null) {
            pane.parse(stroke.getGraphicFill());
        }

        JOptionDialog.show(null, pane,JOptionPane.OK_OPTION);
        graphicFill = pane.create();
        firePropertyChange(PROPERTY_UPDATED, null, create());
    }

    @Override
    protected Object[] getFirstColumnComponents() {
        return new Object[]{guiLabelAlpha,guiLabelCap,guiLabelDashes,guiLabelJoin,guiLabelWidth};
    }
    
    private void showGraphicStrokeDialog() {
        //TODO
//        final JGraphicFillPane pane = new JGraphicFillPane();
//        pane.setLayer(layer);
//
//        if (stroke != null) {
//            pane.parse(stroke.getGraphicFill());
//        }
//
//        final JSimpleDialog dia = new JSimpleDialog(null, true, pane);
//        dia.pack();
//        dia.setLocationRelativeTo(butFill);
//        dia.setVisible(true);
//
//        graphicFill = pane.create();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        typeGroup = new ButtonGroup();
        GuiStrokeDashes = new JDashPane();
        butFill = new JButton();
        butStroke = new JButton();
        guiLabelCap = new JLabel();
        guiLabelJoin = new JLabel();
        guiLabelAlpha = new JLabel();
        guiLabelWidth = new JLabel();
        guiLabelDashes = new JLabel();
        GuiStrokeColor = new JColorExpressionPane();
        GuiStrokeLineJoin = new JLineJoinExpressionPane();
        GuiStrokeLineCap = new JLineCapExpressionPane();
        GuiStrokeWidth = new JNumberExpressionPane();
        GuiStrokeAlpha = new JNumberExpressionPane();
        guiGraphicColor = new JRadioButton();
        guiGraphicFill = new JRadioButton();
        guiGraphicStroke = new JRadioButton();

        setOpaque(false);

        GuiStrokeDashes.setOpaque(false);
        GuiStrokeDashes.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        butFill.setText(MessageBundle.format("fill")); // NOI18N
        butFill.setPreferredSize(new Dimension(68, 22));
        butFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butFillActionPerformed(evt);
            }
        });

        butStroke.setText(MessageBundle.format("stroke")); // NOI18N
        butStroke.setEnabled(false);
        butStroke.setPreferredSize(new Dimension(68, 22));
        butStroke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butStrokeActionPerformed(evt);
            }
        });

        guiLabelCap.setText(MessageBundle.format("linecap")); // NOI18N

        guiLabelJoin.setText(MessageBundle.format("linejoin")); // NOI18N

        guiLabelAlpha.setText(MessageBundle.format("opacity")); // NOI18N

        guiLabelWidth.setText(MessageBundle.format("width")); // NOI18N

        guiLabelDashes.setText(MessageBundle.format("dashes")); // NOI18N

        GuiStrokeColor.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        GuiStrokeLineJoin.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        GuiStrokeLineCap.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        GuiStrokeWidth.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        GuiStrokeAlpha.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JStrokePane.this.propertyChange(evt);
            }
        });

        typeGroup.add(guiGraphicColor);
        guiGraphicColor.setSelected(true);
        guiGraphicColor.setText(MessageBundle.format("graphic_color")); // NOI18N

        typeGroup.add(guiGraphicFill);
        guiGraphicFill.setText(MessageBundle.format("graphic_fill")); // NOI18N
        guiGraphicFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiGraphicFillActionPerformed(evt);
            }
        });

        typeGroup.add(guiGraphicStroke);
        guiGraphicStroke.setText(MessageBundle.format("graphic_stroke")); // NOI18N
        guiGraphicStroke.setEnabled(false);
        guiGraphicStroke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiGraphicStrokeActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiLabelCap)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeLineCap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiLabelJoin)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeLineJoin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiLabelAlpha)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeAlpha, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiLabelDashes)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeDashes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicFill)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(butFill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicStroke)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(butStroke, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiLabelWidth)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeWidth, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicColor)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiLabelAlpha, guiLabelCap, guiLabelDashes, guiLabelJoin, guiLabelWidth});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiGraphicColor, guiGraphicFill, guiGraphicStroke});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(guiLabelCap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeLineCap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(guiLabelJoin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeLineJoin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(guiLabelWidth, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(guiLabelAlpha, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(guiLabelDashes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeDashes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(guiGraphicColor)
                    .addComponent(GuiStrokeColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiGraphicFill)
                    .addComponent(butFill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(guiGraphicStroke)
                    .addComponent(butStroke, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeLineCap, guiLabelCap});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeLineJoin, guiLabelJoin});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeColor, guiGraphicColor});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {butFill, guiGraphicFill});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {butStroke, guiGraphicStroke});

    }// </editor-fold>//GEN-END:initComponents

    private void butStrokeActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_butStrokeActionPerformed
        if(graphicStroke == null){
            showGraphicStrokeDialog();
        }
}//GEN-LAST:event_butStrokeActionPerformed

    private void butFillActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_butFillActionPerformed
        if(graphicFill == null){
            showGraphicFillDialog();
        }
    }//GEN-LAST:event_butFillActionPerformed

private void guiGraphicFillActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiGraphicFillActionPerformed
    if(graphicFill == null){
        showGraphicFillDialog();
    }
}//GEN-LAST:event_guiGraphicFillActionPerformed

private void guiGraphicStrokeActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_guiGraphicStrokeActionPerformed
    if(graphicStroke == null){
        showGraphicStrokeDialog();
    }
}//GEN-LAST:event_guiGraphicStrokeActionPerformed

    private void propertyChange(PropertyChangeEvent evt) {//GEN-FIRST:event_propertyChange
        if (PROPERTY_UPDATED.equalsIgnoreCase(evt.getPropertyName())) {            
            firePropertyChange(PROPERTY_UPDATED, null, create());
        }
    }//GEN-LAST:event_propertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JNumberExpressionPane GuiStrokeAlpha;
    private JColorExpressionPane GuiStrokeColor;
    private JDashPane GuiStrokeDashes;
    private JLineCapExpressionPane GuiStrokeLineCap;
    private JLineJoinExpressionPane GuiStrokeLineJoin;
    private JNumberExpressionPane GuiStrokeWidth;
    private JButton butFill;
    private JButton butStroke;
    private JRadioButton guiGraphicColor;
    private JRadioButton guiGraphicFill;
    private JRadioButton guiGraphicStroke;
    private JLabel guiLabelAlpha;
    private JLabel guiLabelCap;
    private JLabel guiLabelDashes;
    private JLabel guiLabelJoin;
    private JLabel guiLabelWidth;
    private ButtonGroup typeGroup;
    // End of variables declaration//GEN-END:variables
}
