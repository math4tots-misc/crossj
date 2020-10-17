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
    pass


def main():
    global VERBOSE
    parser = argparse.ArgumentParser()
    parser.add_argument('--verbose', '-v', default=False, action='store_true')
    parser.add_argument('--quiet', '-q', dest='verbose', default=False, action='store_false')
    parser.add_argument(
        'key',
        help='qualified class name of the game to run')
    args = parser.parse_args()
    VERBOSE = args.verbose
    key: str = args.key
    pkg = key[:key.rindex('.')]
    clsn = key[len(pkg) + 1:]

    appdir = join(REPO, 'support', 'app', key)
    with open(join(appdir, 'config.json')) as f:
        config: dict = json.load(f)
    app_name = config.pop('name')
    extensions: list = config.pop('extensions', [])

    info(f'App name = {app_name}')

    if config:
        err(f'Unrecognized keys in config.json: {list(config.keys())}')

    rmtree(join(REPO, 'out'))
    run([
        'gdx-setup',
        '--dir', join(REPO, 'out', 'gdx'),
        '--name', app_name,
        '--package', pkg,
        '--mainClass', clsn,
        '--excludeModules', 'android;ios',
    ] + ([
        '--extensions', ';'.join(extensions),
    ] if extensions else []))
    run([
        'python3', 'cpdeps',
        '-o', join(REPO, 'out', 'gdx', 'core', 'src'),
        '-r', join(REPO, 'support', 'java'),
        '-r', join(REPO, 'support', 'shared'),
        '-r', join(REPO, 'support', 'gdx'),
        '-c', key,
    ])
    if os.path.isdir(join(appdir, 'assets')):
        copytree(
            join(appdir, 'assets'),
            join(REPO, 'out', 'gdx', 'core', 'assets'))

    rmtree(join(
        HOME,
        '.m2',
        'repository',
        'com',
        'badlogicgames',
        'gdx',
        'gdx-platform',
        '1.9.11'))

    for dirpath, dirnames, filenames in os.walk(join(REPO, 'out', 'gdx')):
        for filename in filenames:
            if filename == 'build.gradle':
                path = join(dirpath, filename)
                with open(path) as f:
                    data = f.read()
                data = data.replace(
                    'sourceCompatibility = 1.7',
                    'sourceCompatibility = 11')
                with open(path, 'w') as f:
                    f.write(data)

    os.chdir(join(REPO, 'out', 'gdx'))
    run([join(os.getcwd(), 'gradlew'), 'desktop:run'])


if __name__ == '__main__':
    main()
