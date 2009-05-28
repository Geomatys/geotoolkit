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
 * R�f�rence vers un objet {@link Polyline}. Cette classe contient aussi un valeur
 * bool�enne qui sera prise en compte par la m�thode {@link #hashCode}. Cette valeur bool�enne
 * agit comme le spin d'un �lectron. Deux instances de <code>Fermion</code> peuvent r�f�rer au
 * m�me segment {@link Polyline} s'ils n'ont pas la m�me valeur bool�enne ("spin"). Cette classe
 * est r�serv�e � un usage interne par {@link PolygonAssembler}.
 *
 * @see FermionPair
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/Fermion.java $
 * @version $Id: Fermion.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 */
final class Fermion {
    /**
     * R�f�rence vers la polyligne repr�sent�e par cet objet. Dans l'analogie
     * avec la m�canique quantique, �a serait le "niveau atomique" d'un Fermion.
     */
    Polyline path;

    /**
     * Si <code>true</code>, la fin de du trait de c�te <code>path</code> devra �tre fusionn�
     * avec un autre trait (inconnu de cet objet). Si <code>false</code>, c'est le d�but du trait
     * <code>path</code> qui devra �tre fusionn�. Dans l'analogie avec la m�canique quantique,
     * c'est le "spin" d'un Fermion.
     */
    boolean mergeEnd;

    /**
     * Indique si deux cl�s sont identiques. Deux cl�s sont consid�r�s identiques si elles
     * se r�f�rent au m�me trait de c�te {@link #path} avec la m�me valeur bool�enne
     * {@link #mergeEnd}.
     */
    public boolean equals(final Object o) {
        if (o instanceof Fermion) {
            final Fermion k=(Fermion) o;
            return k.path==path && k.mergeEnd==mergeEnd;
        } else {
            return false;
        }
    }

    /**
     * Retourne une valeur � peu pr�s unique pour cet objet. Cette valeur sera b�tie �
     * partir de la r�f�rence {@link #path} et de la valeur bool�enne {@link #mergeEnd}.
     */
    public int hashCode() {
        final int code = System.identityHashCode(path);
        return mergeEnd ? code : ~code;
    }

    /**
     * Renvoie une repr�sentation sous forme de cha�ne de caract�res de cet objet.
     * Cette repr�sentation sera de la forme "Fermion[52 pts]".
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer("Fermion[");
        if (path != null) {
            buffer.append(path.getPointCount());
            buffer.append(" pts");
        }
        buffer.append(']');
        return buffer.toString();
    }
}
