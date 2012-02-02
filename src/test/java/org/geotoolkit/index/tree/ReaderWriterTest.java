/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.TreeReader;
import org.geotoolkit.index.tree.io.TreeWriter;
import org.geotoolkit.util.ArgumentChecks;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Create test suite to test Tree writer and reader. 
 * 
 * @author Rémi Marechal (Geomatys).
 */
public class ReaderWriterTest{
    
    Tree treeRef, treeTest;
    File fil = new File("tree.bin");
    final List<Shape> lData = new ArrayList<Shape>();
    
    public ReaderWriterTest() {
        for (int j = -120; j <= 120; j += 4) {
            for (int i = -200; i <= 200; i += 4) {
                lData.add(new Ellipse2D.Double(i, j, 1, 1));
            }
        }
    }

    private void setBasicRTree(){
        treeRef  = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        treeTest = TreeFactory.createBasicRTree2D(SplitCase.LINEAR, 4);
        insert();
    }
    
    private void setStarRTree(){
        treeRef  = TreeFactory.createStarRTree2D(4);
        treeTest = TreeFactory.createStarRTree2D(4);
        insert();
    }
    
    private void setHilbertRTree(){
        treeRef  = TreeFactory.createHilbertRTree2D(4, 2);
        treeTest = TreeFactory.createHilbertRTree2D(4, 2);
        insert();
    }
    
    private void insert() {
        Collections.shuffle(lData);
        for (Shape shape : lData) {
            treeRef.insert(shape);
        }
    }
    
    @Test
    public void testBasic() throws IOException, ClassNotFoundException{
        setBasicRTree();
        TreeWriter.write(treeRef, fil);
        TreeReader.read(treeTest, fil);
        final List<Shape> listSearchTreeRef = new ArrayList<Shape>();
        final List<Shape> listSearchTreeTest = new ArrayList<Shape>();
        treeRef.search(treeRef.getRoot().getBoundary(), listSearchTreeRef);
        treeTest.search(treeTest.getRoot().getBoundary(), listSearchTreeTest);
        assertTrue(compareList(listSearchTreeRef, listSearchTreeTest));
        assertTrue(countAllNode(treeRef) == countAllNode(treeTest));
        
        //verif nbre de node
        //verif toute les feuilles
        
        //test entre les 2 arbres
    }
    
    private int countAllNode(final Tree tree){
        int count = 0;
        countNode(tree.getRoot(), count);
        return count;
    }
    
    private void countNode(final Node2D node, int count){
        count++;
        for(Node2D nod : node.getChildren()){
            countNode(nod, count);
        }
    }
    
    private List<Node2D> getAllLeaf(final Tree tree){
        final List<Node2D> listLeaf= new ArrayList<Node2D>();
        getLeaf(tree.getRoot(), listLeaf);
        return listLeaf;
    }
    
    private void getLeaf(final Node2D node, final List<Node2D> listLeaf){
        if(node.isLeaf()){
            listLeaf.add(node);
        }
        for(Node2D nod : node.getChildren()){
            getLeaf(nod, listLeaf);
        }
    }
    
    private boolean compareLeaf(final Node2D nodeA, final Node2D nodeB){
        if(!nodeA.isLeaf() || !nodeB.isLeaf()){
            throw new IllegalArgumentException("compareLeaf : you must compare two leaf");
        }
        
        if(!nodeA.getBoundary().getBounds2D().equals(nodeB.getBoundary().getBounds2D())){
            return false;
        }
        
        final List<Shape> listA = new ArrayList<Shape>();
        final List<Shape> listB = new ArrayList<Shape>();
        
        final List<Node2D> lupA = (List<Node2D>)nodeA.getUserProperty("cells");
        final List<Node2D> lupB = (List<Node2D>)nodeB.getUserProperty("cells");
        
        if(lupA != null && !lupA.isEmpty()){
            for(Node2D nod : lupA){
                listA.addAll(nod.getEntries());
            }
        }
        
        if(lupB != null && !lupB.isEmpty()){
            for(Node2D nod : lupB){
                listB.addAll(nod.getEntries());
            }
        }
        
        listA.addAll(nodeA.getEntries());
        listB.addAll(nodeB.getEntries());
        
        return compareList(listA, listB);
    }
    
    /**
     * Compare 2 lists elements.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: return {@code true} if listA and listB are empty.</strong> 
     * </font></blockquote>
     * 
     * @param listA
     * @param listB
     * @throws IllegalArgumentException if listA or ListB is null.
     * @return true if listA contains same elements from listB.
     */
    protected boolean compareList(final List<Shape> listA, final List<Shape> listB) {
        ArgumentChecks.ensureNonNull("compareList : listA", listA);
        ArgumentChecks.ensureNonNull("compareList : listB", listB);

        if (listA.isEmpty() && listB.isEmpty()) {
            return true;
        }

        if (listA.size() != listB.size()) {
            return false;
        }

        boolean shapequals = false;
        for (Shape shs : listA) {
            for (Shape shr : listB) {
                if (shs.equals(shr)) {
                    shapequals = true;
                }
            }
            if (!shapequals) {
                return false;
            }
            shapequals = false;
        }
        return true;
    }
    
    
}
