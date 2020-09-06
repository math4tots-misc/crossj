package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;
import com.github.math4tots.crossj.parser.Parser;
import com.github.math4tots.crossj.parser.Source;

import crossj.List;
import crossj.Map;

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
    }

    @Override
    public TypeDeclaration lookupTypeDeclaration(String name) {
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
