package com.davidot.funstats;

import com.intellij.analysis.AnalysisScope;
import com.intellij.analysis.BaseAnalysisAction;
import com.intellij.analysis.BaseAnalysisActionDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

/**
 * todo
 *
 * @author davidot
 */
public class FunStats extends BaseAnalysisAction {

    public FunStats() {
        super("Funstats calculation","Funstats");
    }

    @Override
    protected void analyze(@NotNull Project project, @NotNull AnalysisScope analysisScope) {
        Messages.showMessageDialog(project, "Test title", "Test Title 2", Messages.getInformationIcon());
    }

    @Nullable
    @Override
    protected JComponent getAdditionalActionSettings(Project project, BaseAnalysisActionDialog dialog) {
        return new SettingsPanel(project,dialog);
    }
}
