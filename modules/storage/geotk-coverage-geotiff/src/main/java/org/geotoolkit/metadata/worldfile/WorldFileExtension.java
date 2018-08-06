package org.geotoolkit.metadata.worldfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import org.geotoolkit.image.io.metadata.ReferencingBuilder;
import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.image.io.plugin.TiffImageReader;
import org.geotoolkit.internal.image.io.GridDomainAccessor;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.metadata.geotiff.GeoTiffExtension;
import org.geotoolkit.nio.IOUtilities;

import org.geotoolkit.io.wkt.PrjFiles;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class WorldFileExtension extends GeoTiffExtension {

    @Override
    public boolean isPresent(Object input) {
            try {
                final WorldFiles wf = new WorldFiles(input);
                return wf.prj != null || wf.tfw != null;
            } catch (IOException ex) {
                TiffImageReader.LOGGER.log(Level.WARNING, "Cannot build World file paths", ex);
            }

        return false;
    }

    @Override
    public SpatialMetadata fillSpatialMetaData(TiffImageReader reader, SpatialMetadata metadata) throws IOException {
        final WorldFiles wf = new WorldFiles(reader.getInput());
        if (wf.prj != null) {
            new ReferencingBuilder(metadata)
                    .setCoordinateReferenceSystem(PrjFiles.read(wf.prj));
        }

        if (wf.tfw != null) {
            new GridDomainAccessor(metadata)
                    .setGridToCRS(SupportFiles.parseTFW(wf.tfw));
        }

        return metadata;
    }

    private static class WorldFiles {

        final Path tfw;
        final Path prj;

        WorldFiles(final Object input) throws IOException {
            final Object tryToPath = IOUtilities.tryToPath(input);
            if (tryToPath instanceof Path) {
                final String tfwSuffix = SupportFiles.toSuffixTFW(tryToPath);
                Object tfwPath = SupportFiles.changeExtension(tryToPath, tfwSuffix);
                Object prjPath = SupportFiles.changeExtension(tryToPath, "prj");
                tfw = tfwPath instanceof Path && Files.isReadable((Path) tfwPath)?
                        (Path) tfwPath : null;
                prj = prjPath instanceof Path && Files.isReadable((Path) prjPath)?
                        (Path) prjPath : null;
            } else {
                prj = tfw = null;
            }
        }
    }
}
