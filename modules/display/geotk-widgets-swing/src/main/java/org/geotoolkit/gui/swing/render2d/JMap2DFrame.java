/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing.render2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.JToolBar.Separator;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.contexttree.TreePopupItem;
import org.geotoolkit.gui.swing.contexttree.menu.ContextPropertyItem;
import org.geotoolkit.gui.swing.contexttree.menu.DeleteItem;
import org.geotoolkit.gui.swing.contexttree.menu.LayerFeatureItem;
import org.geotoolkit.gui.swing.contexttree.menu.LayerPropertyItem;
import org.geotoolkit.gui.swing.contexttree.menu.NewGroupItem;
import org.geotoolkit.gui.swing.contexttree.menu.SeparatorItem;
import org.geotoolkit.gui.swing.contexttree.menu.SessionCommitItem;
import org.geotoolkit.gui.swing.contexttree.menu.SessionRollbackItem;
import org.geotoolkit.gui.swing.contexttree.menu.ZoomToLayerItem;
import org.geotoolkit.gui.swing.etl.JChainEditor;
import org.geotoolkit.gui.swing.chooser.JCoverageStoreChooser;
import org.geotoolkit.gui.swing.chooser.JFeatureStoreChooser;
import org.geotoolkit.gui.swing.chooser.JServerChooser;
import org.geotoolkit.gui.swing.render2d.control.JConfigBar;
import org.geotoolkit.gui.swing.render2d.control.JCoordinateBar;
import org.geotoolkit.gui.swing.render2d.control.JEditionBar;
import org.geotoolkit.gui.swing.render2d.control.JInformationBar;
import org.geotoolkit.gui.swing.render2d.control.JNavigationBar;
import org.geotoolkit.gui.swing.render2d.control.JSelectionBar;
import org.geotoolkit.gui.swing.render2d.decoration.JClassicNavigationDecoration;
import org.geotoolkit.gui.swing.propertyedit.ClearSelectionAction;
import org.geotoolkit.gui.swing.propertyedit.DeleteSelectionAction;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.filterproperty.JCQLPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationIntervalStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationSingleStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JColorMapPane;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSLDImportExportPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.style.symbolizer.JCellSymbolizerPane;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.display3d.scene.ContextContainer3D;
import org.geotoolkit.gui.swing.propertyedit.JLayerCRSPane;
import org.geotoolkit.gui.swing.render2d.control.navigation.PanHandler;
import org.geotoolkit.gui.swing.util.JOptionDialog;
import org.geotoolkit.gui.swing.render3d.JMap3D;
import org.geotoolkit.gui.swing.render3d.control.JMap3dConfigPanel;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.opengis.geometry.Envelope;

/**
 * Simple Frame that can be used to quickly display a map or for debug purpose.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JMap2DFrame extends javax.swing.JFrame {

    private static final ImageIcon M3D_CONFIG = IconBuilder.createIcon(FontAwesomeIcons.ICON_COG, 16, Color.BLACK);

    private final JMap2D guiMap2D;
    private final JMap3D guiMap3D;
    private final JContextTree guiContextTree;
    private final JChainEditor guiChainEditor;

    protected JMap2DFrame(final MapContext context, Hints hints) {
        this(context,false,hints);
    }

    protected JMap2DFrame(final MapContext context, boolean statefull, Hints hints) {
        initComponents();

        guiContextTree = (JContextTree) jScrollPane1;
        guiContextTree.setContext(context);
        initTree(guiContextTree);

        guiMap2D = new JMap2D(statefull);
        guiMap2D.getContainer().setContext(context);
        guiMap2D.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        guiMap2D.getCanvas().setRenderingHint(GO2Hints.KEY_GENERALIZE, GO2Hints.GENERALIZE_ON);
        guiMap2D.getCanvas().setRenderingHint(GO2Hints.KEY_BEHAVIOR_MODE, GO2Hints.BEHAVIOR_PROGRESSIVE);

        guiChainEditor = new JChainEditor(true);
        panETL.add(BorderLayout.CENTER, guiChainEditor);

        if(hints != null){
            guiMap2D.getCanvas().setRenderingHints(hints);
        }

        guiMap2D.getCanvas().setAutoRepaint(true);

        for(TreePopupItem item : guiContextTree.controls()){
            item.setMapView(guiMap2D);
        }

        try{
            Envelope env = context.getAreaOfInterest();
            if(env != null){
                guiMap2D.getCanvas().setObjectiveCRS(env.getCoordinateReferenceSystem());
            }else{
                env = context.getBounds();
                guiMap2D.getCanvas().setObjectiveCRS(context.getCoordinateReferenceSystem());
            }
            if(env != null){
                guiMap2D.getCanvas().setVisibleArea(env);
            }
        }catch(Exception ex ){
            ex.printStackTrace();
        }

        // 2D map
        guiMap2D.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
        panMap2D.add(BorderLayout.CENTER, guiMap2D);
        guiNavBar.setMap(guiMap2D);
        guiInfoBar.setMap(guiMap2D);
        guiCoordBar.setMap(guiMap2D);
        guiConfigBar.setMap(guiMap2D);
        guiSelectionBar.setMap(guiMap2D);
        guiEditBar.setMap(guiMap2D);
        guiMap2D.getCanvas().setAutoRepaint(true);
        guiMap2D.setHandler(new PanHandler(guiMap2D,false));

        //3D map
        guiMap3D = new JMap3D();
        ((ContextContainer3D)guiMap3D.getMap3D().getContainer()).setContext(context);
        panMap3D.add(BorderLayout.CENTER, guiMap3D);

        setSize(1024,768);
        setLocationRelativeTo(null);
    }

    private void initTree(final JContextTree tree) {

        LayerFeatureItem item = new LayerFeatureItem();
        item.actions().add(new ClearSelectionAction());
        item.actions().add(new DeleteSelectionAction());

        tree.controls().add(item);
        tree.controls().add(new NewGroupItem());
        tree.controls().add(new ZoomToLayerItem());
        tree.controls().add(new SeparatorItem());
        tree.controls().add(new SessionCommitItem());
        tree.controls().add(new SessionRollbackItem());
        tree.controls().add(new SeparatorItem());
        tree.controls().add(new DeleteItem());
        tree.controls().add(new SeparatorItem());

        LayerPropertyItem property = new LayerPropertyItem();
        List<PropertyPane> lstproperty = new ArrayList<PropertyPane>();
        lstproperty.add(new LayerGeneralPanel());
        lstproperty.add(new JLayerCRSPane());

        LayerFilterPropertyPanel filters = new LayerFilterPropertyPanel();
        filters.addPropertyPanel(MessageBundle.getString("filter"),new JCQLPropertyPanel());
        lstproperty.add(filters);

        LayerStylePropertyPanel styles = new LayerStylePropertyPanel();
        styles.addPropertyPanel(MessageBundle.getString("analyze"),new JSimpleStylePanel());
        styles.addPropertyPanel(MessageBundle.getString("analyze_vector"),new JClassificationSingleStylePanel());
        styles.addPropertyPanel(MessageBundle.getString("analyze_vector"),new JClassificationIntervalStylePanel());
        styles.addPropertyPanel(MessageBundle.getString("analyze_raster"),new JColorMapPane());
        styles.addPropertyPanel(MessageBundle.getString("analyze_raster"),new JCellSymbolizerPane());
        styles.addPropertyPanel(MessageBundle.getString("sld"),new JAdvancedStylePanel());
        styles.addPropertyPanel(MessageBundle.getString("sld"),new JSLDImportExportPanel());
        lstproperty.add(styles);

        property.setPropertyPanels(lstproperty);

        tree.controls().add(property);
        tree.controls().add(new ContextPropertyItem());

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
        GridBagConstraints gridBagConstraints;

        jSplitPane1 = new JSplitPane();
        panTabs = new JTabbedPane();
        panMap2D = new JPanel();
        jPanel1 = new JPanel();
        jToolBar1 = new JToolBar();
        jButton3 = new JButton();
        guiNavBar = new JNavigationBar();
        jSeparator1 = new Separator();
        guiInfoBar = new JInformationBar();
        jSeparator2 = new Separator();
        guiSelectionBar = new JSelectionBar();
        jSeparator3 = new Separator();
        guiEditBar = new JEditionBar();
        guiConfigBar = new JConfigBar();
        guiCoordBar = new JCoordinateBar();
        panMap3D = new JPanel();
        jPanel2 = new JPanel();
        jToolBar2 = new JToolBar();
        guiConfig3D = new JButton();
        panETL = new JPanel();
        panTree = new JPanel();
        jScrollPane1 = new JContextTree();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenuItem4 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jSeparator4 = new JPopupMenu.Separator();
        jMenuItem1 = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Go-2 Java2D Renderer");

        jSplitPane1.setDividerLocation(200);

        panMap2D.setLayout(new BorderLayout());

        jPanel1.setLayout(new GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jButton3.setText("Export");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jToolBar1, gridBagConstraints);

        guiNavBar.setFloatable(false);
        guiNavBar.setRollover(true);
        guiNavBar.add(jSeparator1);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiNavBar, gridBagConstraints);

        guiInfoBar.setFloatable(false);
        guiInfoBar.setRollover(true);
        guiInfoBar.add(jSeparator2);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiInfoBar, gridBagConstraints);

        guiSelectionBar.setFloatable(false);
        guiSelectionBar.setRollover(true);
        guiSelectionBar.add(jSeparator3);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiSelectionBar, gridBagConstraints);

        guiEditBar.setFloatable(false);
        guiEditBar.setRollover(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(guiEditBar, gridBagConstraints);

        guiConfigBar.setFloatable(false);
        guiConfigBar.setRollover(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiConfigBar, gridBagConstraints);

        panMap2D.add(jPanel1, BorderLayout.NORTH);

        guiCoordBar.setFloatable(false);
        panMap2D.add(guiCoordBar, BorderLayout.PAGE_END);

        panTabs.addTab("2D", panMap2D);

        panMap3D.setLayout(new BorderLayout());

        jPanel2.setLayout(new GridBagLayout());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        guiConfig3D.setIcon(M3D_CONFIG);
        guiConfig3D.setFocusable(false);
        guiConfig3D.setHorizontalTextPosition(SwingConstants.CENTER);
        guiConfig3D.setVerticalTextPosition(SwingConstants.BOTTOM);
        guiConfig3D.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                guiConfig3DActionPerformed(evt);
            }
        });
        jToolBar2.add(guiConfig3D);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jToolBar2, gridBagConstraints);

        panMap3D.add(jPanel2, BorderLayout.NORTH);

        panTabs.addTab("3D", panMap3D);

        panETL.setLayout(new BorderLayout());
        panTabs.addTab("ETL", panETL);

        jSplitPane1.setRightComponent(panTabs);

        panTree.setPreferredSize(new Dimension(100, 300));
        panTree.setLayout(new BorderLayout());
        panTree.add(jScrollPane1, BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(panTree);

        getContentPane().add(jSplitPane1, BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem4.setText("Add coverage store ...");
        jMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openCoverageStoreChooser(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem2.setText("Add feature store ...");
        jMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openFeatureStoreChooser(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Add from server ...");
        jMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openServerChooser(evt);
            }
        });
        jMenu1.add(jMenuItem3);
        jMenu1.add(jSeparator4);

        jMenuItem1.setText("Quit");
        jMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jMenuItem1ActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem1ActionPerformed


private void jButton3ActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    try{
        BufferedImage image = (BufferedImage) guiMap2D.getCanvas().getSnapShot();
        Object output0 = new File("temp0.png");
        Object output1 = new File("temp1.png");

        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/png");
        while (writers.hasNext()) {
            final ImageWriter writer = writers.next();
            final ImageWriterSpi spi = writer.getOriginatingProvider();
            if (spi.canEncodeImage(image)) {
                ImageOutputStream stream = null;
                if (!isValidType(spi.getOutputTypes(), output0)) {
                    stream = ImageIO.createImageOutputStream(output0);
                    output0 = stream;
                    stream = ImageIO.createImageOutputStream(output1);
                    output1 = stream;
                }

                ImageWriteParam iwp = writer.getDefaultWriteParam();
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwp.setCompressionQuality(0);
                IIOImage iimage = new IIOImage(image, null, null);
                writer.setOutput(output0);
                writer.write(null,iimage,iwp);
                writer.dispose();

                iwp = writer.getDefaultWriteParam();
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwp.setCompressionQuality(1);
                iimage = new IIOImage(image, null, null);
                writer.setOutput(output1);
                writer.write(null,iimage,iwp);
                writer.dispose();


                if (output0 != null) {
                    ((ImageOutputStream)output0).close();
                    ((ImageOutputStream)output1).close();
                }

                return;
            }
        }
    }catch(Exception rx){
        rx.printStackTrace();
    }


}//GEN-LAST:event_jButton3ActionPerformed

private void openCoverageStoreChooser(ActionEvent evt) {//GEN-FIRST:event_openCoverageStoreChooser
        try {
            final List<MapLayer> layers = JCoverageStoreChooser.showLayerDialog(this,null);

            for(MapLayer layer : layers){
                if(layer == null) continue;
                guiContextTree.getContext().layers().add(layer);
            }

        } catch (DataStoreException ex) {
            Logger.getLogger(JMap2DFrame.class.getName()).log(Level.SEVERE, null, ex);
        }


}//GEN-LAST:event_openCoverageStoreChooser

private void openFeatureStoreChooser(ActionEvent evt) {//GEN-FIRST:event_openFeatureStoreChooser

        try {
            final List<MapLayer> layers = JFeatureStoreChooser.showLayerDialog(this,null);

            for(MapLayer layer : layers){
                if(layer == null) continue;
                guiContextTree.getContext().layers().add(layer);
            }

        } catch (DataStoreException ex) {
            Logger.getLogger(JMap2DFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

}//GEN-LAST:event_openFeatureStoreChooser

private void openServerChooser(ActionEvent evt) {//GEN-FIRST:event_openServerChooser

    try {
        final List<MapLayer> layers = JServerChooser.showLayerDialog(this,null);

        for(MapLayer layer : layers){
            if(layer == null) continue;
            guiContextTree.getContext().layers().add(layer);
        }

    } catch (DataStoreException ex) {
        Logger.getLogger(JMap2DFrame.class.getName()).log(Level.SEVERE, null, ex);
    }

}//GEN-LAST:event_openServerChooser

    private void guiConfig3DActionPerformed(ActionEvent evt) {//GEN-FIRST:event_guiConfig3DActionPerformed

        final JMap3dConfigPanel config = new JMap3dConfigPanel(guiMap3D);
        JOptionDialog.show(this, config, JOptionPane.OK_OPTION);

    }//GEN-LAST:event_guiConfig3DActionPerformed

    private boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }

    public static void show(final MapContext context){
        show(context,null);
    }

    public static void show(final MapContext context, final Hints hints){
        show(context,false,hints);
    }

    public static void show(MapContext context, final boolean statefull, final Hints hints){
        if(context == null) context = MapBuilder.createContext();
        final MapContext mc = context;
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMap2DFrame(mc,statefull,hints).setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton guiConfig3D;
    private JConfigBar guiConfigBar;
    private JCoordinateBar guiCoordBar;
    private JEditionBar guiEditBar;
    private JInformationBar guiInfoBar;
    private JNavigationBar guiNavBar;
    private JSelectionBar guiSelectionBar;
    private JButton jButton3;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItem3;
    private JMenuItem jMenuItem4;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private Separator jSeparator1;
    private Separator jSeparator2;
    private Separator jSeparator3;
    private JPopupMenu.Separator jSeparator4;
    private JSplitPane jSplitPane1;
    private JToolBar jToolBar1;
    private JToolBar jToolBar2;
    private JPanel panETL;
    private JPanel panMap2D;
    private JPanel panMap3D;
    protected JTabbedPane panTabs;
    private JPanel panTree;
    // End of variables declaration//GEN-END:variables

}
