/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.basic;

import java.util.List;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import static org.geotoolkit.index.tree.TreeUtils.*;

/**Create R-Tree (Basic)
 *
 * @author Rémi Marechal (Geomatys)
 * @author Yohann Sorel (Geomatys)
 */
public class BasicRTree extends AbstractTree2D {

    private SplitCase choice;
    
    /**Create R-Tree.
     * 
     * @param maxElements max value of elements per tree cell.
     * @param choice Split made "linear" or "quadratic".
     */
    public BasicRTree(int maxElements, SplitCase choice) {
        super(maxElements);
        this.choice = choice;
        setRoot(new Node2D(this));
    }
    
    /**
     * {@inheritDoc} 
     */
    public void search(Shape regionSearch, List<Shape> result) {
        final Node2D root = getRoot();
        if(root!=null){
            searchNode(getRoot(), regionSearch, result);
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Shape entry) {
        final Node2D root = getRoot();
        if(root != null){
            if(root.isEmpty()){
                root.getEntries().add(entry);
            }else{
                insertNode(getRoot(), entry);
            }
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void delete(Shape entry) {
        final Node2D root = getRoot();
        if(root != null){
            deleteNode(getRoot(), entry);
        }
    }

    /**
     * @return splitcase choosen to split. 
     */
    public SplitCase getSplitCase() {
        return this.choice;
    }
    
    /**Insert entry in {@code Node2D} in accordance with R-Tree properties.
     * 
     * @param candidate {@code Node2D} where user want insert data.
     * @param entry to insert.
     * @throws IllegalArgumentException if candidate or entry are null.
     */
    private static void insertNode(final Node2D candidate, final Shape entry){
        ArgumentChecks.ensureNonNull("insertNode : candidate", candidate);
        ArgumentChecks.ensureNonNull("insertNode : entry", entry);
        if(candidate.isLeaf()){
            candidate.getEntries().add(entry);
        }else{
            final Node2D nodC = chooseSubtree(candidate.getChildren(), entry);
            insertNode(nodC,entry);
        }
        
        if(!candidate.isLeaf()){
            final List<Node2D> lN =  candidate.getChildren();
            for(int i = lN.size()-1;i>=0;i--){
                if(countElements(lN.get(i)) > candidate.getTree().getMaxElements()){
                    final Node2D n = lN.remove(i);
                    List<Node2D> ls = splitNode(n);
                    ls.get(0).setParent(candidate);
                    ls.get(1).setParent(candidate);
                    lN.addAll(ls);
                }
            }
        }
        
        if(candidate.getParent()==null){
            if(countElements(candidate)>candidate.getTree().getMaxElements()){
                List<Node2D> l = splitNode(candidate);
                l.get(0).setParent(candidate);
                l.get(1).setParent(candidate);
                candidate.getEntries().clear();
                candidate.getChildren().clear();
                candidate.getChildren().addAll(l);
            }
        }
    }
    
    /**Split a overflow {@code Node2D} in accordance with R-Tree properties.
     * 
     * @param candidate {@code Node2D} to Split.
     * @throws IllegalArgumentException if candidate is null.
     * @throws IllegalArgumentException if candidate elements number is lesser 2.
     * @return List<Node2D> which contains two {@code Node2D} (split result of candidate).
     */
    private static List<Node2D> splitNode(final Node2D candidate){
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        if(countElements(candidate) < 2){
            throw new IllegalArgumentException("not enought elements within "+candidate+" to split.");
        }
        
        final Tree tree = candidate.getTree();
        final int maxElmnts = tree.getMaxElements();
        boolean leaf = candidate.isLeaf();
        List<?> ls;
        Object s1, s2 ;
        
        if(leaf){
            ls =  candidate.getEntries();
            s1 = ls.get(0); 
            s2 = ls.get(1);
        }else{
            ls = candidate.getChildren();
            s1 = ls.get(0); 
            s2 = ls.get(1);
        }
            
        double refValue = 0;
        double tempValue = 0;
        int index1 = 0;
        int index2 = 0;

        switch(((BasicRTree)tree).getSplitCase()){
            case LINEAR : {
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        tempValue = (leaf) ? getDistanceBetweenTwoBound2D(((Shape)ls.get(i)).getBounds2D(), ((Shape)ls.get(j)).getBounds())
                                           : getDistanceBetweenTwoBound2D(((Node2D)ls.get(i)).getBoundary().getBounds2D(), ((Node2D)ls.get(j)).getBoundary().getBounds2D());
                        if(tempValue > refValue){
                            s1 = ls.get(i);
                            s2 = ls.get(j);
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }break;
                
            case QUADRATIC : {
                Rectangle2D rectGlobal, bound1, bound2;
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        if(leaf){
                            bound1 = ((Shape)ls.get(i)).getBounds2D();
                            bound2 = ((Shape)ls.get(j)).getBounds2D();
                            rectGlobal = getEnveloppeMin(UnmodifiableArrayList.wrap((Shape)bound1,(Shape)bound2)).getBounds2D();
                            
                        }else{
                            bound1 = ((Node2D)ls.get(i)).getBoundary().getBounds2D();
                            bound2 = ((Node2D)ls.get(j)).getBoundary().getBounds2D();
                            rectGlobal = TreeUtils.getEnveloppeMin(UnmodifiableArrayList.wrap((Shape)bound1,bound2)).getBounds2D();
                            
                        }
                        tempValue = rectGlobal.getWidth()*rectGlobal.getHeight() 
                                        - (bound1.getWidth()*bound1.getHeight()+bound2.getWidth()*bound2.getHeight());
                        
                        if(tempValue > refValue){
                            s1 = ls.get(i);
                            s2 = ls.get(j);
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }break;
        }
        
        ls.remove(Math.max(index1, index2));
        ls.remove(Math.min(index1, index2));
        Rectangle2D r1Temp, r2Temp;
        Node2D result1 = (leaf) ? createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s1))
                                : createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s1), null);
        Node2D result2 = (leaf) ? createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s2))
                                : createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s2), null);
        double demimaxE = maxElmnts/3;
        demimaxE = Math.max(demimaxE, 1);
        if(leaf){
            for(Shape ent : (List<Shape>)ls){
                r1Temp = getEnveloppeMin(UnmodifiableArrayList.wrap((Shape)s1, ent)).getBounds2D();
                r2Temp = getEnveloppeMin(UnmodifiableArrayList.wrap((Shape)s2, ent)).getBounds2D();
                double area1 = r1Temp.getWidth()*r1Temp.getHeight();
                double area2 = r2Temp.getWidth()*r2Temp.getHeight();
                int r1nbE = countElements(result1);
                int r2nbE = countElements(result2);
                if(area1<area2){
                    if(r1nbE<=demimaxE&&r2nbE>demimaxE){
                        insertNode(result1, ent);
                    }else if(r2nbE<=demimaxE&&r1nbE>demimaxE){
                        insertNode(result2,ent);
                    }else{
                        insertNode(result1, ent);
                    }
                }else if(area1 == area2){
                    if(r1nbE<r2nbE){
                        insertNode(result1, ent);
                    }else{
                        insertNode(result2,ent);
                    }
                }else{
                    if(r1nbE<=demimaxE&&r2nbE>demimaxE){
                        insertNode(result1, ent);
                    }else if(r2nbE<=demimaxE&&r1nbE>demimaxE){
                        insertNode(result2,ent);
                    }else{
                        insertNode(result2,ent);
                    }
                }
            }
        }else{
            final List<Node2D> listResult1 = result1.getChildren();
            final List<Node2D> listResult2 = result2.getChildren();
            
            for(Node2D no : (List<Node2D>)ls){

                r1Temp = getEnveloppeMin(UnmodifiableArrayList.wrap(((Node2D)s1).getBoundary(), no.getBoundary())).getBounds2D();
                r2Temp = getEnveloppeMin(UnmodifiableArrayList.wrap(((Node2D)s2).getBoundary(), no.getBoundary())).getBounds2D();
                double area1 = r1Temp.getWidth()*r1Temp.getHeight();
                double area2 = r2Temp.getWidth()*r2Temp.getHeight();
                int lrs1 = listResult1.size();
                int lrs2 = listResult2.size();
                if(area1 < area2){
                    if(lrs1 <=demimaxE&&lrs2 > demimaxE){
                        listResult1.add(no);
                    }else if(lrs2<=demimaxE&&lrs1 > demimaxE){
                        listResult2.add(no);
                    }else{
                        listResult1.add(no);
                    }
                }else if(area1 == area2){
                    if(lrs1 < lrs2){
                        listResult1.add(no);
                    }else{
                        listResult2.add(no);
                    }
                }else{
                    if(lrs1<=demimaxE&&lrs2>demimaxE){
                        listResult1.add(no);
                    }else if(lrs2<=demimaxE&&lrs1>demimaxE){
                        listResult2.add(no);
                    }else{
                        listResult2.add(no);
                    }
                }
            }
            if(listResult1.size()==1){
                result1 = listResult1.get(0);
                result1.setParent(null);
            }
            if(listResult2.size()==1){
                result2 = listResult2.get(0);
                result2.setParent(null);
            }
            for(Node2D nod2d : result1.getChildren()){
                nod2d.setParent(result1);
            }
            for(Node2D nod2d : result2.getChildren()){
                nod2d.setParent(result2);
            }
        }
        return UnmodifiableArrayList.wrap(result1, result2);
    }
    
/**Find appropriate {@code Node2D} to insert {@code Shape}.
     * To define appropriate Node, criterion are : 
     *      - require minimum area enlargement to cover shap.
     *      - or put into {@code Node2D} with lesser elements number in case of area equals.
     * 
     * @param children List of {@code Node2D}.
     * @param entry {@code Shape} to add.
     * @throws IllegalArgumentException if children or entry are null.
     * @throws IllegalArgumentException if children is empty.
     * @return {@code Node2D} which is appropriate to contain shap.
     */
    private static Node2D chooseSubtree(final List<Node2D> children, final Shape entry){
        ArgumentChecks.ensureNonNull("chooseSubtree : List<Node2D> children", children);
        ArgumentChecks.ensureNonNull("chooseSubtree : Shape entry", entry);
        if(children.isEmpty()){
            throw new IllegalArgumentException("chooseSubtree : ln is empty");
        }
        
        if(children.size()==1){
            return children.get(0);
        }
        
        final Rectangle2D sB = entry.getBounds2D();
        Node2D n = children.get(0);
        
        for(Node2D nod : children){
            if(nod.getBoundary().contains(sB)){
                return nod;
            }
        }
        
        final Rectangle2D rn = n.getBoundary().getBounds2D();
        double xr = Math.min(sB.getBounds2D().getMinX(), rn.getMinX());
        double yr = Math.min(sB.getBounds2D().getMinY(), rn.getMinY());
        double widthr = Math.abs(Math.max(sB.getBounds2D().getMaxX()-xr, rn.getMaxX())-xr);
        double heightr = Math.abs(Math.max(rn.getMaxY()-yr, sB.getBounds2D().getMaxY())-yr);
        double area = widthr*heightr;
        double nbElmt = countElements(n);
        
        for(Node2D n2D : children){
            final Rectangle2D rnod = n2D.getBoundary().getBounds2D();
            final int nbe = countElements(n2D); 
             xr = Math.min(sB.getMinX(), rnod.getMinX());
             yr = Math.min(sB.getMinY(), rnod.getMinY());
             widthr = Math.abs(Math.max(sB.getMaxX()-xr, rnod.getMaxX())-xr);
             heightr = Math.abs(Math.max(rnod.getMaxY()-yr, sB.getMaxY())-yr);
            
            if(widthr*heightr<area){
                n = n2D;
                area = widthr*heightr;
                nbElmt = nbe;
            }else if(widthr*heightr==area){
                if(nbe<nbElmt){
                    n = n2D;
                    area = widthr*heightr;
                    nbElmt = nbe;
                }
            }
        }
        return n;
    }
}
