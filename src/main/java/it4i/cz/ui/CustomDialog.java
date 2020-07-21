package it4i.cz.ui;

import ij.gui.GenericDialog;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class CustomDialog extends GenericDialog {

    private final int NO_KEY = -1;
    private final Panel panel;
    private final GridBagConstraints constraints;

    private int currentRow = 0;
    private int currentCol = 0;

    private IModelValidator<CustomDialog> modelValidator;
    private HashMap<Integer, Component> componentModel;

    public CustomDialog(final String dialogTitle) {
        super(dialogTitle);
        componentModel = new HashMap<>();
        this.panel = new Panel();
        this.panel.setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.constraints.fill = GridBagConstraints.BOTH;
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

    public CustomDialog addIntegerField(final String labelText, final int defaultValue, final int componentKey) {
        Label label = new Label(labelText);
        TextField intInput = new TextField(Integer.toString(defaultValue));
        // TODO(Moravec): Add number check.
        addGridComponent(label, currentRow, currentCol++);
        addGridComponent(intInput, currentRow, currentCol);
        advanceToNextRow();

        if (componentKey != NO_KEY) {
            componentModel.put(componentKey, intInput);
        }

        return this;
    }

    private JFileChooser getOpenDirectoryDialog(final String title) {
        JFileChooser directoryChooser = new JFileChooser(title);
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        return directoryChooser;
    }

    private JFileChooser getSingleFileDialog(final String title) {
        JFileChooser fileChooser = new JFileChooser(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

    public CustomDialog addSaveFileField(final String labelText) {
        return addSaveFileField(labelText, NO_KEY);
    }

    public CustomDialog addSaveFileField(final String labelText, final int componentKey) {
        Label label = new Label(labelText);
        TextField filePathInput = new TextField();
        Button saveFileDialogBtn = new Button("Select file");

        saveFileDialogBtn.addActionListener(actionEvent -> {
            final JFileChooser saveFileDialog = getSingleFileDialog(labelText);
            if (saveFileDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                filePathInput.setText(saveFileDialog.getSelectedFile().getAbsolutePath());
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


}
