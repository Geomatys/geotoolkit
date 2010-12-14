

package org.geotoolkit.pending.demo.symbology;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URISyntaxException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.geotoolkit.gui.swing.tree.MutableTreeNode;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.storage.DataStoreException;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTree;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DemoFrame extends JFrame{

    private final JXTree guiTree = new JXTree();
    private final JPanel mainPane = new JPanel(new BorderLayout());

    public DemoFrame(){
        setTitle("SLD/SE 1.1 demo.");
        try {
            guiTree.setModel(createModel());
        } catch (URISyntaxException ex) {
            ex.printStackTrace(); //don't do this in real apps, just for this demo
            return;
        } catch (DataStoreException ex) {
            ex.printStackTrace(); //don't do this in real apps, just for this demo
            return;
        }
        guiTree.setRootVisible(false);
        guiTree.expandAll();
        guiTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        guiTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                mainPane.removeAll();
                final TreePath path = tse.getNewLeadSelectionPath();
                if(path != null){
                    final Object node = path.getLastPathComponent();
                    if(node instanceof MutableTreeNode){
                        final Object candidate = ((MutableTreeNode) node).getUserObject();
                        if(candidate instanceof Component){
                            mainPane.add(BorderLayout.CENTER,(Component)candidate);
                        }
                    }
                }
                mainPane.revalidate();
                mainPane.repaint();
            }
        });


        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerSize(3);
        split.setLeftComponent(new JScrollPane(guiTree));
        split.setRightComponent(mainPane);
        split.setDividerLocation(250);

        setContentPane(split);
    }

    private static TreeModel createModel() throws URISyntaxException, CoverageStoreException, DataStoreException{
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        // Point demo -------------------------------------------------------
        final DefaultMutableTreeNode pointsNode = new DefaultMutableTreeNode("Points");
        pointsNode.add(new PanelNode("Default point", Styles.createWorldContext(Styles.defaultPoint())));
        pointsNode.add(new PanelNode("Mark point", Styles.createWorldContext(Styles.markPoint())));
        pointsNode.add(new PanelNode("Image point", Styles.createWorldContext(Styles.imagePoint())));
        root.add(pointsNode);

        // Line demo -------------------------------------------------------
        final DefaultMutableTreeNode linesNode = new DefaultMutableTreeNode("Lines");
        linesNode.add(new PanelNode("Default line", Styles.createWorldContext(Styles.defaultLine())));
        linesNode.add(new PanelNode("Color line", Styles.createWorldContext(Styles.colorLine())));
        linesNode.add(new PanelNode("Dash line", Styles.createWorldContext(Styles.dashedLine())));
        linesNode.add(new PanelNode("UOM line", Styles.createWorldContext(Styles.uomLine())));
        linesNode.add(new PanelNode("Graphic Fill line", Styles.createWorldContext(Styles.graphicFillLine())));
        linesNode.add(new PanelNode("Graphic Stroke line", Styles.createWorldContext(Styles.graphicStrokeLine())));
        root.add(linesNode);

        // Polygon demo -------------------------------------------------------
        final DefaultMutableTreeNode polygonsNode = new DefaultMutableTreeNode("Polygons");
        polygonsNode.add(new PanelNode("Default polygon", Styles.createWorldContext(Styles.defaultPolygon())));
        polygonsNode.add(new PanelNode("Color polygon", Styles.createWorldContext(Styles.colorPolygon())));
        polygonsNode.add(new PanelNode("Graphic fill polygon", Styles.createWorldContext(Styles.graphicFillPolygon())));
        polygonsNode.add(new PanelNode("Shadow polygon", Styles.createPolygonContext(Styles.shadowPolygon())));
        polygonsNode.add(new PanelNode("Offset polygon", Styles.createPolygonContext(Styles.offsetPolygon())));
        root.add(polygonsNode);

        // Text demo -------------------------------------------------------
        final DefaultMutableTreeNode textsNode = new DefaultMutableTreeNode("Texts");
        textsNode.add(new PanelNode("Default text", Styles.createWorldContext(Styles.defaultText())));
        textsNode.add(new PanelNode("Centered text", Styles.createWorldContext(Styles.centeredText())));
        root.add(textsNode);

        // Raster demo -------------------------------------------------------
        final DefaultMutableTreeNode rastersNode = new DefaultMutableTreeNode("Rasters");
        rastersNode.add(new PanelNode("Default raster", Styles.createRasterContext(Styles.defaultRaster())));
        root.add(rastersNode);

        // Rule demo -------------------------------------------------------
        final DefaultMutableTreeNode rulesNode = new DefaultMutableTreeNode("Rules");
        rulesNode.add(new PanelNode("Scale rule", Styles.createWorldContext(Styles.scaleRule())));
        rulesNode.add(new PanelNode("Filter rule", Styles.createWorldContext(Styles.filterRule())));
        root.add(rulesNode);

        // other demo -------------------------------------------------------
        final DefaultMutableTreeNode othersNode = new DefaultMutableTreeNode("Others");
        othersNode.add(new PanelNode("Isolines", JIsoline.class));
        othersNode.add(new PanelNode("Vector field", Styles.createRasterContext(Styles.vectorFieldtRaster())));
        root.add(othersNode);

        return new DefaultTreeModel(root);
    }


    public static void main(String[] args) {
        WorldFileImageReader.Spi.registerDefaults(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            //dont log anything, just make the frame nicer if possible.
        }

        final JFrame frm = new DemoFrame();
        frm.setSize(1024, 768);
        frm.setLocationRelativeTo(null);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }

    private static class PanelNode extends DefaultMutableTreeNode{

        private final String name;
        private final Class candidate;
        private final MapContext context;

        private PanelNode(String name, MapContext context){
            this.name = name;
            this.candidate = null;
            this.context = context;
        }

        private PanelNode(String name, Class candidate){
            this.name = name;
            this.candidate = candidate;
            this.context = null;
        }

        @Override
        public Object getUserObject() {
            if(context != null){
                return new JSEStylePane(context);
            }

            try {
                return candidate.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
                JXErrorPane.showDialog(ex);
                return null;
            }
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PanelNode other = (PanelNode) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (this.candidate != other.candidate && (this.candidate == null || !this.candidate.equals(other.candidate))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 61 * hash + (this.candidate != null ? this.candidate.hashCode() : 0);
            return hash;
        }

    }


}
