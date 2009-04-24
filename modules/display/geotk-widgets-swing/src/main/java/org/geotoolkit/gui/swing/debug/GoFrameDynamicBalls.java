/*
 * GoFrame.java
 *
 * Created on 14 mai 2008, 15:29
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.contexttree.JContextTreePopup;
import org.geotoolkit.gui.swing.contexttree.column.VisibleTreeTableColumn;
import org.geotoolkit.gui.swing.contexttree.popup.ContextActiveItem;
import org.geotoolkit.gui.swing.contexttree.popup.ContextPropertyItem;
import org.geotoolkit.gui.swing.contexttree.popup.CopyItem;
import org.geotoolkit.gui.swing.contexttree.popup.CutItem;
import org.geotoolkit.gui.swing.contexttree.popup.DeleteItem;
import org.geotoolkit.gui.swing.contexttree.popup.DuplicateItem;
import org.geotoolkit.gui.swing.contexttree.popup.LayerFeatureItem;
import org.geotoolkit.gui.swing.contexttree.popup.LayerPropertyItem;
import org.geotoolkit.gui.swing.contexttree.popup.PasteItem;
import org.geotoolkit.gui.swing.contexttree.popup.SeparatorItem;
import org.geotoolkit.gui.swing.go.decoration.JNeoNavigationDecoration;
import org.geotoolkit.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.filterproperty.JCQLPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.gui.swing.debug.ContextBuilder;
import org.geotoolkit.gui.swing.go.J2DMapVolatile;
import org.geotoolkit.map.MapContext;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author sorel
 */
public class GoFrameDynamicBalls extends javax.swing.JFrame {

    private J2DMapVolatile guiMap;
    private MapContext context = ContextBuilder.buildBigRoadContext();
    
    /** Creates new form GoFrame */
    public GoFrameDynamicBalls() {
        initComponents();        
        initTree(guiContextTree);        
            
        guiContextTree.addContext(context);
                        
        guiMap = new J2DMapVolatile();
//        try {
            guiMap.getContainer().setContext(context);

//        try {
//            guiMap.getCanvas().getController().setObjectiveCRS(context.getCoordinateReferenceSystem());
//        } catch (TransformException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(J2DMap.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        } catch (IOException ex) {
//            Logger.getLogger(GoFrame.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TransformException ex) {
//            Logger.getLogger(GoFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
                        
//        try {            
//            guiMap.getCanvas().getController().setObjectiveCRS(context.getCoordinateReferenceSystem());            
//        } catch (TransformException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(J2DMap.class.getName()).log(Level.SEVERE, null, ex);
//        }
         
        
        guiMap.addDecoration(new JNeoNavigationDecoration());
        
        panGeneral.add(BorderLayout.CENTER, guiMap);
        
        guiNavBar.setMap(guiMap);
        guiCoordBar.setMap(guiMap);
        
        
        setSize(1024,768);
        setLocationRelativeTo(null);             
    }

    private void initTree(JContextTree tree) {
        JContextTreePopup popup = tree.getPopupMenu();

        popup.addItem(new SeparatorItem());
        popup.addItem(new LayerFeatureItem());              //layer
        popup.addItem(new ContextActiveItem(tree));         //context
        popup.addItem(new SeparatorItem());
        popup.addItem(new CutItem(tree));                   //all
        popup.addItem(new CopyItem(tree));                  //all
        popup.addItem(new PasteItem(tree));                 //all
        popup.addItem(new DuplicateItem(tree));             //all        
        popup.addItem(new SeparatorItem());
        popup.addItem(new DeleteItem(tree));                //all
        popup.addItem(new SeparatorItem());

        LayerPropertyItem property = new LayerPropertyItem();
        List<PropertyPane> lstproperty = new ArrayList<PropertyPane>();
        lstproperty.add(new LayerGeneralPanel());
        lstproperty.add(new LayerCRSPropertyPanel());

        LayerFilterPropertyPanel filters = new LayerFilterPropertyPanel();
        filters.addPropertyPanel(new JCQLPropertyPanel());
        lstproperty.add(filters);

        LayerStylePropertyPanel styles = new LayerStylePropertyPanel();
        styles.addPropertyPanel(new JSimpleStylePanel());
        lstproperty.add(styles);

        property.setPropertyPanels(lstproperty);
        
        popup.addItem(property);             //layer
        popup.addItem(new ContextPropertyItem());           //context

        tree.addColumn(new VisibleTreeTableColumn());

        tree.revalidate();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        AxisProportions = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        guiContextTree = new org.geotoolkit.gui.swing.contexttree.JContextTree();
        panGeneral = new javax.swing.JPanel();
        guiCoordBar = new org.geotoolkit.gui.swing.go.control.JCoordinateBar();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        guiNavBar = new org.geotoolkit.gui.swing.go.control.JNavigationBar();
        jToolBar2 = new javax.swing.JToolBar();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Go-2 Java2D Renderer");

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setLeftComponent(guiContextTree);

        panGeneral.setLayout(new java.awt.BorderLayout());
        panGeneral.add(guiCoordBar, java.awt.BorderLayout.PAGE_END);

        jSplitPane1.setRightComponent(panGeneral);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jToolBar1, gridBagConstraints);

        guiNavBar.setFloatable(false);
        guiNavBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(guiNavBar, gridBagConstraints);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        AxisProportions.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jRadioButton2.setText("X == Y");
        jRadioButton2.setFocusable(false);
        jRadioButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preserveAxis(evt);
            }
        });
        jToolBar2.add(jRadioButton2);

        AxisProportions.add(jRadioButton3);
        jRadioButton3.setText("X != Y");
        jRadioButton3.setFocusable(false);
        jRadioButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notPreserveAxis(evt);
            }
        });
        jToolBar2.add(jRadioButton3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jToolBar2, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jMenu1.setText("File");

        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void preserveAxis(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preserveAxis
        
            guiMap.getCanvas().getController().setAxisProportions(1);//GEN-LAST:event_preserveAxis
       
}                             

private void notPreserveAxis(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notPreserveAxis
    guiMap.getCanvas().getController().setAxisProportions(Double.NaN);        
}//GEN-LAST:event_notPreserveAxis

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GoFrameDynamicBalls().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup AxisProportions;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.geotoolkit.gui.swing.contexttree.JContextTree guiContextTree;
    private org.geotoolkit.gui.swing.go.control.JCoordinateBar guiCoordBar;
    private org.geotoolkit.gui.swing.go.control.JNavigationBar guiNavBar;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel panGeneral;
    // End of variables declaration//GEN-END:variables

}
