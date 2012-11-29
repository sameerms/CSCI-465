package wci.frontend.triangle;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.TriangleErrorHandler;
import wci.frontend.triangle.parsers.ProgramParser;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.message.Message;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_PERIOD;
import static wci.frontend.triangle.TriangleErrorCode.IO_ERROR;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;
import static wci.message.MessageType.*;

/**
 * <h1>TriangleParserTD</h1>
 * 
 * <p>
 * The top-down Pascal parser.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class TriangleParserTD extends Parser {
	
	protected static TriangleErrorHandler errorHandler = new TriangleErrorHandler();

	private SymTabEntry routineId;  // name of the routine being parsed
	
	/**
	 * Constructor.
	 * 
	 * @param scanner
	 *            the scanner to be used with this parser.
	 */
	public TriangleParserTD(Scanner scanner) {
		super(scanner);
	}

	/**
	 * Constructor.
	 * 
	 * @param parser
	 *            a recursive descent parser.
	 */
	public TriangleParserTD(TriangleParserTD parser) {
		super(parser.scanner);
	}

	/**
     * Getter.
     * @return the routine identifier's symbol table entry.
     */
    public SymTabEntry getRoutineId()
    {
        return routineId;
    }

    /**
     * Getter.
     * @return the error handler.
     */
    public TriangleErrorHandler getErrorHandler()
    {
        return errorHandler;
    }
    
	/**
	 * Parse a Pascal source program and generate the symbol table and the
	 * intermediate code.
	 */
	public void parse() throws Exception {
		Token token;
		long startTime = System.currentTimeMillis();

		ICode iCode = ICodeFactory.createICode();
        TrianglePredefined.initialize(symTabStack);

        // Create a dummy program identifier symbol table entry.
        routineId = symTabStack.enterLocal("0Main Program".toLowerCase());
        routineId.setDefinition(DefinitionImpl.PROGRAM);
        symTabStack.setProgramId(routineId);

        // Push a new symbol table onto the symbol table stack and set
        // the routine's symbol table and intermediate code.
        routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
        routineId.setAttribute(ROUTINE_ICODE, iCode);
        
        ProgramParser programParser = new ProgramParser(this);
        
        try {
            token = nextToken();

            // Parse the program
            ICodeNode rootNode = programParser.parse(token);
            iCode.setRoot(rootNode);
            symTabStack.pop();

            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
	}

	/**
	 * Return the number of syntax errors found by the parser.
	 * 
	 * @return the error count.
	 */
	public int getErrorCount() {
		return errorHandler.getErrorCount();
	}

	protected Token synchronize(EnumSet<TriangleTokenType> syncSet) throws Exception {
		Token token = currentToken();

		while (!(token instanceof EofToken)
				&& !syncSet.contains((TriangleTokenType) token.getType())) {
			token = nextToken();
		}

		return token;
	}

	protected Token synchronize(TriangleTokenType tokenType,
			TriangleErrorCode errorCode, EnumSet<TriangleTokenType> syncSet)
			throws Exception {
		Token token = currentToken();

		if ((TriangleTokenType) token.getType() != tokenType) {
			errorHandler.flag(token, errorCode, this);
			token = synchronize(syncSet);
		} else {
			token = nextToken();
		}

		return token;
	}
}
