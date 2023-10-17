/*
package codeemoji.inlay.nameviolation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyApplicationComponent implements ApplicationComponent {

    @Override
    public void initComponent() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                programmstart();
            });
        }, 30, TimeUnit.SECONDS);
    }
    public void programmstart() {
        System.out.println("Test programmstart");
        ProjectManager projectManager = ProjectManager.getInstance();
        PsiManager psiManager = PsiManager.getInstance(projectManager.getDefaultProject());

        // Ermitteln des Projekt-Verzeichnisses und Pfad zur XML-Datei
        Project[] openProjects = projectManager.getOpenProjects();
        String projectPath;

        if (openProjects.length > 0 && openProjects[0].getBasePath() != null) {
            projectPath = openProjects[0].getBasePath();
        } else {
            // Verwenden Sie den hardcodierten Pfad als Fallback, weil in Testumgebung es keine openProjects[0].getBasePath() gibt
            // das muss dann rausgenommen werden bevor es veröffentlicht wird
            projectPath = "C:\\Users\\furka_bas98d7\\OneDrive - FH Vorarlberg\\Bachelorarbeit\\1_testProject";
        }
        String outputPath = projectPath + File.separator + "Variables.xml";
        System.out.println("Test programmstart2 " + outputPath);

        // Überprüfen, ob das Verzeichnis für die XML-Datei existiert, andernfalls erstellen
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Überprüfen, ob die XML-Datei existiert, andernfalls erstellen
        File outputFile = new File(outputPath);
        if (!outputFile.exists()) {
            try {
                if (outputFile.createNewFile()) {
                    System.out.println("XML-Datei wurde erstellt: " + outputPath);
                } else {
                    System.err.println("Fehler beim Erstellen der XML-Datei: " + outputPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Fehler beim Erstellen der XML-Datei: " + e.getMessage());
            }
        }

        // Iterieren Sie durch alle Java-Dateien und verarbeiten Sie sie
        VirtualFile[] files = projectManager.getOpenProjects()[0].getBaseDir().getChildren();
        for (VirtualFile file : files) {
            if ("java".equals(file.getExtension())) {

                // Überprüfen, ob die Java-Datei verarbeitet werden soll
                if (shouldProcessJavaFile(outputPath, file.getName(), file.getTimeStamp())) {
                    // Wenn ja, extrahieren Sie die Variablen und schreiben Sie sie in die XML-Datei
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile instanceof PsiJavaFile) {
                        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                        for (PsiClass psiClass : javaFile.getClasses()) {
                            for (PsiField psiField : psiClass.getFields()) {
                                String varName = psiField.getName();
                                writeVariableToXml(outputPath, file.getName(), varName, file.getTimeStamp());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean shouldProcessJavaFile(String outputPath, String fileName, long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(new Date(timeStamp));

        File xmlFile = new File(outputPath);
        if (!xmlFile.exists()) {
            return true; // Wenn die XML-Datei nicht existiert, verarbeiten Sie die Java-Datei
        }

        SAXBuilder builder = new SAXBuilder();
        try {
            Document doc = builder.build(xmlFile);
            Element root = doc.getRootElement();
            List<Element> javaFiles = root.getChildren("javaFile");
            for (Element javaFile : javaFiles) {
                if (javaFile.getAttributeValue("name").equals(fileName)) {
                    String lastModified = javaFile.getAttributeValue("lastModified");
                    if (lastModified.equals(formattedDate)) {
                        return false; // Das Datum stimmt überein, verarbeiten Sie die Java-Datei nicht
                    } else {
                        javaFile.detach(); // Entfernen Sie das alte Element, da das Datum nicht übereinstimmt
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void writeVariableToXml(String outputPath, String fileName, String varName, long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(new Date(timeStamp));

        Document doc;
        Element root;
        File xmlFile = new File(outputPath);

        if (xmlFile.exists()) {
            // Wenn die Datei existiert, lesen Sie sie ein
            SAXBuilder builder = new SAXBuilder();
            try {
                doc = builder.build(xmlFile);
                root = doc.getRootElement();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            // Wenn die Datei nicht existiert, erstellen Sie ein neues Dokument
            root = new Element("project");
            doc = new Document(root);
        }

        // Erstellen Sie ein neues Element und fügen Sie es dem Root-Element hinzu
        Element javaFile = new Element("javaFile");
        javaFile.setAttribute("name", fileName);
        javaFile.setAttribute("lastModified", formattedDate);
        javaFile.addContent(new Element("variable").setText(varName));
        root.addContent(javaFile);

        // Schreiben Sie das Dokument zurück in die Datei
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try (FileWriter writer = new FileWriter(xmlFile)) {
            outputter.output(doc, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

*/
