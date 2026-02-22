import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    //pocetak nekog trenutnog tokena
    private int start = 0;

    //trenutni karakter
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
            case "!": addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            case ">": addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.EQUAL);
            case "<" addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case "==" addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);

            //u slucaju da korisnik u source file ubaci karaktere koje Lox ne koristi
            // na primer @ # ^S
            default:
                Lox.error(line, "Unexpected character");
                break;
    }

}

    //funkcija za dodatnu proveru da li se neki karakter koji trazimo poklapa sa onim koji je na redu dok se radi skeniranje
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return fales;

        current++;
        return true;
    }

//putem ove metode prolazimo kroz niz karaktera
private char advance() {
    return source.charAt(current++);
}

}
