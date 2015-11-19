package org.geotoolkit.nio;

import org.geotoolkit.lang.Static;
import org.geotoolkit.nio.CopyFileVisitor;
import org.geotoolkit.nio.IOUtilities;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Utility methods related to I/O Zip operations.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ZipUtilities extends Static {

    /**
     * The default buffer size for copy operations.
     */
    private static final int BUFFER_SIZE = 8192;

    private ZipUtilities() {
    }

    /**
     * Returns a zip file system
     *
     * @param zipPath to construct the file system from
     * @param create true if the zip file should be created
     * @return a zip file system
     * @throws IOException
     */
    private static FileSystem createZipFileSystem(Path zipPath, boolean create)
            throws IOException {
        // convert the filename to a URI
        final URI uri = URI.create("jar:file:" + zipPath.toAbsolutePath().toString());
        final Map<String, String> env = new HashMap<>();
        if (create) {
            env.put("create", "true");
        }
        return FileSystems.newFileSystem(uri, env);
    }

    /**
     * Zip resources list into zip file using Java NIO API.
     *
     * @param zipPath
     * @param resouces
     * @throws IOException
     */
    public static void zipNIO(Path zipPath, final Path... resouces) throws IOException {

        Files.deleteIfExists(zipPath);
        try (FileSystem zipFs = createZipFileSystem(zipPath, true)) {
            final Path zipRoot = zipFs.getPath("/");

            for (Path resouce : resouces) {
                final Path nResouce = resouce.normalize();

                if (Files.isRegularFile(nResouce, LinkOption.NOFOLLOW_LINKS)) {
                    final Path dest = zipRoot.resolve(nResouce.getFileName().toString());
                    Files.copy(nResouce, dest, StandardCopyOption.REPLACE_EXISTING);

                } else {
                    Files.walkFileTree(nResouce, new CopyFileVisitor(zipRoot, StandardCopyOption.REPLACE_EXISTING));
                }
            }
        }
    }

    /**
     * Unzip zipped file into target path using Java NIO API.
     *
     * @param zipPath
     * @param target
     * @param withRootDirectory create folder into target path named after zip filename
     * @throws IOException
     */
    public static void unzipNIO(Path zipPath, final Path target, boolean withRootDirectory) throws IOException {

        if(Files.notExists(target)) {
            Files.createDirectories(target);
        }

        final Path deflateDirectory;
        if (withRootDirectory) {
            final String folderName = IOUtilities.filenameWithoutExtension(zipPath);
            deflateDirectory = Files.createDirectories(target.resolve(folderName));
        } else {
            deflateDirectory = target;
        }

        try (FileSystem zipFS = createZipFileSystem(zipPath, false)) {
            final Path root = zipFS.getPath("/");
            Files.walkFileTree(root, new CopyFileVisitor(deflateDirectory, StandardCopyOption.REPLACE_EXISTING));
        }
    }



    /**
     * <p>This method allows to put resources into zip archive specified resource,
     * without compression</p>
     *
     * @param zip The resource which files will be archived into. This argument must be
     * instance of File, String (representing a path), or OutputStream. Cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @param resources The files to compress. Tese objects can be File instances or
     * String representing files paths, URL, URI or InputStream. Cannot be null.
     * @throws IOException
     */
    public static void zip(final Path zip, final Checksum checksum, final Path... resources) throws IOException {
        zip(zip, ZipOutputStream.STORED, 0, checksum, resources);
    }

    /**
     * <p>This method allows to put resources into zip archive specified resource.</p>
     *
     * @param zip The resource which files will be archived into. This argument must be
     * instance of File, String (representing a path), or OutputStream. Cannot be null.
     * @param method The compression method is a static int constant from ZipOutputSteam with
     * two theorical possible values :
     * <ul>
     * <li>{@link ZipOutputStream#DEFLATED} to compress archive.</li>
     * <li>{@link ZipOutputStream#STORED} to let the archive uncompressed (unsupported).</li>
     * </ul>
     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @param resources The files to compress. Tese objects can be File instances or
     * String representing files paths, URL, URI or InputStream. Cannot be null.
     * @throws IOException
     */
    public static void zip(final Path zip, final int method, final int level, final Checksum checksum, final Path... resources)
            throws IOException {

        try (OutputStream outStream = Files.newOutputStream(zip)) {
            if (checksum != null) {
                try (CheckedOutputStream cos = new CheckedOutputStream(outStream, checksum)) {
                    try (BufferedOutputStream buf = new BufferedOutputStream(cos)) {
                        zipCore(method, level, buf, resources);
                    }
                }
            } else {
                try (BufferedOutputStream buf = new BufferedOutputStream(outStream)) {
                    zipCore(method, level, buf, resources);
                }
            }
        }
    }

    /**
     * Intermediate operation during ip creation
     * @param method
     * @param level
     * @param buf
     * @param resources
     * @throws IOException
     */
    private static void zipCore(int method, int level, BufferedOutputStream buf, Path[] resources) throws IOException {
        try (final ZipOutputStream zout = new ZipOutputStream(buf)) {
            zout.setMethod(method);
            zout.setLevel(level);
            zipCore(zout, method, level, "", Arrays.asList(resources).iterator());
        }
    }

    /**
     * <p>This method creates an OutputStream with ZIP archive from the list of resources parameter.</p>
     *
     * @param zout OutputStrem on ZIP archive that will contain archives of resource files.
     * @param method The compression method is a static int constant from ZipOutputSteeam with
     * two theorical possible values :
     * <ul>
     * <li>{@link ZipOutputStream#DEFLATED} to compress archive.</li>
     * <li>{@link ZipOutputStream#STORED} to let the archive uncompressed (unsupported).</li>
     * </ul>
     * @param level The compression level is an integer between 0 (not compressed) to 9 (best compression).
     * @param resources Iterator other Path to compress
     * @throws IOException
     */
    private static void zipCore(final ZipOutputStream zout, final int method, final int level, final String entryPath, final Iterator<Path> resources)
            throws IOException {

        final CRC32 crc = new CRC32();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        boolean stored = false;

        if (ZipOutputStream.STORED == method) {
            stored = true;
        } else if (ZipOutputStream.DEFLATED != method) {
            throw new IllegalArgumentException("This compression method is not supported.");
        }

        if (Double.isNaN(level) || Double.isInfinite(level) || level > 9 || level < 0) {
            throw new IllegalArgumentException("Illegal compression level.");
        }

        while (resources.hasNext()) {
            final Path resource = resources.next();

            final String fileName = resource.getFileName().toString();
            final ZipEntry entry = new ZipEntry(entryPath + fileName);
            if (stored) {
                final long size = Files.size(resource);
                entry.setCompressedSize(size);
                entry.setSize(size);
                entry.setCrc(crc.getValue());
            }

            if (Files.isDirectory(resource)) {
                try( DirectoryStream<Path> children = Files.newDirectoryStream(resource)) {
                    final String nextEntryPath = entryPath + fileName + '/';
                    zipCore(zout, method, level, nextEntryPath, children.iterator());
                    continue;
                }
            }
            zout.putNextEntry(entry);

            if (stored) {
                try (SeekableByteChannel sbc = Files.newByteChannel(resource, StandardOpenOption.READ)) {
                    crc.reset();
                    int bytesRead;
                    while ((bytesRead = sbc.read(byteBuffer)) != -1) {
                        crc.update(byteBuffer.array(), 0, bytesRead);
                    }
                }
            }

            try (InputStream is = Files.newInputStream(resource)){
                IOUtilities.copy(is, zout);
            } finally {
                zout.closeEntry();
            }
        }
    }

    /**
     * <p>This method extracts a ZIP archive into the directory which contents it.</p>
     *
     * @param zip The archive parameter as File, URL, URI, InputStream or String path.
     * This argument cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @throws IOException
     */
    public static List<Path> unzip(final Path zip, final Checksum checksum) throws IOException {
        return unzip(zip, zip.getParent(), checksum);
    }

    /**
     * <p>This method extract a ZIP archive into the specified location.</p>
     *
     * <p>ZIP resource parameter
     *
     * @param zip The archive parameter can be instance of InputStream, File,
     * URL, URI or a String path. This argument cannot be null.
     * @param resource The resource where archive content will be extracted.
     * This resource location can be specified as instance of File or a String path.
     * This argument cannot be null.
     * @param checksum Checksum object (instance of Alder32 or CRC32).
     * @throws IOException
     */
    public static List<Path> unzip(final Path zip, final Path resource, final Checksum checksum) throws IOException {
        try (InputStream inStream = Files.newInputStream(zip)) {
            if (checksum != null) {
                try (CheckedInputStream cis = new CheckedInputStream(inStream, checksum)) {
                    try (BufferedInputStream buf = new BufferedInputStream(cis)) {
                        return unzipCore(buf, resource);
                    }
                }
            } else {
                try (BufferedInputStream buf = new BufferedInputStream(inStream)) {
                    return unzipCore(buf, resource);
                }
            }
        }
    }

    /**
     * <p>This method extract a ZIP archive from an InputStream, into directory
     * whose path is indicated by resource object.</p>
     *
     * @param zip InputStream on ZIP resource that contains resources to extract.
     * @param resource The resource where files will be extracted.
     * Must be instance of File or a String path. This argument cannot be null.
     * @throws IOException
     */
    private static List<Path> unzipCore(final InputStream zip, final Path resource) throws IOException {

        final List<Path> unzipped = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(zip)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                final Path file = resource.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(file);
                    continue;
                }
                Files.createDirectories(file.getParent());
                unzipped.add(file);


                try (OutputStream fos = Files.newOutputStream(file)) {
                    IOUtilities.copy(zis, fos);
                }
            }
        }

        return unzipped;
    }
}
