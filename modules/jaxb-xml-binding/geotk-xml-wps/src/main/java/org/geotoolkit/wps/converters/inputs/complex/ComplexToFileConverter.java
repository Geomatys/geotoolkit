package org.geotoolkit.wps.converters.inputs.complex;

import net.iharder.Base64;
import org.geotoolkit.nio.IOUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.xml.ComplexDataType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Save the content of a WPS complex input into local file.
 *
 * @author Alexis Manin (Geomatys)
 */
public class ComplexToFileConverter extends AbstractComplexInputConverter<File> {

    private static ComplexToFileConverter INSTANCE;

    private ComplexToFileConverter() {
    }

    public static synchronized ComplexToFileConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ComplexToFileConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<File> getTargetClass() {
        return File.class;
    }

    @Override
    public File convert(ComplexDataType source, Map<String, Object> params) throws UnconvertibleObjectException {

        if(source == null || source.getContent() == null) {
            throw new UnconvertibleObjectException("Mandatory parameter is missing.");
        }

        Path result = null;
        try {
        //Create a temp file
            final String fileName = UUID.randomUUID().toString();
            result = Files.createTempFile(fileName, ".tmp");

            final List<Object> data = source.getContent();
            if (data.size() < 1) {
                throw new UnconvertibleObjectException("There's no available data in this complex content.");
            }
            String rawData = (String) data.get(0);
            if (params != null && params.get(ENCODING).equals(WPSEncoding.BASE64.getValue())) {

                final byte[] byteData = Base64.decode(rawData);
                if (byteData != null && byteData.length > 0) {
                    try (final ByteArrayInputStream is = new ByteArrayInputStream(byteData)) {
                        IOUtilities.writeStream(is, result);
                    }
                }

            } else {
                if(rawData.startsWith("<![CDATA[") && rawData.endsWith("]]>")) {
                    rawData = rawData.substring(9, rawData.length()-3);
                }
                IOUtilities.writeString(rawData, result);
            }
        } catch (Exception ex) {
            throw new UnconvertibleObjectException(ex);
        }
        return result.toFile();
    }
}
