/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JAbstractMapPane.java
 *
 * Created on 1 févr. 2010, 13:02:27
 */

package org.geotoolkit.pending.demo.symbology;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainter;
import org.geotoolkit.display2d.canvas.painter.BackgroundPainterGroup;
import org.geotoolkit.display2d.canvas.painter.SolidColorPainter;
import org.geotoolkit.display2d.ext.grid.DefaultGridTemplate;
import org.geotoolkit.display2d.ext.grid.GridPainter;
import org.geotoolkit.display2d.ext.grid.GridTemplate;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.contexttree.menu.ContextPropertyItem;
import org.geotoolkit.gui.swing.contexttree.menu.DeleteItem;
import org.geotoolkit.gui.swing.contexttree.menu.LayerFeatureItem;
import org.geotoolkit.gui.swing.contexttree.menu.LayerPropertyItem;
import org.geotoolkit.gui.swing.contexttree.menu.SeparatorItem;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.decoration.JClassicNavigationDecoration;
import org.geotoolkit.gui.swing.propertyedit.ClearSelectionAction;
import org.geotoolkit.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.filterproperty.JCQLPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationIntervalStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JClassificationSingleStylePanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.opengis.filter.FilterFactory;

/**
 *
 * @author sorel
 */
public abstract class JAbstractMapPane extends javax.swing.JPanel {

    public static final MutableStyleFactory SF = new DefaultStyleFactory();
    public static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    protected final JMap2D guiMap;
    protected final MapContext context;
    protected final JContextTree guiContextTree;

    /** Creates new form JAbstractMapPane */
    public JAbstractMapPane(MapContext context) {
        this.context = context;

        initComponents();

        guiContextTree = (JContextTree) jScrollPane1;
        guiContextTree.setContext(context);
        initTree(guiContextTree);

        guiMap = new JMap2D(false);
        guiMap.getContainer().setContext(context);
        //guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_MULTI_THREAD, GO2Hints.MULTI_THREAD_OFF);
        //guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_GENERALIZE, GO2Hints.GENERALIZE_OFF);
        //guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_SYMBOL_RENDERING_ORDER, GO2Hints.SYMBOL_RENDERING_PRIME);
        guiMap.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        guiMap.getCanvas().setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        guiMap.getCanvas().setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        guiMap.getCanvas().getController().setAutoRepaint(true);

        try{
            guiMap.getCanvas().setObjectiveCRS(context.getCoordinateReferenceSystem());
            guiMap.getCanvas().getController().setVisibleArea(context.getBounds());
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



        GridTemplate gridTemplate = new DefaultGridTemplate(
                        DefaultGeographicCRS.WGS84,
                        new BasicStroke(1.5f),
                        new Color(120,120,120,200),

                        new BasicStroke(1,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3, new float[]{5,5}, 0),
                        new Color(120,120,120,60),

                        new Font("serial", Font.BOLD, 12),
                        Color.GRAY,
                        0,
                        Color.WHITE,

                        new Font("serial", Font.ITALIC, 10),
                        Color.GRAY,
                        0,
                        Color.WHITE);

        BackgroundPainter bgWhite = new SolidColorPainter(Color.WHITE);
        guiMap.getCanvas().setBackgroundPainter(BackgroundPainterGroup.wrap(bgWhite ,new GridPainter(gridTemplate)));

    }

    private void initTree(final JContextTree tree) {

        LayerFeatureItem item = new LayerFeatureItem();
        item.actions().add(new ClearSelectionAction());
//        item.actions().add(new DeleteSelectionAction());

        tree.controls().add(item);
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
        styles.addPropertyPanel(new JAdvancedStylePanel());
        lstproperty.add(styles);

        property.setPropertyPanels(lstproperty);

        tree.controls().add(property);
        tree.controls().add(new ContextPropertyItem());

        tree.revalidate();
    }

    protected abstract JComponent createConfigPane();


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        guiNavBar = new org.geotoolkit.gui.swing.go2.control.JNavigationBar();
        guiInfoBar = new org.geotoolkit.gui.swing.go2.control.JInformationBar();
        guiSelectionBar = new org.geotoolkit.gui.swing.go2.control.JSelectionBar();
        guiEditBar = new org.geotoolkit.gui.swing.go2.control.JEditionBar();
        guiConfigBar = new org.geotoolkit.gui.swing.go2.control.JConfigBar();
        jSplitPane2 = new javax.swing.JSplitPane();
        panGeneral = new javax.swing.JPanel();
        guiCoordBar = new org.geotoolkit.gui.swing.go2.control.JCoordinateBar();
        panTree = new javax.swing.JPanel();
        jScrollPane1 = new JContextTree();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jToolBar1, gridBagConstraints);

        guiNavBar.setFloatable(false);
        guiNavBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(guiNavBar, gridBagConstraints);

        guiInfoBar.setFloatable(false);
        guiInfoBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(guiInfoBar, gridBagConstraints);

        guiSelectionBar.setFloatable(false);
        guiSelectionBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(guiSelectionBar, gridBagConstraints);

        guiEditBar.setFloatable(false);
        guiEditBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(guiEditBar, gridBagConstraints);

        guiConfigBar.setFloatable(false);
        guiConfigBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(guiConfigBar, gridBagConstraints);

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jSplitPane2.setDividerLocation(200);

        panGeneral.setLayout(new java.awt.BorderLayout());

        guiCoordBar.setFloatable(false);
        panGeneral.add(guiCoordBar, java.awt.BorderLayout.PAGE_END);

        jSplitPane2.setRightComponent(panGeneral);

        panTree.setPreferredSize(new java.awt.Dimension(100, 300));
        panTree.setLayout(new java.awt.BorderLayout());
        panTree.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jSplitPane2.setLeftComponent(panTree);

        jPanel1.add(jSplitPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Preview", jPanel1);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.geotoolkit.gui.swing.go2.control.JConfigBar guiConfigBar;
    private org.geotoolkit.gui.swing.go2.control.JCoordinateBar guiCoordBar;
    private org.geotoolkit.gui.swing.go2.control.JEditionBar guiEditBar;
    private org.geotoolkit.gui.swing.go2.control.JInformationBar guiInfoBar;
    private org.geotoolkit.gui.swing.go2.control.JNavigationBar guiNavBar;
    private org.geotoolkit.gui.swing.go2.control.JSelectionBar guiSelectionBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panGeneral;
    private javax.swing.JPanel panTree;
    // End of variables declaration//GEN-END:variables


}
