import { count } from 'console';
import * as vscode from 'vscode';
import * as model from './model';


const COMMAND_AUTO_IMPORT = 'language-cj.autoimport';


export function activate(context: vscode.ExtensionContext) {

    const fs = vscode.workspace.fs;
    const world = new model.World(fs);

    async function addSourceRoot(uri: vscode.Uri) {
        const triple = model.parseSourceUri(uri);
        if (triple !== null) {
            const [srcroot,,] = triple;
            await world.addSourceRoot(srcroot);
        }
    }

    function lazyInit(documentUri: vscode.Uri) {
        addSourceRoot(documentUri);
        for (const editor of vscode.window.visibleTextEditors) {
            addSourceRoot(editor.document.uri);
        }
    }

    context.subscriptions.push(vscode.commands.registerCommand(COMMAND_AUTO_IMPORT, (qualifiedName: string) => {
        if (model.IMPORT_EXEMPT_CLASSES.has(qualifiedName)) {
            return;
        }
        const document = vscode.window.activeTextEditor?.document;
        if (document === undefined) {
            return;
        }
        {
            const triple = model.parseSourceUri(document.uri);
            if (triple !== null) {
                const [, pkg, clsname] = triple;
                if (qualifiedName === `${pkg}.${clsname}`) {
                    // You don't need to import the item in its own file.
                    return;
                }
            }
        }
        let pkgline = 0;
        let insertLineno = 0;
        let line = document.lineAt(insertLineno).text;
        while (line === '' || line.startsWith('#') || line.startsWith('package ')) {
            if (line.startsWith('package ')) {
                pkgline = insertLineno;
            }
            insertLineno++;
            line = document.lineAt(insertLineno).text.trim();
        }
        const addExtraNewline = !line.startsWith('import ');

        for (let ln = insertLineno; line.startsWith('import '); ln++, line = document.lineAt(ln).text) {
            const match = /import\s+([\w\.]+)/.exec(line);
            console.log(`MATCH = ${match !== null ? match[1] : 'nomatch'}`);
            if (match !== null && match[1] === qualifiedName) {
                // the import already exists, so there's no need to add an import line
                return;
            }
        }

        if (addExtraNewline && pkgline + 2 < insertLineno) {
            insertLineno = pkgline + 2;
        }

        console.log(`pkgline = ${pkgline}, insertLineno = ${insertLineno}`);

        const edit = new vscode.WorkspaceEdit();
        edit.insert(
            document.uri,
            document.lineAt(insertLineno).range.start,
            `import ${qualifiedName}\n${addExtraNewline ? '\n' : ''}`,
        );
        vscode.workspace.applyEdit(edit);
    }));

    context.subscriptions.push(vscode.workspace.onDidCreateFiles(event => {
        for (const file of event.files) {
            const unixpath = file.path;
            if (unixpath.endsWith(".cj")) {
                const triple = model.parseSourceUri(file);
                if (triple !== null) {
                    const [, pkg, clsname] = triple;
                    fs.writeFile(file, Buffer.from(`package ${pkg}

class ${clsname} {
}
`, 'utf-8'));
                }
            }
        }
    }));

    context.subscriptions.push(vscode.workspace.onDidOpenTextDocument(document => {
        if (document.languageId !== 'cj') {
            return undefined;
        }
        lazyInit(document.uri);
        console.log(`document.languageId = ${document.languageId}, document.fileName = ${document.fileName}`);
    }));

    context.subscriptions.push(vscode.workspace.onDidCloseTextDocument(document => {
        if (document.languageId !== 'cj') {
            return undefined;
        }
    }));

    context.subscriptions.push(vscode.languages.registerCompletionItemProvider('cj', {
        provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
            try {
                lazyInit(document.uri);

                const range = document.getWordRangeAtPosition(position);
                const prefix = document.getText(range);
                if (prefix.length === 0) {
                    return undefined;
                }

                let pkg = "";
                let clsname = "";
                {
                    const sourceTriple = model.parseSourceUri(document.uri);
                    if (sourceTriple !== null) {
                        const [, pkg_, clsname_] = sourceTriple;
                        pkg = pkg_;
                        clsname = clsname_;
                    }
                }

                // completion based on class names
                console.log(`prefix = ${prefix}`);
                addSourceRoot(document.uri);
                const items = Array.from(world.shortNameToQualifiedNames.filterWithPrefix(prefix)).flatMap((pair) => {
                    const [shortName, qualifiedNames] = pair;
                    return Array.from(qualifiedNames).sort().map(qualifiedName => {
                        const item = new vscode.CompletionItem(shortName);
                        item.detail = qualifiedName;
                        item.command = {
                            command: COMMAND_AUTO_IMPORT,
                            title: 'autoimport',
                            arguments: [qualifiedName],
                        };
                        return item;
                    });
                });

                // completion based on local names
                {
                    const item = world.getItemOrNull(pkg + '.' + clsname);
                    if (item !== null) {
                        for (const name of item.localNames) {
                            items.push(new vscode.CompletionItem(name));
                        }
                    }
                }

                console.log(`items.length = ${items.length}`)

                return items;
            } catch (e) {
                console.log(e.stack);
                console.log('ERROR: ' + e);
            }
        }
    }));

    context.subscriptions.push(vscode.languages.registerCompletionItemProvider('cj', {
        provideCompletionItems(document: vscode.TextDocument, position: vscode.Position) {
            try {
                lazyInit(document.uri);
                const range = document.getWordRangeAtPosition(position);

                const items: vscode.CompletionItem[] = [];

                // completion based on method and field names
                const prefix = range === undefined ? "" : document.getText(range);
                const linePrefix = document.lineAt(position).text.substr(0, position.character);
                if (linePrefix.endsWith('.' + prefix)) {
                    for (const fieldName of world.allFieldNames) {
                        if (fieldName.startsWith(prefix)) {
                            items.push(new vscode.CompletionItem(fieldName));
                        }
                    }
                    for (const methodName of world.allMethodNames) {
                        if (methodName.startsWith(prefix)) {
                            items.push(new vscode.CompletionItem(methodName + '('));
                        }
                    }
                }
                return items;
            } catch (e) {
                console.log(e.stack);
                console.log('' + e);
                throw e;
            }
        }
    }, '.'));
}
