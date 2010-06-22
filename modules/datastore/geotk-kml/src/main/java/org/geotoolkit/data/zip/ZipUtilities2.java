package org.geotoolkit.data.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Samuel Andrés
 */
public class ZipUtilities2 {

    private static final String ZIP_EXTENSION = ".zip";
    private static final int DEFAULT_LEVEL_COMPRESSION =
            Deflater.BEST_COMPRESSION;

    // Remplace l'extension si le fichier cible ne fini pas par '.zip'
    private static File getZipTypeFile(final File source, final File target)
            throws IOException {
        if (target.getName().toLowerCase().endsWith(ZIP_EXTENSION)) {
            return target;
        }
        final String tName = target.isDirectory()
                ? source.getName()
                : target.getName();
        final int index = tName.lastIndexOf('.');
        return new File(
                new StringBuilder(target.isDirectory()
                ? target.getCanonicalPath()
                : target.getParentFile().getCanonicalPath()).append(File.separatorChar).append(index < 0 ? tName : tName.substring(0, index)).append(ZIP_EXTENSION).toString());
    }

    // Compresse un fichier
    private final static void compressFile(final ZipOutputStream out, final String parentFolder, final File file)
            throws IOException {
        final String zipName = new StringBuilder(parentFolder).append(file.getName()).append(file.isDirectory() ? '/' : "").toString();

        // Définition des attributs du fichier
        final ZipEntry entry = new ZipEntry(zipName);
        entry.setSize(file.length());
        entry.setTime(file.lastModified());
        out.putNextEntry(entry);

        // Traitement récursif s'il s'agit d'un répertoire
        if (file.isDirectory()) {
            for (final File f : file.listFiles()) {
                compressFile(out, zipName.toString(), f);
            }
            return;
        }

        // Ecriture du fichier dans le zip
        final InputStream in = new BufferedInputStream(
                new FileInputStream(file));
        try {
            final byte[] buf = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = in.read(buf))) {
                out.write(buf, 0, bytesRead);
            }
        } finally {
            in.close();
        }
    }

    // Compresse un fichier à l'adresse pointée par le fichier cible.
    // Remplace le fichier cible s'il existe déjà.
    public static void compress(final File file, final File target,
            final int compressionLevel)
            throws IOException {
        final File source = file.getCanonicalFile();

        // Création du fichier zip
        final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                getZipTypeFile(source, target.getCanonicalFile())));
        out.setMethod(ZipOutputStream.DEFLATED);
        out.setLevel(compressionLevel);

        // Ajout du(es) fichier(s) au zip
        compressFile(out, "", source);
        out.close();
    }

    public static void compress(final File file, final int compressionLevel)
            throws IOException {
        compress(file, file, compressionLevel);
    }

    public static void compress(final File file, final File target)
            throws IOException {
        compress(file, target, DEFAULT_LEVEL_COMPRESSION);
    }

    public static void compress(final File file) throws IOException {
        compress(file, file, DEFAULT_LEVEL_COMPRESSION);
    }

    public static void compress(final String fileName, final String targetName,
            final int compressionLevel) throws IOException {
        compress(new File(fileName), new File(targetName), compressionLevel);
    }

    public static void compress(final String fileName,
            final int compressionLevel) throws IOException {
        compress(new File(fileName), new File(fileName), compressionLevel);
    }

    public static void compress(final String fileName, final String targetName)
            throws IOException {
        compress(new File(fileName), new File(targetName),
                DEFAULT_LEVEL_COMPRESSION);
    }

    public static void compress(final String fileName) throws IOException {
        compress(new File(fileName), new File(fileName),
                DEFAULT_LEVEL_COMPRESSION);
    }

    // Décompresse un fichier zip à l'adresse indiquée par le dossier
    public static void decompress(final File file, final File folder,
            final boolean deleteZipAfter)
            throws IOException {
        final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
                new FileInputStream(file.getCanonicalFile())));
        ZipEntry ze;
        try {
            // Parcourt tous les fichiers
            while (null != (ze = zis.getNextEntry())) {
                final File f = new File(folder.getCanonicalPath(), ze.getName());
                if (f.exists()) {
                    f.delete();
                }

                // Création des dossiers
                if (ze.isDirectory()) {
                    f.mkdirs();
                    continue;
                }
                f.getParentFile().mkdirs();
                final OutputStream fos = new BufferedOutputStream(
                        new FileOutputStream(f));

                // Ecriture des fichiers
                try {
                    try {
                        final byte[] buf = new byte[8192];
                        int bytesRead;
                        while (-1 != (bytesRead = zis.read(buf))) {
                            fos.write(buf, 0, bytesRead);
                        }
                    } finally {
                        fos.close();
                    }
                } catch (final IOException ioe) {
                    f.delete();
                    throw ioe;
                }
            }
        } finally {
            zis.close();
        }
        if (deleteZipAfter) {
            file.delete();
        }
    }

    public static void decompress(final String fileName, final String folderName,
            final boolean deleteZipAfter)
            throws IOException {
        decompress(new File(fileName), new File(folderName), deleteZipAfter);
    }

    public static void decompress(final String fileName, final String folderName)
            throws IOException {
        decompress(new File(fileName), new File(folderName), false);
    }

    public static void decompress(final File file, final boolean deleteZipAfter)
            throws IOException {
        decompress(file, file.getCanonicalFile().getParentFile(), deleteZipAfter);
    }

    public static void decompress(final String fileName,
            final boolean deleteZipAfter)
            throws IOException {
        decompress(new File(fileName), deleteZipAfter);
    }

    public static void decompress(final File file)
            throws IOException {
        decompress(file, file.getCanonicalFile().getParentFile(), false);
    }

    public static void decompress(final String fileName)
            throws IOException {
        decompress(new File(fileName));
    }

//    public static void main(String[] args)
//            throws IOException {
//        ZipUtilities2.compress("/home/samuel/Bureau/resultKmz", "/home/samuel/Bureau/test.zip", Deflater.BEST_SPEED);
//        ZipUtilities2.decompress("/home/samuel/Bureau/test.zip", "/home/samuel/Bureau/dezip", false);
//    }
}
