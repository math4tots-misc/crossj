import * as vscode from 'vscode';
import * as model from './model';


export function activate(context: vscode.ExtensionContext) {

    const fs = vscode.workspace.fs;

    context.subscriptions.push(vscode.workspace.onDidCreateFiles(event => {
        for (const file of event.files) {
            const unixpath = file.path;
            if (unixpath.endsWith(".cj")) {
                const triple = model.parseUnixPath(unixpath);
                if (triple !== null) {
                    const [srcroot, pkg, clsname] = triple;
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
        console.log(`document.languageId = ${document.languageId}, document.fileName = ${document.fileName}`);
    }))

    context.subscriptions.push(vscode.workspace.onDidCloseTextDocument(document => {
        if (document.languageId !== 'cj') {
            return undefined;
        }
    }));
}
