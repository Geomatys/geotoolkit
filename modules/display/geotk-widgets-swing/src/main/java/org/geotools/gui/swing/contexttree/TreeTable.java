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
package org.geotools.gui.swing.contexttree;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableStyleFactory;

import org.geotools.gui.swing.contexttree.column.TreeTableColumn;
import org.geotools.gui.swing.contexttree.node.SubNodeGroup;
import org.geotools.gui.swing.contexttree.renderer.DefaultContextTreeHeaderRenderer;
import org.geotools.gui.swing.contexttree.renderer.HeaderInfo;
import org.geotools.gui.swing.misc.FacilitiesFactory;
import org.geotools.gui.swing.resource.MessageBundle;

import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * TreeTable
 * 
 * @author Johann Sorel
 */
final class TreeTable extends org.jdesktop.swingx.JXTreeTable {

    public static final MutableStyleFactory STYLE_FACTORY;
    
    private static final TreePath[] EMPTY_PATH = {};

    static{

        final Hints hints = new Hints();
        hints.put(Hints.STYLE_FACTORY, MutableStyleFactory.class);
        STYLE_FACTORY = (MutableStyleFactory)FactoryFinder.getStyleFactory(hints);
    }


    /**
     * Default copy action used for Key Input
     */
    private final Action COPY_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            copySelectionInBuffer();
        }
    };
    /**
     * Default cut action used for Key Input
     */
    private final Action CUT_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            cutSelectionInBuffer();
        }
    };
    /**
     * Default paste action used for Key Input
     */
    private final Action PASTE_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            pasteBuffer();
        }
    };
    /**
     * Default delete action used for Key Input
     */
    private final Action DELETE_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelection();
        }
    };
    /**
     * Default duplicate action used for Key Input
     */
    private final Action DUPLICATE_ACTION = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            duplicateSelection();
        }
    };
    /**
     * the buffer containing the cutted/copied datas
     */
    private final List<Object> buffer = new ArrayList<Object>();
    private JContextTreePopup popupManager;
    private TreeSelectionManager selectionManager;
    /**
     * String added to layer name use when paste/duplicate
     */
    private String PREFIX = "-" + MessageBundle.getString("contexttreetable_a_copy") + "- ";

    /**
     * Tree widget to manage MapContexts and MapLayers
     * 
     */
    TreeTable(JContextTree frame) {
        super(new ContextTreeModel(frame));
        
        init(frame);
    }
    
    private void init(JContextTree frame){
        
        putClientProperty("JTree.lineStyle", "Angled");
        selectionManager = new TreeSelectionManager(frame);
        popupManager = new JContextTreePopup(this, frame);

        setComponentPopupMenu(popupManager.getPopupMenu());
        setColumnControlVisible(true);

        setTreeCellRenderer(new DefaultTreeRenderer(new TreeNodeProvider(frame)));
        getTableHeader().setDefaultRenderer(new DefaultContextTreeHeaderRenderer());

        initCellEditAcceleration();
        initDragAndDrop();
        initKeySupport();

        String name = "";
        getColumnModel().getColumn(0).setHeaderValue(new HeaderInfo(name, " ", null));


        getTreeSelectionModel().addTreeSelectionListener(selectionManager);
    }

    /**
     * add mouse listener to set cell in edit mode when mouseover
     */
    private void initCellEditAcceleration() {
        //listener to set cell in edit mode on mouse over
        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                if (p != null) {
                    int row = rowAtPoint(p);
                    int col = columnAtPoint(p);

                    if (row != editingRow || col != editingColumn) {

                        if (isEditing()) {
                            TableCellEditor editor = cellEditor;
                            if (!editor.stopCellEditing()) {
                                editor.cancelCellEditing();
                            }
                        }

                        if (!isEditing() && col >= 0 && row >= 0) {

                            //we handle differently ContextTreeColumn
                            if (getColumnExt(col) instanceof TreeTableColumn) {
                                TreeTableColumn column = (TreeTableColumn) getColumnExt(col);
                                if (isCellEditable(row, col) && column.isEditableOnMouseOver()) {
                                    editCellAt(row, col);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void initDragAndDrop() {
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        DADContextTreeTransferHandler handler = new DADContextTreeTransferHandler();
        setTransferHandler(handler);
        setDropTarget(new DADContextTreeDrop(handler));
        setDragEnabled(true);

    }

    private void initKeySupport() {
        InputMap inputMap = getInputMap();
        ActionMap actionMap = getActionMap();

        KeyStroke copyKeys = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
        KeyStroke cutKeys = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK);
        KeyStroke pasteKeys = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK);
        KeyStroke deleteKeys = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        KeyStroke duplicateKeys = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK);
        String copycode = "copy";
        String cutcode = "cut";
        String pastecode = "paste";
        String deletecode = "delete";
        String duplicatecode = "duplicate";

        inputMap.put(copyKeys, copycode);
        inputMap.put(cutKeys, cutcode);
        inputMap.put(pasteKeys, pastecode);
        inputMap.put(deleteKeys, deletecode);
        inputMap.put(duplicateKeys, duplicatecode);
        actionMap.put(copycode, COPY_ACTION);
        actionMap.put(cutcode, CUT_ACTION);
        actionMap.put(pastecode, PASTE_ACTION);
        actionMap.put(deletecode, DELETE_ACTION);
        actionMap.put(duplicatecode, DUPLICATE_ACTION);

    }

    private MapContext findContext(TreePath tp) {
        ContextTreeNode lastnode = (ContextTreeNode) tp.getLastPathComponent();
        Object obj = lastnode.getUserObject();
        if (obj instanceof MapContext) {
            return (MapContext) obj;
        } else if (lastnode.getParent().equals(getTreeTableModel().getRoot())) {
            return null;
        } else {
            return findContext(new TreePath(lastnode.getParent()));
        }
    }

    private MapLayer findLayer(TreePath tp) {
        ContextTreeNode lastnode = (ContextTreeNode) tp.getLastPathComponent();
        Object obj = lastnode.getUserObject();
        if (obj instanceof MapLayer) {
            return (MapLayer) obj;
        } else if (lastnode.getParent().equals(getTreeTableModel().getRoot())) {
            return null;
        } else {
            return findLayer(new TreePath(lastnode.getParent()));
        }
    }

    TreeSelectionManager getSelectionManager() {
        return selectionManager;
    }

    JContextTreePopup getPopupMenu() {
        return popupManager;
    }

    List<Object> getSelectionList() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();
        List<Object> temp = new ArrayList<Object>();

        if (hasSelection(selections)) {

            for (TreePath tp : selections) {
                temp.add(((ContextTreeNode) tp.getLastPathComponent()).getUserObject());
            }

        }

        return temp;
    }

    /**
     * get the tree model. dont play with the model, too much things are linked    
     * @return the tree model
     */
    @Override
    public ContextTreeModel getTreeTableModel() {
        return (ContextTreeModel) super.getTreeTableModel();
    }

    /**
     * set the tree model. dont play with the model, too much things are linked
     * @param contexttreemodel the new model, <b>MUST</b> be a ContextTreeModel.
     * 
     */
    @Override
    public void setTreeTableModel(TreeTableModel contexttreemodel) {
        if (contexttreemodel != null) {
            if (contexttreemodel instanceof ContextTreeModel) {
                super.setTreeTableModel(contexttreemodel);
            } else {
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
// CUT/COPY/PASTE/DUPLICATE/DELETE  ////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    private boolean hasSelection(TreePath[] selections) {

        if (selections != null) {
            if (selections.length > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyMapContexts(TreePath[] paths) {
        
        if(paths == null){
            return false;
        }
        
        for (TreePath path : paths) {
            if (!(((ContextTreeNode) path.getLastPathComponent()).getUserObject() instanceof MapContext)) {
                return false;
            }
        }
        return true;
    }

    public boolean onlyMapContexts(List<Object> buf) {
        for (Object obj : buf) {
            if (!(obj instanceof MapContext)) {
                return false;
            }
        }
        return true;
    }
    
    
    public boolean onlyMapLayers(TreePath[] paths) {
        
        if(paths == null){
            return false;
        }
        
        for (TreePath path : paths) {
            if (!(((ContextTreeNode) path.getLastPathComponent()).getUserObject() instanceof MapLayer)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean onlyMapLayers(List<Object> buf) {
        for (Object obj : buf) {
            if (!(obj instanceof MapLayer)) {
                return false;
            }
        }
        return true;
    }

    /**
     *  prefix string used when pasting/duplicating datas
     * 
     * @param prefix if null, prefix will be an empty string
     */
    void setPrefixString(String prefix) {
        if (prefix != null) {
            PREFIX = prefix;
        } else {
            PREFIX = "";
        }
    }

    /**
     * prefix used when pasting/duplicating datas
     * 
     * @return String 
     */
    String getPrefixString() {
        return PREFIX;
    }

    /**
     * 
     * @return true if ther is something selected
     */
    boolean hasSelection() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

        if (selections != null) {
            if (selections.length > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Duplicate was is actually selected in the tree. nothing happens
     * if selection isn't composed of only 1 type of datas. (only layers or only contexts )
     * 
     * @return true if duplication succeed
     */
    boolean duplicateSelection() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

        if(selections == null){
            selections = EMPTY_PATH;
        }
        
        if (canDuplicateSelection()) {
            FacilitiesFactory ff = new FacilitiesFactory();

            if (onlyMapLayers(selections)) {

                for (TreePath tp : selections) {
                    MapLayer layer = (MapLayer) ((ContextTreeNode) tp.getLastPathComponent()).getUserObject();
                    MapContext parent = (MapContext) ((ContextTreeNode) ((ContextTreeNode) tp.getLastPathComponent()).getParent()).getUserObject();
                    MapLayer copylayer = ff.duplicateLayer(layer);
                    copylayer.setDescription(STYLE_FACTORY.description(PREFIX+ layer.getDescription().getTitle(), ""));

                    parent.layers().add(copylayer);
                    parent.moveLayer(parent.layers().indexOf(copylayer), parent.layers().indexOf(layer));
                }
                return true;


            } else if (onlyMapContexts(selections)) {

                for (TreePath tp : selections) {
                    MapContext context = (MapContext) ((ContextTreeNode) tp.getLastPathComponent()).getUserObject();
                    MapContext copycontext = ff.duplicateContext(context);
                    copycontext.setDescription(STYLE_FACTORY.description(PREFIX + context.getDescription().getTitle().toString(), ""));

                    getTreeTableModel().addMapContext(copycontext);
                }

                return true;
            }
        }

        return false;
    }

    /**
     * 
     * @return true if tree buffer is empty
     */
    boolean isBufferEmpty() {
        if (buffer.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return true is paste can succeed
     */
    boolean canPasteBuffer() {
        if (isBufferEmpty()) {
            return false;
        } else {

            if (onlyMapContexts(buffer)) {
                return true;
            } else if (onlyMapLayers(buffer)) {
                TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

                if (hasSelection(selections)) {
                    if (selections.length == 1) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * 
     * @return true if duplication can succeed
     */
    boolean canDuplicateSelection() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

        if(selections == null){
            selections = EMPTY_PATH;
        }
        
        
        if (hasSelection(selections)) {
            return (onlyMapContexts(selections) || onlyMapLayers(selections));
        } else {
            return false;
        }
    }

    /**
     * 
     * @return true if delete can succeed
     */
    boolean canDeleteSelection() {
        return canDuplicateSelection();
    }

    /**
     * 
     * @return true if copy can succeed
     */
    boolean canCopySelection() {
        return canDuplicateSelection();
    }

    /**
     * 
     * @return true if cut can succeed
     */
    boolean canCutSelection() {
        return canDuplicateSelection();
    }

    /**
     * delete what is actually selected
     * 
     * @return true if delete suceed
     */
    boolean deleteSelection() {

        if (canDeleteSelection()) {
            TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

                    if(selections == null){
            selections = EMPTY_PATH;
        }
            
            for (int i = selections.length - 1; i >= 0; i--) {
                TreePath tp = selections[i];

                if (((ContextTreeNode) tp.getLastPathComponent()).getUserObject() instanceof MapLayer) {
                    MapLayer layer = (MapLayer) ((ContextTreeNode) tp.getLastPathComponent()).getUserObject();
                    MapContext parent = (MapContext) ((ContextTreeNode) ((ContextTreeNode) tp.getLastPathComponent()).getParent()).getUserObject();

                    parent.layers().remove(layer);

                } else if (((ContextTreeNode) tp.getLastPathComponent()).getUserObject() instanceof MapContext) {
                    MapContext context = (MapContext) ((ContextTreeNode) tp.getLastPathComponent()).getUserObject();
                    getTreeTableModel().removeMapContext(context);
                }
            }

            return true;
        }

        return false;

    }

    /**
     * copy what is actually selected in the tree buffer
     * 
     * @return true if copy succeed
     */
    boolean copySelectionInBuffer() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

        if (hasSelection(selections)) {
            buffer.clear();

            if (onlyMapLayers(selections)) {
                for (TreePath tp : selections) {
                    ContextTreeNode lastnode = (ContextTreeNode) tp.getLastPathComponent();
                    MapLayer layer = (MapLayer) lastnode.getUserObject();
                    buffer.add(layer);
                }
                return true;

            } else if (onlyMapContexts(selections)) {
                for (TreePath tp : selections) {
                    ContextTreeNode lastnode = (ContextTreeNode) tp.getLastPathComponent();
                    MapContext context = (MapContext) lastnode.getUserObject();
                    buffer.add(context);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * copy what is actually selected in the tree buffer and cut it from the tree.
     * 
     * @return true if cut succeed
     */
    boolean cutSelectionInBuffer() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();

        if (hasSelection(selections)) {
            buffer.clear();

            if (onlyMapLayers(selections)) {
                for (TreePath path : selections) {
                    ContextTreeNode childNode = (ContextTreeNode) path.getLastPathComponent();
                    ContextTreeNode parentNode = (ContextTreeNode) childNode.getParent();

                    buffer.add(childNode.getUserObject());
                    ((MapContext) parentNode.getUserObject()).layers().remove((MapLayer) childNode.getUserObject());
                }
                return true;
            } else if (onlyMapContexts(selections)) {
                for (TreePath path : selections) {
                    ContextTreeNode parentNode = (ContextTreeNode) path.getLastPathComponent();

                    buffer.add(parentNode.getUserObject());
                    removeMapContext((MapContext) parentNode.getUserObject());
                }
                return true;
            }


        }
        return false;
    }

    /**
     * paste at the selected node what is in the buffer
     * 
     * @return true if paste succeed
     */
    boolean pasteBuffer() {
        TreePath[] selections = getTreeSelectionModel().getSelectionPaths();


        if (!isBufferEmpty()) {
            FacilitiesFactory ff = new FacilitiesFactory();


            if (onlyMapLayers(buffer)) {

                if (hasSelection(selections)) {

                    if (selections.length == 1) {

                        if (((ContextTreeNode) selections[0].getLastPathComponent()).getUserObject() instanceof MapLayer) {

                            MapLayer insertlayer = (MapLayer) ((ContextTreeNode) selections[0].getLastPathComponent()).getUserObject();
                            MapContext parent = (MapContext) ((ContextTreeNode) ((ContextTreeNode) selections[0].getLastPathComponent()).getParent()).getUserObject();

                            for (Object data : buffer) {
                                MapLayer layer = (MapLayer) data;

                                if (parent.layers().indexOf(layer) == -1) {
                                    parent.layers().add(layer);
                                    parent.moveLayer(parent.layers().indexOf(layer), parent.layers().indexOf(insertlayer));
                                } else {
                                    MapLayer copy = ff.duplicateLayer(layer);
                                    copy.setDescription(STYLE_FACTORY.description(PREFIX + layer.getDescription().getTitle().toString(), ""));
                                    parent.layers().add(copy);
                                    parent.moveLayer(parent.layers().indexOf(copy), parent.layers().indexOf(insertlayer));
                                }
                            }


                        } else if (((ContextTreeNode) selections[0].getLastPathComponent()).getUserObject() instanceof MapContext) {
                            MapContext context = (MapContext) ((ContextTreeNode) selections[0].getLastPathComponent()).getUserObject();


                            for (Object data : buffer) {
                                MapLayer layer = (MapLayer) data;

                                if (context.layers().indexOf(layer) == -1) {
                                    context.layers().add(layer);
                                } else {
                                    MapLayer copy = ff.duplicateLayer(layer);
                                    copy.setDescription(STYLE_FACTORY.description(PREFIX + layer.getDescription().getTitle().toString() , ""));
                                    context.layers().add(copy);
                                    context.moveLayer(context.layers().indexOf(copy), context.layers().indexOf(layer));
                                }
                            }
                        }
                        return true;
                    }
                }

            } else if (onlyMapContexts(buffer)) {

                for (Object data : buffer) {
                    MapContext context = (MapContext) data;

                    if (getMapContextIndex(context) == -1) {
                        addMapContext(context);
                    } else {
                        addMapContext(ff.duplicateContext(context));
                    }

                }

                buffer.clear();
                return true;
            }

        }
        return false;
    }

    /**
     * get a Array of the objects in the buffer
     * 
     * @return object array, can be MapLayers or MapContexts or empty array
     */
    Object[] getBuffer() {
        return buffer.toArray(new Object[buffer.size()]);
    }

    void clearBuffer() {
        buffer.clear();
    }

////////////////////////////////////////////////////////////////////////////////
// COLUMNS MANAGEMENT //////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    /**
     * add a new column in the model and update the treetable
     * @param model the new column model
     */
    void addColumnModel(TreeTableColumn model) {
        getTreeTableModel().addColumnModel(model);
        getColumnModel().addColumn(model);
        revalidate();
    }

    /**
     * remove column
     * @param model
     */
    void removeColumnModel(TreeTableColumn model) {
        getTreeTableModel().removeColumnModel(model);
        getColumnModel().removeColumn(model);
        revalidate();
    }

    /**
     * remove column at index column
     * @param column
     */
    void removeColumnModel(int column) {
        getTreeTableModel().removeColumnModel(column);
        getColumnModel().removeColumn(getColumnModel().getColumn(column));
        revalidate();
    }

    int getColumnModelIndex(TreeTableColumn model) {
        return getTreeTableModel().getColumnModelIndex(model);
    }

    public int getColumnModelCount() {
        return getTreeTableModel().getColumnModelCount();
    }

    /**
     * get the list of column
     * @return list of column models
     */
    TreeTableColumn[] getColumnModels() {
        return getTreeTableModel().getColumnModels().toArray(new TreeTableColumn[0]);
    }

////////////////////////////////////////////////////////////////////////////////
// SUBNODES MANAGEMENT /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    void addSubNodeGroup(SubNodeGroup group) {
        getTreeTableModel().addSubNodeGroup(group);
        revalidate();
    }

    void removeSubNodeGroup(SubNodeGroup group) {
        getTreeTableModel().removeSubNodeGroup(group);
    }

    void removeSubNodeGroup(int index) {
        getTreeTableModel().removeSubNodeGroup(index);
    }

    int getSubNodeGroupCount() {
        return getTreeTableModel().getSubNodeGroupCount();
    }

    int getSubNodeGroupIndex(SubNodeGroup group) {
        return getTreeTableModel().getSubNodeGroupIndex(group);
    }

    SubNodeGroup[] getSubNodeGroups() {
        return getTreeTableModel().getSubNodeGroups();
    }

////////////////////////////////////////////////////////////////////////////////
// MAPCONTEXT MANAGEMENT ///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    /**
     * get the active context
     * @return return the active MapContext, if none return null
     */
    MapContext getActiveContext() {
        return getTreeTableModel().getActiveContext();
    }

    /**
     * active the context if in the tree
     * @param context the mapcontext to active
     */
    void setActiveContext(MapContext context) {
        getTreeTableModel().setActiveContext(context);
    }

    /**
     * add context to the Tree if not allready in it
     * @param context the context to add
     */
    void addMapContext(MapContext context) {
        getTreeTableModel().addMapContext(context);
        expandPath(new TreePath(getTreeTableModel().getRoot()));
        expandPath(new TreePath(getTreeTableModel().getMapContextNode(context)));
    }

    /**
     * remove context from the tree
     * @param context target mapcontext to remove
     */
    void removeMapContext(MapContext context) {
        getTreeTableModel().removeMapContext(context);
    }

    /**
     * count MapContext in the tree
     * @return number of mapcontext in the tree
     */
    int getMapContextCount() {
        return getTreeTableModel().getMapContextCount();
    }

    /**
     * return context at index i
     * @param i position of the mapcontext
     * @return the mapcontext a position i
     */
    MapContext getMapContext(int i) {
        return getTreeTableModel().getMapContext(i);
    }

    /**
     * get the index of a mapcontext in the tree
     * @param context the mapcontext to find
     * @return index of context
     */
    int getMapContextIndex(MapContext context) {
        return getTreeTableModel().getMapContextIndex(context);
    }

    /**
     * MapContext Array
     * @return empty Array if no mapcontexts in tree
     */
    MapContext[] getMapContexts() {
        return getTreeTableModel().getMapContexts();
    }

    /**
     * move a mapcontext
     * @param context the context to move
     * @param newplace new position of the child node
     */
    void moveMapContext(MapContext context, int newplace) {
        ContextTreeNode moveNode = (ContextTreeNode) getTreeTableModel().getMapContextNode(context);
        ContextTreeNode father = (ContextTreeNode) moveNode.getParent();
        getTreeTableModel().moveMapContext(moveNode, father, newplace);

    }

////////////////////////////////////////////////////////////////////////////////
// LISTENERS MANAGEMENT ////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    /**
     * add treeListener to Model
     * @param ker the new listener
     */
    void addTreeContextListener(TreeContextListener ker) {
        getTreeTableModel().addTreeContextListener(ker);
    }

    /**
     * remove treeListener from Model
     * @param ker the listner to remove
     */
    void removeTreeContextListener(TreeContextListener ker) {
        getTreeTableModel().removeTreeContextListener(ker);
    }

    /**
     * get treeListeners list
     * @return the listener's table
     */
    TreeContextListener[] getTreeContextListeners() {
        return getTreeTableModel().getTreeContextListeners();
    }
}

