/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.net.URL;
import java.util.logging.Level;

/**
 * @todo use a lock system bases on FileLock.
 * @deprecated
 */
@Deprecated
class ShpFilesLocker {

    final URL url;
    final Object holder;
    final boolean write;
    boolean upgraded;
    private Trace trace;

    public ShpFilesLocker( URL url, Object reader, boolean write) {
        this.url = url;
        this.holder = reader;
        this.write = write;

        if(write){
            ShapefileDataStoreFactory.LOGGER.log(Level.FINE, "Write lock: {0} by {1}", new Object[]{url, reader});
        }else{
            ShapefileDataStoreFactory.LOGGER.log(Level.FINE, "Read lock: {0} by {1}", new Object[]{url, reader});
        }
        setTraceException();
    }

    private void setTraceException() {
        String name = Thread.currentThread().getName();
        trace = new Trace("Locking ");
        //trace = new Trace("Locking " + url + " for " + ((write)? "write" : "read") + " by " + holder + " in thread " + name);
    }

    /**
     * Returns the Exception that is created when the Locker is created. This is simply a way of
     * determining who created the Locker.
     * 
     * @return the Exception that is created when the Locker is created
     */
    public Exception getTrace() {
        return trace;
    }

    /**
     * Verifies that the url and requestor are the same as the url and the reader or writer of this
     * class. assertions are used so this will do nothing if assertions are not enabled.
     */
    public void compare(URL url2, Object requestor) {
        URL url = this.url;
//        assert (url2 == url) : "Expected: " + url + " but got: " + url2;
//        assert (reader == null || requestor == reader) : "Expected the requestor and the reader to be the same object: "
//                + reader.id();
//        assert (writer == null || requestor == writer) : "Expected the requestor and the writer to be the same object: "
//                + writer.id();
    }

    @Override
    public String toString() {
        return ((write)? "write" : "read") + " on " + url + " by " + holder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (holder.hashCode());
        result = prime * result + ((url == null) ? 0 : url.toString().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShpFilesLocker other = (ShpFilesLocker) obj;
        if (this.url != other.url && (this.url == null || !this.url.toString().equals(other.url.toString()))) {
            return false;
        }
        if (this.holder != other.holder && (this.holder == null || !this.holder.equals(other.holder))) {
            return false;
        }
        if (this.write != other.write) {
            return false;
        }
        return true;
    }


    private static class Trace extends Exception {

        private static final long serialVersionUID = 1L;

        public Trace( String message ) {
            super(message);
        }
    }

}
