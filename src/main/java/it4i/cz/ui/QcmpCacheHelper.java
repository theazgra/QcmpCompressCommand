package it4i.cz.ui;

import ij.IJ;

import java.io.File;

abstract class QcmpCacheHelper {
    private static String QCMP_CACHE_DIRECTORY = null;

    public static String getQcmpCacheDirectory() {
        if (QCMP_CACHE_DIRECTORY == null) {
            synchronized (QcmpCacheHelper.class) {
                if (QCMP_CACHE_DIRECTORY == null) {
                    final String pluginsDir = IJ.getDirectory("plugins");
                    QCMP_CACHE_DIRECTORY = new File(pluginsDir, "qcmp_cache").getAbsolutePath();
                }
            }
        }
        return QCMP_CACHE_DIRECTORY;
    }
}
