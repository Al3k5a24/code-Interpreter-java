package com.craftinginterpreters.lox;

import java.util.List;

abstract class TestGen {
    static class Binary extends TestGen {

        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    static class Grouping extends TestGen {

        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }
    }

    static class Literal extends TestGen {

        final Object value;

        Literal(Object value) {
            this.value = value;
        }
    }

    static class Unary extends TestGen {

        final Token operator;
        final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
    }
}
