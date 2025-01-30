package org.tzi.use.gui.mvm;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpBagLiteral;
import org.tzi.use.uml.ocl.expr.ExpClosure;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpCollectNested;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstEnum;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstReal;
import org.tzi.use.uml.ocl.expr.ExpConstString;
import org.tzi.use.uml.ocl.expr.ExpConstUnlimitedNatural;
import org.tzi.use.uml.ocl.expr.ExpEmptyCollection;
import org.tzi.use.uml.ocl.expr.ExpExists;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpIf;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpIsKindOf;
import org.tzi.use.uml.ocl.expr.ExpIsTypeOf;
import org.tzi.use.uml.ocl.expr.ExpIsUnique;
import org.tzi.use.uml.ocl.expr.ExpIterate;
import org.tzi.use.uml.ocl.expr.ExpLet;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpNavigationClassifierSource;
import org.tzi.use.uml.ocl.expr.ExpObjAsSet;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpObjRef;
import org.tzi.use.uml.ocl.expr.ExpObjectByUseId;
import org.tzi.use.uml.ocl.expr.ExpOclInState;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpOrderedSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpRange;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.ExpSelectByKind;
import org.tzi.use.uml.ocl.expr.ExpSelectByType;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpSortedBy;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;

public class OptimizationVisitor extends BooleanVisitor {

	private boolean debOptMet = false;
	private Expression optExp;

	public OptimizationVisitor() {
		optExp = null; 
	}

	public Expression getOptimizedExpr() {
		return optExp;
	}

	public static Expression optimize(Expression exp) {
		OptimizationVisitor vis = new OptimizationVisitor();
		exp.processWithVisitor(vis);
		return vis.getOptimizedExpr();
	}

	// These expression have already been considered in BooleanVisitor

	private void defaultOptimizing(Expression exp) {
		optExp = exp;
	}

	// The following expressions call defaultOptimizing
	public void visitAllInstances(ExpAllInstances exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitAllInstances");
		defaultOptimizing(exp);
	}

	public void visitUndefined(ExpUndefined exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitUndefined");
		defaultOptimizing(exp);
	}

	// (RC) Las constantes devuelven la misma expresion

	public void visitConstEnum(ExpConstEnum exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitConstEnum");
		defaultOptimizing(exp);
	} 
	public void visitConstInteger(ExpConstInteger exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitConstInteger");
		defaultOptimizing(exp);
	} 
	public void visitConstReal(ExpConstReal exp)  {
		if (debOptMet) System.out.println("OptimizationVisitor visitConstReal");
		defaultOptimizing(exp);
	}
	public void visitConstString(ExpConstString exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitConstString");
		defaultOptimizing(exp);
	} 
	public void visitEmptyCollection(ExpEmptyCollection exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitEmptyCollection");
		defaultOptimizing(exp);
	}	

	//(RC): si es colección vacía devuelves colección vacía y si no devuelves lo que hay
	public void visitCollect(ExpCollect exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitCollect");

		Expression range = exp.getRangeExpression();

		// Optimization 1: if it is empty collection return empty collection
		if (range instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			try {
				ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
				optExp = new ExpEmptyCollection(collectionExpr.type());
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			return;
		}
		// Optimization 2: If the collection is not empty, return what is there
		optExp = exp;
	}
	public void visitCollectNested(ExpCollectNested exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitCollectNested");

		Expression range = exp.getRangeExpression();

		// Optimization 1: if it is empty collection return empty collection
		if (range instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			try {
				ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
				optExp = new ExpEmptyCollection(collectionExpr.type());
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			return;
		}
		// Optimization 2: If the collection is not empty, return whatdefaultOptimizing(exp);optExp = exp;
	}

	// (RC) si la query es cierta te quedas con conjunto vacio y si es falsa te quedas con rango
	public void visitReject(ExpReject exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitReject");

		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();

		Expression queryOpt = optimize(query);

		// Optimization 1: query is a boolean constant 
		if (queryOpt instanceof ExpConstBoolean) {
			boolean queryValue = ((ExpConstBoolean)queryOpt).value();
			if (queryValue) {
				// If the query is true, it is the same as asking that the range is not empty
				try {
					//					Expression args[] = {range};
					//					optExp = ExpStdOp.create("notEmpty", args);
					ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
					optExp = new ExpEmptyCollection(collectionExpr.type());
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is false
				optExp = range;
				return;
			}
		}
	}

	// (RC): si la query es false te quedas range y si  es true te quedas con con conjunto vacio). Si Range es vacio, el resultado es range
	public void visitSelect(ExpSelect exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitSelect");

		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();

		Expression queryOpt = optimize(query);

		// Optimization 1: query is a boolean constant 
		if (queryOpt instanceof ExpConstBoolean) {
			boolean queryValue = ((ExpConstBoolean)queryOpt).value();
			if (!queryValue) {
				// If the query is false you stay with range)
				optExp = range;
				return;
			} else {
				// If it is true you are left with an empty set
				try {
					//					Expression args[] = {range};
					//					optExp = ExpStdOp.create("notEmpty", args);
					ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
					optExp = new ExpEmptyCollection(collectionExpr.type());
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}

	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitOrderedSetLiteral");
		defaultOptimizing(exp);
	}
	public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitConstUnlimitedNatural");
		defaultOptimizing(exp);
	} 
	public void visitSequenceLiteral(ExpSequenceLiteral exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitSequenceLiteral");
		defaultOptimizing(exp);
	}
	public void visitSetLiteral(ExpSetLiteral exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitSetLiteral");
		defaultOptimizing(exp);
	}
	public void visitTupleLiteral(ExpTupleLiteral exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitTupleLiteral");
		defaultOptimizing(exp);
	}	
	public void visitBagLiteral(ExpBagLiteral exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitBagLiteral");
		defaultOptimizing(exp);
	}

	public void visitObjAsSet(ExpObjAsSet exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitObjAsSet");
		defaultOptimizing(exp);
	} 

	public void visitIterate(ExpIterate exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitIterate");
		defaultOptimizing(exp);
	} 

	public void visitNavigation(ExpNavigation exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitNavigation");
		defaultOptimizing(exp);
	} 	

	//	 public void visitVarDeclList(VarDeclList varDeclList){
	//	 }
	//	 public void visitVarDecl(VarDecl varDecl){
	//   }
	public void visitRange(ExpRange exp){
		if (debOptMet) System.out.println("OptimizationVisitor visitRange");
		defaultOptimizing(exp);
	}

	@Override
	public void visitAny(ExpAny exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitAny");
		// No optimization so far
		defaultOptimizing(exp);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitAsType");
		// No optimization so far
		defaultOptimizing(exp);
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitAttrOp");
		// No optimization so far
		defaultOptimizing(exp);
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitConstBoolean");
		// No optimization so far
		defaultOptimizing(exp);
	}

	@Override
	public void visitExists(ExpExists exp) {

		if (debOptMet) System.out.println("OptimizationVisitor visitExists");

		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();

		Expression queryOpt = optimize(query);

		// Optimization 1: source collection is known to be empty
		if (range instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			optExp = new ExpConstBoolean(true);
			return;
		}

		// Optimization 2: query is a boolean constant 
		if (debOptMet) System.out.println("queryOpt ["+queryOpt+"] Class ["+queryOpt.getClass()+"]");
		if (queryOpt instanceof ExpConstBoolean) {
			//			if (debOptMet) System.out.println("Es constante");
			boolean queryValue = ((ExpConstBoolean)queryOpt).value();
			if (queryValue) {
				// If the query is false, it is the same as asking that the range is empty
				try {
					Expression args[] = {range};
					optExp = ExpStdOp.create("isEmpty", args);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is true
				optExp = new ExpConstBoolean(false);
				return;
			}
		}

		// Optimization 3: query is undefined
		if (queryOpt instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			return;
		}		

		// Optimization 4: query is invalid
		//		NO HACER porque es invalid
		//	    }

		// Optimization 5: source collection is undefined
		if (range instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			return;				

		}
		// Optimization 6: source collection is invalid

		// Otherwise - no optimization is possible
		defaultOptimizing(exp);
	}

	@Override
	public void visitForAll(ExpForAll exp) {

		if (debOptMet) System.out.println("OptimizationVisitor visitForAll");

		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();

		Expression queryOpt = optimize(query);

		// Optimization 1: source collection is known to be empty
		if (range instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			optExp = new ExpConstBoolean(true);
			return;
		}

		// Optimization 2: query is a boolean constant 
		if (debOptMet) System.out.println("   queryOpt ["+queryOpt+"] Class ["+queryOpt.getClass()+"]");
		if (queryOpt instanceof ExpConstBoolean) {
			//			if (debOptMet) System.out.println("Es constante");
			boolean queryValue = ((ExpConstBoolean)queryOpt).value();
			if (queryValue) {
				// If the query is false, it is the same as asking that the range is empty
				try {
					Expression args[] = {range};
					optExp = ExpStdOp.create("isEmpty", args);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is true
				optExp = new ExpConstBoolean(false);
				return;
			}
		}else 
			// Optimization 3: query is undefined
			if (queryOpt instanceof ExpUndefined) {
				if (debOptMet) System.out.println("undefined");
				optExp = new ExpUndefined();
				return;
			}		

		// Optimization 4: query is invalid
		//		NO HACER porque es invalid
		//	    }

		// Optimization 5: source collection is undefined
		if (range instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			return;				

		}
		// Optimization 6: source collection is invalid

		// Otherwise - no optimization is possible
		defaultOptimizing(exp);
	}

	@Override
	public void visitIf(ExpIf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIf");
		defaultOptimizing(exp);
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIsKindOf");
		defaultOptimizing(exp);
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIsTypeOf");
		defaultOptimizing(exp);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIsUnique");
		defaultOptimizing(exp);
	}

	@Override
	public void visitLet(ExpLet exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitLet");
		defaultOptimizing(exp);
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitObjOp");
		defaultOptimizing(exp);
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitObjRef");
		defaultOptimizing(exp);
	}

	@Override
	public void visitOne(ExpOne exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitOne");
		defaultOptimizing(exp);
	}


	@Override
	public void visitQuery(ExpQuery exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitQuery. This node should not be reached");
		wrongTypeError("visit query - this node should not be reached");
	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitWithValue");
		defaultOptimizing(exp);
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitSortedBy");
		defaultOptimizing(exp);
	}

	private void optimizeOrExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeOrExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "or" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 1: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue || rightValue);
			return;
		}

		// Optimization 2: only one operator is a boolean constant
		if (leftIsConstant || rightIsConstant) {
			// Assume the constant is the leftmost subexpression
			// Otherwise swap them
			if (rightIsConstant) { 
				Expression aux = optLeft;
				optLeft = optRight;
				optRight = aux;
			}
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			if (leftValue) {
				// Result is always true
				optExp = optLeft;
				return;
			} else {
				// Result is always the rightmost operator
				optExp = optRight;
				return;
			}
		} 


		// Optimization 3: some value is undefined / invalid	

		// Otherwise: no boolean constants, no optimization possible
		defaultOptimizing(exp);
	}

	private void optimizeAndExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeAndExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "and" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 1: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue && rightValue);
			return;
		}

		// Optimization 2: only one operator is a boolean constant
		if (leftIsConstant || rightIsConstant) {
			// Assume the constant is the leftmost subexpression
			// Otherwise swap them
			if (rightIsConstant) { 
				Expression aux = optLeft;
				optLeft = optRight;
				optRight = aux;
			}
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			if (leftValue) {
				// Result is always the rightmost operator
				optExp = optRight;
			} else {
				// Result is always false
				optExp = optLeft;
			}
		} 


		// Optimization 3: some value is undefined / invalid	

		// Otherwise: no boolean constants, no optimization possible
		defaultOptimizing(exp);
	}

	private void optimizeXorExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeXorExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "xor" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 1: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue ^ rightValue);
			return;
		}

		// Optimization 2: only one operator is a boolean constant
		if (leftIsConstant || rightIsConstant) {
			// Assume the constant is the leftmost subexpression
			// Otherwise swap them
			if (rightIsConstant) { 
				Expression aux = optLeft;
				optLeft = optRight;
				optRight = aux;
			}
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			if (leftValue) {
				// Result is always true
				optExp = optLeft;
				return;
			} else {
				// Result is always the rightmost operator
				optExp = optRight;
				return;
			}
		} 


		// Optimization 3: some value is undefined / invalid	

		// Otherwise: no boolean constants, no optimization possible
		defaultOptimizing(exp);

	}

	private void optimizeImpliesExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeImpliesExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "implies" is a binary expression
		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 1: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			//			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			//			boolean rightValue = ((ExpConstBoolean)optRight).value();

			Expression newArgs[] = {optLeft,optRight};
			try {
				Expression expRes = ExpStdOp.create("implies", newArgs);
				optExp = expRes;
				//					optExp = new ExpConstBoolean(leftValue && rightValue);
				return;		
			} catch (ExpInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//		// Optimization 2: only one operator is a boolean constant
		//		if (leftIsConstant || rightIsConstant) {
		//			// Assume the constant is the leftmost subexpression
		//			// Otherwise swap them
		//			if (rightIsConstant) { 
		//				Expression aux = optLeft;
		//				optLeft = optRight;
		//				optRight = aux;
		//			}
		//			boolean leftValue = ((ExpConstBoolean)optLeft).value();
		//			if (leftValue) {
		//				// Result is always the rightmost operator
		//				optExp = optRight;
		//			} else {
		//				// Result is always false
		//				optExp = optLeft;
		//			}
		//		} 


		// Optimization 3: some value is undefined / invalid	

		// Otherwise: no boolean constants, no optimization possible
		defaultOptimizing(exp);


	}
	// Nuevo
	private void optimizeStdOp(ExpStdOp exp, String opName) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeGreater");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: ">" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		if (debOptMet) System.out.println("left ["+left+"]");
		if (debOptMet) System.out.println("right ["+right+"]");

		if (left.toString().equals("(self.age - 1)")) {
			if (debOptMet) System.out.println("falla left ["+left+"]");
		}

		// ojo porque para left [(self.age - 1)] optLeft será null
		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		if (debOptMet) System.out.println("optLeft ["+optLeft+"]");
		if (debOptMet) System.out.println("optRight ["+optRight+"]");

		if (!optLeft.equals(left)||!optRight.equals(right)) {
			Expression newArgs[] = {optLeft,optRight};
			try {
				Expression exprRes = ExpStdOp.create(opName, newArgs);
				optExp = exprRes;
				return;
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}


		defaultOptimizing(exp);
		return;
	}

	//	private void optimizeGreater(ExpStdOp exp) {
	//		if (debOptMet) System.out.println("OptimizationVisitor optimizeGreater");
	//
	//		Expression[] args = exp.args();
	//
	//		// Retrieve subexpressions
	//		// Sanity check: ">" is a binary expression
	//		assert(args.length == 2);
	//		Expression left   = args[0];
	//		Expression right  = args[1];
	//
	//		Expression optLeft  = optimize(left);
	//		Expression optRight = optimize(right);
	//
	//		if (!optLeft.equals(left)||!optRight.equals(right)) {
	//			Expression newArgs[] = {optLeft,optRight};
	//			try {
	//				Expression exprRes = ExpStdOp.create(">", newArgs);
	//				optExp = exprRes;
	//				return;
	//			} catch (ExpInvalidException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//
	//		defaultOptimizing(exp);
	//		return;
	//	}

	private void optimizeAddExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeAddExp");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "AddExpr" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		// Solo se puede usar si es: ExpConstInteger, ExpConstReal o ExpConstUnlimitedNatural

		// ExpConstInteger ---------------------------------------------------------------
		boolean leftIsConstant  = optLeft instanceof ExpConstInteger;
		boolean rightIsConstant = optRight instanceof ExpConstInteger;
		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: only one operator is a boolean constant
		if (leftIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			if (leftValue == 0) {
				optExp = optRight;
				return;
			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue + rightValue);
			return;
		}


		//-------------------------------------------------------------------------------------

		// Hacer lo mismo para reales y para unlimited naturals

		// ExpConstReal ---------------------------------------------------------------
		leftIsConstant  = optLeft instanceof ExpConstReal;
		rightIsConstant = optRight instanceof ExpConstReal;
		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: only one operator is a boolean constant
		if (leftIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			if (leftValue == 0) {
				optExp = optRight;
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue + rightValue);
			return;
		}

		// ExpConstUnlimitedNatural ---------------------------------------------------------------
		//		leftIsConstant  = optLeft instanceof ExpConstUnlimitedNatural;
		//		rightIsConstant = optRight instanceof ExpConstUnlimitedNatural;
		//		// Optimization 1: left and right are boolean constants
		//		if (leftIsConstant && rightIsConstant) {
		//			UnlimitedNaturalValue leftValue = ((UnlimitedNaturalValue)optLeft).value();
		//			UnlimitedNaturalValue rightValue = ((UnlimitedNaturalValue)optRight).value();
		//			optExp = new ExpConstReal(leftValue + rightValue);
		//			return;
		//		}
		//
		//		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		//		// Optimization 2: only one operator is a boolean constant
		//		if (leftIsConstant) {
		//			double leftValue = ((ExpConstReal)optLeft).value();
		//			if (leftValue == 0) {
		//				optExp = optRight;
		//				return;
		//			}
		//		}
		//		if (rightIsConstant) {
		//			double rightValue = ((ExpConstReal)optRight).value();
		//			if (rightValue == 0) {
		//				optExp = optLeft;
		//				return;
		//			}
		//		}

		defaultOptimizing(exp);

	}

	private void optimizeSubsExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeSubsExp");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "SubsExpr" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		// Solo se puede usar si es: ExpConstInteger, ExpConstReal o ExpConstUnlimitedNatural

		// ExpConstInteger ---------------------------------------------------------------
		boolean leftIsConstant  = optLeft instanceof ExpConstInteger;
		boolean rightIsConstant = optRight instanceof ExpConstInteger;
		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: only one operator is a boolean constant
		if (leftIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			if (leftValue == 0) {
				// Si el operador de la izquierda es 0, hay que crear expresión para que el resultado sea el valor de la derecha en negativo
				Expression newArgsOr[] = {optRight};
				try {
					Expression expRes = ExpStdOp.create("-", newArgsOr);
					optExp = expRes;
					return;
				}catch (ExpInvalidException e) {
					e.printStackTrace();	
				}

			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue - rightValue);
			return;
		}


		//-------------------------------------------------------------------------------------
		// Hacer lo mismo para reales y para unlimited naturals

		// ExpConstReal ---------------------------------------------------------------
		leftIsConstant  = optLeft instanceof ExpConstReal;
		rightIsConstant = optRight instanceof ExpConstReal;
		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: only one operator is a boolean constant
		if (leftIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			if (leftValue == 0) {
				// Si el operador de la izquierda es 0, hay que crear expresión para que el resultado sea el valor de la derecha en negativo
				Expression newArgsOr[] = {optRight};
				try {
					Expression expRes = ExpStdOp.create("-", newArgsOr);
					optExp = expRes;
					return;
				}catch (ExpInvalidException e) {
					e.printStackTrace();	
				}

			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}		

		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstReal(leftValue - rightValue);
			return;
		}
		optExp =exp;
	}

	private void optimizeMulExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeMulExp");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "MulExpr" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstInteger;
		boolean rightIsConstant = optRight instanceof ExpConstInteger;
		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: only one operator is a boolean constant
		if (leftIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			if (leftValue == 0) {
				optExp = optRight;
				return;
			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue * rightValue);
			return;
		}

		//-------------------------------------------------------------------------------------
		// Hacer lo mismo para reales y para unlimited naturals

		// ExpConstReal ---------------------------------------------------------------
		leftIsConstant  = optLeft instanceof ExpConstReal;
		rightIsConstant = optRight instanceof ExpConstReal;
		// Optimization 1: left and right are boolean constants

		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 2: only one operator is a boolean constant
		if (leftIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			if (leftValue == 0) {
				optExp = optRight;
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				return;
			}
		}
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue * rightValue);
			return;
		}

	}

	private void optimizeDivExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeDivExp");
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "DivExpr" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right  = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		boolean leftIsConstant  = optLeft instanceof ExpConstInteger;
		boolean rightIsConstant = optRight instanceof ExpConstInteger;


		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: Si izquierda es 0, el resultado es 0
		if (leftIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			if (leftValue == 0) {
				//				optExp = optRight;
				optExp = new ExpConstInteger(0);
				return;
			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			// Si derecha es 0, el resultado es infinito
			if (rightValue == 0) {
				//				optExp = optLeft;
				//				optExp = new ExpConstInteger((Integer) null);
				optExp = new ExpConstInteger(0);
				return;
			}
		}
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();

			double leftValueReal = ((ExpConstInteger)optLeft).value();
			double rightValueReal = ((ExpConstInteger)optRight).value();			

			// Averiguar si el resultado es integer o real
			if (leftValue % rightValue == 0) {
				optExp = new ExpConstInteger(leftValue / rightValue);
			} else {
				optExp = new ExpConstReal(leftValueReal / rightValueReal);
			}
			return;
		}		
		//-------------------------------------------------------------------------------------
		// Hacer lo mismo para reales y para unlimited naturals

		// ExpConstReal ---------------------------------------------------------------
		leftIsConstant  = optLeft instanceof ExpConstReal;
		rightIsConstant = optRight instanceof ExpConstReal;


		// Si uno de los 2 es '0' se puede optimizar sustituyendo la suma por solamente el operador que no es 0
		// Optimization 1: Si izquierda es 0, el resultado es 0
		if (leftIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			if (leftValue == 0) {
				//				optExp = optRight;
				optExp = new ExpConstReal(0);
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			// Si derecha es 0, el resultado es infinito
			if (rightValue == 0) {
				//				optExp = optLeft;
				optExp = new ExpConstReal(null);
				return;
			}
		}

		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue / rightValue);
			return;
		}
		defaultOptimizing(exp);
	}	


	private void optimizeNotExp(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeNotExp");

		//		optExp = exp;
		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "not" is a unary expression
		assert(args.length == 1);
		Expression subexp  = args[0];

		Expression optSubExp = optimize(subexp);

		// Optimization 1: constant value
		if (optSubExp instanceof ExpConstBoolean) {
			boolean value = ((ExpConstBoolean)optSubExp).value();
			optExp = new ExpConstBoolean(!value);
			return;
		}

		// Optimization 2: undefined or invalid value
		// Aqui devolver undefined si es undefined
		// Optimization 3: query is undefined
		if (optSubExp instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			return;
		}	

		// Otherwise, no optimization is possible
		Expression newArgs[] = {optSubExp};
		try{ 
			Expression opt = ExpStdOp.create("not", newArgs);
			optExp = opt;
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}
		defaultOptimizing(exp);
	}


	@Override
	public void visitStdOp(ExpStdOp exp) {
		// Place-holder for operations returning a boolean value
		// Boolean: or, xor, and, not, implies
		// Collection operations: isEmpty, notEmpty, includes, excludes, includesAll, excludesAll
		// Relational: <=, >=, <, >, =, <>
		String opName = exp.opname();
		switch(opName) {
		case "or":
			optimizeOrExp(exp);
			break;	
		case "xor":
			optimizeXorExp(exp); 
			break;
		case "and":
			optimizeAndExp(exp);
			break;
		case "not":
			optimizeNotExp(exp);
			break;	
		case "implies":
			optimizeImpliesExp(exp);
			break;	
		case "=":
			optimizeStdOp(exp, opName);
			//			optExp = exp;
			break;	
		case "<=":
			optimizeStdOp(exp, opName);
			//			optExp = exp; 
			break;	
		case ">=":
			optimizeStdOp(exp, opName);
			//			optExp = exp;
			break;	
		case "<":
			optimizeStdOp(exp, opName);
			//			optExp = exp;
			break;	
		case ">":
			//			optimizeGreater(exp);//OJO
			optimizeStdOp(exp, opName);
			//			optExp = exp;
			break;	
		case "<>":
			optimizeStdOp(exp, opName);
			//			optExp = exp; 
			break;	
		case "isEmpty":
			defaultOptimizing(exp);
			break;	
		case "notEmpty":
			defaultOptimizing(exp);
			break;	
		case "includes":
			defaultOptimizing(exp);
			break;	
		case "excludes":
			defaultOptimizing(exp);
			break;	
		case "includesAll":
			defaultOptimizing(exp);
			break;	
		case "excludesAll":
			defaultOptimizing(exp);
			break;	
		case "+":
			optimizeAddExp(exp);
			break;	
		case "-":
			optimizeSubsExp(exp);
			break;	
		case "*":
			optimizeMulExp(exp);
			break;	
		case "/":
			optimizeDivExp(exp);
			break;	
		default:
			wrongTypeError("unsupported operation type '" + opName + "'");
		}
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitTupleSelectOp");
		// If the tuple is defined as a constant value, we can replace the selection with the value
		defaultOptimizing(exp);
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitVariable");
		defaultOptimizing(exp);
	}

	@Override
	public void visitClosure(ExpClosure exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitClosure");
		defaultOptimizing(exp);
	}

	@Override
	public void visitOclInState(ExpOclInState exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitOclInState");
		defaultOptimizing(exp);
	}

	@Override
	public void visitObjectByUseId(ExpObjectByUseId exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitObjectByUseId");
		defaultOptimizing(exp);
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitSelectByKind");
		defaultOptimizing(exp);
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitExpSelectByType");
		defaultOptimizing(exp);
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitNavigationClassifierSource");
		defaultOptimizing(exp);
	}

}