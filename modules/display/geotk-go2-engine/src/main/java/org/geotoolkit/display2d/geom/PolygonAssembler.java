/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.geom;

import java.awt.Shape;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.math.Line;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.opengis.util.ProgressListener;


/**
 * Assemble all {@linkplain Polyline polylines} in order to create closed {@linkplain Polygon
 * polygons} for proper rendering. This class analyses all available polylines and merges
 * together the polylines that look like parts of the same polygons. It can also complete the
 * polygons that were cut by the map border.
 *
 * This method is useful in the context of geometries digitalized from many consecutive
 * maps (for example the GEBCO digital atlas). It is not possible to fill polygons with
 * <A HREF="http://java.sun.com/products/java-media/2D/">Java2D</A> if the polygons are
 * broken in many pieces, as in the figure below.
 *
 * <p align="center"><img src="doc-files/splitted.png"></p>
 *
 * <P>Running this method <strong>once</strong> for a given collection of geometries before
 * renderering helps to repair them. The algorithm is:</P>
 * <ol>
 *   <li>A list of all possible pairs of polylines is built.</li>
 *   <li>For any pair of polylines, the shortest distance between their extremities is
 *       computed. All combinations between the beginning and the end of a polyline with
 *       the beginning or end of the other polyline are taken into account.</li>
 *   <li>The pair with the shortest distance are identified. When the shortest distance
 *       from one polyline's extremity is the other extremity of the same polyline, then
 *       the polyline is identified as a closed polygon (e.g. an island or a lake).
 *       Otherwise, the closest polylines are merged together.</li>
 *   <li>The loop is reexecuted from step 1 until no more polylines have been merged.</li>
 * </ol>
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/geom/PolygonAssembler.java $
 * @version $Id: PolygonAssembler.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @task TODO: L'impl�mentation actuelle de cette m�thode ne prend pas en compte les
 *             cas o� deux polylignes se chevaucheraient. (En fait, un d�but de prise
 *             en compte est fait et concerne les cas o� des polylignes se chevauchent
 *             d'un seul point).
 *
 * @task TODO: Localize logging and progress messages. Improves the progres bar: for now,
 *             it restart from 0 many times (it is pretty hard to guess in advance how much
 *             pass will be needed). Empirical tests suggest that there is about 4 long passes
 *             and hundred of very short (almost instantaneous) passes.
 */
final class PolygonAssembler implements Comparator {
    /**
     * The level for logging messages.
     */
    private static final Level LEVEL = Level.FINEST;

    /**
     * The locale for progress and error message.
     *
     * @taks TODO: Make it configurable.
     */
    private final Locale locale = Locale.getDefault();

    /**
     * The progress listener, or <code>null</code> if none.
     */
    private final ProgressListener progress;

    /**
     * Forme g�om�trique de la r�gion dans laquelle ont �t� d�coup�s les polygones.
     */
    private final Shape clip;

    /**
     * Constante � utiliser dans les appels de {@link Shape#getPathIterator(AffineTransform,double)}
     * afin d'obtenir une successions de segments de droites qui approximerait raisonablement les
     * courbes.
     */
    private final double flatness;

    /**
     * The list of polylines to process. May have a length of 0, but will never be null.
     */
    private Polyline[] polylines = new Polyline[16];

    /**
     * Isoligne pr�sentement en cours d'analyse.
     */
    private GeometryCollection collection;

    /**
     * Ellipso�de � utiliser pour calculer les distances, ou <code>null</code> si le
     * syst�me de coordonn�es est cart�sien. Cette information est d�duite � partir
     * du syst�me de coordonn�es de l'isoligne {@link #collection}.
     */
    private Ellipsoid ellipsoid;

    /**
     * Distance maximale (en m�tres) autoris�e entre deux extr�mit�s de polylignes
     * pour permettre leur rattachement. En premi�re approximation, la valeur
     * {@link Double.POSITIVE_INFINITY} donne des r�sultats acceptables dans plusieurs cas.
     */
    private final double dmax = Double.POSITIVE_INFINITY;

    /**
     * Table des polylignes � fusionner. Les valeurs de cette table seront des objets
     * {@link FermionPair}, tandis que les cl�s seront des objets {@link Fermion}.
     */
    private final Map fermions = new HashMap();

    /**
     * Instance d'une cl�. Cette instance est cr��e une fois pour toute pour �viter d'avoir
     * � en cr�er � chaque appel de la m�thode {@link #get(Polyline, boolean)}.  Les valeurs
     * de ses champs seront modifi�s � chaque appels de cette m�thode.
     */
    private final Fermion key = new Fermion();

    /**
     * Les variables suivantes servent aux calculs de distances. Elles sont
     * cr��es une fois pour toutes ici plut�t que d'allouer de la m�moire �
     * chaque ex�cution d'une boucle.
     */
    private final transient Point2D.Double jFirstPoint = new Point2D.Double();
    private final transient Point2D.Double jLastPoint  = new Point2D.Double();
    private final transient Point2D.Double iFirstPoint = new Point2D.Double();
    private final transient Point2D.Double iLastPoint  = new Point2D.Double();
    private final transient Point2D.Double tmpPoint    = new Point2D.Double();
    private final transient Line2D .Double pathLine    = new Line2D .Double();

    /**
     * Buffer r�serv� � un usage interne par la m�thode {@link #nextSegment}.
     */
    private final double[] pitBuffer = new double[8];

    /**
     * The pass number. For reporting in {@link #updateFermions} only.
     */
    private int pass;

    /**
     * Construit un objet qui assemblera les polygones de l'isoligne sp�cifi�e.
     *
     * @param clip     Les limites de la carte exprim�es selon le syst�me de coordonn�es
     *                 des isolignes qui seront � traiter.
     * @param progress Objet � utiliser pour informer des progr�s, ou <code>null</code>
     *                 s'il n'y en a pas.
     *
     * @see #setGeometryCollection
     */
    public PolygonAssembler(final Shape clip, final ProgressListener progress) {
        this.progress  = progress;
        this.flatness  = ShapeUtilities.getFlatness(clip);
        this.clip      = clip;
    }




    ///////////////////////////////////////////////////////////////////
    //////////                                               //////////
    //////////          H E L P E R   M E T H O D S          //////////
    //////////                                               //////////
    ///////////////////////////////////////////////////////////////////

    /**
     * Returns all {@link Polylines} objects in the given geometry.
     */
    private static Collection getPolylines(final Geometry geometry) {
        final Set set = new LinkedHashSet();
        geometry.getPolylines(set);
        return set;
    }

    /**
     * Set the next collection to process. This method must be invoked first, before any
     * processing. It is legal to invoke this method with the current collection (i.e.
     * {@link #collection}); it will update internal fields according the current state
     * of the collection.
     *
     * @param collection A new collection to process, or {@link #collection} for updating
     *        <code>PolygonAssembler</code> according the current collection state.
     */
    private void setGeometryCollection(final GeometryCollection collection) {
        if (collection != this.collection) {
            // TODO: localize
            Polyline.LOGGER.log(LEVEL, "Assembling collection "+collection.getName(locale));
            this.collection = collection;
        }
        this.ellipsoid = CRSUtilities.getHeadGeoEllipsoid(collection.getCoordinateReferenceSystem());
        final Collection set = getPolylines(collection);
        polylines = (Polyline[]) XArrays.resize(set.toArray(polylines), set.size());
        for (int i=0; i<polylines.length; i++) {
            polylines[i] = (Polyline) polylines[i].clone();
        }
        collection.removeAll();
    }

    /**
     * Update the collection with newly completed polygons.
     *
     * @throws TransformException if a transformation was needed and failed.
     */
    private void updateGeometryCollection() throws TransformException {
        collection.removeAll();
        for (int i=0; i<polylines.length; i++) {
            if (!polylines[i].isEmpty()) {
                collection.add(polylines[i]);
            }
        }
    }

    /**
     * Compare la distance s�parant deux objets {@link IntersectionPoint}.
     * Cette m�thode est r�serv�e � un usage interne afin de classer des
     * liste de points d'intersections avant leur traitement.
     */
    @Override
    public int compare(final Object a, final Object b) {
        final double dA = ((IntersectionPoint) a).minDistanceSq;
        final double dB = ((IntersectionPoint) b).minDistanceSq;
        if (dA < dB) return -1;
        if (dA > dB) return +1;
        return 0;
    }

    /**
     * Affecte � la ligne sp�cifi�e les coordonn�es du prochain segment de la forme
     * g�om�trique balay�e par <code>it</code>. Ce segment doit �tre d�crit par une
     * instruction <code>SEG_MOVETO</code> ou un <code>SEG_LINETO</code> suivie d'une
     * instruction <code>SEG_LINETO</code> ou <code>SEG_CLOSE</code>. Un seul appel �
     * <code>pit.next()</code> sera fait (de sorte que vous n'avez pas � l'appeller
     * vous-m�me), � moins qu'il y avait plusieurs instructions <code>SEG_MOVETO</code>
     * cons�cutifs. Dans ce dernier cas, seul le dernier sera pris en compte.
     * <p>
     * Le tableau <code>pitBuffer</code> sera utilis� lors des appels � la m�thode
     * <code>pit.currentSegment(double[])</code>. Selon les sp�cifications de cette
     * derni�re, le tableau doit avoir une longueur d'au moins 6 �l�ments. Toutefois
     * cette m�thode exige un tableau de 8 �l�ments, car elle utilisera les �l�ments
     * 6 et 7 pour sauvegarder les coordonn�es du dernier <code>SEG_MOVETO</code>
     * rencontr�. Cette information est n�cessaire pour permettre la fermeture correcte
     * d'un polygone lors de la prochaine instruction <code>SEG_CLOSE</code>.
     * <p>
     * L'exemple suivant affiche sur le p�riph�rique de sortie standard les coordonn�es
     * de toutes les droites qui composent la forme g�om�trique <var>s</var>.
     *
     * <blockquote><pre>
     * &nbsp;void exemple(Shape s) {
     * &nbsp;    PathIterator pit=s.getPathIterator(null, 1);
     * &nbsp;    while (!pit.isDone()) {
     * &nbsp;         boolean closed = nextSegment(pit);
     * &nbsp;         System.out.println({@link #pathLine});
     * &nbsp;         if (closed) break;
     * &nbsp;    }
     * &nbsp;}
     * </pre></blockquote>
     *
     * @param pit Objet balayant le contour d'une forme g�om�trique.
     *
     * @return <code>false</code> si la ligne s'est termin�e par une instruction
     *         <code>SEG_LINETO</code>, <code>true</code> si elle s'est termin�e
     *         par une instruction <code>SEG_CLOSE</code>. Cette information peut
     *         �tre vu comme une estimation de ce que devrait donner le prochain
     *         appel � la m�thode <code>it.isDone()</code> apr�s un appel � <code>it.next()</code>.
     *
     * @throws IllegalPathStateException si une instruction <code>SEG_QUADTO</code>
     *         ou <code>SEG_CUBICTO</code> a �t� rencontr�e.
     *
     * @see java.awt.geom.PathIterator#currentSegment(double[])
     */
    private boolean nextSegment(final PathIterator pit) {
        loop: while(true) {
            switch (pit.currentSegment(pitBuffer)) {
                case PathIterator.SEG_MOVETO: {
                    System.arraycopy(pitBuffer,0, pitBuffer,6, 2);
                    // fall through
                }
                case PathIterator.SEG_LINETO: {
                    final double x=pitBuffer[0];
                    final double y=pitBuffer[1];
                    pit.next();
                    switch (pit.currentSegment(pitBuffer)) {
                        case PathIterator.SEG_MOVETO: {
                            System.arraycopy(pitBuffer,0, pitBuffer,6, 2);
                            continue loop;
                        }
                        case PathIterator.SEG_LINETO: {
                            pathLine.setLine(x,y,pitBuffer[0],pitBuffer[1]);
                            return false;
                        }
                        case PathIterator.SEG_CLOSE: {
                            pathLine.setLine(x,y,pitBuffer[6],pitBuffer[7]);
                            return true;
                        }
                    }
                }
            }
            throw new IllegalPathStateException();
        }
    }

    /**
     * Returns the squared distance between points <code>P1</code> and <code>P2</code>.
     * If {@link #ellipsoid} is non-null (i.e. if the underlying coordinate system is
     * a geographic one), then the orthordomic distance will be computed.
     */
    private double distanceSq(final Point2D P1, final Point2D P2) {
        if (ellipsoid != null) {
            //TODO fix , avoid cast to implementation class
            final double distance = ((DefaultEllipsoid)ellipsoid).orthodromicDistance(P1, P2);
            return distance*distance;
        } else {
            return P1.distanceSq(P2);
        }
    }

    /**
     * Indique si la ligne sp�cifi�e repr�sente une singularit�, c'est-�-dire si les points
     * (<var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>) et
     * (<var>x<sub>2</sub></var>,<var>y<sub>2</sub></var>) sont identiques.
     */
    private static boolean isSingularity(final Line2D.Double line) {
        return line.x1==line.x2 && line.y1==line.y2;
    }

    /**
     * Recherche un objet <code>FermionPair</code> qui contient au moins un lien vers
     * la polyligne <code>path</code> sp�cifi�e avec la valeur <code>mergeEnd</code>
     * correspondante. L'objet <code>path</code> sp�cifi� peut correspondre indiff�rement
     * � un champ <code>i.path</code> ou <code>j.path</code>. Si aucun objet r�pondant
     * aux crit�res ne fut trouv�, alors cette m�thode retournera <code>null</code>.
     *
     * @param  path Polyligne � rechercher.
     * @param  mergeEnd Valeur de <code>mergeEnd</code> pour la polyligne � rechercher.
     * @return La paire de polylignes trouv�e, ou <code>null</code> s'il n'y en a pas.
     */
    private FermionPair get(final Polyline path, final boolean mergeEnd) {
        key.path     = path;
        key.mergeEnd = mergeEnd;
        return (FermionPair) fermions.get(key);
    }

    /**
     * Remove a polyline from the {@link #polylines} list.
     */
    private void remove(final Polyline polyline) {
        for (int i=polylines.length; --i>=0;) {
            if (polylines[i] == polyline) {
                polylines = (Polyline[]) XArrays.remove(polylines, i, 1);
                // There should be no other polylines, but continue just in case...
            }
        }
    }

    /**
     * Retire une paire de polylignes. Cette m�thode est appell�e apr�s que les polylignes
     * en questions ont �t� fusionn�es, de sorte qu'on a plus besoin des informations qui
     * y �taient associ�es.
     *
     * @param pair Paire de polylignes � retirer de la liste.
     */
    private void remove(final FermionPair pair) {
        fermions.remove(pair.i);
        fermions.remove(pair.j);
    }

    /**
     * Ajoute une paire de polylignes. Cette paire de polylignes sera identifi�e par
     * ses deux cl�s {@link FermionPair#i} et {@link FermionPair#j}, de sorte qu'il
     * sera possible de la retrouver � partir de n'importe quelle de ces cl�s.
     *
     * @param pair Paire de polylignes � ajouter � la liste.
     */
    private void put(final FermionPair pair) {
        fermions.put(pair.i, pair);
        fermions.put(pair.j, pair);
    }

    /**
     * Indique si on consid�re avoir termin� les comparaisons ou si on pense qu'il faudrait
     * en faire encore. Une r�ponse <code>true</code> indique qu'on a vraiement termin� les
     * comparaisons. Une r�ponse <code>false</code> n'implique pas n�cessairement que les
     * nouvelles comparaisons seront concluantes.
     *
     * @param path Polyligne pour laquelle on veut v�rifier si les comparaisons sont termin�es.
     * @return <code>false</code> s'il vaudrait mieux continuer les comparaisons.
     */
    private boolean isDone(final Polyline path) {
        key.path = path;
        FermionPair pair;

        key.mergeEnd = false;
        pair=(FermionPair) fermions.get(key);
        if (pair==null || !pair.allComparisonsDone) {
            return false;
        }
        key.mergeEnd = true;
        pair=(FermionPair) fermions.get(key);
        if (pair==null || !pair.allComparisonsDone) {
            return false;
        }
        return true;
    }

    /**
     * Indique que toutes les comparaisons ont �t� faites. Cette m�thode
     * est appell�e apr�s qu'une s�rie de comparaisons ont �t� faites,
     * pour indiquer qu'il est inutile de les refaire.
     *
     * @param done <code>true</code> si toutes les comparaisons ont �t� faites.
     */
    private void setAllComparisonsDone(final boolean done) {
        for (final Iterator it=fermions.values().iterator(); it.hasNext();) {
            ((FermionPair) it.next()).allComparisonsDone = done;
        }
    }

    /**
     * Renverse l'ordre des donn�es de la polyligne sp�cifi�e. En plus d'inverser les donn�es
     * elles-m�mes, cette m�thode inversera aussi les champs <code>mergeEnd</code> des objets
     * <code>FermionPair</code> qui se r�f�raient � cette polyligne, de sorte que la liste
     * restera � jour.
     *
     * @param path Polyligne � inverser.
     */
    private void reverse(final Polyline path) {
        path.reverse();
        key.path = path;
        key.mergeEnd=true;  final FermionPair op=(FermionPair) fermions.remove(key);
        key.mergeEnd=false; final FermionPair np=(FermionPair) fermions.remove(key);
        if (op != null) {
            if (op.i.path==path) {op.i.mergeEnd=!op.i.mergeEnd; fermions.put(op.i, op);}
            if (op.j.path==path) {op.j.mergeEnd=!op.j.mergeEnd; fermions.put(op.j, op);}
        }
        if (np!=null && np!=op) {
            if (np.i.path==path) {np.i.mergeEnd=!np.i.mergeEnd; fermions.put(np.i, np);}
            if (np.j.path==path) {np.j.mergeEnd=!np.j.mergeEnd; fermions.put(np.j, np);}
        }
    }

    /**
     * Inverse toutes les coordonn�es contenu dans le tableau sp�cifi� en
     * argument. Les coordonn�es sont suppos�es regroup�es par paires de
     * nombres r�els (<var>x</var>,<var>y</var>). Rien ne sera fait si le
     * tableau sp�cifi� est nul.
     */
    private static void reverse(final float array[]) {
        if (array != null) {
            int length = array.length;
            for (int i=0; i<length;) {
                float tmp2 = array[--length];   array[length] = array[i+1];
                float tmp1 = array[--length];   array[length] = array[i+0];
                array[i++] = tmp1;
                array[i++] = tmp2;
            }
        }
    }

    /**
     * Remplace toutes les occurences de la polyligne <code>searchFor</code> par la polyligne
     * <code>replaceBy</code>. Cette m�thode est appell�e apr�s que ces deux polylignes aient
     * �t� fusionn�es ensemble. Apr�s la fusion, une des deux polylignes n'est plus n�cessaire.
     * La r�gle voulant qu'il n'y ait jamais deux polylignes avec la m�me valeur de
     * <code>mergeEnd</code> restera respect�e si cette m�thode n'est appell�e qu'apr�s
     * une fusion pour suprimer la polyligne en trop.
     *
     * @param searchFor Polyligne � remplacer.
     * @param replaceBy Polyligne rempla�ant <code>searchFor</code>.
     */
    private void replace(final Polyline searchFor, final Polyline replaceBy) {
        key.path = searchFor;
        key.mergeEnd=true;  final FermionPair op=(FermionPair) fermions.remove(key);
        key.mergeEnd=false; final FermionPair np=(FermionPair) fermions.remove(key);
        if (op != null) {
            if (op.i.path==searchFor) {op.i.path=replaceBy; fermions.put(op.i, op);}
            if (op.j.path==searchFor) {op.j.path=replaceBy; fermions.put(op.j, op);}
        }
        if (np!=null && np!=op) {
            if (np.i.path==searchFor) {np.i.path=replaceBy; fermions.put(np.i, np);}
            if (np.j.path==searchFor) {np.j.path=replaceBy; fermions.put(np.j, np);}
        }
    }

    /**
     * Indique que la polyligne <code>polyline</code> pourrait repr�senter un polygone ferm�.
     * Cette m�thode v�rifiera d'abord s'il existe d'autres objets {@link FermionPair} pour
     * cette polyligne. Si c'est le cas, et si la distance mesur�e par ces {@link FermionPair}
     * est inf�rieure � la valeur de l'argument <code>sqrt(distanceSq)</code> de cette m�thode,
     * alors rien ne sera fait.
     *
     * @param polyline R�f�rence vers la polyligne repr�sentant peut-�tre un polygone ferm�.
     * @param distanceSq Carr� de la distance entre le premier et dernier point de ce polygone.
     * @return <code>true</code> si une information pr�c�demment calcul�e a d�
     *         �tre supprim�e. Dans ce cas, toutes la boucle calculant ces
     *         information devra �tre refaite.
     *
     * @see #candidateToMerging
     */
    private boolean candidateToClosing(final Polyline polyline, final double distanceSq) {
        /*
         * Recherche les objets <code>FermionPair</code> se r�f�rant d�j� � la polyligne
         * <code>polyline</code>. Au besoin, un nouvel objet sera cr�� si aucun n'avait �t�
         * d�finie. Les variables <code>op</code> et <code>np</code> contiendront des r�f�rences
         * vers les deux objets <code>FermionPair</code> possibles. Elles ne seront jamais nulles,
         * mais peuvent avoir la m�me valeur toutes les deux.
         */
        FermionPair op = get(polyline, true);
        FermionPair np = get(polyline, false);
        if (np == null) {
            np = op;
            if (np == null) {
                np = new FermionPair();
            }
        }
        if (op == null) {
            op = np;
        }
        if (distanceSq<np.minDistanceSq && distanceSq<op.minDistanceSq) {
            /*
             * �limine toutes les r�f�rences vers <code>op</code> et <code>np</code>. Par
             * la suite, on choisira arbitrairement de laisser tomber <code>op</code> et
             * de r�utiliser <code>np</code> pour m�moriser les nouvelles informations.
             */
            remove(op);
            remove(np);
            np.i.mergeEnd = true;
            np.j.mergeEnd = false;
            np.minDistanceSq = distanceSq;
            np.i.path = np.j.path = polyline;
            np.allComparisonsDone = false;
            put(np);
            return true;
        }
        return false;
    }

    /**
     * D�clare que les polylignes <code>jPath</code> et <code>iPath</code> pourraient �tre
     * fusionn�es. Cette information ne sera retenue que si la distance <code>sqrt(distanceSQ)</code>
     * sp�cifi�e en argument est plus courte que ce qui avait �t� m�moris� pr�c�demment.
     *
     * @param jPath      Pointeur vers une des polylignes � fusionner.
     * @param mergeEndJ  <code>true</code> si <code>distanceSq</code> est
     *                   mesur�e par rapport � la fin de cette polyligne.
     * @param iPath      Pointeur vers l'autre polyligne � fusionner.
     * @param mergeEndI  <code>true</code> si <code>distanceSq</code> est
     *                   mesur�e par rapport � la fin de cette polyligne.
     * @param distanceSq Carr� de la distance entre les deux polylignes.
     * @return <code>true</code> si une information pr�c�demment calcul�e a d�
     *         �tre supprim�e. Dans ce cas, toutes la boucle calculant ces
     *         information devra �tre refaite.
     *
     * @see #candidateToClosing
     */
    private boolean candidateToMerging(final Polyline jPath, final boolean mergeEndJ,
                                       final Polyline iPath, final boolean mergeEndI,
                                       final double distanceSq)
    {
        assert (jPath != iPath);
        /*
         * Recherche s'il y avait des objets qui m�morisaient d�j� iPath et/ou jPath avec le
         * param�tre <code>mergeEnd</code> appropri�. Si aucun de ces objets ne fut trouv�, un
         * objet sera cr�� et ajout� � la liste.
         */
        FermionPair pi = get(iPath, mergeEndI);
        FermionPair pj = get(jPath, mergeEndJ);
        if (pi == null) {
            pi = pj;
            if (pi == null) {
                pi = new FermionPair();
            }
        }
        if (pj == null) {
            pj = pi;
        }
        /*
         * V�rifie maintenant si la distance sp�cifi�e en argument est inf�rieure
         * aux distances qui avaient �t� m�moris�es pr�c�demment pour chacun des
         * objets <code>[i/j].path</code> concern�s.
         */
        if (distanceSq<pi.minDistanceSq && distanceSq<pj.minDistanceSq) {
            remove(pi);
            remove(pj);
            pj.i.path = iPath;
            pj.j.path = jPath;
            pj.i.mergeEnd = mergeEndI;
            pj.j.mergeEnd = mergeEndJ;
            pj.minDistanceSq = distanceSq;
            pj.allComparisonsDone = false;
            put(pj);
            return true;
        }
        return false;
    }




    ///////////////////////////////////////////////////////////////////
    //////////                                               //////////
    //////////     P O L Y G O N S   A S S E M B L A G E     //////////
    //////////                                               //////////
    ///////////////////////////////////////////////////////////////////

    /**
     * Dresse une liste des paires de polylignes les plus rapproch�es. Si cette liste
     * existait d�j�, alors cette m�thode ne fera que la remettre � jour en tentant
     * d'�viter de r�p�ter certains calculs inutiles.
     */
    private void updateFermions() {
        boolean hasChanged;
        while (true) {
            hasChanged = false;
            boolean tryAgain;
            do {
                tryAgain = false;
                pass++;
                if (progress != null) {
                    progress.setDescription("Analyzing (pass "+pass+')'); // TODO: localize
                }
                for (int j=0; j<polylines.length; j++) {
                    if (progress != null) {
                        /*
                         * Utiliser 'j' directement pour informer des progr�s ne donne pas
                         * une progression lin�aire, car l'algorithme ci-dessous utilise
                         * deux blocs 'for' imbriqu�s. Le temps n�c�ssaire au calcul est de
                         * l'ordre de O(n�) ou n est le nombre de polylignes restant � traiter.
                         * On utilisera donc plut�t la formule ci-dessous, qui ferra para�tre
                         * lin�aire la progression.
                         */
                        progress.progress(100f * (j*(2*polylines.length-j)) /
                                          (polylines.length*polylines.length));
                    }
                    final Polyline jPath = polylines[j];
                    if (!isDone(jPath)) {
                        /*
                         * Le code de ce bloc est assez laborieux. Aussi, il ne sera ex�cut� que
                         * si les informations dans la cache ne sont plus valides pour cette
                         * polyligne. Les prochaines lignes calculent la distance entre le premier
                         * et le dernier point de la polyligne j. On part de l'hypoth�se que j est
                         * un polygone ferm� (�le ou lac par exemple).
                         */
                        double minDistanceSq;
                        minDistanceSq = distanceSq(jPath.getFirstPoint(jFirstPoint),
                                                   jPath.getLastPoint (jLastPoint));
                        tryAgain |= candidateToClosing(jPath, minDistanceSq);
                        if (minDistanceSq != 0) { // Simple optimisation (pourrait �tre retir�e)
                            /*
                             * On v�rifie maintenant si, dans les prochaines polylignes, il y en
                             * aurait une dont le d�but ou la fin serait plus proche du d�but
                             * ou de la fin de la polyligne j. Si on trouve une telle polyligne,
                             * elle sera d�sign�e par i et remplacera l'hypoth�se pr�c�dente �
                             * l'effet que j est un polygone ferm�.
                             */
                            for (int i=j+1; i<polylines.length; i++) {
                                final Polyline iPath = polylines[i];
                                if (!isDone(iPath)) {
                                    minDistanceSq = distanceSq(iPath.getFirstPoint(iFirstPoint),
                                                               iPath.getLastPoint (iLastPoint));
                                    tryAgain |= candidateToClosing(iPath, minDistanceSq);
                                    /*
                                     * Les conditions suivantes recherche avec quel agencement
                                     * des polylignes i et j on obtient la plus courte distance
                                     * possible. On commence par calculer cette distance en
                                     * supposant qu'aucune polyligne n'est invers�e.
                                     */
                                    tryAgain |= candidateToMerging(jPath, true, iPath, false,
                                                        distanceSq(jLastPoint, iFirstPoint));
                                    /*
                                     * Distance si l'on suppose que la polyligne J est invers�e.
                                     */
                                    tryAgain |= candidateToMerging(jPath, false, iPath, false,
                                                        distanceSq(jFirstPoint, iFirstPoint));
                                    /*
                                     * Distance si l'on suppose que la polyligne I est invers�e.
                                     */
                                    tryAgain |= candidateToMerging(jPath, true, iPath, true,
                                                        distanceSq(jLastPoint, iLastPoint));
                                    /*
                                     * Distance si l'on suppose que les deux polylignes sont
                                     * invers�es. Notez que fusionner I � J apr�s avoir invers�
                                     * ces deux polylignes revient � fusionner J � I sans les
                                     * inverser, ce qui est plus rapide.
                                     */
                                    tryAgain |= candidateToMerging(jPath, false, iPath, true,
                                                        distanceSq(jFirstPoint, iLastPoint));
                                    /*
                                     * Si l'on vient de trouver que cette polyligne i est plus pr�s
                                     * de la polyligne j que tous les autres jusqu'� maintenant
                                     * (incluant j lui-m�me), alors on m�morisera dans la cache
                                     * que j devrait �tre fusionn�e avec i. Mais la boucle n'est
                                     * pas termin�e et une meilleure combinaison peut encore
                                     * �tre trouv�e...
                                     */
                                }
                            }
                        }
                    }
                }
                setAllComparisonsDone(true);
                hasChanged |= tryAgain;
            }
            while (tryAgain);
            if (!hasChanged) break;
            setAllComparisonsDone(false);
            /*
             * Apr�s avoir fait un premier examen optimis�e pour la vitesse, il est n�cessaire
             * de refaire un second passage sans les optimisations. L'exemple suivant illustre
             * un cas o� c'est n�cessaire. Supposons que le premier passage a d�termin� que la
             * polyligne A pourrait �tre ferm�e comme une �le. Plus loin la polyligne B trouve
             * qu'elle pourrait se fusionner avec A pour former un segment AB, mais la distance
             * AB est plus grande que la distance AA, alors B laisse tomber. Supposons que plus
             * loin un segment C offre une distance AC plus courte que AA, de sorte que A n'est
             * plus une �le. B n'a pas �t� inform�e que l'autre extr�mit� de A est devenu
             * disponible, d'o� la n�cessit� d'un second passage. Si ce second passage n'a rien
             * chang�, alors on aura vraiment termin�.
             */
        }
    }

    /**
     * Examine toutes les polylignes en m�moire et rattachent ensemble celles qui semblent faire
     * partie d'un m�me polygone ferm� (par exemple un trait de c�te ou une �le).  Cette m�thode
     * est tr�s utile apr�s la lecture d'un fichier de donn�es bathym�triques g�n�r� par l'atlas
     * digitalis� GEBCO. Ce dernier ne dessine pas les c�tes d'un seul trait.  Avoir un trait de
     * c�te divis� en plusieurs segments nous emp�che de d�terminer si un point se trouve sur la
     * terre ferme ou sur la mer. Par le fait m�me, �a rend difficile tout remplissage des terres.
     * Cette m�thode tente d'y rem�dier en proc�dant grosso-modo comme suit:<p>
     *
     * <ol>
     *   <li>Les polylignes seront examin�es deux par deux. Toutes les combinaisons
     *       possibles de paires de polylignes seront �valu�es.</li>
     *   <li>Pour deux polylignes donn�es, la plus courte distance qui s�pare deux extr�mit�s
     *       sera calcul�e. Tous les agencements possibles entre le d�but ou la fin d'une
     *       polyligne avec le d�but ou la fin de l'autre seront �valu�es.</li>
     *   <li>Parmis toutes les combinaisons possibles �valu�es aux �tapes 1 et 2, on recherche
     *       la plus courte distance entre deux extr�mit�s. On ignore les cas o� la polyligne la
     *       plus pr�s d'une polyligne est elle-m�me (c'est-�-dire que la distance entre son d�but
     *       et sa fin est plus courte qu'avec tous autre agencement), car ils d�crivent des
     *       polygones ferm�s.</li>
     *   <li>Tant qu'on a trouve deux polylignes tr�s proches l'une de l'autre, on proc�de � leur
     *       fusion puis on recommence � l'�tape 1.</li>
     * </ol>
     *
     * <blockquote>
     *     NOTE: L'impl�mentation actuelle de cette m�thode ne prend pas en compte les
     *           cas o� deux polylignes se chevaucheraient. (En fait, un d�but de prise
     *           en compte est fait et concerne les cas o� des polylignes se chevauchent
     *           d'un seul point).
     * </blockquote>
     *
     * @throws TransformException if a transformation was needed and failed.
     */
    private void assemblePolygons() throws TransformException {
        updateFermions();
        final StringBuffer message;
        if (Polyline.LOGGER.isLoggable(LEVEL)) {
            message = new StringBuffer();
        } else {
            message = null;
        }
        if (progress != null) {
            progress.setDescription("Assembling polygons"); // TODO: localize
            progress.progress(0);
        }
        int count = 0;
        final float progressScale = 100f / fermions.size();
        final double dmaxSq = dmax*dmax;
        Iterator it=fermions.values().iterator();
        while (it.hasNext()) {
            if (progress != null) {
                progress.progress(count++ * progressScale);
            }
            /*
             * D�termine quelles paires de polylignes sont s�par�es par la plus courte
             * distance. Cette paire sera retir�e de la liste des polylignes � fusionner.
             */
            final FermionPair pair = (FermionPair) it.next();
            it.remove();
            if (pair.i.path!=pair.j.path && pair.minDistanceSq<=dmaxSq) {
                remove(pair); // Retire aussi l'autre r�f�rence
                /*
                 * Initialise des variables internes qui pointeront vers les donn�es.
                 */
                final int overlap = (pair.minDistanceSq==0) ? 1 : 0;
                if (message != null) {
                    message.setLength(0);
                    message.append("Merging ");
                    message.append(pair);
                    message.append(' ');
                    // Will be completed later.
                }
                /*
                 * Proc�de � la fusion des polylignes. Il n'y aura pas de nouvelle
                 * allocation de m�moire. Ce sera un simple jeu de pointeurs. Si
                 * les deux polylignes sont � inverser, il sera plus rapide de les
                 * coller en ordre inverse (�a �vite un appel � la fastidieuse
                 * m�thode <code>reverse</code>).
                 */
                if (pair.i.mergeEnd == pair.j.mergeEnd) {
                    if (pair.i.path.getPointCount() <= pair.j.path.getPointCount()) {
                        reverse(pair.i.path);
                        pair.i.mergeEnd = !pair.i.mergeEnd;
                        if (message != null) {
                            message.append("(reverse #1) ");
                        }
                    } else {
                        reverse(pair.j.path);
                        pair.j.mergeEnd = !pair.j.mergeEnd;
                        if (message != null) {
                            message.append("(reverse #2) ");
                        }
                    }
                }
                final String appendOrder;
                Polyline oldPath, newPath;
                if (pair.j.mergeEnd && !pair.i.mergeEnd) {
                    appendOrder = "Append #1 to #2";
                    oldPath = pair.i.path;
                    newPath = pair.j.path;
                } else {
                    assert pair.i.mergeEnd && !pair.j.mergeEnd;
                    appendOrder = "Append #2 to #1";
                    oldPath = pair.j.path;
                    newPath = pair.i.path;
                }
                if (message != null) {
                    message.append(appendOrder);
                    Polyline.LOGGER.log(LEVEL, message.toString());
                }
                if (false) {
                    // TODO: Why is this shape frozen?
                    if (newPath.isFrozen()) {
                        replace(newPath, newPath=(Polyline)newPath.clone());
                    }
                }
                newPath.append(oldPath.subpoly(overlap));
                replace(oldPath, newPath);
                remove(oldPath);
                /*
                 * Les op�rations pr�c�dentes ayant modifi� la liste, on
                 * doit demander un nouvel it�rateur pour pouvoir continuer.
                 */
                it = fermions.values().iterator();
            }
        }
    }




    ///////////////////////////////////////////////////////////////////
    //////////                                               //////////
    //////////     P O L Y G O N S   C O M P L E T I O N     //////////
    //////////                                               //////////
    ///////////////////////////////////////////////////////////////////

    /**
     * Examine toutes les polylignes en m�moire et rattachent ensemble celles qui semblent faire
     * partie d'un m�me polygone ferm�. Cette m�thode va aussi tenter de compl�ter les polygones
     * de fa�on � pouvoir les remplir. Elle est g�n�ralement appell�e pour l'isoligne correspondant
     * au niveau 0. Les autres profondeurs utiliseront {@link #assemblePolygons} afin de ne pas
     * compl�ter les polygones.
     * <br><br>
     * Pour pouvoir compl�ter correctement les polygones, cette m�thode a besoin de conna�tre
     * la forme g�om�trique des limites de la carte. Il ne s'agit pas des limites que vous
     * souhaitez donner � la carte, mais des limites qui avaient �t� sp�cifi�es au logiciel
     * qui a produit les donn�es des polylignes. Dans la tr�s grande majorit� des cas, cette
     * forme est un rectangle. Mais cette m�thode accepte aussi d'autres formes telles qu'un
     * cercle ou un triangle. La principale restriction est que cette forme doit contenir une
     * et une seule surface ferm�e. Cette forme g�om�trique aura �t� sp�cifi�e lors de la
     * construction de cet objet <code>PolygonAssembler</code>.
     *
     * @param ptRef  La coordonn�e d'un point en mer, selon le syst�me de coordonn�es de l'isoligne
     *               en cours ({@link #collection}). Ce point <u>doit</u> �tre sur l'un des bords de
     *               la carte (gauche, droit, haut ou bas si {@link #clip} est un rectangle). S'il
     *               n'est pas exactement sur un des bords, il sera projet� sur le bord le plus
     *               proche.
     * @param inside Normalement <code>false</code>. Si toutefois l'argument <code>sea</code>
     *               ne repr�sente non pas un point en mer mais plutot un point sur la
     *               terre ferme, alors sp�cifiez <code>true</code> pour cet argument.
     *
     * @throws TransformException if a transformation was needed and failed.
     * @throws IllegalStateException si une erreur est survenue lors du traitement des isolignes.
     * @throws IllegalArgumentException Si un probl�me d'unit�s est survenu. En principe, cette
     *         erreur ne devrait pas se produre.
     */
    private void completePolygons(final Point2D ptRef, final boolean inside)
            throws TransformException
    {
        if (progress != null) {
            progress.setDescription("Creating map border"); // TODO: localize
            progress.progress(0);
        }
        final List intersections = new ArrayList();
        IntersectionPoint startingPoint;
        /*
         * Proj�te sur la bordure de la carte la position du point de r�f�rence.
         * Au passage, cette m�thode m�morisera le num�ro du segment de droite
         * de la bordure sur laquelle se trouve le point, ainsi que son produit
         * scalaire. Ces informations serviront plus tard � d�terminer dans quel
         * ordre rattacher les polylignes.
         */
        startingPoint                  = new IntersectionPoint(ptRef);
        startingPoint.border           = -1;
        startingPoint.scalarProduct    = Double.NaN;
        startingPoint.minDistanceSq    = Double.POSITIVE_INFINITY;
        startingPoint.crs = collection.getCoordinateReferenceSystem();
        PathIterator pit               = clip.getPathIterator(null, flatness);
        for (int border=0; !pit.isDone(); border++) {
            final boolean closed = nextSegment(pit); // Update 'pathLine'
            if (!isSingularity(pathLine)) {
                final Point2D projected = ShapeUtilities.nearestColinearPoint(pathLine, ptRef);
                final double distanceSq = ptRef.distanceSq(projected);
                if (distanceSq < startingPoint.minDistanceSq) {
                    startingPoint.setLocation(projected, pathLine, border);
                    startingPoint.minDistanceSq = distanceSq;
                }
            }
            if (closed) break;
        }
        Polyline.LOGGER.log(LEVEL, "Reference point: "+startingPoint);
        if (startingPoint.minDistanceSq > 8*flatness*flatness) {
            throw new IllegalStateException("Reference point is too far away"); // TODO: localize
        }
        updateFermions();
        /*
         * Les variables suivantes sont cr��es ici une fois pour toutes plut�t
         * que d'�tre cr��es � chaque ex�cution de la prochaine boucle.
         */
        final Point2D.Double[] iPoints = {iFirstPoint, iLastPoint};
        final Line           interpole = new Line();
        /*
         * Construit une liste des points d'intersections entre les polylignes et la bordure de
         * la carte. Apr�s l'ex�cution de ce bloc, une s�rie d'objets {@link IntersectionPoint}
         * aura �t� plac�e dans la liste {@link #intersections}. Pour chaque polyligne dont le
         * d�but ou la fin intercepte avec un des bords de la carte, {@link IntersectionPoint}
         * contiendra la coordonn�e de ce point d'intersection ainsi qu'une information indiquant
         * si le calcul fut fait � partir des donn�es du d�but ou de la fin du segment.
         */
        intersections.clear();
        for (int i=0; i<polylines.length; i++) {
            final Polyline iPath = polylines[i];
            if (iPath.getPointCount() < 2) {
                continue;
            }
            /*
             * Met � jour la bo�te de dialogue informant des
             * progr�s de l'op�ration. Les progr�s seront �
             * peu pr�s proportionnels � <var>i</var>.
             */
            if (progress != null) {
                progress.progress(100f * i / polylines.length);
            }
            /*
             * La boucle suivante ne sera ex�cut�e que deux fois. Le premier
             * passage examine les points se trouvant au d�but de la polyligne,
             * tandis que le second passage examine refait exactement le m�me
             * traitement mais avec les points se trouvant � la fin de la polyligne.
             */
            boolean append = false;
            do {
                Point2D.Double extremCoord;
                if (!append) {
                    iPath.getFirstPoints(iPoints);
                    extremCoord = iFirstPoint;
                } else {
                    iPath.getLastPoints(iPoints);
                    extremCoord = iLastPoint;
                }
                /*
                 * Interpole lin�airement les �ventuels points d'intersections avec les bords
                 * de la carte et v�rifie si la distance entre les points originaux et les
                 * points interpol�s est plus petite que celles qui ont �t� calcul�es par
                 * la m�thode {@link #updateFermions}.
                 */
                FermionPair info = null;
                IntersectionPoint intersectPoint = null;
                interpole.setLine(iFirstPoint, iLastPoint);
                assert !Double.isNaN(interpole.getSlope()) &&
                       !Double.isNaN(interpole.getX0   ()) &&
                       !Double.isNaN(interpole.getY0()) : extremCoord;
                double minDistanceSq = Double.POSITIVE_INFINITY;
                pit = clip.getPathIterator(null, flatness);
                for (int border=0; !pit.isDone(); border++) {
                    final boolean closed = nextSegment(pit); // Update 'pathLine'
                    if (!isSingularity(pathLine)) {
                        final Point2D intPt = interpole.intersectionPoint(pathLine);
                        if (intPt != null) {
                            /*
                             * <code>intPt</code> contient le point d'intersection de la polyligne
                             * <code>iPath</code> avec le bord {@link #clip} de la carte. Les
                             * points <code>extrem*</code> (calcul�s pr�c�demment) contiennent les
                             * coordonn�es du point � l'extr�mit� (d�but ou fin) de la polyligne.
                             */
                            double compare = distanceSq(intPt, extremCoord);
                            if (compare < minDistanceSq) {
                                minDistanceSq = compare;
                                /*
                                 * On v�rifie maintenant si la distance entre les deux points
                                 * calcul�e pr�c�demment est plus courte que la plus courte
                                 * distance entre cette polyligne et n'importe quelle autre
                                 * polyligne.
                                 */
                                if (info == null) {
                                    info = get(iPath, append);
                                }
                                if (minDistanceSq <= info.minDistanceSq) {
                                    if (intersectPoint == null) {
                                        intersectPoint = new IntersectionPoint();
                                        intersections.add(intersectPoint);
                                    }
                                    intersectPoint.setLocation(intPt, pathLine, border);
                                    intersectPoint.path             = iPath;
                                    intersectPoint.append           = append;
                                    intersectPoint.crs = collection.getCoordinateReferenceSystem();
                                    intersectPoint.minDistanceSq    = minDistanceSq;
                                    info.allComparisonsDone = false;
                                }
                            }
                        }
                    }
                    if (closed) break;
                }
            }
            while ((append=!append) == true);
        }
        /*
         * Construit une bordure pour les polylignes. Pour fonctionner, cette m�thode a besoin
         * qu'on lui ait calcul� � l'avance les points d'intersections entre tous les polylignes
         * et la bordure de la carte. Ces points d'intersections doivent �tre fournis sous forme
         * d'objets {@link IntersectionPoint} regroup�s dans l'ensemble <code>intersections</code>.
         * Ce code a aussi besoin d'un point de r�f�rence, <code>startingPoint</code>. Ce point
         * ne doit pas �tre �gal � un des points d'intersection de <code>intersections</code>.
         * Il doit s'agir d'un point se trouvant soit � l'int�rieur, soit � l'ext�rieur des
         * polylignes mais jamais sur leurs contours. L'argument <code>inside</code> indique
         * si ce point se trouve � l'int�rieur ou � l'ext�rieur des polylignes.
         */
        pit = null;
        /*
         * La variable suivante indiquent le nombre de points d'intersections qui, pour
         * une raison quelconque, devraient �tre ignor�s. Le nombre d'intersections avec
         * la bordure de la carte devrait en principe �tre un nombre pair. Toutefois, si
         * ce nombre est impair, alors cette variable sera incr�ment�e de 1 afin d'ignorer
         * le point d'intersection qui semble le moins appropri� (celui qui est le plus loin
         * de la bordure de la carte).
         */
        int countIntersectionsToRemove = 0;
        /*
         * Le bloc suivant calcule les valeurs dans le tableau <code>intersectPoint</code>
         * Pour chaque segment dont le d�but ou la fin intersecte avec un des bords de la
         * carte, intersectPoint[...] contiendra la coordonn�e de ce point d'intersection
         * ainsi qu'une information indiquant si le calcul fut fait � partir des donn�es
         * du d�but ou de la fin du segment.
         */
        IntersectionPoint[] intersectPoints = new IntersectionPoint[intersections.size()];
        intersectPoints = (IntersectionPoint[]) intersections.toArray(intersectPoints);
        countIntersectionsToRemove += (intersectPoints.length & 1);
        if (countIntersectionsToRemove > 0) {
            if (countIntersectionsToRemove > intersectPoints.length) {
                countIntersectionsToRemove = intersectPoints.length;
            }
            Arrays.sort(intersectPoints, this);
            if (Polyline.LOGGER.isLoggable(LEVEL)) {
                final StringBuffer message = new StringBuffer("Too many intersection points");
                int index = intersectPoints.length;
                final String lineSeparator = System.getProperty("line.separator", "\n");
                for (int i=countIntersectionsToRemove; --i>=0;) {
                    message.append(lineSeparator);
                    message.append("    Removing ");
                    message.append(intersectPoints[--index]);
                }
                Polyline.LOGGER.log(LEVEL, message.toString());
            }
            intersectPoints = (IntersectionPoint[]) XArrays.resize(intersectPoints,
                              intersectPoints.length-countIntersectionsToRemove);
        }
        if (intersectPoints.length == 0) {
            return;
        }
        /*
         * Maintenant que nous disposons des points                P1            P2
         * d'intersections, on les parcourera dans le         +-----o------------o---+
         * sens normal ou inverse des aiguilles d'une         |     :.           :...o P3
         * montre, selon l'impl�mentation de l'it�rateur      |       :  ....        |
         * {@link PathIterator} utilis�. Un classement sera   |        :.:   :.      |
         * d'abord fait avec {@link Arrays#sort(Object[])}.   +---------------o------+
         * Les intersections seront jointes dans cet ordre.                  P4
         */
        Arrays.sort(intersectPoints);
        int indexNextIntersect = ~Arrays.binarySearch(intersectPoints, startingPoint);
        // Note: ~indexNextIntersect == -1-indexNextIntersect
        if (indexNextIntersect < 0) {
            // TODO: localize
            throw new IllegalArgumentException("Reference point too close from border");
        }
        if (inside) {
            indexNextIntersect++;
        }
        indexNextIntersect %= intersectPoints.length;
        if (Polyline.LOGGER.isLoggable(LEVEL)) {
            final StringBuffer message = new StringBuffer("Sorted list of intersection points");
            final String lineSeparator = System.getProperty("line.separator", "\n");
            for (int j=0; j<intersectPoints.length; j++) {
                message.append(lineSeparator);
                message.append(j==indexNextIntersect ? "==> " : "    ");
                message.append(intersectPoints[j]);
            }
            Polyline.LOGGER.log(LEVEL, message.toString());
        }
        /*
         * Proc�de maintenant � la cr�ation du cadre. On balayera tous les points
         * d'intersections, dans l'ordre dans lequels ils viennent d'�tre class�s.
         * Le balayage commencera � partir du premier point d'intersection qui suit
         * le <code>startingPoint</code>. L'index de ce premier point � �t� calcul�
         * plus haut dans <code>indexNextIntersect</code>.
         */
        int               pitBorder          = -1;
        float             buffer[]           = new float[16];
        boolean           traceLine          = false;
        IntersectionPoint lastIntersectPoint = null;
        IntersectionPoint intersectPoint;
        while ((intersectPoint=intersectPoints[indexNextIntersect]) != null) {
            final int indexLastIntersect = indexNextIntersect++;
            if (indexNextIntersect >= intersectPoints.length) {
                    indexNextIntersect=0;
            }
            intersectPoints[indexLastIntersect] = null;
            /*
             * Si l'on vient d'atteindre le premier point d'une terre, ne fait rien
             * et continue la boucle. Lorsqu'on aurra atteint le deuxi�me point de
             * la terre, alors on ex�cutera le bloc ci-dessous pour relier ces deux
             * points par une ligne.
             */
            if (traceLine) {
                buffer[0] = (float) lastIntersectPoint.x;
                buffer[1] = (float) lastIntersectPoint.y;
                int length = 2;
                /*
                 * Parvenu � ce stade, <code>nextIntersect</code> contient l'index du
                 * prochain point d'intersection d'une ligne de niveau avec la bordure
                 * de la carte. On suivra maintenant la bordure de la carte jusqu'� ce
                 * que l'on atteigne ce point.
                 */
                if (intersectPoint.border != lastIntersectPoint.border) {
                    /*
                     * Positionne l'it�rateur sur le premier bord � consid�rer.
                     * C'est n�cessaire lorsque tous les points pr�c�demment
                     * fusionn�s se trouvaient toujours sur le m�me bord. Dans
                     * ce cas, l'it�rateur n'avait pas �t� incr�ment� car on
                     * n'en avait peut-�tre plus de besoin.
                     */
                    while (pitBorder != lastIntersectPoint.border) {
                        if (pit==null || pit.isDone()) {
                            pit = clip.getPathIterator(null, flatness);
                            pitBorder = -1;
                        }
                        if (nextSegment(pit)) {
                            pit = null;
                            pitBorder = -1;
                        }
                        pitBorder++;
                    }
                    /*
                     * Suit les bords en m�morisant leurs coordonn�es au passage,
                     * jusqu'� ce qu'on aie atteint le dernier bord. Les coordonn�es
                     * seront m�moris�es dans <code>buffer</code>, un tableau qui sera
                     * agrandi au gr�s des besoins.
                     */
                    do {
                        buffer[length++] = (float) pathLine.x2;
                        buffer[length++] = (float) pathLine.y2;
                        if (buffer[length-4]==buffer[length-2] &&
                            buffer[length-3]==buffer[length-1])
                        {
                            length -= 2;
                        }
                        if (length >= buffer.length) {
                            // It's ok to put this code here
                            buffer = XArrays.resize(buffer, 2*length);
                        }
                        if (pit==null || pit.isDone()) {
                            pit = clip.getPathIterator(null, flatness);
                            pitBorder = -1;
                        }
                        if (nextSegment(pit)) {
                            pit = null;
                            pitBorder = -1;
                        }
                    }
                    while (++pitBorder != intersectPoint.border);
                }
                /*
                 * M�morise les coordonn�es du dernier point, de la m�me fa�on
                 * qu'on avait m�moris� ceux du premier point plus haut dans
                 * <code>iFirstPoint</code>.
                 */
                buffer[length++] = (float) intersectPoint.x;
                buffer[length++] = (float) intersectPoint.y;
                /*
                 * On dispose maintenant des coordonn�es d'une ligne partant de la derni�re
                 * intersection jusqu'� la prochaine. Ces coordonn�es seront ajout�es au
                 * d�but ou � la fin du segment qui effectue la pr�c�dente intersection.
                 */
                buffer = XArrays.resize(buffer, length);
                if (lastIntersectPoint.append) {
                    lastIntersectPoint.path.appendBorder(buffer, 0, length);
                } else {
                    reverse(buffer);
                    lastIntersectPoint.path.prependBorder(buffer, 0, length);
                }
                if (Polyline.LOGGER.isLoggable(LEVEL)) {
                    final StringBuffer message = new StringBuffer("    Polyline[");
                    message.append(lastIntersectPoint.path.getPointCount());
                    message.append(" pts].");
                    message.append(lastIntersectPoint.append ? "append[" : "prepend[");
                    message.append(length/2);
                    message.append(" pts]");
                    Polyline.LOGGER.log(LEVEL, message.toString());
                }
            }
            lastIntersectPoint = intersectPoint;
            traceLine = !traceLine;
        }
        if (traceLine) {
            throw new AssertionError("Odd intersects");
        }
        assemblePolygons();
        for (int i=0; i<polylines.length; i++) {
            polylines[i].close();
        }
        /*
         * TODO: Enable the following code when it will have been more extensively tested.
         */
        if (false) {
            final Collection polygons = PolygonInclusion.process(polylines, progress);
            polylines = (Polyline[]) polygons.toArray(new Polyline[polygons.size()]);
        }
    }

    /**
     * Examine toutes les polylignes en m�moire et rattachent ensemble celles qui
     * semblent faire partie d'un m�me polygone ferm�. Cette m�thode est identique
     * � {@link #completePolygons(Point2D,boolean)}, except� qu'elle d�terminera elle-m�me
     * le point de r�f�rence � partir de l'isoligne sp�cifi�e. L'isoligne sp�cifi�e servira
     * uniquement de r�f�rence. Il ne doit pas �tre le m�me que celui qui a �t� sp�cifi�
     * lors de la construction de cet objet.
     *
     * @throws IllegalStateException si une erreur est survenue lors du traitement des isolignes.
     * @throws NullPointerException si <code>otherIsoline</code> est nul ou ne contient pas de donn�es.
     * @throws IllegalArgumentException Si un probl�me d'unit�s est survenu. En principe, cette erreur
     *         ne devrait pas se produre.
     * @throws TransformException if a transformation was needed and failed.
     */
    private void completePolygons(final GeometryCollection otherIsoline) throws TransformException {
        final int comparaison = collection.compareTo(otherIsoline);
        if (comparaison == 0) {
            throw new IllegalArgumentException("Same isoline level");
        }
        /*
         * 'refPt' contiendra le point de r�f�rence, qui servira � diff�rencier
         * la terre de la mer. La cr�ation d'un objet {@link Point2D.Double} est
         * n�cessaire car la m�thode {@link #updateFermions} �crasera les valeurs
         * des autres coordonn�es internes � cet objet.
         */
        final Point2D.Double refPt = new Point2D.Double();
        double minDistanceSq       = Double.POSITIVE_INFINITY;
        final PathIterator pit     = clip.getPathIterator(null, flatness);
        while (!pit.isDone()) {
            final boolean closed = nextSegment(pit);
            if (!isSingularity(pathLine)) {
                /*
                 * 'pathLine' repr�sente maintenant une des bordure de la carte.
                 * On recherchera maintenant le polygone qui se termine le plus
                 * pr�s de cette bordure.
                 */
                final Iterator iterator = getPolylines(otherIsoline).iterator();
                while (iterator.hasNext()) {
                    final Polyline jPath=(Polyline) iterator.next();
                    if (jPath.isEmpty()) {
                        continue;
                    }
                    boolean first = true;
                    do { // Cette boucle sera ex�cut�e deux fois
                        double distanceSq;
                        if (first) {
                            jPath.getFirstPoint(tmpPoint);
                        } else {
                            jPath.getLastPoint(tmpPoint);
                        }
                        /*
                         * Calcule la distance entre l'extr�mit�
                         * du polygone et la bordure de la carte.
                         */
                        final Point2D projected = ShapeUtilities.nearestColinearPoint(pathLine, tmpPoint);
                        distanceSq = distanceSq(tmpPoint, projected);
                        if (distanceSq < minDistanceSq) {
                            minDistanceSq = distanceSq;
                            refPt.setLocation(tmpPoint);
                        }
                    }
                    while ((first=!first) == false);
                }
            }
            if (closed) break;
        }
        completePolygons(refPt, comparaison < 0);
    }




    ///////////////////////////////////////////////////////////////////
    //////////                                               //////////
    //////////            M E A N   M E T H O D S            //////////
    //////////                                               //////////
    ///////////////////////////////////////////////////////////////////

    /**
     * Assemble the specified geometry collection. This method performs the following steps:
     *
     * <ul>
     *   <li>{@link #setGeometryCollection} for setting the collection to process.</li>
     *   <li>{@link #completePolygons} or {@link #assemblePolygons} for doing the work.</li>
     *   <li>{@link #updateGeometryCollection} for updating the collection set in first step.</li>
     * </ul>
     *
     * @param  parent     The geometry collection to assemble.
     * @param  references Siblers of the <code>parent</code> collection.
     * @param  toComplete {@linkplain GeometryCollection#getValue value} of collections to complete
     *         with map border. Usually, only the coast line is completed (<code>value==0</code>).
     * @throws TransformException if a transformation was needed and failed.
     */
    private void assemble(final GeometryCollection parent,
                          final List references,
                          final float[] toComplete)
            throws TransformException
    {
        synchronized (parent) {
            final List   collections = parent.extractCollections();
            final float  parentValue = parent.getValue();
            GeometryCollection refer = parent;
            float delta = Float.POSITIVE_INFINITY;
            for (final Iterator it=references.iterator(); it.hasNext();) {
                final GeometryCollection candidate = (GeometryCollection) it.next();
                final float check = Math.abs(candidate.getValue() - parentValue);
                if (check>0 && check<delta) {
                    refer = candidate;
                    delta = check;
                }
            }
            setGeometryCollection(parent);
            if (refer!=parent && Arrays.binarySearch(toComplete, parentValue)>=0) {
                completePolygons(refer);
            } else {
                assemblePolygons();
            }
            updateGeometryCollection();
            for (final Iterator it=collections.iterator(); it.hasNext();) {
                GeometryCollection child = (GeometryCollection) it.next();
                child = (GeometryCollection) child.clone();
                assemble(child, collections, toComplete);
                parent.add(child);
            }
        }
    }

    /**
     * Assemble the specified geometry collection.
     *
     * @param  collection  The geometry collection to assemble.
     * @param  toComplete {@linkplain GeometryCollection#getValue value} of collections to complete
     *         with map border. Usually, only the coast line is completed (<code>value==0</code>).
     * @throws TransformException if a transformation was needed and failed.
     */
    public void assemble(final GeometryCollection collection, float[] toComplete)
            throws TransformException
    {
        if (progress != null) {
            progress.setDescription("Analyzing"); // TODO: localize
            progress.started();
        }
        toComplete = (float[]) toComplete.clone();
        Arrays.sort(toComplete);
        assemble(collection, Collections.EMPTY_LIST, toComplete);
        if (progress != null) {
            progress.complete();
        }
    }
}
