/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

import java.util.Iterator;
import org.geotoolkit.index.tree.calculator.*;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create an abstract Tree.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Johann Sorel        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class AbstractTree implements Tree{

    protected NodeFactory nodefactory;
    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;
    protected int eltCompteur = 0;

    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    protected AbstractTree(int nbMaxElement, CoordinateReferenceSystem crs, NodeFactory nodefactory) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create NodeFactory : nodefactory", nodefactory);
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", nbMaxElement);
        this.calculator = new CalculatorND();
        this.nodefactory  = nodefactory;
        this.nbMaxElement = nbMaxElement;
        this.crs = crs;
    }

    @Override
    public void insert(Envelope entry) throws IllegalArgumentException {
        ArgumentChecks.ensureNonNull("insert : entry", entry);
        final int dim = entry.getDimension();
        for (int d = 0; d < dim; d++)
            if (Double.isNaN(entry.getMinimum(d)) || Double.isNaN(entry.getMaximum(d)))
                throw new IllegalArgumentException("entry Envelope contain at least one NAN value");
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
    public CoordinateReferenceSystem getCrs(){
        return crs;
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
    public NodeFactory getNodeFactory() {
        return this.nodefactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertAll(Iterator<? extends Envelope> itr) {
        while(itr.hasNext()) {
            insert((Envelope)itr.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll(Iterator<? extends Envelope> itr) {
        while(itr.hasNext()) {
            delete((Envelope)itr.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAll(Iterator<? extends Envelope> itr) {
        while(itr.hasNext()) {
            remove((Envelope)itr.next());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        setRoot(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElementsNumber() {
        return eltCompteur;
    }

    /**
     * {@inheritDoc}
     */
    public void setElementsNumber(int value) {
        this.eltCompteur = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Envelope getExtent() {
        final Node node = getRoot();
        if(node == null){
            return null;
        }else{
            return node.getBoundary();
        }
    }
}
