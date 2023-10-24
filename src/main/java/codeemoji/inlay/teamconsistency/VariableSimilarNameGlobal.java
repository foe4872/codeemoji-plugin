package codeemoji.inlay.teamconsistency;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.INVERTEDEXISTS;

public class VariableSimilarNameGlobal extends CEProvider<NoSettings> {
    private List<JavaFileData> variablesFromXML;
    private boolean initialized = false;

    @Override
    protected InlayHintsCollector buildCollector(@NotNull Editor editor) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String currentFileName = virtualFile != null ? virtualFile.getName() : null;

        ReadVariablesFromXML readService = ServiceManager.getService(ReadVariablesFromXML.class);
        // Abrufen der bereits gelesenen Variablen aus der XML-Datei
        variablesFromXML = readService.getVariablesFromXML();

        return new CEVariableCollector(editor, getKeyId(), INVERTEDEXISTS) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element, @NotNull Map<?, ?> externalInfo) {
                String currentVarName = element.getName();
                for (JavaFileData fileData : variablesFromXML) {
                    if (currentFileName != null && !fileData.getFileName().equals(currentFileName)) {
                        for (String otherVarName : fileData.getVariables()) {
                            if (areInvertedCamelCases(currentVarName, otherVarName)) {
                                //System.out.println("The variable " + currentVarName + " is an inverted camel case of " + otherVarName);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
    }

    private boolean areInvertedCamelCases(String name1, String name2) {
        List<String> wordsName1 = splitCamelCase(name1);
        List<String> wordsName2 = splitCamelCase(name2);
        return wordsName1.size() == wordsName2.size() &&
                new HashSet<>(wordsName1).equals(new HashSet<>(wordsName2)) &&
                !name1.equals(name2);
    }

    private List<String> splitCamelCase(String s) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c) && currentWord.length() > 0) {
                words.add(currentWord.toString());
                currentWord.setLength(0);
            }
            currentWord.append(Character.toLowerCase(c));
        }
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        return words;
    }
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

}
