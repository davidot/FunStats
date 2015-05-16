package com.davidot.funstats.config;

import com.intellij.openapi.components.PersistentStateComponent;
import org.jetbrains.annotations.Nullable;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsConfiguration implements PersistentStateComponent<FunStatsConfiguration.State> {

    class State {
        //settings
        public String currentProfile = "";
        public boolean autoScroll = false;
    }

    State state;

    public String getCurrentProfile() {
        return state.currentProfile;
    }

    public void setCurrentProfile(String currentProfile) {
        state.currentProfile = currentProfile;
    }

    public boolean getAutoScroll() {
        return state.autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        state.autoScroll = autoScroll;
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
