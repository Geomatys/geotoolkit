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

import org.geotoolkit.index.tree.calculator.*;
import org.geotoolkit.index.tree.nodefactory.NodeFactory;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;

/**Create a "generic" Tree.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class DefaultAbstractTree implements Tree{

    protected NodeFactory nodefactory;
    private Node root;
    private final int nbMaxElement;
    protected CoordinateReferenceSystem crs;
    protected Calculator calculator;

    /**
     * To create an R-Tree use {@linkplain TreeFactory}.
     */
    protected DefaultAbstractTree(int nbMaxElement, CoordinateReferenceSystem crs, Calculator calculator, NodeFactory nodefactory) {
        ArgumentChecks.ensureNonNull("Create Tree : CRS", crs);
        ArgumentChecks.ensureNonNull("Create NodeFactory : nodefactory", nodefactory);
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", nbMaxElement);
        final CoordinateSystem cs = crs.getCoordinateSystem();
        if(!(cs instanceof CartesianCS)&&!(cs instanceof SphericalCS)&&!(cs instanceof EllipsoidalCS)){
            throw new IllegalArgumentException("Tree constructor : invalid crs, it isn't Cartesian, Spherical, or Ellipsoidal "+cs);
        }
        final boolean isCartesian = (cs instanceof CartesianCS)?true:false;
        final int dim = cs.getDimension();
        double radius = 0;
        if(cs instanceof EllipsoidalCS){
            final Ellipsoid ell = ((GeodeticDatum)((SingleCRS)cs).getDatum()).getEllipsoid();
            double semiMinorAxis = ell.getSemiMinorAxis();
            double semiMajorAxis = ell.getSemiMajorAxis();
            semiMinorAxis*=semiMinorAxis;
            semiMajorAxis*=semiMajorAxis;
            final double e = (semiMajorAxis - semiMinorAxis)/semiMajorAxis;
            radius = Math.sqrt(semiMajorAxis/2+semiMinorAxis/4*Math.log((1+e)/(1-e))/e);
        }
        final int[] dims = new int[dim];
        AxisDirection ad;
        for(int i = 0;i<dim;i++){
            ad = cs.getAxis(i).getDirection();
            if(ad.compareTo(AxisDirection.EAST) == 0 || ad.compareTo(AxisDirection.WEST) == 0){
                dims[0] = i;
            }else if(ad.compareTo(AxisDirection.NORTH) == 0 || ad.compareTo(AxisDirection.SOUTH) == 0){
                dims[1] = i;
            }else{
                dims[2] = i;
            }
        }
        if(calculator == null){
            if(isCartesian){
                switch(dim){
                    case 2 : this.calculator = DefaultCalculator.CALCULATOR_2D;break;
                    case 3 : this.calculator = DefaultCalculator.CALCULATOR_3D;break;
                    default : throw new IllegalArgumentException("CoordinateSystem dimension from CRS is not conform");
                }
            }else{
                this.calculator = new GeoCalculator(radius, dims);
            }
        }else{
            final String strClash = "Clash between CoordinateSystem and calculator. CoordinateSystem : "+cs.getClass().getName()
                                    +" Calculator : "+calculator.getClass().getName();
            if(calculator instanceof Calculator2D){
                if(cs.getDimension() !=2){
                    throw new IllegalArgumentException(strClash);
                }
            }else if(calculator instanceof Calculator3D){
                if(cs.getDimension() !=3){
                    throw new IllegalArgumentException(strClash);
                }
            }
            this.calculator = calculator;
        }
        this.nodefactory = nodefactory;
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
}
