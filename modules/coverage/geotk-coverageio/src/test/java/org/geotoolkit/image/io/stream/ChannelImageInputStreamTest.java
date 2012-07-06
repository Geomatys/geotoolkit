/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.stream;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.plugin.TextMatrixImageReader;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link ChannelImageInputStream}. A buffer is filled with random data
 * and a standard {@link ImageInputStream} is used for comparison purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final strictfp class ChannelImageInputStreamTest {
    /**
     * The maximal size of the arrays to be used for the tests, in bytes.
     */
    private static final int ARRAY_MAX_SIZE = 512;

    /**
     * Fills a buffer with random data and compare the result with a standard image input stream.
     * We will allocate a small buffer for the {@code ChannelImageInputStream} in order to force
     * frequent interactions between the buffer and the channel.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testWithRandomData() throws IOException {
        final long seed = System.nanoTime();
        final Random random = new Random(seed);
        final byte[] buffer = new byte[512 * 1024];
        for (int i=0; i<buffer.length; i++) {
            buffer[i] = (byte) random.nextInt(256);
        }
        final ImageInputStream r = ImageIO.createImageInputStream(new ByteArrayInputStream(buffer));
        final ImageInputStream t = new ChannelImageInputStream(Channels.newChannel(new ByteArrayInputStream(buffer)), 128);
        try {
            long position = 0;
            int bitOffset = 0;
            int operation = 0;
            try {
                while ((position = r.getStreamPosition()) < buffer.length - ARRAY_MAX_SIZE) {
                    bitOffset = r.getBitOffset();
                    operation = random.nextInt(25);
                    switch (operation) {
                        default: throw new AssertionError(operation);
                        case  0: assertEquals("read()",              r.read(),              t.read());              break;
                        case  1: assertEquals("readBoolean()",       r.readBoolean(),       t.readBoolean());       break;
                        case  2: assertEquals("readChar()",          r.readChar(),          t.readChar());          break;
                        case  3: assertEquals("readByte()",          r.readByte(),          t.readByte());          break;
                        case  4: assertEquals("readShort()",         r.readShort(),         t.readShort());         break;
                        case  5: assertEquals("readUnsignedShort()", r.readUnsignedShort(), t.readUnsignedShort()); break;
                        case  6: assertEquals("readInt()",           r.readInt(),           t.readInt());           break;
                        case  7: assertEquals("readUnsignedInt()",   r.readUnsignedInt(),   t.readUnsignedInt());   break;
                        case  8: assertEquals("readLong()",          r.readLong(),          t.readLong());          break;
                        case  9: assertEquals("readFloat()",         r.readFloat(),         t.readFloat(),  0f);    break;
                        case 10: assertEquals("readDouble()",        r.readDouble(),        t.readDouble(), 0d);    break;
                        case 11: assertEquals("readBit()",           r.readBit(),           t.readBit());           break;
                        case 12: {
                            final int n = random.nextInt(Long.SIZE + 1);
                            assertEquals("readBits(" + n + ')', r.readBits(n), t.readBits(n));
                            break;
                        }
                        case 13: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE);
                            final byte[] exp = new byte[length];
                            final byte[] act = new byte[length];
                            assertEquals("n of read(byte[])", r.read(exp), t.read(act));
                            assertArrayEquals("read(byte[])", exp, act);
                            break;
                        }
                        case 14: {
                            // Note: the reference stream is in violation with the ImageInputStream.readFully(...)
                            // specification since it doesn't reset the bit offset to zero. So we do that ourself.
                            r.setBitOffset(0);
                            final int length = random.nextInt(ARRAY_MAX_SIZE);
                            final byte[] exp = new byte[length]; r.readFully(exp);
                            final byte[] act = new byte[length]; t.readFully(act);
                            assertArrayEquals("readFully(byte[])", exp, act);
                            break;
                        }
                        case 15: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Character.SIZE);
                            final char[] exp = new char[length]; r.readFully(exp, 0, length);
                            final char[] act = new char[length]; t.readFully(act, 0, length);
                            assertArrayEquals("readFully(char[])", exp, act);
                            break;
                        }
                        case 16: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Short.SIZE);
                            final short[] exp = new short[length]; r.readFully(exp, 0, length);
                            final short[] act = new short[length]; t.readFully(act, 0, length);
                            assertArrayEquals("readFully(short[])", exp, act);
                            break;
                        }
                        case 17: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Integer.SIZE);
                            final int[] exp = new int[length]; r.readFully(exp, 0, length);
                            final int[] act = new int[length]; t.readFully(act, 0, length);
                            assertArrayEquals("readFully(int[])", exp, act);
                            break;
                        }
                        case 18: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Long.SIZE);
                            final long[] exp = new long[length]; r.readFully(exp, 0, length);
                            final long[] act = new long[length]; t.readFully(act, 0, length);
                            assertArrayEquals("readFully(long[])", exp, act);
                            break;
                        }
                        case 19: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Float.SIZE);
                            final float[] exp = new float[length]; r.readFully(exp, 0, length);
                            final float[] act = new float[length]; t.readFully(act, 0, length);
                            assertTrue("readFully(float[])", Arrays.equals(exp, act));
                            break;
                        }
                        case 20: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE * Byte.SIZE / Double.SIZE);
                            final double[] exp = new double[length]; r.readFully(exp, 0, length);
                            final double[] act = new double[length]; t.readFully(act, 0, length);
                            assertTrue("readFully(double[])", Arrays.equals(exp, act));
                            break;
                        }
                        case 21: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE);
                            r.skipBytes(length);
                            t.skipBytes(length);
                            break;
                        }
                        case 22: {
                            r.flush();
                            t.flush();
                            break;
                        }
                        case 23: {
                            r.setByteOrder(ByteOrder.BIG_ENDIAN);
                            t.setByteOrder(ByteOrder.BIG_ENDIAN);
                            break;
                        }
                        case 24: {
                            r.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                            t.setByteOrder(ByteOrder.LITTLE_ENDIAN);
                            break;
                        }
                    }
                    assertEquals("getStreamPosition()", r.getStreamPosition(), t.getStreamPosition());
                    assertEquals("getBitOffset()",      r.getBitOffset(),      t.getBitOffset());
                }
            } catch (AssertionError e) {
                final PrintStream err = System.err;
                err.println("Position:    " + position);
                err.println("Bit offset:  " + bitOffset);
                err.println("Byte order:  " + r.getByteOrder());
                err.println("Operation:   " + operation);
                err.println("Random seed: " + seed);
                err.println("Exception:   " + e.getLocalizedMessage());
                throw e;
            }
        } finally {
            t.close();
            r.close();
        }
    }

    /**
     * Tests the reading of a file. This method is mostly for testing the
     * {@link ChannelImageInputStream#skip(long)} method, which has a special
     * case for file stream.
     *
     * @throws IOException Should never happen.
     */
    @Test
    public void testUsingFile() throws IOException {
        final File file = TestData.file(TextMatrixImageReader.class, "matrix.txt");
        final FileInputStream in = new FileInputStream(file);
        final ImageInputStream r = ImageIO.createImageInputStream(new FileInputStream(file));
        final ImageInputStream t = new ChannelImageInputStream(in.getChannel(), 128);
        try {
            final long          seed = System.nanoTime();
            final Random      random = new Random(seed);
            final long  streamLength = t.length();
            assertTrue("length()", streamLength > 0);
            long position = 0;
            int bitOffset = 0;
            int operation = 0;
            try {
                while ((position = r.getStreamPosition()) < streamLength - ARRAY_MAX_SIZE) {
                    bitOffset = r.getBitOffset();
                    operation = random.nextInt(3);
                    switch (operation) {
                        default: throw new AssertionError(operation);
                        case  0: assertEquals("readBit()",  r.readBit(),  t.readBit());  break;
                        case  1: assertEquals("readByte()", r.readByte(), t.readByte()); break;
                        case  2: {
                            final int length = random.nextInt(ARRAY_MAX_SIZE);
                            r.skipBytes(length);
                            t.skipBytes(length);
                            break;
                        }
                    }
                    assertEquals("getStreamPosition()", r.getStreamPosition(), t.getStreamPosition());
                    assertEquals("getBitOffset()",      r.getBitOffset(),      t.getBitOffset());
                }
            } catch (AssertionError e) {
                final PrintStream err = System.err;
                err.println("Position:    " + position);
                err.println("Bit offset:  " + bitOffset);
                err.println("Operation:   " + operation);
                err.println("Random seed: " + seed);
                err.println("Exception:   " + e.getLocalizedMessage());
                throw e;
            }
        } finally {
            t.close();
            r.close();
            in.close();
        }
    }
}
