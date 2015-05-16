package com.davidot.funstats.config;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.Nullable;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsConfiguration implements PersistentStateComponent<FunStatsConfiguration.State> {

    public class State {
        //settings
        public String currentProfile = "";
        public boolean useTestFiles = false;
    }

    State state;

    public String getCurrentProfile() {
        return state.currentProfile;
    }

    public void setCurrentProfile(String currentProfile) {
        state.currentProfile = currentProfile;
    }

    public boolean getAutoScroll() {
        return state.useTestFiles;
    }

    public void setUseTestFiles(boolean useTestFiles) {
        state.useTestFiles = useTestFiles;
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(State state) {
        this.state = state;
    }

}
