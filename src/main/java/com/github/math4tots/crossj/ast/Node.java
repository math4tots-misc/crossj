package com.github.math4tots.crossj.ast;

import com.github.math4tots.crossj.parser.Mark;

public interface Node {
    Mark getMark();
    Node getParent();
}
