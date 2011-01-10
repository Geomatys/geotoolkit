/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Johann Sorel
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
package org.geotoolkit.gui.swing.crschooser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.io.wkt.UnformattableObjectException;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.resources.Vocabulary;

import org.opengis.util.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CRSChooser component
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JCRSChooser extends javax.swing.JDialog {

    
    public static enum ACTION {
        APPROVE,
        CANCEL,
        CLOSE
    }
    private JCRSList liste;
    private ACTION exitmode = ACTION.CLOSE;
    private CoordinateReferenceSystem crs = null;

    /** Creates new form JCRSChooser
     * @param parent
     * @param modal 
     */
    public JCRSChooser(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();

        final JLabel lbl = new JLabel(MessageBundle.getString("loading"));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        pan_list.add(BorderLayout.CENTER,lbl);

        new Thread(){
            @Override
            public void run() {
                liste = new JCRSList();

                liste.addListSelectionListener(new ListSelectionListener() {

                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        IdentifiedObject item;
                        try {
                            item = liste.getSelectedItem();
                        } catch (FactoryException ex) {
                            String message = ex.getLocalizedMessage();
                            if (message == null) {
                                message = Classes.getShortClassName(ex);
                            }
                            setErrorMessage(message);
                            return;
                        }
                        setIdentifiedObject(item);
                    }
                });

                pan_list.removeAll();
                pan_list.add(BorderLayout.CENTER, liste);
                pan_list.revalidate();
                pan_list.repaint();
                if(crs != null){
                    liste.setCRS(crs);
                }
            }
        }.start();
        
    }

    public void setCRS(final CoordinateReferenceSystem crs) {
        this.crs = crs;
        if (crs != null) {
            String epsg = crs.getName().toString();
            gui_jtf_crs.setText(epsg);
            setIdentifiedObject(crs);
        }
    }

    public CoordinateReferenceSystem getCRS() {
        if(liste != null){
             return liste.getCRS();
        }else{
            return crs;
        }
    }

    private void setIdentifiedObject(final IdentifiedObject item) {
        String text = "";
        try {
            if (item != null) {
                text = item.toWKT();
            }
        } catch (UnsupportedOperationException e) {
            text = e.getLocalizedMessage();
            if (text == null) {
                text = Classes.getShortClassName(e);
            }
            final String lineSeparator = System.getProperty("line.separator", "\n");
            if (e instanceof UnformattableObjectException) {
                text = Vocabulary.format(Vocabulary.Keys.WARNING) + ": " + text +
                        lineSeparator + lineSeparator + item + lineSeparator;
            } else {
                text = Vocabulary.format(Vocabulary.Keys.ERROR) + ": " + text + lineSeparator;
            }
        }
        wktArea.setText(text);
    }

    /**
     * Sets an error message to display instead of the current identified object.
     *
     * @param message The error message.
     */
    private void setErrorMessage(final String message) {
        wktArea.setText(Vocabulary.format(Vocabulary.Keys.ERROR_$1, message));
    }

    public ACTION showDialog() {
        exitmode = ACTION.CLOSE;
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        return exitmode;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {



        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        gui_jtf_crs = new JTextField();
        pan_list = new JPanel();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        wktArea = new JTextArea();
        but_valider = new JButton();
        but_fermer = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(MessageBundle.getString("crschooser_title")); // NOI18N
        jLabel1.setText(MessageBundle.getString("crschooser_crs")); // NOI18N
        gui_jtf_crs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                gui_jtf_crsActionPerformed(evt);
            }
        });
        gui_jtf_crs.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                gui_jtf_crsKeyTyped(evt);
            }
        });

        pan_list.setLayout(new BorderLayout());

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(pan_list, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addComponent(gui_jtf_crs, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(21, 21, 21)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(gui_jtf_crs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(pan_list, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(MessageBundle.getString("crschooser_list"), jPanel1); // NOI18N

        wktArea.setColumns(20);
        wktArea.setEditable(false);
        wktArea.setRows(5);
        jScrollPane1.setViewportView(wktArea);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);

        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(MessageBundle.getString("crschooser_wkt"), jPanel2); // NOI18N
        but_valider.setText(MessageBundle.getString("crschooser_apply")); // NOI18N
        but_valider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                but_valideractionAjouter(evt);
            }
        });

        but_fermer.setText(MessageBundle.getString("crschooser_cancel")); // NOI18N
        but_fermer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                but_fermeractionFermer(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(349, Short.MAX_VALUE)
                .addComponent(but_valider)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(but_fermer)
                .addContainerGap())
            .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(but_fermer)
                    .addComponent(but_valider))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void gui_jtf_crsActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_gui_jtf_crsActionPerformed
        liste.searchCRS(gui_jtf_crs.getText());
    }//GEN-LAST:event_gui_jtf_crsActionPerformed

    private void gui_jtf_crsKeyTyped(final KeyEvent evt) {//GEN-FIRST:event_gui_jtf_crsKeyTyped
        liste.searchCRS(gui_jtf_crs.getText());
    }//GEN-LAST:event_gui_jtf_crsKeyTyped

    private void but_valideractionAjouter(final ActionEvent evt) {//GEN-FIRST:event_but_valideractionAjouter
        exitmode = ACTION.APPROVE;
        dispose();
    }//GEN-LAST:event_but_valideractionAjouter

    private void but_fermeractionFermer(final ActionEvent evt) {//GEN-FIRST:event_but_fermeractionFermer
        exitmode = ACTION.CANCEL;
        dispose();
    }//GEN-LAST:event_but_fermeractionFermer
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton but_fermer;
    private JButton but_valider;
    private JTextField gui_jtf_crs;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JTabbedPane jTabbedPane1;
    private JPanel pan_list;
    private JTextArea wktArea;
    // End of variables declaration//GEN-END:variables
}
