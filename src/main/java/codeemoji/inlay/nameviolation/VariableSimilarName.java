package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.POOP;

@SuppressWarnings("UnstableApiUsage")
public class VariableSimilarName extends CEProvider<NoSettings> {

    private final Set<String> variableNames = new HashSet<>();

    @Override
    public String getPreviewText() {
        return "";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        variableNames.clear();
        return new CEVariableCollector(editor, getKeyId(), POOP) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element, @NotNull Map<?, ?> externalInfo) {
                String currentVarName = element.getName();
                for (String otherVarName : variableNames) {
                    if (areInvertedCamelCases(currentVarName, otherVarName)) {
                        System.out.println("The variable " + currentVarName + " has inverted camel case with " + otherVarName);
                        return true;
                    }
                }
                variableNames.add(currentVarName);
                return false;
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

        };
    }

    public Set<String> getCollectedVariableNames() {
        return variableNames;
    }
}
