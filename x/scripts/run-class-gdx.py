import os
import shutil
import subprocess
import argparse
import json

REPO = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
HOME = os.environ.get('HOME')
join = os.path.join


def run(*args, **kwargs):
    subprocess.run(*args, check=True, **kwargs)


def rmtree(*args, **kwargs):
    shutil.rmtree(*args, ignore_errors=True, **kwargs)


def copytree(src, dst, *args, **kwargs):
    shutil.copytree(src, dst, *args, dirs_exist_ok=True, **kwargs)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        'key',
        help='qualified class name of the game to run')
    args = parser.parse_args()
    key: str = args.key
    pkg = key[:key.rindex('.')]
    clsn = key[len(pkg) + 1:]

    appdir = join(REPO, 'support', 'app', key)
    with open(join(appdir, 'config.json')) as f:
        config = json.load(f)

    rmtree(join(REPO, 'out'))
    run([
        'gdx-setup',
        '--dir', join(REPO, 'out', 'gdx'),
        '--name', config['name'],
        '--package', pkg,
        '--mainClass', clsn,
        '--excludeModules', 'android;ios',
    ])
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
