package com.davidot.funstats;

import com.davidot.funstats.config.FunStatsConfiguration;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsComponent implements ProjectComponent {

    private final Project project;
    private final FunStatsConfiguration configuration = new FunStatsConfiguration();

    public FunStatsComponent(Project project) {
        this.project = project;

    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here

    }

    @NotNull
    public String getComponentName() {
        return "FunStatsComponent";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }
}
