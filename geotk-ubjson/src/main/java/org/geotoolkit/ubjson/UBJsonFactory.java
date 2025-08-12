/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ubjson;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.sis.storage.DataStoreException;

/**
 * Jackson factory implementation for UBJSON.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class UBJsonFactory extends JsonFactory {

    public final static String FORMAT_NAME_UBJSON = "ubjson";

    public UBJsonFactory() {
        super();
    }

    public UBJsonFactory(ObjectCodec codec) {
        super(codec);
    }

    protected UBJsonFactory(UBJsonFactory src, ObjectCodec oc) {
        super(src, oc);
    }

    protected UBJsonFactory(UBJsonFactoryBuilder b) {
        super(b, false);
    }

    @Override
    public UBJsonFactoryBuilder rebuild() {
        return new UBJsonFactoryBuilder(this);
    }

    /**
     * Main factory method to use for constructing {@link UBJsonFactory} instances with different configuration.
     */
    public static UBJsonFactoryBuilder builder() {
        return new UBJsonFactoryBuilder();
    }

    @Override
    public UBJsonFactory copy() {
        _checkInvalidCopy(UBJsonFactory.class);
        return new UBJsonFactory(this, null);
    }

    // **********************************************************
    // Versioned
    // **********************************************************

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    // *********************************************************
    // Format detection functionality
    // *********************************************************

    @Override
    public String getFormatName() {
        return FORMAT_NAME_UBJSON;
    }

    /**
     * Sub-classes need to override this method
     */
    @Override
    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        return MatchStrength.INCONCLUSIVE;
    }

    // *********************************************************
    // Capability introspection
    // *********************************************************

    @Override
    public boolean requiresPropertyOrdering() {
        return false;
    }

    @Override
    public boolean canHandleBinaryNatively() {
        return false;
    }

    @Override
    public boolean canUseCharArrays() {
        return false;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return false;
    }

    // *********************************************************************
    // Overridden internal factory methods, parser
    // *********************************************************************

    @Override
    public JsonParser _createParser(InputStream in, IOContext ctxt) throws IOException {
        return new TreeTraversingParser(parse(ctxt, in));
    }

    @Override
    public JsonParser _createParser(Reader r, IOContext ctxt) throws IOException {
        final boolean autoClose = ctxt.isResourceManaged() || isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE);
        try {
            final char[] arr = new char[8 * 1024];
            final StringBuilder buffer = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = r.read(arr, 0, arr.length)) != -1) {
                buffer.append(arr, 0, numCharsRead);
            }
            if (autoClose) r.close();
            byte[] bytes = buffer.toString().getBytes(StandardCharsets.UTF_8);
            return _createParser(bytes, 0, bytes.length, ctxt);
        } finally {
            ctxt.close();
        }
    }

    @Override
    public JsonParser _createParser(byte[] data, int offset, int len, IOContext ctxt) throws IOException {
        return _createParser(new ByteArrayInputStream(data, offset, len), ctxt);
    }

    @Override
    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt, boolean recyclable) throws IOException {
        return _createParser(new CharArrayReader(data, offset, len), ctxt);
    }

    // *********************************************************************
    //  Overridden internal factory methods, generator
    // *********************************************************************

    @Override
    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        try {
            return new UBJsonGenerator(ctxt, _generatorFeatures, _objectCodec, new WriterOutputStream(out));
        } catch (IllegalArgumentException | DataStoreException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        try {
            return new UBJsonGenerator(ctxt, _generatorFeatures, _objectCodec, out);
        } catch (IllegalArgumentException | DataStoreException ex) {
            throw new IOException(ex);
        }
    }

    // *********************************************************************
    // Low-level methods for reading/writing UBJSON
    // *********************************************************************

    private ObjectNode parse(IOContext ctxt, InputStream in) throws IOException {
        final UBJsonParser parser = new UBJsonParser();
        try {
            return parser.parse(in);
        } catch (IllegalArgumentException | DataStoreException ex) {
            throw new IOException(ex);
        }
    }

}
