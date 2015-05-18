package com.davidot.funstats;

import com.davidot.funstats.config.FunStatsConfiguration;
import com.davidot.funstats.results.Variable;
import com.davidot.funstats.results.VariableVisibility;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsResults {

    private final FunStatsConfiguration config;

    private final FunStatsComponent component;
    private int filesRead;
    private boolean scanLocal = true;

    private List<Variable> variables = new ArrayList<>();

    private List<Variable> publicMembers = new ArrayList<>();
    private List<Variable> protectedMembers = new ArrayList<>();
    private List<Variable> packagePrivateMembers = new ArrayList<>();
    private List<Variable> privateMembers = new ArrayList<>();
    private List<Variable> paramMembers = new ArrayList<>();
    private List<Variable> localMembers = new ArrayList<>();
    private Project project;

    public FunStatsResults(Project project, FunStatsComponent component, int filesRead) {
        this.component = component;
        this.filesRead = filesRead;
        this.config = component.getConfiguration();
        scanLocal = config.getScanLocalFields();
        this.project = project;
    }

    public void addVariable(Variable variable) {
        if(variable.getVisibility() == VariableVisibility.INLINE && !scanLocal) {
            //don't add it
            return;
        }
        switch(variable.getVisibility()) {
            case PUBLIC:
                publicMembers.add(variable);
                break;
            case PROTECTED:
                protectedMembers.add(variable);
                break;
            case PACKAGE_PRIVATE:
                packagePrivateMembers.add(variable);
                break;
            case PRIVATE:
                privateMembers.add(variable);
                break;
            case INLINE:
                localMembers.add(variable);
                break;
            case PARAMETER:
                paramMembers.add(variable);
                break;
        }
    }

    public void show() {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);

        ToolWindow window = manager.registerToolWindow("FunStats", true, ToolWindowAnchor.BOTTOM);
        window.setTitle("FunStats");
        window.setAvailable(true,null);
        window.show(null);
        window.getComponent().add(createTabbedPane());

    }

    private Component createTabbedPane() {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.insertTab("Public members",null, createTab(publicMembers),"The public members",0);


        return tabbedPane;
    }

    private Component createTab(List<Variable> members) {
        JBTable table = new JBTable();
        table.setAutoResizeMode(JBTable.AUTO_RESIZE_OFF);



        return table;
    }

}
