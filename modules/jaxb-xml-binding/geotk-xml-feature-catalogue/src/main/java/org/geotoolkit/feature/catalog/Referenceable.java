/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.feature.catalog;

/**
 *
 * @author guilhem
 * @module pending
 */
public interface Referenceable {
    
    public void setReference(boolean isReference);
    
    public boolean isReference();
    
    public Referenceable getReference();
    
    public String getId();

}
