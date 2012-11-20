package wci.frontend;

/**
 * <h1>Scanner</h1>
 * 
 * <p>
 * A language-independent framework class. This abstract scanner class will be
 * implemented by language-specific subclasses.
 * </p>
 * 
 * <p>
 * Copyright (c) 2009 by Ronald Mak
 * </p>
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public abstract class Scanner {
	protected Source source; // source
	private Token currentToken; // current token
	private Token nextToken; // nextg token

	/**
	 * Constructor
	 * 
	 * @param source
	 *            the source to be used with this scanner.
	 */
	public Scanner(Source source) {
		nextToken = null;
		this.source = source;
	}

	/**
	 * @return the current token.
	 */
	public Token currentToken() {
		return currentToken;
	}

	/**
	 * Return next token from the source.
	 * 
	 * @return the next token.
	 * @throws Exception  if an error occurred.
	 */
	public Token nextToken() throws Exception {
		if (nextToken == null) {
			currentToken = extractToken();
		} else {
			currentToken = nextToken;
			nextToken = null;
		}
		return currentToken;
	}

	/**
	 * Return next token from the source. Unlike nextToken(), this method
	 * does not advance to the next token.
	 * 
	 * @return the next token.
	 * @throws Exception  if an error occurred.
	 */
	public Token peekToken() throws Exception {
		if (nextToken == null) {
			nextToken = extractToken();
		} 
		
		return nextToken;
	}
	
	/**
	 * Do the actual work of extracting and returning the next token from the
	 * source. Implemented by scanner subclasses.
	 * 
	 * @return the next token.
	 * @throws Exception
	 *             if an error occurred.
	 */
	protected abstract Token extractToken() throws Exception;

	/**
	 * Call the source's currentChar() method.
	 * 
	 * @return the current character from the source.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public char currentChar() throws Exception {
		return source.currentChar();
	}

	/**
	 * Call the source's nextChar() method.
	 * 
	 * @return the next character from the source.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public char nextChar() throws Exception {
		return source.nextChar();
	}
}
