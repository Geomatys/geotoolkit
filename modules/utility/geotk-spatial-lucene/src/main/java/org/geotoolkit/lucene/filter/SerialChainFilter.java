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
import java.util.BitSet;

import java.util.List;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

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
public class SerialChainFilter extends Filter {

    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8132561537335553911L;

    private List<Filter> chain;
    
    public static final int AND     = 1;	     
    public static final int OR      = 2;   
    public static final int NOT     = 3;
    public static final int XOR     = 4;
    public static final int DEFAULT = OR;
	
    private int[] actionType;

    public SerialChainFilter(List<Filter> chain) {
        this.chain      = chain;
        this.actionType = new int[]{DEFAULT};
    }

    public SerialChainFilter(List<Filter> chain, int[] actionType) {
        this.chain      = chain;
        this.actionType = actionType.clone();
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.search.Filter#bits(org.apache.lucene.index.IndexReader)
     */
    @Override
    public BitSet bits(IndexReader reader) throws CorruptIndexException, IOException {

        final int chainSize  = chain.size();
        final int actionSize = actionType.length;

        final BitSet bits    = chain.get(0).bits(reader);

        //if there is only an operand not we don't enter the loop
        int j = 0;
        if (actionType[j] == NOT) {
            bits.flip(0, reader.maxDoc());
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

            final BitSet nextFilterResponse = chain.get(i).bits(reader);

            //if the next operator is NOT we have to process the action before the current operand
            if (j < actionSize && actionType[j] == NOT) {
                nextFilterResponse.flip(0, reader.maxDoc());
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
        return bits;
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
    
    /** 
     * Returns true if <code>o</code> is equal to this.
     * 
     * @see org.apache.lucene.search.RangeFilter#equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerialChainFilter)) return false;
        final SerialChainFilter other = (SerialChainFilter) o;

        if (this.chain.size() != other.getChain().size() ||
        	this.actionType.length != other.getActionType().length)
        	return false;
        
        for (int i = 0; i < this.chain.size(); i++) {
            if (this.actionType[i] != other.getActionType()[i]  || !this.chain.get(i).equals(other.getChain().get(i)))
                return false;
        }
        return true;
    }
    
    /** 
     * Returns a hash code value for this object.
     * 
     * @see org.apache.lucene.search.RangeFilter#hashCode
     */
    @Override
    public int hashCode() {
      if (chain.size() == 0)
    	  return 0;

      int h = chain.get(0).hashCode() ^ Integer.valueOf(actionType[0]).hashCode();
      for (int i = 1; i < this.chain.size(); i++) {
    	  h ^= chain.get(i).hashCode();
    	  h ^= Integer.valueOf(actionType[i]).hashCode();
      }

      return h;
    }
    
    @Override
    public String toString() {
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
                if (chain.size() > i + 1)
                    buf.append('\t').append(" " + chain.get(i + 1).toString()).append('\n');
            }
        }
        buf.append('\n');
    	return buf.toString().trim();
    }
}
