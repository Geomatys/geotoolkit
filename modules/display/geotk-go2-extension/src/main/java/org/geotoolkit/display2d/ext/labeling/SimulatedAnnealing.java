/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.display2d.ext.labeling;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.display2d.style.labeling.candidate.Candidate;
import org.geotoolkit.display2d.style.labeling.candidate.LabelingUtilities;
import org.geotoolkit.display2d.style.labeling.candidate.PointCandidate;

/**
 * Not effective implementation of simulated annealing.
 * Really slow and no good result, experimentale.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class SimulatedAnnealing {

    private SimulatedAnnealing(){
    }

    public static Set<Candidate> simulate(List<Candidate> cdts, double temperature, double coolDown){

        final Map<Candidate,Point> candidates = new HashMap<Candidate,Point>();
        for(Candidate cdt : cdts){
            candidates.put(cdt, new Point(
                    (int) Math.floor(Math.random() * temperature - temperature/2),
                    (int) Math.floor(Math.random() * temperature - temperature/2)));
        }

        //calculate the cost without any changes
        for(Candidate c : candidates.keySet()){
            Point p = candidates.get(c);
            c.setCost(solutionCost(c, p, candidates));
        }

//        double bestSolutionCost = solutionCost(candidates);
        int sameResult = 0;

        int count = 0;
        //when we reach 10 times the same result, that means we haven't find anything better
        while(sameResult < 10){
            System.out.println(count++ +" "+ sameResult);

            boolean change = false;

            for(Candidate c : candidates.keySet()){
                if(!(c instanceof PointCandidate)) continue;

                Point p = candidates.get(c);

                //TODO find the best local solution using label intersecting this label
                boolean better = findBestLocalCombinaison((PointCandidate)c, p, candidates,temperature);

                if(better){
                    System.out.println("found better");
                    change = true;
                }

            }

            sameResult = (change) ? 0 : sameResult+1;


            //This approach of a global solution cost to much CPU,
            //we must find a way to make a local area best solution
//            // See if this improved the global solution
//            double solutionCost = solutionCost(candidates);
//            if (solutionCost < bestSolutionCost) {
//                bestSolutionCost = solutionCost;
//
//                //solution is better, change the candidate displacements
//                for(Candidate c : candidates.keySet()){
//                    Point p = candidates.get(c);
//                    if(c instanceof PointCandidate){
//                        PointCandidate pc = (PointCandidate) c;
//                        pc.x += pc.correctionX;
//                        pc.y += pc.correctionY;
//                        pc.correctionX = 0;
//                        pc.correctionY = 0;
//                    }
//                }
//
//                sameResult = 0;
//            } else {
//                sameResult++;
//            }

            //reduce temperature
            temperature = coolDown * temperature;
        }

        for(Candidate c : candidates.keySet()){
            Point p = candidates.get(c);
            if(c instanceof PointCandidate){
                PointCandidate pc = (PointCandidate) c;
                pc.correctionX = p.x;
                pc.correctionY = p.y;
            }
        }

        return candidates.keySet();
    }

    private static boolean findBestLocalCombinaison(PointCandidate c, Point p, Map<Candidate,Point> candidates, double temperature){

        Map<PointCandidate,Point> localCandidates = new HashMap<PointCandidate,Point>();
        localCandidates.put(c, new Point(p));

        //search for the candidates that intersect the current candidate
        for(Candidate other : candidates.keySet()){
            if(other == c) continue;
            if(!(other instanceof PointCandidate)) continue;

            Point otherPoint = candidates.get(other);

            if(LabelingUtilities.intersects((PointCandidate) other,otherPoint, c, p, false)){
                localCandidates.put((PointCandidate) other, new Point(otherPoint));
            }
        }

        if(localCandidates.size() > 1){
            final int originalCost = solutionLocalCost(localCandidates,candidates);
            int cost = originalCost;

            //search the best solution
            int sameResult = 0;

            while(sameResult < 5){
                for(Candidate cdt : localCandidates.keySet()){
                    Point pt = localCandidates.get(cdt);
                    pt.x = (int) Math.floor(Math.random() * temperature - temperature/2);
                    pt.y = (int) Math.floor(Math.random() * temperature - temperature/2);
                }

                int acost = solutionLocalCost(localCandidates,candidates);

                if(acost < cost){
                    cost = acost;
                    sameResult = 0;
                    for(Candidate cdt : localCandidates.keySet()){
                        Point pt = localCandidates.get(cdt);
                        candidates.get(cdt).setLocation(pt);
                    }
                }else{
                    sameResult++;
                }

            }

            return cost < originalCost;
        }else{
            return false;
        }
        
    }

    /**
     * Called to determine if annealing should take place.
     *
     * @param d The distance.
     * @return True if annealing should take place.
     */
    public boolean anneal(double d, double temperature) {
        if (temperature < 1.0E-4) {
            return (d > 0.0);
        }
        return (Math.random() < Math.exp(d / temperature));
    }

    private static int solutionCost(Map<? extends Candidate,Point> candidates){
        int d = 0;

        for(Candidate c : candidates.keySet()){
            Point p = candidates.get(c);
            d += solutionCost(c, p, candidates);
        }

        return d;
    }

    private static int solutionLocalCost(Map<? extends Candidate,Point> localCandidates, Map<? extends Candidate,Point> candidates){
        int d = 0;

        for(Candidate c : localCandidates.keySet()){
            Point p = localCandidates.get(c);
            d += solutionCost(c, p, candidates);
        }

        return d;
    }


    private static int solutionCost(Candidate candidate, Point p, Map<? extends Candidate,Point> candidates){
        int d = Math.abs(p.x) + Math.abs(p.y);

        for(Candidate other : candidates.keySet()){
            if(other == candidate) continue;

            if(candidate instanceof PointCandidate && other instanceof PointCandidate){
                Point otherPoint = candidates.get(other);

                if(candidate.getPriority() > other.getPriority()){
                    continue;
                }else if(candidate.getPriority() == other.getPriority()){
                    if(LabelingUtilities.intersects((PointCandidate)candidate, p, (PointCandidate)other, otherPoint,false)){
                        d += 10;
                    }
                }else{
                    if(LabelingUtilities.intersects((PointCandidate)candidate, p, (PointCandidate)other, otherPoint,false)){
                        d += 20;
                    }
                }
            }
        }

        return d;
    }

}
