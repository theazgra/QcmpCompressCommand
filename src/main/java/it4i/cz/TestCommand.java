package it4i.cz;

import azgracompress.data.V2i;
import azgracompress.data.V3i;
import azgracompress.io.BufferInputData;
import azgracompress.io.InputData;
import azgracompress.io.loader.IPlaneLoader;
import azgracompress.io.loader.PlaneLoaderFactory;
import azgracompress.utilities.Stopwatch;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import it4i.cz.ui.CustomDialog;
import org.scijava.Priority;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, menuPath = "Compression>Test", priority = Priority.HIGH_PRIORITY)
public class TestCommand implements Command {

    @Parameter
    LogService logger;

    @Override
    public void run() {
        final int VEC_DIM_KEY = 0;
        CustomDialog dialog = new CustomDialog("Voxel test").addIntegerField("Vector dimension", 3, VEC_DIM_KEY);
        if (!dialog.exec()) {
            return;
        }
        final int vecDim = dialog.getIntegerValue(VEC_DIM_KEY);


        //        DataCompressor.main(new String[]{"-tcb", "-vq", "3x3x3", "-o", "D:\\tmp\\test.qcmp", "D:\\biology\\tiff_data\\planes100
        //        .tif"});

        final ImagePlus currentImage = WindowManager.getCurrentImage();

        final int stackSize = currentImage.getImageStackSize();
        final ImageStack imageStack = currentImage.getImageStack();
        assert (currentImage.getNSlices() == stackSize);
        final V3i datasetDims = new V3i(currentImage.getWidth(),
                                        currentImage.getHeight(),
                                        currentImage.getImageStackSize());


        final Object[] pixelBuffers = new Object[stackSize];
        for (int planeIndex = 0; planeIndex < stackSize; planeIndex++) {
            final Object planePixelBuffer = imageStack.getPixels(planeIndex + 1);
            assert (planePixelBuffer != null);
            pixelBuffers[planeIndex] = planePixelBuffer;
        }
        BufferInputData bufferInputData = new BufferInputData(pixelBuffers,
                                                              datasetDims,
                                                              InputData.PixelType.Gray16,
                                                              currentImage.getOriginalFileInfo().fileName);
        IPlaneLoader loader;

        try {
            Stopwatch s = Stopwatch.startNew();
            loader = PlaneLoaderFactory.getPlaneLoaderForInputFile(bufferInputData);
            final V2i blocDim = new V2i(vecDim);
            logger.info("Vector dimensions: " + blocDim);
            final int[][] voxels = loader.loadBlocks(blocDim);
            s.stop();
            IJ.showMessage("Block loading time: " + s.getElapsedTimeString());
            logger.info("Block loading time: " + s.getElapsedTimeString());
            //            logger.info("continue exec...");
            //            s.restart();
            //            ImageU16Dataset reconstructedDataset = new Voxel(bufferInputData.getDimensions()).reconstructFromVoxels
            //            (blocDim, voxels);
            //            s.stop();
            //            IJ.showMessage("Voxel reconstruction time: " + s.getElapsedTimeString());
            //
            //            ImageStackHelper.displayDataset(reconstructedDataset, "Voxel reconstructed dataset");

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        //        CompressionOptions co = new CompressionOptions();
        //        co.setInputDataInfo(bufferInputData);
        //        co.setOutputFilePath("D:\\tmp\\test.qcmp");
        //        co.setQuantizationType(QuantizationType.Vector3D);
        //        co.setQuantizationVector(new V3i(3, 3, 3));
        //        co.setBitsPerCodebookIndex(4);
        //        co.setCodebookType(CompressionOptions.CodebookType.Global);
        //        ImageCompressor ic = new ImageCompressor(co);
        //        ic.trainAndSaveCodebook();
    }
}
