package com.davidot.funstats;

import com.davidot.funstats.config.FunStatsConfiguration;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * todo
 *
 * @author davidot
 */
@State(name = "Funstats", storages = {
        @Storage(id= "default", file = StoragePathMacros.PROJECT_FILE)
})
public class FunStatsComponent implements ProjectComponent, PersistentStateComponent<FunStatsConfiguration.State> {

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

    public FunStatsConfiguration getConfiguration() {
        return configuration;
    }

    @Nullable
    @Override
    public FunStatsConfiguration.State getState() {
        return configuration.getState();
    }

    @Override
    public void loadState(FunStatsConfiguration.State state) {
        configuration.loadState(state);
    }
}
