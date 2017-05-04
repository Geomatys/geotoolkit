/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.process.ProcessingRegistry;
import org.geotoolkit.processing.chain.ChainProcessDescriptor;
import org.geotoolkit.processing.chain.model.Chain;
import org.geotoolkit.processing.chain.model.event.EventChain;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Split;

/**
 * Chain visual editor.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JChainEditor extends JPanel{

    // views flags.
    private static final int BASIC_VIEW = 0;
    private static final int INTERMEDIATE_VIEW = 1;
    private static final int ADVANCED_VIEW = 2;

    private EventChain chain = null;

    //swing elements
    private final JXMultiSplitPane guiSplitPane = new JXMultiSplitPane();
    private final JPanel guiCenterPanel = new JPanel(new BorderLayout());
    private final JSplitPane guiRightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JTree guiProcessTree = new JTree();
    private final JTree guiOtherTree = new JTree();
    private final JTree guiDataTree = new JTree();
    private final JToolBar guiToolbar = new JToolBar(JToolBar.HORIZONTAL);
    private final boolean editable;
    private int usedView = BASIC_VIEW;
    private ChainScene scene;

    public JChainEditor(final boolean editable) {
        super(new BorderLayout());
        this.editable = editable;

        //configure the data tree and actions
        guiDataTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        //guiDataTree.setCellRenderer(new JDataTreeCellRenderer());
        guiDataTree.setDragEnabled(true);
        guiDataTree.setRootVisible(false);
        guiDataTree.setTransferHandler(new TransferHandler(){

            @Override
            public void exportAsDrag(JComponent comp, InputEvent e, int action) {
                super.exportAsDrag(comp, e, action);
            }

            @Override
            protected Transferable createTransferable(JComponent c) {

                final Collection candidates = new ArrayList();

                final TreePath[] paths = guiDataTree.getSelectionPaths();

                if(paths != null){
                    for(TreePath tp : paths){
                        Object candidate = tp.getLastPathComponent();
                        if(candidate instanceof DefaultMutableTreeNode){
                            candidate = ((DefaultMutableTreeNode)candidate).getUserObject();
                        }
                        candidates.add(candidate);
                    }
                }

                final Transferable tr = new Transferable() {

                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[]{new DataFlavor(Object.class, "java/object")};
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return true;
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        return candidates;
                    }
                };

                return tr;
            }

            /**
             * The list handles both copy and move actions.
             */
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

        });

        //configure tree renderer and actions
        guiProcessTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        guiProcessTree.setCellRenderer(new JProcessTreeCellRenderer());
        guiProcessTree.setDragEnabled(true);
        guiProcessTree.setRootVisible(false);
        ToolTipManager.sharedInstance().registerComponent(guiProcessTree);
        guiProcessTree.setTransferHandler(new TransferHandler(){

            @Override
            public void exportAsDrag(JComponent comp, InputEvent e, int action) {
                super.exportAsDrag(comp, e, action);
            }

            @Override
            protected Transferable createTransferable(JComponent c) {

                final Collection candidates = new ArrayList();

                final TreePath[] paths = guiProcessTree.getSelectionPaths();

                if(paths != null){
                    for(TreePath tp : paths){
                        Object candidate = tp.getLastPathComponent();
                        if(candidate instanceof DefaultMutableTreeNode){
                            candidate = ((DefaultMutableTreeNode)candidate).getUserObject();
                        }
                        candidates.add(candidate);
                    }
                }

                final Transferable tr = new Transferable() {

                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[]{new DataFlavor(Object.class, "java/object")};
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return true;
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        return candidates;
                    }
                };

                return tr;
            }

            /**
             * The list handles both copy and move actions.
             */
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

        });

        //configure tree renderer and actions
        guiOtherTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
        guiOtherTree.setCellRenderer(new JOtherTreeCellRenderer());
        guiOtherTree.setDragEnabled(true);
        guiOtherTree.setRootVisible(false);
        ToolTipManager.sharedInstance().registerComponent(guiOtherTree);
        guiOtherTree.setTransferHandler(new TransferHandler(){

            @Override
            public void exportAsDrag(JComponent comp, InputEvent e, int action) {
                super.exportAsDrag(comp, e, action);
            }

            @Override
            protected Transferable createTransferable(JComponent c) {

                final Collection candidates = new ArrayList();

                final TreePath[] paths = guiOtherTree.getSelectionPaths();

                if(paths != null){
                    for(TreePath tp : paths){
                        Object candidate = tp.getLastPathComponent();
                        if(candidate instanceof DefaultMutableTreeNode){
                            candidate = ((DefaultMutableTreeNode)candidate).getUserObject();
                        }
                        candidates.add(candidate);
                    }
                }

                final Transferable tr = new Transferable() {

                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[]{new DataFlavor(Object.class, "java/object")};
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return true;
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        return candidates;
                    }
                };

                return tr;
            }

            /**
             * The list handles both copy and move actions.
             */
            @Override
            public int getSourceActions(JComponent c) {
                return COPY_OR_MOVE;
            }

        });



        final JLabel guiLblData = new JLabel(MessageBundle.format("datas"));
        guiLblData.setHorizontalTextPosition(SwingConstants.CENTER);
        guiLblData.setHorizontalAlignment(SwingConstants.CENTER);
        final JLabel guiLblProcess = new JLabel(MessageBundle.format("processes"));
        guiLblProcess.setHorizontalTextPosition(SwingConstants.CENTER);
        guiLblProcess.setHorizontalAlignment(SwingConstants.CENTER);
        final JLabel guiLblOther = new JLabel(MessageBundle.format("other"));
        guiLblOther.setHorizontalTextPosition(SwingConstants.CENTER);
        guiLblOther.setHorizontalAlignment(SwingConstants.CENTER);
        final JPanel rtpane = new JPanel(new BorderLayout());

        final JSplitPane rlpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        final JPanel rppane = new JPanel(new BorderLayout());
        final JPanel ropane = new JPanel(new BorderLayout());
        rtpane.add(BorderLayout.NORTH,guiLblData);
        rtpane.add(BorderLayout.CENTER,new JScrollPane(guiDataTree));

        rppane.add(BorderLayout.NORTH,guiLblProcess);
        rppane.add(BorderLayout.CENTER,new JScrollPane(guiProcessTree));
        ropane.add(BorderLayout.NORTH,guiLblOther);
        ropane.add(BorderLayout.CENTER,new JScrollPane(guiOtherTree));

        rlpane.setTopComponent(rppane);
        rlpane.setBottomComponent(ropane);
        rlpane.setDividerLocation(325);

        guiRightPanel.setTopComponent(rtpane);
        guiRightPanel.setBottomComponent(rlpane);
        guiRightPanel.setDividerLocation(150);


        final Leaf left = new Leaf("left"); left.setWeight(0.15);
        final Leaf center = new Leaf("center"); center.setWeight(0.70);
        final Leaf right = new Leaf("right"); right.setWeight(0.15);
        final Split splitModel = new Split(
               center,
               new Divider(),
               right);
        guiSplitPane.setModel(splitModel);
        guiSplitPane.add(guiCenterPanel, "center");

        // hide process/data panel for non-editor user
        if(editable) {
            guiSplitPane.add(guiRightPanel, "right");
        }

        /*
         * Button to save/load/execute chain.
         */
        final Action actNew = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final EventChain chain = new EventChain();
                setChain(chain);
            }
        };
        final Action actSave = new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final EventChain chain = getChain();
                if(chain == null) return;

                final JFileChooser jfc = new JFileChooser();
                final int action = jfc.showSaveDialog(null);
                if(action == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null){
                    try {
                        final File file = jfc.getSelectedFile();
                        chain.write(file);
                    } catch (JAXBException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        final Action actLoad = new AbstractAction("Load") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setChain(new EventChain());

                final JFileChooser jfc = new JFileChooser();
                final int action = jfc.showOpenDialog(null);
                if(action == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null){
                    try {
                        final File file = jfc.getSelectedFile();
                        EventChain chain = new EventChain(Chain.read(file));
                        setChain(chain);
                    } catch (JAXBException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        final Action actExecute = new AbstractAction("Execute") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final EventChain chain = getChain();
                if(chain == null) return;
                final ChainProcessDescriptor desc = new ChainProcessDescriptor(chain, null);
        //        final Process process = desc.createProcess(input);
        //        ChainProcess process = new ChainProcess(null, null)
            }
        };

        /*
         * Button to change view mode.
         */
        final ButtonGroup guiToggleGroup = new ButtonGroup();

        final ActionListener toggleListener = new ToggleViewActionListener();
        final JToggleButton guiToggleBasicView  = new JToggleButton();
        guiToggleBasicView.setText(MessageBundle.format("guiToogleBasicView"));
        guiToggleBasicView.setName("tglBasic");
        if (usedView == BASIC_VIEW) {
            guiToggleBasicView.getModel().setSelected(true);
        }
        guiToggleBasicView.addActionListener(toggleListener);
        guiToggleGroup.add(guiToggleBasicView);

        final JToggleButton guiToggleNoConstantView  = new JToggleButton();
        guiToggleNoConstantView.setText(MessageBundle.format("guiToogleInterView"));
        guiToggleNoConstantView.setName("tglCst");
        if (usedView == INTERMEDIATE_VIEW) {
            guiToggleNoConstantView.getModel().setSelected(true);
        }
        guiToggleNoConstantView.addActionListener(toggleListener);
        guiToggleGroup.add(guiToggleNoConstantView);

        final JToggleButton guiToggleFullView  = new JToggleButton();
        guiToggleFullView.setText(MessageBundle.format("guiToogleAdvancedView"));
        guiToggleFullView.setName("tglFull");
        if (usedView == ADVANCED_VIEW) {
            guiToggleFullView.getModel().setSelected(true);
        }
        guiToggleFullView.addActionListener(toggleListener);
        guiToggleGroup.add(guiToggleFullView);

        guiToolbar.setFloatable(false);
        //save/load/execute actions
        guiToolbar.add(actNew);
        guiToolbar.add(actSave);
        guiToolbar.add(actLoad);
        guiToolbar.add(actExecute);
        guiToolbar.add(new JSeparator());
        //view actions
        guiToolbar.add(guiToggleBasicView);
        guiToolbar.add(guiToggleNoConstantView);
        guiToolbar.add(guiToggleFullView);

        add(BorderLayout.NORTH, guiToolbar);
        add(BorderLayout.CENTER, guiSplitPane);
    }

    public void setChain(final EventChain chain){
        this.chain = chain;

        //center
        guiCenterPanel.removeAll();

        //scene
        createView();
        final JComponent view = scene.createView();
        guiCenterPanel.add(BorderLayout.CENTER,new JScrollPane(view));
        guiCenterPanel.revalidate();
        guiCenterPanel.repaint();

        if(chain != null){
            final JProcessTreeModel processTreeModel = new JProcessTreeModel();
            for(ProcessingRegistry factory : chain.getFactories()){
                processTreeModel.addRegistry(factory);
            }

            this.guiProcessTree.setModel(processTreeModel);
            //this.guiDataTree.setModel(new JDataTreeModel(session));
            this.guiOtherTree.setModel(new JOtherTreeModel());
        }

    }

    public EventChain getChain() {
        return chain;
    }

    /**
     * Create the ChainView depending of usedView attribute.
     */
    private void createView() {

        if (scene != null) {
            scene.dispose();
        }

        if (usedView == BASIC_VIEW) {
            scene = new ChainSceneBasic(chain, editable);

        } else if (usedView == INTERMEDIATE_VIEW) {
            scene = new ChainSceneIntermediate(chain, editable);

        } else if (usedView == ADVANCED_VIEW) {
            scene = new ChainSceneAdvanced(chain, editable);
        }
    }

    /**
     * ActionListener used by toggle view buttons.
     */
    private class ToggleViewActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JToggleButton btn = (JToggleButton) e.getSource();

            if ("tglBasic".equals(btn.getName())) {
                usedView = BASIC_VIEW;

            } else if ("tglCst".equals(btn.getName())) {
                usedView = INTERMEDIATE_VIEW;

            } else if ("tglFull".equals(btn.getName())) {
                usedView = ADVANCED_VIEW;
            }

            guiCenterPanel.removeAll();
            //update scene.
            createView();

            final JComponent sceneView = scene.createView();
            guiCenterPanel.add(BorderLayout.CENTER, new JScrollPane(sceneView));
            guiCenterPanel.revalidate();
            guiCenterPanel.repaint();
        }
    }
}
