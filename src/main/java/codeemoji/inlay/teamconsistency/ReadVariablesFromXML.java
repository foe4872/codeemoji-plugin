package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadVariablesFromXML implements StartupActivity {

    private final String path;

    ReadVariablesFromXML(){
        // Ermitteln des Projekt-Verzeichnisses und Pfad zur XML-Datei
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();

        if (openProjects.length > 0 && openProjects[0].getBasePath() != null) {
            path = openProjects[0].getBasePath()+ "\\Variables.xml";
        } else {
            // hardcodierten Pfad als Fallback verwenden, weil in Testumgebung es keine openProjects[0].getBasePath() gibt
            // das muss dann rausgenommen werden bevor es veröffentlicht wird
            path = "C:\\Users\\furka_bas98d7\\OneDrive - FH Vorarlberg\\Bachelorarbeit\\1_testProject\\Variables.xml";
        }
    }

    @Override
    public void runActivity(@NotNull Project project) {
        System.out.println("test ReadVariablesFromXML");

        // Lese Variablen aus der XML-Datei
        List<JavaFileData> variablesFromXML = extractVariablesFromXML(path);
        for (JavaFileData fileData : variablesFromXML) {
            System.out.println("Dateiname: " + fileData.getFileName());
            System.out.println("Letzte Änderung: " + fileData.getLastModified());
            for (String variable : fileData.getVariables()) {
                System.out.println("Variable: " + variable);
            }
            System.out.println("----");
        }
    }


    private List<JavaFileData> extractVariablesFromXML(String xmlFilePath) {
        List<JavaFileData> fileList = new ArrayList<>();

        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList fileNodes = doc.getElementsByTagName("file");

            for (int i = 0; i < fileNodes.getLength(); i++) {
                Node fileNode = fileNodes.item(i);

                if (fileNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element fileElement = (Element) fileNode;
                    String fileName = fileElement.getAttribute("name");
                    String lastModified = fileElement.getAttribute("lastModified");

                    JavaFileData fileData = new JavaFileData(fileName, lastModified);

                    NodeList variableNodes = fileElement.getElementsByTagName("variable");
                    for (int j = 0; j < variableNodes.getLength(); j++) {
                        Node variableNode = variableNodes.item(j);
                        if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element variableElement = (Element) variableNode;
                            String variableName = variableElement.getAttribute("name");
                            fileData.addVariable(variableName);
                        }
                    }

                    fileList.add(fileData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }
}
