package wci.frontend.triangle;

import java.util.Hashtable;
import java.util.HashSet;

import wci.frontend.TokenType;

/**
 * <h1>PascalTokenType</h1>
 * 
 * <p>
 * Pascal token types.
 * </p>
 * 
 * <p>
 * Copyright (c) 2009 by Ronald Mak
 * </p>
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public enum TriangleTokenType implements TokenType {
	// Reserved words.
	// REMOVED -- AND, CASE, DIV, DOWNTO, FILE, FOR, GOTO, LABEL,
	// REMOVED -- MOD, NIL, NOT, OR, PACKED, PROGRAM, REPEAT, SET, UNTIL, TO,
	// WITH
	// CHANGED -- FUNCTION, PROCEDURE
	// ADDED -- LET, TRUE, FALSE, CHAR, INTEGER, BOOLEAN
	// edited by Jaron :)
	ARRAY, BEGIN, CONST, DO, ELSE, END, FUNC, IF, IN, LET, OF, PROC, RECORD, 
	THEN, TYPE, VAR, WHILE,

	// Symbol tokens
	DOT("."), COLON(":"), SEMICOLON(";"), COMMA(","), COLON_EQUALS(":="), 
	TILDE("~"), LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACKET("["), 
	RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),

	// Op-characters
	PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), EQUALS("="), 
	LESS_THAN("<"), GREATER_THAN(">"), BACK_SLASH("\\"), AMPERSAND("&"), 
	AT_SYMBOL("@"), PERCENT("%"), QUESTION_MARK("?"), CIRCUMFLEX("^"),

	INTEGER, IDENTIFIER, BOOLEAN, CHAR, OPERATOR, // should these be part of
													// reserved ??
	ERROR, END_OF_FILE;

	private static final int FIRST_RESERVED_INDEX = ARRAY.ordinal();
	private static final int LAST_RESERVED_INDEX = WHILE.ordinal();

	private static final int FIRST_SYMBOL_INDEX = DOT.ordinal();
	private static final int LAST_SYMBOL_INDEX = RIGHT_BRACE.ordinal();

	private static final int FIRST_OPERATOR_INDEX = PLUS.ordinal();
	private static final int LAST_OPERATOR_INDEX = CIRCUMFLEX.ordinal();

	private String text; // token text

	/**
	 * Constructor.
	 */
	TriangleTokenType() {
		this.text = this.toString().toLowerCase();
	}

	/**
	 * Constructor.
	 * 
	 * @param text
	 *            the token text.
	 */
	TriangleTokenType(String text) {
		this.text = text;
	}

	/**
	 * Getter.
	 * 
	 * @return the token text.
	 */
	public String getText() {
		return text;
	}

	// Set of lower-cased Pascal reserved word text strings.
	public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
	static {
		TriangleTokenType values[] = TriangleTokenType.values();
		for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
			RESERVED_WORDS.add(values[i].getText().toLowerCase());
		}
	}

	public static Hashtable<String, TriangleTokenType> SYMBOL_TOKEN = new Hashtable<String, TriangleTokenType>();
	static {
		TriangleTokenType values[] = TriangleTokenType.values();
		for (int i = FIRST_SYMBOL_INDEX; i <= LAST_SYMBOL_INDEX; ++i) {
			SYMBOL_TOKEN.put(values[i].getText(), values[i]);
		}
	}

	// Hash table of Pascal special symbols. Each special symbol's text
	// is the key to its Pascal token type.
	public static Hashtable<String, TriangleTokenType> OPERATOR_CHARACTERS = new Hashtable<String, TriangleTokenType>();
	static {
		TriangleTokenType values[] = TriangleTokenType.values();
		for (int i = FIRST_OPERATOR_INDEX; i <= LAST_OPERATOR_INDEX; ++i) {
			OPERATOR_CHARACTERS.put(values[i].getText(), values[i]);
		}
	}
}