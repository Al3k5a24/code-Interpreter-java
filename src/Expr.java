import java.util.List;

public abstract class Expr {
    public interface Visitor<R> {
        R visitBinaryExpr(Binary expr);

        R visitGroupingExpr(Grouping expr);

        R visitLiteralExpr(Literal expr);

        R visitUnaryExpr(Unary expr);
    }

    abstract <R> R accept(Visitor<R> visitor);

    public static class Binary extends Expr {

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        public final Expr left;
        public final Token operator;
        public final Expr right;

        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    public static class Grouping extends Expr {

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }
    }

    static class Literal extends Expr {

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;

        Literal(Object value) {
            this.value = value;
        }
    }

    static class Unary extends Expr {

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }
    }
}
