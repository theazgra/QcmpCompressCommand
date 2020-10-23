package it4i.cz;

import cz.it4i.qcmp.compression.CompressionOptions;
import cz.it4i.qcmp.compression.ImageDecompressor;
import cz.it4i.qcmp.data.ImageU16Dataset;
import cz.it4i.qcmp.fileformat.FileExtensions;
import cz.it4i.qcmp.io.FileInputData;
import ij.IJ;
import org.scijava.Priority;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

@Plugin(type = Command.class, menuPath = "Compression>QCMP>Decompress", priority = Priority.HIGH_PRIORITY)
public class QCMPDecompressCommand implements Command {

    @Parameter
    LogService logger;

    public QCMPDecompressCommand() {
    }

    public QCMPDecompressCommand(final LogService logger) {
        this.logger = logger;
    }

    @Override
    public void run() {
        final JFileChooser fileOpenDialog = new JFileChooser("D:\\");
        fileOpenDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileOpenDialog.setAcceptAllFileFilterUsed(false);
        fileOpenDialog.setFileFilter(new FileNameExtensionFilter("QCMP files", FileExtensions.QCMP));
        if (fileOpenDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            final String filePath = fileOpenDialog.getSelectedFile().getAbsolutePath();
            openQCMPFile(filePath);
        }
    }

    public void openQCMPFile(final String filePath) {
        final CompressionOptions decompressionOptions = new CompressionOptions();
        decompressionOptions.setInputDataInfo(new FileInputData(filePath));
        decompressionOptions.setOutputFilePath("D:\\tmp\\fiji_decompressed_qcmp.raw");

        IJ.showStatus("Decompressing the file...");
        final ImageDecompressor imageDecompressor = new ImageDecompressor(decompressionOptions);

        imageDecompressor.addStatusListener(statusMessage -> DefaultListeners.handleStatusReport(logger, statusMessage));
        imageDecompressor.addProgressListener((message, index, finalIndex) ->
                                                      DefaultListeners.handleProgressReport(logger, message, index, finalIndex));

        final Optional<ImageU16Dataset> maybeDecompressedDataset = imageDecompressor.decompressInMemory();
        if (!maybeDecompressedDataset.isPresent()) {
            IJ.showMessage("Error decompressing the file.");
            return;
        }

        ImageStackHelper.displayDataset(maybeDecompressedDataset.get(), new File(filePath).getName());
    }
}
