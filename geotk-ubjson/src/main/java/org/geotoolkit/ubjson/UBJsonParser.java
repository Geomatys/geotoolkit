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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.apache.sis.io.stream.ChannelDataInput;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;

/**
 * UBJSON parser.
 *
 * @author Johann Sorel (Geomatys)
 */
final class UBJsonParser {

    private static final JsonNodeFactory NF = JsonNodeFactory.instance;

    public ObjectNode parse(InputStream stream) throws IllegalArgumentException, DataStoreException, IOException {
        final ChannelDataInput di = new StorageConnector(stream).getStorageAs(ChannelDataInput.class);
        final byte marker = di.readByte();
        if (marker == UBJson.OBJECT_START) {
            return parseObject(di);
        } else {
            throw new IOException("Object start marker not found");
        }
    }

    private static ObjectNode parseObject(ChannelDataInput in) throws IOException {
        final ObjectNode node = NF.objectNode();
        byte marker;
        while ( (marker = in.readByte()) != UBJson.OBJECT_END) {
            final String name = readStringNoMarker(marker, in);
            final JsonNode value = parseValue(in.readByte(), in);
            node.set(name, value);
        }
        return node;
    }

    private static ArrayNode parseArray(ChannelDataInput in) throws IOException {
        final ArrayNode node = NF.arrayNode();
        byte marker;
        while ( (marker = in.readByte()) != UBJson.ARRAY_END) {
            node.add(parseValue(marker, in));
        }
        return node;
    }

    private static JsonNode parseValue(byte marker, ChannelDataInput in) throws IOException {
        switch (marker) {
            case UBJson.NO_OP : //should not happen
            case UBJson.NULL : return NF.nullNode();
            case UBJson.TRUE : return NF.booleanNode(true);
            case UBJson.FALSE : return NF.booleanNode(false);
            case UBJson.UINT8 : return NF.numberNode(in.readUnsignedByte());
            case UBJson.INT8 : return NF.numberNode(in.readByte());
            case UBJson.INT16 : return NF.numberNode(in.readShort());
            case UBJson.INT32 : return NF.numberNode(in.readInt());
            case UBJson.INT64 : return NF.numberNode(in.readLong());
            case UBJson.FLOAT32 : return NF.numberNode(in.readFloat());
            case UBJson.FLOAT64 : return NF.numberNode(in.readDouble());
            case UBJson.CHAR : return NF.textNode(""+((char)in.readUnsignedByte()));
            case UBJson.HIGH_PRECISION_NUMBER : return NF.numberNode(new BigDecimal(readStringNoMarker(in.readByte(), in)));
            case UBJson.ARRAY_START : return parseArray(in);
            case UBJson.OBJECT_START : return parseObject(in);
            case UBJson.STRING : return NF.textNode(readStringNoMarker(in.readByte(), in));
            default: throw new IOException("Unexpected marker " + marker);
        }
    }

    private static String readStringNoMarker(int marker, ChannelDataInput in) throws IOException {
        final long size;
        switch (marker) {
            case UBJson.INT8 : size = in.readByte(); break;
            case UBJson.INT16 : size = in.readShort(); break;
            case UBJson.INT32 : size = in.readInt(); break;
            case UBJson.INT64 : size = in.readLong(); break;
            default: throw new IOException("Unexpected size marker");
        }
        final byte[] datas = in.readBytes(Math.toIntExact(size));
        return new String(datas, StandardCharsets.UTF_8);
    }

}
