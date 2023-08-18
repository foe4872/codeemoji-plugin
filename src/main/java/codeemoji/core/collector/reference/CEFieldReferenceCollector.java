package codeemoji.core.collector.reference;

import codeemoji.core.collector.CESingleCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEFieldReferenceCollector extends CESingleCollector<PsiField, PsiReferenceExpression> {

    protected CEFieldReferenceCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        PsiReference reference = expression.getReference();
                        if (reference != null) {
                            PsiElement resolveElement = reference.resolve();
                            if (resolveElement instanceof PsiField field && checkHint(field)) {
                                addInlay(expression, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }
            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (reference != null) {
            PsiElement lastChild = reference.getLastChild();
            int length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }
}