/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.wps.xml.v200;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author guilhem
 */
public class WriterWrapper extends Writer {

    private Writer wrapped;

    public WriterWrapper(Writer wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        String s = new String(cbuf, off, len);
        if (s.contains("&lt;![CDATA[")) {
            s = s.replace("&lt;![CDATA[", "<![CDATA[");
        }
        if (s.contains("]]&gt;")) {
            s = s.replace("]]&gt;", "]]>");
        }
        cbuf = s.toCharArray();
        wrapped.write(cbuf, 0, cbuf.length);
    }

    @Override
    public void flush() throws IOException {
        wrapped.flush();
    }

    @Override
    public void close() throws IOException {
        wrapped.close();
    }

}
