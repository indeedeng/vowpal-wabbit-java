package com.indeed.vw.wrapper.jni;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


/**
 * Simple library class for working with JNI (Java Native Interface),
 * see <a href="http://adamheinrich.com/blog/2012/how-to-load-native-jni-library-from-jar/">here.
 *
 * @author Adam Heirnich &lt;adam@adamh.cz&gt;, <a href="http://www.adamh.cz">http://www.adamh.cz
 * @author Jon Morra
 * @author artem@indeed.com
 */
public class NativeUtils {
    private static final Logger logger = Logger.getLogger(NativeUtils.class);

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }

    /**
     *
     * @return
     * @throws IOException
     */
    private static String getOsFamily() throws IOException {
        final int jvmBitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        final String osName = System.getProperty("os.name");
        final String osFamily;
        if (osName.toLowerCase().contains("mac")) {
            osFamily = "Darwin";
        } else if (osName.toLowerCase().contains("linux")) {
            osFamily = "Linux";
        } else {
            throw new IllegalStateException("Unsupported operating system " + osName + " " + jvmBitness);
        }
        switch (jvmBitness) {
            case 64:
                return osFamily;
            case 32:
                return osFamily + ".32";
            default:
                throw new IllegalStateException("Unsupported operating system " + osName + " " + jvmBitness);
        }
    }

    /**
     * Loads a library from current JAR archive by looking up platform dependent name.
     *
     * @param path   The filename inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @param suffix The suffix to be appended to the name
     * @throws UnsupportedEncodingException If an error occurs while determining the Linux specific
     *                                      information.
     * @throws IOException                  If temporary file creation or read/write operation fails
     */
    public static void loadOSDependentLibrary(final String path, final String suffix) throws IOException {
        final String osFamily = getOsFamily();
        String osDependentLib = null;
        final String currentOsDependentLib = path + "." + osFamily + suffix;
        if (NativeUtils.class.getResource(currentOsDependentLib) != null) {
            osDependentLib = currentOsDependentLib;
        }
        if (osDependentLib != null) {
            logger.info("Loading " + osDependentLib);
            loadLibraryFromJar(osDependentLib);
        } else {
            logger.info("Loading " + path + suffix);
            loadLibraryFromJar(path + suffix);
        }
    }

    /**
     * Loads library from current JAR archive
     * <p/>
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     *
     * @param path The filename inside JAR as absolute path (beginning with '/'), e.g. /package/File.ext
     * @throws IOException              If temporary file creation or read/write operation fails
     * @throws IllegalArgumentException If source file (param path) does not exist
     * @throws IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of {@link File#createTempFile(java.lang.String, java.lang.String)}).
     */
    public static void loadLibraryFromJar(final String path) throws IOException {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        // Obtain filename from path
        String[] parts = path.split("/");
        final String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

        // Split filename to prefix and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "." + parts[parts.length - 1] : null; // Thanks, davs! :-)
        }

        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        // Prepare temporary file
        final File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();

        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }

        // Prepare buffer for data copying
        final byte[] buffer = new byte[1024];
        int readBytes;

        // Open and check input stream
        final InputStream is = NativeUtils.class.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }

        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }

        // Finally, load the library
        System.load(temp.getAbsolutePath());

        final String libraryPrefix = prefix;
        final String lockSuffix = ".lock";

        // create lock file
        final File lock = new File(temp.getAbsolutePath() + lockSuffix);
        lock.createNewFile();
        lock.deleteOnExit();

        // file filter for library file (without .lock files)
        final FileFilter tmpDirFilter = new FileFilter() {
            public boolean accept(final File pathname) {
                return pathname.getName().startsWith(libraryPrefix) && !pathname.getName().endsWith(lockSuffix);
            }
        };

        // get all library files from temp folder
        final String tmpDirName = System.getProperty("java.io.tmpdir");
        final File tmpDir = new File(tmpDirName);
        final File[] tmpFiles = tmpDir.listFiles(tmpDirFilter);

        // delete all files which don't have n accompanying lock file
        for (final File tmpFile : tmpFiles) {
            // Create a file to represent the lock and test.
            final File lockFile = new File(tmpFile.getAbsolutePath() + lockSuffix);
            if (!lockFile.exists()) {
                tmpFile.delete();
            }
        }
    }
}
