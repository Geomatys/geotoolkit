/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree.manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import org.geotoolkit.index.tree.FileTreeElementMapper;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Mapper to store on hard drive all inserted Tree data.<br/>
 * In this case stored data type is {@link NamedEnvelope}.
 *
 * @author Remi Marechal (Geomatys).
 */
public final class LuceneFileTreeEltMapper extends FileTreeElementMapper<NamedEnvelope> {

    /**
     * File name of file which store all {@link NamedEnvelope#id}.
     */
    private final static String ID_MAP_NAME = "idMap.bin";
    
    /**
     * Mutual Coordinate Reference System from all stored NamedEnvelopes.
     */
    private final CoordinateReferenceSystem crs;
    
    /**
     * CRS dimension.
     */
    private final int dim;
    
    /**
     * Stream to read and write all {@link NamedEnvelope#id} at the mapIndex position.
     */
    private final RandomAccessFile idMapInOutStream;
    
    /**
     * Byte file position of first stored {@link NamedEnvelope#id}. 
     */
    private final int beginPosition;
    
    /**
     * Byte file position, just after last stored {@link NamedEnvelope#id}. 
     */
    private long idMapCurrentPosition;
    
    /**
     * Create a new Tree Mapper adapted to Lucene Tree use case.
     * 
     * @param crs 
     * @param mapperOutPut path where to store all Tree data.
     * @throws IOException if pblem during head file writing.
     */
    public LuceneFileTreeEltMapper(final CoordinateReferenceSystem crs, final File mapperOutPut) throws IOException {
        super(mapperOutPut, ((crs.getCoordinateSystem().getDimension() << 1) * Double.SIZE + Integer.SIZE + Long.SIZE) >> 3);
        final File idMapOutPut = new File(mapperOutPut.getParent(), ID_MAP_NAME);
        idMapInOutStream = new RandomAccessFile(idMapOutPut, "rw");
        
        // prepare to store idMapCurrentPosition during close() call.
        idMapInOutStream.writeLong(-1);
        
        // write crs
        final ByteArrayOutputStream temp   = new ByteArrayOutputStream();
        final ObjectOutputStream objOutput = new ObjectOutputStream(temp);
        objOutput.writeObject(crs);
        objOutput.flush();
        final byte[] crsByteArray = temp.toByteArray();
        idMapInOutStream.writeInt(crsByteArray.length);
        idMapInOutStream.write(crsByteArray);
        objOutput.close();
        this.beginPosition = (int) idMapInOutStream.getChannel().position();
        this.dim           = crs.getCoordinateSystem().getDimension();
        this.crs           = crs;
        this.idMapCurrentPosition = beginPosition;
    }
    
    /**
     * Build an approriate Tree Mapper from an already filled Mapper file. 
     * 
     * @param mapperInput File path which contain already filled {@link File}.
     * @throws IOException if pblem during file head reading
     * @throws ClassNotFoundException if pblem during crs file head reading.
     */
    public LuceneFileTreeEltMapper(final File mapperInput) throws IOException, ClassNotFoundException {
        super(mapperInput);
        final File idMapOutPut = new File(mapperInput.getParent(), ID_MAP_NAME);
        idMapInOutStream = new RandomAccessFile(idMapOutPut, "rw");
        this.idMapCurrentPosition = idMapInOutStream.readLong();
        if (idMapCurrentPosition == -1)
            throw new IllegalStateException("You should call close method before.");
        // read CRS
        final int byteTabLength           = idMapInOutStream.readInt();
        final byte[] crsByteArray         = new byte[byteTabLength];
        idMapInOutStream.read(crsByteArray, 0, byteTabLength);
        final ObjectInputStream crsInputS = new ObjectInputStream(new ByteArrayInputStream(crsByteArray));
        this.crs                          = (CoordinateReferenceSystem) crsInputS.readObject();
        crsInputS.close();
        this.beginPosition                = (int) idMapInOutStream.getChannel().position();
        this.dim                          = crs.getCoordinateSystem().getDimension();
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    protected void writeObject(final NamedEnvelope Object) throws IOException {
        final String neID = Object.getId();
        idMapInOutStream.getChannel().position(idMapCurrentPosition);
        byteBuffer.putLong(idMapCurrentPosition);
        idMapInOutStream.writeUTF(neID);
        idMapCurrentPosition = (int) idMapInOutStream.getChannel().position();
        byteBuffer.putInt(Object.getNbEnv());
        for (int d = 0; d < dim; d++) {
            byteBuffer.putDouble(Object.getLower(d));
            byteBuffer.putDouble(Object.getUpper(d));
        }
    }

    /**
     * {@inheritDoc }
     * Moreover store {@link NamedEnvelope#id} in other file, which is in the same 
     * parent directory as TreeMapper file.  
     */
    @Override
    protected NamedEnvelope readObject() throws IOException {
        final long idPos = byteBuffer.getLong();
        final FileChannel idChan = idMapInOutStream.getChannel();
        idChan.position(idPos);
        final String neID = idMapInOutStream.readUTF();
        assert (neID != null) : "stored Named Envelope should not have a null identifier. Problem during identifier reading."; 
        final int nbEnvelope = byteBuffer.getInt();
        final NamedEnvelope resultEnvelope = new NamedEnvelope(crs, neID, nbEnvelope);
        for (int d = 0; d < dim; d++) {
            final double lower = byteBuffer.getDouble();
            final double upper = byteBuffer.getDouble();
            resultEnvelope.setRange(d, lower, upper);
        }
        return resultEnvelope;
    }

    @Override
    public synchronized int getTreeIdentifier(NamedEnvelope object) throws IOException {
        try {
            return super.getTreeIdentifier(object);
        } catch (IllegalStateException ex) {
            return -1;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected boolean areEquals(final NamedEnvelope objectA, final NamedEnvelope objectB) {
         return objectA.getId().equals(objectB.getId());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(final NamedEnvelope object) throws IOException {
        return object;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void clear() throws IOException {
        super.clear(); 
        idMapInOutStream.getChannel().position(beginPosition);
        idMapCurrentPosition = beginPosition;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void flush() throws IOException {
        super.flush();
        final long chanPos = idMapInOutStream.getChannel().position();
        final long buffPos = byteBuffer.position();
        idMapInOutStream.getChannel().position(0);
        idMapInOutStream.writeLong(idMapCurrentPosition);
        idMapInOutStream.getChannel().position(chanPos);

        assert (chanPos + buffPos) == (idMapInOutStream.getChannel().position() + byteBuffer.position()): "LuceneTreeEltMapper.flush() expected : "+(chanPos + buffPos)+" found : "+(idMapInOutStream.getChannel().position() + byteBuffer.position());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        super.close(); 
        idMapInOutStream.getChannel().position(0);
        idMapInOutStream.writeLong(idMapCurrentPosition);
        idMapInOutStream.close();
    }

    @Override
    public Map<Integer, NamedEnvelope> getFullMap() throws IOException {
        throw new UnsupportedOperationException("Not supported yet int File implementation.");
    }
}


