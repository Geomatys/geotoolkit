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
package org.geotoolkit.index.tree.hilbert;

import java.util.Arrays;

/**To generate Hilbert curve in n-dimension space in n-order.
 *
 * @author Martin Desruisseaux (Geomatys).
 * @author Rémi Maréchal (Geomatys).
 */
public class Hilbert {

    private static final double LN2 = 0.6931471805599453;
    final int dimension;
    final int[]coordinates;
    final int nbCells;
    final int[] basicPath;
    int compteurPathBis = 0;
    boolean[] currentSign;
    final int l;

    /**Create an Hilbert object to generate appropriate Hilbert curve in n-dimension space.
     *
     * @param dimension ordinate number of space in which create Hilbert curve.
     * @param coords table to fill with Hilbert curve coordinates.
     */
    public Hilbert(int dimension, int ...coords ) {
        assert dimension >= 2 : "generate an Hilbert curve in space with lesser 2 dimension have no sens : dimension = "+dimension;
        this.dimension = dimension;
        this.coordinates = coords;
        final int length = coords.length;
        assert length % dimension == 0 : " coords length is not congruous to dimension : length = "+length+" dim = "+dimension;
        this.nbCells = length/dimension;
        assert (Integer.bitCount(nbCells) == 1) : " iterate number is not congruous to 2 : iterate number = "+nbCells;
        currentSign = new boolean[dimension];
        Arrays.fill(currentSign, true);
        basicPath = generateBasicPath(dimension-1, new int[]{dimension-1, dimension-2, dimension-1});
        l = (2<<dimension-1)-1;
        computePath();
    }

    /**
     * @return Hilbert order computed from dimension and coordinate table length.
     */
    private int getOrder(){
        return (int) Math.round(Math.log(nbCells)/(dimension*LN2));
    }

    /**Compute an dimension index array.
     * Array represent ordinate path followed by Hilbert curve.
     *
     * @param currentlyTab dimension index array to iterate.
     * @param remainingOrder current Hilbert order during iteration.
     * @param addToOrdinate Hilbert ordinate path result.
     * @param addToSign sign following by Hilbert curve path.
     * @return dimension index array.
     */
    private void iteratePath(final int[] currentlyTab,  final int remainingOrder,
                             final int[] addToOrdinate, final boolean[]addToSign) {
        int curTabi;
        if (remainingOrder == 1) {
            for(int i = 0;i<l;i++){
                curTabi = currentlyTab[i];
                addToOrdinate[compteurPathBis] = curTabi;
                addToSign[compteurPathBis] = currentSign[curTabi];
                currentSign[curTabi]=!currentSign[curTabi];
                compteurPathBis++;
            }
            return;
        }
        final int size = (2<<(dimension*remainingOrder-1))-1;
        assert addToOrdinate.length >= size : "addToOrdinate table is not enought longer : "+addToOrdinate.length+" expected min longer : "+size;
        assert addToSign.length >= size     : "addToSign table is not enought longer     : "+addToSign.length+" expected min longer : "+size;
        int operande,compteurJoint = 0;
        int[] tabTemp;
        for (int j = 0; j<l; j++) {
            for(int i = 0;i<((j!=l-1&&j!=0)?2:1);i++){
                operande = basicPath[j];
                tabTemp = currentlyTab.clone();
                oLogic(tabTemp, operande);
                iteratePath(tabTemp, remainingOrder-1, addToOrdinate, addToSign);
                if(j != l-1){
                    curTabi = currentlyTab[compteurJoint];
                    addToOrdinate[compteurPathBis] = curTabi;
                    currentSign[curTabi] = !currentSign[curTabi];
                    addToSign[compteurPathBis] = currentSign[curTabi];
                    compteurPathBis++;
                    compteurJoint++;
                }
            }
            if(j!=l-2&&j!=0)j++;
        }
    }

    /**Operation on all tab values ordinate.
     *
     * @param tab int table which contain ordinate Hilbert curve path piece.
     * @param operande operation on tab values.
     */
    private void oLogic(int[] tab, final int operande){
        for(int i = 0;i<l;i++){
            tab[i] = Math.abs((tab[i] + operande)%dimension);
        }
    }

    /**Create appropriate first Hibert curve path iteration.
     *
     * @param remainingDim space dimension.
     * @param currentlyPath
     * @return order 1 Hilbert curve path.
     */
    private int[] generateBasicPath(int remainingDim, int[] currentlyPath){
        if(remainingDim == 1){
            return currentlyPath;
        }
        final int length = currentlyPath.length;
        final int size = 2*length+1;
        final int[] path = new int[size];
        System.arraycopy(currentlyPath.clone(), 0, path, 0, length);
        path[length] = remainingDim-2;
        System.arraycopy(currentlyPath.clone(), 0, path, length+1, length);
        return generateBasicPath(remainingDim-1, path);
    }

    /**Compute Hilbert curve and fill coordinate array.
     *
     * Curve is generate from dimension array and sign array.
     * @see #getPath(int) : generate dimension array.
     * @see #getSign(int) : generate sign array.
     */
    private void computePath(){
        int[] departure = new int[dimension];
        System.arraycopy(departure.clone(), 0, coordinates, 0, dimension);
        int destPos = dimension;
        final int size = (2<<getOrder()*dimension-1)-1;
        final int[] addToOrdinate = new int[size];
        final boolean[] addToSign = new boolean[size];
        iteratePath(basicPath, getOrder(), addToOrdinate, addToSign);
        for(int i = 0; i<size; i++){
            final int val = (addToSign[i])?1:-1;
            final int dim = addToOrdinate[i];
            departure[dim] = departure[dim]+val;
            System.arraycopy(departure.clone(), 0, coordinates, destPos, dimension);
            destPos+=dimension;
        }
    }

    /**Compute and generate Hilbert curve path coordinates array.
     *
     * @param dimension ordinate number of space in which create Hilbert curve.
     * @param order Hilbert order of Hilbert curve asked.
     * @return Hilbert curve path.
     */
    public static int[] createPath(int dimension, int order){
        final int[] path = new int[(2<<(dimension*order)-1)*dimension];
        final Hilbert hilb = new Hilbert(dimension, path);
        return path;
    }

}
