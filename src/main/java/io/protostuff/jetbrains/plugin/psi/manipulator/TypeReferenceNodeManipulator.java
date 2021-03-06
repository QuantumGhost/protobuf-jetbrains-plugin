package io.protostuff.jetbrains.plugin.psi.manipulator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import io.protostuff.compiler.parser.ProtoParser;
import io.protostuff.jetbrains.plugin.ProtoLanguage;
import io.protostuff.jetbrains.plugin.ProtoParserDefinition;
import io.protostuff.jetbrains.plugin.psi.TypeReferenceNode;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.jetbrains.annotations.NotNull;

/**
 * Type reference node manipulator.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class TypeReferenceNodeManipulator extends AbstractElementManipulator<TypeReferenceNode> {

    @Override
    public TypeReferenceNode handleContentChange(@NotNull TypeReferenceNode node,
            @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String oldText = node.getText();
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText
                .substring(range.getEndOffset());
        Project project = node.getProject();
        PsiFileFactoryImpl factory = (PsiFileFactoryImpl) PsiFileFactory.getInstance(project);
        IElementType type = ProtoParserDefinition.rule(ProtoParser.RULE_typeReference);
        ScopeNode context = node.getContext();
        PsiElement newNode = factory
                .createElementFromText(newText, ProtoLanguage.INSTANCE, type, context);
        if (newNode == null) {
            throw new IncorrectOperationException();
        }
        return (TypeReferenceNode) node.replace(newNode);
    }
}
