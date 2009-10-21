/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.legacy.geom;


/**
 * Paire d'objets {@link Polyline} � fusionner ensemble. Cet objet contient deux r�f�rences
 * vers deux objets {@link Polyline}, d�sign�s {@link #i} et {@link #j}. Les
 * champs <code>[i/j].mergeEnd</code> indiquent de quelle fa�on il faut fusionner ces
 * segments (par exemple faut-il ajouter <var>i</var> � la fin de <var>j</var> ou
 * l'inverse?).
 * <p>
 * Pour les amateurs de physique quantique, vous pouvez voir un objet <code>FermionPair</code>
 * comme une paire de Fermions. Tout comme il ne peut pas y avoir deux Fermions au m�me niveau
 * avec le m�me spin, on ne doit pas avoir nulle part dans la liste deux pointeurs
 * <code>[i/j].path</code> identiques associ�s � la m�me valeur bool�enne <code>[i/j].mergeEnd</code>.
 *
 * @version $Id: FermionPair.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
final class FermionPair {
    /**
     * Pointeur vers les polylignes � fusionner. L'ordre dans laquel ces polylignes seront
     * fusionn�s <u>n'est pas</u> d�termin� par cet objet. La d�cision d'ajouter
     * <code>i.path</code> � la fin de <code>j.path</code> ou inversement est laiss�e aux
     * m�thode utilisant ces objets.
     * <p>
     * Les champs <code>mergeEnd</code> indiquent de quelle fa�on les polylignes peuvent �tre
     * fusionn�s. Il peut arriver qu'il faille inverser l'ordre des donn�es d'un des polylignes.
     * Si un des ces champs a la valeur <code>true</code>, cela signifie que la distance
     * <code>minDistance</code> est mesur�e par rapport � la fin de ce polyligne. Sinon, elle est
     * mesur�e par rapport au d�but. Si vous fusionnez <code>j.path</code> � la fin de
     * <code>i.path</code>, alors il faut inverser l'ordre des donn�es de <code>i.path</code>
     * si <code>i.mergeEnd</code> a la valeur <code>false</code>, et inverser les donn�es de
     * <code>j.path</code> si <code>j.mergeEnd</code> a la valeur <code>true</code>.
     */
    final Fermion i=new Fermion(), j=new Fermion();

    /**
     * Distance au carr� entre le d�but ou la fin de <code>j.path</code> avec le d�but ou la
     * fin de <code>i.path</code>. Les champs <code>[i/j].mergeEnd</code> indiquent quelles
     * extr�mit�es de {@link Polyline} sont compar�es.
     */
    double minDistanceSq=Double.POSITIVE_INFINITY;

    /**
     * Indique si la paire <code>i.path</code> et <code>j.path</code> est le r�sultat
     * des comparaisons de toutes les combinaisons possibles de segments.
     */
    boolean allComparisonsDone;

    /**
     * Renvoie une repr�sentation sous forme de cha�ne de caract�res de cet objet. Cette
     * repr�sentation sera g�n�ralement de la forme "Polyline[23+56 pts; D=0.3 km]" ou
     * "Polyline[65 pts; D=0.2 km]". Cette information est utile � des fins de d�boguage.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(j.path==i.path ? "Polygon[" : "Polyline[");
        buffer.append(i.path.getPointCount());
        if (j.path != i.path) {
            buffer.append('+');
            buffer.append(j.path.getPointCount());
        }
        buffer.append(" pts; D=");
        buffer.append((float) (Math.sqrt(minDistanceSq)/1000));
        buffer.append(" km]");
        return buffer.toString();
    }
}
