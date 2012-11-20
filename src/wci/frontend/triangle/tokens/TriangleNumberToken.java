package wci.frontend.triangle.tokens;

import wci.frontend.*;
import wci.frontend.triangle.*;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.*;

/**
 * <h1>PascalNumberToken</h1>
 *
 * <p>Pascal number tokens (integer and real).</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TriangleNumberToken extends TriangleToken
{
    private static final int MAX_EXPONENT = 37;

    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public TriangleNumberToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal number token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();  // token's characters
        
        // ############## ADDED ##############
        //extractNumber(textBuffer);
        extractWholeNumber(textBuffer);
        text = textBuffer.toString();
    }

    // ############## ADDED ##############
    protected void extractWholeNumber(StringBuilder textBuffer) throws Exception {
    	char currentChar = currentChar();
    	type = INTEGER;
    	
    	do {
    		textBuffer.append(currentChar);
    		currentChar = nextChar();
    	} while (Character.isDigit(currentChar));
        value = new Integer(computeIntegerValue(textBuffer.toString()));    		
    }
    // ############## ##############
    
    /**
     * Compute and return the integer value of a string of digits.
     * Check for overflow.
     * @param digits the string of digits.
     * @return the integer value.
     */
    private int computeIntegerValue(String digits)
    {
        // Return 0 if no digits.
        if (digits == null) {
            return 0;
        }

        int integerValue = 0;
        int prevValue = -1;    // overflow occurred if prevValue > integerValue
        int index = 0;

        // Loop over the digits to compute the integer value
        // as long as there is no overflow.
        while ((index < digits.length()) && (integerValue >= prevValue)) {
            prevValue = integerValue;
            integerValue = 10*integerValue +
                           Character.getNumericValue(digits.charAt(index++));
        }

        // No overflow:  Return the integer value.
        if (integerValue >= prevValue) {
            return integerValue;
        }

        // Overflow:  Set the integer out of range error.
        else {
            type = ERROR;
            value = RANGE_INTEGER;
            return 0;
        }
    }

}
