package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.typeimpl.TypeChecker;

import java.util.ArrayList;
import java.util.EnumSet;

import java.util.Iterator;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.PARAMETERS;

/**
 * <h1>ProperActualParameterSequenceParser</h1>
 *
 * <p>Parse a Triangle Proper-Actual-Parameter-Sequence.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ProperActualParameterSequenceParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR,LEFT_PAREN,LEFT_BRACE,LEFT_BRACKET,LET, 
    			TriangleTokenType.IF, VAR, PROC, FUNC);
    	FOLLOW_SET = EnumSet.of(RIGHT_PAREN);
    	FIRST_FOLLOW_SET = FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public ProperActualParameterSequenceParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Proper-Actual-Parameter-Sequence.
     * 
     * Proper-Actual-Parameter-Sequence ::= Actual-Parameter | Actual-Parameter , Proper-Actual-Parameter
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, ArrayList<SymTabEntry> formalParameterSeq) throws Exception {
        ICodeNode properActualParameterSequenceNode = ICodeFactory.createICodeNode(PARAMETERS);
        TriangleTokenType type = null;
        int index = 0;
        do{
        	token = currentToken();
        	SymTabEntry formalParam = formalParameterSeq != null? formalParameterSeq.get(index) : null;
        	index++;
        	ActualParameterParser actualParameter = new ActualParameterParser(this);
        	ICodeNode paramNode = actualParameter.parse(token, formalParam);
        	properActualParameterSequenceNode.addChild(paramNode);
        	token = currentToken();
        	if ((type = (TriangleTokenType)token.getType()) == COMMA){
        		token = nextToken(); // absorb comma
        	}
        }while(type == COMMA);
        
        return properActualParameterSequenceNode;
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
