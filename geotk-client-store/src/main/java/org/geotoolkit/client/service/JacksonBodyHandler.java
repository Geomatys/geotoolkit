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
package org.geotoolkit.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @see https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpResponse.BodySubscribers.html#mapping(java.net.http.HttpResponse.BodySubscriber,java.util.function.Function)
 */
public final class JacksonBodyHandler<W> implements HttpResponse.BodyHandler<Supplier<W>> {

    private final ObjectMapper mapper;
    private final JavaType ref;

    public JacksonBodyHandler(ObjectMapper mapper, Class<W> valueClass) {
        this(mapper, (JavaType)((valueClass == null) ? null : mapper.constructType(valueClass)));
    }

    public JacksonBodyHandler(ObjectMapper mapper, TypeReference<W> ref) {
        this(mapper, (JavaType)((ref == null) ? null : mapper.constructType(ref)));
    }

    public JacksonBodyHandler(ObjectMapper mapper, JavaType ref) {
        this.mapper = mapper;
        this.ref = ref;
    }

    @Override
    public HttpResponse.BodySubscriber<Supplier<W>> apply(HttpResponse.ResponseInfo responseInfo) {
        final String contentEncoding = responseInfo.headers().firstValue("Content-Encoding").orElse("");
        return asJSON(contentEncoding);
    }

    private BodySubscriber<Supplier<W>> asJSON(String contentEncoding) {
        final BodySubscriber<InputStream> upstream = BodySubscribers.ofInputStream();

        return BodySubscribers.mapping(upstream,
            (InputStream is) -> () -> {
                try (InputStream in = is) {
                    InputStream di = in;
                    if ("gzip".equals(contentEncoding)) {
                        di = new GZIPInputStream(di);
                    }
                    final W body;
                    if (ref == null) {
                        // Drain the InputStream
                        while (in.read() != -1) {
                            // Ignore
                        }
                        body = null;
                    } else {
                        byte[] blob = di.readAllBytes();
                        final String str = new String(blob, StandardCharsets.UTF_8);
                        body = mapper.readValue(str, ref);
                    }
                    return body;

                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }

}
