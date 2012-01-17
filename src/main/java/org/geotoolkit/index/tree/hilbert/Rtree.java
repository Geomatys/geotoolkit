/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.utilsRTree;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**Define a generic R-Tree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class Rtree {
    
    /**
     * Max elements value autorized in each tree leaf and branch.
     */
    protected int maxElements;
    
    /**
     * Tree trunk.
     */
    protected Node treeTrunk;
    
    /**Find some {@code Entry} which intersect regionSearch param 
     * and add them into result {@code List} param.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: if no result finded, the list passed in parameter is unchanged.</strong> 
     * </font></blockquote>
     * 
     * @param regionSearch Define the region to find Shape within tree.
     * @param result List of Entr(y)(ies).
     */
    public abstract void search(Rectangle2D regionSearch, List<Entry> result);
    
    /**Insert a {@code Entry} into Rtree.
     * 
     * @param Entry to insert into tree.
     */
    public abstract void insert(Entry entry);
    
    /**Find a {@code Entry} into the tree and delete it.
     * 
     * @param Entry to delete.
     */
    public abstract void delete(Entry entry);
    
    /**
     * @return max number autorized by tree cells.
     */
    public abstract int getMaxElements();
    
    /**To draw tree.
     * 
     * @param g graphic element.
     */
    public abstract void paint(Graphics2D g);
}
