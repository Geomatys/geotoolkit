/*
 * Map and oceanographical data visualisation
 * Copyright (C) 1999 Pêches et Océans Canada
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details (http://www.gnu.org/).
 *
 *
 * Contact: Observatoire du Saint-Laurent
 *          Institut Maurice Lamontagne
 *          850 de la Mer, C.P. 1000
 *          Mont-Joli (Québec)
 *          G5H 3Z4
 *          Canada
 *
 *          mailto:osl@osl.gc.ca
 */
package org.geotoolkit.process.coverage.kriging;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.util.*;
import javax.vecmath.Point3d;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.util.ArgumentChecks;

/**
 * <p>Search and compute Isoline on a {@link RenderedImage} or an
 * object given by {@link PixelIterator}.<br/><br/>
 *
 * Uses example :<br/>
 * final  {@link RenderedImage} ri = user image.<br/>
 * final double[] isolineIntervals = new double[]{10, 50, 100};//for example<br/>
 * {@link PixelIterator} pixelIterator = {@link PixelIteratorFactory#createDefaultIterator(ri)}; <br/><br/>
 *
 * {@link IsolineCreator} isolineContour    = new IsolineCreator(pixelIterator, isolineIntervals);<br/>
 * or {@link IsolineCreator} isolineContour = new IsolineCreator(ri, isolineIntervals);<br/><br/>
 *
 * final {@link Map}&lt;{@link Point3d},List&lt;{@link Coordinate}&gt;&gt; isoline = {@link #createIsolines() };<br/><br/>
 *
 * Note : Caller will choose constructor with {@link PixelIterator} parameter
 * if he search isoline within an area pre-define in {@link PixelIterator}.<br/>
 * Otherwise caller can use constructor with {@link RenderedImage} which find isoline in all image area.</p>
 *
 * @version 1.1
 * @author Martin Desruisseaux
 * @author Howard Freeland
 * @author Johann Sorel (adaptation isoligne et mise a jour sur geotoolkit)
 * @author Rémi Marechal (Geomatys).
 * @module pending
 */
public class IsolineCreator {

    private static final double EPS = 1E-8;

    /**
     * Isoline intervals.
     */
    private final double[] lls;

    /**
     * Iterator use to compute isoline.
     */
    private final PixelIterator pixelIterator;

    /**
     * Area where caller search isoline.
     */
    private final Rectangle areaIterate;

    /**
     * Define isoline on object that iterate by {@link PixelIterator}.
     *
     * @param pixelIterator must be instance of RowMajor iterator.
     * @param isolineLevel Isoline levels.
     */
    public IsolineCreator(final PixelIterator pixelIterator, final double[] isolineLevel) {

        // faire une verif pixel iterator de type row major
        ArgumentChecks.ensureNonNull("pixelIterator", pixelIterator);
        ArgumentChecks.ensureNonNull("isoline interval", isolineLevel);
        if (pixelIterator.getNumBands() != 1)
            throw new IllegalArgumentException("image not conform, number of bands exceed 1");
        this.pixelIterator = pixelIterator;
        this.areaIterate   = pixelIterator.getBoundary(true);
        this.lls = isolineLevel;
    }

    /**
     * Define isoline on {@link RenderedImage}.
     *
     * @param renderedImage Image where caller search isoline.
     * @param lls Isoline intervals.
     */
    public IsolineCreator(final RenderedImage renderedImage, final double[] lls) {
        this(PixelIteratorFactory.createRowMajorIterator(renderedImage), lls);
    }

    /**
     * <p>Create a contour plot of {@link RenderedImage} or object iterate from {@link PixelIterator}.<br/><br/>
     *
     * The contouring result will be stored in the {@link #contour} field. If
     * this field was null, a new {@link ca.dfo.map.Bathymetry} object will be
     * created. Subclass may override this method to perform the contouring in an
     * other way, or to create a different instance of <code>Bathymetry</code> if
     * the <code>bathymetry</code> field was null.</p>
     *
     * @return Isoline {@link Map}.
     * @throws IllegalStateException
     */
    public Map<Point3d, List<Coordinate>> createIsolines() throws IllegalStateException {

        final Map<Point3d,List<Coordinate>> cellMapResult = new HashMap<Point3d,List<Coordinate>>();

        final double x[] = new double[4];
        final double y[] = new double[4];
        final double z[] = new double[4];
        final Coordinate toMerge[] = new Coordinate[2];

        final int minX = areaIterate.x;
        final int minY = areaIterate.y;
        final int areaWidth = areaIterate.width;
        final int areaHeight = areaIterate.height;
        final int xlength = minX + areaWidth - 1;
        final int ylength = minY + areaHeight - 1;

        for (int i = minX; i<xlength; i++) {
            /*
             * Obtient les coordonnées des quatre coins    [0]...[3]
             * de la cellule à tracer. Les coordonnées      :     :
             * seront mémorisés aux index suivants:        [1]...[2]
             */
            x[0] = x[1] = i;
            x[2] = x[3] = i+1;

            for (int j = minY; j<ylength; j++) {
                y[0] = y[3] = j;
                y[1] = y[2] = j+1;

//                pixelIterator.moveTo((int)x[0],(int)y[0], 0);
//                pixelIterator.next();
//                z[0] = pixelIterator.getSampleDouble();
//                pixelIterator.moveTo((int)x[1],(int)y[1], 0);
//                pixelIterator.next();
//                z[1] = pixelIterator.getSampleDouble();
//                pixelIterator.moveTo((int)x[3],(int)y[3], 0);
//                pixelIterator.next();
//                z[3] = pixelIterator.getSampleDouble();
//                pixelIterator.moveTo((int)x[2],(int)y[2], 0);
//                pixelIterator.next();
//                z[2] = pixelIterator.getSampleDouble();

                pixelIterator.moveTo((int)x[0],(int)y[0], 0);
//                pixelIterator.next();
                z[0] = pixelIterator.getSampleDouble();
                pixelIterator.next();
                z[3] = pixelIterator.getSampleDouble();
                pixelIterator.moveTo((int)x[1],(int)y[1], 0);
//                pixelIterator.next();
                z[1] = pixelIterator.getSampleDouble();
                pixelIterator.next();
                z[2] = pixelIterator.getSampleDouble();

                /*
                 * Obtient les valeurs extrêmes de z: zmin et zmax.
                 * Par la suite, on balayera les valeurs zl de toutes
                 * les courbes de niveaux qui passent entre ces deux
                 * bornes. Par exemple si zmin = 8.76 et zmax=11.35,
                 * alors on balayera (par défaut) zl = 9, 10 et 11.
                 */
                double zmin = Double.POSITIVE_INFINITY;
                double zmax = Double.NEGATIVE_INFINITY;
                for (int k=0; k<4; k++) {//4 pour z.lenght
                    if (z[k] < zmin) zmin = z[k];
                    if (z[k] > zmax) zmax = z[k];
                }
                //zl = hauteur de courbe de niveau courante
                for(double zline : lls){
                    if (zline > zmax || zline < zmin) {
                        // la cellule ne contient pas ce niveau
                        continue;
                    }

                    final boolean isDone[] = new boolean[4];

                    while (true) {
                        int kmin = -1;
                        int mmin = -1;
                        double px0 = Double.NaN;
                        double py0 = Double.NaN;
                        double px1 = Double.NaN;
                        double py1 = Double.NaN;
                        double d2min = Double.POSITIVE_INFINITY;
                        /*
                         * Pour chacun des côtés de la cellule, on determine le point
                         * d'intersection entre la courbe de niveau et le côté
                         * de la cellule. Seuls les côtés qui n'ont pas déjà été
                         * traités lors d'un passage précèdent (!isDone) seront
                         * examinés.
                         */
                        for (int k0=0; k0<4; k0++) {
                            if (!isDone[k0]) {
                                final int k1 = (k0+1) % 4;
                                if ((zline >= z[k0] && zline <= z[k1]) || (zline >= z[k1] && zline <= z[k0])) {
                                    //Ce coté contient la valeur de la courbe de niveau
                                    double tmp;
                                    if (z[k1] == z[k0]) {
                                        tmp = (x[k0]-x[k1])*(x[k0]-x[k1])+(y[k0]-y[k1])*(y[k0]-y[k1]);//Math.pow(x[k0]-x[k1], 2) + Math.pow(y[k0]-y[k1], 2);
                                        if (tmp < d2min) {
                                            d2min = tmp;
                                            kmin = k0;
                                            mmin = k0;
                                            px0 = x[k0];
                                            px1 = x[k1];
                                            py0 = y[k0];
                                            py1 = y[k1];
                                        }
                                    } else {
                                        tmp = (zline-z[k0]) / (z[k1]-z[k0]);
                                        final double tx0 = x[k0] + tmp * (x[k1] - x[k0]);
                                        final double ty0 = y[k0] + tmp * (y[k1] - y[k0]);
                                        /*
                                         * (tx0,ty0) contient le point d'intersection de la courbe zline
                                         * avec le côté k0. On recherche maintenant un point
                                         * d'intersection avec un autre côté. La paire de points
                                         * (tx0,ty0) - (tx1,ty1) qui donnent la distance la plus courte
                                         * sera retenue comme la paire à tracer.
                                         */
                                        for (int m0 = 0; m0 < 4; m0++) {
                                            if (m0 != k0 && !isDone[m0]) {
                                                final int m1 = (m0 + 1) % 4;
                                                if ((zline >= z[m0] && zline <= z[m1]) || (zline >= z[m1] && zline <= z[m0])) {
                                                    tmp = (zline - z[m0]) / (z[m1] - z[m0]);
                                                    final double tx1 = x[m0] + tmp * (x[m1] - x[m0]);
                                                    final double ty1 = y[m0] + tmp * (y[m1] - y[m0]);
//                                                    tmp = Math.pow(tx0 - tx1, 2) + Math.pow(ty0 - ty1, 2);
                                                    tmp = (tx0 - tx1)*(tx0 - tx1)+(ty0 - ty1)*(ty0 - ty1);// Math.pow(tx0 - tx1, 2) + Math.pow(ty0 - ty1, 2);
                                                    if (tmp < d2min) {
                                                        d2min = tmp;
                                                        kmin = k0;
                                                        mmin = m0;
                                                        px0 = tx0;
                                                        py0 = ty0;
                                                        px1 = tx1;
                                                        py1 = ty1;
                                                    }
                                                } else {
                                                    isDone[m0] = true;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    isDone[k0] = true;
                                }
                            }
                        }
                        /*
                         * Maintenant que l'on dispose des coordonnées d'une ligne
                         * droite, ajoute ces coordonnées à la "bathymetrie" que
                         * l'on est en train de construire. Si aucune coordonnées
                         * n'ont été trouvées, c'est qu'on en a fini avec cette cellule
                         * pour ce niveau. On passera alors au niveau suivant.
                         */
                        if (kmin >= 0 && mmin >= 0) {
                            isDone[kmin] = true;
                            isDone[mmin] = true;
                            toMerge[0] = new Coordinate(px0, py0);
                            toMerge[1] = new Coordinate(px1, py1);

                            /*
                             * Vérifie si les points d'intersections (px0, py0) et/ou (px1, py1) ont
                             * déjà été trouvés avant. Si c'est le cas, le segment de ligne (px0,py0)
                             * (px1, py1) devra venir se rattacher aux segments déjà existants. Sinon,
                             * on créera une nouvelle ligne à laquelle viendra peut-être s'en rattacher
                             * d'autres plus tard. Note: La présence du (float) est une façon paresseuse
                             * d'arrondir les nombres de façon à éviter certaines erreurs d'arrondissements.
                             */
                            final Point3d P0 = new Point3d((float) px0, (float) py0, zline);
                            final Point3d P1 = new Point3d((float) px1, (float) py1, zline);
                            final List<Coordinate> I0 = cellMapResult.remove(P0);
                            final List<Coordinate> I1 = cellMapResult.remove(P1);
                            /*
                             * Si aucun des points d'intersections n'avaient été trouvés avant,
                             * créé un nouvel {@link Polygon}. Sinon, procéde à des fusions...
                             */
                            if (I0 == null && I1 == null) {
                                final List<Coordinate> polylines = new ArrayList<Coordinate>();
                                polylines.add(toMerge[0]);
                                polylines.add(toMerge[1]);
                                cellMapResult.put(P0, polylines);
                                cellMapResult.put(P1, polylines);

                            }else if (I0 == null && I1 != null) {
                                merge(toMerge, I1);
                                cellMapResult.put(P0, I1);

                            }else if (I0 != null && I1 == null) {
                                merge(toMerge, I0);
                                cellMapResult.put(P1, I0);

                            }else if (I0 != null && I1 != null) {

                                if(!merge(toMerge, I1)){
                                    merge(toMerge,I0);
                                }

                                if (I0 != I1) {
                                    merge(I1, I0);

                                    /*
                                     * Puisque I1 a été fusionné à I0 et qu'on ne gardera plus
                                     * que I0, il faut maintenant changer les références vers
                                     * I1 en référence vers I0. Il ne reste normalement qu'une
                                     * référence vers I1 et une référence vers I0. On le vérifiera
                                     * pour s'assurer que l'algorithme n'a pas de bug.
                                     */
                                    int checkRefCountI0 = 0;
                                    int checkRefCountI1 = 0;
                                    final Iterator<Map.Entry<Point3d,List<Coordinate>>> it = cellMapResult.entrySet().iterator();
                                    if (it != null) {
                                        while (it.hasNext()) {
                                            final Map.Entry<Point3d,List<Coordinate>> entrie = it.next();
                                            final Object I = entrie.getValue();
                                            if (I == I0) {
                                                checkRefCountI0++;
                                            }
                                            if (I == I1) {
                                                entrie.setValue(I0);
                                                checkRefCountI1++;
                                            }
                                        }
                                    }
                                    /*
                                     *@TODO maybe this piece of code should be rewrited
                                     * See issue GEOTK-196 on http://dev.geomatys.com for more informations
                                     */
//                                    if (checkRefCountI0 != 1) {
//                                        throw new IllegalStateException("should not happen");
//                                    }
//                                    if (checkRefCountI1 != 1) {
//                                        throw new IllegalStateException("should not happen");
//                                    }
                                } else {
                                    /*
                                     * Si on vient de refermer une cle (I0==I1), alors il ne
                                     * reste plus de référence vers celle-ci. On en créera une
                                     * avec un point bidon, choisie de façon à être innacessible
                                     * par le reste de cette méthode.
                                     */
                                    P0.add(P1);
                                    P0.scale(0.5);
                                    P0.z = zline;
                                    cellMapResult.put(P0, I0);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return cellMapResult;
    }

    private boolean merge(final Coordinate[] toMerge,final List<Coordinate> coords){
        Coordinate coord0 = toMerge[0];
        Coordinate coord1 = toMerge[1];

        Coordinate startCoord = coords.get(0);
        Coordinate endCoord = coords.get(coords.size()-1);

        if(equalCoordinates(startCoord, coord0)){
            //add at the begining
            coords.add(0,coord1);
        }else if(equalCoordinates(startCoord, coord1)){
            //add at the begining
            coords.add(0,coord0);
        }else if(equalCoordinates(endCoord, coord0)){
            //add at the end
            coords.add(coord1);
        }else if(equalCoordinates(endCoord, coord1)){
            //add at the end
            coords.add(coord0);
        }else{
            return false;
        }
        return true;
    }

    private boolean merge(final List<Coordinate> toMerge,final List<Coordinate> coords){
        Coordinate coord0 = toMerge.get(0);
        Coordinate coord1 = toMerge.get(toMerge.size()-1);

        Coordinate startCoord = coords.get(0);
        Coordinate endCoord = coords.get(coords.size()-1);

        if(equalCoordinates(startCoord, coord0)){
            //add at the begining
            toMerge.remove(0);
            Collections.reverse(toMerge);
            coords.addAll(0,toMerge);
        }else if(equalCoordinates(startCoord, coord1)){
            //add at the begining
            toMerge.remove(toMerge.size()-1);
            coords.addAll(0,toMerge);
        }else if(equalCoordinates(endCoord, coord0)){
            //add at the end
            toMerge.remove(0);
            coords.addAll(toMerge);
        }else if(equalCoordinates(endCoord, coord1)){
            //add at the end
            toMerge.remove(toMerge.size()-1);
            Collections.reverse(toMerge);
            coords.addAll(toMerge);
        }else{
            return false;
        }
        return true;
    }

    /**
     * Compare two {@link Coordinate}.
     *
     * @param coord0
     * @param coord1
     * @return true if the two coordinates are considered equals else false.
     */
    private boolean equalCoordinates(final Coordinate coord0, final Coordinate coord1){

        //test x values
        if (Math.abs(coord0.x - coord1.x) > EPS * Math.max(Math.abs(coord0.x), Math.abs(coord1.x))) {
            return false;
        }
        if (Math.abs(coord0.y - coord1.y) > EPS * Math.max(Math.abs(coord0.y), Math.abs(coord1.y))) {
            return false;
        }
        return true;
    }
}
