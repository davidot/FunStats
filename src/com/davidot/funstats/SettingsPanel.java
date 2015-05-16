package com.davidot.funstats;

import com.intellij.analysis.BaseAnalysisActionDialog;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * todo
 *
 * @author davidot
 */
public class SettingsPanel extends JPanel {

    public SettingsPanel(Project project, BaseAnalysisActionDialog dialog) {
        super(new GridBagLayout());

        final JCheckBox checkBox = buildCheckBox();

        final JComponent separator =
                new TitledSeparator("Funstats Calculator");

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets.left = 0;
        constraints.insets.bottom = 8;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        add(separator, constraints);

        constraints.gridy = 1;
        constraints.weighty = 1.0;
        constraints.insets.left = 12;
        add(checkBox, constraints);
    }

    private JCheckBox buildCheckBox() {
        return new JCheckBox("Check variable names");
    }

}
