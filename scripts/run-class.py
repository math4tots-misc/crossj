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
    parser.add_argument(
        '--target',
        '-t',
        default='auto',
        choices=('auto', 'desktop', 'android', 'java', 'js', 'web'))
    parser.add_argument(
        'key',
        help='qualified main class name to run')
    parser.add_argument(
        'extra',
        nargs=argparse.REMAINDER,
    )
    args = parser.parse_args()
    VERBOSE = args.verbose
    target = args.target
    key: str = args.key
    pkg = key[:key.rindex('.')]
    clsn = key[len(pkg) + 1:]
    extra = args.extra[1:] if args.extra[0:1] == ['--'] else args.extra[:]

    ## Check if there is an app config for this main class
    appdir = find_app_dir(key)

    if appdir:
        with open(join(appdir, 'config.json')) as f:
            config: dict = json.load(f)
        app_type = config.pop('type', 'gdx')

        if target == 'auto':
            if app_type == 'gdx':
                target = 'desktop'
            elif app_type == 'gdx-android':
                target = 'android'
            else:
                err(f'Could not determine default target for {app_type}')
    else:
        if target == 'auto':
            target = 'js'

        if target in ('java', 'js', 'web'):
            app_type = target
        else:
            err(f'App config not found for {key} (target={target})')

    if app_type in ('gdx', 'gdx-android'):
        main_gdx(
            app_type=app_type,
            target=target,
            key=key,
            pkg=pkg,
            clsn=clsn,
            config=config,
            appdir=appdir,
        )
    elif app_type == 'js':
        main_js(
            key=key,
            extra=extra,
        )
    elif app_type == 'web':
        main_web(
            key=key,
            extra=extra,
        )
    elif app_type == 'java':
        main_java(
            key=key,
            extra=extra,
        )
    else:
        err(f'Unrecognized app type {app_type}')


def find_app_dir(key):
    """
    Find the app config directory for the given main class, or return
    None if none is found.
    """
    # If an appdir with given key does not exist in the crossj repo,
    # check the crossjx repo.
    candidates = [
        join(REPO, 'support', 'app', key),
        join(XREPO, 'support', 'app', key),
    ]
    for appdir in candidates:
        if os.path.isdir(appdir):
            return appdir
    return None


def cpdeps(outdir, srcdirs, mainclss):
    """
    Copy all sources from the given srcdirs into outdirs
    that are reached by following the dependencies of maincls.
    """
    cmd = ['python3', 'cpdeps', '-o', outdir]
    for srcdir in srcdirs:
        cmd.extend(['-r', srcdir])
    for maincls in mainclss:
        cmd.extend(['-c', maincls])
    run(cmd)


def main_js(*, key, extra):
    transpiler_jar_path = join(
        REPO,
        'target',
        'crossj-1.0-SNAPSHOT-jar-with-dependencies.jar',
    )
    src_out_dir = join(REPO, 'out', 'js', 'src')
    rmtree(join(REPO, 'out'))
    cpdeps(
        outdir=src_out_dir,
        srcdirs=[
            join(REPO, 'support', 'js'),
            join(REPO, 'support', 'shared'),
        ] + [optdir for optdir in [
            join(XREPO, 'support', 'js'),
            join(XREPO, 'support', 'shared'),
        ] if os.path.isdir(optdir)],
        mainclss=[
            key,
            'java.lang.String',
            'java.util.Iterator',
            'java.lang.invoke.MethodHandles',
            'java.lang.annotation.Annotation',
        ],
    )
    run([
        'java', '-jar', transpiler_jar_path,
        '-r', src_out_dir,
        '-m', key,
        '-o', join(REPO, 'out', 'js'),
        '--no-prune',
    ])
    run([
        'node', join(REPO, 'out', 'js', 'bundle.js'),
    ] + extra)


def main_web(*, key, extra):
    transpiler_jar_path = join(
        REPO,
        'target',
        'crossj-1.0-SNAPSHOT-jar-with-dependencies.jar',
    )
    src_out_dir = join(REPO, 'out', 'web', 'src')
    rmtree(join(REPO, 'out'))
    cpdeps(
        outdir=src_out_dir,
        srcdirs=[
            join(REPO, 'support', 'js'),
            join(REPO, 'support', 'shared'),
        ] + [optdir for optdir in [
            join(XREPO, 'support', 'js'),
            join(XREPO, 'support', 'shared'),
        ] if os.path.isdir(optdir)],
        mainclss=[
            key,
            'java.lang.String',
            'java.util.Iterator',
            'java.lang.invoke.MethodHandles',
            'java.lang.annotation.Annotation',
        ],
    )
    run([
        'java', '-jar', transpiler_jar_path,
        '-r', src_out_dir,
        '-m', key,
        '-o', join(REPO, 'out', 'web'),
        '--target', 'web',
        '--no-prune',
    ])

    # This will only work on macos...
    run([
        'open', join(REPO, 'out', 'web', 'index.html'),
    ])


def main_java(*, key, extra):
    src_out_dir = join(REPO, 'out', 'java', 'src')
    cls_out_dir = join(REPO, 'out', 'java', 'cls')
    rmtree(join(REPO, 'out'))
    cpdeps(
        outdir=src_out_dir,
        srcdirs=[
            join(REPO, 'support', 'java'),
            join(REPO, 'support', 'shared'),
        ] + [optdir for optdir in [
            join(XREPO, 'support', 'java'),
            join(XREPO, 'support', 'shared'),
        ] if os.path.isdir(optdir)],
        mainclss=[key],
    )

    srcs = []
    for dirpath, dirnames, filenames in os.walk(src_out_dir):
        for filename in filenames:
            if filename.endswith('.java'):
                srcs.append(join(dirpath, filename))

    run([
        'javac', '-d', cls_out_dir,
    ] + srcs)

    run([
        'java', '-cp', cls_out_dir, key, '--',
    ] + extra)


def main_gdx(*, app_type, target, key, pkg, clsn, config, appdir):

    if app_type == 'gdx-android' and target != 'android':
        err(f'gdx-android app must target android backend')

    app_name = config.pop('name')
    extensions: list = config.pop('extensions', [])
    screen_orientation = config.pop('screen-orientation', 'landscape')

    info(f'App name = {app_name}')

    if app_type == 'gdx-android':
        android_config = config.pop('android', dict())
        android_permissions = android_config.pop('permissions', [])
        android_services = android_config.pop('services', [])
        if android_config:
            err(f"""Unrecognized android keys in config.json: {(
                list(android_config.keys())
            )}""")

    if config:
        err(f'Unrecognized keys in config.json: {list(config.keys())}')

    android_root = os.environ.get("ANDROID_SDK_ROOT")

    if app_type == 'gdx-android':
        exclude_modules = 'ios;desktop'
    else:
        exclude_modules = 'ios' if android_root else 'android;ios'

    rmtree(join(REPO, 'out'))
    run([
        'gdx-setup',
        '--dir', join(REPO, 'out', 'gdx'),
        '--name', app_name,
        '--package', pkg,
        '--mainClass', clsn,
        '--excludeModules', exclude_modules,
    ] + ([
        '--sdkLocation', android_root,
    ] if android_root else []) + ([
        '--extensions', ';'.join(extensions),
    ] if extensions else []))

    if app_type == 'gdx-android':
        # If an application is explicitly marked 'gdx-android',
        # it should be passed the starting Activity and Bundle
        for dirpath, dirnames, filenames in os.walk(join(REPO, 'out', 'gdx', 'android', 'src')):
            for filename in filenames:
                if filename == 'AndroidLauncher.java':
                    path = join(dirpath, filename)
                    with open(path) as f:
                        data = f.read()
                    data = data.replace(
                        f'initialize(new {clsn}(), config);',
                        f'initialize(new {clsn}(this, savedInstanceState), config);')
                    with open(path, 'w') as f:
                        f.write(data)

    optdirs = [
        join(XREPO, 'support', 'java'),
        join(XREPO, 'support', 'shared'),
        join(XREPO, 'support', 'gdx'),
    ] + (
        [join(XREPO, 'support', 'gdx-android')]
        if app_type == 'gdx-android' else
        []
    )

    target_src_dir = join(
        REPO,
        'out',
        'gdx',
        'android' if app_type == 'gdx-android' else 'core',
        'src')
    cpdeps(
        outdir=target_src_dir,
        srcdirs=[
            join(REPO, 'support', 'java'),
            join(REPO, 'support', 'shared'),
            join(REPO, 'support', 'gdx'),
        ] + (
            [join(REPO, 'support', 'gdx-android')]
            if app_type == 'gdx-android' else
            []
        ) + [optdir for optdir in optdirs if os.path.isdir(optdir)],
        mainclss=[key],
    )

    if os.path.isdir(join(appdir, 'assets')):
        copytree(
            join(appdir, 'assets'),
            join(REPO, 'out', 'gdx', 'android' if android_root else 'core', 'assets'))

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
            if filename == 'AndroidManifest.xml':
                path = join(dirpath, filename)
                with open(path) as f:
                    data = f.read()
                ####
                # Make sure that screen orientation is set
                ####
                data = data.replace(
                    'android:screenOrientation="landscape"',
                    f'android:screenOrientation="{screen_orientation}"',
                )
                if app_type == 'gdx-android':
                    ####
                    # Permissions
                    ####
                    parts = []
                    for permission in android_permissions:
                        parts.append(f"""<uses-permission android:name="{(
                            permission
                        )}"/>\n""")
                    data = data.replace(
                        '<application ',
                        ''.join(parts) + '<application ',
                    )

                    ####
                    # Declare components (e.g. service, activity, ...)
                    ####
                    parts = []
                    for service in android_services:
                        service_name = service.pop('name')
                        exported = service.pop('exported', True)
                        exported_str = "true" if exported else "false"
                        parts.append(
                            '<service \n'
                            f'   android:name="{service_name}"\n'
                            f'   android:exported="{exported_str}"\n'
                            '>\n'
                            '</service>\n'
                        )
                        if service:
                            err(f"""Unrecognized android service keys: {
                                list(service.keys())
                            }""")
                    data = data.replace(
                        '<activity',
                        ''.join(parts) + '<activity',
                    )
                with open(path, 'w') as f:
                    f.write(data)

            if filename == 'build.gradle':
                path = join(dirpath, filename)
                with open(path) as f:
                    data = f.read()
                ####
                # Update the used Java version. By default, 1.7 is set, but we want at least 8
                # or sometimes even 11.
                ####
                # data = data.replace(
                #     'sourceCompatibility = 1.7',
                #     'sourceCompatibility = 11')
                data = data.replace(
                    'sourceCompatibility = 1.7',
                    'sourceCompatibility = JavaVersion.VERSION_1_10')
                ####
                # Use retrolambda so that compiled code can be run on Android
                ####
                if target == 'android':
                    data = data.replace(
                    "allprojects {",
                    """plugins {
   id "me.tatarka.retrolambda" version "3.7.1"
}
retrolambda {
    javaVersion JavaVersion.VERSION_1_6
    defaultMethods false
    incremental true
}
allprojects {""",
                    )
                    data = data.replace(
                    '''project(":android") {
    apply plugin: "com.android.application"''',
                    '''project(":android") {
    apply plugin: "com.android.application"
    apply plugin: "me.tatarka.retrolambda"''',
                    )
                    data = data.replace(
                    '''project(":core") {
    apply plugin: "java-library"''',
                    '''project(":core") {
    apply plugin: "java-library"
    apply plugin: "me.tatarka.retrolambda"''',
                    )
                ###
                # minSdkVersion
                #   at least 24 is needed for static interface methods
                #   at least 26 is needed for lambda expressions
                ###
                data = data.replace(
                    'minSdkVersion 14\n',
                    'minSdkVersion 26\n',
                )
                with open(path, 'w') as f:
                    f.write(data)

    os.chdir(join(REPO, 'out', 'gdx'))

    if target == 'desktop':
        run([join(os.getcwd(), 'gradlew'), 'desktop:run'])
    elif target == 'android':
        run([join(os.getcwd(), 'gradlew'), 'android:installDebug'])
    else:
        raise Exception(f'Unrecognized target {target}')


if __name__ == '__main__':
    main()
