package codeemoji.inlay.teamconsistency;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.intellij.openapi.startup.ProjectActivity;

public class WriteVariablesInXML implements ProjectActivity {
    private final String path;
    private final String outputPath;
    private ReadVariablesFromXML _readVariablesFromXML;

    public WriteVariablesInXML() {
        // Ermitteln des Projekt-Verzeichnisses und Pfad zur XML-Datei
        ProjectManager projectManager = ProjectManager.getInstance();
        Project[] openProjects = projectManager.getOpenProjects();
         _readVariablesFromXML = ServiceManager.getService(ReadVariablesFromXML.class);

        if (openProjects.length > 0 && openProjects[0].getBasePath() != null) {
            path = openProjects[0].getBasePath();
        } else {
            // hardcodierten Pfad als Fallback verwenden, weil in Testumgebung es keine openProjects[0].getBasePath() gibt
            // das muss dann rausgenommen werden bevor es veröffentlicht wird
            path = "C:\\Users\\furka_bas98d7\\OneDrive - FH Vorarlberg\\Bachelorarbeit\\1_testProject";
        }
        outputPath = path + File.separator + "Variables.xml";
    }

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> {
            ApplicationManager.getApplication().runReadAction(() -> {
                createXMLFile();
                List<JavaFileData> changedFiles = collectProjectVariables(project);
                writeToXML(changedFiles);
                _readVariablesFromXML.ExecuteReadVariables(outputPath);
            });
        });
        return null;
    }

    public List<JavaFileData> readExistingXML() {
        List<JavaFileData> existingData = new ArrayList<>();
        try {
            File xmlFile = new File(outputPath);
            if (!xmlFile.exists()) return existingData;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            NodeList fileNodes = doc.getElementsByTagName("file");
            for (int i = 0; i < fileNodes.getLength(); i++) {
                Element fileElement = (Element) fileNodes.item(i);
                String fileName = fileElement.getAttribute("name");
                String lastModified = fileElement.getAttribute("lastModified");

                JavaFileData javaFileData = new JavaFileData(fileName, lastModified);
                existingData.add(javaFileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existingData;
    }

    public void writeToXML(List<JavaFileData> changedData) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document;
            File xmlFile = new File(outputPath);
            if (xmlFile.exists()) {
                document = documentBuilder.parse(xmlFile);
            } else {
                document = documentBuilder.newDocument();
                Element rootElement = document.createElement("project");
                document.appendChild(rootElement);
            }

            for (JavaFileData javaFileData : changedData) {
                NodeList existingFileNodes = document.getElementsByTagName("file");
                Element matchingFileElement = null;

                for (int i = 0; i < existingFileNodes.getLength(); i++) {
                    Element currentFileElement = (Element) existingFileNodes.item(i);
                    if (currentFileElement.getAttribute("name").equals(javaFileData.getFileName())) {
                        matchingFileElement = currentFileElement;
                        break;
                    }
                }

                if (matchingFileElement != null) {
                    matchingFileElement.getParentNode().removeChild(matchingFileElement);
                }

                Element fileElement = document.createElement("file");
                fileElement.setAttribute("name", javaFileData.getFileName());
                fileElement.setAttribute("lastModified", javaFileData.getLastModified());

                for (String variableName : javaFileData.getVariables()) {
                    Element variableElement = document.createElement("variable");
                    variableElement.setAttribute("name", variableName);
                    fileElement.appendChild(variableElement);
                }

                document.getDocumentElement().appendChild(fileElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(xmlFile);
            transformer.transform(domSource, streamResult);

            if (changedData.isEmpty()) {
                System.out.println("Keine Änderungen vorhanden. XML-Datei wurde nicht aktualisiert.");
            } else {
                System.out.println("XML-Datei wurde erfolgreich aktualisiert!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fehler beim Schreiben in die XML-Datei: " + e.getMessage());
        }
    }

    public List<JavaFileData> collectProjectVariables(Project project) {
        List<JavaFileData> javaFiles = new ArrayList<>();
        List<JavaFileData> existingFiles = readExistingXML();
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile baseDir = project.getBaseDir();
        PsiDirectory psiDirectory = psiManager.findDirectory(baseDir);

        if (psiDirectory != null) {
            psiDirectory.acceptChildren(new PsiRecursiveElementVisitor() {
                @Override
                public void visitFile(PsiFile file) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    if (file instanceof PsiJavaFile && "java".equalsIgnoreCase(virtualFile.getExtension())) {
                        long timeStamp = virtualFile.getTimeStamp();
                        Date lastModifiedDate = new Date(timeStamp);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                        String formattedDate = sdf.format(lastModifiedDate);

                        Optional<JavaFileData> matchingExistingFile = existingFiles.stream()
                                .filter(jfd -> jfd.getFileName().equals(virtualFile.getName()))
                                .findFirst();

                        if (!matchingExistingFile.isPresent() ||
                                (matchingExistingFile.isPresent() && !matchingExistingFile.get().getLastModified().equals(formattedDate))) {

                            System.out.println("Abweichung identifiziert für Datei: " + virtualFile.getName());

                            JavaFileData javaFileData = new JavaFileData(virtualFile.getName(), formattedDate);
                            javaFiles.add(javaFileData);

                            // Variablen für diese Java-Datei sammeln
                            if (file instanceof PsiJavaFile) {
                                file.accept(new JavaRecursiveElementVisitor() {
                                    @Override
                                    public void visitField(@NotNull PsiField field) {
                                        javaFileData.addVariable(field.getName());
                                        super.visitField(field);
                                    }
                                    @Override
                                    public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                                        javaFileData.addVariable(variable.getName());
                                        super.visitLocalVariable(variable);
                                    }
                                });
                            }
                        }
                    }
                    super.visitFile(file);
                }
            });
        }
        return javaFiles;
    }

    public void createXMLFile() {
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
        } else {
            System.out.println("XML-Datei existiert bereits: " + outputPath);
        }
    }
}
