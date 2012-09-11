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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.filter.text.cql2.CQL;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;

import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;

/**
 * Expression dialog
 *
 * @author Johann Sorel
 * @module pending
 */
public class JExpressionDialog extends javax.swing.JDialog {

    private Expression old = null;

    /**
     * Creates new form JExpressionDialog
     */
    public JExpressionDialog() {
        this(null, null);
    }

    /**
     *
     * @param layer the layer to edit
     */
    public JExpressionDialog(final MapLayer layer) {
        this(layer, null);
    }

    /**
     *
     * @param exp the default expression
     */
    public JExpressionDialog(final Expression exp) {
        this(null, exp);
    }

    /**
     *
     * @param layer the layer to edit
     * @param exp the default expression
     */
    public JExpressionDialog(final MapLayer layer, final Expression exp) {
        initComponents();

        setLayer(layer);
        setExpression(exp);

        guiFields.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (guiFields.getSelectedValue() != null) {
                    append(guiFields.getSelectedValue().toString());
                }
            }
        });
    }

    private void append(final String val) {
        if (!jta.getText().endsWith(val)) {

            if (!jta.getText().endsWith(" ") && jta.getText().length() > 0) {
                jta.append(" ");
            }
            jta.append(val);
        }
    }

    /**
     *
     * @param layer the layer to edit
     */
    public void setLayer(final MapLayer layer) {
        guiFields.removeAll();

        if (layer instanceof FeatureMapLayer) {

            guiFields.removeAll();

            final Collection<PropertyDescriptor> col = ((FeatureMapLayer)layer).getCollection().getFeatureType().getDescriptors();
            final List<String> vec = new ArrayList<String>();

            for(final PropertyDescriptor desc : col){
                vec.add(desc.getName().getLocalPart());
            }

            guiFields.setListData(vec.toArray());
        }

    }

    /**
     *
     * @param exp the default expression
     */
    public void setExpression(final Expression exp) {
        this.old = exp;
        jta.setText("");
        guiXpathProperty.setText("");

        if (exp != null) {
            if (exp != Expression.NIL) {
                jta.setText(exp.toString());
                if(exp instanceof PropertyName){
                    guiXpathProperty.setText(exp.toString());
                }
            }
        }

    }

    /**
     *
     * @return Expression : New Expression
     */
    public Expression getExpression() {
//        final FilterFactory ff = FactoryFinder.getFilterFactory(null);
//        return ff.property(jta.getText());

        final Component activeTab = guiTabs.getSelectedComponent();

        if(activeTab == guiCQLPane){
            try {
                Expression expr = CQL.toExpression(jta.getText());
                return expr;
            } catch (CQLException ex) {
                ex.printStackTrace();
                return old;
            }
        }else if(activeTab == guiXPathPane){
            return GO2Utilities.FILTER_FACTORY.property(guiXpathProperty.getText());
        }else{
            return old;
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jToolBar1.setFloatable(false);
        jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT));
        jToolBar1.setRollover(true);

        jButton6.setText(MessageBundle.getString("apply")); // NOI18N
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actionClose(evt);
            }
        });
        jToolBar1.add(jButton6);

        getContentPane().add(jToolBar1, BorderLayout.SOUTH);

        guiCQLPane.setDividerLocation(200);

        jScrollPane2.setViewportView(guiFields);

        guiCQLPane.setLeftComponent(jScrollPane2);

        jta.setColumns(20);
        jta.setRows(5);
        jScrollPane1.setViewportView(jta);

        jButton5.setFont(jButton5.getFont().deriveFont(jButton5.getFont().getStyle() | Font.BOLD));
        jButton5.setText("*");
        jButton5.setMargin(new Insets(2, 4, 2, 4));
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton5actionMultiply(evt);
            }
        });

        jButton4.setFont(jButton4.getFont().deriveFont(jButton4.getFont().getStyle() | Font.BOLD));
        jButton4.setText("/");
        jButton4.setMargin(new Insets(2, 4, 2, 4));
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton4actionDivide(evt);
            }
        });

        jButton3.setFont(jButton3.getFont().deriveFont(jButton3.getFont().getStyle() | Font.BOLD));
        jButton3.setText("-");
        jButton3.setMargin(new Insets(2, 4, 2, 4));
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3actionMinus(evt);
            }
        });

        jButton2.setFont(jButton2.getFont().deriveFont(jButton2.getFont().getStyle() | Font.BOLD));
        jButton2.setText("+");
        jButton2.setMargin(new Insets(2, 4, 2, 4));
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2actionPlus(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jButton2)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap())
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
        );

        jPanel2Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jButton2, jButton3, jButton4, jButton5});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(SwingConstants.VERTICAL, new Component[] {jButton2, jButton3, jButton4, jButton5});

        guiCQLPane.setRightComponent(jPanel2);

        guiTabs.addTab("CQL", guiCQLPane);

        guiXpathProperty.setColumns(20);
        guiXpathProperty.setRows(5);
        jScrollPane3.setViewportView(guiXpathProperty);

        GroupLayout guiXPathPaneLayout = new GroupLayout(guiXPathPane);
        guiXPathPane.setLayout(guiXPathPaneLayout);
        guiXPathPaneLayout.setHorizontalGroup(
            guiXPathPaneLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
        );
        guiXPathPaneLayout.setVerticalGroup(
            guiXPathPaneLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );

        guiTabs.addTab("Xpath property", guiXPathPane);

        GroupLayout guiCategorizePaneLayout = new GroupLayout(guiCategorizePane);
        guiCategorizePane.setLayout(guiCategorizePaneLayout);
        guiCategorizePaneLayout.setHorizontalGroup(
            guiCategorizePaneLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 535, Short.MAX_VALUE)
        );
        guiCategorizePaneLayout.setVerticalGroup(
            guiCategorizePaneLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        guiTabs.addTab("Categorize", guiCategorizePane);

        GroupLayout guiInterpolatePaneLayout = new GroupLayout(guiInterpolatePane);
        guiInterpolatePane.setLayout(guiInterpolatePaneLayout);
        guiInterpolatePaneLayout.setHorizontalGroup(
            guiInterpolatePaneLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 535, Short.MAX_VALUE)
        );
        guiInterpolatePaneLayout.setVerticalGroup(
            guiInterpolatePaneLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        guiTabs.addTab("Interpolate", guiInterpolatePane);

        getContentPane().add(guiTabs, BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButton2actionPlus(final ActionEvent evt) {//GEN-FIRST:event_jButton2actionPlus
        append("+");
    }//GEN-LAST:event_jButton2actionPlus

    private void jButton3actionMinus(final ActionEvent evt) {//GEN-FIRST:event_jButton3actionMinus
        append("-");
    }//GEN-LAST:event_jButton3actionMinus

    private void jButton4actionDivide(final ActionEvent evt) {//GEN-FIRST:event_jButton4actionDivide
        append("/");
    }//GEN-LAST:event_jButton4actionDivide

    private void jButton5actionMultiply(final ActionEvent evt) {//GEN-FIRST:event_jButton5actionMultiply
        append("*");
    }//GEN-LAST:event_jButton5actionMultiply

    private void actionClose(final ActionEvent evt) {//GEN-FIRST:event_actionClose
        dispose();
    }//GEN-LAST:event_actionClose
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final JSplitPane guiCQLPane = new JSplitPane();
    private final JPanel guiCategorizePane = new JPanel();
    private final JList guiFields = new JList();
    private final JPanel guiInterpolatePane = new JPanel();
    private final JTabbedPane guiTabs = new JTabbedPane();
    private final JPanel guiXPathPane = new JPanel();
    private final JTextArea guiXpathProperty = new JTextArea();
    private final JButton jButton2 = new JButton();
    private final JButton jButton3 = new JButton();
    private final JButton jButton4 = new JButton();
    private final JButton jButton5 = new JButton();
    private final JButton jButton6 = new JButton();
    private final JPanel jPanel2 = new JPanel();
    private final JScrollPane jScrollPane1 = new JScrollPane();
    private final JScrollPane jScrollPane2 = new JScrollPane();
    private final JScrollPane jScrollPane3 = new JScrollPane();
    private final JToolBar jToolBar1 = new JToolBar();
    private final JTextArea jta = new JTextArea();
    // End of variables declaration//GEN-END:variables
}
