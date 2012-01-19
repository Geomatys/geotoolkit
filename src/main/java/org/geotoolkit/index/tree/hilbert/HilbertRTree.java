/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.CoupleNode2D;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;

/**Create Hilbert RTree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertRTree extends AbstractTree2D{

    int hilbertOrder;
    
    /**Create Hilbert RTree.
     * 
     * @param maxElements max elements number autorized
     * @param hilbertOrder max order value.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0. 
     */
    public HilbertRTree( int maxElements, int hilbertOrder) {
        super(maxElements);
        ArgumentChecks.ensureStrictlyPositive("impossible to create Hilbert Rtree with order <= 0", hilbertOrder);
        this.hilbertOrder = hilbertOrder;
        setRoot(null);
    }
    
    /**
     * @return Max Hilbert order value. 
     */
    public int getHilbertOrder(){
        return hilbertOrder;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this)+"\n"+getRoot();
    }

    public void search(Shape regionSearch, List<Shape> result) {
        searchHilbertNode((HilbertNode2D)getRoot(), regionSearch, result);
    }
    
    public void insert(Shape entry) {
        if(getRoot() == null){
            setRoot(createHilbertNode2D(this, null, 0, null, UnmodifiableArrayList.wrap(entry)));
        }else{
            insertNode(((HilbertNode2D)getRoot()), entry);
        }
    }

    public void delete(Shape entry) {
        deleteHilbertNode(getRoot(), entry);
    }

    private static void searchHilbertNode(Node2D candidate, Shape regionSearch, List<Shape> result){
        if(regionSearch.intersects(candidate.getBoundary().getBounds2D())){
            if(candidate.isLeaf()){
                HilbertNode2D hN2D = (HilbertNode2D)candidate;
                for(Node2D n2d : (List<Node2D>)hN2D.getUserProperty("cells")){
                    if(!n2d.isEmpty()){
                        if(n2d.getBoundary().getBounds2D().intersects(regionSearch.getBounds2D())){
                            for(Shape sh : n2d.getEntries()){
                                if(sh.getBounds2D().intersects(regionSearch.getBounds2D())){
                                    result.add(sh);
                                }
                            }
                        }
                    }
                }
                
            }else{
                for(Node2D nod : candidate.getChildren()){
                    searchHilbertNode(nod, regionSearch, result);
                }
            }
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    public static void insertNode(HilbertNode2D candidate, Shape entry) {

        ArgumentChecks.ensureNonNull("impossible to insert a null entry", entry);

        if (candidate.isFull()) {
            List<Node2D> lSp = splitNode2D(candidate);
            if (lSp != null) {
                ((List<Point2D>)candidate.getUserProperty("centroids")).clear();
                ((List<Node2D>)candidate.getUserProperty("cells")).clear();
                candidate.getChildren().clear();
                candidate.setLeaf(false);
                candidate.setHilbertOrder(0);
                candidate.getChildren().addAll(lSp);
            }
        }
        if (candidate.isLeaf()) {
            if((!candidate.getBoundary().getBounds2D().contains(entry.getBounds2D()))){//risk bound vs boundary
                List<Shape> lS = new ArrayList<Shape>();
                searchHilbertNode(candidate, candidate.getBoundary(), lS);
                lS.add(entry);
                Rectangle2D enveloppe = TreeUtils.getEnveloppeMin(lS).getBounds2D();
                candidate.setBound(enveloppe);

                if (((HilbertNode2D)candidate).getHilbertOrder() > 0) {
                    TreeUtils.organize_List2DElements_From(0, null, lS);
                }
                candidate.createBasicHB(candidate.getHilbertOrder(), enveloppe);
                for (Shape sh : lS) {
                    chooseSubtree(candidate, entry).getEntries().add(sh);
                }
            }else{
                chooseSubtree(candidate, entry).getEntries().add(entry);
            }
                
        } else {
            insertNode(((HilbertNode2D)chooseSubtree(candidate, entry)), entry);
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if this {@code Node} contains lesser two subnode.
     * @throws IllegalArgumentException if this {@code Node} doesn't contains {@code Entry}.
     */
    public static List<Node2D> splitNode2D(HilbertNode2D candidate) {

        if (candidate.getChildren().size() < 2 && !candidate.isleaf) {
            throw new IllegalStateException("impossible to split node with lesser two subnode");
        }

        if (candidate.isleaf && (candidate.getHilbertOrder() < ((HilbertRTree)candidate.getTree()).getHilbertOrder())) {
            
            List<Shape> lS = new ArrayList<Shape>();
            searchHilbertNode(candidate, candidate.getBoundary().getBounds2D(), lS);
            if (lS.isEmpty()) {
                throw new IllegalStateException("impossible to increase Hilbert order of a empty Node");
            }
            candidate.createBasicHB(candidate.getHilbertOrder()+1, TreeUtils.getEnveloppeMin(lS).getBounds2D());
            TreeUtils.organize_List2DElements_From(0, null, lS);
            for (Shape sh : lS) {
                Node2D n = chooseSubtree(candidate, sh);
                n.getEntries().add(sh);
            }
            return null;
        } else {
            final List<Node2D> lS = splitAxis(candidate);
            final Node2D ls1 = lS.get(0);
            final Node2D ls2 = lS.get(1);
            ls1.setParent(candidate);
            ls2.setParent(candidate);
//            if (ls1.intersects(ls2)&&this.leaf) {
//                branchGrafting(ls1, ls2);
//            }
            return lS;
        }
    }
    
    /**Compute and define which axis to split {@code this Node}.
     * 
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    public static int defineSplitAxis(Node2D candidate){
        
        final Tree tree = candidate.getTree();
        final int val = tree.getMaxElements();
        boolean isleaf = candidate.isLeaf();
        double perimX = 0;
        double perimY = 0;
        List splitList1 = new ArrayList();
        List splitList2 = new ArrayList();
        List listElmnts;
        CoupleNode2D couplelements;
        
        if(isleaf){
             listElmnts = new ArrayList();
             searchHilbertNode(candidate, candidate.getBoundary(), listElmnts);
        }else{
             listElmnts = candidate.getChildren();
        }
        
        for(int index = 1; index<=2;index++){
            if(isleaf){
                 TreeUtils.organize_List2DElements_From(index, null, listElmnts);
            }else{
                 TreeUtils.organize_List2DElements_From(index, listElmnts, null);
            }
            
            for(int i = val;i<=listElmnts.size()-val;i++){
                for(int j = 0;j<i;j++){
                    splitList1.add(listElmnts.get(j));
                }
                for(int k =  i;k<listElmnts.size();k++){
                    splitList2.add(listElmnts.get(k));
                }

                if(candidate.isLeaf()){
                    couplelements = new CoupleNode2D(AbstractTree2D.createNode(tree, null, null, (List<Shape>)splitList1),
                                                     AbstractTree2D.createNode(tree, null, null, (List<Shape>)splitList2));
                }else{
                    couplelements = new CoupleNode2D(AbstractTree2D.createNode(tree, null, (List<Node2D>)splitList1, null),
                                                     AbstractTree2D.createNode(tree, null, (List<Node2D>)splitList2, null));
                }

                switch(index){
                    case 1 : {
                        perimX+=couplelements.getPerimeter();
                    }break;
                    
                    case 2 : {
                        perimY+=couplelements.getPerimeter();
                    }break;
                }
                
                splitList1.clear();
                splitList2.clear();
            }
        }
        
        if(perimX<=perimY){
            return 1;
        }else{
            return 2;
        }
    }
    
    /**
     * To choose axis to split :
     *      - case 1 : to choose x axis split.
     *      - case 2 : to choose y axis split.
     * 
     * @param index choose one or 2
     * @return List of two Node which is split of Node passed in parameter.
     * @throws Exception if try to split leaf with only one element.
     */
    public static List<Node2D> splitAxis(final Node2D candidate) {
        
        final Tree tree = candidate.getTree();
        final int val2 = tree.getMaxElements()/2;
        final boolean leaf = candidate.isLeaf();
        
        TreeUtils.organize_Node2DElements_From(defineSplitAxis(candidate), candidate);
        final List splitList1 = new ArrayList();
        final List splitList2 = new ArrayList();
        List listElements;
        
        if(leaf){
            listElements = new ArrayList();
            searchHilbertNode(candidate, candidate.getBoundary(), listElements);
        }else{
            listElements = candidate.getChildren();
        }
        
        if(listElements.size() <=1){
            throw new IllegalArgumentException("you can't split Leaf with only one elements or lesser");
        }
        
        if(listElements.size() == 2){
            if(leaf){
                return UnmodifiableArrayList.wrap(createHilbertNode2D(tree, null, 0, null, UnmodifiableArrayList.wrap((Shape)listElements.get(0))),
                                                  createHilbertNode2D(tree, null, 0, null, UnmodifiableArrayList.wrap((Shape)listElements.get(1))));
            }else{
                return UnmodifiableArrayList.wrap(createHilbertNode2D(tree, null, 0, UnmodifiableArrayList.wrap((Node2D)listElements.get(0)), null),
                                                  createHilbertNode2D(tree, null, 0, UnmodifiableArrayList.wrap((Node2D)listElements.get(1)), null));
            }
        }
        
        CoupleNode2D couNN;
        
         List<CoupleNode2D> lSAO = new ArrayList<CoupleNode2D>();
         List<CoupleNode2D> lSSo = new ArrayList<CoupleNode2D>();
        
        for(int i = val2;i<=listElements.size()-val2;i++){
            for(int j = 0;j<i;j++){
                splitList1.add(listElements.get(j));
            }
            for(int k =  i;k<listElements.size();k++){
                splitList2.add(listElements.get(k));
            }
            if(leaf){
                couNN = new CoupleNode2D(createHilbertNode2D(tree, null, 0, null, splitList1), createHilbertNode2D(tree, null, 0, null, splitList2));
            }else{
                couNN = new CoupleNode2D(createHilbertNode2D(tree, null, 0, splitList1, null), createHilbertNode2D(tree, null, 0, splitList2, null));
            }
            
            if(couNN.intersect()){
                lSAO.add(couNN);
            }else{
                lSSo.add(couNN);
            }
            splitList1.clear();
            splitList2.clear();
        }
        return lSSo.isEmpty() ? TreeUtils.getMinOverlapsOrPerimeter(lSAO, 0) : TreeUtils.getMinOverlapsOrPerimeter(lSSo, 1);
    }
    
    /**Find appropriate subnode to insert new entry.
     * Appropriate subnode is choosed to answer HilbertRtree criterions.
     * 
     * @param entry to insert.
     * @throws IllegalArgumentException if this subnodes list is empty.
     * @throws IllegalArgumentException if entry is null.
     * @return subnode choosen.
     */
    private static Node2D chooseSubtree(Node2D candidate, Shape entry) {
        ArgumentChecks.ensureNonNull("impossible to choose subtree with entry null", entry);
       
        if (candidate.isLeaf()) {
            if (((HilbertNode2D)candidate).getHilbertOrder() < 1) {
                return ((List<Node2D>)candidate.getUserProperty("cells")).get(0);
            }
            int index;
            index = ((HilbertNode2D)candidate).getHVOfEntry(entry);
            for (Node2D nod : ((List<Node2D>)candidate.getUserProperty("cells"))) {
                if (index <= ((Integer)(nod.getUserProperty("hilbertValue"))) && !nod.isFull()) {
                    return nod;
                }
            }
            final Rectangle2D rect = entry.getBounds2D();
            return ((List<Node2D>)candidate.getUserProperty("cells")).get(findAnotherCell(index, candidate, new Point2D.Double(rect.getCenterX(), rect.getCenterY())));
        } else {
            for (Node2D no : candidate.getChildren()) {
                if (no.getBoundary().getBounds2D().contains(entry.getBounds2D())) {
                    return no;
                }
            }

            int index = 0;
            final Rectangle2D rtotal = candidate.getBoundary().getBounds2D();
            double overlaps = rtotal.getWidth() * rtotal.getHeight();
            List<Node2D> children = candidate.getChildren();
            int childSize = children.size();
            final List<Shape> lS = new ArrayList<Shape>();
            final List<Shape> li = new ArrayList<Shape>();
            final List<Shape> lindex = new ArrayList<Shape>();
            for (int i = 0; i < childSize; i++) {
                double overlapsTemp = 0;
                for (int j = 0; j < childSize; j++) {
                    if (i != j) {
                        lS.clear();
                        final Node2D ni = children.get(i);
                        final Node2D nj = children.get(j);
                        searchHilbertNode(ni, ni.getBoundary(), lS);
                        lS.add(entry);
                        final Rectangle2D lBound = TreeUtils.getEnveloppeMin(lS).getBounds2D();
                        final Rectangle2D njBound = nj.getBoundary().getBounds2D();
                        final Rectangle2D inter = lBound.createIntersection(njBound);
                        overlapsTemp += inter.getWidth() * inter.getHeight();
                    }
                }
                if (overlapsTemp < overlaps) {
                    index = i;
                    overlaps = overlapsTemp;
                } else if (overlapsTemp == overlaps) {
                    li.clear();
                    lindex.clear();
                    final Node2D ni = children.get(i);
                    final Node2D nindex = children.get(index);
                    searchHilbertNode(ni, ni.getBoundary().getBounds2D(), li);
                    searchHilbertNode(nindex, nindex.getBoundary().getBounds2D(), lindex);
                    int si = li.size();
                    int sindex = lindex.size();
                    if (si < sindex) {
                        index = i;
                        overlaps = overlapsTemp;
                    }
                }
            }
            return children.get(index);
        }
    }
    
    /**To answer Hilbert criterions and to avoid call split method,  in some case 
     * we constrain tree leaf to choose another cell to insert Entry. 
     * 
     * @param index of subnode which is normaly choosen.
     * @param ptEntryCentroid subnode choosen centroid.
     * @throws IllegalArgumentException if method call by none leaf {@code Node}.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalStateException if no another cell is find.
     * @return int index of another subnode.
     */
    private static int findAnotherCell(int index, Node2D candidate, Point2D ptEntryCentroid) {
        ArgumentChecks.ensureNonNull("impossible to find another leaf with ptCentroid null", ptEntryCentroid);
        if (!candidate.isLeaf()) {
            throw new IllegalArgumentException("impossible to find another leaf in Node which isn't LEAF tree");
        }
        ArgumentChecks.ensureBetween("index to find another leaf is out of required limit",
                0, (int) Math.pow(2, (((HilbertNode2D)candidate).getHilbertOrder() * 2)), index);
        List<Node2D> listCells = (List<Node2D>)candidate.getUserProperty("cells");
        int siz = listCells.size();
        boolean oneTime = false;
        int indexTemp1 = index;
        int indexTemp2 = index;
        for (int i = index; i < siz; i++) {
            if (! listCells.get(i).isFull()) {
                indexTemp1 = i;
                break;
            }
            if (i == siz - 1) {
                if(oneTime){
                    throw new IllegalStateException("will be able to split");
                }
                oneTime = true;
                i = -1;
            }
        }
        return indexTemp1;
//        for (int j = index; j >= 0; j--) {
//            if (! listCells.get(j).isFull()) {
//                indexTemp2 = j;
//                break;
//            }
//        }
//
//        if (indexTemp1 != index && indexTemp2 != index) {
//            return (TreeUtils.distancebetweentwoPoint2D(TreeUtils.getCentroid(listCells.get(indexTemp1)), ptEntryCentroid) 
//                  < TreeUtils.distancebetweentwoPoint2D(TreeUtils.getCentroid(listCells.get(indexTemp2)), ptEntryCentroid))
//                    ? indexTemp1 : indexTemp2;
//        }
//        if (indexTemp1 != index) {
//            return indexTemp1;
//        }
//        if (indexTemp2 != index) {
//            return indexTemp2;
//        }
        
        
    }
    
    private static void deleteHilbertNode(Node2D candidate, Shape entry){
        if(candidate.getBoundary().intersects(entry.getBounds2D())){
            if(candidate.isLeaf()){
                HilbertNode2D hN2D = (HilbertNode2D)candidate;
                //do a barell roll !!!!!
            }else{
                for(Node2D nod : candidate.getChildren()){
                    deleteHilbertNode(nod, entry);
                }
            }
        }
    }
    
    public static Node2D createHilbertNode2D(Tree tree, Node2D parent, int hilbertOrder, List<Node2D> children, List<Shape> entries){
        return new HilbertNode2D(tree, parent, hilbertOrder, children, entries);
    }
}
