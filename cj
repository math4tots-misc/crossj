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
CJ_SRC_ROOT = join(REPO, 'src', 'main', 'cj')
CJ_SRC_JS_ROOT = join(REPO, 'src', 'main', 'cj-js')
CJ_TEST_ROOT = join(REPO, 'src', 'test', 'cj')
CJ_TEST_JS_ROOT = join(REPO, 'src', 'test', 'cj-js')


def run(cmd, *, cwd=None):
    subprocess.run(cmd, check=True, cwd=cwd)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('id')
    parser.add_argument('--verbose', '-v', default=False, action='store_true')

    # whether to rebuild the transpiler.
    # For javascript targets, transpiler is always rebuilt
    # (however, note the transpiler's transpiler won't be rebuilt)
    parser.add_argument('--build', '-b', default=False, action='store_true')

    # flags for determining whether the transpiler itself should be built with java or javascript
    parser.add_argument('--use-java', '-j', default=True, action='store_true')
    parser.add_argument('--use-javascript', '--use-js', '-js', dest='use_java', action='store_false')

    # parser.add_argument('remainder', nargs=argparse.REMAINDER)
    args = parser.parse_args()

    config = load_config(args.id)
    type_ = config['type']

    if type_ == 'cli':
        jspath = join(REPO, 'out', 'cj.js')
        cli_config = config['cli']
        maincls = cli_config['main']
        emit_js(
            maincls=maincls,
            build_transpiler=args.build,
            use_java=args.use_java,
            verbose=args.verbose,
            out_js_path=jspath,
        )
        run(['node', jspath])

    elif type_ == 'cordova':
        tmpjspath = join(REPO, 'out', 'cj.js')
        jspath = join(CORDOVA_ROOT, 'www', 'js', 'cj.js')
        cordova_config = config['cordova']
        maincls = cordova_config['main']
        id = cordova_config['id']
        name = cordova_config['name']
        platforms = cordova_config['platforms']
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
        )
        shutil.move(tmpjspath, jspath)
        run(['cordova', 'run', 'browser'], cwd=CORDOVA_ROOT)

    else:
        raise Exception(f'Unrecognized app type {repr(type_)}')


def get_app_path(id):
    return join(REPO, 'src', 'main', 'app', id)


def load_config(id):
    app_path = get_app_path(id)
    config_path = join(app_path, 'config.json')

    if os.path.isfile(config_path):
        with open(config_path) as f:
            return json.loads(f.read())

    return {
        'type': 'cli',
        'cli': {
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


def emit_cordova_project(id, name, main_class, verbose, app_root, platforms):
    shutil.rmtree(CORDOVA_ROOT, ignore_errors=True)
    run(['cordova', 'create', CORDOVA_ROOT, id, name])
    for platform in platforms:
        run(['cordova', 'platform', 'add', platform], cwd=CORDOVA_ROOT)
    wwwroot = join(CORDOVA_ROOT, 'www')

    if join(app_root, 'index.html'):
        shutil.copyfile(join(app_root, 'index.html'), join(wwwroot, 'index.html'))


if __name__ == '__main__':
    main()
