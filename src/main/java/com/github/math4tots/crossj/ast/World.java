package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.TypeSolver;
import com.github.math4tots.crossj.parser.Mark;
import com.github.math4tots.crossj.parser.Parser;
import com.github.math4tots.crossj.parser.Source;

import crossj.List;
import crossj.Map;
import crossj.Set;

public class World implements Node {
    private static final Mark MARK = new Mark(new Source("<world>", ""), 1, 1);

    /**
     * Maps fully qualified type names to their class-or-interface declarations
     */
    private final Map<String, ClassOrInterfaceDeclaration> map = Map.of();

    /**
     * Like map, but class names are stored
     *      packageName -> shortName -> <declaration>
     * instead of directly
     *      qualifiedName -> <declaration>
     *
     * This is to enable '*' imports, where looking up all classes in a package
     * is required.
     */
    private final Map<String, Map<String, ClassOrInterfaceDeclaration>> packageMap = Map.of();

    private final Set<String> packageNames = Set.of();

    private final TypeSolver typeSolver = new TypeSolver(this);

    @Override
    public Mark getMark() {
        return MARK;
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public World getWorld() {
        return this;
    }

    public ClassOrInterfaceDeclaration getTypeDeclaration(String qualifiedName) {
        return map.get(qualifiedName);
    }

    public List<ClassOrInterfaceDeclaration> getAllDeclarationsInPackage(String packageName) {
        Map<String, ClassOrInterfaceDeclaration> map = packageMap.get(packageName);
        if (map == null) {
            throw err("Package " + packageName + " not found");
        }
        return map.values().list();
    }

    public List<ClassOrInterfaceDeclaration> getAllDeclarations() {
        return map.values().list();
    }

    void addTypeDeclaration(ClassOrInterfaceDeclaration declaration) {
        String qualifiedName = declaration.getQualifiedName();
        map.put(qualifiedName, declaration);

        String packageName = declaration.getPackageName();
        if (!packageMap.containsKey(packageName)) {
            packageMap.put(packageName, Map.of());
        }
        packageMap.get(packageName).put(declaration.getName(), declaration);

        while (true) {
            packageNames.add(packageName);
            if (packageName.contains(".")) {
                packageName = packageName.substring(0, packageName.lastIndexOf("."));
            } else {
                break;
            }
        }
    }

    public boolean hasPackageWithName(String packageName) {
        return packageNames.contains(packageName);
    }

    @Override
    public TypeDeclaration lookupTypeDeclaration(String name) {
        return lookupClassOrInterfaceDeclaration(name);
    }

    @Override
    public VariableDeclaration lookupVariableDeclaration(String name) {
        // at this scope, there are no variables
        return null;
    }

    public ClassOrInterfaceDeclaration lookupClassOrInterfaceDeclaration(String name) {
        return map.get(name);
    }

    /**
     * Parses the given source and adds the TypeDeclaration to this world.
     * @param source
     */
    public void parse(Source source) {
        Parser.parse(this, source);
    }
}
