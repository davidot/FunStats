package com.davidot.funstats;

import com.intellij.analysis.AnalysisScope;
import com.intellij.analysis.BaseAnalysisAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

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
    protected void analyze(@NotNull final Project project, @NotNull AnalysisScope analysisScope) {
        FunStatsComponent component = project.getComponent(FunStatsComponent.class);
        FunStatsCalculator calculator = new FunStatsCalculator(project,analysisScope,component);
        calculator.run();
    }
    

}
