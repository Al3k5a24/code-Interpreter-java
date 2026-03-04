import java.util.List;

public class Parser {
    public final List<Token> tokens;
    public int current=0;

    boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    Token peek() {
        return tokens.get(current);
    }

    //previous je zapravo trenutni, jer current cita sekvencijalno
    // sto znaci da gleda karakter ispred kada procita
    Token previous() {
        return tokens.get(current - 1);
    }

    Parser(List<Token> tokens){
        this.tokens=tokens;
    }

    //definisemo izraz
    private Expr expression(){
        return equality();
    }

    private Expr equality(){
        Expr expr = comparison();

        // implementacija ( ... )* iz gramatike pravila
        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //proveravamo da li je trenutni token 1 od datih tipova
    private boolean match(TokenType... types){
        for(TokenType type:types){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }


    public Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Expr comparison(){
        Expr expr = term();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term(){
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }
}