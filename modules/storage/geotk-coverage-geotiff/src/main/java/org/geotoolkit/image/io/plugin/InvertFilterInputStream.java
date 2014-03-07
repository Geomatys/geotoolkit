/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.image.io.plugin;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author rmarechal
 */
class InvertFilterInputStream extends FilterInputStream {
    
    private final static int SHIFT_INT_TO_BYTE = 24;
    private final static int BYTE_MASK = 0xFF;
    
    InvertFilterInputStream(InputStream in){
        super(in);
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    @Override
    public int read() throws IOException {
        final int superInt = super.read(); 
        return (superInt < 0) ? superInt : invertByte(superInt);
    }

    /**
     * 
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException 
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int superInt = super.read(b, off, len);
        
        for (int p = off, maxPos = off + len; p < maxPos; p++) {
            b[p] = (byte) invertByte(b[p]);
        }
        return superInt;
    }
    
    private int invertByte(int b) {
        final int invertByte = Integer.reverse(b);
        return ((invertByte >>> SHIFT_INT_TO_BYTE) & BYTE_MASK);
    }
}
