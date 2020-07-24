package it4i.cz;

import ij.IJ;
import org.jetbrains.annotations.NotNull;
import org.scijava.log.LogService;

class DefaultListeners {
    public static void handleStatusReport(final @NotNull LogService logger,
                                          final @NotNull String message) {
        logger.info("QCMP status: " + message);
    }

    public static void handleProgressReport(final @NotNull LogService logger,
                                            final @NotNull String message,
                                            final int index,
                                            final int finalIndex) {
        logger.info("QCMP progress: " + message);
        IJ.showProgress(index, finalIndex);
    }
}
