import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    //pocetak nekog trenutnog tokena
    private int start = 0;

    //pokazivac na sledece karaktere
    private int current = 0;

    //linija na kom se nalazi current karakter
    private int line = 1;

    Scanner(String source){
        this.source=source;
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }

    List<Token> scanTokens(){
        while(!isAtEnd()){
            start=current;
            scanToken();
        }

        //kada dodje do kraja karaktera, tada se dolazi do EOF(end of line)
        //nije preko potrebno, vise estetski
        tokens.add(new Token(TokenType.EOF,"",null,line));
        return tokens;
    }

    // za tokene koji imaju literal vrednost(mora da se doradi)
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type,text,literal,line));
    }

    // metode za skeniranje tokena koji nemaju literal(runtime) vrednost
    private void addToken(TokenType type) {
        addToken(type,null);
    }

    private void scanToken(){
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;

            //edge cases u slucajevima da li se navedeni karakteri nalaze jedno do drugog ili su odvojeni razmakom
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);

            case ' ':
            case '\r':
            case '\t':
                // ignorisanje belih znakova.
                break;

            //nov red
            case '\n':
                line++;
                break;


            case '/':
                    if(match('/')){
                        //dok se ne dodje do kraja linije ili fajla, preskaci karaktere
                     while(peek() != '\n' && !isAtEnd()){
                         advance();
                     }
                     // funkcionalnost /*...*/ (rucno implementirano)
                    }else if(match('*')){
                        while(!(peek() == '*' && peekNext() =='/') && !isAtEnd()){
                            advance();
                        }

                        // citamo "*/"
                        if (!isAtEnd()) {
                            advance(); // *
                            advance(); // /
                        }
                    }
                    else{
                        addToken(TokenType.SLASH);
                    } break;


            //u slucaju da korisnik u source file ubaci karaktere koje Lox ne koristi
            // na primer @ # ^S
            default:
            if (isDigit(c)) {
                number();
            }
            else if (isAlpha(c)) {
            identifier();
            }
            else {
                Lox.error(line, "Unexpected character.");
            }
    }

}
    // gleda "sledeci"(trejutni) karakter i ne pomera se, na kraju fajla  vraca null karakter kako bi izbegli exception
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //funkcija za dodatnu proveru da li se neki karakter koji trazimo poklapa sa onim koji je na redu dok se radi skeniranje
    //dodatna funkcionalnost je trazenje kombinacija nekih karaktera, na primer za komentar, vece jednako, ...
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }

//putem ove metode prolazimo kroz niz karaktera
private char advance() {
    return source.charAt(current++);

    //II nacin za implementaciju:
    //char c = source.chatAt(current);
    //current++;
    //return c;
}

//proveravamo da li je karakter cifra
private boolean isDigit(char c){
        return c>='0' && c<='9';
}

//tokenizacija broja
private void number(){
        while(isDigit(peek())){
            advance();
        }

        if(peek()=='.' && isDigit(peekNext())){
            advance();

            while(isDigit(peek())){
                advance();
            }
        }

        addToken(TokenType.NUMBER,
                Double.parseDouble(source.substring(start, current)));
}
    //ista funkcionalnost kao peek, samo da se zadrzi na sledecem karakteru
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //proveravamo da li su rezervisane reci
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }
}

