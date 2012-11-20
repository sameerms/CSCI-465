package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>FormalParameterSequenceParser</h1>
 *
 * <p>Parse a Triangle Formal-Parameter-Sequence.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class FormalParameterSequenceParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER, VAR, PROC, FUNC, RIGHT_PAREN);
    	FOLLOW_SET = EnumSet.of(RIGHT_PAREN, END_OF_FILE);
    	FIRST_FOLLOW_SET = FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public FormalParameterSequenceParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Formal-Parameter-Sequence.
     * 
     * Formal-Parameter-Sequence ::= 
     *                               | proper-Formal-Parameter-Sequence
     *                               
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return The list of symbol table entries of the parameters.
     * @throws Exception if an error occurred.
     */
    public ArrayList<SymTabEntry> parse(Token token) throws Exception {
    	ArrayList<SymTabEntry> FormalParameterSequenceList = new ArrayList<SymTabEntry>();
        token = currentToken();
        
        if (!FOLLOW_SET.contains((TriangleTokenType)token.getType())){
        	ProperFormalParameterSequenceParser properFormalParameterSequence = new ProperFormalParameterSequenceParser(this);
        	FormalParameterSequenceList = properFormalParameterSequence.parse(token);
        }
        
        return FormalParameterSequenceList;
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
