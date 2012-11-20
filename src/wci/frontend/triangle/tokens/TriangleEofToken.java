package wci.frontend.triangle.tokens;

import wci.frontend.EofToken;
import wci.frontend.Source;
import static wci.frontend.triangle.TriangleTokenType.END_OF_FILE;

public class TriangleEofToken extends EofToken {
	
	/**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public TriangleEofToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @param position the position in the line where the EOF occurred. 
     * @throws Exception if an error occurred.
     */
    public TriangleEofToken(Source source, int position)
            throws Exception
        {
            super(source);
            this.position = position;
        }
    /**
     * Extract a Pascal number token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
    	type = END_OF_FILE;
    }
}
