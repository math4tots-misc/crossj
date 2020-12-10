
export class OpenDocument {
    words: Set<String>

    constructor() {
        this.words = new Set();
    }
}

/**
 * Parses a file system path and returns the triple:
 *  - source root path (i.e. path ending in '/src/main/cj/' or '/src/main/cj-js/')
 *  - package name (e.g. 'cjx.foobar')
 *  - class name (e.g. Nullable)
 *
 * If the path is invalid, returns null
 */
export function parseUnixPath(unixpath: string) : [string, string, string] | null {

    if (!unixpath.endsWith('.cj')) {
        return null;
    }

    unixpath = unixpath.substring(0, unixpath.length - '.cj'.length);

    for (const component of ['/src/main/cj/', '/src/main/cj-js/']) {
        const i = unixpath.lastIndexOf(component);
        if (i === -1) {
            continue;
        }
        const srcroot = unixpath.substring(0, i + component.length);
        const relpath = unixpath.substring(i + component.length);
        const j = relpath.lastIndexOf('/');
        const pkg = relpath.substring(0, j).replace(/\//g, '.');
        const clsname = relpath.substring(j + 1);
        return [srcroot, pkg, clsname];
    }

    return null;
}
