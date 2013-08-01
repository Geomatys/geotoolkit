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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.index.tree.calculator.*;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.Classes;
import org.geotoolkit.index.tree.access.TreeAccess;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.index.tree.mapper.TreeElementMapper;
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

    protected TreeAccess treeAccess;
    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;
    protected int eltCompteur;
    protected final TreeElementMapper<E> treeEltMap;
    protected int treeIdentifier;
    
    // search
    protected int currentLength;
    protected int currentPosition;
    protected int[] tabSearch;

    //dfebug
//    protected int countadjust;
    
    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    protected AbstractTree(TreeAccess treeAccess, CoordinateReferenceSystem crs, TreeElementMapper<E> treeEltMap) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create TreeAccess : treeAccess", treeAccess);
        ArgumentChecks.ensureNonNull("Create TreeElementMapper : treeEltMap", treeEltMap);
        this.treeAccess = treeAccess;
        this.treeEltMap = treeEltMap;
        this.calculator = new CalculatorND();
        this.nbMaxElement = treeAccess.getMaxElementPerCells();
        this.eltCompteur = treeAccess.getEltNumber();
        ArgumentChecks.ensureBetween("Create Tree : maxElements", 2, Integer.MAX_VALUE, nbMaxElement);
        this.crs = crs;
        //debug
//        this.countadjust = 0;
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
        try {
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
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract void insert(Object object, double... coordinates) throws StoreIndexException;
    
    @Override
    public boolean remove(E object) throws StoreIndexException {
        try {
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
        } catch (IOException ex) {
            throw new StoreIndexException(ex);
        }
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
            try {
               treeAccess.rewind();
            } catch (IOException ex) {
                throw new StoreIndexException("Impossible to rewind treeAccess during setRoot(null).", ex);
            }
            treeIdentifier = 1;
            eltCompteur = 0;
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
        return (node == null) ? null : node.getBoundary().clone();
    }
}
