package cjx.re

import cjx.re.RegexMatcher
import cjx.re.NFA
import cjx.re.RegexNode
import cjx.re.DFA


## A Regex is a compiled list of patterns.
##
## When a regex is matched, the result includes a match index that indicates
## which of the patterns that make up the Regex matched.
##
## For example:
##
## <pre>
## var re = Regex.fromPatterns("aa*", "bb*");
## var matcher = re.matcher("bb");
## Assert.that(matcher.match());
## Assert.equals(matcher.getMatchIndex(), 1); // "bb*" was the pattern at index 1
## </pre>
class Regex {
    val _dfa: DFA

    def fromPatterns(patterns: List[String]): Try[Regex] {
        val regexNodes = List[RegexNode].builder()
        for pattern in patterns {
            regexNodes.add(RegexNode.fromPattern(pattern)?)
        }
        val nfa = NFA.fromRegexNodes(regexNodes.build())
        val dfa = DFA.fromNFA(nfa)
        return Try.Ok(Regex.new(dfa))
    }

    def dfa(self: Self): DFA {
        return self._dfa
    }

    def matcher(self: Self, string: String): RegexMatcher {
        return RegexMatcher._new(self._dfa, string)
    }

    ## Returns true if the entire string matches this regex.
    ##
    ## Equivalent to `re.matcher(string).matchAll()`
    def matches(self: Self, string: String): Bool {
        return self.matcher(string).matchAll()
    }

    def inspect(self: Self): String {
        return self._dfa.inspect()
    }
}
