package com.davidot.funstats;

import com.davidot.funstats.results.Variable;
import com.davidot.funstats.results.VariableVisibility;
import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo
 *
 * @author davidot
 */
public class FunStatsCalculator {

    private final Project project;
    private final AnalysisScope scope;
    private List<Variable> variables = new ArrayList<Variable>();


    public FunStatsCalculator(Project project, AnalysisScope scope) {
        this.project = project;
        this.scope = scope;
    }

    public void run() {
        Task.Backgroundable task = new Task.Backgroundable(project,"Calculating funstats") {
            @Override
            public void run(@NotNull final ProgressIndicator indicator) {
                final int filesNum = scope.getFileCount();
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {



                        scope.accept(new PsiElementVisitor() {
                            int currentFilesDone = 0;

                            @Override
                            public void visitFile(PsiFile file) {
                                super.visitFile(file);
                                indicator.setText("Analyzing :" + file.getName());
                                analyzeFile(file);
                                currentFilesDone++;


                                indicator.setFraction((double) currentFilesDone / (double) filesNum);
                            }
                        });
                    }
                });

            }

            @Override
            public void onSuccess() {
                FunStatsCalculator.this.onSuccess();
            }
        };
        task.queue();
    }

    private void onSuccess() {
        System.out.println(variables);

        List<Variable> members = new ArrayList<>();
        List<Variable> parameters = new ArrayList<>();
        List<Variable> localfields = new ArrayList<>();

        for(Variable variable: variables) {
            switch(variable.getVisibility()) {
                case PUBLIC:
                    //fall-through
                case PROTECTED:
                    //fall-through
                case PACKAGE_PRIVATE:
                    //fall-through
                case PRIVATE:
                    members.add(variable);
                    break;
                case INLINE:
                    localfields.add(variable);
                    break;
                case PARAMETER:
                    parameters.add(variable);
                    break;
            }
        }

        HashMap<Integer, Integer> memberLengths = new HashMap<>();
        long total = 0;
        for(Variable variable: members) {
            int now = 1;
            int length = variable.getName().length();
            total += length;
            if(memberLengths.containsKey(length)) {
                now = memberLengths.get(length) + 1;
            }
            memberLengths.put(length, now);
        }

        double average = 0;
        if(!members.isEmpty()) {
            average = total / members.size();
        }

        int mean = 0;
        int meanLength = 0;
        int max = 0;

        System.out.println("\t-\tMembers\t-\t");
        System.out.println("|\tLength\t|\tTimes\t|");
        for(Map.Entry<Integer, Integer> entry : memberLengths.entrySet()) {
            System.out.println("|\t" + entry.getKey() + "\t\t|\t" + entry.getValue() + "\t\t|");
            if(entry.getValue() > mean) {
                mean = entry.getValue();
                meanLength = entry.getKey();
            }
            if(entry.getKey() > max) {
                max = entry.getKey();
            }
        }
        System.out.println("Average:" + average);
        System.out.println("Mean:" + (meanLength < 1 ? "No mean" :
                meanLength + " " + mean + " times"));

        for(Variable variable : members) {
            if(variable.getName().length() == max) {
                System.out.println("Longest membername:" + variable);
            }
        }

        /*HashMap<Integer, Integer> paramLengths = new HashMap<>();
        for(Variable variable: parameters) {
            int now = 1;
            int length = variable.getName().length();
            if(paramLengths.containsKey(length)) {
                now = paramLengths.get(length) + 1;
            }
            paramLengths.put(length, now);
        }

        System.out.println("\t-\tParameters\t-\t");
        System.out.println("|\tLength\t|\tTimes\t|");
        for(Map.Entry<Integer, Integer> entry : paramLengths.entrySet()) {
            System.out.println("|\t" + entry.getKey() + "\t\t|\t" + entry.getValue() + "\t\t|");
        }

        HashMap<Integer, Integer> localLengths = new HashMap<>();
        for(Variable variable: localfields) {
            int now = 1;
            int length = variable.getName().length();
            if(localLengths.containsKey(length)) {
                now = localLengths.get(length) + 1;
            }
            localLengths.put(length, now);
        }

        System.out.println("\t-\tLocal Fields\t-\t");
        System.out.println("|\tLength\t|\tTimes\t|");
        for(Map.Entry<Integer, Integer> entry : localLengths.entrySet()) {
            System.out.println("|\t" + entry.getKey() + "\t\t|\t" + entry.getValue() + "\t\t|");
        }*/

    }

    private void analyzeFile(PsiFile file) {
        for(PsiElement element: file.getChildren()) {
//            System.out.println("In file analyzing:{" + element.getText() + "}");
            if(element instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) element;
                analyzeClass(psiClass);
            }
        }
    }

    public void analyzeClass(PsiClass psiClass) {
        analyzeElementHolder(psiClass, psiClass);
    }

    public void analyzeElementHolder(PsiElement classElement, PsiClass psiClass) {
//        System.out.println("Analyzing holder:" + classElement);
        for(PsiElement element: classElement.getChildren()) {
            if(element instanceof PsiWhiteSpace || element instanceof PsiJavaToken) {
                continue;
            }
//            System.out.println("Analyzing element:" + element + " => " + element.getText());

            if(element instanceof PsiField || element instanceof PsiParameter || element instanceof PsiLocalVariable) {
                analyzeElement(element,psiClass);
            } else {
                analyzeElementHolder(element,psiClass);
            }
        }
        System.out.println("-----------------------------");
    }

    public void analyzeElement(PsiElement element, PsiClass containingClass) {
//        System.out.println("Analyzing element:" + element + " => " + element.getText());
        String name = "";
        VariableVisibility visibility = null;
        String type = "";

        if(element instanceof PsiField) {
            PsiField psiField = (PsiField) element;
//                System.out.println("Found field");

            PsiModifierList psiModifierList = psiField.getModifierList();
            String modifier;
            if(psiModifierList != null) {
                modifier = psiModifierList.getText();
                for(VariableVisibility vis : VariableVisibility.values()) {
                    if(modifier.equals(vis.getString())) {
                        visibility = vis;
                    }
                }
            }
            if(visibility == null) {
                visibility = VariableVisibility.PACKAGE_PRIVATE;
            }

            type = psiField.getType().getCanonicalText();

            name = psiField.getName();
        } else if(element instanceof PsiParameter) {
            PsiParameter psiParameter = (PsiParameter) element;

            type = psiParameter.getType().getCanonicalText();

            visibility = VariableVisibility.PARAMETER;

            name = psiParameter.getName();
        } else if(element instanceof PsiLocalVariable) {
            PsiLocalVariable localVariable = (PsiLocalVariable) element;

            type = localVariable.getType().getCanonicalText();

            visibility = VariableVisibility.INLINE;

            name = localVariable.getName();
        }

//        System.out.println("Variable name:" + name);
//        System.out.println("Variable type:" + type);
//        System.out.println("Variable visibility" + visibility);


        if(name.isEmpty()) {
//                System.out.println("Can't find name");
            return;
        } else if(visibility == null) {
//                System.out.println("Can't find visibility");
            return;
        } else if(type.isEmpty()) {
//                System.out.println("Can't find type");
            return;
        }

//            System.out.println("Found variable");


        variables.add(new Variable(name, visibility, type, containingClass.getName()));

    }



}
