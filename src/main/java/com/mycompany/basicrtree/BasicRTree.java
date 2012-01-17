/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.basicrtree;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import com.mycompany.utilsRTree.SplitCase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.Tree;
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
        searchNode(getRoot(), regionSearch, result);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Shape entry) {
        if(getRoot().isEmpty()){
            getRoot().getEntries().add(entry);
        }else{
            insertNode(getRoot(), entry);
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void delete(Shape entry) {
        deleteNode(getRoot(), entry);
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
     */
    private static void insertNode(final Node2D candidate, final Shape entry){
        
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
     * @param candidate {@code Node2D} to Split
     * @return List<Node2D> which contains two {@code Node2D} (split of candidate).
     */
    private static List<Node2D> splitNode(final Node2D candidate){
        if(countElements(candidate) < 2){
            throw new IllegalArgumentException("not enought elements within "+candidate+" to split.");
        }
        
        final Tree tree = candidate.getTree();
        final int maxElmnts = tree.getMaxElements();
        boolean leaf = candidate.isLeaf();
        List<?> ls;
        Object s1, s2 ;
        Node2D lfTemp;
        
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
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        lfTemp = (leaf) ? createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)ls.get(i),(Shape)ls.get(j))) 
                                        : createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)ls.get(i),(Node2D)ls.get(j)), null);
                        tempValue = getDeadSpace(lfTemp);
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
        Node2D b1Temp, b2Temp;
        Node2D result1 = (leaf) ? createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s1))
                                : createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s1), null);
        Node2D result2 = (leaf) ? createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s2))
                                : createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s2), null);
        final double demimaxE = maxElmnts/2;
        
        if(leaf){
            for(Shape ent : (List<Shape>)ls){
                b1Temp = createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s1, ent));
                b2Temp = createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape)s2, ent));
                int r1nbE = countElements(result1);
                int r2nbE = countElements(result2);
                if(b1Temp.getArea()<b2Temp.getArea()){
                    if(r1nbE<=demimaxE&&r2nbE>demimaxE){
                        insertNode(result1, ent);
                    }else if(r2nbE<=demimaxE&&r1nbE>demimaxE){
                        insertNode(result2,ent);
                    }else{
                        insertNode(result1, ent);
                    }
                }else if(b1Temp.getArea() == b2Temp.getArea()){
                    if(r1nbE<r2nbE){
                        insertNode(result1, ent);
                    }else{
                        insertNode(result2,ent);
                    }
                }else{
                    if(r1nbE<=demimaxE&&r2nbE>demimaxE){
                        insertNode(result1, ent);
                    }else if(r2nbE<=maxElmnts/2&&r1nbE>maxElmnts/2){
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
                b1Temp = createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s1, no), null);
                b2Temp = createNode(tree, null, UnmodifiableArrayList.wrap((Node2D)s2, no), null);

                if(b1Temp.getArea() < b2Temp.getArea()){
                    if(countElements(result1) <=maxElmnts/2&&countElements(result2) > maxElmnts/2){
                        listResult1.add(no);
                    }else if(countElements(result2)<=maxElmnts/2&&countElements(result1) > maxElmnts/2){
                        listResult2.add(no);
                    }else{
                        listResult1.add(no);
                    }
                }else if(b1Temp.getArea() == b2Temp.getArea()){
                    if(countElements(b1Temp) < countElements(b2Temp)){
                        listResult1.add(no);
                    }else{
                        listResult2.add(no);
                    }
                }else{
                    if(countElements(result1)<=maxElmnts/2&&countElements(result2)>maxElmnts/2){
                        listResult1.add(no);
                    }else if(countElements(result2)<=maxElmnts/2&&countElements(result1)>maxElmnts/2){
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
    
/**Find appropriate {@code Node2D} to contains {@code Entry2D<Object>}.
     * To define appropriate Node, criterion are : 
     *      - require minimum area enlargement to cover shap.
     *      - or put into {@code Node2D} with lesser elements number in case area equals.
     * 
     * @param children List of {@code Node2D}.
     * @param entry {@code Entry2D<Object>} to add.
     * @return {@code Node2D} which is appropriate to contain shap.
     */
    private static Node2D chooseSubtree(final List<Node2D> children, final Shape entry){
        
        if(children.isEmpty()){
            throw new IllegalArgumentException("chooseSubtree : ln is empty");
        }
        
        if(children.size()==1){
            return children.get(0);
        }
        
        Rectangle2D sB = entry.getBounds2D();
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
