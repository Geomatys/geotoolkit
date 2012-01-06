/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.starRTree;

import com.mycompany.hilbeRTree.Bound;
import com.mycompany.utilsRTree.CoupleNode;
import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**Create tree Branch({@code Node}).
 *
 * @author Marechal Rémi (Geomatys)
 */
public class StarBranch extends Node{
    
    private final ArrayList<Node> listN = new ArrayList<Node>();
    private boolean insertAgain = true;
    
    /**Create a virgin StarBranch (tree branch).
     * 
     * @param tree pointer on RTree (StarRTree).
     */
    public StarBranch(Rtree tree) {
        this.tree = tree;
        reSize();
    }
    
    /**Create StarBranch with some Node to stock. 
     * 
     * @param tree pointer on RTree (StarRTree).
     * @param nod Node table.
     */
    public StarBranch(Rtree tree, Node ...nod) {
        this.tree = tree;
        listN.addAll(Arrays.asList(nod));
        reSize();
    }
    
    /**Create StarBranch with some Node to stock.
     * 
     * @param tree pointer on RTree (StarRTree).
     * @param lN Node list.
     */
    public StarBranch(Rtree tree, List<Node> lN) {
        if(!(lN.get(0)instanceof Node)||lN.isEmpty()){
            throw new IllegalArgumentException("list doesn't contains Node elements");
        }
        this.tree = tree;
        for(int i=0; i<lN.size();i++){
            listN.add(lN.get(i));
        }
        reSize();
    }
   
    /**To compare two {@code Node} from them boundary box x axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Node> SHAPE_COMPARATOR_X = new Comparator<Node>() {

        public int compare(Node o1, Node o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBounds2D().getMinX());
            java.lang.Double x2 = new java.lang.Double(o2.getBounds2D().getMinX());
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Node} from them boundary box y axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Node> SHAPE_COMPARATOR_Y = new Comparator<Node>() {

        public int compare(Node o1, Node o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBounds2D().getMinY());
            java.lang.Double y2 = new java.lang.Double(o2.getBounds2D().getMinY());
            return y1.compareTo(y2);
        }
    };

    /**To compare two distances between two distincts {@code Node} and a same {@code Node}. 
     * Distance is Euclidean distance between joint {@code Node} centroid and {@code Node} centroid.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: to use compare method create CoupleNode where the first param is the joint {@code Node}.</strong> 
     * </font></blockquote>
     * 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<CoupleNode> SHAPE_COMPARATOR_CENTROID = new Comparator<CoupleNode>() {

        public int compare(CoupleNode cn1, CoupleNode cn2) {
            if (!cn1.getNodeA().getBounds2D().equals(cn2.getNodeA().getBounds2D())) {
                throw new IllegalArgumentException("you will be able to compare two shapes between the same Node");
            }
            Node ref = (Node) cn1.getNodeA();
            java.lang.Double c1 = new java.lang.Double(ref.distanceBetweenTwoPoint(ref.getCentroid(), ref.getCentroid(cn1.getNodeB().getBounds2D())));
            java.lang.Double c2 = new java.lang.Double(ref.distanceBetweenTwoPoint(ref.getCentroid(), ref.getCentroid(cn2.getNodeB().getBounds2D())));
            return c1.compareTo(c2);
        }
    };
    
    
    /**Find all {@code Entry} which intersect regionSearch param. 
     * 
     * @param regionSearch area of search.
     * @param result {@code List} where is put search result.
     */
    @Override
    public void search(final Rectangle2D regionSearch, final List<Entry> result) {
        if(this.intersects(regionSearch)){
            for(Node no : listN){
                no.search(regionSearch, result);
            }
        }
    }

    /**Insert new {@code Entry} in branch and organize branch if it's necessary.
     * 
     * @param shape to add.
     */
    @Override
    public void insert(final Entry entry) {
        
        chooseSubtree(/*listN,*/ entry).insert(entry);
        
        if(listN.size()>tree.getMaxElements()&&insertAgain){
            insertAgain = false;
            
            for(Entry ent : getElementAtMore30PerCent()){
                tree.insert(ent);
            }
            reSize();
            insertAgain = true;
        }
        for(int i = 0;i<listN.size();i++){
            if(((Node)listN.get(i)).getNbElements()>tree.getMaxElements()){
                final Node n = (Node)listN.remove(i);
                final List<Node> l = n.split();
                for(Node nod : l){
                    nod.setParent(this);
                }
                listN.addAll(l);
            }
        }
        if(listN.size()>tree.getMaxElements()){
            final List<Node> l = split();
            for(Node nod : l){
                nod.setParent(this);
            }
            listN.clear();
            listN.addAll(l);
        }
        reSize();
    }
  
    /**Delete {@code Entry} (if it is find) and condense tree.
     * 
     * @param shape to delete.
     */
    @Override
    public boolean delete(final Entry entry) {
        if(!this.getBounds2D().intersects(entry.getBoundary())){
            return false;
        }
        
        for(int i=0,n=listN.size();i<n;i++){
            final Node candidate = (Node)listN.get(i);
            final boolean removed = candidate.delete(entry);
            if(removed){
                reSize();
                return true;
            }
        }
        
        return false;
    }

    /**Delete shape at more 30% largest of {@code this Node}.
     * 
     * @return all Entry within subNodes at more 30% largest of {@code this Node}.
     */
    protected List<Entry> getElementAtMore30PerCent(){
        organizeFrom(0);
        List<Entry> lsh = new ArrayList<Entry>();
        for(int i = 0;i<listN.size();i++){
            if(distanceBetweenTwoPoint(getCentroid(), listN.get(i).getCentroid())
               >Math.max(this.getWidth(), this.getHeight())*0.3){
                search(listN.get(i).getBounds2D(), lsh);
                listN.remove(i);
            }
        }
        reSize();
        return lsh;
    }
    
    /**
     * Organize all {@code Node} by differents criterion.
     * 
     * @param index : - 0 : organize all Node by nearest to furthest between them centroid and this Node centroid.
     *                - 1 : organize all Node by smallest x value to tallest.
     *                - 2 : organize all Node by smallest y value to tallest.
     */
    public void organizeFrom(int index){
       
        List<CoupleNode> lcouple = new ArrayList<CoupleNode>(listN.size());
        
        for(Node nod : listN){
            lcouple.add(new CoupleNode(this, nod));
        }
        
        switch(index){
            case 0 : 
                Collections.sort(lcouple, SHAPE_COMPARATOR_CENTROID);
                listN.clear();
                for(CoupleNode cNod : lcouple){
                    listN.add(cNod.getNodeB());
                }
                break;
            
            case 1 : 
                Collections.sort(listN, SHAPE_COMPARATOR_X);
                break;
                
            case 2 : 
                Collections.sort(listN, SHAPE_COMPARATOR_Y);
                break;
        }
    }
    
    /**Find appropriate {@code Node} to contains {@code Shape}.
     * To define appropriate Node, criterion are : 
     *      - require minimum area enlargement to cover shap.
     *      - or put into Node with lesser elements number in case area equals.
     * 
     * @param lN List of {@code Shape} means you must pass a list of {@code Node}.
     * @param shap {@code Shape} to add.
     * @return {@code Node} which is appropriate to contain shap.
     */
    public Node chooseSubtree(/*final List<Node> lNS,*/ final Entry entry){
     
        for (Node no : listN) {
                if (no.getBounds2D().contains(entry.getBoundary())) {
                    return no;
                }
            }

            int index = 0;
            Rectangle2D rtotal = this.getBounds2D();
            double overlaps = rtotal.getWidth() * rtotal.getHeight();
            
            for (int i = 0; i < listN.size(); i++) {
                double overlapsTemp = 0;
                for (int j = 0; j < listN.size(); j++) {
                    if (i != j) {
                        Node ni =  listN.get(i);
                        Node nj =  listN.get(j);
                        List<Bound> lB = new ArrayList<Bound>(Arrays.asList(ni, entry));
//                        lB.add((Bound) entry);
                        Rectangle2D lBound = getEnveloppeMin(lB);
                        Rectangle2D njBound = nj.getBounds2D();
                        Rectangle2D inter = lBound.createIntersection(njBound);
                        overlapsTemp += inter.getWidth() * inter.getHeight();
                    }
                }
                if (overlapsTemp < overlaps) {
                    index = i;
                    overlaps = overlapsTemp;
                } else if (overlapsTemp == overlaps) {
                    int si = ( listN.get(i)).getNbElements();
                    int sindex = ( listN.get(index)).getNbElements();
                    if (si < sindex) {
                        index = i;
                        overlaps = overlapsTemp;
                    }
                }
            }
            return listN.get(index);
        
        
        
        
        
//        if(!(lNS.get(0)instanceof Node)){
//            throw new IllegalArgumentException("list passed is not a Node list");
//        }
//        
//        if(lNS.size()==1){
//            return (Node)lNS.get(0);
//        }
//        
//        for(Shape sh : lNS){
//            if(sh.getBounds2D().contains(entry.getBoundary())){
//                return (Node)sh;
//            }
//        }
//        
//        final Rectangle2D sB = entry.getBoundary();
//        Node n = lNS.get(0);
//        double nbElmt = n.getNbElements();
//        double xr = Math.min(sB.getMinX(), n.getMinX());
//        double yr = Math.min(sB.getMinY(), n.getMinY());
//        double widthr = Math.abs(Math.max(sB.getMaxX()-xr, n.getMaxX())-xr);
//        double heightr = Math.abs(Math.max(n.getMaxY()-yr, sB.getMaxY())-yr);
//        double overlaps = widthr*heightr-n.getArea();
//        
//        for(Node nod : lNS){
//             xr = Math.min(sB.getMinX(), nod.getMinX());
//             yr = Math.min(sB.getMinY(), nod.getMinY());
//             widthr = Math.abs(Math.max(sB.getMaxX()-xr, nod.getMaxX())-xr);
//             heightr = Math.abs(Math.max(nod.getMaxY()-yr, sB.getMaxY())-yr);
//             
//             if(widthr*heightr-nod.getArea()<overlaps){
//                 n = nod;
//                 nbElmt = nod.getNbElements();
//                 overlaps = widthr*heightr-nod.getArea();
//             }else if(widthr*heightr-nod.getArea() == overlaps){
//                 if(nod.getNbElements()<nbElmt){
//                    n = nod;
//                    overlaps = widthr*heightr-nod.getArea();
//                    nbElmt = nod.getNbElements();
//                }else if(nod.getNbElements()==nbElmt){
//                    if( distanceBetweenTwoPoint(getCentroid(sB), nod.getCentroid())
//                       <distanceBetweenTwoPoint(getCentroid(sB), n.getCentroid())){
//                        n = nod;
//                        overlaps = widthr*heightr-nod.getArea();
//                        nbElmt = nod.getNbElements();
//                    }
//                }
//             }
//        }
//        return n; 
    }
    
    @Override
    public void paint(Graphics2D g) {
        for(Shape no : listN){
            ((Node)no).paint(g);
        }
//        g.setColor(Color.MAGENTA);
//        g.draw(this);
    }

    @Override
    public void trim() {
        
        for(int i = listN.size()-1;i>=0;i--){
            if(((Node)listN.get(i)).getNbElements()==0){
                listN.remove(i);
                
            } 
        }
        
        final List<Entry> lsh = new ArrayList<Entry>();
        
        if(listN.size()==1){
            if(listN.get(0) instanceof StarBranch){
                Node n = listN.remove(0);
                listN.addAll(((StarBranch)n).getElements());
                reSize();
            }
        }
        
        if(getNbElements()<=tree.getMaxElements()/3){
            search(getBounds2D(), lsh);
            listN.clear();
        }
        
        reSize();
        
        if(listN.isEmpty()&&parent!=null){
            parent.trim();
        }
        
        for(Entry ent : lsh){
            tree.insert(ent);
        }
        reSize();
    }

    @Override
    public int getNbElements() {
        return listN.size();
    }

    @Override
    public List<Node> split() {
        final int size = listN.size();
        if(size<=1){
            throw new IllegalStateException("you can't split Leaf with only one elements");
        }
        
        if(size==2){
                return UnmodifiableArrayList.wrap(
                        (Node)new StarBranch(tree, (Node)listN.get(0)),
                        (Node)new StarBranch(tree, (Node)listN.get(1)));
            
        }
        return splitAxis(defineSplitAxis());
    }
    
    /**
     * We can choose axis to split.
     *      - case 1 : to choose x axis split.
     *      - case 2 : to choose y axis split.
     * 
     * @param index choose one or 2
     * @return List of two Node which is split of Node passed in parameter.
     * @throws Exception if try to split leaf with only one element.
     */
    private List<Node> splitAxis(int index) {
        
        /**
         * Its to be in accordance with R*Tree properties.
         * We can, during splitting action, create new Node which contains
         * 33% in minimum of data from origin Node.
         */
        //final int val = tree.getMaxElements()/3;
        final int val2 = tree.getMaxElements()/2;
        
        if(index!=1&&index!=2){
            throw new IllegalArgumentException(" index is not conform !!!");
        }
        
        if(listN.size()<=1){
            throw new IllegalArgumentException("you can't split Leaf with only one elements or lesser");
        }
        
        if(listN.size()==2){
            return UnmodifiableArrayList.wrap((Node)new StarBranch(tree, (Node)listN.get(0)), (Node)new StarBranch(tree, (Node)listN.get(1)));
        }
        
        organizeFrom(index);
        List<Node> splitList1 = new ArrayList<Node>();
        List<Node> splitList2 = new ArrayList<Node>();
        
        CoupleNode couNN;
        
        final List<CoupleNode> lSAO = new ArrayList<CoupleNode>();
        final List<CoupleNode> lSSo = new ArrayList<CoupleNode>();
        
        for(int i = val2;i<=listN.size()-val2;i++){
            for(int j = 0;j<i;j++){
                splitList1.add(listN.get(j));
            }
            for(int k =  i;k<listN.size();k++){
                splitList2.add(listN.get(k));
            }
            
            couNN = new CoupleNode(new StarBranch(tree, splitList1), new StarBranch(tree, splitList2));
            
            if(couNN.intersect()){
                lSAO.add(couNN);
            }else{
                lSSo.add(couNN);
            }
            splitList1.clear();
            splitList2.clear();
        }
        
        return lSSo.isEmpty() ? getMinOverlapsOrPerimeter(lSAO, 0) : getMinOverlapsOrPerimeter(lSSo, 1);
    }

    /**Compute and define which axis to split {@code this Node}.
     * 
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    private int defineSplitAxis(){
        
        final int val = tree.getMaxElements()/3;
        
        double perimX = 0;
        double perimY = 0;
        
        final List<Node> splitList1 = new ArrayList<Node>();
        final List<Node> splitList2 = new ArrayList<Node>();
        
        CoupleNode coupNN;
        for(int index = 1; index<=2;index++){
            
            organizeFrom(index);
            
            for(int i = val;i<=listN.size()-val;i++){
                for(int j = 0;j<i;j++){
                    splitList1.add(listN.get(j));
                }
                for(int k =  i;k<listN.size();k++){
                    splitList2.add(listN.get(k));
                }

                coupNN = new CoupleNode(new StarBranch(tree, splitList1), new StarBranch(tree, splitList2));

                switch(index){
                    case 1 : {
                        perimX+=coupNN.getPerimeter();
                    }break;
                    
                    case 2 : {
                        perimY+=coupNN.getPerimeter();
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
    
    @Override
    public void reSize() {
        if(listN.isEmpty()){
            this.setRect(java.lang.Double.NaN, java.lang.Double.NaN, java.lang.Double.NaN, java.lang.Double.NaN);
        }else{
            Rectangle2D rectTemp = listN.get(0).getBounds2D();
            if(listN.size() == 1){
                this.setRect(rectTemp);
            }else{

                double x1 = rectTemp.getMinX();
                double y1 = rectTemp.getMinY();
                double x2 = x1;
                double y2 = y1;

                for(Node nod : listN){
                    rectTemp = nod.getBounds2D();
                    double xTemp  = rectTemp.getMinX();
                    double yTemp  = rectTemp.getMinY();
                    double x1Temp = rectTemp.getMaxX();
                    double y1Temp = rectTemp.getMaxY();

                    x1  = (xTemp < x1)  ? xTemp  : x1;
                    y1  = (yTemp < y1)  ? yTemp  : y1;
                    x2  = (x1Temp > x2) ? x1Temp : x2;
                    y2  = (y1Temp > y2) ? y1Temp : y2;
                }
                this.setRect(new Rectangle2D.Double(x1, y1, Math.abs(x2-x1), Math.abs(y2-y1)));
            }   
        }
    }

    public List<Node> getElements(){
        return listN;
    }
    
    @Override
    public double getDeadSpace() {
        double area = 0;
        for(Node nod : listN){
            area+= nod.getArea();
        }
        return this.getArea()-area;
    }
    
    @Override
    public String toString() {
        return Trees.toString("Node ", listN);
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//
//    public Rectangle2D getBoundary() {
//        return this.getBounds2D();
//    }

}
