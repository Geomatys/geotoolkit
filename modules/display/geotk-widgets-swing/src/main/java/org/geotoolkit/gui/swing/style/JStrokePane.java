/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.style.JSimpleDialog;
import org.geotoolkit.gui.swing.style.JGraphicFillPane;
import org.geotoolkit.gui.swing.style.StyleElementEditor;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Stroke;

/**
 * Stroke panel
 * 
 * @author Johann Sorel
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
        super();
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
    public void setLayer(MapLayer layer) {
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
    public void parse(Stroke stroke) {

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

        final JSimpleDialog dia = new JSimpleDialog(null, true, pane);
        dia.pack();
        dia.setLocationRelativeTo(butFill);
        dia.setVisible(true);

        graphicFill = pane.create();
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
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        lbl_b_alpha = new JLabel();
        lbl_b_width = new JLabel();
        lbl_b_alpha1 = new JLabel();
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

        butFill.setText(MessageBundle.getString("fill")); // NOI18N
        butFill.setPreferredSize(new Dimension(68, 22));
        butFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butFillActionPerformed(evt);
            }
        });

        butStroke.setText(MessageBundle.getString("stroke")); // NOI18N
        butStroke.setEnabled(false);





        butStroke.setPreferredSize(new Dimension(68, 22));
        butStroke.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                butStrokeActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText(MessageBundle.getString("linecap")); // NOI18N
        jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel2.setText(MessageBundle.getString("linejoin")); // NOI18N
        lbl_b_alpha.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl_b_alpha.setText(MessageBundle.getString("opacity")); // NOI18N
        lbl_b_width.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl_b_width.setText(MessageBundle.getString("width")); // NOI18N
        lbl_b_alpha1.setHorizontalAlignment(SwingConstants.RIGHT);
        lbl_b_alpha1.setText(MessageBundle.getString("dashes")); // NOI18N
        typeGroup.add(guiGraphicColor);
        guiGraphicColor.setSelected(true);
        guiGraphicColor.setText(MessageBundle.getString("graphic_color")); // NOI18N
        guiGraphicColor.setOpaque(false);

        typeGroup.add(guiGraphicFill);
        guiGraphicFill.setText(MessageBundle.getString("graphic_fill")); // NOI18N
        guiGraphicFill.setOpaque(false);
        guiGraphicFill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiGraphicFillActionPerformed(evt);
            }
        });

        typeGroup.add(guiGraphicStroke);
        guiGraphicStroke.setText(MessageBundle.getString("graphic_stroke")); // NOI18N
        guiGraphicStroke.setEnabled(false);
        guiGraphicStroke.setOpaque(false);
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
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeLineCap, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeLineJoin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbl_b_width)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbl_b_alpha)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(lbl_b_alpha1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeDashes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicColor)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(GuiStrokeColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicFill)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(butFill, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(guiGraphicStroke)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(butStroke, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel1, jLabel2, lbl_b_alpha, lbl_b_alpha1, lbl_b_width});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {guiGraphicColor, guiGraphicFill, guiGraphicStroke});

        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeLineCap, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeLineJoin, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(lbl_b_width, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeWidth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                    .addComponent(lbl_b_alpha, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(GuiStrokeAlpha, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(lbl_b_alpha1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeLineCap, jLabel1});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeLineJoin, jLabel2});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {GuiStrokeColor, guiGraphicColor});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {butFill, guiGraphicFill});

        layout.linkSize(SwingConstants.VERTICAL, new Component[] {butStroke, guiGraphicStroke});

    }// </editor-fold>//GEN-END:initComponents

    private void butStrokeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_butStrokeActionPerformed
        if(graphicStroke == null){
            showGraphicStrokeDialog();
        }
}//GEN-LAST:event_butStrokeActionPerformed

    private void butFillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_butFillActionPerformed
        if(graphicFill == null){
            showGraphicFillDialog();
        }
    }//GEN-LAST:event_butFillActionPerformed

private void guiGraphicFillActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiGraphicFillActionPerformed
    if(graphicFill == null){
        showGraphicFillDialog();
    }
}//GEN-LAST:event_guiGraphicFillActionPerformed

private void guiGraphicStrokeActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiGraphicStrokeActionPerformed
    if(graphicStroke == null){
        showGraphicStrokeDialog();
    }
}//GEN-LAST:event_guiGraphicStrokeActionPerformed
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
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel lbl_b_alpha;
    private JLabel lbl_b_alpha1;
    private JLabel lbl_b_width;
    private ButtonGroup typeGroup;
    // End of variables declaration//GEN-END:variables
}
