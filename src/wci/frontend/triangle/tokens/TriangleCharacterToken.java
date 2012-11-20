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
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        //note by Jaron :) -- The above statement does not consume a quote, it consumes the value after a quote
        //note by Jaron :) -- The TriangleScanner.extractToken() consumes it. also refer to line 55 in the TriangleScanner class.
        textBuffer.append('\'');

        /* ############## REMOVED ##############
        // Get string characters.
        do {
            // Replace any whitespace character with a blank.
            if (Character.isWhitespace(currentChar)) {
                currentChar = ' ';
            }

            if ((currentChar != '\'') && (currentChar != EOF)) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }

            // Quote?  Each pair of adjacent quotes represents a single-quote.
            if (currentChar == '\'') {
                while ((currentChar == '\'') && (peekChar() == '\'')) {
                    textBuffer.append("''");
                    valueBuffer.append(currentChar); // append single-quote
                    currentChar = nextChar();        // consume pair of quotes
                    currentChar = nextChar();
                }
            }
        } while ((currentChar != '\'') && (currentChar != EOF));
		############## ############## */
        
        // ############## This should work, no clue why it doesn't.
        /*
        System.out.println(Character.toString(currentChar)+ " | "+SPECIAL_SYMBOLS.values().contains(Character.toString(currentChar)));
         */
        
        // ############## ADDED ##############
        if (Character.isWhitespace(currentChar)) {//note by Jaron--This also includes tabs aka \t !!fix later!! should differentiate between space and tab
    		currentChar = ' ';
        	textBuffer.append(currentChar);
        	valueBuffer.append(currentChar);
        	//edit by Jaron :)
        	currentChar = nextChar();  // consume final quote
        	//---end of edit---
        }
        else if (isSpecialSymbol(currentChar) || Character.isLetterOrDigit(currentChar)) {
        	textBuffer.append(currentChar);
        	valueBuffer.append(currentChar);
        	//edit by Jaron :)
        	currentChar = nextChar();  // consume final quote
        	//---end of edit---
        } else {
        	type = ERROR;
        	value = INVALID_CHARACTER;
        }
        //edit by Jaron :) quick fix!! make better later 
        if(currentChar == '\''){
        	textBuffer.append('\'');

            type = CHAR;
            value = valueBuffer.toString();
            text = textBuffer.toString();
            
            nextChar();
            return;
        }
        //---end of edit---
        while (currentChar != '\'') {
    		currentChar = nextChar();

    		type = ERROR;
	        value = INVALID_STATEMENT;
    	}
        // ############## ##############
        if (currentChar == '\'') {
            nextChar();  // consume final quote
            if (type != ERROR) {
                textBuffer.append('\'');

                type = CHAR;
                value = valueBuffer.toString();
            }
        }
        else {
            type = ERROR;
            // ############## REMOVED ##############
            // value = UNEXPECTED_EOF;
            // ############## ADDED ##############
            value = INVALID_STATEMENT;
        }

        text = textBuffer.toString();
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
