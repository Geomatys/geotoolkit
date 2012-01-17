/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.util.ArgumentChecks;

/**
 *
 * @author marechal
 */
public final class Entry implements Bound{
    private final Object obj;
    private final Rectangle2D boundary;

    public Entry(final Object obj, final Rectangle2D boundary) {
        ArgumentChecks.ensureNonNull("Object ", obj);
        ArgumentChecks.ensureNonNull("Boundary", boundary);
        this.obj = obj;
        this.boundary = boundary;
    }

    @Override
    public Rectangle2D getBoundary() {
        return boundary;
    }

    public Object getObj() {
        return obj;
    }

    @Override
    public String toString() {
        String str = obj.toString()+" id = "+obj.hashCode();
        return str;
//        return obj.toString();
    }
    
    //pour le moment
    public void paint(Graphics2D g){
        g.setColor(Color.black);
        g.draw((Shape)obj);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Entry)){
            return false;
        }
        return (this.obj.equals(((Entry)obj).getObj()))&&(this.boundary.equals(((Entry)obj).getBoundary()));
    }
}
