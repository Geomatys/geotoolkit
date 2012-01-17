/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.star;

import org.geotoolkit.util.ArgumentChecks;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.Node2D;
import static org.geotoolkit.index.tree.TreeUtils.*;

/**Create R*Tree.
 *
 * @author Maréchal Rémi (Geomatys)
 * @author Johann Sorel (Geomatys).
 * @version SNAPSHOT
 */
public class StarRTree extends AbstractTree2D{

    /**
     * In accordance with R*Tree properties.
     * To avoid unneccessary split permit to 
     * reinsert some elements just one time.
     */
    private boolean insertAgain = true;
    
    /**
     * Create R*Tree.
     */
    public StarRTree(int maxElements) {
        super(maxElements);
        setRoot(new Node2D(this));
    }

    /**Add an element in RTree.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: insertion is in accordance with R*Tree properties.</strong> 
     * </font></blockquote>
     * 
     * @param shape to add.
     */
    @Override
    public void insert(Shape entry) {
        if(getRoot().isEmpty()){
            getRoot().getEntries().add(entry);
        }else{
            insertNode(getRoot(), entry);
        }
    }

    /**Find shape and delete it.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Condense tree after deleting action.</strong> 
     * </font></blockquote>
     * 
     * @param shape to delete.
     */
    @Override
    public void delete(Shape entry) {
        deleteNode(getRoot(), entry);
    }

    /**Find all element(s), which intersect or whithin regionSearch.
     * 
     * @param regionSearch search area.
     * @param result list which contain result search.
     */
    @Override
    public void search(Shape regionSearch, List<Shape> result) {
        searchNode(getRoot(), regionSearch, result);
    }
    
    /**Find appropriate {@code Node} to insert {@code Shape}.
     * To define appropriate Node, R*Tree criterion are : 
     *      - require minimum area enlargement to cover shap.
     *      - or put into Node with lesser elements number in case area equals.
     * 
     * @param lN List of {@code Shape} means you must pass a list of {@code Node}.
     * @param shap {@code Shape} to add.
     * @return {@code Node} which is appropriate to contain shap.
     */
    private static Node2D chooseSubtree(final List<Node2D> lN, final Shape entry){
     
        ArgumentChecks.ensureNonNull("chooseSubtree", lN);
        ArgumentChecks.ensureNonNull("chooseSubtree", entry);
        if(lN.isEmpty()){
            throw new IllegalArgumentException("impossible to find subtree from empty list");
        }
        
        for (Node2D no : lN) {
            if (no.getBoundary().contains(entry.getBounds2D())) {
                return no;
            }
        }

        int index = 0;
        final Rectangle2D rtotal = lN.get(0).getParent().getBoundary().getBounds2D();
        double overlaps = rtotal.getWidth() * rtotal.getHeight();

        for (int i = 0; i < lN.size(); i++) {
            double overlapsTemp = 0;
            for (int j = 0; j < lN.size(); j++) {
                if (i != j) {
                    final Node2D ni =  lN.get(i);
                    final Node2D nj =  lN.get(j);
                    final List<Shape> lB = new ArrayList<Shape>(Arrays.asList(ni.getBoundary(), entry));
                    final Rectangle2D inter = getEnveloppeMin(lB).getBounds2D().createIntersection(nj.getBoundary().getBounds2D());
                    overlapsTemp += inter.getWidth() * inter.getHeight();
                }
            }
            if (overlapsTemp < overlaps) {
                index = i;
                overlaps = overlapsTemp;
            } else if (overlapsTemp == overlaps) {
                final int si = countElements(lN.get(i));
                final int sindex = countElements(lN.get(index));
                if (si < sindex) {
                    index = i;
                    overlaps = overlapsTemp;
                }
            }
        }
        return lN.get(index);
    }
    
    /**Insert new {@code Entry} in branch and organize branch if it's necessary.
     * 
     * @param shape to add.
     */
    private static void insertNode(final Node2D candidate, final Shape entry) {
        
        if(candidate.isLeaf()){
            candidate.getEntries().add(entry);
        }else{
            insertNode(chooseSubtree(candidate.getChildren(), entry), entry);
        }
        
        final StarRTree tree = (StarRTree)candidate.getTree();
        final int maxElmts = tree.getMaxElements();
        final List<Node2D> lC = candidate.getChildren(); 
        if(countElements(candidate) > maxElmts && tree.getIA()){
            tree.setIA(false);
            final List<Shape> lsh30 = getElementAtMore33PerCent(candidate);
            for(Shape ent : lsh30){
                deleteNode(candidate, ent);
            }
            for(Shape ent : lsh30){
                tree.insert(ent);
            }
            tree.setIA(true);
        }
        
        if(!candidate.isLeaf()){
            for(int i = lC.size()-1; i>=0; i--){
                if(countElements(lC.get(i)) >maxElmts){
                    final Node2D ns = lC.remove(i);
                    final List<Node2D> l = splitNode(ns);
                    final Node2D l0 = l.get(0);
                    final Node2D l1 = l.get(1);
                    l0.setParent(candidate);
                    l1.setParent(candidate);
                    lC.add(l0);
                    lC.add(l1);
                    if(l0.isLeaf()&&l1.isLeaf()&&l0.getBoundary().intersects(l1.getBoundary().getBounds2D())){
                        branchGrafting(l0, l1);
                    }
                }
            }
        }
        
        if(countElements(candidate)>candidate.getTree().getMaxElements()){
            final List<Node2D> l = splitNode(candidate);
            final Node2D l0 = l.get(0);
            final Node2D l1 = l.get(1);
            l0.setParent(candidate);
            l1.setParent(candidate);
            candidate.getEntries().clear();
            candidate.getChildren().clear();
            candidate.getChildren().addAll(l);
            if(l0.isLeaf()&&l1.isLeaf()&&l0.getBoundary().intersects(l1.getBoundary().getBounds2D())){
                branchGrafting(l0, l1);
            }
        }
    }
    
    /**Split a overflow {@code Node2D} in accordance with R-Tree properties.
     * 
     * @param candidate {@code Node2D} to Split
     * @return List<Node2D> which contains two {@code Node2D} (split of candidate).
     */
    private static List<Node2D> splitNode(final Node2D candidate){
        if(countElements(candidate) < 2){
            throw new IllegalArgumentException("not enought elements within "+candidate+" to split.");
        }
        
        final List<Node2D> lsn = new ArrayList<Node2D>(splitAxis(candidate));
        for(int i = lsn.size()-1;i>=0;i--){
            if(lsn.get(i).getChildren().size()==1){
                final Node2D nt = lsn.remove(i);
                lsn.addAll(nt.getChildren());
            }
        }
        return lsn;
    }
    
    /**Delete shape at more 30% largest of {@code this Node}.
     * 
     * @return all Entry within subNodes at more 33% largest of {@code this Node}.
     */
    private static List<Shape> getElementAtMore33PerCent(final Node2D candidate){
        final List<Shape> lsh = new ArrayList<Shape>();
        final Rectangle2D rect = candidate.getBoundary().getBounds2D();
        double rw2 = rect.getWidth();
        rw2 *= rw2;
        double rh2 = rect.getHeight();
        rh2 *= rh2;
        
        final double distPermit = Math.sqrt(rh2+rw2)/1.666666666;
        searchNode(candidate, rect, lsh);
        for(int i = lsh.size()-1;i>=0;i--){
            if(getDistanceBetweenTwoBound2D(lsh.get(i).getBounds2D(), rect) < distPermit){
                lsh.remove(i);
            }
        }
        return lsh;
    }
    
    private boolean getIA(){
        return insertAgain;
    }
    
    private void setIA(boolean insertAgain){
        this.insertAgain = insertAgain;
    }
}