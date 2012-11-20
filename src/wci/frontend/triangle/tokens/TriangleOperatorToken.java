package wci.frontend.triangle.tokens;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleToken;
import wci.frontend.triangle.TriangleTokenType;

import static wci.frontend.triangle.TriangleErrorCode.INVALID_CHARACTER;
import static wci.frontend.triangle.TriangleTokenType.*;


public class TriangleOperatorToken  extends TriangleToken {
	/**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public TriangleOperatorToken(Source source) throws Exception
    {
        super(source);
    }

    /**
     * Extract a Triangle special symbol token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract() throws Exception
    {
        char currentChar = currentChar();

        text = Character.toString(currentChar);
        type = OPERATOR;
        currentChar = nextChar();
        while(TriangleTokenType.OPERATOR_CHARACTERS
        		.containsKey(Character.toString(currentChar))){
        	text += currentChar();
        	currentChar = nextChar();
        }        
    }

}
