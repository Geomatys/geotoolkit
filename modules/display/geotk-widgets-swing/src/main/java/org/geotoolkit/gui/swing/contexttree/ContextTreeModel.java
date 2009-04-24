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
package org.geotoolkit.gui.swing.contexttree;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.column.TreeTableColumn;
import org.geotoolkit.gui.swing.contexttree.node.SubNodeGroup;
import org.geotoolkit.gui.swing.resource.IconBundle;

import org.geotoolkit.map.ContextListener;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.CollectionChangeEvent;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * ContextTreeModel for JContextTree
 * 
 * @author Johann Sorel
 */
public final class ContextTreeModel extends DefaultTreeTableModel implements ContextListener {
    
    /**
     * number of the tree column
     */
    public static final int TREE = 0;
    private static final SubNodeGroup[] EMPTY_SUBNODEGROUP_ARRAY = new SubNodeGroup[] {};
    
    private final EventListenerList listeners = new EventListenerList();    
    private final JContextTree frame;
    private final ArrayList<TreeTableColumn> columns = new ArrayList<TreeTableColumn>();
    private final ArrayList<SubNodeGroup> subgroups = new ArrayList<SubNodeGroup>();
    private final Vector columnNames = new Vector(); 
    
    private final LightContextTreeModel lightModel;
    
    private MapContext activeContext;
    private boolean treeedit = true;        
    

    /**
     * Creates a new instance of ContextTreeModel
     * prevent build model by other use
     * 
     */
    ContextTreeModel(JContextTree frame) {
        super();
        
        lightModel = new LightContextTreeModel(this);
                
        this.frame = frame;
                
        ContextTreeNode node = new ContextTreeNode(lightModel) {

            @Override
            public ImageIcon getIcon() {
                return IconBundle.EMPTY_ICON;
            }

            @Override
            public boolean isEditable() {
                return false;
            }

            @Override
            public Object getValue() {
                return "Root";
            }

            @Override
            public void setValue(Object obj) {
            }
        };
        setRoot(node);

        columnNames.add("");

        setColumnIdentifiers(columnNames);
        
    }
        

    /**
     * set if the treecolumn (maplayer and mapcontext titles) can be edited
     * @param b new value
     */
    void setTreeColumEditable(boolean b) {
        treeedit = b;
    }
    
    /**
     * move a node
     * @param newChild the moving node
     * @param father his new parent node
     * @param index position in the father node
     */
    void moveNode(MutableTreeTableNode newChild, MutableTreeTableNode father, int index) {
        super.removeNodeFromParent(newChild);
        super.insertNodeInto(newChild, father, index);
    }    
    
    /**
     * get the class of a specific column
     * @param column column number
     * @return Class of the column
     */
    @Override
    public Class getColumnClass(int column) {
        Class c ;

        if (column == TREE) {
            c = TreeTableModel.class;
        } else {
            if (column <= columns.size()) {
                c = columns.get(column - 1).getColumnClass();
            }else{
                c = Object.class;
            }
        }

        return c;
    }

    /**
     * get number of column
     * @return int
     */
    @Override
    public int getColumnCount() {
        return 1 + columns.size();
    }

    /**
     * know is the cell is editable
     * @param node specific node
     * @param column column number
     * @return editable state
     */
    @Override
    public boolean isCellEditable(Object node, int column) {

        if (column == TREE) {
            ContextTreeNode n = (ContextTreeNode) node;
            return (treeedit && n.isEditable(column));
        } else {
            if (column <= columns.size()) {
                return columns.get(column - 1).isCellEditable(((ContextTreeNode) node).getUserObject());
            } else {
                return false;
            }
        }
    }

    /**
     * insert a node at a specific node
     * @param newChild the new node
     * @param father the node who will contain the new node
     * @param index position of the new node
     */
    @Override
    public void insertNodeInto(MutableTreeTableNode newChild, MutableTreeTableNode father, int index) {
        super.insertNodeInto(newChild, father, index);
    }

    /**
     * remove a node from his parent
     * @param node the node to remove
     */
    @Override
    public void removeNodeFromParent(MutableTreeTableNode node) {
        super.removeNodeFromParent(node);
    }

    
        
////////////////////////////////////////////////////////////////////////////////
// COLUMNS MANAGEMENT //////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * add a new column in the model
     * @param model the new column model
     */
    void addColumnModel(TreeTableColumn model) {
        columns.add(model);
        columnNames.add(model.getTitle());
        setColumnIdentifiers(columnNames);
        
        model.setModelIndex(columns.indexOf(model) + 1);
    }
    
    void removeColumnModel(TreeTableColumn model) {
        int index = columns.indexOf(model);
        this.removeColumnModel(index);
        
    }
    
    void removeColumnModel(int index){
        columns.remove(index);
        columnNames.remove(index+1);
        setColumnIdentifiers(columnNames);
        
        for(TreeTableColumn col : columns){
            col.setModelIndex(columns.indexOf(col) + 1);
        }
        
    }
    
    public int getColumnModelCount() {
        return columns.size();
    }

    public TreeTableColumn getColumnModel(int index){
        return columns.get(index);
    }
    
    public int getColumnModelIndex(TreeTableColumn model) {
        return columns.indexOf(model);
    }
        
    /**
     * get the list of column
     * @return list of column models
     */
    ArrayList<TreeTableColumn> getColumnModels() {
        return columns;
    }

////////////////////////////////////////////////////////////////////////////////
// SUBNODES MANAGEMENT /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
    void addSubNodeGroup(SubNodeGroup group) {
        if(group!=null && !subgroups.contains(group)){
            subgroups.add(group);           
            visitNode(getRoot(),group);       
        }        
    }
    
    void removeSubNodeGroup(SubNodeGroup group) {
        if(group!=null && subgroups.contains(group)){                                           
            subgroups.remove(group);     
            cleanNode(getRoot(), group);
        }        
    }
            
    void removeSubNodeGroup(int index){
       SubNodeGroup grp = subgroups.get(index);
       removeSubNodeGroup(grp);
    }
    
    int getSubNodeGroupCount(){
       return subgroups.size();
    }
    
    int getSubNodeGroupIndex(SubNodeGroup group){
        return subgroups.indexOf(group);
    }
    
    SubNodeGroup[] getSubNodeGroups() {
        return subgroups.toArray(EMPTY_SUBNODEGROUP_ARRAY);
    }
        
    private void visitNode(TreeTableNode node, SubNodeGroup group){
                
        for(int i=0, max=node.getChildCount(); i<max;i++){
            visitNode(node.getChildAt(i),group);
        }
        
        if(node instanceof ContextTreeNode ){
            ContextTreeNode tn = (ContextTreeNode) node;
            Object obj = tn.getUserObject();
            if(group.isValid(obj)){
                group.installInNode(lightModel,tn);                
                
            }
        }
        
    }
    
    private void cleanNode(TreeTableNode node){
        
        for(SubNodeGroup sub : subgroups){
            cleanNode(node, sub);
        }
        
    }
    
    private void cleanNode(TreeTableNode node, SubNodeGroup group){
        
        for(int max=node.getChildCount(), i=max-1; i>=0;i--){
            cleanNode(node.getChildAt(i),group);
        }
        
        if(node instanceof ContextTreeNode ){
            ContextTreeNode tn = (ContextTreeNode) node;   
            Object obj = tn.getUserObject();
            if(group.isValid(obj)){
                group.removeForNode(lightModel,tn);
            }
                        
        }
    }
    
////////////////////////////////////////////////////////////////////////////////
// MAPCONTEXT MANAGEMENT ///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * get the active context
     * @return return the active MapContext, if none return null
     */
    public MapContext getActiveContext() {
        return activeContext;
    }

    /**
     * active the context if in the tree
     * @param context the mapcontext to active
     */
    void setActiveContext(MapContext context) {

        if (getMapContextIndex(context) >= 0) {
            ContextTreeNode node;

            if (activeContext != null) {
                node = (ContextTreeNode) getMapContextNode(activeContext);
                modelSupport.fireChildChanged(new TreePath(getRoot()), getMapContextIndex(activeContext), node);
            }

            activeContext = context;
            node = (ContextTreeNode) getMapContextNode(activeContext);
            modelSupport.fireChildChanged(new TreePath(getRoot()), getMapContextIndex(activeContext), node);
        } else if (activeContext != null) {
            ContextTreeNode node = (ContextTreeNode) getMapContextNode(activeContext);
            modelSupport.fireChildChanged(new TreePath(getRoot()), getMapContextIndex(activeContext), node);
            activeContext = null;
        }

        fireContextActivated(context, getMapContextIndex(context));
    }

    /**
     * add context to the Tree if not allready in it
     * @param context the context to add
     */
    void addMapContext(MapContext context) {

        if (getMapContextIndex(context) < 0) {
            context.addContextListener(this);

            ContextTreeNode node = new MapContextTreeNode(lightModel,context);

            insertNodeInto(node, (ContextTreeNode) getRoot(), getRoot().getChildCount());

            for (int i = context.layers().size() - 1; i >= 0; i--) {
                ContextTreeNode layer = new LayerContextTreeNode(lightModel,context.layers().get(i));
                insertNodeInto(layer, node, node.getChildCount());
            }

            fireContextAdded(context, getMapContextIndex(context));
            setActiveContext(context);
            
            //subnodes
            for(SubNodeGroup grp : subgroups){
                visitNode(node, grp);
            }
            
        }
    }

    /**
     * remove context from the tree
     * @param context target mapcontext to remove
     */
    void removeMapContext(MapContext context) {

        for (int i = 0; i < getRoot().getChildCount(); i++) {
            ContextTreeNode jm = (ContextTreeNode) getRoot().getChildAt(i);

            if (jm.getUserObject().equals(context)) {
                cleanNode(jm);
                removeNodeFromParent(jm);

                if (jm.getUserObject().equals(activeContext)) {
                    activeContext = null;
                    fireContextActivated(null, -1);
                }

                fireContextRemoved(context, i);
            }
        }
    }

    /**
     * count MapContext in the tree
     * @return number of mapcontext in the tree
     */
    int getMapContextCount() {
        return getRoot().getChildCount();
    }

    /**
     * return context at index i
     * @param i position of the mapcontext
     * @return the mapcontext a position i
     */
    MapContext getMapContext(int i) {
        return (MapContext) ((ContextTreeNode)getRoot().getChildAt(i)).getUserObject();
    }

    /**
     * get the index of a mapcontext in the tree
     * @param context the mapcontext to find
     * @return index of context, -1 if context isn't in the tree
     */
    int getMapContextIndex(MapContext context) {
        int ret = -1;

        if (context != null) {
            for (int i = 0; i < getRoot().getChildCount(); i++) {
                ContextTreeNode jm = (ContextTreeNode) getRoot().getChildAt(i);
                if (jm.getUserObject().equals(context)) {
                    ret = i;
                }
            }
        }

        return ret;
    }

    /**
     * return the node of context
     * <b>use with care!<b/>
     * @param context the context to find
     * @return the node contining the mapcontext
     */
    TreeNode getMapContextNode(MapContext context) {
        TreeNode node = null;

        if (context != null) {
            for (int i = 0; i < getRoot().getChildCount(); i++) {
                ContextTreeNode jm = (ContextTreeNode) getRoot().getChildAt(i);
                if (jm.getUserObject().equals(context)) {
                    node = jm;
                }
            }
        }

        return node;
    }

    /**
     * moveContext depending on nodes
     * <b>use with care!<b/>
     * @param moveNode the node to move
     * @param father the new parent node
     * @param place new position of the child node
     */
    void moveMapContext(ContextTreeNode moveNode, ContextTreeNode father, int place) {
        int depart = ((ContextTreeNode) getRoot()).getIndex(moveNode);

        removeNodeFromParent(moveNode);
        insertNodeInto(moveNode, father, place);

        fireContextMoved((MapContext) moveNode.getUserObject(), depart, place);
    }
    
    MapContext[] getMapContexts(){
        
        ContextTreeNode rootnode = ((ContextTreeNode)root);
        int childs = rootnode.getChildCount();
        MapContext[] contexts = new MapContext[childs];
        for(int i=0;i<childs;i++){
            contexts[i] = (MapContext) ((ContextTreeNode)rootnode.getChildAt(i)).getUserObject();
        }
        return contexts;
    }
     
    
////////////////////////////////////////////////////////////////////////////////
// LAYER MANAGEMENT - USE BY DRAG&DROP CLASSES /////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * add a maplayer into a node, used for drag and drop
     * @param newChild the new node
     * @param parent the father node
     * @param index new position of the child node
     */
    void insertLayerInto(ContextTreeNode newChild, ContextTreeNode parent, int index) {


        if (newChild.getUserObject() instanceof MapLayer && parent.getUserObject() instanceof MapContext) {

            MapContext context = (MapContext) parent.getUserObject();
            MapLayer layer = (MapLayer) newChild.getUserObject();


            index = context.layers().size() - index;
            if (index < 0) {
                index = 0;
            }
            if (index > context.layers().size()) {
                index = context.layers().size();
            }
            if (index > parent.getChildCount()) {
                index = 0;
            }
            context.layers().add(index, layer);
        }
    }

    /**
     * remove a node maplayer from it's parent
     * @param node the node to remove
     */
    void removeLayerFromParent(ContextTreeNode node) {

        if (node.getUserObject() instanceof MapLayer && ((ContextTreeNode) node.getParent()).getUserObject() instanceof MapContext) {

            MapContext context = (MapContext) ((ContextTreeNode)node.getParent()).getUserObject();
            MapLayer layer = (MapLayer) node.getUserObject();

            context.layers().remove(layer);
        }
    }

    /**
     * mode a specific node
     * @param Child the node to move
     * @param parent the new father node
     * @param index the position of the child node
     */
    void moveLayer(ContextTreeNode Child, ContextTreeNode parent, int destPosition) {

        if (Child.getUserObject() instanceof MapLayer && parent.getUserObject() instanceof MapContext) {

            MapContext context = (MapContext) parent.getUserObject();
            MapLayer layer = (MapLayer) Child.getUserObject();

            destPosition = context.layers().size() - 1 - destPosition;
            if (destPosition < 0) {
                destPosition = 0;
            }
            if (destPosition > context.layers().size()) {
                destPosition = context.layers().size();
            }
            if (destPosition > parent.getChildCount()) {
                destPosition = 0;
            }
            int sourcePosition = context.layers().indexOf(layer);

            context.moveLayer(sourcePosition, destPosition);

        }
    }

    
////////////////////////////////////////////////////////////////////////////////
// FIREEVENT AND LISTENERS /////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * generate a treeevent for an added node
     * @param mapcontext the added mapcontext
     * @param position the position of the mapcontext in the tree
     */
    private void fireContextAdded(MapContext mapcontext, int position) {
        TreeContextEvent kevent = new TreeContextEvent(frame, mapcontext, position);

        TreeContextListener[] list = getTreeContextListeners();
        for (int i = 0; i < list.length; i++) {
            list[i].contextAdded(kevent);
        }
    }

    /**
     * generate a treeevent for a mapcontext removed
     * @param mapcontext the removed mapcontext
     * @param position the last position of the mapcontext
     */
    private void fireContextRemoved(MapContext mapcontext, int position) {
        TreeContextEvent event = new TreeContextEvent(frame, mapcontext, position);

        TreeContextListener[] list = getTreeContextListeners();
        for (int i = 0; i < list.length; i++) {
            list[i].contextRemoved(event);
        }
    }

    /**
     * generate a treeevent for an activated mapcontext
     * @param mapcontext the activated mapcontext (null if none activated)
     * @param index the position of the activated context
     */
    private void fireContextActivated(MapContext mapcontext, int index) {
        TreeContextEvent event = new TreeContextEvent(frame, mapcontext, index);

        TreeContextListener[] list = getTreeContextListeners();
        for (int i = 0; i < list.length; i++) {
            list[i].contextActivated(event);
        }
    }

    /**
     * generate a treeevent for a moving context
     * @param mapcontext the moving mapcontext
     * @param begin the start position of the mapcontext
     * @param end the end position of the mapcontext
     */
    private void fireContextMoved(MapContext mapcontext, int begin, int end) {
        TreeContextEvent event = new TreeContextEvent(frame, mapcontext, begin, end);

        TreeContextListener[] list = getTreeContextListeners();
        for (int i = 0; i < list.length; i++) {
            list[i].contextMoved(event);
        }
    }

    /**
     * add treeListener to Model
     * @param ker the new listener
     */
    void addTreeContextListener(TreeContextListener ker) {
        listeners.add(TreeContextListener.class, ker);
    }

    /**
     * remove treeListener from Model
     * @param ker the listner to remove
     */
    void removeTreeContextListener(TreeContextListener ker) {
        listeners.remove(TreeContextListener.class, ker);
    }

    /**
     * get treeListeners list
     * @return the listener's table
     */
    TreeContextListener[] getTreeContextListeners() {
        return listeners.getListeners(TreeContextListener.class);
    }

    
////////////////////////////////////////////////////////////////////////////////
// MAPCONTEXT LISTENER /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * when a layer is added
     * @param mle the event
     */
    private void layerAdded(MapContext context, MapLayer mapLayer, int index) {
  
        int i = 0;
        boolean find = false;
        while (i < getRoot().getChildCount() && !find) {

            if (((ContextTreeNode) getRoot().getChildAt(i)).getUserObject().equals(context)) {

                ContextTreeNode layer = new LayerContextTreeNode(lightModel,mapLayer);
                ContextTreeNode father = (ContextTreeNode) getRoot().getChildAt(i);


                index = context.layers().size() - 1 - index;
                if (index > father.getChildCount()) {
                    index = father.getChildCount();
                }
                if (index < 0) {
                    index = 0;
                }
                insertNodeInto(layer, father, index);
                
                //subnodes
                for(SubNodeGroup grp : subgroups){
                    visitNode(layer, grp);
                }
                
            }
            i++;
        }
    }

    /**
     * when a layer is removed
     * @param mle the event
     */
    private void layerRemoved(MapContext context,MapLayer layer) {

        int i = 0;
        boolean find = false;
        while (i < getRoot().getChildCount() && !find) {

            if (((ContextTreeNode) getRoot().getChildAt(i)).getUserObject().equals(context)) {

                ContextTreeNode father = (ContextTreeNode) getRoot().getChildAt(i);

                for (int t = 0; t < father.getChildCount(); t++) {
                    ContextTreeNode node = (ContextTreeNode) father.getChildAt(t);

                    if (layer.equals(node.getUserObject())) {
                        cleanNode(node);                        
                        removeNodeFromParent(node);
                    }
                }
            }
            i++;
        }
    }

    /**
     * when a layer changed
     * @param mle the event
     */
    private void layerChanged(MapContext context,MapLayer layer) {

        int i = 0;
        boolean find = false;
        while (i < getRoot().getChildCount() && !find) {

            if (((ContextTreeNode) getRoot().getChildAt(i)).getUserObject().equals(context)) {

                ContextTreeNode father = (ContextTreeNode) getRoot().getChildAt(i);

                for (int t = 0; t < father.getChildCount(); t++) {
                    ContextTreeNode node = (ContextTreeNode) father.getChildAt(t);

                    if (layer.equals(node.getUserObject())) {
                        modelSupport.fireChildChanged(new TreePath(getPathToRoot(father)), father.getIndex(node), node);
                    }
                }
            }
            i++;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

                }

    @Override
    public void layerChange(CollectionChangeEvent<MapLayer> event) {
        final int type = event.getType();
        
        if(CollectionChangeEvent.ITEM_ADDED == type){
            for(MapLayer layer : event.getItems()){
                layerAdded((MapContext) event.getSource(),layer,(int) event.getRange().getMaximum());
                }
        }else if(CollectionChangeEvent.ITEM_REMOVED == type){
            for(MapLayer layer : event.getItems()){
                layerRemoved((MapContext) event.getSource(),layer);
                }
        }else if(CollectionChangeEvent.ITEM_CHANGED == type){
            for(MapLayer layer : event.getItems()){
                layerChanged((MapContext) event.getSource(),layer);
                }
                }


    }
}
