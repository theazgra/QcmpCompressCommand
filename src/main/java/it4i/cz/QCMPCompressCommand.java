/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package it4i.cz;

import cz.it4i.qcmp.compression.CompressionOptions;
import cz.it4i.qcmp.compression.ImageCompressor;
import cz.it4i.qcmp.data.V3i;
import cz.it4i.qcmp.fileformat.FileExtensions;
import cz.it4i.qcmp.io.BufferInputData;
import cz.it4i.qcmp.io.InputData;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import it4i.cz.ui.CompressionDialog;
import it4i.cz.ui.ImageInfo;
import net.imagej.ImageJ;
import org.scijava.Priority;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;


@Plugin(type = Command.class, menuPath = "Compression>QCMP>Compress", priority = Priority.HIGH_PRIORITY)
public class QCMPCompressCommand implements Command {

    /**
     * Injected parameters.
     */
    @Parameter
    private LogService logger;


    @Override
    public void run() {
        boolean runDecompressOnTmpFile = false;
        String tmpFile = null;

        final ImagePlus currentImage = WindowManager.getCurrentImage();
        //Returns the dimensions of this image (width, height, nChannels, nSlices, nFrames) as a 5 element int array.

        if (currentImage == null) {
            IJ.showMessage("No image is opened.");
            return;
        }
        if (currentImage.getType() != ImagePlus.GRAY16) {
            IJ.showMessage("Only 16 bit images are currently supported.");
            return;
        }

        final int stackSize = currentImage.getImageStackSize();
        final ImageStack imageStack = currentImage.getImageStack();



        assert (currentImage.getNSlices() == stackSize);
        final V3i datasetDims = new V3i(currentImage.getWidth(),
                                        currentImage.getHeight(),
                                        currentImage.getImageStackSize());

        final ImageInfo imageInfo = new ImageInfo(currentImage.getTitle(),
                                                  datasetDims.toString(),
                                                  Integer.toString(currentImage.getBitDepth()));
        final CompressionDialog dialog = new CompressionDialog("QCMP compression", imageInfo);
        if (dialog.exec()) {

            final CompressionOptions options = dialog.getChosenOptions();
            final Object[] pixelBuffers = new Object[stackSize];
            for (int planeIndex = 0; planeIndex < stackSize; planeIndex++) {
                final Object planePixelBuffer = imageStack.getPixels(planeIndex + 1);
                assert (planePixelBuffer != null);
                pixelBuffers[planeIndex] = planePixelBuffer;
            }

            if (options.getOutputFilePath() == null || options.getOutputFilePath().isEmpty()) {
                tmpFile = getTmpFile();
                if (tmpFile == null) {
                    IJ.showMessage("No output file was defined, unable to create temporary file.");
                    return;
                }
                options.setOutputFilePath(tmpFile);
                runDecompressOnTmpFile = true;
            }

            options.setInputDataInfo(new BufferInputData(pixelBuffers,
                                                         datasetDims,
                                                         InputData.PixelType.Gray16,
                                                         currentImage.getOriginalFileInfo().fileName));

            final ImageCompressor imageCompressor = new ImageCompressor(options);

            if (options.isVerbose()) {
                imageCompressor.addStatusListener(statusMessage ->
                                                          DefaultListeners.handleStatusReport(logger, statusMessage));

                imageCompressor.addProgressListener((message, index, finalIndex) ->
                                                            DefaultListeners.handleProgressReport(logger, message,
                                                                                                  index, finalIndex));
            }


            if (imageCompressor.compress()) {
                if (!runDecompressOnTmpFile)
                    IJ.showMessage("Compressed file is saved at: " + options.getOutputFilePath());
            } else {
                IJ.showMessage("Failed to compress the image. Check error log.");
            }

            if (runDecompressOnTmpFile) {
                // Decompress and show the file!
                new QCMPDecompressCommand(logger).openQCMPFile(tmpFile);
            }
        }
    }

    private String getTmpFile() {
        try {
            return File.createTempFile("qcmp_temp_file", FileExtensions.QCMP).getAbsolutePath();
        } catch (final IOException e) {
            return null;
        }
    }


    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }
}
