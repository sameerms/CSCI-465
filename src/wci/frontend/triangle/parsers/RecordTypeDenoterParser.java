package wci.frontend.triangle.parsers;


import java.util.ArrayList;
import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;

import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.frontend.triangle.TriangleErrorCode.IDENTIFIER_REDEFINED;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_IDENTIFIER;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_COLON;

/**
 * <h1>RecordTypeDenoterParser</h1>
 * 
 * <p>
 * Parse a Triangle Record-Type-Denoter statement.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class RecordTypeDenoterParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static final EnumSet<TriangleTokenType> IDENTIFIER_SET;
	static final EnumSet<TriangleTokenType> COLON_SET;
	static {
		FIRST_SET = EnumSet.of(IDENTIFIER);
		FOLLOW_SET = EnumSet.of(END);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
		IDENTIFIER_SET = EnumSet.of(COLON, IDENTIFIER, ARRAY, RECORD);
		COLON_SET = EnumSet.of(IDENTIFIER, ARRAY, RECORD, COMMA);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public RecordTypeDenoterParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Record-Type-Denoter.
	 * 
	 * Record-Type-Denoter ::= Identifier : Type-Denoter(, Identifier : Type-Denoter)*
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return A list of SymTabEntry elements giving the order of each record
	 * 			type denoter.  This allows for checking structural equivalence.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public ArrayList<SymTabEntry> parse(Token token) throws Exception {
		TriangleTokenType type = null;
		ArrayList<SymTabEntry> recordTypeDenoterList = new ArrayList<SymTabEntry>();
		
		do {
			Token identifierToken = currentToken();
			token = synchronize(IDENTIFIER, MISSING_IDENTIFIER, IDENTIFIER_SET);
			token = synchronize(COLON, MISSING_COLON, COLON_SET);
			TypeDenoterParser typeDenoter = new TypeDenoterParser(this);
			TypeSpec identifierType = typeDenoter.parse(token);
			// check if identifier is in current symbol table.  if not, then
			// add it to the table.
			String name = identifierToken.getText().toLowerCase();
			SymTabEntry variableId = symTabStack.lookupLocal(name);
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (variableId == null) {
            	variableId = symTabStack.enterLocal(name);
            	variableId.setDefinition(DefinitionImpl.VARIABLE);
            	variableId.appendLineNumber(identifierToken.getLineNumber());
            	variableId.setTypeSpec(identifierType);
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
            // add this record type denoter to array list.
            recordTypeDenoterList.add(variableId);
            
			token = currentToken();
			type = (TriangleTokenType) token.getType();
			if (type == COMMA) {
				token = nextToken();
			}
		} while (type == COMMA);

		return recordTypeDenoterList;
	}

	/**
	 * Set the current line number as a statement node attribute.
	 * 
	 * @param node
	 *            ICodeNode
	 * @param token
	 *            Token
	 */
	protected void setLineNumber(ICodeNode node, Token token) {
		if (node != null) {
			node.setAttribute(LINE, token.getLineNumber());
		}
	}

}
