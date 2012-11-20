package wci.frontend.triangle;

import wci.frontend.*;
import wci.frontend.triangle.tokens.*;

import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.triangle.TriangleErrorCode.*;

/**
 * <h1>PascalScanner</h1>
 *
 * <p>The Pascal scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TriangleScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public TriangleScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next Pascal token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
    	int position = source.getPosition();
    	
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new TriangleEofToken(source,position);
        }
        else if (Character.isLetter(currentChar)) {
            token = new TriangleWordToken(source);
        }
        else if (Character.isDigit(currentChar)) {
            token = new TriangleNumberToken(source);
        }
        else if (currentChar == '\'') {
            token = new TriangleCharacterToken(source);
        }
        else if (TriangleTokenType.SYMBOL_TOKEN
                 .containsKey(Character.toString(currentChar))) {
            token = new TriangleSpecialSymbolToken(source);
        }
        else if (TriangleTokenType.OPERATOR_CHARACTERS
        		.containsKey(Character.toString(currentChar))){
        	token = new TriangleOperatorToken(source);
        }
        else {
            token = new TriangleErrorToken(source, INVALID_CHARACTER,
                                         Character.toString(currentChar));
            nextChar();  // consume character
        }

        return token;
    }

    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();

        while (Character.isWhitespace(currentChar) || (currentChar == '!')) {

            // Start of a comment?
            if (currentChar == '!') {
                do {
                    currentChar = nextChar();  // consume comment characters
                } while (currentChar != EOL);

                // Found closing '}'?
                if (currentChar == EOL) {
                    currentChar = nextChar();  // consume the '}'
                }
            }

            // Not a comment.
            else {
                currentChar = nextChar();  // consume whitespace character
            }
        }
    }
}
