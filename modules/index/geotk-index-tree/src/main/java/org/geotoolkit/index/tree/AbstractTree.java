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

import java.io.IOException;
import org.geotoolkit.index.tree.calculator.*;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.io.TreeElementMapper;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create an abstract Tree.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Johann Sorel        (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class AbstractTree<E> implements Tree<E> {

    protected NodeFactory nodefactory;
    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;
    protected int eltCompteur = 0;
    protected final TreeElementMapper<E> treeEltMap;
    private int treeIdentifier;
    
    // search
    protected int currentLength;
    protected int currentPosition;
    protected int[] tabSearch;

    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    @Deprecated
    protected AbstractTree(int nbMaxElement, CoordinateReferenceSystem crs, NodeFactory nodefactory, TreeElementMapper<E> treeEltMap) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create NodeFactory : nodefactory", nodefactory);
        ArgumentChecks.ensureBetween("Create Tree : maxElements", 2, Integer.MAX_VALUE, nbMaxElement);
        ArgumentChecks.ensureNonNull("Create TreeElementMapper : treeEltMap", treeEltMap);
        this.treeEltMap = treeEltMap;
        this.calculator = new CalculatorND();
        this.nodefactory  = nodefactory;
        this.nbMaxElement = nbMaxElement;
        this.crs = crs;
        treeIdentifier = 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] searchID(Envelope regionSearch) throws StoreIndexException {
        return searchID(DefaultTreeUtils.getCoords(regionSearch));
    }
    
    public abstract int[] searchID(double[] regionSearch) throws StoreIndexException ;
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(E object) throws IllegalArgumentException , StoreIndexException{
        ArgumentChecks.ensureNonNull("insert : object", object);
        final Envelope env = treeEltMap.getEnvelope(object);
        if (!CRS.equalsIgnoreMetadata(crs, env.getCoordinateReferenceSystem()))
            throw new IllegalArgumentException("During insertion element should have same CoordinateReferenceSystem as Tree.");
        final double[] coordinates = DefaultTreeUtils.getCoords(env);
        for (double d : coordinates)
            if (Double.isNaN(d))
                throw new IllegalArgumentException("coordinates contain at least one NAN value");
        treeEltMap.setTreeIdentifier(object, treeIdentifier);
        insert(treeIdentifier, coordinates);
        treeIdentifier++;
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract void insert(Object object, double... coordinates) throws StoreIndexException;
    
    @Override
    public boolean remove(E object) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("insert : object", object);
        final Envelope env = treeEltMap.getEnvelope(object);
        if (!CRS.equalsIgnoreMetadata(crs, env.getCoordinateReferenceSystem()))
            throw new IllegalArgumentException("During insertion element should have same CoordinateReferenceSystem as Tree.");
        final double[] coordinates = DefaultTreeUtils.getCoords(env);
        for (double d : coordinates)
            if (Double.isNaN(d))
                throw new IllegalArgumentException("coordinates contain at least one NAN value");
        final int treeID = treeEltMap.getTreeIdentifier(object);
        return remove(treeID, coordinates);
    }
    
    protected abstract boolean remove(Object object, double... coordinates) throws StoreIndexException;
    
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
    public void setRoot(Node root) throws StoreIndexException{
        this.root = root;
        if (root == null) {
//            treeEltMap.clear();
            treeIdentifier = 1;
        }
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
    public void clear() throws StoreIndexException {
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

    @Override
    public TreeElementMapper getTreeElementMapper() {
        return treeEltMap;
    }
    
    @Override
    public void close() throws StoreIndexException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double[] getExtent() throws StoreIndexException {
        final Node node = getRoot();
        if (node == null) {
            return null;
        } else {
            try {
                return node.getBoundary().clone();
            } catch (IOException ex) {
                throw new StoreIndexException("abstract Tree : "+this.getClass().getName()+ " impossible to read root node boundary.", ex);
            }
        }
    }
}
