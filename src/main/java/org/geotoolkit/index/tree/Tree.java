/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.Collection;

/**Define a generic Tree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public interface Tree<B,V> {
    
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
    void search(B regionSearch, Collection<Entry> result);
    
    /**Insert a {@code Entry} into Rtree.
     * 
     * @param Entry to insert into tree.
     */
     void insert(Entry<B,V> entry);
    
    /**Find a {@code Entry} into the tree and delete it.
     * 
     * @param Entry to delete.
     */
     void delete(Entry<B,V> entry);
    
    /**
     * @return max number autorized by tree cells.
     */
     int getMaxElements();
}
