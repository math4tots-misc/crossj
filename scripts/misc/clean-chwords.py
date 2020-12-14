"""
Converts partially cleaned data from https://en.wiktionary.org/wiki/Appendix:Mandarin_Frequency_lists/1-1000
to something I'd put in src/main/data/cjx.chinese/

Sample usage:
python3 scripts/misc/clean-chwords.py words.xml > src/main/data/cjx.chinese/words03.txt
"""
import xml.etree.ElementTree as ET
import re
import sys

extractor0 = re.compile(r'title="([^"]+)"')
extractor1 = re.compile(r'>([^<>]+)</a></span></td>')

replacer0 = re.compile(r'<link\b.*')

def extract0(line):
    candidate0 = extractor0.search(line).group(1)
    if candidate0.endswith(' (page does not exist)'):
        candidate0 = candidate0[:-len(' (page does not exist)')]
    candidate1 = extractor1.search(line).group(1)
    assert candidate0 == candidate1, (candidate0, candidate1)
    return candidate0


def extract1(line):
    assert line.startswith('<td>')
    line = line[len('<td>'):].strip()
    line = replacer0.sub('', line)
    assert '<' not in line and '>' not in line, line
    return line


try:
    with open(sys.argv[1]) as f:
        while True:
            traditional = extract0(next(f))
            simplified = extract0(next(f))
            pinyin = extract0(next(f))
            definition = extract1(next(f))
            print(f"{traditional}|{simplified}|{pinyin}|{definition}")
except StopIteration:
    pass
