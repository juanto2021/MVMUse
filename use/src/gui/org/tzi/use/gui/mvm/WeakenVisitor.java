package org.tzi.use.gui.mvm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpClosure;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstUnlimitedNatural;
import org.tzi.use.uml.ocl.expr.ExpExists;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpIf;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpIsKindOf;
import org.tzi.use.uml.ocl.expr.ExpIsTypeOf;
import org.tzi.use.uml.ocl.expr.ExpIsUnique;
import org.tzi.use.uml.ocl.expr.ExpLet;
import org.tzi.use.uml.ocl.expr.ExpNavigationClassifierSource;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpObjRef;
import org.tzi.use.uml.ocl.expr.ExpObjectByUseId;
import org.tzi.use.uml.ocl.expr.ExpOclInState;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpRange;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.ExpSelectByKind;
import org.tzi.use.uml.ocl.expr.ExpSelectByType;
import org.tzi.use.uml.ocl.expr.ExpSortedBy;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.Type;


public class WeakenVisitor extends BooleanVisitor {

	private List<Expression> mutatedExpr;
	private static Map<String, List<VarDecl>> mapVarsByType; 

	public WeakenVisitor(Map<String, List<VarDecl>> pMapVarsByType) {
		mutatedExpr = new LinkedList<Expression>();
		mapVarsByType= pMapVarsByType; 
	}

	public List<Expression> getMutatedExpr() {
		return mutatedExpr;
	}

	private static List<Expression> strengthen(Expression exp, Map<String, List<VarDecl>> pMapVarsByType) {
		StrengthenVisitor vis = new StrengthenVisitor(pMapVarsByType);
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}
	private static List<Expression> weaken(Expression exp, Map<String, List<VarDecl>> pMapVarsByType) {
		WeakenVisitor vis = new WeakenVisitor( pMapVarsByType);
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}

	private void defaultWeakening() {
		// The default weakening of an expression is setting it to "true"
		Expression mutant = new ExpConstBoolean(true);
		mutatedExpr.add(mutant);
	}
	private void includeDecl(VarDeclList decl) {

		int nDecs = decl.size();
		for (int nDec=0;nDec<nDecs;nDec++) {
			VarDecl v = decl.varDecl(nDec); 
			String typeName = v.type().shortName();

			List<VarDecl> varsByType = mapVarsByType.getOrDefault(typeName, new ArrayList<>());

			// Check if the variable is already in the list
			boolean exists = false;
			for (VarDecl existingVar : varsByType) {
				if (existingVar.name().equals(v.name())) {
					exists = true;
					break;
				}
			}

			// If the variable does not exist, add it to the list and update the map
			if (!exists) {
				varsByType.add(v);
				mapVarsByType.put(typeName, varsByType);
			}

//			System.out.println("mapVarsByType "+mapVarsByType);
		}
	}
	private void excludeDecl(VarDeclList decl) {
		int nDecs = decl.size();
		for (int nDec=0;nDec<nDecs;nDec++) {
			VarDecl v = decl.varDecl(nDec); 
			String typeName = v.type().shortName();
			List<VarDecl> varsByType = mapVarsByType.getOrDefault(typeName, new ArrayList<>());

			// Check if the variable is already in the list
			boolean exists = false;
			for (VarDecl existingVar : varsByType) {
				if (existingVar.name().equals(v.name())) {
					exists = true;
					break;
				}
			}
			if (exists) {
				varsByType.remove(v);
				if (varsByType.size()>0) {
					mapVarsByType.replace(typeName, varsByType);
				}else {
					mapVarsByType.remove(typeName);
				}

			}
		}
//		System.out.println("mapVarsByType "+mapVarsByType);
	}

	// These expression have already been considered in BooleanVisitor
	// public void visitAllInstances(ExpAllInstances exp) 
	// public void visitBagLiteral(ExpBagLiteral exp) 
	// public void visitCollect(ExpCollect exp) 
	public void visitCollect(ExpCollect exp) {
//		System.out.println("Weaken - visitCollect");
	}
	// public void visitCollectNested(ExpCollectNested exp) 
	// public void visitConstEnum(ExpConstEnum exp) 
	// public void visitConstInteger(ExpConstInteger exp) 
	// public void visitConstReal(ExpConstReal exp) 
	// public void visitConstString(ExpConstString exp) 
	// public void visitEmptyCollection(ExpEmptyCollection exp) 
	// public void visitObjAsSet(ExpObjAsSet exp) 
	// public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp)
	// public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) 
	// public void visitIterate(ExpIterate exp) 
	// public void visitReject(ExpReject exp) 
	// public void visitSelect(ExpSelect exp) 
	// public void visitNavigation(ExpNavigation exp) 	
	// public void visitSequenceLiteral(ExpSequenceLiteral exp) 
	// public void visitSetLiteral(ExpSetLiteral exp)
	// public void visitTupleLiteral(ExpTupleLiteral exp) 
	// public void visitVarDeclList(VarDeclList varDeclList) 
	// public void visitVarDecl(VarDecl varDecl)
	// public void visitRange(ExpRange exp) 
	// public void visitUndefined(ExpUndefined exp)


	@Override
	public void visitAny(ExpAny exp) {
//		System.out.println("Weaken - visitAny");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}	
	}

	@Override
	public void visitAsType(ExpAsType exp) {
//		System.out.println("Weaken - visitAsType");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}	
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
//		System.out.println("Weaken - visitAttrOp");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}	
	}


	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
//		System.out.println("Weaken - visitConstBoolean");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}


	@Override
	public void visitExists(ExpExists exp) {
//		System.out.println("Weaken - visitExists");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();

		// Include the declarations and their type in the map
		includeDecl(decl);

		// Generate mutants for the query
		List<Expression> queryMutants = weaken(query, mapVarsByType);

		// Generate the hull of the range
		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		// Mutation 1: weaken the property within the "exists" (W2a)
		// Generate a new mutant for each mutant of the query expression
		for (Expression mutant: queryMutants) {
			try {
				Expression newMutant = new ExpExists(decl,range,mutant);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the hull of the collection within the query (W2b)
		for(Expression hull: rangeHull) {
			try {
				Expression newMutant = new ExpExists(decl,hull,query);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3
		// Do both - weaken the property within the "exists" (W2c)
		// And compute the hull of the collection
		for(Expression mutant: queryMutants) {
			for(Expression hull: rangeHull) {
				try {
					Expression newMutant = new ExpExists(decl,hull,mutant);
					mutatedExpr.add(newMutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}

		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

		// Remove previously included decl elements from the map
		excludeDecl(decl);		
	}

	@Override
	public void visitForAll(ExpForAll exp) {
//		System.out.println("Weaken - visitForAll");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression(); // coleccion de tipo x
		VarDeclList decl = exp.getVariableDeclarations(); 

		// Include the declarations and their type in the map	
		includeDecl(decl);
//Aqui
		// Generate mutants for the query
		List<Expression> queryMutants = weaken(query, mapVarsByType);

		// Generate the kernel of the range
		// TODO: review next sentence when KernelVisitor is implemented
		List<Expression> rangeKernel = KernelVisitor.kernel(query);

		// Generate the hull of the range
		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		// Mutation 1: compute the kernel of the collection (W1a) --- e1v->forAll(x|p(x)) ---
		// Generate a new mutant for each mutant of the kernel collection within the query
		for(Expression kernel: rangeKernel) {
			try {
				Expression newMutant = new ExpForAll(decl,kernel,query);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 2: compute the weaken of the collection (W1b) --- e1->forAll(x|p(x)-) ---
		for(Expression mutant: queryMutants) {
			try {
				Expression newMutant = new ExpForAll(decl,range,mutant);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 3: do both (W1c) --- e1v->forAll(x|p(x)-) ---
		for(Expression mutant: queryMutants) {
			for(Expression kernel: rangeKernel) {
				try {
					Expression newMutant = new ExpForAll(decl,kernel,mutant);
					mutatedExpr.add(newMutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();	
				}	
			}
		}

		//Mutation 4: (W1d) --- e1->isEmpty() or e1->exists(x|p(x))	---------------------
		try {
			Expression newArgsEmpty[] = {range};
			Expression mutantEmpty = ExpStdOp.create("isEmpty", newArgsEmpty);

			Expression mutantExist = new ExpExists(decl,range,query);

			Expression newArgsOr[] = {mutantEmpty, mutantExist};
			Expression mutant = ExpStdOp.create("or", newArgsOr);
			mutatedExpr.add(mutant);
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}

		// Mutation 5: (W1e) --- e1->isEmpty() or e1->exists(x|p(x)-) --------------------
		// Generate a new mutant for each mutant of the weaken collection within the query
		for(Expression mutant: queryMutants) {
			try {
				Expression newArgsEmpty[] = {range};
				Expression mutantEmpty = ExpStdOp.create("isEmpty", newArgsEmpty);

				Expression mutantExist = new ExpExists(decl,range,mutant);

				Expression newArgsOr[] = {mutantEmpty, mutantExist};
				Expression mutantOr = ExpStdOp.create("or", newArgsOr);
				mutatedExpr.add(mutantOr);

			} catch (ExpInvalidException e) {
				e.printStackTrace();	
			}	
		}
		//Mutation 6: (W1f) --- e1->isEmpty() or e1^->exists(x|p(x))
		// Generate a new mutant for each mutant of the hull collection within the query
		for(Expression hull: rangeHull) {
			try {
				Expression newArgsEmpty[] = {range};
				Expression mutantEmpty = ExpStdOp.create("isEmpty", newArgsEmpty);
				Expression mutantExist = new ExpExists(decl,hull,query);

				Expression newArgsOr[] = {mutantEmpty, mutantExist};
				Expression mutantOr = ExpStdOp.create("or", newArgsOr);
				mutatedExpr.add(mutantOr);
			} catch (ExpInvalidException e) {
				e.printStackTrace();	
			}	
		}

		// Mutation 7: do both (W1g) --- e1->isEmpty() or e1^->exists(x|p(x)-)
		for(Expression mutant: queryMutants) {
			for(Expression hull: rangeHull) {
				try {
					Expression newArgsEmpty[] = {hull};
					Expression mutantEmpty = ExpStdOp.create("isEmpty", newArgsEmpty);
					Expression mutantExist = new ExpExists(decl,hull,mutant);

					Expression newArgsOr[] = {mutantEmpty, mutantExist};
					Expression mutantOr = ExpStdOp.create("or", newArgsOr);
					mutatedExpr.add(mutantOr);

				} catch (ExpInvalidException e) {
					e.printStackTrace();	
				}	
			}
		}

		// Mutation 8: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

		// Remove previously included decl elements from the map
		excludeDecl(decl);
	}

	@Override
	public void visitIf(ExpIf exp) {
//		System.out.println("Weaken - visitIf");
		Expression cond = exp.getCondition();
		Expression thenExp = exp.getThenExpression();
		Expression elseExp = exp.getElseExpression();

		// Mutate each of the alternatives of the conditional
		List<Expression> thenMutants = weaken(thenExp, mapVarsByType);
		List<Expression> elseMutants = weaken(elseExp, mapVarsByType);

		// Mutation1: (3) we mutate both branches of the conditiona (W7)
		for(Expression thenMutant: thenMutants) {
			for(Expression elseMutant: elseMutants) {
				Expression newMutant;
				try {
					newMutant = new ExpIf(cond, thenMutant, elseMutant);
					mutatedExpr.add(newMutant);
				}  catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {

//		System.out.println("Weaken - visitIsKindOf");
		Type targetType = exp.getTargetType();
		Set<? extends Type> setSuperTypes= targetType.allSupertypes();// Type set. Each one is a T2

		// Get the expression being checked
		Expression sourceExp = exp.getSourceExpr(); 

		for (Type superType : setSuperTypes) {
			String typeName = superType.shortName();
			if (!typeName.equals("OclAny")) {
				try {
					Expression mutant = new ExpIsKindOf(sourceExp, superType);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e1) {
					e1.printStackTrace();
				}
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
//		System.out.println("Weaken - visitIsTypeOf");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
//		System.out.println("Weaken - visitIsUnique");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();

		// Include the declarations and their type in the map
		includeDecl(decl);

		// Generate the kernel of the range
		// TODO: Verify next sentence when KernelVisitor is implemented
		List<Expression> rangeKernel = KernelVisitor.kernel(range);

		// Mutation 1: compute the kernel of the collection -- e1v->isUnique(x|p(x)) --
		for(Expression kernel: rangeKernel) {
			try {
				Expression mutant = new ExpIsUnique(decl.varDecl(0), kernel, query);
				mutatedExpr.add(mutant);
			}  catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

		// Remove previously included decl elements from the map
		excludeDecl(decl);
		
		
	}

	@Override
	public void visitLet(ExpLet exp) {
//		System.out.println("Weaken - visitLet");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
//		System.out.println("Weaken - visitObjOp");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
//		System.out.println("Weaken - visitObjRef");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitOne(ExpOne exp) {
//		System.out.println("Weaken - visitOne");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();

		// Include the declarations and their type in the map
		includeDecl(decl);

		// Generate mutants for the query
		List<Expression> queryMutants = weaken(query, mapVarsByType);

		// Generate the hull of the range
		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		// Mutation 1: use the property within the "exists" (W3a)
		try {
			Expression newMutant = new ExpExists(decl,range,query);
			mutatedExpr.add(newMutant);
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}

		// Mutation 2: weaken the property within the "exists" (W3b)
		// Generate a new mutant for each mutant of the query expression
		for (Expression mutant: queryMutants) {
			try {
				Expression newMutant = new ExpExists(decl,range,mutant);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: Compute the hull of the collection within the query (W3c)
		for(Expression hull: rangeHull) {
			try {
				Expression newMutant = new ExpExists(decl,hull,query);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 4
		// Do both - weaken the property within the "exists" (W3d)
		// And compute the hull of the collection
		for(Expression mutant: queryMutants) {
			for(Expression hull: rangeHull) {
				try {
					Expression newMutant = new ExpExists(decl,hull,mutant);
					mutatedExpr.add(newMutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}

		// Mutation 5: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}	

		// Remove previously included decl elements from the map
		excludeDecl(decl);		
	}

	@Override
	public void visitQuery(ExpQuery exp) {
//		System.out.println("Weaken - visitQuery");
		wrongTypeError("visit query - this node should not be reached");

	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {
//		System.out.println("Weaken - visitWithValue");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
//		System.out.println("Weaken - visitSortedBy");
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	//OK
	private void mutateOrExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateOrExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "or" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		List<Expression> leftMutants  = weaken(left, mapVarsByType);
		List<Expression> rightMutants = weaken(right, mapVarsByType); 

		// Mutation 1: Weaken the left subexpression (W2a)
		for(Expression mutant: leftMutants) {
			Expression newArgs[] = {mutant, right};
			try {
				Expression mutant1 = ExpStdOp.create("or", newArgs);
				mutatedExpr.add(mutant1);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 2: Weaken the right subexpression (W2b)
		for(Expression mutant: rightMutants) {
			Expression newArgs[] = {left, mutant};
			try {
				Expression mutant2 = ExpStdOp.create("or", newArgs);
				mutatedExpr.add(mutant2);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 3: Weaken both subexpressions (W2c)
		for(Expression mutantA: leftMutants) {
			for (Expression mutantB: rightMutants) {
				Expression newArgs[] = {mutantA, mutantB};
				try {
					Expression mutant3 = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutant3);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}

		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateAndExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateAndExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "and" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		List<Expression> leftMutants  = weaken(left, mapVarsByType);
		List<Expression> rightMutants = weaken(right, mapVarsByType);

		// Mutation 1: Weaken the left subexpression (W1a)
		for(Expression mutant: leftMutants) {
			Expression newArgs[] = {mutant, right};
			try {
				Expression mutant1 = ExpStdOp.create("and", newArgs);
				mutatedExpr.add(mutant1);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 2: Weaken the right subexpression (W1b)
		for(Expression mutant: rightMutants) {
			Expression newArgs[] = {left, mutant};
			try {
				Expression mutant2 = ExpStdOp.create("and", newArgs);
				mutatedExpr.add(mutant2);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 3: Weaken both subexpressions (W1c)
		for(Expression mutantA: leftMutants) {
			for (Expression mutantB: rightMutants) {
				Expression newArgs[] = {mutantA, mutantB};
				try {
					Expression mutant3 = ExpStdOp.create("and", newArgs);
					mutatedExpr.add(mutant3);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}
		// Mutation 4: use only left (W1d)
		mutatedExpr.add(left);
		// Mutation 5: use only right (W1e)
		mutatedExpr.add(right);
		// Mutation 6: use only leftMutants individually (W1f)
		for(Expression mutant: leftMutants) {
			mutatedExpr.add(mutant);
		}
		// Mutation 7: use only rightMutants individually (W1g)
		for(Expression mutant: rightMutants) {
			mutatedExpr.add(mutant);
		}
		// Mutation 8: Replace the "and" with an "or" (W1h)
		try {
			Expression mutant = ExpStdOp.create("or", args);
			mutatedExpr.add(mutant);
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}
		// Mutation 9: Weaken the left subexpression of leftMutants (W1i)
		for(Expression mutant: leftMutants) {
			Expression newArgs[] = {mutant, right};
			try {
				Expression mutantNew = ExpStdOp.create("or", newArgs);
				mutatedExpr.add(mutantNew);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}
		// Mutation 10: Weaken the right subexpression (W1j)
		for(Expression mutant: rightMutants) {
			Expression newArgs[] = {left, mutant};
			try {
				Expression mutant2 = ExpStdOp.create("or", newArgs);
				mutatedExpr.add(mutant2);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}
		// Mutation 11: Weaken both subexpressions (W1k)
		for(Expression mutantA: leftMutants) {
			for (Expression mutantB: rightMutants) {
				Expression newArgs[] = {mutantA, mutantB};
				try {
					Expression mutant3 = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutant3);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}

		// Mutation 12: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

	}

	private void mutateXorExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateXorExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "xor" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0]; // e1
		Expression right = args[1]; // e2
		if (left.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID) &&  
				right.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID)) {     
			List<Expression> leftMutantsWeaken  = weaken(left, mapVarsByType);
			List<Expression> rightMutantsWeaken = weaken(right, mapVarsByType);
			List<Expression> leftMutantsStrengthen  = strengthen(left, mapVarsByType);
			List<Expression> rightMutantsStrengthen = strengthen(right, mapVarsByType);

			// Mutation 1: Replace the "xor" with an "or" (W5a)
			try {
				Expression mutant = ExpStdOp.create("or", args);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			// Mutation 2: Weaken the left subexpression of leftMutants (W5b)
			for(Expression mutant: leftMutantsWeaken) {
				Expression newArgs[] = {mutant, right};
				try {
					Expression mutantNew = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutantNew);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
			// Mutation 3: Weaken the right subexpression (W5c)
			for(Expression mutant: rightMutantsWeaken) {
				Expression newArgs[] = {left, mutant};
				try {
					Expression mutant2 = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutant2);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
			// Mutation 4: Weaken both subexpressions (W5d)
			for(Expression mutantA: leftMutantsWeaken) {
				for (Expression mutantB: rightMutantsWeaken) {
					Expression newArgs[] = {mutantA, mutantB};
					try {
						Expression mutant3 = ExpStdOp.create("or", newArgs);
						mutatedExpr.add(mutant3);
					} catch (ExpInvalidException e) {
						e.printStackTrace();
					}
				}
			}
			// Mutation 5: (e1 xor e2)+ or (e1- xor e2-) or (e1+ xor e2+)
			Expression newArgsBasic[] = {left, right};
			try {
				Expression mutantBasic = ExpStdOp.create("xor", newArgsBasic);// (e1 xor e2)
				List<Expression> basicMutantsStrengthen = strengthen(mutantBasic, mapVarsByType);

				for(Expression basicMutantStr: basicMutantsStrengthen) {
					for(Expression leftMutantWeak: leftMutantsWeaken) {
						for(Expression rightMutantWeak: rightMutantsWeaken) {
							Expression newArgsWeak[] = {leftMutantWeak, rightMutantWeak};
							Expression mutantPairWeak = ExpStdOp.create("xor", newArgsWeak); // (e1- xor e2-)
							for(Expression leftMutantStr: leftMutantsStrengthen) {
								for(Expression rightMutantStr: rightMutantsStrengthen) {
									Expression newArgsStrength[] = {leftMutantStr, rightMutantStr};
									Expression mutantPairStrength = ExpStdOp.create("xor", newArgsStrength); // (e1+ xor e2+)

									Expression newArgsEnd1[] = {basicMutantStr, mutantPairWeak};// (e1 xor e2) or (e1- xor e2-)
									Expression mutantEnd1 = ExpStdOp.create("or", newArgsEnd1);

									Expression newArgsEnd2[] = {mutantEnd1, mutantPairStrength};// (e1- xor e2-) or (e1+ xor e2+)
									Expression mutantEnd = ExpStdOp.create("or", newArgsEnd2);								

									mutatedExpr.add(mutantEnd);
								}
							}
						}
					}
				}

			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 6: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateImpliesExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateImpliesExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "implies" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		List<Expression> leftMutants  = strengthen(left, mapVarsByType);
		List<Expression> rightMutants = weaken(right, mapVarsByType);

		// Mutation 1, combine each left strength with rigth (W4a)
		for(Expression mutant: leftMutants) {
			Expression newArgs[] = {mutant, right};
			try {
				Expression mutantNew = ExpStdOp.create("implies", newArgs);
				mutatedExpr.add(mutantNew);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 2, combine each left with each rigth weaken (W4b)
		for(Expression mutant: rightMutants) {
			Expression newArgs[] = {left, mutant};
			try {
				Expression mutant2 = ExpStdOp.create("implies", newArgs);
				mutatedExpr.add(mutant2);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}

		// Mutation 3, combine each left strenght with each rigth weaken (W4c)
		for(Expression mutantA: leftMutants) {
			for (Expression mutantB: rightMutants) {
				Expression newArgs[] = {mutantA, mutantB};
				try {
					Expression mutant3 = ExpStdOp.create("implies", newArgs);
					mutatedExpr.add(mutant3);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}
		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

	}

	private void mutateNotExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateNotExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "xor" is a binary expression
		assert(args.length == 1);
		Expression subexp  = args[0];

		// TODO: Review next sentence when Strengthen is implemented
		List<Expression> mutants  = strengthen(subexp, mapVarsByType);

		// Mutation 1: strengthen the subexpression (W3a)
		for(Expression mutantSubExp: mutants) {
			Expression newArgs[] = {mutantSubExp};
			try {
				Expression mutant = ExpStdOp.create("not", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}


	private void mutateIsEmptyExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateIsEmptyExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "isEmpty()" is a unary operation
		assert(args.length == 1);		
		Expression col  = args[0];

		// Generate the kernel of the range
		// TODO: Verify next sentence when KernelVisitor is implemented
		List<Expression> rangeKernel = KernelVisitor.kernel(col);

		// Mutation 1: Compute the kernel of the collection --- e1v->isEmpty() --------
		// Compute the "isEmpty()" operation on the srink collection
		for(Expression mutantCol: rangeKernel) {
			Expression newArgs[] = {mutantCol};
			try {
				Expression mutant = ExpStdOp.create("isEmpty", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateNotEmptyExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateNotEmptyExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "isEmpty()" is a unary operation
		assert(args.length == 1);
		Expression col  = args[0];

		List<Expression> rangeHull = HullVisitor.hull(col,mapVarsByType);

		// Mutation 1: Compute the hull of the collection --- e1^->notEmpty() --------
		// Compute the "notEmpty()" operation on the reduced collection
		for(Expression hull: rangeHull) {
			Expression newArgs[] = {hull};
			try {
				//				put assert to ensure that it is of type collection
				assert(hull.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)): "Expression is not of collection type";
				if (!hull.type().isTypeOfBoolean()) {
					//					System.out.println("Weaken - es boolean");
					Expression mutant = ExpStdOp.create("notEmpty", newArgs);
					mutatedExpr.add(mutant);
				}
				//				Expression mutant = ExpStdOp.create("notEmpty", newArgs);
				//				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateIncludesExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateIncludesExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "includes()" is a binary operation
		assert(args.length == 2);
		Expression col  = args[0];
		Expression elem = args[1];

		List<Expression> rangeHull = HullVisitor.hull(col,mapVarsByType);

		// Mutation 1: Compute the hull of the collection -- e1^->includes(e2) --
		// Compute the "includes()" operation on the expanded collection
		for(Expression mutantCol: rangeHull) {
			Expression newArgs[] = {mutantCol, elem};
			try {
				Expression mutant = ExpStdOp.create("includes", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateExcludesExp(ExpStdOp exp) {
		System.out.println("Weaken - mutateExcludesExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "excludes()" is a binary operation
		assert(args.length == 2);
		Expression col  = args[0];
		Expression elem = args[1];

		// Generate the kernel of the range
		// TODO: Verify next sentence when KernelVisitor is implemented
		List<Expression> rangeKernel = KernelVisitor.kernel(col);

		// Mutation 1: Compute the kernel of the collection -- e1v->excludes(e2) --
		// Compute the "includes()" operation on the reduced collection
		for(Expression mutantCol: rangeKernel) {
			Expression newArgs[] = {mutantCol, elem};
			try {
				Expression mutant = ExpStdOp.create("excludes", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}	

	private void mutateIncludesAllExp(ExpStdOp exp) {
		System.out.println("Weaken - mutateIncludesAllExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "includesAll()" is a binary operation
		assert(args.length == 2);
		Expression baseCol  = args[0];
		Expression otherCol = args[1];

		// Generate the hull of the range
		List<Expression> baseColMutantsHull = HullVisitor.hull(baseCol,mapVarsByType);

		// Generate the kernel of the range
		// TODO: Verify next sentence when KernelVisitor is implemented
		List<Expression> otherColMutantsKernel = KernelVisitor.kernel(otherCol);

		// Mutation 1: Compute the hull of the base collection (W5a) -- e1^->includesAll(e2) --
		for(Expression mutantCol: baseColMutantsHull) {
			Expression newArgs[] = {mutantCol, otherCol};
			try {
				Expression mutant = ExpStdOp.create("includesAll", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the kernel of the other collection (w5b) -- e1->includesAll(e2v) --
		for(Expression mutantCol: otherColMutantsKernel) {
			Expression newArgs[] = {baseCol, mutantCol};
			try {
				Expression mutant = ExpStdOp.create("includesAll", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}


		// Mutation 3: Compute the hull of the base collection and the kernel of the other collection (W5c)
		// e1^->includesAll(e2v)
		for(Expression baseMutant: baseColMutantsHull) {
			for(Expression otherMutant: otherColMutantsKernel) {
				Expression newArgs[] = {baseMutant, otherMutant};
				try {
					Expression mutant = ExpStdOp.create("includesAll", newArgs);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();

				}
			}
		}

		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateExcludesAllExp(ExpStdOp exp) {
		System.out.println("Weaken - mutateExcludesAllExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "includesAll()" is a binary operation
		assert(args.length == 2);
		Expression baseCol  = args[0];
		Expression otherCol = args[1];

		// Generate the kernel of the range
		// TODO: Verify next sentence when KernelVisitor is implemented
		List<Expression> baseColMutantsKernel = KernelVisitor.kernel(baseCol);
		List<Expression> otherMutantsKernel = KernelVisitor.kernel(otherCol);

		// Mutation 1: Compute the kernel of the base collection (W6a) -- e1v->excludesAll(e2) --
		for(Expression mutantCol: baseColMutantsKernel) {
			Expression newArgs[] = {mutantCol, otherCol};
			try {
				Expression mutant = ExpStdOp.create("excludesAll", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the kernel of the other collection (W6b) -- e1->excludesAll(e2v) --
		for(Expression mutantCol: otherMutantsKernel) {
			Expression newArgs[] = {baseCol, mutantCol};
			try {
				Expression mutant = ExpStdOp.create("excludesAll", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: Compute the kernel of the base collection and the kernel of the other collection (W6c)
		// e1v->excludesAll(e2v)
		for(Expression baseMutant: baseColMutantsKernel) {
			for(Expression otherMutant: otherMutantsKernel) {
				Expression newArgs[] = {baseMutant, otherMutant};
				try {
					Expression mutant = ExpStdOp.create("excludesAll", newArgs);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();

				}
			}
		}

		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}	

	private void mutateLessEqualExp(ExpStdOp exp) {

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: ">" is a binary operation
		assert(args.length == 2);
		Expression left = args[0];
		Expression right = args[1];

		if(left.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID) &&
				right.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID)) {
			// Mutation 1: e1 - K <= e2 (W5)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("-", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create("<=", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}			
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateGreaterEqualExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateGreaterEqualExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: ">" is a binary operation
		assert(args.length == 2);
		Expression left = args[0];
		Expression right = args[1];

		if(left.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID) &&
				right.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID)) {
			// Mutation 1: e1 + K >= e2 (W3a)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("+", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create(">=", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}			
		}
		// Mutation 2: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	private void mutateLessExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateLessExp");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: ">" is a binary operation
		assert(args.length == 2);
		Expression left = args[0];
		Expression right = args[1];

		if(left.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID) &&
				right.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID)) {

			// Mutation 1: e1<=e2 (W4a)
			try {
				Expression newArgs[] = {left, right};
				Expression mutant1 = ExpStdOp.create("<=", newArgs);
				mutatedExpr.add(mutant1);		
			} catch (ExpInvalidException e1) {
				e1.printStackTrace();
			}

			// Mutation 2: e1 - K < e2 (W4b)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("-", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create("<", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			// Mutation 3: e1 - K <= e2 (W4c)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("-", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create("<=", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}			
		}
		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}
	//OK
	private void mutateGreaterExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateGreaterExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: ">" is a binary operation
		assert(args.length == 2);
		Expression left = args[0];
		Expression right = args[1];

		if(left.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID) &&
				right.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID)) {

			// Mutation 1: e1>=e2 (W2a)
			try {
				Expression newArgs[] = {left, right};
				Expression mutant1 = ExpStdOp.create(">=", newArgs);
				mutatedExpr.add(mutant1);		
			} catch (ExpInvalidException e1) {
				e1.printStackTrace();
			}

			// Mutation 2: e1 + K > e2 (W2b)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("+", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create(">", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			// Mutation 3: e1 + K >= e2 (W2c)
			try {
				Expression newArgs1[] = {left, new ExpConstInteger(1)};
				Expression aux = ExpStdOp.create("+", newArgs1);
				Expression newArgs2[] = {aux,right};
				Expression mutant = ExpStdOp.create(">=", newArgs2);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}			
		}
		// Mutation 4: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}

	}

	private void mutateNotEqualsExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateNotEqualsExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "xor" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		// Aqui el siguiente IF es provisional
		if (left.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID) &&      
				right.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID)) {     
			List<Expression> leftMutantsWeaken  = weaken(left, mapVarsByType);
			List<Expression> rightMutantsWeaken = weaken(right, mapVarsByType);
			List<Expression> leftMutantsStrengthen  = strengthen(left, mapVarsByType);
			List<Expression> rightMutantsStrengthen = strengthen(right, mapVarsByType);

			// Mutation 1: Replace the "xor" with an "or" (W5a)
			try {
				Expression mutant = ExpStdOp.create("or", args);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			// Mutation 2: Weaken the left subexpression of leftMutants (W5b)
			for(Expression mutant: leftMutantsWeaken) {
				Expression newArgs[] = {mutant, right};
				try {
					Expression mutantNew = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutantNew);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
			// Mutation 3: Weaken the right subexpression (W5c)
			for(Expression mutant: rightMutantsWeaken) {
				Expression newArgs[] = {left, mutant};
				try {
					Expression mutant2 = ExpStdOp.create("or", newArgs);
					mutatedExpr.add(mutant2);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
			// Mutation 4: Weaken both subexpressions (W5d)
			for(Expression mutantA: leftMutantsWeaken) {
				for (Expression mutantB: rightMutantsWeaken) {
					Expression newArgs[] = {mutantA, mutantB};
					try {
						Expression mutant3 = ExpStdOp.create("or", newArgs);
						mutatedExpr.add(mutant3);
					} catch (ExpInvalidException e) {
						e.printStackTrace();
					}
				}
			}
			// Mutation 5: (e1 xor e2)+ or (e1- xor e2-) or (e1+ xor e2+)
			Expression newArgsBasic[] = {left, right};
			try {
				Expression mutantBasic = ExpStdOp.create("xor", newArgsBasic);// (e1 xor e2)
				List<Expression> basicMutantsStrengthen = strengthen(mutantBasic, mapVarsByType);

				for(Expression basicMutantStr: basicMutantsStrengthen) {
					for(Expression leftMutantWeak: leftMutantsWeaken) {
						for(Expression rightMutantWeak: rightMutantsWeaken) {
							Expression newArgsWeak[] = {leftMutantWeak, rightMutantWeak};
							Expression mutantPairWeak = ExpStdOp.create("xor", newArgsWeak); // (e1- xor e2-)
							for(Expression leftMutantStr: leftMutantsStrengthen) {
								for(Expression rightMutantStr: rightMutantsStrengthen) {
									Expression newArgsStrength[] = {leftMutantStr, rightMutantStr};
									Expression mutantPairStrength = ExpStdOp.create("xor", newArgsStrength); // (e1+ xor e2+)

									Expression newArgsEnd1[] = {basicMutantStr, mutantPairWeak};// (e1 xor e2) or (e1- xor e2-)
									Expression mutantEnd1 = ExpStdOp.create("or", newArgsEnd1);

									Expression newArgsEnd2[] = {mutantEnd1, mutantPairStrength};// (e1- xor e2-) or (e1+ xor e2+)
									Expression mutantEnd = ExpStdOp.create("or", newArgsEnd2);								

									mutatedExpr.add(mutantEnd);
								}
							}
						}
					}
				}

			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		defaultWeakening();
	}
	private void mutateEqualsExp(ExpStdOp exp) {
//		System.out.println("Weaken - mutateEqualsExp");
		Expression[] args = exp.args();
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];
		if (left.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID) && 
				right.type().isKindOfBoolean(Type.VoidHandling.EXCLUDE_VOID)) { 

			try {
				// Mutation: e1->e2
				Expression newArgs1[] = {left,right};
				Expression mutantNew1 = ExpStdOp.create("implies", newArgs1);

				// Mutation: e2->e1
				Expression newArgs2[] = {right,left};
				Expression mutantNew2 = ExpStdOp.create("implies", newArgs2);

				// Mutation 1: e1->e2 and e2->e1 (W6)
				Expression newArgs[] = {mutantNew1, mutantNew2};

				Expression mutant1 = ExpStdOp.create("and", newArgs);
				mutatedExpr.add(mutant1);

			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	

		}else if(left.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID) &&
				right.type().isKindOfNumber(Type.VoidHandling.EXCLUDE_VOID)) {
			// Mutation 3: >= (W1a)
			try {
				Expression newArgs[] = {left,right};
				Expression mutantNew = ExpStdOp.create(">=", newArgs);
				mutatedExpr.add(mutantNew);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
			// Mutation 4: <= (W1b)
			try {
				Expression newArgs[] = {left,right};
				Expression mutantNew = ExpStdOp.create("<=", newArgs);
				mutatedExpr.add(mutantNew);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}	
		}
	}


	private void mutateUndefinedExp(ExpStdOp exp) {
		System.out.println("Weaken - mutateUndefinedExp");
		defaultWeakening();
	}


	@Override
	public void visitStdOp(ExpStdOp exp) {
//		System.out.println("Weaken - visitStdOp");
		// Place-holder for operations returning a boolean value
		// Boolean: or, xor, and, not, implies
		// Collection operations: isEmpty, notEmpty, includes, excludes, includesAll, excludesAll
		// Relational: <=, >=, <, >, =, <>
		String opName = exp.opname();
//		System.out.println("Weaken - visitStdOp - opName ["+opName+"] -----------------------------------");
		switch(opName) {
		case "or":
			mutateOrExp(exp);
			break;	
		case "xor":
			mutateXorExp(exp); 
			break;
		case "and":
			mutateAndExp(exp);
			break;
		case "not":
			mutateNotExp(exp);
			break;	
		case "implies":
			mutateImpliesExp(exp);
			break;	
		case "=":
			mutateEqualsExp(exp); 
			break;	
		case "<=":
			mutateLessEqualExp(exp); 
			break;	
		case ">=":
			mutateGreaterEqualExp(exp);
			break;	
		case "<":
			mutateLessExp(exp);
			break;	
		case ">":
			mutateGreaterExp(exp);
			break;	
		case "<>":
			mutateNotEqualsExp(exp); 
			break;	
		case "isEmpty":
			mutateIsEmptyExp(exp);
			break;	
		case "notEmpty":
			mutateNotEmptyExp(exp);
			break;	
		case "includes":
			mutateIncludesExp(exp);
			break;	
		case "excludes":
			mutateExcludesExp(exp);
			break;	
		case "includesAll":
			mutateIncludesAllExp(exp);
			break;	
		case "excludesAll":
			mutateExcludesAllExp(exp);
			break;	
		case "isUndefined":
			mutateUndefinedExp(exp);
			break;
		default:
			wrongTypeError("unsupported operation type '" + opName + "'");
		}

	}

	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {
//		System.out.println("Weaken - visitTupleLiteral");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
//		System.out.println("Weaken - visitTupleSelectOp");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
//		System.out.println("Weaken - visitUndefined");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitVariable(ExpVariable exp) {
//		System.out.println("Weaken - visitVariable");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitClosure(ExpClosure exp) {
//		System.out.println("Weaken - visitClosure");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitOclInState(ExpOclInState exp) {
//		System.out.println("Weaken - visitOclInState");
		defaultWeakening();
	}

	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
//		System.out.println("Weaken - visitVarDeclList");
		// TODO Auto-generated method stub

	}

	@Override
	public void visitVarDecl(VarDecl varDecl) {
//		System.out.println("Weaken - visitVarDecl");
		// TODO Auto-generated method stub

	}

	@Override
	public void visitObjectByUseId(ExpObjectByUseId exp) {
//		System.out.println("Weaken - visitObjectByUseId");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) {
//		System.out.println("Weaken - visitConstUnlimitedNatural");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind exp) {
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType exp) {
//		System.out.println("Weaken - visitExpSelectByType");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitRange(ExpRange exp) {
//		System.out.println("Weaken - visitRange");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
//		System.out.println("Weaken - visitNavigationClassifierSource");
		// Mutation 1: replace by "true"
		if (exp.type().isTypeOfBoolean()) {		
			defaultWeakening();
		} else {
			wrongTypeError("variable of type " + exp.type().toString() );
		}
	}

}
