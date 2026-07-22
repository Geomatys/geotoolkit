/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.client.joins;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.geotoolkit.ogcapi.request.joins.UploadFile;

/**
 * Builds a {@code multipart/form-data} (RFC 7578) request body. The Joins Core Input File Upload
 * requirements class (spec section 11) is the only part of {@code geotk-ogcapi} that needs this
 * encoding, so this helper stays local to the {@code joins} client package rather than being
 * added to the shared {@code geotk-client-store} HTTP plumbing.
 *
 * @author Johann Sorel (Geomatys)
 */
final class MultipartFormDataBody {

    private final String boundary = "----geotk-joins-" + UUID.randomUUID();
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /**
     * @return the {@code Content-Type} header value to use for this body.
     */
    String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    /**
     * Adds a simple form field part. Does nothing if {@code value} is {@code null}.
     */
    MultipartFormDataBody addField(String name, Object value) {
        if (value == null) {
            return this;
        }
        writePartHeader(name, null, null);
        write(String.valueOf(value));
        writeCrlf();
        return this;
    }

    /**
     * Adds a file part, with the {@code Content-Disposition: form-data; filename="..."} header
     * required by Requirement 33.C / 40.C,E of the Joins spec. Does nothing if {@code file} is
     * {@code null}.
     */
    MultipartFormDataBody addFile(String name, UploadFile file) {
        if (file == null) {
            return this;
        }
        writePartHeader(name, file.getFileName(), file.getContentType());
        write(file.getContent());
        writeCrlf();
        return this;
    }

    /**
     * @return the fully encoded body, including the closing boundary.
     */
    byte[] build() {
        write("--" + boundary + "--");
        writeCrlf();
        return buffer.toByteArray();
    }

    private void writePartHeader(String name, String fileName, String contentType) {
        write("--" + boundary);
        writeCrlf();
        final StringBuilder disposition = new StringBuilder("Content-Disposition: form-data; name=\"").append(name).append('"');
        if (fileName != null) {
            disposition.append("; filename=\"").append(fileName).append('"');
        }
        write(disposition.toString());
        writeCrlf();
        if (contentType != null) {
            write("Content-Type: " + contentType);
            writeCrlf();
        }
        writeCrlf();
    }

    private void write(String text) {
        write(text.getBytes(StandardCharsets.UTF_8));
    }

    private void write(byte[] bytes) {
        buffer.write(bytes, 0, bytes.length);
    }

    private void writeCrlf() {
        write("\r\n");
    }
}
