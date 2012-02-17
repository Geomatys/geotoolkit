/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;

/**Create a "generic" Tree.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class DefaultAbstractTree implements Tree<DefaultNode, GeneralEnvelope>{

    private DefaultNode root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;

    protected DefaultAbstractTree(int nbMaxElement, CoordinateReferenceSystem crs) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", nbMaxElement);
        if(!(crs.getCoordinateSystem() instanceof CartesianCS)){
            throw new IllegalArgumentException("Tree constructor : invalid crs");
        }
        this.nbMaxElement = nbMaxElement;
        this.crs = crs;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int getMaxElements() {
        return this.nbMaxElement;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public DefaultNode getRoot() {
        return this.root;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setRoot(DefaultNode root) {
        this.root = root;
    }
}
