/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.index.tree.DefaultTreeUtils.*;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.Node.PROP_HILBERT_ORDER;
import org.geotoolkit.index.tree.hilbert.HilbertIterator;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

/**
 *
 * @author rmarechal
 */
public class CalculatorND extends Calculator {

    public CalculatorND() {
        super(null);
    }

    
    @Override
    public double getSpace(Envelope envelop) {//volume 
        final int dim = envelop.getDimension();
        if (dim <= 2) return getGeneralEnvelopArea(envelop);
        return getGeneralEnvelopBulk(envelop);
    }

    @Override
    public double getEdge(Envelope envelop) {//perimetre
        final int dim = envelop.getDimension();
        if (dim <= 2) return getGeneralEnvelopPerimeter(envelop);
        return getGeneralEnvelopArea(envelop);
    }

    @Override
    public double getDistance(Envelope envelopA, Envelope envelopB) {
        return getDistance(getMedian(envelopA), getMedian(envelopB));
    }

    @Override
    public double getDistance(DirectPosition positionA, DirectPosition positionB) {
        return getDistanceBetween2DirectPosition(positionA, positionB);
    }

    @Override
    public double getDistance(Node nodeA, Node nodeB) {
        return getDistance(nodeA.getBoundary(), nodeB.getBoundary());
    }

    @Override
    public double getOverlaps(Envelope envelopA, Envelope envelopB) {//pourcentage
        final int dim = envelopA.getDimension();
        assert (dim == envelopB.getDimension()) : "dimension not equals";
        final GeneralEnvelope union = new GeneralEnvelope(envelopA);
        union.add(envelopB);
        final GeneralEnvelope intersection = new GeneralEnvelope(envelopA);
        intersection.intersects(envelopB, true);
        double ratio = 1;
        for (int d = 0; d < dim; d++) {
            ratio *= (intersection.getSpan(d) / union.getSpan(d));
        }
        return ratio;
    }

    @Override
    public double getEnlargement(Envelope envMin, Envelope envMax) {
        final int dim = envMin.getDimension();
        assert (dim == envMax.getDimension()) : "dimension not equals";
        //paranoicUnion
        final GeneralEnvelope union = new GeneralEnvelope(envMin);
        union.add(envMax);//normaly equal to envMax.
        double ratio = 1;
        for (int d = 0; d < dim; d++) {
            ratio *= (union.getSpan(d)/envMin.getSpan(d));
        }
        return ratio;
    }
    
    /**
     * Find {@code DirectPosition} Hilbert coordinate from this Node.
     *
     * @param pt {@code DirectPosition}
     * @throws IllegalArgumentException if parameter "dPt" is out of this node
     * boundary.
     * @throws IllegalArgumentException if parameter dPt is null.
     * @return int[] table of length 3 which contains 3 coordinates.
     */
    public int[] getHilbCoord(final Node candidate, final DirectPosition dPt, final Envelope envelop, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("DirectPosition dPt : ", dPt);
        if (!new GeneralEnvelope(envelop).contains(dPt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final Calculator calc = candidate.getTree().getCalculator();
        assert calc instanceof CalculatorND : "getHilbertCoord : calculatorND type required";
        final double div  = 2 << hilbertOrder - 1;
        
        List<Integer> lInt = new ArrayList<Integer>();
        
        for(int d = 0; d < envelop.getDimension(); d++){
            final double span = envelop.getSpan(d);
            if (span <= 1E-9) continue;
            final double currentDiv = span/div;
            int val = (int) (Math.abs(dPt.getOrdinate(d) - envelop.getMinimum(d)) / currentDiv);
            if (val == div) val--;
            lInt.add(val);
        }
        final int[] result = new int[lInt.size()];
        int i = 0;
        for (Integer val : lInt) result[i++] = val;
        return result;
    }
    
    
    @Override
    public int getHVOfEntry(Node candidate, Envelope entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final DirectPosition ptCE = getMedian(entry);
        final GeneralEnvelope bound = new GeneralEnvelope(candidate.getBoundary());
        final int order = (Integer) candidate.getUserProperty(PROP_HILBERT_ORDER);
        if (! bound.contains(ptCE)) throw new IllegalArgumentException("entry is out of this node boundary");
        
        int[] hCoord = getHilbCoord(candidate, ptCE, bound, order);
        final int spaceHDim = hCoord.length;
        
        if (spaceHDim == 1) return hCoord[0];
        
        final HilbertIterator hIt = new HilbertIterator(order, spaceHDim);
        int hilberValue = 0;
        while (hIt.hasNext()) {
            final int[] currentCoords = hIt.next();
            if (Arrays.equals(hCoord, currentCoords)) return hilberValue;
            hilberValue++;
        }
        throw new IllegalArgumentException("should never throw");
    }
    
}
