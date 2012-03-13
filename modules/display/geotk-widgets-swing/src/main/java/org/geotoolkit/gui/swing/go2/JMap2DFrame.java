/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.gui.swing.go2;

import java.awt.BorderLayout;
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
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.geotoolkit.data.DataStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.gui.swing.go2.control.JConfigBar;
import org.geotoolkit.gui.swing.go2.control.JCoordinateBar;
import org.geotoolkit.gui.swing.go2.control.JEditionBar;
import org.geotoolkit.gui.swing.go2.control.JInformationBar;
import org.geotoolkit.gui.swing.go2.control.JNavigationBar;
import org.geotoolkit.gui.swing.go2.control.JSelectionBar;
import org.geotoolkit.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.filterproperty.JCQLPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
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
import org.geotoolkit.gui.swing.filestore.JFileCoverageChooser;
import org.geotoolkit.gui.swing.filestore.JFileDatastoreChooser;
import org.geotoolkit.gui.swing.go2.decoration.JClassicNavigationDecoration;
import org.geotoolkit.gui.swing.propertyedit.ClearSelectionAction;
import org.geotoolkit.gui.swing.propertyedit.DeleteSelectionAction;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationSingleStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationIntervalStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JRasterColorMapStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSLDImportExportPanel;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;

import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.util.RandomStyleFactory;
import org.opengis.feature.type.Name;
import org.opengis.geometry.Envelope;

/**
 * Simple Frame that can be used to quickly display a map or for debug purpose.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JMap2DFrame extends javax.swing.JFrame {
    
    private final JMap2D guiMap;
    private final JContextTree guiContextTree;
    
    private JMap2DFrame(final MapContext context, Hints hints) {
        initComponents();        

        guiContextTree = (JContextTree) jScrollPane1;
        guiContextTree.setContext(context);
        initTree(guiContextTree);
                        
        guiMap = new JMap2D(false);
        guiMap.getContainer().setContext(context);
        guiMap.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_GENERALIZE, GO2Hints.GENERALIZE_ON);
        guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_BEHAVIOR_MODE, GO2Hints.BEHAVIOR_PROGRESSIVE);
        
        if(hints != null){
            guiMap.getCanvas().setRenderingHints(hints);
        }
        
        guiMap.getCanvas().getController().setAutoRepaint(true);

        for(TreePopupItem item : guiContextTree.controls()){
            item.setMapView(guiMap);
        }

        try{
            Envelope env = context.getAreaOfInterest();
            if(env != null){
                guiMap.getCanvas().setObjectiveCRS(env.getCoordinateReferenceSystem());
            }else{
                env = context.getBounds();
                guiMap.getCanvas().setObjectiveCRS(context.getCoordinateReferenceSystem());
            }
            if(env != null){
                guiMap.getCanvas().getController().setVisibleArea(env);
            }
        }catch(Exception ex ){
            ex.printStackTrace();
        }

        guiMap.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
        
        panGeneral.add(BorderLayout.CENTER, guiMap);
        
        guiNavBar.setMap(guiMap);
        guiInfoBar.setMap(guiMap);
        guiCoordBar.setMap(guiMap);
        guiConfigBar.setMap(guiMap);
        guiSelectionBar.setMap(guiMap);
        guiEditBar.setMap(guiMap);

        guiMap.getCanvas().getController().setAutoRepaint(true);

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
        lstproperty.add(new LayerCRSPropertyPanel());

        LayerFilterPropertyPanel filters = new LayerFilterPropertyPanel();
        filters.addPropertyPanel(new JCQLPropertyPanel());
        lstproperty.add(filters);

        LayerStylePropertyPanel styles = new LayerStylePropertyPanel();
        styles.addPropertyPanel(new JSimpleStylePanel());
        styles.addPropertyPanel(new JClassificationSingleStylePanel());
        styles.addPropertyPanel(new JClassificationIntervalStylePanel());
        styles.addPropertyPanel(new JRasterColorMapStylePanel());
        styles.addPropertyPanel(new JAdvancedStylePanel());
        styles.addPropertyPanel(new JSLDImportExportPanel());
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

        buttonGroup1 = new ButtonGroup();
        AxisProportions = new ButtonGroup();
        jSplitPane1 = new JSplitPane();
        panGeneral = new JPanel();
        guiCoordBar = new JCoordinateBar();
        panTree = new JPanel();
        jScrollPane1 = new JContextTree();
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
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenuItem3 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jSeparator4 = new JPopupMenu.Separator();
        jMenuItem1 = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Go-2 Java2D Renderer");

        jSplitPane1.setDividerLocation(200);

        panGeneral.setLayout(new BorderLayout());

        guiCoordBar.setFloatable(false);
        panGeneral.add(guiCoordBar, BorderLayout.PAGE_END);

        jSplitPane1.setRightComponent(panGeneral);

        panTree.setPreferredSize(new Dimension(100, 300));
        panTree.setLayout(new BorderLayout());
        panTree.add(jScrollPane1, BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(panTree);

        getContentPane().add(jSplitPane1, BorderLayout.CENTER);

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

        getContentPane().add(jPanel1, BorderLayout.NORTH);

        jMenu1.setText("File");

        jMenuItem3.setText("Add image file ...");
        jMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem2.setText("Add vector data ...");
        jMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
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
        BufferedImage image = (BufferedImage) guiMap.getCanvas().getSnapShot();
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

    private void jMenuItem2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        
        final List<DataStore> stores = JFileDatastoreChooser.showDialog();
        
        for(DataStore store : stores){
            
            try{
                final Session session = store.createSession(true);
                for(Name n : store.getNames()){
                    final FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(n));
                    final MutableStyle style = RandomStyleFactory.createDefaultVectorStyle(col);
                    final FeatureMapLayer layer = MapBuilder.createFeatureLayer(col, style);
                    layer.setName(n.getLocalPart());
                    layer.setDescription(new DefaultStyleFactory().description(n.getLocalPart(), n.getLocalPart()));
                    guiMap.getContainer().getContext().layers().add(layer);
                    
                }
            }catch(DataStoreException ex){
                ex.printStackTrace();
            }
            
        }
        
        
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        
        final Map<File,ImageReaderSpi> map = JFileCoverageChooser.showDialog();
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed


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

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMap2DFrame(context,hints).setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup AxisProportions;
    private ButtonGroup buttonGroup1;
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
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private Separator jSeparator1;
    private Separator jSeparator2;
    private Separator jSeparator3;
    private JPopupMenu.Separator jSeparator4;
    private JSplitPane jSplitPane1;
    private JToolBar jToolBar1;
    private JPanel panGeneral;
    private JPanel panTree;
    // End of variables declaration//GEN-END:variables

}
