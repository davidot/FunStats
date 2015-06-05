package com.davidot.funstats.results;

import com.davidot.funstats.FunStatsComponent;
import com.davidot.funstats.config.FunStatsConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;

import javax.swing.table.AbstractTableModel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<Method> methods = new ArrayList<>();
    private Project project;

    public FunStatsResults(Project project, FunStatsComponent component, int filesRead) {
        this.component = component;
        this.filesRead = filesRead;
        this.config = component.getConfiguration();
        scanLocal = config.getScanLocalFields();
        this.project = project;
    }

    public void addVariable(Variable variable) {
        if(variable.getVisibility() == Visibility.INLINE && !scanLocal) {
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

    public void addMethod(Method method) {
        methods.add(method);
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
        tabbedPane.insertTab("Public members",null, createTab(toStats(publicMembers)),"The public members",0);


        return tabbedPane;
    }

    private HashMap<Integer, Integer> toStats(List<Variable> members) {
        HashMap<Integer, Integer> memberLengths = new HashMap<>();
        for(Variable variable: members) {
            int now = 1;
            int length = variable.getName().length();
            if(memberLengths.containsKey(length)) {
                now = memberLengths.get(length) + 1;
            }
            memberLengths.put(length, now);
        }
        return memberLengths;
    }

    private Component createTab(final HashMap<Integer,Integer> lengthMap) {
        final int lengthMapSize = lengthMap.size();

        final int[] rows = new int[lengthMapSize];
        int i = 0;
        for(Map.Entry<Integer,Integer> entry:lengthMap.entrySet()) {
            rows[i++] = entry.getKey();
        }

        JBTable table = new JBTable(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return 2;
            }

            @Override
            public int getColumnCount() {
                return lengthMapSize;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if(rowIndex == 0) {
                    return rows[rowIndex];
                }
                return lengthMap.get(rows[rowIndex]);
            }
        });
        table.setAutoResizeMode(JBTable.AUTO_RESIZE_OFF);



        return table;
    }

}
