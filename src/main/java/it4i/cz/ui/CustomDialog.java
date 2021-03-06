package it4i.cz.ui;

import cz.it4i.qcmp.fileformat.FileExtensions;
import ij.IJ;
import ij.gui.GenericDialog;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.TextListener;
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
    private final int warningKeySource = 0;

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
            if (button.getLabel() == null)
                continue;
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
            if (button.getLabel() == null)
                continue;
            if (button.getLabel().trim().equals("OK")) {
                button.setEnabled(true);
                break;
            }
        }
    }

    private void addGridComponent(final Component component, final int row, final int col) {
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

    public Component getComponent(final int key) {
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

        final Label label = new Label(labelText);
        final Choice comboBox = new Choice();
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


    private boolean isInteger(final String str) {
        try {
            final int tmp = Integer.parseInt(str);
            return true;
        } catch (final NumberFormatException x) {
            return false;
        }
    }

    public CustomDialog addCheckBox(final String label, final boolean defaultValue) {
        return addCheckBox(label, defaultValue, NO_KEY);
    }

    public CustomDialog addCheckBox(final String labelText, final boolean defaultValue, final int componentKey) {
        final Checkbox checkbox = new Checkbox(labelText, defaultValue);
        addGridComponent(checkbox, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, checkbox);
        }
        return this;
    }

    public int getIntegerValue(final int componentKey) {
        return Integer.parseInt(((TextField) getComponent(componentKey)).getText());
    }

    public CustomDialog addIntegerField(final String labelText, final int defaultValue) {
        return addIntegerField(labelText, defaultValue, NO_KEY, null);
    }

    public CustomDialog addIntegerField(final String labelText,
                                        final int defaultValue,
                                        final int componentKey) {
        return addIntegerField(labelText, defaultValue, componentKey, null);
    }

    public CustomDialog addIntegerField(final String labelText,
                                        final int defaultValue,
                                        final int componentKey,
                                        final TextListener customTextListener) {

        final Label label = new Label(labelText);
        final TextField intInput = new TextField(Integer.toString(defaultValue));

        intInput.addTextListener(textAction -> {
            if (!isInteger(intInput.getText())) {
                setWarning(intInput, "Invalid integer value for '" + labelText + "'");
            } else {
                resetWarning(intInput);
            }
        });

        if (customTextListener != null) {
            intInput.addTextListener(customTextListener);
        }

        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(intInput, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, intInput);
        }

        return this;
    }

    private JFileChooser getOpenDirectoryDialog(final String title) {
        final JFileChooser directoryChooser = new JFileChooser(JFILE_CHOOSER_DEFAULT_DIR);
        directoryChooser.setDialogTitle(title);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return directoryChooser;
    }

    private JFileChooser getSingleFileDialog(final String title, final String extension) {
        final JFileChooser fileChooser = new JFileChooser(JFILE_CHOOSER_DEFAULT_DIR);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (extension != null) {
            final FileNameExtensionFilter extFilter = new FileNameExtensionFilter(extension, extension);
            fileChooser.setFileFilter(extFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        return fileChooser;
    }

    public CustomDialog addOpenFolderField(final String labelText) {
        return addOpenFolderField(labelText, NO_KEY);
    }

    public CustomDialog addOpenFolderField(final String labelText, final int componentKey) {
        final Label label = new Label(labelText);
        final TextField directoryInput = new TextField();
        final Button openFolderDialogBtn = new Button("Choose folder");

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
        final Label label = new Label(labelText);
        final TextField filePathInput = new TextField();
        final Button saveFileDialogBtn = new Button("Select file");

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
        final Label label = new Label(labelText);
        final Label infoLabel = new Label(info);
        infoLabel.setForeground(Color.BLUE);
        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(infoLabel, currentRow, currentCol++);
        advanceToNextRow();
        return this;
    }
}
