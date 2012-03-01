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
package org.geotoolkit.index.tree.star;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.getMedian;
import org.geotoolkit.index.tree.*;
import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.nodefactory.NodeFactory;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**Create R*Tree in Euclidean space.
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Johann Sorel  (Geomatys).
 */
public class StarRTree extends DefaultAbstractTree {

    /**
     * In accordance with R*Tree properties.
     * To avoid unnecessary split permit to 
     * reinsert some elements just one time.
     */
    boolean insertAgain = true;
    
    /**Create R*Tree.
     * 
     * @param maxElements max elements number permit by cells. 
     * @param crs  
     */
    public StarRTree(int nbMaxElement, CoordinateReferenceSystem crs, Calculator calculator, NodeFactory nodefactory) {
        super(nbMaxElement, crs, calculator, nodefactory);
        setRoot(null);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void search(final Envelope regionSearch, final List<Envelope> result) throws MismatchedReferenceSystemException{
        ArgumentChecks.ensureNonNull("search : region search", regionSearch);
        ArgumentChecks.ensureNonNull("search : result", result);
        if(!CRS.equalsIgnoreMetadata(crs, regionSearch.getCoordinateReferenceSystem())){
            throw new MismatchedReferenceSystemException();
        }
        final Node root = this.getRoot();
        if(root != null){
            nodeSearch(root, regionSearch, result);
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(final Envelope entry) throws MismatchedReferenceSystemException{
        ArgumentChecks.ensureNonNull("insert : entry", entry);
        if(!CRS.equalsIgnoreMetadata(crs, entry.getCoordinateReferenceSystem())){
            throw new MismatchedReferenceSystemException();
        }
        
        final Node root = getRoot();
        final int dim = entry.getDimension();
        final double[] coords = new double[2 * dim];
        System.arraycopy(entry.getLowerCorner().getCoordinate(), 0, coords, 0, dim);
        System.arraycopy(entry.getUpperCorner().getCoordinate(), 0, coords, dim, dim);
        if (root == null || root.isEmpty()) {
            setRoot(createNode(this, null, null, UnmodifiableArrayList.wrap(entry), coords));
        } else {
            nodeInsert(root, entry);
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void delete(final Envelope entry) throws MismatchedReferenceSystemException{
        ArgumentChecks.ensureNonNull("delete : entry", entry);
        if(!CRS.equalsIgnoreMetadata(crs, entry.getCoordinateReferenceSystem())){
            throw new MismatchedReferenceSystemException();
        }
        final Node root = getRoot();
        if (root != null) {
            deleteNode(root, entry);
        }
    }

    /**Find all {@code Envelope} which intersect regionSearch parameter in {@code Node}. 
     * 
     * @param candidate current Node
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    private static void nodeSearch(final Node candidate, final Envelope regionSearch, final List<Envelope> resultList){
        final Envelope bound = candidate.getBoundary();
        if(bound != null){
            if(regionSearch == null){
                if(candidate.isLeaf()){
                    resultList.addAll(candidate.getEntries());
                }else{
                    for(Node nod : candidate.getChildren()){
                        nodeSearch(nod, null, resultList);
                    }
                }
            }else{
                final GeneralEnvelope rS = new GeneralEnvelope(regionSearch);
                if(rS.contains(bound, true)){
                    nodeSearch(candidate, null, resultList);
                }else if(rS.intersects(bound, true)){
                    if(candidate.isLeaf()){
                        for(Envelope gn : candidate.getEntries()){
                            if(rS.intersects(gn, true)){
                                resultList.add(gn);
                            }
                        }
                    }else{
                        for(Node child : candidate.getChildren()){
                            nodeSearch(child, regionSearch, resultList);
                        }
                    }
                }
            }
        }
    }
    
    /**Insert new {@code Entry} in branch and re-organize {@code Node} if it's necessary.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: insertion is in accordance with R*Tree properties.</strong> 
     * </font></blockquote>
     * 
     * @param candidate where to insert entry.
     * @param entry to add.
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @throws IllegalArgumentException if {@code Envelope} entry is null.
     */
    private static void nodeInsert(final Node candidate, final Envelope entry) {
        if(candidate.isLeaf()){
            candidate.getEntries().add(entry);
        }else{
            nodeInsert(chooseSubTree(candidate, entry), entry);
        }
        
        final StarRTree tree = (StarRTree) candidate.getTree();
        final int maxElmts = tree.getMaxElements();
        if (DefaultTreeUtils.countElements(candidate) > maxElmts && tree.getIA()) {
            tree.setIA(false);
            final List<Envelope> lsh30 = getElementAtMore33PerCent(candidate);
            for (Envelope ent : lsh30) {
                deleteNode(candidate, ent);
            }
            for (Envelope ent : lsh30) {
                tree.insert(ent);
            }
            tree.setIA(true);
        }
        
        if(!candidate.isLeaf()){
            final List<Node> children = candidate.getChildren();
            int size = children.size();
            if(children.get(0).isLeaf()){
                for(int i = 0; i<size-1; i++){
                    for(int j = i+1; j<size; j++){
                        final Node nodeA = children.get(i);
                        final Node nodeB = children.get(j);
                        if(new GeneralEnvelope(nodeA.getBoundary()).intersects(nodeB.getBoundary(), false) && nodeA.isLeaf() && nodeB.isLeaf()){
                            branchGrafting(nodeA, nodeB);
                        }
                    }
                }
            }
            
            for(int i =0; i<size; i++){
                if (DefaultTreeUtils.countElements(children.get(i)) > candidate.getTree().getMaxElements()) {
                    final Node child = children.remove(i);
                    final List<Node> l = nodeSplit(child);
                    final Node l0 = l.get(0);
                    final Node l1 = l.get(1);
                    l0.setParent(candidate);
                    l1.setParent(candidate);
                    children.addAll(l);
                }
            }
        }
        
        if (candidate.getParent() == null) {
            if (DefaultTreeUtils.countElements(candidate) > candidate.getTree().getMaxElements()) {
                List<Node> l = nodeSplit(candidate);
                final Node l0 = l.get(0);
                final Node l1 = l.get(1);
                l0.setParent(candidate);
                l1.setParent(candidate);
                candidate.getEntries().clear();
                candidate.getChildren().clear();
                candidate.getChildren().addAll(l);
            }
        }
    }
    
    /**Find appropriate {@code Node} to insert {@code Envelope} entry.
     * <blockquote><font size=-1>
     * <strong>To define appropriate Node, R*Tree criterion are : 
     *      - require minimum area enlargement to cover {@code Envelope} entry.
     *      - or put into Node with lesser elements number in case area equals.
     * </strong> 
     * </font></blockquote>
     * 
     * @param parent Find in its children {@code Node}.
     * @param entry {@code Envelope} to add.
     * @throws IllegalArgumentException if {@code Node} listSubnode is empty.
     * @return {@code Node} which will be appropriate to contain entry.
     */
    private static Node chooseSubTree(final Node parent, final Envelope entry) {
        
        final List<Node> childrenList = parent.getChildren();
        
        if (childrenList.isEmpty()) {
            throw new IllegalArgumentException("impossible to find subtree from empty list");
        }
        
        final int size = childrenList.size();
        if (size == 1) {
            return childrenList.get(0);
        }
        final Calculator calc = parent.getTree().getCalculator();
        if(childrenList.get(0).isLeaf()){
            final List<Node> listOverZero = new ArrayList<Node>();
            double overlapsRef = -1;
            int index = -1;
            double overlapsTemp = 0;
            for(int i = 0; i < size; i++){
                final GeneralEnvelope gnTemp = new GeneralEnvelope(childrenList.get(i).getBoundary());
                gnTemp.add(entry);
                for(int j = 0; j < size; j++){
                    if(i != j){
                        final Envelope gET = childrenList.get(j).getBoundary();
                        overlapsTemp += calc.getOverlaps(gnTemp, gET);
                    }
                }
                if(overlapsTemp == 0){
                    listOverZero.add(childrenList.get(i));
                }else{
                    if((overlapsTemp<overlapsRef) || overlapsRef==-1){
                        overlapsRef = overlapsTemp;
                        index = i;
                    }else if(overlapsTemp == overlapsRef){
                        if(DefaultTreeUtils.countElements(childrenList.get(i))<DefaultTreeUtils.countElements(childrenList.get(index))){
                            overlapsRef = overlapsTemp;
                            index = i;
                        }
                    }
                }
                overlapsTemp = 0;
            }
            if(!listOverZero.isEmpty()){
                double areaRef = -1;
                int indexZero = -1;
                double areaTemp;
                for(int i = 0, s = listOverZero.size(); i<s;i++){
                    final GeneralEnvelope gE = new GeneralEnvelope(listOverZero.get(i).getBoundary());
                    gE.add(entry);
                    areaTemp = calc.getEdge(gE);
                    if(areaTemp<areaRef || areaRef == -1){
                        areaRef = areaTemp;
                        indexZero = i;
                    }
                }
                return listOverZero.get(indexZero);
            }
            if(index == -1){
                throw new IllegalStateException("chooseSubTree : no subLeaf find");
            }
            return childrenList.get(index);
        }
        
        for (Node no : childrenList) {
            if (new GeneralEnvelope(no.getBoundary()).contains(entry, true)) {
                return no;
            }
        }
        
        double enlargRef = -1;
        int indexEnlarg = -1;
        for(int i = 0, s = childrenList.size(); i<s;i++){
            final Node n3d = childrenList.get(i);
            final Envelope gEN = n3d.getBoundary();
            final GeneralEnvelope GE = new GeneralEnvelope(gEN);
            GE.add(entry);
            double enlargTemp = calc.getEnlargement(gEN, GE);
            if(enlargTemp<enlargRef || enlargRef == -1){
                enlargRef = enlargTemp;
                indexEnlarg = i;
            }
        }
        return childrenList.get(indexEnlarg); 
    }
    
    /**Compute and define how to split {@code Node} candidate.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: To choose which {@code Node} couple, split algorithm sorts the entries (for tree leaf), or {@code Node} (for tree branch) 
     *               in accordance to their lower or upper boundaries on the selected dimension (see defineSplitAxis method) and examines all possible divisions.
     *               Two {@code Node} resulting, is the final division which has the minimum overlaps between them.</strong> 
     * </font></blockquote>
     * 
     * @return Two appropriate {@code Node} in List in accordance with R*Tree split properties.
     */
    private static List<Node> nodeSplit(final Node candidate){
        
        final int splitIndex = defineSplitAxis(candidate);
        final boolean isLeaf = candidate.isLeaf();
        final Tree tree = candidate.getTree();
        final Calculator calc = tree.getCalculator();
        final NodeFactory nodeFact = tree.getNodeFactory();
        List eltList;
        if(isLeaf){
            eltList = candidate.getEntries(); 
        }else{
            eltList = candidate.getChildren();
        }
        final int size = eltList.size();
        final double size04 = size*0.4;
        final int demiSize = (int) ((size04>=1)?size04:1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        final List<CoupleGE> listCGE = new ArrayList<CoupleGE>();
        GeneralEnvelope gESPLA, gESPLB;
        double bulkTemp;
        double bulkRef = -1;
        CoupleGE coupleGE;
        int index = 0;
        int lower_or_upper = 0;
        int cut2;
        Comparator comp;
        for(int lu = 0; lu<2; lu++){
            if(isLeaf){
            if(lu == 0){
                    comp = calc.sortFrom(splitIndex, true, false);
                }else{
                    comp = calc.sortFrom(splitIndex, false, false);
                }
            }else{
                if(lu == 0){
                    comp = calc.sortFrom(splitIndex, true, true);
                }else{
                    comp = calc.sortFrom(splitIndex, false, true);
                }
            }
            Collections.sort(eltList, comp);
            
            for(int cut = demiSize; cut<=size - demiSize;cut++){
                for(int i = 0;i<cut;i++){
                    splitListA.add(eltList.get(i));
                }
                for(int j = cut;j<size;j++){
                    splitListB.add(eltList.get(j));
                }
                cut2 = size - cut;
                if(isLeaf){
                    gESPLA = new GeneralEnvelope((Envelope)splitListA.get(0));
                    gESPLB = new GeneralEnvelope((Envelope)splitListB.get(0));
                    for(int i = 1; i<cut;i++){
                        gESPLA.add((Envelope)splitListA.get(i));
                    }
                    for(int i = 1; i<cut2;i++){
                        gESPLB.add((Envelope)splitListB.get(i));
                    }
                }else{
                    gESPLA = new GeneralEnvelope(((Node)splitListA.get(0)).getBoundary());
                    gESPLB = new GeneralEnvelope(((Node)splitListB.get(0)).getBoundary());
                    for(int i = 1; i<cut;i++){
                        gESPLA.add(((Node)splitListA.get(i)).getBoundary());
                    }
                    for(int i = 1; i<cut2;i++){
                        gESPLB.add(((Node)splitListB.get(i)).getBoundary());
                    }
                }
                coupleGE = new CoupleGE(gESPLA, gESPLB, calc);
                bulkTemp = coupleGE.getOverlaps();
                if(bulkTemp <bulkRef||bulkRef==-1){
                    bulkRef = bulkTemp;
                    index = cut;
                    lower_or_upper = lu;
                }else if(bulkTemp == 0){
                    coupleGE.setUserProperty("cut", cut);
                    coupleGE.setUserProperty("lower_or_upper", lu);
                    listCGE.add(coupleGE);
                }
                splitListA.clear();
                splitListB.clear();
            }
        }
          
        if(!listCGE.isEmpty()){
            double areaRef = -1;
            double areaTemp;
            for(CoupleGE cge : listCGE){
                areaTemp = cge.getEdge();
                if(areaTemp<areaRef || areaRef == -1){
                    areaRef = areaTemp;
                    index = (Integer) cge.getUserProperty("cut");
                    lower_or_upper = (Integer) cge.getUserProperty("lower_or_upper");
                }
            }
        }
        
        if(isLeaf){
            if(lower_or_upper == 0){
                comp = calc.sortFrom(splitIndex, true, false);
            }else{
                comp = calc.sortFrom(splitIndex, false, false);
            }
        }else{
            if(lower_or_upper == 0){
                comp = calc.sortFrom(splitIndex, true, true);
            }else{
                comp = calc.sortFrom(splitIndex, false, true);
            }
        }
        Collections.sort(eltList, comp);
        
        for(int i = 0;i<index;i++){
            splitListA.add(eltList.get(i));
        }
        for(int i =index; i<size; i++){
            splitListB.add(eltList.get(i));
        }
        
        if(isLeaf){
            return UnmodifiableArrayList.wrap(tree.createNode(tree, null, null, splitListA),
                                              tree.createNode(tree, null, null, splitListB));
        }else{
            final Node resultA = (Node) ((splitListA.size() == 1)?splitListA.get(0):tree.createNode(tree, null, splitListA, null));
            final Node resultB = (Node) ((splitListB.size() == 1)?splitListB.get(0):tree.createNode(tree, null, splitListB, null));
            return UnmodifiableArrayList.wrap(resultA, resultB);
        }
    }
    
    /**Compute and define which axis to split {@code Node} candidate.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Define split axis method decides a split axis among all dimensions.
     *               The choosen axis is the one with smallest overall perimeter or area (in fonction with dimension size).
     *               It work by sorting all entry or {@code Node}, from their left boundary coordinates. 
     *               Then it considers every divisions of the sorted list that ensure each node is at least 40% full.
     *               The algorithm compute perimeters or area of two result {@code Node} from every division.
     *               A second pass repeat this process with respect their right boundary coordinates. 
     *               Finally the overall perimeter or area on one axis is the som of all perimeter or area obtained from the two pass.</strong> 
     * </font></blockquote>
     * 
     * @throws IllegalArgumentException if candidate is null.
     * @return prefered ordinate index to split.
     */
    private static int defineSplitAxis(final Node candidate){
        ArgumentChecks.ensureNonNull("defineSplitAxis : ", candidate);
        final boolean isLeaf = candidate.isLeaf();
        List eltList;
        if(isLeaf){
            eltList = candidate.getEntries(); 
        }else{
            eltList = candidate.getChildren();
        }
        final Calculator calc = candidate.getTree().getCalculator();
        final int dim = candidate.getBoundary().getDimension();
        final int size = eltList.size();
        final double size04 = size*0.4;
        final int demiSize = (int) ((size04>=1)?size04:1);
        final List splitListA = new ArrayList();
        final List splitListB = new ArrayList();
        GeneralEnvelope gESPLA, gESPLB;
        double bulkTemp, bulkRef = -1;
        int index = 0;
        int cut2;
        Comparator comp;
        for(int indOrg=0;indOrg<dim;indOrg++){
            bulkTemp = 0;
            for(int left_or_right = 0;left_or_right<2;left_or_right++){
                if(isLeaf){
                    if(left_or_right == 0){
                        comp = calc.sortFrom(indOrg, true, false);
                    }else{
                        comp = calc.sortFrom(indOrg, false, false);
                    }
                }else{
                    if(left_or_right == 0){
                        comp = calc.sortFrom(indOrg, true, true);
                    }else{
                        comp = calc.sortFrom(indOrg, false, true);
                    }
                }
                Collections.sort(eltList, comp);
                for(int cut = demiSize, sdem = size - demiSize; cut<=sdem;cut++){
                    splitListA.clear();
                    splitListB.clear();
                    for(int i = 0;i<cut;i++){
                        splitListA.add(eltList.get(i));
                    }
                    for(int j = cut;j<size;j++){
                        splitListB.add(eltList.get(j));
                    }
                    cut2 = size - cut;
                    if(isLeaf){
                        gESPLA = new GeneralEnvelope((Envelope)splitListA.get(0));
                        gESPLB = new GeneralEnvelope((Envelope)splitListB.get(0));
                        for(int i = 1; i<cut;i++){
                            gESPLA.add((Envelope)splitListA.get(i));
                        }
                        for(int i = 1; i<cut2;i++){
                            gESPLB.add((Envelope)splitListB.get(i));
                        }
                    }else{
                        gESPLA = new GeneralEnvelope(((Node)splitListA.get(0)).getBoundary());
                        gESPLB = new GeneralEnvelope(((Node)splitListB.get(0)).getBoundary());
                        for(int i = 1; i<cut;i++){
                            gESPLA.add(((Node)splitListA.get(i)).getBoundary());
                        }
                        for(int i = 1; i<cut2;i++){
                            gESPLB.add(((Node)splitListB.get(i)).getBoundary());
                        }
                    }
                    bulkTemp += calc.getEdge(gESPLA);
                    bulkTemp += calc.getEdge(gESPLB);
                }
            }
            if(indOrg == 0){
                bulkRef = bulkTemp;
                index = indOrg;
            }else{
                if(bulkTemp < bulkRef){
                    bulkRef = bulkTemp;
                    index = indOrg;
                }
            }
        }
        return index;
    }
        
    /**
     * Travel {@code Tree}, find {@code Envelope} entry if it exist and delete it.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Moreover {@code Tree} is condensate after a deletion to stay conform about R-Tree properties.</strong> 
     * </font></blockquote>
     * 
     * @param candidate {@code Node}  where to delete.
     * @param entry {@code Envelope} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static boolean deleteNode(final Node candidate, final Envelope entry) throws MismatchedReferenceSystemException{
        ArgumentChecks.ensureNonNull("DeleteNode : Node candidate", candidate);
        ArgumentChecks.ensureNonNull("DeleteNode : Node candidate", candidate);
        if(new GeneralEnvelope(candidate.getBoundary()).intersects(entry, true)){
            if(candidate.isLeaf()){
                final boolean removed = candidate.getEntries().remove(entry);
                if(removed){
                    trim(candidate);
                    return true;
                }
            }else{
                for(Node no : candidate.getChildren()){
                    final boolean removed = deleteNode(no, entry);
                    if(removed){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**Condense R-Tree.
     * 
     * Condense made, travel up from leaf to tree trunk.
     * 
     * @param candidate {@code Node} to begin condense.
     * @throws IllegalArgumentException if candidate is null.
     */
    private static void trim(final Node candidate) throws MismatchedReferenceSystemException {
        ArgumentChecks.ensureNonNull("trim : Node candidate", candidate);
        final List<Node> children = candidate.getChildren();
        final Tree tree = candidate.getTree();
        final List<Envelope> reinsertList = new ArrayList<Envelope>();
        for(int i = children.size()-1;i>=0;i--){
            final Node child = children.get(i);
            if(child.isEmpty()){
                children.remove(i);
            }else if(child.getChildren().size() ==1){
                final Node n = children.remove(i);
                for(Node n2d : n.getChildren()){
                    n2d.setParent(candidate);
                }
                children.addAll(n.getChildren());
            }else if(child.isLeaf()&&child.getEntries().size()<=tree.getMaxElements()/3){
                reinsertList.addAll(child.getEntries());
                children.remove(i);
            }
        }
        if(candidate.getParent()!=null){
            trim(candidate.getParent());
        }
        for(Envelope ent : reinsertList){
            tree.insert(ent);
        }
    }
    
    /**
     * Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     * 
     * @param nodeA Node
     * @param nodeB Node
     * @throws IllegalArgumentException if nodeA or nodeB are not tree leaf.
     * @throws IllegalArgumentException if nodeA or nodeB, and their subnodes, don't contains some {@code Envelope} entry(ies).
     */
    private static void branchGrafting(final Node nodeA, final Node nodeB ) throws MismatchedReferenceSystemException{
        if(!nodeA.isLeaf() || !nodeB.isLeaf()){
            throw new IllegalArgumentException("branchGrafting : not leaf");
        }
        final List<Envelope> listGlobale = new ArrayList<Envelope>(nodeA.getEntries());
        listGlobale.addAll(new ArrayList<Envelope>(nodeB.getEntries()));
        nodeA.getEntries().clear();
        nodeB.getEntries().clear();
        if(listGlobale.isEmpty()){
            throw new IllegalArgumentException("branchGrafting : empty list");
        }
        final Calculator calc = nodeA.getTree().getCalculator();
        final GeneralEnvelope globalE = new GeneralEnvelope(listGlobale.get(0));
        final int size = listGlobale.size();
        for(int i = 1;i<size; i++){
            globalE.add(listGlobale.get(i));
        }
        double lengthDimRef = -1;
        int indexSplit = -1;
        for(int i = 0, dim = globalE.getDimension(); i<dim; i++){
            double lengthDimTemp = globalE.getSpan(i);
            if(lengthDimTemp>lengthDimRef){
                lengthDimRef = lengthDimTemp;
                indexSplit = i;
            }
        }
        final Comparator comp = calc.sortFrom(indexSplit, true, false);
        Collections.sort(listGlobale, comp);
        GeneralEnvelope envB;
        final GeneralEnvelope envA = new GeneralEnvelope(listGlobale.get(0));
        double overLapsRef = -1;
        int index =-1;
        final int size04 = (int)((size*0.4 >= 1) ? size*0.4 : 1);
        for(int cut = size04; cut < size-size04; cut++){
            for(int i = 1; i<cut;i++){
                envA.add(listGlobale.get(i));
            }
            envB = new GeneralEnvelope(listGlobale.get(cut));
            for(int i = cut+1; i<size;i++){
                envB.add(listGlobale.get(i));
            }
            double overLapsTemp = calc.getOverlaps(envA, envB);
            if(overLapsTemp < overLapsRef || overLapsRef == -1){
                overLapsRef = overLapsTemp;
                index = cut;
            }
        }
        for(int i = 0; i<index;i++){
            nodeInsert(nodeA, listGlobale.get(i));
        }
        for(int i = index; i<size;i++){
            nodeInsert(nodeB, listGlobale.get(i));
        }
    }
    
    /**Get statement from re-insert state.
     * 
     * @return true if it's permit to re-insert else false.
     */
    private boolean getIA() {
        return insertAgain;
    }

    /**Affect statement to permit or not, re-insertion.
     * @param insertAgain
     */
    private void setIA(boolean insertAgain) {
        this.insertAgain = insertAgain;
    }
    
    /**Recover lesser 33% largest of {@code Node} candidate within it.
     * 
     * @throws IllegalArgumentException if {@code Node} candidate is null.
     * @return all Entry within subNodes at more 33% largest of this {@code Node}.
     */
    private static List<Envelope> getElementAtMore33PerCent(final Node candidate) {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidate", candidate);
        final Calculator calc = candidate.getTree().getCalculator();
        final List<Envelope> lsh = new ArrayList<Envelope>();
        final Envelope boundGE = candidate.getBoundary();
        final DirectPosition candidateCentroid = getMedian(boundGE);
        final double distPermit = calc.getDistance(boundGE.getLowerCorner(), boundGE.getUpperCorner()) / 1.666666666;
        nodeSearch(candidate, boundGE, lsh);
        for (int i = lsh.size() - 1; i >= 0; i--) {
            if (calc.getDistance(candidateCentroid, DefaultTreeUtils.getMedian(lsh.get(i))) < distPermit) {
                lsh.remove(i);
            }
        }
        return lsh;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Node createNode(Tree tree, Node parent, List<Node> listChildren, List<Envelope> listEntries, double... coordinates) {
        final int ddim = coordinates.length;
        if((ddim % 2) != 0){
            throw new IllegalArgumentException("coordinate dimension is not correct");
        }
        if(ddim == 0){
            return nodefactory.createNode(tree, parent, null, null,listChildren, listEntries);
        }
        final int dim = coordinates.length/2;
        final double[] dp1Coords = new double[dim];
        final double[] dp2Coords = new double[dim];
        System.arraycopy(coordinates, 0, dp1Coords, 0, dim);
        System.arraycopy(coordinates, dim, dp2Coords, 0, dim);
        
        final DirectPosition dp1 = new GeneralDirectPosition(crs);
        final DirectPosition dp2 = new GeneralDirectPosition(crs);
        for(int i =0; i<dim; i++){
            dp1.setOrdinate(i, dp1Coords[i]);
            dp2.setOrdinate(i, dp2Coords[i]);
        }
        return nodefactory.createNode(tree, parent, dp1, dp2,listChildren, listEntries);
    }
}
