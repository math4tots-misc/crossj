#!/usr/bin/env python3
import os
import os.path
import shutil
import subprocess
import argparse
import json
import sys
import shutil

REPO = os.path.dirname(os.path.realpath(__file__))
HOME = os.environ.get('HOME')
join = os.path.join
CORDOVA_ROOT = join(REPO, 'out', 'cordova')
CANVAS_ROOT = join(REPO, 'out', 'canvas')
DATA_ROOT = join(REPO, 'src', 'main', 'data')
CJ_SRC_ROOT = join(REPO, 'src', 'main', 'cj')
CJ_SRC_JS_ROOT = join(REPO, 'src', 'main', 'cj-js')
CJ_TEST_ROOT = join(REPO, 'src', 'test', 'cj')
CJ_TEST_JS_ROOT = join(REPO, 'src', 'test', 'cj-js')


def run(cmd, *, cwd=None):
    subprocess.run(cmd, check=True, cwd=cwd)


def err(message):
    raise Exception(message)


CANVAS_HTML = r"""
<html>
    <head>
        <meta charset="utf-8">
        <title>Sample App</title>
        <style>
            * {
                margin: 0;
                padding: 0;
            }
            html, body {
                height: 100%;
            }
            #canvas {
                position: absolute;
                width: 100%;
                height: 100%;
            }
        </style>
    </head>
    <body>
        <canvas id="canvas"></canvas>
        <script src="cj.js"></script>
    </body>
</html>
"""

ACTIONS = ('run', 'build')


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('action', choices=ACTIONS)
    parser.add_argument('id')
    parser.add_argument('--verbose', '-v', default=False, action='store_true')

    # whether to rebuild the transpiler.
    # For javascript targets, transpiler is always rebuilt
    # (however, note the transpiler's transpiler won't be rebuilt)
    parser.add_argument('--build', '-b', default=False, action='store_true')

    # flags for determining whether the transpiler itself should be built with java or javascript
    parser.add_argument('--use-java', '-j', default=True, action='store_true')
    parser.add_argument('--use-javascript', '--use-js', '-js', dest='use_java', action='store_false')

    parser.add_argument('rest', nargs=argparse.REMAINDER)

    args = parser.parse_args()

    action = args.action
    config = load_config(args.id)
    type_ = config.pop('type')
    rest = args.rest

    if type_ == 'node':
        jspath = join(REPO, 'out', 'cj.js')
        cli_config = config.pop('node')
        maincls = cli_config.pop('main')
        if config or cli_config:
            err(f"Unrecognized config parameers (config {config}), (node {cli_config})")
        emit_js(
            maincls=maincls,
            build_transpiler=args.build,
            use_java=args.use_java,
            verbose=args.verbose,
            out_js_path=jspath,
        )
        if action == 'run':
            run(['node', jspath] + list(rest))

    elif type_ == 'canvas':
        canvas_config = config.pop('canvas')
        maincls = canvas_config.pop('main')
        app_path = get_app_path(args.id)
        wwwroot = join(CANVAS_ROOT, 'www')
        shutil.rmtree(wwwroot, ignore_errors=True)
        if os.path.exists(join(app_path, 'www')):
            shutil.copytree(join(app_path, 'www'), wwwroot)
        else:
            os.makedirs(wwwroot, exist_ok=True)

        jspath = join(wwwroot, 'cj.js')

        indexpath = join(wwwroot, 'index.html')
        with open(indexpath, 'w') as f:
            f.write(CANVAS_HTML)

        emit_js(
            maincls=maincls,
            build_transpiler=args.build,
            use_java=args.use_java,
            out_js_path=jspath,
            verbose=args.verbose,
        )
        if action == 'run':
            run(['open', indexpath])

    elif type_ == 'cordova':
        tmpjspath = join(REPO, 'out', 'cj.js')
        jspath = join(CORDOVA_ROOT, 'www', 'js', 'cj.js')
        cordova_config = config.pop('cordova')
        maincls = cordova_config.pop('main')
        id = cordova_config.pop('id')
        name = cordova_config.pop('name')
        platforms = cordova_config.pop('platforms')
        plugins = cordova_config.pop('plugins', [])
        data_map = cordova_config.pop('data', {})
        if config or cordova_config:
            err(f"Unrecognized config parameters (config {config}), (cordova {cordova_config})")
        emit_js(
            maincls=maincls,
            build_transpiler=args.build,
            use_java=args.use_java,
            verbose=args.verbose,
            out_js_path=tmpjspath,
        )
        emit_cordova_project(
            id=id,
            name=name,
            main_class=maincls,
            verbose=args.verbose,
            app_root=get_app_path(args.id),
            platforms=platforms,
            plugins=plugins,
        )
        shutil.move(tmpjspath, jspath)
        for relsrcpath, reldestpath in data_map.items():
            srcpath = join(DATA_ROOT, relsrcpath)
            destpath = join(CORDOVA_ROOT, reldestpath)
            os.makedirs(os.path.dirname(destpath), exist_ok=True)
            if os.path.isdir(srcpath):
                shutil.copytree(srcpath, destpath)
            else:
                shutil.copyfile(srcpath, destpath)
        if action == 'run':
            run(['cordova', 'run', 'browser'], cwd=CORDOVA_ROOT)

    else:
        err(f'Unrecognized app type {repr(type_)}')


def get_app_path(id):
    return join(REPO, 'src', 'main', 'app', id)


def load_config(id):
    app_path = get_app_path(id)
    config_path = join(app_path, 'config.json')

    if os.path.isfile(config_path):
        with open(config_path) as f:
            return json.loads(f.read())

    return {
        'type': 'node',
        'node': {
            'main': id,
        }
    }


def emit_js(maincls, build_transpiler, use_java, verbose, out_js_path):
    if build_transpiler or not use_java:
        cmd = [
            'python3',
            join(REPO, 'scripts', 'run-class.py'),
        ] + (['-t', 'java'] if use_java else []) + [
            'crossj.hacks.cj.CJJSTranslator',
        ]
    else:
        cmd = [
            'java',
            '-cp', join(REPO, 'target', 'classes'),
            'crossj.hacks.cj.CJJSTranslator',
        ]

    cmd.extend(['-r', CJ_SRC_ROOT, '-r', CJ_SRC_JS_ROOT])

    if maincls == 'test':
        for testroot in [CJ_TEST_ROOT, CJ_TEST_JS_ROOT]:
            cmd.extend(['-r', testroot])
            for dirpath, dirnames, filenames in os.walk(testroot):
                for filename in filenames:
                    if filename.endswith('Test.cj'):
                        relpath = os.path.relpath(join(dirpath, filename), testroot)
                        clsname = relpath[:-len('.cj')].replace(os.sep, '.')
                        cmd.extend(['-t', clsname])
    else:
        cmd.extend(['-m', maincls])

    cmd.extend(['-o', out_js_path])

    if verbose:
        print(f'cmd = {cmd}')

    run(cmd)


def emit_cordova_project(id, name, main_class, verbose, app_root, platforms, plugins):
    shutil.rmtree(CORDOVA_ROOT, ignore_errors=True)
    run(['cordova', 'create', CORDOVA_ROOT, id, name])
    for platform in platforms:
        run(['cordova', 'platform', 'add', platform], cwd=CORDOVA_ROOT)
    wwwroot = join(CORDOVA_ROOT, 'www')

    if os.path.exists(join(app_root, 'www')):
        shutil.rmtree(wwwroot)
        shutil.copytree(join(app_root, 'www'), wwwroot)

    os.makedirs(join(wwwroot, 'js'), exist_ok=True)
    os.makedirs(join(wwwroot, 'img'), exist_ok=True)
    os.makedirs(join(wwwroot, 'css'), exist_ok=True)

    for plugin in plugins:
        run(['cordova', 'plugin', 'add', plugin], cwd=CORDOVA_ROOT)


if __name__ == '__main__':
    main()
