package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VariableSimilarNameGlobal implements EditorFactoryListener {

    private final WriteVariablesInXML xmlReader = new WriteVariablesInXML();

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor.getProject() == null) return;

        System.out.println(" test VariableSimilarNameGlobal-editorCreated");

        // Lese Variablen aus der XML-Datei
        List<JavaFileData> variablesFromXML = xmlReader.readExistingXML();
        for (JavaFileData fileData : variablesFromXML) {
            System.out.println("Dateiname: " + fileData.getFileName());
            System.out.println("Letzte Änderung: " + fileData.getLastModified());
            for (String variable : fileData.getVariables()) {
                System.out.println("Variable: " + variable);
            }
            System.out.println("----");
        }


    }
}

