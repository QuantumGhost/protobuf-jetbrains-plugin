package io.protostuff.jetbrains.plugin;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import io.protostuff.jetbrains.plugin.psi.ProtoPsiFileRoot;
import io.protostuff.jetbrains.plugin.psi.ProtoType;
import io.protostuff.jetbrains.plugin.settings.ProtobufSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class GoToClassContributor implements GotoClassContributor {

    public static String getQualifiedNameUserType(ProtoType protoType) {
        return protoType.getFullName();
    }

    @Nullable
    @Override
    public String getQualifiedName(final NavigationItem item) {
        if (item instanceof ProtoType) {
            return getQualifiedNameUserType((ProtoType) item);
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        List<ProtoType> types = findUserTypes(project, includeNonProjectItems);
        List<String> names = new ArrayList<>(types.size());
        for (ProtoType type : types) {
            if (type.getName().length() > 0) {
                names.add(type.getName());
            }
        }
        return names.toArray(new String[names.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        List<ProtoType> types = findUserTypes(project, includeNonProjectItems, name);
        return types.toArray(new NavigationItem[types.size()]);
    }

    private List<ProtoType> findUserTypes(Project project, boolean includeNonProjectItems) {
        return getProtoTypes(project, includeNonProjectItems,
                protoType -> true);
    }

    private List<ProtoType> findUserTypes(Project project, boolean includeNonProjectItems, String key) {
        return getProtoTypes(project, includeNonProjectItems,
                protoType -> key.equals(protoType.getName()));
    }

    @NotNull
    private List<ProtoType> getProtoTypes(Project project, boolean includeNonProjectItems, Predicate<ProtoType> filter) {
        List<ProtoType> result = new ArrayList<>();
        List<VirtualFile> files = new ArrayList<>();
        addProjectAndLibraryFiles(project, includeNonProjectItems, files);
        if (includeNonProjectItems) {
            addFilesFromCustomIncludePath(project, files);
        }
        for (VirtualFile virtualFile : files) {
            ProtoPsiFileRoot file = (ProtoPsiFileRoot) PsiManager.getInstance(project).findFile(virtualFile);
            if (file != null) {
                Collection<ProtoType> types = file.getAllTypes();
                for (ProtoType type : types) {
                    if (filter.test(type)) {
                        result.add(type);
                    }
                }
            }
        }
        return result;
    }

    private void addProjectAndLibraryFiles(Project project, boolean includeNonProjectItems, List<VirtualFile> files) {
        FileBasedIndex index = FileBasedIndex.getInstance();
        GlobalSearchScope scope = getSearchScope(project, includeNonProjectItems);
        files.addAll(index.getContainingFiles(FileTypeIndex.NAME, ProtoFileType.INSTANCE, scope));
    }

    private void addFilesFromCustomIncludePath(Project project, List<VirtualFile> files) {
        ProtobufSettings settings = project.getComponent(ProtobufSettings.class);
        List<VirtualFile> includePaths = settings.getIncludePathsVf();
        for (VirtualFile includePath : includePaths) {
            FileBasedIndex.iterateRecursively(includePath, file -> {
                if (!file.isDirectory() && isProtoFile(file)) {
                    files.add(file);
                }
                return true;
            }, null, null, null);
        }
    }

    private boolean isProtoFile(VirtualFile file) {
        return file.getName().toLowerCase().endsWith(ProtoFileType.FILE_EXTENSION);
    }

    @NotNull
    private GlobalSearchScope getSearchScope(Project project, boolean includeNonProjectItems) {
        if (includeNonProjectItems) {
            return GlobalSearchScope.allScope(project);
        } else {
            return GlobalSearchScope.projectScope(project);
        }
    }

}