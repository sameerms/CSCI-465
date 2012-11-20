package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

/**
 * <h1>CommandParser</h1>
 *
 * <p>Parse a Triangle command statement.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CommandParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER,BEGIN,TriangleTokenType.IF,LET,WHILE);
    	FOLLOW_SET = EnumSet.of(SEMICOLON, END, END_OF_FILE);
    	FIRST_FOLLOW_SET = FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public CommandParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle command.
     * 
     * Command ::= Single-Command (;Single-Command)*
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
        ICodeNode commandNode = null;
        token = currentToken();
        commandNode = ICodeFactory.createICodeNode(COMPOUND);
        setLineNumber(commandNode,token);
        // Parse a single-command
    	SingleCommandParser singleCommand = new SingleCommandParser(this);
        commandNode.addChild(singleCommand.parse(token));
        token = currentToken();
        // implement (; single-command)*
        while((TriangleTokenType)token.getType() == SEMICOLON){
        	token = nextToken(); //skip semicolon
        	singleCommand = new SingleCommandParser(this);
        	commandNode.addChild(singleCommand.parse(token));
        	token = currentToken();
        }
        
        // Set the current line number as an attribute.
        //setLineNumber(statementNode, token);
        
        return commandNode;
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
