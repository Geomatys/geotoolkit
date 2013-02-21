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
import javax.measure.unit.Unit;
import org.geotoolkit.index.tree.calculator.*;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;

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
    protected final int[]dims;
    protected final int spatialDimension;
    protected int eltCompteur = 0;

    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    protected AbstractTree(int nbMaxElement, CoordinateReferenceSystem crs, NodeFactory nodefactory) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create NodeFactory : nodefactory", nodefactory);
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", nbMaxElement);
        CoordinateReferenceSystem currentlyCrs = null;
        int tempOrdinate = 0;
        if(crs instanceof CompoundCRS) {
            for(CoordinateReferenceSystem ccrrss : ((CompoundCRS)crs).getComponents()) {
                final CoordinateSystem cs = ccrrss.getCoordinateSystem();
                if((cs instanceof CartesianCS) || (cs instanceof SphericalCS) || (cs instanceof EllipsoidalCS)) {
                    currentlyCrs = ccrrss;
                    break;
                }
                tempOrdinate += cs.getDimension();
            }
            if(currentlyCrs == null) throw new IllegalArgumentException("Tree constructor compoundCrs: "
                                        + "Cartesian, Spherical, or Ellipsoidal CoordinateSystem not find "+crs);
        }else{
            currentlyCrs = crs;
        }
        final CoordinateSystem cs = currentlyCrs.getCoordinateSystem();
        if( !(cs instanceof CartesianCS) && !(cs instanceof SphericalCS) && !(cs instanceof EllipsoidalCS))
            throw new IllegalArgumentException("Tree constructor : invalid crs, it isn't Cartesian, Spherical, or Ellipsoidal "+ cs);

        final boolean isCartesian = (cs instanceof CartesianCS) ? true : false;
        spatialDimension = cs.getDimension();
        dims = new int[spatialDimension];
        double radius = 0;
        CoordinateSystemAxis csa;
        AxisDirection ad;
        for(int i = 0; i<spatialDimension; i++) {
            csa = cs.getAxis(i);
            ad = csa.getDirection();
            if(ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0){
                dims[0] = i + tempOrdinate;
            }else if(ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0){
                dims[1] = i + tempOrdinate;
            }else{
                dims[2] = i + tempOrdinate;
            }
        }
        if (isCartesian) {
            Unit unit = cs.getAxis(dims[0]-tempOrdinate).getUnit();
            for (int i = 1; i<spatialDimension; i++) {
                if(!unit.equals(cs.getAxis(dims[i]-tempOrdinate).getUnit()))
                    throw new IllegalArgumentException("axis "+i+"from cartesian space is not in same Unit from other axis"
                                                      +"expected : "+unit+" find : "+cs.getAxis(dims[i]-tempOrdinate).getUnit());
            }
            switch (spatialDimension) {
                case 2 : this.calculator = new Calculator2D(dims); break;
                case 3 : this.calculator = new Calculator3D(dims); break;
                default : throw new IllegalArgumentException("CoordinateSystem dimension from CRS is not conform");
            }
        }else{
            if (cs instanceof EllipsoidalCS) {
                final Ellipsoid ell = ((GeodeticDatum)((SingleCRS)currentlyCrs).getDatum()).getEllipsoid();
                double semiMinorAxis = ell.getSemiMinorAxis();
                double semiMajorAxis = ell.getSemiMajorAxis();
                semiMinorAxis *= semiMinorAxis;
                semiMajorAxis *= semiMajorAxis;
                final double e = (semiMajorAxis - semiMinorAxis)/semiMajorAxis;
                radius = Math.sqrt(semiMajorAxis/2 + semiMinorAxis/4 * Math.log((1+e)/(1-e)) /e);
            }
            if (!cs.getAxis(dims[0]-tempOrdinate).getUnit().equals(cs.getAxis(dims[1]-tempOrdinate).getUnit()))
                throw new IllegalArgumentException("longitude and latitude are not in same unit."
                                                    +"longitude unit : "+ cs.getAxis(dims[0]-tempOrdinate).getUnit()
                                                    +"latitude unit : " + cs.getAxis(dims[1]-tempOrdinate).getUnit());
            switch (spatialDimension) {
                case 2  : this.calculator = new GeoCalculator2D(radius, dims); break;
                case 3  : this.calculator = new GeoCalculator3D(radius, dims); break;
                default : throw new IllegalArgumentException("CoordinateSystem dimension from CRS is not conform");
            }
        }
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

    /**
     * @return used ordinate sequence.
     */
    public int[]getDims() {
        return dims;
    }
}
