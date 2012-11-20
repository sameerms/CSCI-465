package wci.intermediate.icodeimpl;

import wci.intermediate.ICodeNodeType;

/**
 * <h1>ICodeNodeType</h1>
 *
 * <p>Node types of the intermediate code parse tree.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum ICodeNodeTypeImpl implements ICodeNodeType
{
    // Program structure
    PROGRAM, PROCEDURE, FUNCTION, BLOCK,

    // Statements
    COMPOUND, ASSIGN, LOOP, TEST, CALL, PARAMETERS, IF,
    IF_STATEMENT, SELECT, SELECT_BRANCH, SELECT_CONSTANTS, NO_OP,
    
    //Expressions
    IF_EXPRESSION,
    
    // Relational operators
    EQ, NE, LT, LE, GT, GE, NOT,

    // Additive operators
    ADD, SUBTRACT, OR, NEGATE,

    // Multiplicative operators
    MULTIPLY, INTEGER_DIVIDE, FLOAT_DIVIDE, MOD, AND,

    // Operands
    VARIABLE, SUBSCRIPTS, FIELD, CHAR_CONSTANT,
    INTEGER_CONSTANT, REAL_CONSTANT,
    STRING_CONSTANT, BOOLEAN_CONSTANT,
    ARRAY_CONSTANT, RECORD_CONSTANT,

    // Parameters
    WRITE_PARM, PROC_PARM, FUNC_PARM, VAR_PARM, VALUE_PARM
}
