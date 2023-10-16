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
    private final Map<String, List<String>> synonymDictionary = new HashMap<>();
    public Set<String> getCollectedVariableNames() {
        return variableNames;
    }

    public VariableSimilarName() {
        synonymDictionary.put("cost", Arrays.asList("price", "charge", "fee"));
        synonymDictionary.put("animal", Arrays.asList("beast", "creature"));
        synonymDictionary.put("car", Arrays.asList("vehicle", "automobile", "machine"));
        // hier weitere Synonyme hinzufügen ...
    }

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
                    if (areInvertedCamelCases(currentVarName, otherVarName) ||
                            hasSynonymousParts(currentVarName, otherVarName) ||
                            hasSameBaseWithDifferentNumericSuffix(currentVarName, otherVarName)) {
                        System.out.println("The variable " + currentVarName + " is similar to " + otherVarName);
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

            private boolean areSynonyms(String word1, String word2) {
                return synonymDictionary.getOrDefault(word1, Collections.emptyList()).contains(word2) ||
                        synonymDictionary.getOrDefault(word2, Collections.emptyList()).contains(word1);
            }

            private boolean hasSynonymousParts(String name1, String name2) {
                List<String> partsName1 = splitCamelCase(name1);
                List<String> partsName2 = splitCamelCase(name2);

                for (String part1 : partsName1) {
                    for (String part2 : partsName2) {
                        if (areSynonyms(part1, part2)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private boolean hasSameBaseWithDifferentNumericSuffix(String name1, String name2) {
                String base1 = name1.replaceAll("\\d+", "");
                String base2 = name2.replaceAll("\\d+", "");
                return base1.equals(base2) && !name1.equals(name2);
            }

        };
    }
}
