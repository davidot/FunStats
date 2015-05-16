package com.davidot.funstats;

import com.davidot.funstats.config.FunStatsConfiguration;
import com.intellij.analysis.BaseAnalysisActionDialog;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * todo
 *
 * @author davidot
 */
public class SettingsPanel extends JPanel {

    public SettingsPanel(Project project, BaseAnalysisActionDialog dialog) {
        super(new GridBagLayout());

        final FunStatsComponent component = project.getComponent(FunStatsComponent.class);

        FunStatsConfiguration configuration = component.getConfiguration();

        final JCheckBox checkBox = createAutoScroll(configuration);

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

    private JCheckBox createAutoScroll(final FunStatsConfiguration configuration) {
        final JCheckBox jCheckBox = new JCheckBox("Scan test files");
        jCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.setUseTestFiles(jCheckBox.isSelected());
            }
        });
        return jCheckBox;
    }

}
