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

package org.geotoolkit.display2d.style.labeling.intelligent;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AIUtilities {

    public static boolean intersects(Candidate candidate1, Candidate candidate2){
        if(candidate1 instanceof PointCandidate){
            if(candidate2 instanceof PointCandidate){
                return intersects((PointCandidate)candidate1, (PointCandidate)candidate2);
            }else if(candidate2 instanceof LinearCandidate){
                return intersects((LinearCandidate)candidate2, (PointCandidate)candidate1);
            }
        }else if(candidate1 instanceof LinearCandidate){
            if(candidate2 instanceof PointCandidate){
                return intersects((LinearCandidate)candidate1, (PointCandidate)candidate2);
            }else if(candidate2 instanceof LinearCandidate){
                return intersects((LinearCandidate)candidate1, (LinearCandidate)candidate2);
            }
        }

        throw new IllegalArgumentException("Unexpected Candidate classes.");
    }

    public static boolean intersects(PointCandidate label1, PointCandidate label2){

        final AffineTransform trs = new AffineTransform();
        trs.translate(0, label1.upper);
        trs.rotate(-Math.toRadians(label1.getDescriptor().getRotation()));
        trs.translate(-label1.x, -label1.y);
        // at this step the label1 is horizontal and with start coordinate at (0,0)

        trs.translate(label2.x, label2.y);
        trs.rotate(Math.toRadians(label2.getDescriptor().getRotation()));
        trs.translate(0, -label2.upper);
        //at this step the label2 is in the label1 normalize area
        
        final int label1Height = label1.upper+label1.lower;
        final int label2Height = label2.upper+label2.lower;

        Point2D p1 = new Point2D.Double(0,             0);
        Point2D p2 = new Point2D.Double(0,             label2Height);
        Point2D p3 = new Point2D.Double(label2.width,  0);
        Point2D p4 = new Point2D.Double(label2.width,  label2Height);

        p1 = trs.transform(p1, p1);
        p2 = trs.transform(p2, p2);
        p3 = trs.transform(p3, p3);
        p4 = trs.transform(p4, p4);

        if( (p1.getX()<=0 && p2.getX()<=0 && p3.getX()<=0 && p4.getX()<=0)
             || (p1.getX()>=label1.width && p2.getX()>=label1.width && p3.getX()>=label1.width && p4.getX()>=label1.width)){
            //label2 is on the left or on the right of label1
            return false;
        }

        if( (p1.getY()<=0 && p2.getY()<=0 && p3.getY()<=0 && p4.getY()<=0)
             || (p1.getY()>=label1Height && p2.getY()>=label1Height && p3.getY()>=label1Height && p4.getY()>=label1Height)){
            //label2 is above or under label1
            return false;
        }

        final Rectangle2D rect = new Rectangle2D.Double(0, 0, label1.width, label1Height);

        //test points within label1
        if(rect.contains(p1)) return true;
        if(rect.contains(p2)) return true;
        if(rect.contains(p3)) return true;
        if(rect.contains(p4)) return true;

        //test border intersection
        if(rect.intersectsLine(p1.getX(), p1.getY(), p2.getX(), p2.getY())) return true;
        if(rect.intersectsLine(p2.getX(), p2.getY(), p4.getX(), p4.getY())) return true;
        if(rect.intersectsLine(p4.getX(), p4.getY(), p3.getX(), p3.getY())) return true;
        if(rect.intersectsLine(p3.getX(), p3.getY(), p1.getX(), p1.getY())) return true;

        //check that the polygon is not contained in the other
        double minx = Math.min( Math.min(p1.getX(), p2.getX()), Math.min(p3.getX(), p4.getX()) );
        double maxx = Math.max( Math.max(p1.getX(), p2.getX()), Math.max(p3.getX(), p4.getX()) );
        double miny = Math.min( Math.min(p1.getY(), p2.getY()), Math.min(p3.getY(), p4.getY()) );
        double maxy = Math.max( Math.max(p1.getY(), p2.getY()), Math.max(p3.getY(), p4.getY()) );

        if(minx <=0 && maxx >= label1.width && miny <=0 && maxy >= label1Height){
            return true;
        }


        return false;
    }

    public static boolean intersects(LinearCandidate linear, PointCandidate point){
        return false;
    }

    public static boolean intersects(LinearCandidate linear1, LinearCandidate linear2){
        return false;
    }

}
