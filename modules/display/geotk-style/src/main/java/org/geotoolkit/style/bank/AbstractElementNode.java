/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.style.bank;

import java.lang.ref.SoftReference;
import org.geotoolkit.gui.swing.tree.DefaultMutableTreeNode;
import org.opengis.style.Description;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractElementNode extends DefaultMutableTreeNode implements ElementNode {

    private final String name;
    private final Description desc;
    private final ElementType type;
    private SoftReference ref = null;

    public AbstractElementNode(final String name, final Description desc, final ElementType type) {
        this.name = name;
        this.desc = desc;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Description getDescription() {
        return desc;
    }

    @Override
    public ElementType getType() {
        return type;
    }

    protected abstract Object createUserObject();

    @Override
    public synchronized Object getUserObject() {
        Object uo = null;

        if(ref != null){
            uo = ref.get();
        }

        if(uo == null){
            uo = createUserObject();
            ref = new SoftReference(uo);
        }

        return uo;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractElementNode other = (AbstractElementNode) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }



}
