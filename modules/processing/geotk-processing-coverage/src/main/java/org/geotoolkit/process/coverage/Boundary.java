/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.process.coverage;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.util.NumberRange;

/**
 * Define an "in construction" geometries.
 * This type of objects contain a list of floeatings coordinates sequences
 * that are progressivly merged together to obtain a polygon which might contain holes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Boundary {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final LinearRing[] EMPTY_RING_ARRAY = new LinearRing[0];

    //finished geometries
    private final List<LinearRing> holes = new ArrayList<LinearRing>();

    //in construction geometries
    private final LinkedList<LinkedList<Coordinate>> floatings = new LinkedList<LinkedList<Coordinate>>();
    public final NumberRange range;

    public Boundary(NumberRange range){
        this.range = range;
    }
    
    public void start(int firstX, int secondX, int y){
        if(firstX == secondX) throw new IllegalArgumentException("bugging algorithm");
        final LinkedList<Coordinate> exterior = new LinkedList<Coordinate>();
        exterior.addFirst(new Coordinate(firstX, y));
        exterior.addFirst(new Coordinate(firstX, y+1));
        exterior.addLast(new Coordinate(secondX, y));
        exterior.addLast(new Coordinate(secondX, y+1));
        floatings.add(exterior);

        //checkValidity();
    }

    public void addFloating(final Coordinate from, final Coordinate to){
        if(from.equals2D(to)){
            throw new IllegalArgumentException("bugging algorithm");
        }
        //System.err.println("Add Floating From: " + from + " New : " + to );

        final LinkedList<Coordinate> ll = new LinkedList<Coordinate>();
        ll.addFirst(to);
        ll.addLast(from);
        floatings.add(ll);

        //checkValidity();
    }

    public void add(final Coordinate from, final Coordinate to){
        if(from.equals2D(to)){
            throw new IllegalArgumentException("bugging algorithm");
        }

        //System.err.println("Add From: " + from + " New : " + to );


        for(final LinkedList<Coordinate> ll : floatings){
            final Coordinate first = ll.getFirst();
            if(first.equals2D(from)){

                if(from.x == to.x && ll.size() > 1){
                    final Coordinate second = ll.get(1);
                    if(second.x == from.x){
                        //points are aligned, just move the first point
                        first.y = to.y;
                    }else{
                        //points are not aligned, must create a new point
                        ll.addFirst(to);
                    }
                }else if(from.y == to.y && ll.size() > 1){
                    final Coordinate second = ll.get(1);
                    if(second.y == from.y){
                        //points are aligned, just move the first point
                        first.x = to.x;
                    }else{
                        //points are not aligned, must create a new point
                        ll.addFirst(to);
                    }
                }else{
                    ll.addFirst(to);
                }

                //checkValidity();
                return;
            }

            final Coordinate last = ll.getLast();
            if(last.equals2D(from)){
                if(from.x == to.x && ll.size() > 1){
                    final Coordinate second = ll.get(ll.size()-2);
                    if(second.x == from.x){
                        //points are aligned, just move the first point
                        last.y = to.y;
                    }else{
                        //points are not aligned, must create a new point
                        ll.addLast(to);
                    }
                }else if(from.y == to.y && ll.size() > 1){
                    final Coordinate second = ll.get(ll.size()-2);
                    if(second.y == from.y){
                        //points are aligned, just move the first point
                        last.x = to.x;
                    }else{
                        //points are not aligned, must create a new point
                        ll.addLast(to);
                    }
                }else{
                    ll.addLast(to);
                }

                //checkValidity();
                return;
            }
        }

        //checkValidity();
        throw new IllegalArgumentException("bugging algorithm");
    }

    public Polygon link(final Coordinate from, final Coordinate to){
        if(from.equals2D(to)){
            throw new IllegalArgumentException("bugging algorithm : " + to);
        }

        //System.err.println("Link From: " + from + " to : " + to );


        LinkedList<Coordinate> fromList = null;
        boolean fromStart = false;
        LinkedList<Coordinate> toList = null;
        boolean toStart = false;

        for(final LinkedList<Coordinate> ll : floatings){

            if(fromList == null){
                final Coordinate first = ll.getFirst();
                final Coordinate last = ll.getLast();
                if(first.equals2D(from)){
                    fromStart = true;
                    fromList = ll;
                }else if(last.equals2D(from)){
                    fromStart = false;
                    fromList = ll;
                }
            }

            if(toList == null){
                final Coordinate first = ll.getFirst();
                final Coordinate last = ll.getLast();
                if(first.equals2D(to)){
                    toStart = true;
                    toList = ll;
                }else if(last.equals2D(to)){
                    toStart = false;
                    toList = ll;
                }
            }

            if(fromList != null && toList != null) break;
        }


        if(fromList != null && toList != null){
            if(fromList == toList){
                //same list finish it
                //checkValidity();
                return finish(fromList);
            }else{
                combine(fromList, fromStart, toList, toStart);
                //checkValidity();
                return null;
            }

        }else if(fromList != null ){
            add(from, to);
            //checkValidity();
            return null;
        }else if(toList != null){
            add(to, from);
            //checkValidity();
            return null;
        }

        //checkValidity();
        throw new IllegalArgumentException("bugging algorithm");
    }

    private void combine(LinkedList<Coordinate> fromList, boolean fromStart,
                         LinkedList<Coordinate> toList, boolean toStart){

        if(fromStart){
            if(toStart){
                while(!toList.isEmpty()){
                    fromList.addFirst(toList.pollFirst());
                }
            }else{
                while(!toList.isEmpty()){
                    fromList.addFirst(toList.pollLast());
                }
            }

        }else{
            if(toStart){
                while(!toList.isEmpty()){
                    fromList.addLast(toList.pollFirst());
                }
            }else{
                while(!toList.isEmpty()){
                    fromList.addLast(toList.pollLast());
                }
            }
        }

        floatings.remove(toList);
        //checkValidity();
    }


    public void checkValidity(){

        //check for list with less than 2 elements
        for(LinkedList<Coordinate> ll : floatings){
            if(ll.size() < 2){
                //System.err.println(">>>> ERROR : " + this.toString());
                throw new IllegalArgumentException("What ? a list with less than 2 elements, not valid !");
            }
        }

        //check for diagonal cases
        for(LinkedList<Coordinate> ll : floatings){
            Coordinate last = ll.get(0);
            for(int i=1;i<ll.size();i++){
                Coordinate current = ll.get(i);
                if(last.x != current.x && last.y != current.y){
                    //System.err.println(">>>> ERROR : " + this.toString());
                    throw new IllegalArgumentException("What ? a diagonal, not valid !");
                }
                last = current;
            }
        }

    }



    private Polygon finish(LinkedList<Coordinate> coords){

        if(floatings.size() == 1){
            //closing the polygon enveloppe
            //copy first point at the end
            coords.add(new Coordinate(coords.get(0)));

            final Coordinate[] ring = coords.toArray(new Coordinate[coords.size()]);
            final LinearRing exterior = GF.createLinearRing(ring);
            final Polygon polygon = GF.createPolygon(exterior, holes.toArray(EMPTY_RING_ARRAY));
            polygon.setUserData(range);
            floatings.remove(coords);
            //checkValidity();
            return polygon;
        }else{
            //closing a hole in the geometry
            //copy first point at the end
            coords.add(new Coordinate(coords.get(0)));

            final Coordinate[] ring = coords.toArray(new Coordinate[coords.size()]);
            holes.add(GF.createLinearRing(ring));
            floatings.remove(coords);
            //checkValidity();
            return null;
        }

    }

    private static void reverse(Coordinate[] array){
        for(int l=0, r=array.length-1; l<r; l++, r--) {
            Coordinate temp = array[l];
            array[l] = array[r];
            array[r] = temp;
        }
    }

    public Polygon merge(Boundary candidate){

        //System.err.println("M > before : " + toString());
        //System.err.println("M > with : " + candidate.toString());

        //merge the floating sequences
        this.floatings.addAll(candidate.floatings);

        //merge the holes
        this.holes.addAll(candidate.holes);

        candidate.floatings.clear();
        candidate.holes.clear();

        //System.err.println("M > after : " + toString());
        //checkValidity();
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Boundary : ");
        sb.append(range.toString());

        for(LinkedList<Coordinate> coords : floatings){
            sb.append("  \t{");
            for(Coordinate c : coords){
                sb.append("["+Double.valueOf(c.x).intValue()+";"+Double.valueOf(c.y).intValue()+"]");
            }
            sb.append("}");
        }

        return sb.toString();
    }

    public boolean isEmpty(){
        return floatings.isEmpty();
    }

}
