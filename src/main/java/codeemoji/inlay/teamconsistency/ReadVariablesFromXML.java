package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadVariablesFromXML {
    private List<JavaFileData> variablesFromXML = new ArrayList<>();
    public List<JavaFileData> getVariablesFromXML() {
        return this.variablesFromXML;
    }

    public void ExecuteReadVariables(String outputPath) {
        this.variablesFromXML = extractVariablesFromXML(outputPath);
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
