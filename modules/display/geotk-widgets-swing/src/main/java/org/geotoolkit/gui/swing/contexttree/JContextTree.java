/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.TreeSelectionModel;

import org.geotoolkit.gui.swing.contexttree.column.TreeTableColumn;
import org.geotoolkit.gui.swing.contexttree.column.VisibleTreeTableColumn;
import org.geotoolkit.gui.swing.contexttree.node.SubNodeGroup;
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
import org.geotoolkit.gui.swing.map.map2d.Map2D;

import org.geotoolkit.map.MapContext;

/**
 * JContextTree is used to handle MapContexts and their MapLayers. this component
 * is based on a JXTreeTable.
 * 
 * @author Johann Sorel
 */
public class JContextTree extends javax.swing.JPanel{

    private TreeTable treetable;
    
    /**
     * build a default JContextTree.
     */
    public JContextTree(){
        init();
    }
    
    private void init(){
        treetable = new TreeTable(this);
        
        JScrollPane pane = new JScrollPane(treetable);
        pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250,100));
        
        add(BorderLayout.CENTER,pane);
    }
    
        
    /**
     * get the Popupmenu manager
     * @return JContextTreePopup
     */
    public JContextTreePopup getPopupMenu(){
        return treetable.getPopupMenu();
    }
        
    /**
     * model that handle selections
     * @return TreeSelectionModel
     */
    public TreeSelectionModel getTreeSelectionModel(){
        return treetable.getTreeSelectionModel();
    }
        
    /**
     * 
     * @return true if selection is only composed of MapLayer
     */
    public boolean selectionContainOnlyLayers(){
        return treetable.onlyMapLayers(getTreeSelectionModel().getSelectionPaths());
    }
    
    /**
     * 
     * @return true if selection is only composed of MapContext
     */
    public boolean selectionContainOnlyContexts(){
        return treetable.onlyMapContexts(getTreeSelectionModel().getSelectionPaths());
    }
    
////////////////////////////////////////////////////////////////////////////////
// STATIC CONSTRUCTORS /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * create a default TreeTable, with default columns
     * and default JContextTreePopup items
     * 
     * @param map
     * @return default TreeTable
     */
    public static JContextTree createDefaultTree(Map2D map) {
        JContextTree tree = new JContextTree();

        tree.addColumn(new VisibleTreeTableColumn());
                
        
        JContextTreePopup popup = tree.getPopupMenu();        
                
        popup.addItem(new SeparatorItem() );        
        popup.addItem(new LayerFeatureItem());              //layer
        popup.addItem(new ContextActiveItem(tree));         //context
        popup.addItem(new SeparatorItem() );
        popup.addItem(new CutItem(tree));                   //all
        popup.addItem(new CopyItem(tree));                  //all
        popup.addItem(new PasteItem(tree));                 //all
        popup.addItem(new DuplicateItem(tree));             //all        
        popup.addItem(new SeparatorItem() );        
        popup.addItem(new DeleteItem(tree));                //all
        popup.addItem(new SeparatorItem() );        
        popup.addItem(new LayerPropertyItem());             //layer
        popup.addItem(new ContextPropertyItem());           //context
                
        
        tree.revalidate();

        return tree;
    }
        
////////////////////////////////////////////////////////////////////////////////
// CUT/COPY/PASTE/DUPLICATE/DELETE  ////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
    
    /**
     *  prefix string used when pasting/duplicating datas
     * 
     * @param prefix if null, prefix will be an empty string
     */
    public void setPrefixString(String prefix){
        treetable.setPrefixString(prefix);
    }
        
    /**
     * prefix used when pasting/duplicating datas
     * 
     * @return String 
     */
    public String getPrefixString() {
        return treetable.getPrefixString();
    }

    /**
     * 
     * @return true if ther is something selected
     */
    public boolean hasSelection() {
        return treetable.hasSelection();
    }

    /**
     * Duplicate was is actually selected in the tree. nothing happens
     * if selection isn't composed of only 1 type of datas. (only layers or only contexts )
     * 
     * @return true if duplication succeed
     */
    public boolean duplicateSelection() {
        return treetable.duplicateSelection();
    }
    
    /**
     * 
     * @return true if tree buffer is empty
     */
    public boolean isBufferEmpty() {
        return treetable.isBufferEmpty();
    }

    /**
     * 
     * @return true is paste can succeed
     */
    public boolean canPasteBuffer() {
        return treetable.canPasteBuffer();
    }
    
    /**
     * 
     * @return true if duplication can succeed
     */
    public boolean canDuplicateSelection() {
        return treetable.canDuplicateSelection();
    }

    /**
     * 
     * @return true if delete can succeed
     */
    public boolean canDeleteSelection() {
        return treetable.canDeleteSelection();
    }

    /**
     * 
     * @return true if copy can succeed
     */
    public boolean canCopySelection() {
        return treetable.canCopySelection();
    }

    /**
     * 
     * @return true if cut can succeed
     */
    public boolean canCutSelection() {
        return treetable.canCutSelection();
    }

    /**
     * delete what is actually selected
     * 
     * @return true if delete suceed
     */
    public boolean deleteSelection() {
        return treetable.deleteSelection();
    }

    /**
     * copy what is actually selected in the tree buffer
     * 
     * @return true if copy succeed
     */
    public boolean copySelectionInBuffer() {
        return treetable.copySelectionInBuffer();
    }

    /**
     * copy what is actually selected in the tree buffer and cut it from the tree.
     * 
     * @return true if cut succeed
     */
    public boolean cutSelectionInBuffer() {
        return treetable.cutSelectionInBuffer();
    }
 
    /**
     * paste at the selected node what is in the buffer
     * 
     * @return true if paste succeed
     */
    public boolean pasteBuffer() {
        return treetable.pasteBuffer();
    }

    /**
     * get a Array of the objects in the buffer
     * 
     * @return object array, can be MapLayers or MapContexts or empty array
     */
    public Object[] getBuffer() {
        return treetable.getBuffer();
    }

    /**
     * clear the buffer
     */
    public void clearBuffer(){
        treetable.clearBuffer();
    }
    
        
////////////////////////////////////////////////////////////////////////////////
// COLUMNS MANAGEMENT //////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * add a new column in the model and update the treetable
     * @param model the new column model
     */
    public void addColumn(TreeTableColumn model) {
        treetable.addColumnModel(model);
    }
    
    /**
     * remove column
     * @param model
     */
    public void removeColumn(TreeTableColumn model){
        treetable.removeColumnModel(model);
    }
    
    /**
     * remove column at index column
     * @param column
     */
    public void removeColumn(int column){
             treetable.removeColumnModel(column);
    }
    
    /**
     * 
     * @return number of columns (without the tree column)
     */
    public int getColumnCount(){
        return treetable.getColumnCount();
    }
    
    /**
     * 
     * @param model
     * @return index of model column
     */
    public int getColumnIndex(TreeTableColumn model){
        return treetable.getColumnModelIndex(model);
    }
    
    /**
     * get the list of column
     * @return list of column models
     */
    public TreeTableColumn[] getColumns() {
        return treetable.getColumnModels();
    }

////////////////////////////////////////////////////////////////////////////////
// SUBNODES MANAGEMENT /////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
    /**
     * add a SubNodeGroup
     * @param group
     */
    public void addSubNodeGroup(SubNodeGroup group){
        treetable.addSubNodeGroup(group);
    }
    
    /**
     * remove SubNodeGroup
     * @param group : SubNodeGroup
     */
    public void removeSubNodeGroup(SubNodeGroup group){
        treetable.removeSubNodeGroup(group);
    }
    
    /**
     * remove SubNodeGroup at index 
     * @param index
     */
    public void removeSubNodeGroup(int index){
       treetable.removeSubNodeGroup(index);
    }
    
    /**
     * 
     * @return number of SubNodeGroup
     */
    public int getSubNodeGroupCount(){
       return treetable.getSubNodeGroupCount();
    }
    
    /**
     * 
     * @param group
     * @return index of SubNodeGroup
     */
    public int getSubNodeGroupIndex(SubNodeGroup group){
        return treetable.getSubNodeGroupIndex(group);
    }
    
    /**
     * get the list of SubNodeGroup
     * @return list of SubNodeGroup
     */
    public SubNodeGroup[] getSubNodeGroups() {
        return treetable.getSubNodeGroups();
    }
    
    
////////////////////////////////////////////////////////////////////////////////
// MAPCONTEXT MANAGEMENT ///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * get the active context
     * @return return the active MapContext, if none return null
     */
    public MapContext getActiveContext() {
        return treetable.getActiveContext();
    }
    
    /**
     * active the context if in the tree
     * @param context the mapcontext to active
     */
    public void setActiveContext(MapContext context) {
        treetable.setActiveContext(context);
    }
    
    /**
     * add context to the Tree if not allready in it
     * @param context the context to add
     */
    public void addContext(MapContext context) {
        treetable.addMapContext(context);
    }
    
    /**
     * remove context from the tree
     * @param context target mapcontext to remove
     */
    public void removeContext(MapContext context) {
        treetable.removeMapContext(context);
    }
    
    /**
     * count MapContext in the tree
     * @return number of mapcontext in the tree
     */
    public int getContextCount() {
        return treetable.getMapContextCount();
    }
    
    /**
     * return context at index i
     * @param i position of the mapcontext
     * @return the mapcontext a position i
     */
    public MapContext getContext(int i) {
        return treetable.getMapContext(i);
    }
    
    /**
     * get the index of a mapcontext in the tree
     * @param context the mapcontext to find
     * @return index of context
     */
    public int getContextIndex(MapContext context) {
        return treetable.getMapContextIndex(context);
    }
    
    /**
     * MapContext Array
     * @return empty Array if no mapcontexts in tree
     */
    public MapContext[] getContexts(){
        return treetable.getMapContexts();
    }
        
    /**
     * move a mapcontext
     * @param context the context to move
     * @param newplace new position of the child node
     */
    public void moveContext(MapContext context, int newplace) {
        treetable.moveMapContext(context, newplace);
    }

////////////////////////////////////////////////////////////////////////////////
// LISTENERS MANAGEMENT ////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * add tree context Listener 
     * @param ker the new listener
     */
    public void addTreeContextListener(TreeContextListener ker) {
        treetable.addTreeContextListener(ker);
    }
    
    /**
     * remove tree context Listener 
     * @param ker the listner to remove
     */
    public void removeTreeContextListener(TreeContextListener ker) {
        treetable.removeTreeContextListener(ker);
    }
    
    /**
     * get tree context Listeners array
     * @return the listener's table
     */
    public TreeContextListener[] getTreeContextListeners() {
        return treetable.getTreeContextListeners();
    }
    
    
    
}
