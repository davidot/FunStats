package com.davidot.funstats.results;

import com.davidot.funstats.FunStatsComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
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

    public static final String FUN_STATS_ID = "FunStats";

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
    private ToolWindow window;
    private JPanel contentPanel;

    public FunStatsResults(Project project, FunStatsComponent component, int filesRead) {
        this.component = component;
        this.filesRead = filesRead;
        scanLocal = true;
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

        if(manager.getToolWindow(FUN_STATS_ID) != null) {
            manager.unregisterToolWindow(FUN_STATS_ID);
        }
        window = manager.registerToolWindow(FUN_STATS_ID, true, ToolWindowAnchor.BOTTOM);
        window.setTitle("FunStats");
        window.setAvailable(true, null);
        contentPanel = new JPanel(new BorderLayout());
        window.getComponent().add(contentPanel);


        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new AnAction("Close", "Close the tab", AllIcons.Actions.Cancel) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                close();
            }
        });
        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar =
                actionManager.createActionToolbar("FunStats", toolbarGroup, false);
        contentPanel.add(toolbar.getComponent(), BorderLayout.WEST);
        contentPanel.add(createTabbedPane(), BorderLayout.CENTER);

        window.show(null);

    }

    private void close() {
        window.hide(null);
        window.setAvailable(false,null);
    }

    private Component createTabbedPane() {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.insertTab("Public members",null, createTab(toStats(publicMembers)),"The public members",0);
        tabbedPane.insertTab("Protected members",null, createTab(toStats(protectedMembers)),"The public members",0);
        tabbedPane.insertTab("Package local members",null, createTab(toStats(packagePrivateMembers)),"The public members",0);
        tabbedPane.insertTab("Private members",null, createTab(toStats(privateMembers)),"The public members",0);
        tabbedPane.insertTab("Local variables",null, createTab(toStats(localMembers)),"The public members",0);
        tabbedPane.insertTab("Parameters",null, createTab(toStats(paramMembers)),"The public members",0);


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
                return lengthMapSize + 1;
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if(rowIndex == 0) {
                    if(columnIndex == 0) {
                        return "Length";
                    }
                    return "Amount of appearances";
                }
                if(columnIndex == 0) {
                    return rows[rowIndex - 1];
                }
                return lengthMap.get(rows[rowIndex - 1]);
            }
        });
        table.setAutoResizeMode(JBTable.AUTO_RESIZE_OFF);



        return table;
    }

}
