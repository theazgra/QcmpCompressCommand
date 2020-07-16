/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package it4i.cz;

import azgracompress.U16;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.apache.log4j.pattern.LogEvent;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLTransactionRollbackException;
import java.util.ArrayList;
import java.util.List;

/**
 * This example illustrates how to create an ImageJ {@link Command} plugin.
 * <p>
 * The code here is a simple Gaussian blur using ImageJ Ops.
 * </p>
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link `run`} method implementation with your own logic.
 * </p>
 */
@Plugin(type = Command.class, menuPath = "Plugins>QCMP Compression")
public class QCMPCompressCommand<T extends RealType<T>> implements Command {

    // Different quantization methods.
    private static final String SQ = "Scalar";
    private static final String VQ1D = "Row Vector";
    private static final String VQ2D = "Matrix Vector";

    // Different codebook types.
    private static final String Individual = "Individual";
    private static final String MiddlePlane = "Middle plane";
    private static final String Global = "Global";

    @Parameter(label = "Input image or dataset")
    private File inputImageFile;

    @Parameter(label = "Quantization method", choices = {SQ, VQ1D, VQ2D})
    private String quantizationMethod = VQ2D;

    @Parameter(label = "Quantization codebook size", choices = {"4", "8", "16", "32", "64", "128", "256"})
    private String codebookSize = "256";

    @Parameter(label = "Vector width", min = "1", max = "50")
    private int vectorWidth = 3;

    @Parameter(label = "Vector height", min = "1", max = "50", required = false)
    private int vectorHeight = 3;

    @Parameter(label = "Codebook type", choices = {Individual, MiddlePlane, Global})
    private String codebookType = Individual;

    @Parameter(label = "Codebook cache", required = false, validater = "/d")
    private String codebookCacheDirectory;

    /**
     * Injected parameters.
     */
    @Parameter
    private LogService logger;

    @Parameter
    private DatasetIOService datasetIOService;

    @Parameter
    private UIService uiService;


    @Override
    public void run() {
        logger.info("Input file " + inputImageFile.getPath());
        logger.info("Quantization method: " + quantizationMethod + "(" + codebookType + " codebook, L = " + codebookSize + ")");
        logger.info(String.format("Vector dimensions [%dx%d]", vectorWidth, vectorHeight));

        try {
            final Dataset loadedDataset = datasetIOService.open(inputImageFile.getAbsolutePath());

            uiService.show(loadedDataset);
        } catch (IOException e) {
            logger.error(String.format("Failed to load an image from: '%s'", inputImageFile.getAbsolutePath()), e);
        }

        //        final Img<T> image = (Img<T>) currentData.getImgPlus();
        //        //
        //        // Enter image processing code here ...
        //        // The following is just a Gauss filtering example
        //        //
        //        final double[] sigmas = {1.0, 3.0, 5.0};
        //
        //        List<RandomAccessibleInterval<T>> results = new ArrayList<>();
        //
        //        for (double sigma : sigmas) {
        //            results.add(opService.filter().gauss(image, sigma));
        //        }
        //
        //        // display result
        //        for (RandomAccessibleInterval<T> elem : results) {
        //            uiService.show(elem);
        //        }
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


        // ask the user for a file to open
        //        final File file = ij.ui().chooseFile(null, "open");
        //
        //        if (file != null) {
        //            // load the dataset
        //            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());
        //
        //            // show the image
        //            ij.ui().show(dataset);
        //
        //            // invoke the plugin
        //            ij.command().run(QCMPCompressCommand.class, true);
        //        }
    }
}
