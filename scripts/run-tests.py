import os
import shutil
import subprocess
import argparse
import json
import sys

REPO = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
HOME = os.environ.get('HOME')
join = os.path.join
VERBOSE = False
XREPO = join(HOME, 'git', 'crossjx')
JAR = join(REPO, 'target', 'crossj-1.0-SNAPSHOT-jar-with-dependencies.jar')


def run(*args, **kwargs):
    subprocess.run(*args, check=True, **kwargs)


def rmtree(*args, **kwargs):
    shutil.rmtree(*args, ignore_errors=True, **kwargs)


def copytree(src, dst, *args, **kwargs):
    shutil.copytree(src, dst, *args, dirs_exist_ok=True, **kwargs)


def err(message):
    sys.stderr.write(f'ERROR: {message}\n')
    sys.exit(1)


def info(message):
    if VERBOSE:
        print(message)


def main():
    global VERBOSE
    parser = argparse.ArgumentParser()
    parser.add_argument('--verbose', '-v', default=False, action='store_true')
    parser.add_argument('--quiet', '-q', dest='verbose', default=False, action='store_false')
    parser.add_argument('--target', '-t', default='js', choices=('js', 'java'))
    args = parser.parse_args()
    VERBOSE = args.verbose
    target = args.target

    rmtree(join(REPO, 'out'))

    if target == 'js':
        xsrcdirs = []

        optdirs = [
            join(XREPO, 'support', 'js'),
            join(XREPO, 'support', 'shared'),
            join(XREPO, 'support', 'tests'),
        ]

        for optdir in optdirs:
            if os.path.isdir(optdir):
                xsrcdirs.extend(['-r', optdir])

        run([
            'java', '-jar', JAR,
            '-r', join(REPO, 'support', 'js'),
            '-r', join(REPO, 'support', 'shared'),
            '-r', join(REPO, 'support', 'tests'),
        ] + xsrcdirs + [
            '-m', 'crossj.misc.RunTests',
            '--no-prune',
        ])

        run(['node', join(REPO, 'out', 'bundle.js')])

    elif target == 'java':
        srcdirs = [
            join(REPO, 'support', 'java'),
            join(REPO, 'support', 'shared'),
            join(REPO, 'support', 'tests'),
        ]

        optdirs = [
            join(XREPO, 'support', 'java'),
            join(XREPO, 'support', 'shared'),
            join(XREPO, 'support', 'tests'),
        ]

        for optdir in optdirs:
            if os.path.isdir(optdir):
                srcdirs.append(optdir)

        srcs = []
        for srcdir in srcdirs:
            for dirpath, dirnames, filenames in os.walk(srcdir):
                for filename in filenames:
                    if filename.endswith('.java'):
                        srcs.append(join(dirpath, filename))

        run([
            'javac', '-d', join(REPO, 'out', 'clss'),
            '-encoding', "UTF-8",
            '-Xlint:unchecked',
        ] + srcs)

        testclasses = []
        for testroot in [join(REPO, 'support', 'tests'), join(XREPO, 'support', 'tests')]:
            if not os.path.isdir(testroot):
                continue
            for dirpath, dirnames, filenames in os.walk(testroot):
                for filename in filenames:
                    if filename.endswith('.java'):
                        classname = os.path.relpath(
                            join(dirpath, filename[:-len('.java')]),
                            testroot,
                        ).replace(os.sep, '.')
                        testclasses.append(classname)

        env = dict(os.environ)
        env['TESTCLASSES'] = ';'.join(testclasses)
        run([
            'java', '-cp', 'out/clss', 'crossj.misc.RunTests',
        ], env=env)

    else:
        err(f'Unrecognized target {target}')


if __name__ == '__main__':
    main()
