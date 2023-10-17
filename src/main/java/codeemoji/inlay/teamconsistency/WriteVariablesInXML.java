package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.PsiRecursiveElementVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class WriteVariablesInXML implements StartupActivity {

    String path = "C:\\Users\\furka_bas98d7\\OneDrive - FH Vorarlberg\\Bachelorarbeit\\1_testProject";
    @Override
    public void runActivity(@NotNull Project project) {
        System.out.println("test WriteVariablesInXML");
        createXMLFile();
        // Hier speichern wir alle Projekt-Variablen als JavaFileDate-Objekt in eine Liste ab
        //JavaFileData weil Inhalt von Javadatei und das Datum davon ausgelesen wird
        List<JavaFileData> collectedData = collectProjectVariables(project);

        // Die gesammelte Datenliste in die XML-Datei schreiben
        writeToXML(collectedData);
    }

    public void writeToXML(List<JavaFileData> collectedData) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            // Wurzelelement erstellen
            Document document = documentBuilder.newDocument();
            Element root = document.createElement("project");
            document.appendChild(root);

            // Daten in XML-Format umwandeln
            for (JavaFileData javaFileData : collectedData) {
                Element fileElement = document.createElement("file");
                fileElement.setAttribute("name", javaFileData.getFileName());
                fileElement.setAttribute("lastModified", javaFileData.getLastModified());

                for (String variableName : javaFileData.getVariables()) {
                    Element variableElement = document.createElement("variable");
                    variableElement.setAttribute("name", variableName);
                    fileElement.appendChild(variableElement);
                }

                root.appendChild(fileElement);
            }

            // In XML-Datei schreiben
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);

            // Hier setzen Sie den Pfad und den Namen der XML-Datei.
            String outputPath = path+ File.separator + "Variables.xml";
            StreamResult streamResult = new StreamResult(new File(outputPath));

            transformer.transform(domSource, streamResult);

            System.out.println("XML-Datei wurde erfolgreich erstellt!");

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public void createXMLFile() {
        ProjectManager projectManager = ProjectManager.getInstance();
        //PsiManager psiManager = PsiManager.getInstance(projectManager.getDefaultProject());

        // Ermitteln des Projekt-Verzeichnisses und Pfad zur XML-Datei
        Project[] openProjects = projectManager.getOpenProjects();
        String projectPath;

        if (openProjects.length > 0 && openProjects[0].getBasePath() != null) {
            projectPath = openProjects[0].getBasePath();
        } else {
            // Verwenden Sie den hardcodierten Pfad als Fallback, weil in Testumgebung es keine openProjects[0].getBasePath() gibt
            // das muss dann rausgenommen werden bevor es veröffentlicht wird
            projectPath = path;
        }
        String outputPath = projectPath + File.separator + "Variables.xml";

        /*
        // Überprüfen, ob das Verzeichnis für die XML-Datei existiert, andernfalls erstellen
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }*/

        System.out.println("Test createXMLFile " + outputPath);

        // Überprüfen, ob die XML-Datei existiert, andernfalls erstellen
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            try {
                if (outputFile.createNewFile()) {
                    try (PrintWriter writer = new PrintWriter(outputFile, StandardCharsets.UTF_8)) {
                        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                        writer.println("<project>");
                        writer.println("</project>");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("XML-Datei wurde erstellt: " + outputPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fehler beim Erstellen der XML-Datei: " + e.getMessage());
            }
        }
    }



    public List<JavaFileData> collectProjectVariables(Project project) {
        List<JavaFileData> javaFiles = new ArrayList<>();
        PsiManager psiManager = PsiManager.getInstance(project);

        VirtualFile baseDir = project.getBaseDir();
        PsiDirectory psiDirectory = psiManager.findDirectory(baseDir);

        if (psiDirectory != null) {
            psiDirectory.acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof PsiVariable) {
                        PsiVariable variable = (PsiVariable) element;
                        VirtualFile virtualFile = variable.getContainingFile().getVirtualFile();

                        if (virtualFile != null && "java".equalsIgnoreCase(virtualFile.getExtension())) {
                            long timeStamp = virtualFile.getTimeStamp();
                            Date lastModifiedDate = new Date(timeStamp);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                            String formattedDate = sdf.format(lastModifiedDate);

                            JavaFileData javaFileData = javaFiles.stream()
                                    .filter(jfd -> jfd.getFileName().equals(virtualFile.getName()))
                                    .findFirst()
                                    .orElseGet(() -> {
                                        JavaFileData newData = new JavaFileData(virtualFile.getName(), formattedDate);
                                        javaFiles.add(newData);
                                        return newData;
                                    });

                            javaFileData.addVariable(variable.getName());
                        }
                    }
                    super.visitElement(element);
                }
            });
        }

        return javaFiles;
    }





}



