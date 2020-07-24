package it4i.cz.ui;

import azgracompress.fileformat.FileExtensions;
import ij.IJ;
import ij.gui.GenericDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class CustomDialog extends GenericDialog {

    private static final String JFILE_CHOOSER_DEFAULT_DIR = "D:\\";
    private final int NO_KEY = -1;
    private final Panel panel;
    private final GridBagConstraints constraints;

    private int currentRow = 0;
    private int currentCol = 0;

    private IModelValidator<CustomDialog> modelValidator;
    private final HashMap<Integer, Component> componentModel;
    private final HashSet<Component> invalidComponents;
    private final Label warningLabel;
    private int warningKeySource = 0;

    public CustomDialog(final String dialogTitle) {
        super(dialogTitle);
        componentModel = new HashMap<>();
        invalidComponents = new HashSet<>();
        this.panel = new Panel();
        this.panel.setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.constraints.fill = GridBagConstraints.BOTH;

        this.warningLabel = new Label("");
        warningLabel.setForeground(Color.RED);
        addGridComponent(warningLabel, currentRow, 0);
        advanceToNextRow();
    }

    private void setWarning(final Component invalidComponent, final String warningMessage) {
        warningLabel.setText(warningMessage);
        invalidComponents.add(invalidComponent);

        for (final Button button : getButtons()) {
            if (button.getLabel() == null) continue;
            if (button.getLabel().trim().equals("OK")) {
                button.setEnabled(false);
                break;
            }
        }
        invalidate();
    }

    private void resetWarning(final Component invalidComponent) {
        warningLabel.setText("");

        invalidComponents.remove(invalidComponent);
        if (invalidComponents.size() > 0)
            return;

        for (final Button button : getButtons()) {
            if (button.getLabel() == null) continue;
            if (button.getLabel().trim().equals("OK")) {
                button.setEnabled(true);
                break;
            }
        }
    }

    private void addGridComponent(final Component component, int row, int col) {
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = col;
        constraints.gridy = row;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        panel.add(component, constraints);
        addPanel(panel);
    }

    private void advanceToNextRow() {
        ++currentRow;
        currentCol = 0;
    }

    public Component getComponent(int key) {
        if (key == NO_KEY)
            return null;
        return componentModel.get(key);
    }

    public void setValidator(final IModelValidator<CustomDialog> validator) {
        this.modelValidator = validator;
    }

    public boolean exec() {
        showDialog();
        if (modelValidator != null) {
            if (!modelValidator.validateModel(this)) {
                IJ.showMessage("Dialog model failed to validate");
                return false;
            }
        }
        return wasOKed();
    }


    public CustomDialog addComboBox(final String labelText,
                                    final String[] choices,
                                    final String defaultValue,
                                    final int componentKey) {

        Label label = new Label(labelText);
        Choice comboBox = new Choice();
        for (final String option : choices)
            comboBox.addItem(option);
        comboBox.select(defaultValue);
        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(comboBox, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, comboBox);
        }

        return this;
    }

    public CustomDialog addComboBox(final String labelText, final String[] choices, final String defaultValue) {
        return addComboBox(labelText, choices, defaultValue, NO_KEY);
    }


    public CustomDialog addIntegerField(final String labelText, final int defaultValue) {
        return addIntegerField(labelText, defaultValue, NO_KEY);
    }

    private boolean isInteger(final String str) {
        try {
            int tmp = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException x) {
            return false;
        }
    }

    public CustomDialog addIntegerField(final String labelText, final int defaultValue, final int componentKey) {
        Label label = new Label(labelText);
        TextField intInput = new TextField(Integer.toString(defaultValue));

        intInput.addTextListener(textAction -> {
            if (!isInteger(intInput.getText())) {
                setWarning(intInput, "Invalid integer value for '" + labelText + "'");
            } else {
                resetWarning(intInput);
            }
        });
        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(intInput, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, intInput);
        }

        return this;
    }

    private JFileChooser getOpenDirectoryDialog(final String title) {
        JFileChooser directoryChooser = new JFileChooser(JFILE_CHOOSER_DEFAULT_DIR);
        directoryChooser.setDialogTitle(title);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return directoryChooser;
    }

    private JFileChooser getSingleFileDialog(final String title, final String extension) {
        JFileChooser fileChooser = new JFileChooser(JFILE_CHOOSER_DEFAULT_DIR);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (extension != null) {
            FileNameExtensionFilter extFilter = new FileNameExtensionFilter(extension, extension);
            fileChooser.setFileFilter(extFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        return fileChooser;
    }

    public CustomDialog addOpenFolderField(final String labelText) {
        return addOpenFolderField(labelText, NO_KEY);
    }

    public CustomDialog addOpenFolderField(final String labelText, final int componentKey) {
        Label label = new Label(labelText);
        TextField directoryInput = new TextField();
        Button openFolderDialogBtn = new Button("Choose folder");

        openFolderDialogBtn.addActionListener(actionEvent -> {
            final JFileChooser dirOpener = getOpenDirectoryDialog(labelText);
            if (dirOpener.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                directoryInput.setText(dirOpener.getSelectedFile().getAbsolutePath());
            }
        });


        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(directoryInput, currentRow, currentCol++);
        addGridComponent(openFolderDialogBtn, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, directoryInput);
        }

        return this;
    }

    public CustomDialog addSaveFileField(final String labelText, final String extension) {
        return addSaveFileField(labelText, extension, NO_KEY);
    }

    public CustomDialog addSaveFileField(final String labelText, final String extension, final int componentKey) {
        Label label = new Label(labelText);
        TextField filePathInput = new TextField();
        Button saveFileDialogBtn = new Button("Select file");

        saveFileDialogBtn.addActionListener(actionEvent -> {
            final JFileChooser saveFileDialog = getSingleFileDialog(labelText, extension);

            if (saveFileDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String absPath = saveFileDialog.getSelectedFile().getAbsolutePath();
                if (!absPath.endsWith(FileExtensions.QCMP)) {
                    absPath += ("." + FileExtensions.QCMP);
                }
                filePathInput.setText(absPath);
            }
        });

        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(filePathInput, currentRow, currentCol++);
        addGridComponent(saveFileDialogBtn, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, filePathInput);
        }

        return this;
    }


    public CustomDialog addInfoField(final String labelText, final String info) {
        Label label = new Label(labelText);
        Label infoLabel = new Label(info);
        infoLabel.setForeground(Color.BLUE);
        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(infoLabel, currentRow, currentCol++);
        advanceToNextRow();
        return this;
    }
}
