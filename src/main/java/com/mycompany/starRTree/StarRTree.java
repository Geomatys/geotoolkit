/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.starRTree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Rtree;
import com.mycompany.utilsRTree.SplitCase;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**Create R*Tree.
 *
 * @author Maréchal Rémi (Geomatys)
 * @version SNAPSHOT
 */
public class StarRTree extends Rtree{

    /**
     * @param maxElements max number of elements by cells.
     */
    public StarRTree(int maxElements) {
        this.maxElements = maxElements;
        this.treeTrunk = new StarBranch(this);
    }

    /**
     * @param maxElements max number of elements by cells.
     * @param shape table of shape to add in the R*Tree.
     */
    public StarRTree(int maxElements, Entry ...entry) {
        this.maxElements = maxElements;
        StarLeaf sL = new StarLeaf(this, entry[0]);
        this.treeTrunk = new StarBranch(this, sL);
        sL.setParent(treeTrunk);
        if(entry.length>1){
            for(int i = 1; i<entry.length;i++){
                treeTrunk.insert(entry[i]);
            }
        }
        
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
    public void insert(Entry entry) {
        if(((StarBranch)treeTrunk).getElements().isEmpty()){
            StarLeaf sL =  new StarLeaf(this, entry);
            
            treeTrunk = new StarBranch(this, sL);
            sL.setParent(treeTrunk);
        }else{
            treeTrunk.insert(entry);
        }
    }

    /**Find shape and delete it.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Recondense tree after deleting action.</strong> 
     * </font></blockquote>
     * 
     * @param shape to delete.
     */
    @Override
    public void delete(Entry entry) {
        treeTrunk.delete(entry);
    }

    /**Find all element(s), which intersect or whithin regionSearch.
     * 
     * @param regionSearch search area.
     * @param result list which contain result search.
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        treeTrunk.search(regionSearch, result);
    }

    /**
     * @return max number what each cells can contains.
     */
    @Override
    public int getMaxElements() {
        return this.maxElements;
    }

    @Override
    public void paint(Graphics2D g) {
        treeTrunk.paint(g);
    }
    
    @Override
    public String toString() {
        return treeTrunk.toString();
    }
}
