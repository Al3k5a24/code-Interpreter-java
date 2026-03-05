import java.util.List;

public class Parser {

    private static class ParseError extends RuntimeException {}

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

    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
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
        Expr expr = term(); //evaluiramo term(visi prioritet)

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                TokenType.LESS,
                TokenType.LESS_EQUAL)){
            Token operator = previous(); //uzimamo operatore koje smo sad prosli
            Expr right = term(); // desna strana je opet term
            expr = new Expr.Binary(expr, operator, right); //gradi cvor
        }

        return expr;
    }

    private Expr term(){
        Expr expr = factor(); //pozivamo nivo ispod (visi prioritet)
        while (match(TokenType.MINUS, TokenType.PLUS)) { // ista logika kao comparison, samo za + i -
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    //ista logika kao comparison
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
        if (match(TokenType.BANG, TokenType.MINUS)) { //prefiks operator, zato ide if
            Token operator = previous();
            Expr right = unary(); //rekurzivni poziv
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary(){
        if(match(TokenType.FALSE)) return new Expr.Literal(false);
        if(match(TokenType.TRUE)) return new Expr.Literal(true);
        if(match(TokenType.NIL)) return new Expr.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING)){
            return new Expr.Literal(previous().literal);
        }

        //Kada naiđe na (, poziva expression() koji je na vrhu hijerarhije
        // – ovo znači da unutar zagrada možeš imati kompletan izraz.
        // Rešenje za ugnježdene izraze.
        if (match(TokenType.LEFT_PAREN)){
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        return primary();
    }

    //slicno kao match, proverava da li je sledeci token ocekivan tip
    //ako jeste, nastavljamo dalje, ako ne, prijavljujemo gresku
    private Token consume(TokenType type, String message){
        if(check(type)) return advance();
        throw error(peek(), message);
    }

}