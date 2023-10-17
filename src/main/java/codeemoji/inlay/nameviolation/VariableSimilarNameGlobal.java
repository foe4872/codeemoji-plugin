package codeemoji.inlay.nameviolation;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class VariableSimilarNameGlobal implements EditorFactoryListener {
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        if (editor.getProject() == null) return;

        // Extrahiere VirtualFile aus Editor
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        System.out.println(" test VariableSimilarNameGlobal-editorCreated");
        // Hier Logik hinzufügen
    }
}
