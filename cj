#!/usr/bin/env python3
import os
import shutil
import subprocess
import argparse
import json
import sys

REPO = os.path.dirname(os.path.realpath(__file__))
HOME = os.environ.get('HOME')
join = os.path.join


def run(cmd, *, cwd=None):
    subprocess.run(cmd, check=True, cwd=cwd)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('maincls')
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
    jspath = join(REPO, 'out', 'cj.js')
    mainroot = join(REPO, 'src', 'main', 'cj')
    testroot = join(REPO, 'src', 'test', 'cj')

    if args.build or not args.use_java:
        cmd = [
            'python3',
            join(REPO, 'scripts', 'run-class.py'),
        ] + (['-t', 'java'] if args.use_java else []) + [
            'crossj.hacks.cj.CJJSTranslator',
        ]
    else:
        cmd = [
            'java',
            '-cp', join(REPO, 'target', 'classes'),
            'crossj.hacks.cj.CJJSTranslator',
        ]

    cmd.extend(['-r', mainroot])

    if args.maincls == 'test':
        cmd.extend(['-r', testroot])
        for dirpath, dirnames, filenames in os.walk(testroot):
            for filename in filenames:
                if filename.endswith('Test.cj'):
                    relpath = os.path.relpath(join(dirpath, filename), testroot)
                    clsname = relpath[:-len('.cj')].replace(os.sep, '.')
                    cmd.extend(['-t', clsname])
    else:
        cmd.extend(['-m', args.maincls])

    cmd.extend(['-o', jspath])

    if args.verbose:
        print(f'cmd = {cmd}')

    run(cmd)
    run(['node', jspath])


if __name__ == '__main__':
    main()
