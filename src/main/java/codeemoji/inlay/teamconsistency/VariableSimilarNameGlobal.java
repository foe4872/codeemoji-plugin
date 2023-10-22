package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import com.intellij.openapi.startup.ProjectActivity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VariableSimilarNameGlobal implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> {

            // Hier wird ein Listener zum EditorFactory hinzugefügt, der darauf wartet, dass ein neuer Editor in IntelliJ erstellt wird.
            // Sobald ein neuer Editor geöffnet wird, wird die Methode `editorCreated` aufgerufen.
            EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener() {
                @Override
                public void editorCreated(@NotNull EditorFactoryEvent event) {
                    System.out.println("Ein Editor wurde geöffnet!");

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
            }, project);
        });
        return null;
    }
}
