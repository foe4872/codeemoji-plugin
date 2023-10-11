package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import static codeemoji.inlay.nameviolation.NameViolationSymbols.SMALL_NAME;

@SuppressWarnings("UnstableApiUsage")
public class ShortDescriptiveName extends CEProvider<ShortDescriptiveNameSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                                
                  private String s = ": ";
                                
                  public String statement(String p) {
                    String result = p + "-> ";
                    while (rentals.hasMoreElements()) {
                      Rental a = (Rental) rentals.nextElement();
                      result += a.getMovie().getTitle() + s
                        + String.valueOf(a.calculateAmount());
                    }
                    return result;
                  }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), SMALL_NAME) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element, @NotNull Map<?, ?> externalInfo) {
                if (null != element.getNameIdentifier()) {
                    if (!CheckScopeLineCounter(element)){
                        return false;
                    }
                    return getSettings().getNumberOfLetters() >= element.getNameIdentifier().getTextLength();
                }
                return false;
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShortDescriptiveNameSettings settings) {
        return new ShortDescriptiveNameConfigurable(settings);
    }

    public boolean CheckScopeLineCounter(@NotNull PsiVariable _psiVariable) {
        PsiVariable psiVariable = _psiVariable;
        boolean isBigScope = false;

        // Traverse the PSI tree to find the enclosing block, method, class or catch section
        PsiElement currentElement = psiVariable;
        while (currentElement != null) {
            if (currentElement instanceof PsiCodeBlock
                    || currentElement instanceof PsiMethod
                    || currentElement instanceof PsiClass
                    || currentElement instanceof PsiCatchSection) {  // Add this line
                break;
            }
            currentElement = currentElement.getParent();
        }

        if (currentElement != null) {
            // Get the start and end offsets of the enclosing element
            int startOffset = currentElement.getTextRange().getStartOffset();
            int endOffset = currentElement.getTextRange().getEndOffset();

            // Get the document corresponding to the PsiFile
            PsiFile psiFile = currentElement.getContainingFile();
            Document document = FileDocumentManager.getInstance().getDocument(psiFile.getVirtualFile());

            if (document != null) {
                // Calculate the start and end line numbers
                int startLine = document.getLineNumber(startOffset);
                int endLine = document.getLineNumber(endOffset);

                // Calculate the number of lines over which the variable is in scope
                int numLines = endLine - startLine + 1;

                //System.out.println("The variable "+ psiVariable.getNameIdentifier().getText() + " is in scope for " + numLines + " lines.");
                if(numLines >= getSettings().getBigScope()) {
                    isBigScope = true;
                }
            }
        }
        return isBigScope;
    }


}