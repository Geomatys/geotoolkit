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

import org.geotoolkit.index.tree.calculator.Calculator;
import org.geotoolkit.index.tree.calculator.Calculator2D;
import org.geotoolkit.index.tree.calculator.Calculator3D;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem;

/**Create a "generic" Tree.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class DefaultAbstractTree implements Tree<Node>{

    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;

    protected DefaultAbstractTree(int nbMaxElement, CoordinateReferenceSystem crs, Calculator calculator) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create Tree : Calculator", calculator);
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", nbMaxElement);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        if(!(cs instanceof CartesianCS)){
            throw new IllegalArgumentException("Tree constructor : invalid crs");
        }
        final String strClash = "Clash between CoordinateSystem and calculator. CoordinateSystem dimension = "+cs.getDimension();
        if(calculator instanceof Calculator2D){
            if(cs.getDimension() !=2){
                throw new IllegalArgumentException(strClash+" calculator dimension = 2");
            }
        }else if(calculator instanceof Calculator3D){
            if(cs.getDimension() !=3){
                throw new IllegalArgumentException(strClash+" calculator dimension = 3");
            }
        }
        this.calculator = calculator;
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
    public Node getRoot() {
        return this.root;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setRoot(Node root) {
        this.root = root;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public Calculator getCalculator() {
        return this.calculator;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }
}
