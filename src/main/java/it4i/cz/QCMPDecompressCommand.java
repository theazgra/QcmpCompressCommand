package it4i.cz;

import azgracompress.compression.CompressionOptions;
import azgracompress.compression.ImageDecompressor;
import azgracompress.data.ImageU16Dataset;
import azgracompress.fileformat.FileExtensions;
import azgracompress.io.FileInputData;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// TODO(Moravec): Place plugin inside Plugins menu.
//@Plugin(type = Command.class, menuPath = "Plugins>Compression>QCMP Decompression")
@Plugin(type = Command.class, menuPath = "Compression>QCMP Decompression")
public class QCMPDecompressCommand implements Command {
    @Override
    public void run() {
        JFileChooser fileOpenDialog = new JFileChooser("D:\\");
        fileOpenDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileOpenDialog.setAcceptAllFileFilterUsed(false);
        fileOpenDialog.setFileFilter(new FileNameExtensionFilter("QCMP files", FileExtensions.QCMP));
        if (fileOpenDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            final String filePath = fileOpenDialog.getSelectedFile().getAbsolutePath();

            final CompressionOptions decompressionOptions = new CompressionOptions();
            decompressionOptions.setInputDataInfo(new FileInputData(filePath));
            decompressionOptions.setOutputFilePath("D:\\tmp\\fiji_decompressed_qcmp.raw");

            IJ.showStatus("Decompressing the file...");
            ImageDecompressor imageDecompressor = new ImageDecompressor(decompressionOptions);
            final ImageU16Dataset decompressedDataset = imageDecompressor.decompressInMemory();
            if (decompressedDataset == null) {
                IJ.showMessage("Error decompressing the file.");
                return;
            }


            final ImagePlus img = NewImage.createShortImage("Decompressed image",
                    decompressedDataset.getPlaneDimensions().getX(),
                    decompressedDataset.getPlaneDimensions().getY(),
                    decompressedDataset.getPlaneCount(),
                    ImagePlus.GRAY16);

            final ImageStack imageStack = img.getImageStack();
            for (int planeIndex = 0; planeIndex < decompressedDataset.getPlaneCount(); planeIndex++) {
                imageStack.setPixels(decompressedDataset.getPlaneData(planeIndex), planeIndex + 1);
            }

            img.show();
        }
    }
}