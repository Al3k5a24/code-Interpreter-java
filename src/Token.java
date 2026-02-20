//dodato kako bi kasnije, u slucaju pojave greske, mogli prikazati korisniku na kojoj liniji se dogodila greska
public class Token {
    final TokenType type;

    //tekst koji cini jedan token, jer lexer cita karakter po karekter
    final String lexeme;

    //arsirana runtime vrednost tog tokena
    final Object literal;

    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
