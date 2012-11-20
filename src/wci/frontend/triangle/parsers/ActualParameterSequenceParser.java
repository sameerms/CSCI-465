package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>ActualParameterSequence</h1>
 *
 * <p>Parse a Triangle Actual Parameter Sequence.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ActualParameterSequenceParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR,LEFT_PAREN,LEFT_BRACE,LEFT_BRACKET,LET, 
    			TriangleTokenType.IF, VAR, PROC, FUNC, RIGHT_PAREN);
    	FOLLOW_SET = EnumSet.of(RIGHT_PAREN, END_OF_FILE);
    	FIRST_FOLLOW_SET = FIRST_SET.clone();
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public ActualParameterSequenceParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Actual-Parameter-Sequence.
     * 
     * Actual-Parameter-Sequence ::=   | Proper-Actual-Parameter-Sequence
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token,ArrayList<SymTabEntry> formalParameterSeq) throws Exception {
    	ICodeNode actualParameterSequenceNode = null;
    	
        token = currentToken();
        TriangleTokenType type = (TriangleTokenType)token.getType();
        
        if (!FOLLOW_SET.contains(type)){
        	ProperActualParameterSequenceParser properActualParameterSequence = new ProperActualParameterSequenceParser(this);
            actualParameterSequenceNode = properActualParameterSequence.parse(token, formalParameterSeq);	
        }
        
        return actualParameterSequenceNode;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token)
    {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    }
