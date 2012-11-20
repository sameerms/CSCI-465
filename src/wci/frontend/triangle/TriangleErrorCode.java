package wci.frontend.triangle;

/**
 * <h1>PascalErrorCode</h1>
 *
 * <p>Pascal translation error codes.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum TriangleErrorCode
{
    ALREADY_FORWARDED("Already specified in FORWARD"),
    CASE_CONSTANT_REUSED("CASE constant reused"),
    INCOMPATIBLE_ASSIGNMENT("Incompatible assignment"),
    INCOMPATIBLE_TYPES("Incompatible types"),
    INVALID_ASSIGNMENT("Invalid assignment statement"),
    INVALID_CHARACTER("Invalid character"),
    INVALID_CONSTANT("Invalid constant"),
    INVALID_EXPONENT("Invalid exponent"),
    INVALID_EXPRESSION("Invalid expression"),
    INVALID_FIELD("Invalid field"),
    INVALID_FRACTION("Invalid fraction"),
    INVALID_IDENTIFIER_USAGE("Invalid identifier usage"),
    INVALID_NUMBER("Invalid number"),
    INVALID_STATEMENT("Invalid statement"),
    INVALID_SUBRANGE_TYPE("Invalid subrange type"),    INVALID_TARGET("Invalid assignment target"),
    INVALID_VAR_PARM("Invalid VAR parameter"),
    MIN_GT_MAX("Min limit greater than max limit"),
    
    
    //ADDED
    MISSING_LEFT_PAREN("Missing ("),
    MISSING_LEFT_BRACE("Missing {"),
    MISSING_TILDE("Missing ~"),
    MISSING_RIGHT_BRACE("Missing }"),
    MISSING_OPERATOR("Missing Operator"),
    MISSING_TYPE_DEFINITION("Expecting a type or type definition"),
    MISSING_PROC_IDENTIFIER("Missing procedure identifier"),
    MISSING_FUNC_IDENTIFIER("Missing function identifier"),
    MISSING_FORMAL_PARAMETER("Expecting a formal parameter"),
    MISSING_DECLARATION("Missing decleration statement"),
    MISSING_CHARACTER_LITERAL("Missing character literal"),
    MISSING_EXPRESSION("Expecting the beginning of an expression"),
    MISSING_ACTUAL_PARAMETER("Missing actual parameter"),
    MISSING_SINGLE_COMMAND("Missing command statement"),
    INVALID_TYPE("Invalid type"),
    IDENTIFIER_REDEFINED("Redefined identifier"),
    IDENTIFIER_UNDEFINED("Undefined identifier"),
    INVALID_INDEX_TYPE("Invalid index type"),
    NOT_AN_ARRAY("Identifier or field not declared as an array"),
    NOT_A_RECORD("Identifier not declared as a record"),
    ELEMENT_TYPE_MISMATCH("Array constant element type mismatch"),
    PROCEDURE_UNDEFINED("Procedure is undefined"),
    PROC_PARM_MISMATCH("Actual and Formal proc parameters do not match"),
    FUNC_PARM_MISMATCH("Actual and Formal func parameters do not match"),
    FUNCTION_UNDEFINED("Function is undefined"),
    IDENTIFIER_NOT_PROCEDURE("Identifier is not defined as a procedure"),
    IDENTIFIER_NOT_FUNCTION("Identifier is not defined as a function"),
    OPERATOR_NOT_FUNCTION("Operator is not defined as a function"),
    OPERATOR_NOT_DEFINED("Operator not defined"),
    NUMBER_ACTUAL_FORMAL_NO_MATCH("Mismatch in number of actual and formal parameters"),
    PARAMETER_TYPE_MISMATCH("Type mismatch with formal parameter"),
    EXPECTING_VARIABLE_PARAM("Expecting a variable parameter"),
    FORMAL_PARAM_NOT_VAR("Not expecting a Var actual parameter"),
    FORMAL_PARAM_NOT_PROC("Not expecting a Proc actual paramter"),
    FORMAL_PARAM_NOT_FUNC("Not expecting a Func actual paramter"),
    FORMAL_PARAM_NOT_VALUE("Not expecting a Value actual paramter"),
    EXPECTING_BOOLEAN("Expecting a boolean"),
    ElSE_TYPE_MISMATCH("Else expression type does not match then expression type"),
    ASSIGNMENT_NOT_TYPE_COMPATIBLE("Right hand side is not type compatible with left hand side"),
    IDENTIFIER_CONSTANT("Identifier cannot be a constant"),
    //
    MISSING_BEGIN("Missing BEGIN"),
    MISSING_COLON("Missing :"),
    MISSING_COLON_EQUALS("Missing :="),
    MISSING_COMMA("Missing ,"),
    MISSING_CONSTANT("Missing constant"),
    MISSING_DO("Missing DO"),
    MISSING_DOT_DOT("Missing .."),
    MISSING_ELSE("Missing ELSE"),
    MISSING_END("Missing END"),
    MISSING_EQUALS("Missing ="),
    MISSING_FOR_CONTROL("Invalid FOR control variable"),
    MISSING_IDENTIFIER("Missing identifier"),
    MISSING_IN("Missing IN"),    
    MISSING_LET("Missing LET"),
    MISSING_LEFT_BRACKET("Missing ["),
    MISSING_OF("Missing OF"),
    MISSING_PERIOD("Missing ."),
    MISSING_PROGRAM("Missing PROGRAM"),
    MISSING_RIGHT_BRACKET("Missing ]"),
    MISSING_RIGHT_PAREN("Missing )"),
    MISSING_SEMICOLON("Missing ;"),
    MISSING_THEN("Missing THEN"),
    MISSING_VARIABLE("Missing variable"),
    NOT_CONSTANT_IDENTIFIER("Not a constant identifier"),
    NOT_RECORD_VARIABLE("Not a record variable"),
    NOT_TYPE_IDENTIFIER("Not a type identifier"),
    RANGE_INTEGER("Integer literal out of range"),
    RANGE_REAL("Real literal out of range"),
    STACK_OVERFLOW("Stack overflow"),
    TOO_MANY_LEVELS("Nesting level too deep"),
    TOO_MANY_SUBSCRIPTS("Too many subscripts"),
    UNEXPECTED_EOF("Unexpected end of file"),
    UNEXPECTED_TOKEN("Unexpected token"),
    UNIMPLEMENTED("Unimplemented feature"),
    UNRECOGNIZABLE("Unrecognizable input"),
    WRONG_NUMBER_OF_PARMS("Wrong number of actual parameters"),

    // Fatal errors.
    IO_ERROR(-101, "Object I/O error"),
    TOO_MANY_ERRORS(-102, "Too many syntax errors");

    private int status;      // exit status
    private String message;  // error message

    /**
     * Constructor.
     * @param message the error message.
     */
    TriangleErrorCode(String message)
    {
        this.status = 0;
        this.message = message;
    }

    /**
     * Constructor.
     * @param status the exit status.
     * @param message the error message.
     */
    TriangleErrorCode(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

    /**
     * Getter.
     * @return the exit status.
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * @return the message.
     */
    public String toString()
    {
        return message;
    }
}
