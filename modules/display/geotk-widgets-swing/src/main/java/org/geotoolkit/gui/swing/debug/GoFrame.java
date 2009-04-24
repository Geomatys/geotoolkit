/*
 * GoFrame.java
 *
 * Created on 14 mai 2008, 15:29
 */

package org.geotoolkit.gui.swing.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.geotoolkit.display2d.canvas.GO2Hints;
import org.geotoolkit.gui.swing.go.control.JConfigBar;
import org.geotoolkit.gui.swing.go.control.JCoordinateBar;
import org.geotoolkit.gui.swing.go.control.JInformationBar;
import org.geotoolkit.gui.swing.go.control.JNavigationBar;
import org.geotoolkit.gui.swing.go.control.JSelectionBar;
import org.geotoolkit.gui.swing.propertyedit.LayerCRSPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerFilterPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerGeneralPanel;
import org.geotoolkit.gui.swing.propertyedit.LayerStylePropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.PropertyPane;
import org.geotoolkit.gui.swing.propertyedit.filterproperty.JCQLPropertyPanel;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSimpleStylePanel;
import org.geotoolkit.gui.swing.go.J2DMapVolatile;
import org.geotoolkit.gui.swing.go.decoration.JClassicNavigationDecoration;
import org.geotoolkit.gui.swing.go.decoration.JNorthArrowDecoration;
import org.geotoolkit.gui.swing.go.decoration.JScaleBarDecoration;
import org.geotoolkit.gui.swing.maptree.JContextTree;
import org.geotoolkit.gui.swing.maptree.menu.ContextPropertyItem;
import org.geotoolkit.gui.swing.maptree.menu.DeleteItem;
import org.geotoolkit.gui.swing.maptree.menu.LayerFeatureItem;
import org.geotoolkit.gui.swing.maptree.menu.LayerPropertyItem;
import org.geotoolkit.gui.swing.maptree.menu.SeparatorItem;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JAdvancedStylePanel;
import org.geotoolkit.map.MapContext;

/**
 *
 * @author sorel
 */
public class GoFrame extends javax.swing.JFrame {

    private J2DMapVolatile guiMap;
//    private MapContext context = org.constellation.map.PostGRIDContextBuilder.buildPostGridContext();

    private MapContext context = ContextBuilder.buildWMSContext();
    private JContextTree guiContextTree;
    
    /** Creates new form GoFrame */
    public GoFrame() {
        initComponents();        

//        context.layers().get(context.layers().size()-1).graphicBuilders().add(new ChartGraphicBuilder());

        guiContextTree = (JContextTree) jScrollPane1;
        guiContextTree.setContext(context);
        initTree(guiContextTree);
                        
        guiMap = new J2DMapVolatile(false);
        guiMap.getContainer().setContext(context);
        guiMap.getCanvas().setRenderingHint(GO2Hints.KEY_MULTI_THREAD, GO2Hints.MULTI_THREAD_OFF);
        guiMap.getCanvas().getController().setAutoRepaint(true);

        try{
            guiMap.getCanvas().setObjectiveCRS(context.getCoordinateReferenceSystem());
//            guiMap.getCanvas().getController().setVisibleArea(context.getBounds());
        }catch(Exception ex ){
            ex.printStackTrace();
        }

        guiMap.addDecoration(new JClassicNavigationDecoration());
        guiMap.addDecoration(new JScaleBarDecoration());
        //guiMap.addDecoration(new JNorthArrowDecoration());
        
        panGeneral.add(BorderLayout.CENTER, guiMap);
        
        guiNavBar.setMap(guiMap);
        guiInfoBar.setMap(guiMap);
        guiCoordBar.setMap(guiMap);
        guiConfigBar.setMap(guiMap);
        guiSelectionBar.setMap(guiMap);

        guiMap.getCanvas().getController().setAutoRepaint(true);

        setSize(1024,768);
        setLocationRelativeTo(null);             
    }

    private void initTree(final JContextTree tree) {

        tree.controls().add(new LayerFeatureItem());
        tree.controls().add(new SeparatorItem());
        tree.controls().add(new DeleteItem(tree));
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
        styles.addPropertyPanel(new JAdvancedStylePanel());
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
        guiInfoBar = new JInformationBar();
        guiSelectionBar = new JSelectionBar();
        guiConfigBar = new JConfigBar();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenuItem1 = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Go-2 Java2D Renderer");

        jSplitPane1.setDividerLocation(200);

        panGeneral.setLayout(new BorderLayout());
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
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiNavBar, gridBagConstraints);

        guiInfoBar.setFloatable(false);
        guiInfoBar.setRollover(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiInfoBar, gridBagConstraints);

        guiSelectionBar.setFloatable(false);
        guiSelectionBar.setRollover(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(guiSelectionBar, gridBagConstraints);

        guiConfigBar.setFloatable(false);
        guiConfigBar.setRollover(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        jPanel1.add(guiConfigBar, gridBagConstraints);

        getContentPane().add(jPanel1, BorderLayout.NORTH);

        jMenu1.setText("File");

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

private void jMenuItem1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    System.exit(0);
}//GEN-LAST:event_jMenuItem1ActionPerformed
                 

private void jButton3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

    try{
        BufferedImage image = (BufferedImage) guiMap.getCanvas().getSnapShot();
        Object output0 = new File("temp0.png");
        Object output1 = new File("temp1.png");

        System.out.println("laaa");

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


    private boolean isValidType(final Class<?>[] validTypes, final Object type) {
        for (final Class<?> t : validTypes) {
            if (t.isInstance(type)) {
                return true;
            }
        }
        return false;
    }

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
            @Override
            public void run() {
                new GoFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup AxisProportions;
    private ButtonGroup buttonGroup1;
    private JConfigBar guiConfigBar;
    private JCoordinateBar guiCoordBar;
    private JInformationBar guiInfoBar;
    private JNavigationBar guiNavBar;
    private JSelectionBar guiSelectionBar;
    private JButton jButton3;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JSelectionBar jSelectionBar1;
    private JSplitPane jSplitPane1;
    private JToolBar jToolBar1;
    private JPanel panGeneral;
    private JPanel panTree;
    // End of variables declaration//GEN-END:variables

}
