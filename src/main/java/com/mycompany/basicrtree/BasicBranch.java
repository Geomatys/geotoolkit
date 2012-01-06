/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.basicrtree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**Create a R-Tree Branch.
 *
 * @author Rémi Marechal (Geomatys).
 */
 class BasicBranch extends Node {
   
    private List<Node> listR;
    
    /**Create a virgin Basic Rtree Branch.
     * 
     * @param tree pointer on Basic RTree.
     */
    public BasicBranch(Rtree tree) {
        this.tree = tree;
        this.listR = new ArrayList<Node>();
        reSize();
    }
    
    /**Basic Branch copy constructor.
     * 
     * @param br BasicBranch.
     * @param tree pointer on Basic RTree.
     */
    public BasicBranch(BasicBranch br, Rtree tree) {
        this.listR = br.getNodes();
        this.tree = tree;
        reSize();
    }
    
    /**Create Basic branch with Node list in param.
     * 
     * @param listR Node list.
     * @param tree pointer on Basic RTree.
     */
    public BasicBranch(List<Node> listR, Rtree tree) {
        this.listR = new ArrayList<Node>(listR);
        this.tree = tree;
        reSize();
    }
    
    /**Create Basic branch with Node table in param.
     * 
     * @param listR Node table.
     * @param tree pointer on Basic RTree.
     */
    public BasicBranch(Rtree tree, Node ...nod) {
        this.tree = tree;
        listR = new ArrayList<Node>(Arrays.asList(nod));
        reSize();
    }
    
    /**Add a {@code Node} in this branch.
     * 
     * @param n Node to add.
     */
    public void addNode(Node n){
        listR.add(n);
        reSize();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public double getArea(){
        return this.width*this.height;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public double getDeadSpace(){
        double areaElement = 0;
        
        for(Rectangle2D rect : listR){
            areaElement += (rect.getWidth()*rect.getHeight());
        }
        
        return (this.getArea()-areaElement);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result){
        if(this.intersects(regionSearch)){
            for(Node nod : listR){
                nod.search(regionSearch, result);
            }
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Entry entry){
        chooseSubtree(listR, entry).insert(entry);
        for(int i = 0;i<listR.size();i++){
            if(listR.get(i).getNbElements()>tree.getMaxElements()){
                Node n = listR.remove(i);
                List<Node> l = n.split();
                for(Node nod : l){
                    nod.setParent(this);
                }
                listR.addAll(l);
            }
        }

        if(getNbElements()>tree.getMaxElements()){
            List<Node> l = split();
            for(Node nod : l){
                nod.setParent(this);
            }
            listR.clear();
            listR.addAll(l);
        }
        reSize();
    }
    /**
     * @return 
     */
    @Override
    public List<Node> split(){
        if(getNbElements()<2){
            throw new IllegalArgumentException("not enought elements within "+this+" to split.");
        }
        List<Node> ls = getNodes();
        Node s1 = ls.get(0), s2 = ls.get(1);
        Node lfTemp;
        double refValue = 0;
        double tempValue = 0;
        int index1 = 0;
        int index2 = 0;

        switch(((BasicRTree)tree).getSplitCase()){
            case LINEAR : {
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        tempValue = getDistanceBetweenTwoNode(ls.get(i), ls.get(j));
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
                        lfTemp = new BasicBranch(this.tree,ls.get(i),ls.get(j));
                        tempValue = lfTemp.getDeadSpace();
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
        BasicBranch b1Temp, b2Temp;
        BasicBranch result1 = new BasicBranch(this.tree, s1);
        BasicBranch result2 = new BasicBranch(this.tree, s2);
        for(Node no : ls){
            b1Temp = new BasicBranch(this.tree, s1, no);
            b2Temp = new BasicBranch(this.tree, s2, no);
            
            if(b1Temp.getArea() < b2Temp.getArea()){
                if(result1.getNbElements()<=tree.getMaxElements()/2&&result2.getNbElements()>tree.getMaxElements()/2){
                    result1.addNode(no);
                }else if(result2.getNbElements()<=tree.getMaxElements()/2&&result1.getNbElements()>tree.getMaxElements()/2){
                    result2.addNode(no);
                }else{
                    result1.addNode(no);
                }
            }else if(b1Temp.getArea() == b2Temp.getArea()){
                if(b1Temp.getNbElements() < b2Temp.getNbElements()){
                    result1.addNode(no);
                }else{
                    result2.addNode(no);
                }
            }else{
                if(result1.getNbElements()<=tree.getMaxElements()/2&&result2.getNbElements()>tree.getMaxElements()/2){
                    result1.addNode(no);
                }else if(result2.getNbElements()<=tree.getMaxElements()/2&&result1.getNbElements()>tree.getMaxElements()/2){
                    result2.addNode(no);
                }else{
                    result2.addNode(no);
                }
            }
        }
        
        return UnmodifiableArrayList.wrap((Node)result1, (Node)result2);
    }
    
    /**Find appropriate {@code Node} to contains {@code Entry}.
     * To define appropriate Node, criterion are : 
     *      - require minimum area enlargement to cover shap.
     *      - or put into Node with lesser elements number in case area equals.
     * 
     * @param lN List of {@code Node}.
     * @param shap {@code Shape} to add.
     * @return {@code Node} which is appropriate to contain shap.
     */
    private Node chooseSubtree(List<Node> lN, Entry entry){
        
        if(lN.size()==1){
            return lN.get(0);
        }
        
        Rectangle2D sB = entry.getBoundary();
        Node n = lN.get(0);
        
        for(Node nod : lN){
            if(nod.contains(sB)){
                return nod;
            }
        }
        
        double xr = Math.min(sB.getMinX(), n.getMinX());
        double yr = Math.min(sB.getMinY(), n.getMinY());
        double widthr = Math.abs(Math.max(sB.getMaxX()-xr, n.getMaxX())-xr);
        double heightr = Math.abs(Math.max(n.getMaxY()-yr, sB.getMaxY())-yr);
        double area = widthr*heightr;
        double nbElmt = n.getNbElements();
        
        for(Node nod : lN){
             xr = Math.min(sB.getMinX(), nod.getMinX());
             yr = Math.min(sB.getMinY(), nod.getMinY());
             widthr = Math.abs(Math.max(sB.getMaxX()-xr, nod.getMaxX())-xr);
             heightr = Math.abs(Math.max(nod.getMaxY()-yr, sB.getMaxY())-yr);
            
            if(widthr*heightr<area){
                n = nod;
                area = widthr*heightr;
                nbElmt = nod.getNbElements();
            }else if(widthr*heightr==area){
                if(nod.getNbElements()<nbElmt){
                    n = nod;
                    area = widthr*heightr;
                    nbElmt = nod.getNbElements();
                }
            }
        }
        return n;
    }
    
    /**
     * @return Node List.
     */
    public List<Node> getNodes(){
        return new ArrayList<Node>(this.listR);
    }
    
    /**Distance returned is compute between boundary gravity center of each {@code Node}.
     * 
     * @param n1 first Node.
     * @param n2 second Node.
     * @return Euclidean distance between two Node.
     */
    double getDistanceBetweenTwoNode(final Node n1, final Node n2){
        final Rectangle2D rn1 = n1.getBounds2D();
        final Rectangle2D rn2 = n2.getBounds2D();
        final double x1 = (rn1.getMinX()+rn1.getMaxX())/2;
        final double y1 = (rn1.getMinY()+rn1.getMaxY())/2;
        final double x2 = (rn2.getMinX()+rn2.getMaxX())/2;
        final double y2 = (rn2.getMinY()+rn2.getMaxY())/2;
        return Math.hypot(Math.abs(x1-x2), Math.abs(y1-y2));
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void paint(Graphics2D g){
        
        g.setStroke(new BasicStroke(1/2));
        for(Node rect : listR){
            rect.paint(g);
        }
        g.setColor(Color.MAGENTA);
        //g.setStroke(new BasicStroke(1/2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5.0f}, 0));
        g.draw(this);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int getNbElements() {
        return listR.size();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean delete(Entry entry) {
        if(!this.getBounds2D().intersects(entry.getBoundary())){
            return false;
        }
        for(Node no : listR){
            final boolean removed = no.delete(entry);
            if(removed){
                reSize();
                return true;
            }
        }
        return false;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public double getPerimeter() {
        return 2*(this.getWidth()+this.getHeight());
    }

    /**
     * @return number of subnodes. 
     */
    public List<Node> getElements() {
        return new ArrayList<Node>(listR);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void reSize() {
        
        if(listR.isEmpty()){
            this.setRect(0, 0, 0, 0);
        }else{
            Rectangle2D rectTemp = listR.get(0).getBounds2D();
            if(listR.size() == 1){
                this.setRect(rectTemp);
            }else{

                double x1 = rectTemp.getMinX();
                double y1 = rectTemp.getMinY();
                double x2 = x1;
                double y2 = y1;

                for(Shape shap : listR){
                    rectTemp = shap.getBounds2D();
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

    /**
     * {@inheritDoc} 
     */
    @Override
    public void trim() {
        for(int i = listR.size()-1;i>=0;i--){
            if(listR.get(i) instanceof Node){
                if(((Node)listR.get(i)).getNbElements()==0){
                    listR.remove(i);
                } 
            }
        }
        
        final List<Entry> lsh = new ArrayList<Entry>();
        
        if(listR.size()==1&&listR.get(0) instanceof Node){
            listR = new ArrayList<Node>(Arrays.asList(listR.get(0)));
        }else{
            if(getNbElements()<=tree.getMaxElements()/2){
                tree.search(getBounds2D(), lsh);
                listR.clear();
            }
        }
        reSize();
        
        if(listR.isEmpty()&&parent!=null){
            parent.trim();
        }
        
        for(Entry ent : lsh){
            tree.insert(ent);
        }
        reSize();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Trees.toString("Node ", listR);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
