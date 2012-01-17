/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Shape;

/**Represente boundary of Entry or Node.
 * @see com.mycompany.utilsRTree.Node#getEnveloppeMin(java.util.List) 
 * @author Rémi Maréchal (Géomatys).
 */
public interface Bound {
    
    /**
     * @return boundary of this bound.
     */
    public Shape getBoundary();
}
