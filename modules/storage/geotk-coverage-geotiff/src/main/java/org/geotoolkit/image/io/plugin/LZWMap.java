/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.util.Arrays;
import java.util.HashMap;
import org.apache.sis.util.ArgumentChecks;

/**
 * Map adapted for LZW compression use.
 *
 * @author Remi Marechal (Geomatys).
 */
class LZWMap extends HashMap<byte[], Short> {

    /**
     * {@code byte[]} array use to store key.
     */
    private final byte[][] keys;

    /**
     * {@code short[]} array use to store LZW key associate code.
     */
    private final short[][] values;

    /**
     * Maximum key and value array length.
     */
    private final static int PRIME_NUMBER = 5407;

    /**
     * Default value array length.
     */
    private final static int DEFAULTVALUES_LENGTH = 20;

    LZWMap() {
        keys   = new byte[PRIME_NUMBER][];
        values = new short[PRIME_NUMBER][];
    }

    /**
     * Return true if container start with element byte suite else false.<br/><br/>
     *
     * @param container
     * @param element
     * @return
     */
    private boolean containAtBegin(final byte[] container, final byte[] element) {
        final int eltLength = element.length;
        if (eltLength > container.length) return false;
//            throw new IllegalArgumentException("element byte array should have length lesser than container length. element length = "+eltLength+" container length = "+container.length);
        for (int i = 0; i < element.length; i++) {
            if (element[i] != container[i]) return false;
        }
        return true;
    }

    /**
     * Put code in relation with key at expected position.
     *
     * @param position
     * @param eltLength
     * @param code
     */
    private void putCode(final int position, final int eltLength, final short code) {
        if (values[position] == null) {
            values[position] = new short[((eltLength + DEFAULTVALUES_LENGTH - 1) / DEFAULTVALUES_LENGTH) * DEFAULTVALUES_LENGTH];
        } else if (values[position].length <= eltLength - 2) {
            values[position] = Arrays.copyOf(values[position], values[position].length << 1);
        }
        values[position][eltLength - 2] = code;
    }

    /**
     * Add
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public Short put(final byte[] key, final Short value) {
        ArgumentChecks.ensureNonNull("key ", key);
        //-- hash code creation, constitute by the first pair of byte from key. --//
        final int hash = (((key[0] & 0xFF) << Byte.SIZE) | (key[1] & 0xFF));
        int arrayPos   = hash % PRIME_NUMBER;
        assert arrayPos >= 0;
        while (true) {
            if (keys[arrayPos] == null) {
                //-- add directly --//
                keys[arrayPos] = key;
                putCode(arrayPos, key.length, value);
                break;
            } else {
                // 2 case
                final byte[] precKey = keys[arrayPos];
                if (containAtBegin(precKey, key)) {
                    //-- if key is already contained --//
                    break;
                } else if (containAtBegin(key, precKey)) {
                    keys[arrayPos] = key;
                    putCode(arrayPos, key.length, value);
                    break;
                } else {
                    arrayPos++;
                    if (arrayPos == PRIME_NUMBER) {
                        assert assertArrayConform() : "keys array must contain at least null value";
                        arrayPos = 0;
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Return {@code true} if key as already been put else {@code false}.
     *
     * @param key
     * @return {@code true} if key as already been put else {@code false}.
     */
    @Override
    public boolean containsKey(Object key) {
        ArgumentChecks.ensureNonNull("key ", key);
        if (!(key instanceof byte[]))
            throw new IllegalArgumentException("key must be instance of byte[]");
        final byte[] ki = (byte[]) key;
        //-- hash code creation, constitute by the first pair of byte from key. --//
        final int hash = (((ki[0] & 0xFF) << Byte.SIZE) | (ki[1] & 0xFF));
        int arrayPos   = hash % PRIME_NUMBER;
        assert arrayPos >= 0 : "expected : >= 0 found : "+(hash % PRIME_NUMBER)+" hash code : "+hash;

        while (keys[arrayPos] != null) {
            if (containAtBegin(keys[arrayPos], ki)) return true;
            arrayPos++;
            if (arrayPos == PRIME_NUMBER) {
                assert assertArrayConform() : "keys array must contain at least null value";
                arrayPos = 0;
            }
        }
        return false;
    }

    /**
     * Return {@code true} if {@linkplain #keys} array contain at least one {@code null} value else return {@code false}.<br/>
     * Normaly should never return {@code false}.
     *
     * @return {@code true} if {@linkplain #keys} array contain at least one {@code null} value else return {@code false}.
     */
    private boolean assertArrayConform() {
        final int keysLength = keys.length;
        for (int i = 0; i < keysLength; i++) {
            if (keys[i] == null) return true;
        }
        return false;
    }

    /**
     * Return LZW code in relation with {@code byte[]} key if it is found
     * else return 256 which is a reserved code.
     *
     * @param key
     * @return short value which is LZW code in relation with specified key.
     * @throws {@link NullArgumentException} if key is {@code null}.
     * @throws {@link IllegalArgumentException} if key is not instance of {@code byte[]}.
     */
    @Override
    public Short get(Object key) {
        ArgumentChecks.ensureNonNull("key ", key);
        if (!(key instanceof byte[]))
            throw new IllegalArgumentException("key must be instance of byte[]");
        final byte[] ki = (byte[]) key;
        //-- hash code creation, constitute by the first pair of byte from key. --//
        final int hash = (((ki[0] & 0xFF) << Byte.SIZE) | (ki[1] & 0xFF));
        int arrayPos   = hash % PRIME_NUMBER;
        assert arrayPos >= 0;

        while (keys[arrayPos] != null) {
            if (containAtBegin(keys[arrayPos], ki)) return values[arrayPos][ki.length - 2];
            arrayPos++;
            if (arrayPos == PRIME_NUMBER) {
                assert assertArrayConform() : "keys array must contain at least null value";
                arrayPos = 0;
            }
        }
        /*
         * In LZW compression adapted for tiff image, short value 256 is a reserved value.
         * If value is not find from key, return 256 to stipulate that value is not find.
         */
        return 256;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
    }
}
