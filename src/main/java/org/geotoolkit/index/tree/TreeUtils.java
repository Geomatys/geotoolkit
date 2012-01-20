/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**Some Utils methods.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public final class TreeUtils {

    private TreeUtils() {
    }
    
    /**To compare two {@code Node2D} from them boundary box minimum x axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Node2D> NODE2D_COMPARATOR_X = new Comparator<Node2D>() {

        public int compare(Node2D o1, Node2D o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getBounds2D().getMinX());
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getBounds2D().getMinX());
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Node} from them boundary box minimum y axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Node2D> NODE2D_COMPARATOR_Y = new Comparator<Node2D>() {

        public int compare(Node2D o1, Node2D o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getBounds2D().getMinY());
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getBounds2D().getMinY());
            return y1.compareTo(y2);
        }
    };

    /**To compare two distances between two distincts {@code Node2D} and a same {@code Node2D}. 
     * Distance is Euclidean distance between joint {@code Node2D} centroid and {@code Node2D} centroid.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: to use compare method create CoupleNode2D where the first param is the same {@code Node2D}.</strong> 
     * </font></blockquote>
     * 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<CoupleNode2D> NODE2D_COMPARATOR_CENTROID = new Comparator<CoupleNode2D>() {

        public int compare(CoupleNode2D cn1, CoupleNode2D cn2) {
            if (!cn1.getObject1().getBoundary().equals(cn2.getObject1().getBoundary())) {
                throw new IllegalArgumentException("you will be able to compare two shapes between the same Node");
            }
            java.lang.Double c1 = cn1.getDistance();
            java.lang.Double c2 = cn2.getDistance();
            return c1.compareTo(c2);
        }
    };
    
    /**To compare two {@code Shape} from them boundary box minimum x axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Shape> SHAPE_COMPARATOR_X = new Comparator<Shape>() {

        public int compare(Shape o1, Shape o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBounds2D().getMinX());
            java.lang.Double x2 = new java.lang.Double(o2.getBounds2D().getMinX());
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Shape} from them boundary box minimum y axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Shape> SHAPE_COMPARATOR_Y = new Comparator<Shape>() {

        public int compare(Shape o1, Shape o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBounds2D().getMinY());
            java.lang.Double y2 = new java.lang.Double(o2.getBounds2D().getMinY());
            return y1.compareTo(y2);
        }
    };

    /**To compare two distances between two distincts {@code Shape} and a same {@code Shape}. 
     * Distance is Euclidean distance between joint {@code Shape} centroid and {@code Shape} centroid.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: to use compare method create CoupleNode where the first param is the joint {@code Shape}.</strong> 
     * </font></blockquote>
     * 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<CoupleShape> SHAPE_COMPARATOR_CENTROID = new Comparator<CoupleShape>() {

        public int compare(CoupleShape cs1, CoupleShape cs2) {
            if (!cs1.getObject1().equals(cs2.getObject1())) {
                throw new IllegalArgumentException("you will be able to compare two shapes between the same Node");
            }
            java.lang.Double c1 = cs1.getDistance();
            java.lang.Double c2 = cs2.getDistance();
            return c1.compareTo(c2);
        }
    };
    
    /**
     * @param node to denominate elements number.
     * @return elements number within node.
     */
    public static int countElements(Node2D node){
        return node.getChildren().size()+node.getEntries().size();
    }
    
    /**Find all {@code Shape} which intersect regionSearch param. 
     * 
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    public static void searchNode(final Node2D candidate, final Shape regionSearch, final List<Shape> result) {
        Shape bound = candidate.getBoundary();
        if(bound !=null){
            if(bound.intersects(regionSearch.getBounds2D())){
                if(candidate.isLeaf()){
                    for(Shape ent : candidate.getEntries()){
                        if(ent.intersects(regionSearch.getBounds2D())){
                            result.add(ent);
                        }
                    }
                }else{
                    for(Node2D nod : candidate.getChildren()){
                        searchNode(nod, regionSearch, result);
                    }   
                }
            }   
        }
    }
    
    /**Distance returned is compute between boundary gravity center of each {@code Node2D}.
     * 
     * @param n1 first Node.
     * @param n2 second Node.
     * @return Euclidean distance between two Node.
     */
    public static double getDistanceBetweenTwoBound2D(final Rectangle2D rn1, final Rectangle2D rn2){
        ArgumentChecks.ensureNonNull("getDistanceBetweenTwoBound2D : rn1", rn1);
        ArgumentChecks.ensureNonNull("getDistanceBetweenTwoBound2D : rn2", rn2);
        final double x1 = (rn1.getMinX()+rn1.getMaxX())/2;
        final double y1 = (rn1.getMinY()+rn1.getMaxY())/2;
        final double x2 = (rn2.getMinX()+rn2.getMaxX())/2;
        final double y2 = (rn2.getMinY()+rn2.getMaxY())/2;
        return Math.hypot(Math.abs(x1-x2), Math.abs(y1-y2));
    }
    
    /**Compute general boundary of all shapes passed in param.
     * 
     * @param lS Shape List.
     * @return Shape which is general boundary.
     */
    public static Shape getEnveloppeMin(final List<Shape> lS){
        ArgumentChecks.ensureNonNull("getEnveloppeMin : lS", lS);
        if(lS.isEmpty()){
            throw new IllegalArgumentException("impossible to get Enveloppe : empty list");
        }
        final Rectangle2D envlop = new Rectangle2D.Double();
        for(Shape sh : lS){
            if(envlop.isEmpty()){
                envlop.setRect(sh.getBounds2D());
            }else{
                final Rectangle2D rt = sh.getBounds2D();
                envlop.setFrameFromDiagonal(Math.min(envlop.getMinX(), rt.getMinX()), 
                                            Math.min(envlop.getMinY(), rt.getMinY()), 
                                            Math.max(envlop.getMaxX(), rt.getMaxX()), 
                                            Math.max(envlop.getMaxY(), rt.getMaxY()));
            }
        }
        return envlop;
    }
    
    /**Compute empty area of {@code Node2D}.
     * 
     * @return this area subtract some of area elements.
     */
    public static double getDeadSpace(final Node2D node){
        ArgumentChecks.ensureNonNull("getDeadSpace : node", node);
        double areaElement = 0;
        final Rectangle2D rc = new Rectangle2D.Double();
        for(Node2D nod : node.getChildren()){
            if(rc.isEmpty()){
                rc.setRect(nod.getBoundary().getBounds2D());
            }else{
                rc.add(nod.getBoundary().getBounds2D());
            }
            areaElement += rc.getWidth() * rc.getHeight();
        }
        for(Shape e2d : node.getEntries()){
            if(rc.isEmpty()){
                rc.setRect(e2d.getBounds2D());
            }else{
                rc.add(e2d.getBounds2D());
            }
            areaElement += rc.getWidth() * rc.getHeight();
        }
        final Rectangle2D areaCandidate = node.getBoundary().getBounds2D();
        return (areaCandidate.getWidth()*areaCandidate.getHeight() - areaElement);
    }
    
    /**
     * @param node
     * @return return center of {@code Node2D}.
     */
    public static Point2D getCentroid(Node2D node){
        ArgumentChecks.ensureNonNull("getCentroid : node", node);
        final Rectangle2D rnod = node.getBoundary().getBounds2D();
        return new Point2D.Double(rnod.getCenterX(), rnod.getCenterY());
    }
    
    /**
     * Organize all elements from {@code List<Node2D>} and {@code List<Shape>} by differents criterion.
     * 
     * @param index : - 0 : organize all List by nearest to furthest between them centroid and this Node centroid.
     *                - 1 : organize all List by smallest x value to tallest.
     *                - 2 : organize all List by smallest y value to tallest.
     * @throws IllegalArgumentException if listNode and listEntries are null.
     */
    public static void organize_List2DElements_From(int index, final List<Node2D> listNode, final List<Shape> listEntries) {
        if(listNode==null&&listEntries==null){
            throw new IllegalArgumentException("impossible to organize empty lists");
        }
        switch (index) {
            case 1:
                if(listNode!=null){
                    Collections.sort(listNode, NODE2D_COMPARATOR_X);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, SHAPE_COMPARATOR_X);
                }
                break;

            case 2:
                if(listNode!=null){
                    Collections.sort(listNode, NODE2D_COMPARATOR_Y);
                }
                if(listEntries!=null){
                    Collections.sort(listEntries, SHAPE_COMPARATOR_Y);
                }
                break;
        }
    }
    
    /**
     * Organize all children from {@code Node2D} by differents criterion.
     * 
     * @param index : - 0 : organize all Node2D children by nearest to furthest between them centroid and this Node centroid.
     *                - 1 : organize all Node2D children by smallest x value to tallest.
     *                - 2 : organize all Node2D children by smallest y value to tallest.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalArgumentException if candidate is null.
     */
    public static void organize_Node2DElements_From(int index, final Node2D candidate) {

        ArgumentChecks.ensureBetween("organizeNode2DElementsFrom : index", 0, 2, index);
        ArgumentChecks.ensureNonNull("organizeNode2DElementsFrom : candidate", candidate);

        final List<Node2D> lN2D = candidate.getChildren();
        final List<Shape> lS = candidate.getEntries();
        
        switch (index) {
//            case 0:
//                final List<CoupleNode2D> lCN = new ArrayList<CoupleNode2D>();
//                for(Node2D n2d : lN2D){
//                    lCN.add(new CoupleNode2D(candidate, n2d));
//                }
//                Collections.sort(lCN, NODE2D_COMPARATOR_CENTROID);
//                lN2D.clear();
//                for(CoupleNode2D cn2d : lCN){
//                    lN2D.add(cn2d.getObject2());
//                }
//                
//                final List<CoupleShape> lCS = new ArrayList<CoupleShape>();
//                for(Shape shap : lS){
//                    lCS.add(new CoupleShape(candidate.getBoundary(), shap));
//                }
//                Collections.sort(lCS, SHAPE_COMPARATOR_CENTROID);
//                lS.clear();
//                for(CoupleShape cs : lCS){
//                    lS.add(cs.getObject2());
//                }

            case 1:
                Collections.sort(lN2D, NODE2D_COMPARATOR_X);
                Collections.sort(lS, SHAPE_COMPARATOR_X);
                break;

            case 2:
                Collections.sort(lN2D, NODE2D_COMPARATOR_Y);
                Collections.sort(lS, SHAPE_COMPARATOR_Y);
                break;
        }
    }
    
    /**Find in lN, couple of {@code Node2D} with smallest overlapping or perimeter.
     * Choose indice : - case 0 : find couple with smallest overlapping.
     *                 - case 1 : find couple with smallest perimeter.
     * 
     * @param lN list of CoupleShape.
     * @see CoupleShape.
     * @param indice to select criterion.
     * @throws IllegalArgumentException if lCN is empty or null.
     * @throws IllegalArgumentException if indice is out of required limits.
     * @return {@code List<Node2D>} with size = 2, which is selectionned couple of {@code Node}.
     */
    public static List<Node2D> getMinOverlapsOrPerimeter(final List<CoupleNode2D> lCN, int indice) {

        ArgumentChecks.ensureBetween("indice out of permit limits", 0, 1, indice);
        ArgumentChecks.ensureNonNull("CoupleNode null", lCN);
        if (lCN.isEmpty()) {
            throw new IllegalArgumentException("CoupleNode list is empty");
        }
        if (lCN.size() == 1) {
            return UnmodifiableArrayList.wrap( lCN.get(0).getObject1(), lCN.get(0).getObject2());
        }

        double valueRef;
        int index = 0;

        switch (indice) {
            case 0: {
                valueRef = lCN.get(0).getOverlaps();
                for (int i = 1; i < lCN.size(); i++) {
                    double valueTemp = lCN.get(i).getOverlaps();
                    if (valueTemp < valueRef) {
                        valueRef = valueTemp;
                        index = i;
                    }
                }
            }
            break;
            case 1: {
                valueRef = lCN.get(0).getPerimeter();
                for (int i = 1, n = lCN.size(); i < n; i++) {
                    double valueTemp = lCN.get(i).getPerimeter();
                    if (valueTemp < valueRef) {
                        valueRef = valueTemp;
                        index = i;
                    }
                }
            }
            break;
        }
        return UnmodifiableArrayList.wrap( lCN.get(index).getObject1(), lCN.get(index).getObject2());
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
        
        if(countElements(candidate) <=1){
            throw new IllegalArgumentException("you can't split Leaf with only one elements or lesser");
        }
        
        if(countElements(candidate) == 2){
            if(leaf){
                return UnmodifiableArrayList.wrap(AbstractTree2D.createNode(tree, null, null, UnmodifiableArrayList.wrap(candidate.getEntries().get(0))),
                                                  AbstractTree2D.createNode(tree, null, null, UnmodifiableArrayList.wrap(candidate.getEntries().get(1))));
            }else{
                return UnmodifiableArrayList.wrap(AbstractTree2D.createNode(tree, null, UnmodifiableArrayList.wrap(candidate.getChildren().get(0)), null),
                                                  AbstractTree2D.createNode(tree, null, UnmodifiableArrayList.wrap(candidate.getChildren().get(1)), null));
            }
        }
        
        organize_Node2DElements_From(defineSplitAxis(candidate), candidate);
        final List splitList1 = new ArrayList();
        final List splitList2 = new ArrayList();
        List listElements;
        
        if(leaf){
            listElements = candidate.getEntries();
        }else{
            listElements = candidate.getChildren();
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
                couNN = new CoupleNode2D(AbstractTree2D.createNode(tree, null, null, splitList1), AbstractTree2D.createNode(tree, null, null, splitList2));
            }else{
                couNN = new CoupleNode2D(AbstractTree2D.createNode(tree, null, splitList1, null), AbstractTree2D.createNode(tree, null, splitList2, null));
            }
            
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
    
    /**Compute euclidean distance between two {@code Point2D}.
     * 
     * @param pointA
     * @param pointB
     * @return distance between pointA and pointB.
     */
    public static double distancebetweentwoPoint2D(final Point2D pointA, final Point2D pointB){
        final double x = Math.abs(pointB.getX()-pointA.getX());
        final double y = Math.abs(pointB.getY()-pointA.getY());
        return Math.sqrt(x*x+y*y);
    }
    
    /**Compute and define which axis to split {@code this Node}.
     * 
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    public static int defineSplitAxis(Node2D candidate){
        
        final Tree tree = candidate.getTree();
        final int val = tree.getMaxElements();
        
        double perimX = 0;
        double perimY = 0;
        List splitList1 = new ArrayList();
        List splitList2 = new ArrayList();
        List listElmnts;
        CoupleNode2D couplelements;
        
        if(candidate.isLeaf()){
             listElmnts = candidate.getEntries();
        }else{
             listElmnts = candidate.getChildren();
        }
        
        for(int index = 1; index<=2;index++){
            
            organize_Node2DElements_From(index, candidate);
            
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
    
    /**Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     * 
     * @param n1 Node2D
     * @param n2 Node2D
     * @throws IllegalArgumentException if n1 or n2 are null.
     * @throws IllegalArgumentException if n1 and n2 have different "parent".
     * @throws IllegalArgumentException if n1 or n2 are not tree leaf.
     * @throws IllegalArgumentException if n1 or n2, and their subnodes, don't contains some {@code Entry}.
     */
    public static void branchGrafting(final Node2D n1, final Node2D n2) {
        
        ArgumentChecks.ensureNonNull("Node n1 null", n1);
        ArgumentChecks.ensureNonNull("Node n2 null", n2);
        
        if(!(n1.isLeaf()&&n2.isLeaf())){
            throw new IllegalArgumentException("you won't be exchange data with not leaf nodes.");
        }

        final Node2D theFather = n1.getParent();

        final int hp1 = theFather.hashCode();
        final int hp2 = n2.getParent().hashCode();

        if (hp1 != hp2) {
            throw new IllegalArgumentException("you won't be exchange data with nodes which don't own same parent");
        }
        
        Tree tree = theFather.getTree();
        
        final Rectangle2D boundN1 = n1.getBoundary().getBounds2D();
        final Rectangle2D boundN2 = n2.getBoundary().getBounds2D();

        if (boundN1.intersects(boundN2)) {

            final List<Shape> lEOverlaps = new ArrayList<Shape>();
            searchNode(theFather, boundN1.createIntersection(boundN2), lEOverlaps);
            if (!lEOverlaps.isEmpty()) {
                for(Shape sh : lEOverlaps){
//                    deleteNode(theFather, sh);//tu travail sur des feuilles vois si tu peux pas faire autrement ke passer par delete!!!!
                    n1.getEntries().remove(sh);
                    n2.getEntries().remove(sh);
                }
                int indice;
                if(n1.getEntries().isEmpty()||n2.getEntries().isEmpty()){
                    indice = -1;
                }else{
                    indice = findAppropriateSplit(n1.getEntries(), n2.getEntries(), lEOverlaps);
                }
                if (!boundN1.contains(boundN2) && !boundN2.contains(boundN1) && indice != -1) {
                    
                    for (int i = 0; i < indice; i++) {
                        n1.getEntries().add(lEOverlaps.get(i));
                    }

                    for (int i = indice, s = lEOverlaps.size(); i < s; i++) {
                        n2.getEntries().add(lEOverlaps.get(i));
                    }
                } else {

                    List<Shape> lS = new ArrayList<Shape>(n1.getEntries());
                    lS.addAll(n2.getEntries());
                    lS.addAll(lEOverlaps);

                    final int s = lS.size();
                    final int smin = s / 3;
                    final Node2D canditemp = AbstractTree2D.createNode(tree, null, null, lS);
                    final Rectangle2D rectGlob = canditemp.getBoundary().getBounds2D();
                    final double rGW = rectGlob.getWidth();
                    final double rGH = rectGlob.getHeight();
                    final int index = (rGW < rGH) ? 2 : 1;
                    organize_Node2DElements_From(index, canditemp);
                    lS = canditemp.getEntries();
                    int indexing = s / 2;
                    double overlapsTemp = rGW * rGH;
                    final Rectangle2D rTemp1 = new Rectangle2D.Double();
                    final Rectangle2D rTemp2 = new Rectangle2D.Double();
                    final List<Shape> lTn1 = new ArrayList<Shape>();
                    final List<Shape> lTn2 = new ArrayList<Shape>();

                    for (int i = smin; i < s - smin; i++) {
                        final List<Shape> testn1 = new ArrayList<Shape>();
                        final List<Shape> testn2 = new ArrayList<Shape>();
                        for (int j = 0; j < i; j++) {
                            testn1.add(lS.get(j));
                        }
                        for (int k = i; k < s; k++) {
                            testn2.add(lS.get(k));
                        }
                        final Rectangle2D r1 = getEnveloppeMin(testn1).getBounds2D();
                        final Rectangle2D r2 = getEnveloppeMin(testn2).getBounds2D();
                        final Rectangle2D overlaps = r1.createIntersection(r2);
                        final double over = overlaps.getWidth() * overlaps.getHeight();
                        int indexingTemp = Math.abs(s / 2 - i);
                        if (over <= overlapsTemp && indexingTemp <= indexing) {
                            lTn1.clear();
                            lTn2.clear();
                            indexing = indexingTemp;
                            overlapsTemp = over;
                            rTemp1.setRect(r1);
                            rTemp2.setRect(r2);
                            lTn1.addAll(testn1);
                            lTn2.addAll(testn2);
                        }
                    }
                    final List<Shape> ln1 = n1.getEntries();
                    final List<Shape> ln2 = n2.getEntries();
                    ln1.clear();
                    ln2.clear();
                    for (Shape ent : lTn1) {
                        ln1.add(ent);
                    }
                    for (Shape ent : lTn2) {
                        ln2.add(ent);
                    }
                }
            }
        }
    }
    
    /**Add progressively each {@code listOverlaps} element(s) in {@code listnode1} or {@code listnode2}. 
     * Each list symbolize a surface representing by them elements.
     * The aim is to distribute one by one in order each data of {@code listOverlaps} in {@code listNode1} and 
     * {@code listNode2} to avoid (if it's possible) overlaps between {@code listNode1} surface and {@code listNode2} surface.
     * 
     * @see HilbertLeaf#exchangeData(com.mycompany.utilsRTree.Node, com.mycompany.utilsRTree.Node) 
     * @param listShap1 list of Node elements.
     * @param listShap2 list of Node elements.
     * @param listOverlaps list of elements on the overlaps surface.
     * @throws IllegalArgumentException if listNode1 or listNode2 or listOverlaps are null.
     * @throws IllegalArgumentException if one of these list is empty.
     * @return index of overlaps list if a solution is find, else -1
     */
    private static int findAppropriateSplit(List<Shape> listShap1, List<Shape> listShap2, List<Shape> listOverlaps) {
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listNode1 is null", listShap1);
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listNode2 is null", listShap2);
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listOverlaps is null", listOverlaps);
        
        if (listOverlaps.isEmpty()||listShap1.isEmpty()||listShap2.isEmpty()) {
            throw new IllegalArgumentException("impossible to find solution with  empty list of element");
        }

        for (int i = 0, s = listOverlaps.size(); i < s; i++) {
            final List<Shape> testn1 = new ArrayList<Shape>(listShap1);
            final List<Shape> testn2 = new ArrayList<Shape>(listShap2);
            for (int choix = 0; choix < 2; choix++) {
                for (int j = 0; j < i + choix; j++) {
                    testn1.add(listOverlaps.get(j));
                }
                for (int k = i + choix; k < s; k++) {
                    testn2.add(listOverlaps.get(k));
                }

                final Rectangle2D overlaps = getEnveloppeMin(testn1).getBounds2D().createIntersection(getEnveloppeMin(testn2).getBounds2D());

                if (overlaps.getWidth() * overlaps.getHeight() <= 0) {
                    return i + choix;
                }
            }
        }
        return -1;
    }
    
    /**Travel {@code Tree}, find {@code Entry} if it exist and delete it.
     * Moreover {@code Tree} is condensate after a deletion to stay conform about R-Tree properties.
     * 
     * @param candidate {@code Node2D}  where to search.
     * @param result List<Entry> to add finded {@code Entry}.
     */
    public static boolean deleteNode(final Node2D candidate, final Shape entry) {
        ArgumentChecks.ensureNonNull("DeleteNode : Node2D candidate", candidate);
        ArgumentChecks.ensureNonNull("DeleteNode : Shape entry", entry);
        
        if(candidate.getBoundary() == null){
            System.out.println("putain d'arbre ");
        }
        if(candidate.getBoundary().getBounds2D().intersects(entry.getBounds2D())){
            if(candidate.isLeaf()){
                final boolean removed = candidate.getEntries().remove(entry);
                if(removed){
                    trim(candidate);
                    return true;
                }
            }else{
                for(Node2D no : candidate.getChildren()){
                    final boolean removed = deleteNode(no, entry);
                    if(removed){
                        return true;
                    }
                }
            }
        }
        
        
        return false;
    }
    
    /**Methode which permit to condense R-Tree.
     * Condense made begin by leaf and travel up to treetrunk.
     * @param candidate {@code Node2D} to begin condense.
     */
    public static void trim(final Node2D candidate) {
        
        List<Node2D> children = candidate.getChildren();
        final Tree tree = candidate.getTree();
        List<Shape> reinsertList = new ArrayList<Shape>();
        for(int i = children.size()-1;i>=0;i--){
            final Node2D child = children.get(i);
            if(child.isEmpty()){
                children.remove(i);
            }else if(child.getChildren().size() ==1){
                children.remove(i);
                for(Node2D n2d : child.getChildren()){
                    n2d.setParent(candidate);
                }
                children.addAll(child.getChildren());
            }else if(child.isLeaf()&&child.getEntries().size()<=tree.getMaxElements()/3){
                reinsertList.addAll(child.getEntries());
                children.remove(i);
            }
        }
        
//        for(int i = children.size()-1;i>=0;i--){
//            final Node2D child = children.get(i);
//            if(child.isEmpty()){
//                children.remove(i);
//            }else if(countElements(child)==1){
//                if(!child.isLeaf()){
//                    children.remove(i);
//                    for(Node2D chichild : child.getChildren()){
//                        chichild.setParent(candidate);
//                    }
//                    children.addAll(child.getChildren());
//                }
//            } 
//        }
//        children = candidate.getChildren();
//        final List<Shape> lsh = new ArrayList<Shape>();
//        for(int i = children.size()-1;i>=0;i--){
//            final Node2D child = children.get(i);
//            if(countElements(child) <=tree.getMaxElements()/3){
//                tree.search(child.getBoundary(), lsh);
//                children.remove(i);
//            }
//        }
            
        
        if(candidate.getParent()!=null){
            trim(candidate.getParent());
        }
        
        for(Shape ent : reinsertList){
            tree.insert(ent);
        }
    }
    
}
