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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.geotoolkit.util.Strings;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.process.ProgressController;


/**
 * Classe priv�e utilis�e pour identifier les lacs � l'int�rieur d'une �le ou d'un continent.
 * Chaque noeud contient une r�f�rence vers un objet {@link Polygon} et une liste de r�f�rences
 * vers d'autres objets <code>PolygonInclusion</code> dont les polygones sont enti�rement compris
 * � l'int�rieur de celui de cet objet <code>PolygonInclusion</code>.
 *
 * @todo     : This class is not yet used. It should be part of <code>PolygonAssembler</code>,
 *             work, but is not yet finished neither tested.
 *
 * @version $Id: PolygonInclusion.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
final class PolygonInclusion {
    /**
     * Polygone associ� � cet objet.
     */
    private final Polyline polygon;

    /**
     * Liste des objets <code>PolygonInclusion</code> fils. Les polygons de chacun de
     * ces fils sera enti�rement compris dans le polygon {@link #polygon} de cet objet.
     */
    private Collection childs;

    /**
     * Construit un noeud qui enveloppera le polygone sp�cifi�.
     * Ce noeud n'aura aucune branche pour l'instant.
     */
    private PolygonInclusion(final Polyline polygon) {
        this.polygon = polygon;
    }

    /**
     * V�rifie si deux noeuds sont identiques.
     * Cette m�thode ne doit pas �tre red�finie.
     */
    @Override
    public final boolean equals(final Object other) {
        return this == other;
    }

    /**
     * Retourne un code repr�sentant ce noeud.
     * Cette m�thode ne doit pas �tre red�finie.
     */
    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Ajoute � la liste des polygons-fils ({@link #childs}) tous les polygones de la liste
     * sp�cifi�e (<code>polygons</code>) qui sont enti�rement compris dans {@link #polygon}.
     * Chaque polygone ajout� � {@link #childs} sera retir� de <code>polygons</code>, de
     * sorte qu'apr�s l'appel de cette m�thode, <code>polygons</code> ne contiendra plus
     * que les polygones qui ne sont pas compris dans {@link #polygon}.
     */
    private void addChilds(final Collection polygons) {
        for (final Iterator it=polygons.iterator(); it.hasNext();) {
            final PolygonInclusion node = (PolygonInclusion) it.next();
            if (node!=this && polygon.contains(node.polygon))  {
                if (childs == null) {
                    childs = new LinkedList();
                }
                childs.add(node);
                it.remove();
            }
        }
        buildTree(childs, null);
    }

    /**
     * Apr�s avoir ajout� des polygones � la liste interne, on appel {@link #addChilds}
     * de fa�on r�cursive pour chacun des polygones de {@link #childs}. On obtiendra
     * ainsi une arborescence des polygones, chaque parent contenant enti�rement 0, 1
     * ou plusieurs enfants. Par exemple appellons "Continent" le polygone r�f�r� par
     * {@link #polygon}. Supposons que "Continent" contient enti�rement deux autres
     * polygones, "Lac" et "�le". Le code pr�c�dent avait ajout� "Lac" et "�le" � la
     * liste {@link #childs}. Maintenant on demandera � "Lac" d'examiner cette liste. Il
     * trouvera qu'il contient enti�rement "�le" et l'ajoutera � sa propre liste interne
     * apr�s l'avoir retir� de la liste {@link #child} de "Continent".
     */
    private static void buildTree(final Collection childs, final ProgressController progress) {
        if (childs != null) {
            int count = 0;
            final Set alreadyProcessed = new HashSet(childs.size() + 64);
            for (Iterator it=childs.iterator(); it.hasNext();) {
                final PolygonInclusion node = (PolygonInclusion) it.next();
                if (alreadyProcessed.add(node)) {
                    if (progress != null) {
                        progress.setProgress(100f * (count++ / childs.size()));
                    }
                    node.addChilds(childs);
                    it = childs.iterator(); // Need a new iterator since collection changed.
                }
            }
        }
    }

    /**
     * Add polygons in the <code>polygons</code> array.
     *
     * @param  nodes The collection of <code>PolygonInclusion</code> to process.
     * @param  polygons The destination in which to add {@link Polygon} objects.
     *
     * @throws TransformException if a transformation was required and failed.
     *         This exception should never happen if all polygons use the same
     *         coordinate system.
     */
    private static void createPolygons(final Collection nodes, final Collection polygons)
            throws TransformException
    {
        if (nodes != null) {
            for (final Iterator it=nodes.iterator(); it.hasNext();) {
                PolygonInclusion node = (PolygonInclusion) it.next();
                if (!node.polygon.isClosed()) {
                    polygons.add(node.polygon);
                    continue;
                }
                final Polygon polygon = new Polygon(node.polygon);
                polygons.add(polygon);
                if (node.childs != null) {
                    for (final Iterator it2=node.childs.iterator(); it2.hasNext();) {
                        node = (PolygonInclusion) it.next();
                        polygon.addHole(node.polygon);
                        createPolygons(node.childs, polygons);
                    }
                }
            }    
        }
    }

    /**
     * Examine tous les polygones sp�cifi�s et tente de diff�rencier les �les des lacs.
     *
     * @param  The source polylines.
     * @param  progres An optional progress listener.
     * @return The polygons.
     * @throws TransformException if a transformation was required and failed.
     *         This exception should never happen if all polygons use the same
     *         coordinate system.
     */
    static Collection process(final Polyline[] polygons, final ProgressController progress)
            throws TransformException
    {
        if (progress != null) {
            // TODO: localize...
            progress.setTask("Searching lakes");
            progress.started();
        }
        final List nodes = new LinkedList();
        for (int i=0; i<polygons.length; i++) {
            nodes.add(new PolygonInclusion(polygons[i]));
        }
        buildTree(nodes, progress);
        final List result = new ArrayList(polygons.length);
        createPolygons(nodes, result);
        return result;
    }

    /**
     * Retourne une cha�ne de caract�res contenant le polygone
     * {@link #polygon} de ce noeud ainsi que de tous les noeuds-fils.
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        toString(buffer, 0);
        return buffer.toString();
    }

    /**
     * Impl�mentation de la m�thode {@link #toString()}.
     * Cette m�thode s'appellera elle-m�me de fa�on recursive.
     */
    private void toString(final StringBuffer buffer, final int indentation) {
        
        buffer.append(Strings.spaces(indentation));
        buffer.append(polygon);
        buffer.append('\n');
        if (childs != null) {
            for (final Iterator it=childs.iterator(); it.hasNext();) {
                ((PolygonInclusion) it.next()).toString(buffer, indentation+2);
            }
        }
    }
}

