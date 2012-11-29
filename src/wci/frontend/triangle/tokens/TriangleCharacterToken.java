package wci.frontend.triangle.tokens;

import wci.frontend.*;
import wci.frontend.triangle.*;

import static wci.frontend.Source.EOL;
import static wci.frontend.Source.EOF;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.*;

/**
 * <h1>PascalStringToken</h1>
 *
 * <p> Pascal string tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TriangleCharacterToken extends TriangleToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public TriangleCharacterToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal string token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {

        char currentChar = nextChar();  // consume initial quote
        text = "'"+currentChar;
        value = new Character(currentChar);
        currentChar = nextChar();
        if(currentChar != '\''){
        	type = ERROR;
        	value = INVALID_CHARACTER;
        } else {
        	type = CHAR;
        }
        currentChar = nextChar();
    }
    private boolean isSpecialSymbol(char currentChar) {
        for (TriangleTokenType token : SYMBOL_TOKEN.values()) {
        	if (token.getText().contains(Character.toString(currentChar))) {
    			return true;
        	}
        }
        return false;
    }
}
