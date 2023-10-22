package codeemoji.inlay.teamconsistency;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.POOP;


public class VariableSimilarNameGlobal extends CEProvider<NoSettings> {
    private final EditorFactoryListener myListener = new EditorFactoryListener() {
        @Override
        public void editorCreated(@NotNull EditorFactoryEvent event) {
            // Ihre Logik, die beim Erstellen eines Editors ausgeführt wird
            Editor editor = event.getEditor();

            //buildCollector(editor);

            // Holen Sie sich die Instanz von ReadVariablesFromXML
            ReadVariablesFromXML readService = ServiceManager.getService(ReadVariablesFromXML.class);

            // Abrufen der bereits gelesenen Variablen aus der XML-Datei
            List<JavaFileData> variablesFromXML = readService.getVariablesFromXML();

            // Ausgabe der Daten
            for (JavaFileData fileData : variablesFromXML) {
                System.out.println("Dateiname: " + fileData.getFileName());
                System.out.println("Letzte Änderung: " + fileData.getLastModified());
                for (String variable : fileData.getVariables()) {
                    System.out.println("Variable: " + variable);
                }
                System.out.println("----");
            }
        }

    };

/*    public VariableSimilarNameGlobal() {
        EditorFactory.getInstance().addEditorFactoryListener(myListener, (disposable) -> {
            // Aufräumarbeiten, falls nötig, z.B. Listener entfernen
            EditorFactory.getInstance().removeEditorFactoryListener(myListener);
        });
    }*/

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @NotNull
    @Override
    public InlayHintsCollector buildCollector(@NotNull Editor editor) {
        // Ihre ursprüngliche Logik zur Erstellung des Collectors
        return new CEVariableCollector(editor, getKeyId(), POOP) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element, @NotNull Map<?, ?> externalInfo) {
                return false;
            }
        };
    }

    public void myMethod1() {
        // Ihre Methode, die beim Öffnen eines Editors aufgerufen wird
    }
}


