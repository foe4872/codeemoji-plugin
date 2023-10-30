package codeemoji.inlay.teamconsistency;

import java.util.ArrayList;
import java.util.List;

public class JavaFileData {
    private String fileName;
    private String lastModified;
    private List<String> variables = new ArrayList<>();

    public JavaFileData(String fileName, String lastModified) {
        this.fileName = fileName;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLastModified() {
        return lastModified;
    }

    public List<String> getVariables() {
        return variables;
    }

    // Methode zum Hinzufügen einer Variable
    public void addVariable(String variableName) {
        this.variables.add(variableName);
    }
}
