package test;

import java.util.List;

abstract class TestGen {
    interface Visitor<R> {
    R visitBinaryTestGen(Binary testgen);
    R visitGroupingTestGen(Grouping testgen);
    R visitLiteralTestGen(Literal testgen);
    R visitUnaryTestGen(Unary testgen);
    }

  abstract <R> R accept(Visitor<R> visitor);
  static class Binary extends TestGen {

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryTestGen(this);
    }

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

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingTestGen(this);
    }

    final Expr expression;
    Grouping(Expr expression) {
      this.expression = expression;
  }
  }
  static class Literal extends TestGen {

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralTestGen(this);
    }

    final Object value;
    Literal(Object value) {
      this.value = value;
  }
  }
  static class Unary extends TestGen {

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryTestGen(this);
    }

    final Token operator;
    final Expr right;
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
  }
  }
}
