package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_PARMS;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;
import static wci.frontend.triangle.TriangleErrorCode.IDENTIFIER_REDEFINED;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_FORMAL_PARAMETER;

/**
 * <h1>FormalParameterParser</h1>
 * 
 * <p>
 * Parse a Triangle Formal-Parameter.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class FormalParameterParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static final EnumSet<TriangleTokenType> IDENTIFIER_FOLLOW_SET;
	static final EnumSet<TriangleTokenType> PROC_IDENTIFIER_SET;
	static final EnumSet<TriangleTokenType> FUNC_IDENTIFIER_SET;
	static final EnumSet<TriangleTokenType> FUNC_RIGHT_PAREN_SET;
	static {
		FIRST_SET = EnumSet.of(IDENTIFIER, VAR, PROC, FUNC);
		FOLLOW_SET = EnumSet.of(COMMA, RIGHT_PAREN);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
		IDENTIFIER_FOLLOW_SET = EnumSet.of(COLON, IDENTIFIER, ARRAY, RECORD,
				COMMA, RIGHT_PAREN, TILDE, END, SEMICOLON, IN);
		PROC_IDENTIFIER_SET = EnumSet.of(LEFT_PAREN, IDENTIFIER, PROC, VAR,
				FUNC, RIGHT_PAREN);
		FUNC_IDENTIFIER_SET = PROC_IDENTIFIER_SET.clone();
		FUNC_IDENTIFIER_SET.add(COLON);
		FUNC_IDENTIFIER_SET.add(ARRAY);
		FUNC_IDENTIFIER_SET.add(RECORD);
		FUNC_RIGHT_PAREN_SET = EnumSet.of(COLON, IDENTIFIER, ARRAY, RECORD);
		FUNC_RIGHT_PAREN_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public FormalParameterParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Formal-Parameter.
	 * 
	 * Formal-Parameter ::= identifier : Type-Denoter | var Identifier :
	 * Type-Denoter | proc Identifier(Formal-Parameter-Sequence) | func
	 * Identifier(Formal-Parameter-Sequence) : Type-Denoter
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return the symbol table entry of this formal parameter.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public SymTabEntry parse(Token token) throws Exception {
		SymTabEntry formalParameterTableEntry = null;

		TypeDenoterParser typeDenoterParser;
		FormalParameterSequenceParser formalParameterSequenceParser;
		Token identifierToken = null;
		
		token = currentToken();
		TriangleTokenType tokenType = (TriangleTokenType) token.getType();
		
		switch (tokenType) {
		case IDENTIFIER:
			identifierToken = currentToken();
			token = nextToken(); // absorb the Identifier
			token = synchronize(COLON, TriangleErrorCode.MISSING_COLON,
					TypeDenoterParser.FIRST_FOLLOW_SET);
			typeDenoterParser = new TypeDenoterParser(this);
			TypeSpec valueParamType = typeDenoterParser.parse(token);
			SymTabEntry valueParamId = symTabStack.lookup(identifierToken.getText().toLowerCase());
			if (valueParamId == null){
				valueParamId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
				valueParamId.appendLineNumber(identifierToken.getLineNumber());
				valueParamId.setDefinition(DefinitionImpl.VALUE_PARM);
				valueParamId.setTypeSpec(valueParamType);
				valueParamType.setIdentifier(valueParamId);
			} else {
				errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
			}
			formalParameterTableEntry = valueParamId;
			break;
		case VAR:
			identifierToken = nextToken(); // absorb the Var
			token = synchronize(IDENTIFIER,
					TriangleErrorCode.MISSING_IDENTIFIER, IDENTIFIER_FOLLOW_SET);
			token = synchronize(COLON, TriangleErrorCode.MISSING_COLON,
					TypeDenoterParser.FIRST_FOLLOW_SET);
			typeDenoterParser = new TypeDenoterParser(this);
			TypeSpec varParamType = typeDenoterParser.parse(token);
			SymTabEntry varParamId = symTabStack.lookup(identifierToken.getText().toLowerCase());
			if (varParamId == null){
				varParamId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
				varParamId.appendLineNumber(identifierToken.getLineNumber());
				varParamId.setDefinition(DefinitionImpl.VAR_PARM);
				varParamId.setTypeSpec(varParamType);
				varParamType.setIdentifier(varParamId);
			} else {
				errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
			}
			formalParameterTableEntry = varParamId;
			break;
		case PROC:
			identifierToken = nextToken(); // absorb proc
			token = synchronize(IDENTIFIER,
					TriangleErrorCode.MISSING_IDENTIFIER, PROC_IDENTIFIER_SET);
			token = synchronize(LEFT_PAREN,
					TriangleErrorCode.MISSING_LEFT_PAREN, PROC_IDENTIFIER_SET);
			symTabStack.push();
			formalParameterSequenceParser = new FormalParameterSequenceParser(
					this);
			ArrayList<SymTabEntry> procParamList = formalParameterSequenceParser.parse(token);
			symTabStack.pop(); // no need to keep the symbol table for a proc param
			token = synchronize(RIGHT_PAREN,
					TriangleErrorCode.MISSING_RIGHT_PAREN, FIRST_FOLLOW_SET);
			SymTabEntry procId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (procId == null) {
            	procId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	procId.setDefinition(DefinitionImpl.PROC_PARM);
            	procId.appendLineNumber(identifierToken.getLineNumber());
            	procId.setTypeSpec(TrianglePredefined.undefinedType);
            	procId.setAttribute(ROUTINE_PARMS, procParamList);
            }
            else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
            }
			formalParameterTableEntry = procId;
			break;
		case FUNC:
			identifierToken = nextToken(); // absorb func
			token = synchronize(IDENTIFIER,
					TriangleErrorCode.MISSING_IDENTIFIER, FUNC_IDENTIFIER_SET);
			token = synchronize(LEFT_PAREN,
					TriangleErrorCode.MISSING_LEFT_PAREN, FUNC_IDENTIFIER_SET);
			symTabStack.push();
			formalParameterSequenceParser = new FormalParameterSequenceParser(
					this);
			ArrayList<SymTabEntry> funcParamList = formalParameterSequenceParser.parse(token);
			symTabStack.pop(); // no need to keep the symbol table for func param
			token = synchronize(RIGHT_PAREN,
					TriangleErrorCode.MISSING_RIGHT_PAREN, FUNC_RIGHT_PAREN_SET);

			token = synchronize(COLON, TriangleErrorCode.MISSING_COLON,
					FUNC_RIGHT_PAREN_SET);
			typeDenoterParser = new TypeDenoterParser(this);
			TypeSpec funcType = typeDenoterParser.parse(token);
			SymTabEntry funcId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (funcId == null) {
            	funcId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	funcId.setDefinition(DefinitionImpl.FUNC_PARM);
            	funcId.appendLineNumber(identifierToken.getLineNumber());
            	funcId.setTypeSpec(funcType);
            	funcId.setAttribute(ROUTINE_PARMS, funcParamList);
            }
            else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
            }
			formalParameterTableEntry = funcId;
			break;
		default:
			errorHandler.flag(token, MISSING_FORMAL_PARAMETER, this);
			break;
		}

		return formalParameterTableEntry;
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
