package org.geotoolkit.wps.converters.outputs.complex;

import net.iharder.Base64;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.io.WPSEncoding;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * A converter to transform a File into ComplexOutput data for wps ExecuteResponse query.
 *
 * @author Alexis Manin (Geomatys)
 *         Date :05/02/13
 *         Time: 09:28
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
    public Class<? super File> getSourceClass() {
        return File.class;
    }

    /**
     * Convert a file into ComplexDataType object, according to the specifications given in params parameter.
     *
     * @param source The file to convert.
     * @param params The parameters used for conversion (Mime-Type/encoding). If null, mime is set to application/octet-stream, and encoding to base64
     * @return
     * @throws NonconvertibleObjectException
     */
    @Override
    public ComplexDataType convert(File source, Map<String, Object> params) throws NonconvertibleObjectException {

        if (source == null) {
            throw new NonconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof File)) {
            throw new NonconvertibleObjectException("The requested output data is not an instance of File.");
        }
        if (params == null) {
            throw new NonconvertibleObjectException("Mandatory parameters are missing.");
        }

        final ComplexDataType complex = new ComplexDataType();
        String mime = (String) params.get(MIME);
        String encoding = (String) params.get(ENCODING);

        if (mime == null) {
            mime = WPSMimeType.APP_OCTET.val();
            encoding = WPSEncoding.BASE64.getValue();
        }

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
                        throw new NonconvertibleObjectException("Mandatory parameters are missing.");
                    }
                    if (!schemaLocation.contains(tmpDir)) {
                        String schemaName = source.getName().replace("\\.[a-z]ml", "").concat(".xsd");
                        File schemaDest = new File(tmpDir, schemaName);
                        try {
                            FileUtilities.copy(source, schemaDest);
                            schemaLocation = schemaDest.getAbsolutePath();
                        } catch (IOException e) {
                            throw new NonconvertibleObjectException("Unexpected error on schema copy.", e);
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
            if (!encoding.equals(WPSEncoding.BASE64.getValue())) {
                throw new NonconvertibleObjectException("Encoding should be in Base64 for complex request.");
            }

            FileInputStream stream = null;
            try {
                stream = new FileInputStream(source);
                byte[] barray = new byte[(int) source.length()];
                stream.read(barray);
                complex.getContent().add(Base64.encodeBytes(barray));
            } catch (Exception ex) {
                throw new NonconvertibleObjectException(ex.getMessage(), ex);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ioEx) {
                        LOGGER.log(Level.WARNING, "Unable to close the stream for result file.", ioEx);
                    }
                }
            }
        }

        return complex;
    }
}
