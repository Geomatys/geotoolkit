/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007-2009, Geomatys
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
package org.geotoolkit.lucene.filter;

import java.io.IOException;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.FixedBitSet;
import org.geotoolkit.index.tree.Tree;

/**
 * 
 * Provide a serial chain filter, passing the bitset in with the
 * index reader to each of the filters in an ordered fashion.
 * 
 * Based off chain filter, but will some improvements to allow a narrowed down
 * filtering. Traditional filter required iteration through an IndexReader.
 * 
 * By implementing the ISerialChainFilter class, you can create a bits(IndexReader reader, BitSet bits)
 * 
 * 
 * @author Patrick O'Leary
 * @author Guilhem Legal
 * @module pending
 */
public class SerialChainFilter extends Filter implements  org.geotoolkit.lucene.filter.Filter {

    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8132561537335553911L;

    private final List<Filter> chain;
    
    public static final int AND     = 1;	     
    public static final int OR      = 2;   
    public static final int NOT     = 3;
    public static final int XOR     = 4;
    public static final int DEFAULT = OR;
	
    private final int[] actionType;

    public SerialChainFilter(final List<Filter> chain) {
        this.chain      = chain;
        this.actionType = new int[]{DEFAULT};
    }

    public SerialChainFilter(final List<Filter> chain, final int[] actionType) {
        this.chain      = chain;
        this.actionType = actionType.clone();
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Filter#bits(org.apache.lucene.index.IndexReader)
     */
    @Override
    public  DocIdSet getDocIdSet(final LeafReaderContext ctx, final Bits b) throws CorruptIndexException, IOException {

        final int chainSize  = chain.size();
        final int actionSize = actionType.length;

        final FixedBitSet bits    = (FixedBitSet) ((BitDocIdSet)chain.get(0).getDocIdSet(ctx, b)).bits();

        //if there is only an operand not we don't enter the loop
        int j = 0;
        if (actionType[j] == NOT) {
            bits.flip(0, ctx.reader().maxDoc());
            j++;
        }

        for (int i = 1; i < chainSize; i++) {

            int action;
            if (j < actionSize) {
                action = actionType[j];
                j++;
            } else {
                action = DEFAULT;
            }

            final FixedBitSet nextFilterResponse = (FixedBitSet) ((BitDocIdSet)chain.get(i).getDocIdSet(ctx, b)).bits();

            //if the next operator is NOT we have to process the action before the current operand
            if (j < actionSize && actionType[j] == NOT) {
                nextFilterResponse.flip(0, ctx.reader().maxDoc());
                j++;
            }

            switch (action) {

                case AND:
                    bits.and(nextFilterResponse);
                    break;
                case OR:
                    bits.or(nextFilterResponse);
                    break;
                case XOR:
                    bits.xor(nextFilterResponse);
                    break;
                default:
                    bits.or(nextFilterResponse);
                    break;

            }

        }
        // invalidate deleted document
        return invalidateDeletedDocument(bits, b);
    }

    private BitDocIdSet invalidateDeletedDocument(final BitSet results, final Bits initial) {
        if (initial != null) {
            for (int i = 0; i < initial.length(); i++) {
                if (!initial.get(i)) {
                    results.clear(i);
                }
            }
        }
        return new BitDocIdSet(results);
    }

      /**
     * @return the chain
     */
    public List<Filter> getChain() {
        return chain;
    }

    /**
     * @return the actionType
     */
    public int[] getActionType() {
        return actionType.clone();
    }

    /**
     * Return the flag correspounding to the specified filterName.
     * 
     * @param filterName A filter name : And, Or, Xor or Not.
     * 
     * @return an int flag.
     */
    public static int valueOf(final String filterName) {

        if (filterName.equals("And")) {
            return AND;
        } else if (filterName.equals("Or")) {
            return OR;
        } else if (filterName.equals("Xor")) {
            return XOR;
        } else if (filterName.equals("Not")) {
            return NOT;
        } else {
            return DEFAULT;
        }
    }

    /**
     * Return the filterName correspounding to the specified flag.
     * 
     * @param flag an int flag.
     * 
     * @return A filter name : And, Or, Xor or Not. 
     */
    public static String valueOf(final int flag) {
        switch (flag) {
            case AND:
                return "AND";
            case OR:
                return "OR";
            case NOT:
                return "NOT";
            case XOR:
                return "XOR";
            default:
               return "unknow";
        }
    }
    
    @Override
    public void applyRtreeOnFilter(final Tree rTree, final boolean envelopeOnly) {
        for (Filter f : chain) {
            if (f instanceof org.geotoolkit.lucene.filter.Filter) {
                ((org.geotoolkit.lucene.filter.Filter)f).applyRtreeOnFilter(rTree, envelopeOnly);
            }
        }
    }

    
    /** 
     * Returns true if <code>o</code> is equal to this.
     * 
     * @see org.apache.lucene.search.RangeFilter#equals
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        
        if (o instanceof SerialChainFilter) {
            final SerialChainFilter other = (SerialChainFilter) o;

            if (this.chain.size() != other.getChain().size() || 
                this.actionType.length != other.getActionType().length) {
                return false;
            }

            for (int i = 0; i < this.chain.size(); i++) {
                if (!this.chain.get(i).equals(other.getChain().get(i))) {
                    return false;
                }
            }
            for (int i = 0; i < this.actionType.length; i++) {
                if (this.actionType[i] != other.getActionType()[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.chain);
        hash = 37 * hash + Arrays.hashCode(this.actionType);
        return hash;
    }
    
    
    
    @Override
    public String toString(String s) {
    	final StringBuffer buf = new StringBuffer();
    	buf.append("[SerialChainFilter]").append('\n');
        if (chain != null && chain.size() == 1) {
            buf.append("NOT ").append('\n');
            buf.append('\t').append(chain.get(0));
            
        } else if (chain != null && chain.size() > 0) {
            buf.append('\t').append(chain.get(0)).append('\n');
            
            for (int i = 0; i < actionType.length; i++) {
                switch(actionType[i]) {
                    case AND:
                        buf.append("AND");
                        break;
                    case OR:
                        buf.append("OR");
                        break;
                    case NOT:
                        buf.append("NOT");
                        break;
                    case XOR:
                        buf.append("XOR");
                        break;
                    default:
                        buf.append(actionType[i]);
                }
                buf.append('\n');
                if (chain.size() > i + 1) {
                    buf.append('\t').append(" ").append(chain.get(i + 1)).append('\n');
                }
            }
        }
        buf.append('\n');
    	return buf.toString().trim();
    }
}
