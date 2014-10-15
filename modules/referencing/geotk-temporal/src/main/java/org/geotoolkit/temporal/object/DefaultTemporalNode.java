/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.temporal.object;

import java.util.Map;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalEdge;
import org.opengis.temporal.TemporalNode;

/**
 *
 * @author rmarechal
 */
public class DefaultTemporalNode extends AbstractIdentifiedObject implements TemporalNode {

    /**
     * 
     * @param properties
     * @throws IllegalArgumentException 
     */
    public DefaultTemporalNode(Map<String, ?> properties) throws IllegalArgumentException {
        super(properties);
    }

    
    
    @Override
    public Instant getRealization() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TemporalEdge getPreviousEdge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TemporalEdge getNextEdge() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
