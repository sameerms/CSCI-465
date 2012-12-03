package wci.intermediate.symtabimpl;

import java.util.ArrayList;
import java.util.HashMap;

import wci.intermediate.*;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static wci.intermediate.symtabimpl.TrianglePredefined.undefinedType;
import static wci.intermediate.typeimpl.TypeFormImpl.*;

/**
 * <h1>Predefined</h1>
 *
 * <p>Enter the predefined Pascal types, identifiers, and constants
 * into the symbol table.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TrianglePredefined
{
    // Predefined types.
    public static TypeSpec integerType;
    public static TypeSpec booleanType;
    public static TypeSpec charType;
    public static TypeSpec anyType;
    public static TypeSpec undefinedType;

    // Predefined identifiers.
    public static SymTabEntry integerId;
    public static SymTabEntry booleanId;
    public static SymTabEntry charId;
    public static SymTabEntry falseId;
    public static SymTabEntry trueId;
    public static SymTabEntry maxintId;
    
    // Map relational operator tokens to node types.
 	public static final HashMap<String, ICodeNodeType>	STANDARD_ENVIRONMENT;
 	static{
 		STANDARD_ENVIRONMENT = new HashMap<String, ICodeNodeType>();
 		STANDARD_ENVIRONMENT.put("+", ADD);
 		STANDARD_ENVIRONMENT.put("-", SUBTRACT);
 		STANDARD_ENVIRONMENT.put("\\/", OR);
 		STANDARD_ENVIRONMENT.put("*", MULTIPLY);
		STANDARD_ENVIRONMENT.put("/",INTEGER_DIVIDE);
		STANDARD_ENVIRONMENT.put("//",MOD);
		STANDARD_ENVIRONMENT.put("/\\", AND);
		STANDARD_ENVIRONMENT.put("=", EQ);
		STANDARD_ENVIRONMENT.put("\\=", NE);
		STANDARD_ENVIRONMENT.put("<", LT);
		STANDARD_ENVIRONMENT.put("<=", LE);
		STANDARD_ENVIRONMENT.put(">", GT);
		STANDARD_ENVIRONMENT.put(">=", GE);
		STANDARD_ENVIRONMENT.put("\\", NOT);
		STANDARD_ENVIRONMENT.put("eof", EOF);
		STANDARD_ENVIRONMENT.put("eol", EOL);
		STANDARD_ENVIRONMENT.put("get", GET);
		STANDARD_ENVIRONMENT.put("put", PUT);
		STANDARD_ENVIRONMENT.put("getint", GETINT);
		STANDARD_ENVIRONMENT.put("putint", PUTINT);
		STANDARD_ENVIRONMENT.put("geteol", GETEOL);
		STANDARD_ENVIRONMENT.put("puteof", PUTEOL);
 	}
 	

    /**
     * Initialize a symbol table stack with predefined identifiers.
     * @param symTab the symbol table stack to initialize.
     */
    public static void initialize(SymTabStack symTabStack)
    {
        initializeTypes(symTabStack);
        initializeConstants(symTabStack);
        initializeOperators(symTabStack);
    }

    /**
     * Initialize the predefined types.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeTypes(SymTabStack symTabStack)
    {
        // Type integer.
        integerId = symTabStack.enterLocal("integer");
        integerType = TypeFactory.createType(SCALAR);
        integerType.setIdentifier(integerId);
        integerId.setDefinition(DefinitionImpl.TYPE);
        integerId.setTypeSpec(integerType);
        
        // Type boolean.
        booleanId = symTabStack.enterLocal("boolean");
        booleanType = TypeFactory.createType(SCALAR);
        booleanType.setIdentifier(booleanId);
        booleanId.setDefinition(DefinitionImpl.TYPE);
        booleanId.setTypeSpec(booleanType);

        // Type char.
        charId = symTabStack.enterLocal("char");
        charType = TypeFactory.createType(SCALAR);
        charType.setIdentifier(charId);
        charId.setDefinition(DefinitionImpl.TYPE);
        charId.setTypeSpec(charType);

        // Any type
        anyType = TypeFactory.createType(SCALAR);
        
        // Undefined type.
        undefinedType = TypeFactory.createType(SCALAR);
    }

    /**
     * Initialize the predefined constant.
     * @param symTabStack the symbol table stack to initialize.
     */
    private static void initializeConstants(SymTabStack symTabStack)
    {
        // Boolean enumeration constant false.
        falseId = symTabStack.enterLocal("false");
        falseId.setDefinition(DefinitionImpl.CONSTANT);
        falseId.setTypeSpec(booleanType);
        falseId.setAttribute(CONSTANT_VALUE, new Boolean(false));

        // Boolean enumeration constant true.
        trueId = symTabStack.enterLocal("true");
        trueId.setDefinition(DefinitionImpl.CONSTANT);
        trueId.setTypeSpec(booleanType);
        trueId.setAttribute(CONSTANT_VALUE, new Boolean(true));
        
        maxintId = symTabStack.enterLocal("maxint");
        maxintId.setDefinition(DefinitionImpl.CONSTANT);
        maxintId.setTypeSpec(integerType);
        maxintId.setAttribute(CONSTANT_VALUE, Integer.MAX_VALUE);
    }
    
    private static void initializeOperators(SymTabStack symTabStack)
    {
    	unaryOperator(symTabStack,"\\",booleanType,booleanType);
    	//addOperator(symTabStack);
    	binaryOperator(symTabStack,"+",integerType,integerType,integerType);
    	binaryOperator(symTabStack,"-",integerType,integerType,integerType);
    	binaryOperator(symTabStack,"*",integerType,integerType,integerType);
    	binaryOperator(symTabStack,"/",integerType,integerType,integerType);
    	//comparison operators
    	binaryOperator(symTabStack,"<",integerType,integerType,booleanType);
    	binaryOperator(symTabStack,"<=",integerType,integerType,booleanType);
    	binaryOperator(symTabStack,">",integerType,integerType,booleanType);
    	binaryOperator(symTabStack,">=",integerType,integerType,booleanType);
    	binaryOperator(symTabStack,"=",anyType,anyType,booleanType);
    	binaryOperator(symTabStack,"\\=",anyType,anyType,booleanType);
    	//Boolean operators
    	binaryOperator(symTabStack,"\\/",booleanType,booleanType,booleanType);
    	binaryOperator(symTabStack,"/\\",booleanType,booleanType,booleanType);
    	
    	noParamRoutine(symTabStack,"eof",DefinitionImpl.FUNCTION,booleanType);
    	noParamRoutine(symTabStack,"eol",DefinitionImpl.FUNCTION,booleanType);
    	noParamRoutine(symTabStack,"geteol",DefinitionImpl.PROCEDURE,undefinedType);
    	noParamRoutine(symTabStack,"puteol",DefinitionImpl.PROCEDURE,undefinedType);
    	
    	routineWithSingleParam(symTabStack,"chr",DefinitionImpl.FUNCTION, 
    			DefinitionImpl.VALUE_PARM, integerType,charType);
    	routineWithSingleParam(symTabStack,"ord",DefinitionImpl.FUNCTION, 
    			DefinitionImpl.VALUE_PARM, charType,integerType);
    	routineWithSingleParam(symTabStack,"get", DefinitionImpl.PROCEDURE, 
    			DefinitionImpl.VAR_PARM, charType, undefinedType);
    	routineWithSingleParam(symTabStack,"put", DefinitionImpl.PROCEDURE, 
    			DefinitionImpl.VALUE_PARM, charType, undefinedType);
    	routineWithSingleParam(symTabStack,"getint", DefinitionImpl.PROCEDURE, 
    			DefinitionImpl.VAR_PARM, integerType, undefinedType);
    	routineWithSingleParam(symTabStack,"putint", DefinitionImpl.PROCEDURE, 
    			DefinitionImpl.VALUE_PARM, integerType, undefinedType);
    	
    }
    
    private static SymTabEntry createRoutineSymEntry(SymTabStack symTabStack, String name, Definition def, TypeSpec returnType)
    {
    	SymTabEntry routineId;
    	
    	ICode routineICode = ICodeFactory.createICode();
    	// create function entry into symbol table
    	routineId = symTabStack.enterLocal(name);
    	routineId.setDefinition(def);
    	routineId.setTypeSpec(returnType);
    	routineId.setAttribute(ROUTINE_ICODE, routineICode);
    	routineId.setAttribute(ROUTINE_PARMS, new ArrayList<SymTabEntry>());
    	routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
    	symTabStack.pop();
    	return routineId;
    }
    
    @SuppressWarnings("unchecked")
	private static void addFormalParam(SymTabEntry routineId, String identifier, TypeSpec type, Definition def)
    {
        SymTabEntry paramId = ((SymTab)routineId.getAttribute(ROUTINE_SYMTAB)).enter(identifier);
        paramId.setDefinition(def);
 		paramId.setTypeSpec(type);
 		((ArrayList<SymTabEntry>)routineId.getAttribute(ROUTINE_PARMS)).add(paramId);
    }
    
    private static void noParamRoutine(SymTabStack symTabStack, String opString, Definition def, TypeSpec returnType)
    {
    	SymTabEntry routineId = createRoutineSymEntry(symTabStack, opString,def,returnType);
    	ICodeNode node = ICodeFactory.createICodeNode(STANDARD_ENVIRONMENT.get(opString));
    	((ICode)routineId.getAttribute(ROUTINE_ICODE)).setRoot(node);
    }
    
    private static void routineWithSingleParam(SymTabStack symTabStack, String opString, Definition routineDef,
    		Definition paramDef, TypeSpec operandType, TypeSpec returnType)
    {
    	SymTabEntry routineId = createRoutineSymEntry(symTabStack, opString, routineDef,returnType);
    	addFormalParam(routineId, "p", operandType, paramDef);
    	ICodeNode vnameNode = ICodeFactory.createICodeNode(VARIABLE);
		vnameNode.setAttribute(ID, "p");
		vnameNode.setTypeSpec(operandType);
    	ICodeNode opNode = ICodeFactory.createICodeNode(STANDARD_ENVIRONMENT.get(opString));
    	opNode.addChild(vnameNode);
    	((ICode)routineId.getAttribute(ROUTINE_ICODE)).setRoot(opNode);
    }
    
    private static void unaryOperator(SymTabStack symTabStack, String opString, TypeSpec operandType, 
    		TypeSpec returnType)
    {
    	SymTabEntry routineId = createRoutineSymEntry(symTabStack, opString,DefinitionImpl.FUNCTION,returnType);
    	addFormalParam(routineId, "operand", operandType, DefinitionImpl.VALUE_PARM);
    	ICodeNode vnameNode = ICodeFactory.createICodeNode(VARIABLE);
		vnameNode.setAttribute(ID, "operand");
		vnameNode.setTypeSpec(operandType);
    	ICodeNode opNode = ICodeFactory.createICodeNode(STANDARD_ENVIRONMENT.get(opString));
    	opNode.addChild(vnameNode);
    	((ICode)routineId.getAttribute(ROUTINE_ICODE)).setRoot(opNode);
    }
    
    private static void binaryOperator(SymTabStack symTabStack, String opString, TypeSpec lhsType, 
    		TypeSpec rhsType, TypeSpec returnType)
    {
    	SymTabEntry routineId = createRoutineSymEntry(symTabStack, opString,DefinitionImpl.FUNCTION,returnType);
    	addFormalParam(routineId, "lhs", lhsType, DefinitionImpl.VALUE_PARM);
    	addFormalParam(routineId, "rhs", rhsType, DefinitionImpl.VALUE_PARM);
    	ICodeNode vnameNode = ICodeFactory.createICodeNode(VARIABLE);
		vnameNode.setAttribute(ID, "lhs");
		vnameNode.setTypeSpec(lhsType);
    	ICodeNode opNode = ICodeFactory.createICodeNode(STANDARD_ENVIRONMENT.get(opString));
    	opNode.addChild(vnameNode);
    	vnameNode = ICodeFactory.createICodeNode(VARIABLE);
		vnameNode.setAttribute(ID, "rhs");
		vnameNode.setTypeSpec(rhsType);
		opNode.addChild(vnameNode);
    	((ICode)routineId.getAttribute(ROUTINE_ICODE)).setRoot(opNode);
    }
}
