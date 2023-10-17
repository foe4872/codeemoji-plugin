package codeemoji.inlay.teamconsistency;

import java.util.ArrayList;
import java.util.List;

public class JavaFileData {
    private String fileName;
    private String lastModified;
    private List<String> variables = new ArrayList<>();

    // Konstruktor
    public JavaFileData(String fileName, String lastModified) {
        this.fileName = fileName;
        this.lastModified = lastModified;
    }

    // Getter und Setter für fileName
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Getter und Setter für lastModified
    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    // Getter für variables (Setter nicht notwendig, da wir die Liste direkt manipulieren können)
    public List<String> getVariables() {
        return variables;
    }

    // Methode zum Hinzufügen einer Variable
    public void addVariable(String variableName) {
        this.variables.add(variableName);
    }
}
