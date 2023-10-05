package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.POOP;

@SuppressWarnings("UnstableApiUsage")
public class VariableSimilarName extends CEProvider<NoSettings> {

    private final List<String> variableNames = new ArrayList<>();

    @Override
    public String getPreviewText() {
        return "";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        variableNames.clear(); // Clear the list at the start of each collection process.
        return new CEVariableCollector(editor, getKeyId(), POOP) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element, @NotNull Map<?, ?> externalInfo) {
                String currentVarName = element.getName();
                if (isCamelCase(currentVarName)) {
                    for (String otherVarName : variableNames) {
                        if (!currentVarName.equals(otherVarName) && isCamelCase(otherVarName) && isSimilarName(currentVarName, otherVarName)) {
                            System.out.println("The variable " + currentVarName + " is similar to " + otherVarName);
                            return true;
                        }
                    }
                    variableNames.add(currentVarName);
                }
                return false;
            }

            private boolean isCamelCase(String str) {
                return str.matches("[a-z]+([A-Z][a-z]+)+");
            }

            private boolean isSimilarName(String name1, String name2) {
                return isSimilar(name1, name2) || hasSimilarCharacters(name1, name2);
            }

            private boolean hasSimilarCharacters(String name1, String name2) {
                Set<Character> set1 = new HashSet<>();
                Set<Character> set2 = new HashSet<>();

                for (char c : name1.toCharArray()) set1.add(c);
                for (char c : name2.toCharArray()) set2.add(c);

                Set<Character> intersection = new HashSet<>(set1);
                intersection.retainAll(set2);

                Set<Character> union = new HashSet<>(set1);
                union.addAll(set2);

                double jaccardIndex = (double) intersection.size() / union.size();

                return jaccardIndex >= 0.5;
            }

            private boolean isSimilar(String a, String b) {
                if (a.equals(b)) return false;

                List<String> wordsA = splitCamelCase(a);
                List<String> wordsB = splitCamelCase(b);

                // Checking for common word segments
                for (String wordA : wordsA) {
                    for (String wordB : wordsB) {
                        if (wordA.equals(wordB)) {
                            int distance = StringUtils.getLevenshteinDistance(a, b);
                            return distance < 6; // Adjust this threshold as needed
                        }
                    }
                }
                return false;
            }

            private List<String> splitCamelCase(String s) {
                Matcher m = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])").matcher(s);
                List<String> words = new ArrayList<>();
                int start = 0;
                while (m.find()) {
                    words.add(s.substring(start, m.start()));
                    start = m.start();
                }
                words.add(s.substring(start));
                return words;
            }
        };
    }

    public List<String> getCollectedVariableNames() {
        return variableNames;
    }
}
