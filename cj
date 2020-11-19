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
    parser.add_argument('remainder', nargs=argparse.REMAINDER)
    args = parser.parse_args()
    jspath = join(REPO, 'out', 'cj.js')
    run([
        'python3',
        join(REPO, 'scripts', 'run-class.py'),
        'crossj.hacks.cj.CJJSTranslator',
        '-r', join(REPO, 'src', 'main', 'cj'),
        '-m', args.maincls,
        '-o', jspath,
    ])
    run(['node', jspath] + list(args.remainder))


if __name__ == '__main__':
    main()
