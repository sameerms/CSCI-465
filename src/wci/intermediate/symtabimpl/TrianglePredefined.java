package wci.intermediate.symtabimpl;

import wci.intermediate.*;

import static wci.intermediate.symtabimpl.SymTabKeyImpl.*;
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
    public static TypeSpec undefinedType;

    // Predefined identifiers.
    public static SymTabEntry integerId;
    public static SymTabEntry booleanId;
    public static SymTabEntry charId;
    public static SymTabEntry falseId;
    public static SymTabEntry trueId;
    public static SymTabEntry maxintId;
    

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
    	SymTabEntry funcId;
    	
    	funcId = symTabStack.enterLocal("\\");
    	funcId.setDefinition(DefinitionImpl.FUNCTION);
    	funcId.setTypeSpec(booleanType);
    	funcId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
    	
    	funcId.setAttribute(ROUTINE_ICODE, routineICode);
    }
}
