package com.davidot.funstats.config;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.Nullable;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsConfiguration implements PersistentStateComponent<FunStatsConfiguration.State> {

    public static class State {

        //settings
        public String currentProfile = "";
        public boolean scanLocalFields = false;
    }

    State state = new State();

    public String getCurrentProfile() {
        return state.currentProfile;
    }

    public void setCurrentProfile(String currentProfile) {
        state.currentProfile = currentProfile;
    }

    public boolean getScanLocalFields() {
        return state.scanLocalFields;
    }

    public void setScanLocalFields(boolean scanLocalFields) {
        state.scanLocalFields = scanLocalFields;
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
