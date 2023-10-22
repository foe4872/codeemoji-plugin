package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VariableSimilarNameGlobal {

    public VariableSimilarNameGlobal() {
        //initializeListener();
    }

    public void initializeListener() {
        ProjectManager projectManager = ProjectManager.getInstance();
        Project project = projectManager.getDefaultProject();

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener() {
            @Override
            public void editorCreated(@NotNull EditorFactoryEvent event) {
                System.out.println("Ein Editor wurde geöffnet!");

                // Instanz von ReadVariablesFromXML abrufen
                ReadVariablesFromXML readService = ServiceManager.getService(ReadVariablesFromXML.class);
                // Abrufen der bereits gelesenen Variablen aus der XML-Datei
                List<JavaFileData> variablesFromXML = readService.getVariablesFromXML();

                // Daten ausgeben
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
    }
}
