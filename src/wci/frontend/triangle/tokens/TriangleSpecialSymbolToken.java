package wci.frontend.triangle.tokens;

import wci.frontend.*;
import wci.frontend.triangle.*;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.*;

/**
 * <h1>PascalSpecialSymbolToken</h1>
 * 
 * <p>
 * Pascal special symbol tokens.
 * </p>
 * 
 * <p>
 * Copyright (c) 2009 by Ronald Mak
 * </p>
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class TriangleSpecialSymbolToken extends TriangleToken {
	/**
	 * Constructor.
	 * 
	 * @param source
	 *            the source from where to fetch the token's characters.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public TriangleSpecialSymbolToken(Source source) throws Exception {
		super(source);
	}

	/**
	 * Extract a Triangle special symbol token from the source.
	 * 
	 * @throws Exception
	 *             if an error occurred.
	 */
	protected void extract() throws Exception {
		char currentChar = currentChar();

		text = Character.toString(currentChar);

		if (currentChar == ':' && peekChar() == '=') {
			text += nextChar();
		}
		currentChar = nextChar();
		type = TriangleTokenType.SYMBOL_TOKEN.get(text);
	}
}