package org.geotoolkit.wps.converters.outputs.complex;

import net.iharder.Base64;
import org.geotoolkit.nio.IOUtilities;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.ComplexDataType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.xml.WPSXmlFactory;

/**
 * A converter to transform a File into ComplexOutput data for wps ExecuteResponse query.
 *
 * @author Alexis Manin (Geomatys)
 */
public class FileToComplexConverter extends AbstractComplexOutputConverter<File> {

    private static FileToComplexConverter INSTANCE;

    private FileToComplexConverter() {
    }

    public static synchronized FileToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<File> getSourceClass() {
        return File.class;
    }

    /**
     * Convert a file into ComplexDataType object, according to the specifications given in params parameter.
     *
     * @param source The file to convert.
     * @param params The parameters used for conversion (Mime-Type/encoding). If null, mime is set to application/octet-stream, and encoding to base64
     * @return
     * @throws UnconvertibleObjectException
     */
    @Override
    public ComplexDataType convert(File source, Map<String, Object> params) throws UnconvertibleObjectException {

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (params == null) {
            throw new UnconvertibleObjectException("Mandatory parameters are missing.");
        }

        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }

        String mime = (String) params.get(MIME);
        String encoding = (String) params.get(ENCODING);

        if (mime == null) {
            mime = WPSMimeType.APP_OCTET.val();
            encoding = WPSEncoding.BASE64.getValue();
        }
        final ComplexDataType complex = WPSXmlFactory.buildComplexDataType(wpsVersion, mime,encoding, null);

        //Plain text
        if (mime.startsWith("text")) {
            //XML is special case, we try to find an associate schema.
            if (mime.contains("xml") || mime.contains("gml")) {
                String schemaLocation = source.getAbsolutePath();
                schemaLocation.replace("\\.[a-z]ml", "").concat(".xsd");
                File ogrSchema = new File(schemaLocation);
                // If we find a schema, we ensure it's location is public before giving it.
                if (ogrSchema.exists()) {
                    String tmpDir = (String) params.get(TMP_DIR_PATH);
                    String tmpURL = (String) params.get(TMP_DIR_URL);
                    if (tmpDir == null || tmpURL == null) {
                        throw new UnconvertibleObjectException("Mandatory parameters are missing.");
                    }
                    if (!schemaLocation.contains(tmpDir)) {
                        String schemaName = source.getName().replace("\\.[a-z]ml", "").concat(".xsd");
                        Path schemaDest = Paths.get(tmpDir, schemaName);
                        try {
                            IOUtilities.copy(source.toPath(), schemaDest);
                            schemaLocation = schemaDest.toAbsolutePath().toString();
                        } catch (IOException e) {
                            throw new UnconvertibleObjectException("Unexpected error on schema copy.", e);
                        }
                    }
                    complex.setSchema(schemaLocation.replace(tmpDir, tmpURL));
                }
            }
            // CData needed because files could contain problematic characters.
            complex.getContent().add("<![CDATA[");
            complex.getContent().add(source);
            complex.getContent().add("]]>");
        } else {
            //If no text format, We'll put it as a base64 object.
            if (encoding == null || !encoding.equals(WPSEncoding.BASE64.getValue())) {
                throw new UnconvertibleObjectException("Encoding should be in Base64 for complex request.");
            }

            try (FileInputStream stream =new FileInputStream(source)) {
                byte[] barray = new byte[(int) source.length()];
                stream.read(barray);
                complex.getContent().add(Base64.encodeBytes(barray));
            } catch (Exception ex) {
                throw new UnconvertibleObjectException(ex.getMessage(), ex);
            }
        }

        return complex;
    }
}
