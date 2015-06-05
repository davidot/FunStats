package com.davidot.funstats;

import com.davidot.funstats.results.FunStatsResults;
import com.davidot.funstats.results.Method;
import com.davidot.funstats.results.Variable;
import com.davidot.funstats.results.Visibility;
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
import com.intellij.psi.PsiMethod;
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
    private final FunStatsComponent component;
    private FunStatsResults results;
    private List<Variable> variables = new ArrayList<Variable>();
    private List<Method> methods = new ArrayList<Method>();


    public FunStatsCalculator(Project project, AnalysisScope scope, FunStatsComponent component) {
        this.project = project;
        this.scope = scope;
        this.component = component;
    }

    public void run() {
        Task.Backgroundable task = new Task.Backgroundable(project,"Calculating funstats") {
            @Override
            public void run(@NotNull final ProgressIndicator indicator) {
                final int filesNum = scope.getFileCount();
                results = new FunStatsResults(project,component,filesNum);
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
        results.show();
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
            System.out.println("Analyzing element:" + element + " => " + element.getText());

            if(element instanceof PsiMethod) {
                analyzeMethod((PsiMethod) element,psiClass);
            } else if(element instanceof PsiField || element instanceof PsiLocalVariable) {
                analyzeElement(element,psiClass);
            } else {
                analyzeElementHolder(element,psiClass);
            }
        }
        System.out.println("-----------------------------");
    }

    public void analyzeMethod(PsiMethod method, PsiClass containingClass) {
        String name = "";
        Visibility visibility = Visibility.PACKAGE_PRIVATE;
        List<PsiParameter> parameters = new ArrayList<>();
        String className = containingClass.getName();

        PsiModifierList psiModifierList = method.getModifierList();
        String modifier;
        if(psiModifierList != null) {
            modifier = psiModifierList.getText();
            for(Visibility vis : Visibility.values()) {
                if(modifier.equals(vis.getString())) {
                    visibility = vis;
                }
            }
        }


        for(PsiElement element :method.getParameterList().getChildren()) {
            if(element instanceof PsiParameter) {
                parameters.add((PsiParameter) element);
            }
        }

        List<Variable> params = new ArrayList<>();

        for(PsiParameter psiParameter: parameters) {
            String pType = psiParameter.getType().getCanonicalText();

            String pName = psiParameter.getName();

            if(pType.isEmpty() || pName.isEmpty()) {
                continue;
            }

            params.add(new Variable(pName,Visibility.PARAMETER,pType, className));
        }

        if(name.isEmpty()) {
//                System.out.println("Can't find name");
            return;
        }

        methods.add(new Method(name,visibility, params.toArray(new Variable[params.size()]),className));
        variables.addAll(params);


    }

    public void analyzeElement(PsiElement element, PsiClass containingClass) {
//        System.out.println("Analyzing element:" + element + " => " + element.getText());
        String name = "";
        Visibility visibility = null;
        String type = "";

        if(element instanceof PsiField) {
            PsiField psiField = (PsiField) element;
//                System.out.println("Found field");

            PsiModifierList psiModifierList = psiField.getModifierList();
            String modifier;
            if(psiModifierList != null) {
                modifier = psiModifierList.getText();
                for(Visibility vis : Visibility.values()) {
                    if(modifier.equals(vis.getString())) {
                        visibility = vis;
                    }
                }
            }
            if(visibility == null) {
                visibility = Visibility.PACKAGE_PRIVATE;
            }

            type = psiField.getType().getCanonicalText();

            name = psiField.getName();
        } else if(element instanceof PsiParameter) {
            PsiParameter psiParameter = (PsiParameter) element;

            type = psiParameter.getType().getCanonicalText();

            visibility = Visibility.PARAMETER;

            name = psiParameter.getName();
        } else if(element instanceof PsiLocalVariable) {
            PsiLocalVariable localVariable = (PsiLocalVariable) element;

            type = localVariable.getType().getCanonicalText();

            visibility = Visibility.INLINE;

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
