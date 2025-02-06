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
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.UnlimitedNaturalValue;

public class OptimizationVisitor extends BooleanVisitor {

	private boolean debOptMet = false;
	private boolean debShowTrace = false;

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

	private void defaultOptimizing(Expression exp) {
		optExp = exp;
	}

	private void showTrace(String method, Expression exp,Expression optExp) {
		System.out.println("(** show trace ["+method+"])->["+exp.toString()+"] ["+optExp.toString()+"]");
	}

	// The following expressions call defaultOp	timizing
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
		//		Expression range = exp.getRangeExpression(); // No se puede obtener range
		defaultOptimizing(exp);
	}	

	//(RC): si es coleccion vacia devuelves coleccion vacia y si no devuelves lo que hay
	public void visitCollect(ExpCollect exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitCollect");

		Expression range = exp.getRangeExpression();

		// Optimization 1: if it is empty collection return empty collection
		if (range instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			try {
				ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
				optExp = new ExpEmptyCollection(collectionExpr.type());
				if (debShowTrace) showTrace("visitCollect",exp,optExp);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			return;
		}
		// Optimization 2: If the collection is not empty, return what is there
		defaultOptimizing(exp);
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
				if (debShowTrace) showTrace("visitCollectNested",exp,optExp);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
			return;
		}
		// Optimization 2: If the collection is not empty, return whatdefaultOptimizing(exp);optExp = exp;
		defaultOptimizing(exp);
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
					if (debShowTrace) showTrace("visitReject",exp,optExp);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is false
				optExp = range;
				if (debShowTrace) showTrace("visitReject",exp,optExp);
				return;
			}
		}
		defaultOptimizing(exp);
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
				if (debShowTrace) showTrace("visitSelect",exp,optExp);
				return;
			} else {
				// If it is true you are left with an empty set
				try {
					//					Expression args[] = {range};
					//					optExp = ExpStdOp.create("notEmpty", args);
					ExpEmptyCollection collectionExpr = (ExpEmptyCollection) range;
					optExp = new ExpEmptyCollection(collectionExpr.type());
					if (debShowTrace) showTrace("visitSelect",exp,optExp);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			}
		}
		defaultOptimizing(exp);
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

	// FALTAN
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
		Expression rangeOpt = optimize(range);

		// Optimization 1: source collection is known to be empty
		if (rangeOpt instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("visitExists",exp,optExp);
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
					Expression args[] = {rangeOpt};
					optExp = ExpStdOp.create("isEmpty", args);
					if (debShowTrace) showTrace("visitExists",exp,optExp);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is true
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("visitExists",exp,optExp);
				return;
			}
		}

		// Optimization 3: query is undefined
		if (queryOpt instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			if (debShowTrace) showTrace("visitExists",exp,optExp);
			return;
		}		

		// Optimization 4: query is invalid
		//		NO HACER porque es invalid
		//	    }

		// Optimization 5: source collection is undefined
		if (rangeOpt instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			if (debShowTrace) showTrace("visitExists",exp,optExp);
			return;				

		}
		// Optimization 6: source collection is invalid

		// Otherwise - no optimization is possible
		//		defaultOptimizing(exp);


		try {
			VarDeclList v = exp.getVariableDeclarations();
			optExp = new ExpExists(v, rangeOpt, queryOpt);
		} catch (ExpInvalidException e) {

			e.printStackTrace();
		}



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
			if (debShowTrace) showTrace("visitForAll",exp,optExp);
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
					if (debShowTrace) showTrace("visitForAll",exp,optExp);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
				return;
			} else {
				// If the query is false, the answer is true
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("visitForAll",exp,optExp);
				return;
			}
		}else 
			// Optimization 3: query is undefined
			if (queryOpt instanceof ExpUndefined) {
				if (debOptMet) System.out.println("undefined");
				optExp = new ExpUndefined();
				if (debShowTrace) showTrace("visitForAll",exp,optExp);
				return;
			}		

		// Optimization 4: query is invalid
		//		NO HACER porque es invalid
		//	    }

		// Optimization 5: source collection is undefined
		if (range instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			if (debShowTrace) showTrace("visitForAll",exp,optExp);
			return;				

		}
		// Optimization 6: source collection is invalid

		// Otherwise - no optimization is possible
		defaultOptimizing(exp);
	}

	@Override
	public void visitIf(ExpIf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIf");
		Expression cond = exp.getCondition();
		Expression thenExp = exp.getThenExpression();
		Expression elseExp = exp.getElseExpression();

		//		Expression optCond  = optimize(cond);
		//		Expression optThen  = optimize(thenExp);
		//		Expression optElse = optimize(elseExp);

		//		boolean condIsConstant  = optCond instanceof ExpConstBoolean;
		//		boolean thenIsConstant  = optThen instanceof ExpConstBoolean;
		//		boolean elseIsConstant = optElse instanceof ExpConstBoolean;

		// Si then es igual que else devolver una cualquiera

		if (thenExp.toString().equals(elseExp.toString())) {
			optExp = thenExp;
			if (debShowTrace) showTrace("visitIf",exp,optExp);
			return;
		}

		//		if (condIsConstant) {
		//			optExp = thenExp;
		//			return;
		//		} else {
		//			optExp = elseExp;
		//			return;
		//		}


		// RC: si cierta te quedas con hijo izquierdo sino hijo derecho
		//boolean condValue = ((ExpConstBoolean)cond).value(); // PROVIS
		//		Falla: 
		// class org.tzi.use.uml.ocl.expr.ExpStdOp cannot be cast to class org.tzi.use.uml.ocl.expr.ExpConstBoolean (org.tzi.use.uml.ocl.expr.ExpStdOp
		//		boolean queryValue=true;// PROVIS
		//
		//		if (condValue) {
		//			optExp = thenExp;
		//			return;
		//		} else {
		//			optExp = elseExp;
		//			return;
		//		}		
		defaultOptimizing(exp);

	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIsKindOf");
		// Determinar si la expresion es constante de cualquier tipo

		Type expType = exp.type();
		if (expType.isTypeOfBoolean()||expType.isTypeOfReal()||expType.isTypeOfUnlimitedNatural()) {
			Expression target = exp.getSourceExpr();
			Type nameType = exp.getTargetType();
			//			if (!target.type().conformsTo(nameType) ) {// Inicialmente estaba asi pero lo cambio a ver si coincido con Robert
			if (!nameType.conformsTo(target.type()) ) {
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("visitIsKindOf",exp,optExp);
				return;
			}		
		}

		defaultOptimizing(exp);

	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		if (debOptMet) System.out.println("OptimizationVisitor visitIsTypeOf");
		// Determinar si la expresion es constante de cualquier tipo

		Type expType = exp.type();
		if (expType.isTypeOfBoolean()||expType.isTypeOfReal()||expType.isTypeOfUnlimitedNatural()) {
			Expression target = exp.getSourceExpr();
			Type nameType = exp.getTargetType();
			if (!nameType.conformsTo(target.type()) || !target.type().conformsTo(nameType) ) {
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("visitIsTypeOf",exp,optExp);
				return;
			}
		}
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

	// Aqui
	public boolean isKnownNotEmpty(Expression exp) {
		//		Expression[] args = exp.args();
		//		Expression col  = args[0];

		boolean bRes=false;
		// Averiguar si la expresión es de tipo coleccion

		// Definir una nueva función isKnownNotEmpty(), que dada una expresión de tipo "collection" 
		// devuelva cierto si la expresión sabemos que no está vacía.
		if (exp instanceof ExpEmptyCollection) {
			return bRes;
		}else {

			if (exp instanceof ExpCollect) {
				//				Es una expresión del tipo C1->collect() y sabemos que C1 no está vacía.???????
				return bRes;
			}
			//  Por ejemplo, sabemos que una expresión no está vacía si:
			if (exp instanceof ExpStdOp) {
				ExpStdOp expStd = (ExpStdOp) exp;
				Expression[] args = expStd.args();
				String opName = expStd.opname();
				Expression left  = args[0];
				Expression right  = args[1];

				//	Es una expresión del tipo COLECCION->including(X)
				if (opName.equals("including")) {
					System.out.println("show Including ["+exp.toString()+"]");
					if (exp.toString().equals("self.orderLine->including(ol1)")) {
						System.out.println("Aqui");
					}
					bRes=true;
					return bRes;
				}
				//	Es una expresión del tipo C1->includingAll(C2) y o bien C1 o C2 no están vacías (podemos saberlo haciendo una llamada recursiva de isKnownNotEmpty sobre C1 y C2).
				if (opName.equals("includingAll")) {
					boolean leftNotEmpty = isKnownNotEmpty(left);
					boolean rightNotEmpty = isKnownNotEmpty(right);
					bRes = leftNotEmpty || rightNotEmpty;
					return bRes;
				}
			}

			boolean leftIsConstantBoolean  = exp instanceof ExpConstBoolean;
			boolean leftIsConstantReal  = exp instanceof ExpConstReal;
			if (leftIsConstantBoolean || leftIsConstantReal) {
				//	Es una expresión de tipo constante, como Set{1,2} y sabemos que no está vacía.		
				bRes=true;
			}



			//	Podemos ver si hay alguna otra expresión más que debamos considerar.
			//			bRes=true;
			return bRes;
		}
	}

	// Crear metodo para operaciones binarias y unarias
	private Expression optimizeOpBinary(Expression optLeft, Expression optRight, String opName) {
		Expression opt= null;
		try {
			Expression argsNew[] = {optLeft,optRight};
			opt = ExpStdOp.create(opName, argsNew);
			return opt;
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}
		return opt;
	}
	private Expression optimizeOpUnary(Expression optExpUni, String opName) {
		Expression opt= null;
		try {
			Expression argsNew[] = {optExpUni};
			opt = ExpStdOp.create(opName, argsNew);
			return opt;
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}
		return opt;
	}


	private void optimizeIsEmpty(Expression exp) {
		//si empty devolver cierto

		Expression[] args = ((ExpStdOp) exp).args();
		Expression col  = args[0];
		Expression optCol  = optimize(col);

		// Preguntar si optExp es vacia
		if ((optCol instanceof ExpEmptyCollection)) {
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeIsEmpty",exp,optExp);
			return;	
		}
		//	if (exp.toString().equals("self.orderLine->including(ol1)->isEmpty")) {
		//		System.out.println("Aqui");
		//	}
		// Llamar esta operación en los VisitIsEmpty() y VisitNotEmpty(), y en caso que devuelva cierto optimizar la operación
		boolean resKnownNotEmpty = isKnownNotEmpty(optCol);

		//		if (exp.toString().equals("self.orderLine->including(ol1)->isEmpty")) {
		//			System.out.println("Aqui");
		//			if (!resKnownNotEmpty) {
		//				System.out.println("Aqui");
		//			}
		//		}

		if (resKnownNotEmpty) {
			optExp = new ExpConstBoolean(false);
			if (debShowTrace) showTrace("optimizeIsEmpty",exp,optExp);
			return;
		}else {
			// Aqui unary
			//			defaultOptimizing(exp);

			Expression expRes = optimizeOpUnary(optCol, "isEmpty");
			optExp= expRes;
			if (debShowTrace) showTrace("optimizeIsEmpty",exp,optExp);
			return;
		}

	}

	private void optimizeIsNotEmpty(ExpStdOp exp) {
		//si not empty devolver falso

		Expression[] args = ((ExpStdOp) exp).args();
		Expression col  = args[0];
		Expression optCol  = optimize(col);

		// Preguntar si optExp es vacia
		if ((optCol instanceof ExpEmptyCollection)) {
			optExp = new ExpConstBoolean(false);
			if (debShowTrace) showTrace("optimizeIsNotEmpty",exp,optExp);
			return;	
		}
		// Llamar esta operación en los VisitIsEmpty() y VisitNotEmpty(), y en caso que devuelva cierto optimizar la operación
		boolean resKnownNotEmpty = isKnownNotEmpty(optCol);

		if (resKnownNotEmpty) {
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeIsNotEmpty",exp,optExp);
			return;
		}else {
			defaultOptimizing(exp);
			//			Expression expRes = optimizeOpUnary(optCol, "notIsEmpty");
			//			optExp= expRes;
			//			if (debShowTrace) showTrace("optimizeIsNotEmpty",exp,optExp);
			//			return;
		}
	}

	private void optimizeIncludes(ExpStdOp exp) {
		//si col1 es vacio->falso
		//sino mantener lo que hay

		Expression[] args = exp.args();

		assert(args.length == 2);
		Expression left  = args[0];
		//		Expression right = args[1];

		Expression optLeft  = optimize(left);
		//		Expression optRight = optimize(right);

		if (optLeft instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			optExp = new ExpConstBoolean(false);
			if (debShowTrace) showTrace("optimizeIncludes",exp,optExp);
			return;
		}else {
			defaultOptimizing(exp);
		}
	}

	private void optimizeIncludesAll(ExpStdOp exp) {
		//		si col2 es vac�o -> cierto
		//		sino, mirar la primera
		//		si es vacia devolver falso
		//		sino es ni uno ni otro nos quedamos con lo que hay

		Expression[] args = exp.args();

		assert(args.length == 2);
		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		if (optRight instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection 2 is empty");
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeIncludesAll",exp,optExp);
			return;
		}else {
			if (optLeft instanceof ExpEmptyCollection) {
				if (debOptMet) System.out.println("Collection 1 is empty");
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("optimizeIncludesAll",exp,optExp);
				return;
			}
		}
		defaultOptimizing(exp);
	}	

	private void optimizeExcludes(ExpStdOp exp) {
		//		col1.excludes( X) 
		//		si col1 es vac�o -> cierto

		Expression[] args = exp.args();
		Expression left  = args[0];
		//		Expression right = args[1];

		Expression optLeft  = optimize(left);
		//		Expression optRight = optimize(right);

		if (optLeft instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection is empty");
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeExcludes",exp,optExp);
			return;
		}else {
			defaultOptimizing(exp);
		}
	}

	private void optimizeExcludesAll(ExpStdOp exp) {
		//		col1.excludesAll( col2 )
		//		si col1 es vac�o -> cierto
		//		si col2 es vac�o -> falso

		Expression[] args = exp.args();

		Expression left  = args[0];
		Expression right = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight = optimize(right);

		if (optLeft instanceof ExpEmptyCollection) {
			if (debOptMet) System.out.println("Collection 1 is empty");
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeExcludesAll",exp,optExp);
			return;
		}else {
			if (optRight instanceof ExpEmptyCollection) {
				if (debOptMet) System.out.println("Collection 2 is empty");
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("optimizeExcludesAll",exp,optExp);
				return;
			}
		}
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

		// Optimization 1: If the 2 expressions are equal we take and return only one

		if (optLeft.toString().equals(optRight.toString())) {
			optExp = optLeft;
			if (debShowTrace) showTrace("optimizeOrExp",exp,optExp);
			return;
		}

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue || rightValue);
			if (debShowTrace) showTrace("optimizeOrExp",exp,optExp);
			return;
		}

		// Optimization 3: only one operator is a boolean constant
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
				if (debShowTrace) showTrace("optimizeOrExp",exp,optExp);
				return;
			} else {
				// Result is always the rightmost operator
				optExp = optRight;
				if (debShowTrace) showTrace("optimizeOrExp",exp,optExp);
				return;
			}
		} 

		// Optimization 4: We optimize the children	

		Expression expRes = optimizeOpBinary(optLeft, optRight, "or");
		optExp= expRes;

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

		// Optimization 1: If the 2 expressions are equal we take and return only one

		if (optLeft.toString().equals(optRight.toString())) {
			optExp = optLeft;
			if (debShowTrace) showTrace("optimizeAndExp",exp,optExp);
			return;
		}

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue && rightValue);
			if (debShowTrace) showTrace("optimizeAndExp",exp,optExp);
			return;
		}

		// Optimization 3: only one operator is a boolean constant
		if (leftIsConstant || rightIsConstant) {
//			System.out.println("Aqui optimizeAndExp");
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
				if (debShowTrace) showTrace("optimizeAndExp",exp,optExp);
				return;
			} else {
				// Result is always false
				optExp = new ExpConstBoolean(false);
				if (debShowTrace) showTrace("optimizeAndExp",exp,optExp);
				return;
			}
		} 


		// Optimization 4: Optimization 4: We optimize the children	

		Expression expRes = optimizeOpBinary(optLeft, optRight, "and");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeAndExp",exp,optExp);
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

		// Optimization 1: If the 2 expressions are equal we return false

		if (optLeft.toString().equals(optRight.toString())) {
			optExp = new ExpConstBoolean(false);
			if (debShowTrace) showTrace("optimizeXorExp",exp,optExp);
			return;
		}

		boolean leftIsConstant  = optLeft instanceof ExpConstBoolean;
		boolean rightIsConstant = optRight instanceof ExpConstBoolean;
		// Optimization 1: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			boolean leftValue = ((ExpConstBoolean)optLeft).value();
			boolean rightValue = ((ExpConstBoolean)optRight).value();
			optExp = new ExpConstBoolean(leftValue ^ rightValue);
			if (debShowTrace) showTrace("optimizeXorExp",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeXorExp",exp,optExp);
				return;
			} else {
				// Result is always the rightmost operator
				optExp = optRight;
				if (debShowTrace) showTrace("optimizeXorExp",exp,optExp);
				return;
			}
		} 

		// Optimization 4: Optimization 4: We optimize the children	

		Expression expRes = optimizeOpBinary(optLeft, optRight, "xor");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeXorExp",exp,optExp);

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

		//		X = cierto: "X implies Y" se simplifca como "Y"
		if (leftIsConstant && ((ExpConstBoolean) optLeft).value()) {
			optExp = optRight;
			if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);
			return;	
		}	
		//		X = false: "X implies Y" se simplifica como cierto
		if (leftIsConstant && !((ExpConstBoolean) optLeft).value()) {
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);
			return;	
		}
		//		Y = cierto: "X implies Y" se simplifica como cierto
		if (rightIsConstant && ((ExpConstBoolean) optRight).value()) {
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);
			return;	
		}	
		//		Y = false: "X implies Y" se simplifica como "no()X"
		if (rightIsConstant && !((ExpConstBoolean) optRight).value()) {
			try {
				Expression newArgsOr[] = {optRight};
				Expression opt;
				opt = ExpStdOp.create("not", newArgsOr);
				optExp = opt;
				if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);
				return;	
			} catch (ExpInvalidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		//		Para cualquier valor donde X = Y: "X implies Y" se simplifica como cierto
		if (optLeft.toString().equals(optRight.toString())) {
			optExp = new ExpConstBoolean(true);
			if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);
			return;	
		}
		//		defaultOptimizing(exp);

		// Optimization 4: Optimization 4: We optimize the children	

		Expression expRes = optimizeOpBinary(optLeft, optRight, "implies");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeImpliesExp",exp,optExp);


	}

	private void optimizeSum(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeSum");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "sum" is a unary expression
		assert(args.length == 1);
		Expression left   = args[0];

		Expression optLeft  = optimize(left);

		if (optLeft instanceof ExpEmptyCollection) {
			optExp = new ExpConstInteger(0);
			if (debShowTrace) showTrace("optimizeSum",exp,optExp);
			return;
		}

		try {
			Expression argsNew[] = {optLeft};
			optExp = ExpStdOp.create("sum", argsNew);
			if (debShowTrace) showTrace("optimizeSum",exp,optExp);
			return;
		} catch (ExpInvalidException e) {
			e.printStackTrace();
		}
	}

	private void optimizeSize(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeSum");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "sum" is a unary expression
		assert(args.length == 1);
		Expression left   = args[0];

		Expression optLeft  = optimize(left);

		if (optLeft instanceof ExpEmptyCollection) {
			optExp = new ExpConstInteger(0);
			if (debShowTrace) showTrace("optimizeSize",exp,optExp);
			return;
		}

		//		try {
		//			Expression argsNew[] = {optLeft};
		//			optExp = ExpStdOp.create("size", argsNew);
		//			if (debShowTrace) showTrace("optimizeSize",exp,optExp);
		//			return;
		//		} catch (ExpInvalidException e) {
		//			e.printStackTrace();
		//		}


		Expression expRes = optimizeOpUnary(optLeft, "size");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeSize",exp,optExp);

	}

	private void optimizeIncluding(ExpStdOp exp) {
		if (debOptMet) System.out.println("OptimizationVisitor optimizeIncluding");

		Expression[] args = exp.args();

		// Retrieve subexpressions
		// Sanity check: "including" is a binary expression
		assert(args.length == 2);
		Expression left   = args[0];
		Expression right   = args[1];

		Expression optLeft  = optimize(left);
		Expression optRight  = optimize(right);

		if (optLeft instanceof ExpEmptyCollection) {
			// Se podria incluir esta implementacion (OJO: deberia ser esto)
			//		Set{}->including(1)
			//		====>
			//		Set{1}
			try {
				Expression argsNew[] = {optLeft,optRight};
				optExp = ExpStdOp.create("including", argsNew);
				if (debShowTrace) showTrace("optimizeIncluding",exp,optExp);
				return;
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		//		try {
		//			Expression argsNew[] = {optLeft,optRight};
		//			optExp = ExpStdOp.create("including", argsNew);
		//			if (debShowTrace) showTrace("optimizeIncluding",exp,optExp);
		//			return;
		//		} catch (ExpInvalidException e) {
		//			e.printStackTrace();
		//		}

		// Optimization 4: We optimize the children	

		Expression expRes = optimizeOpBinary(optLeft, optRight, "including");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeIncluding",exp,optExp);

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
				if (debShowTrace) showTrace("optimizeStdOp op->"+opName+" ",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
				return;
			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue + rightValue);
			if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue + rightValue);
			if (debShowTrace) showTrace("optimizeAddExp",exp,optExp);
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
					if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
				return;
			}
		}		
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue - rightValue);
			if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
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
					if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
				return;
			}
		}		

		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstReal(leftValue - rightValue);
			if (debShowTrace) showTrace("optimizeSubsExp",exp,optExp);
			return;
		}
		defaultOptimizing(exp);
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
				if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
				return;
			}
		}
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
				return;
			}
		}
		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			int leftValue = ((ExpConstInteger)optLeft).value();
			int rightValue = ((ExpConstInteger)optRight).value();
			optExp = new ExpConstInteger(leftValue * rightValue);
			if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
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
				if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			if (rightValue == 0) {
				optExp = optLeft;
				if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
				return;
			}
		}
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue * rightValue);
			if (debShowTrace) showTrace("optimizeMulExp",exp,optExp);
			return;
		}
		defaultOptimizing(exp);
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
				if (debShowTrace) showTrace("optimizeDivExp",exp,optExp);
				return;
			}
		}
		// Devolver unlimited.NATURAL
		if (rightIsConstant) {
			int rightValue = ((ExpConstInteger)optRight).value();
			// Si derecha es 0, el resultado es infinito
			if (rightValue == 0) {
				double unlimitedValue = ((UnlimitedNaturalValue) UnlimitedNaturalValue.UNLIMITED).value();
				optExp = new ExpConstReal(unlimitedValue);
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
			//			if (leftValue % rightValue == 0) {
			//				optExp = new ExpConstInteger(leftValue / rightValue);
			//			} else {
			optExp = new ExpConstReal(leftValueReal / rightValueReal);
			if (debShowTrace) showTrace("optimizeDivExp",exp,optExp);
			//			}
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
				if (debShowTrace) showTrace("optimizeDivExp",exp,optExp);
				return;
			}
		}
		if (rightIsConstant) {
			double rightValue = ((ExpConstReal)optRight).value();
			// Si derecha es 0, el resultado es infinito
			if (rightValue == 0) {
				//				optExp = optLeft;
				optExp = new ExpConstReal(null);
				if (debShowTrace) showTrace("optimizeDivExp",exp,optExp);
				return;
			}
		}

		// Optimization 2: left and right are boolean constants
		if (leftIsConstant && rightIsConstant) {
			double leftValue = ((ExpConstReal)optLeft).value();
			double rightValue = ((ExpConstReal)optRight).value();
			optExp = new ExpConstReal(leftValue / rightValue);
			if (debShowTrace) showTrace("optimizeDivExp",exp,optExp);
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
			if (debShowTrace) showTrace("optimizeNotExp",exp,optExp);
			return;
		}

		// Optimization 2: undefined or invalid value
		// Aqui devolver undefined si es undefined
		// Optimization 3: query is undefined
		if (optSubExp instanceof ExpUndefined) {
			if (debOptMet) System.out.println("undefined");
			optExp = new ExpUndefined();
			if (debShowTrace) showTrace("optimizeNotExp",exp,optExp);
			return;
		}	

		// Otherwise, no optimization is possible
		//		Expression newArgs[] = {optSubExp};
		//		try{ 
		//			Expression opt = ExpStdOp.create("not", newArgs);
		//			optExp = opt;
		//			if (debShowTrace) showTrace("optimizeNotExp",exp,optExp);
		//			return;
		//		} catch (ExpInvalidException e) {
		//			e.printStackTrace();
		//		}

		// Optimization 4: Optimization 4: We optimize the children	

		Expression expRes = optimizeOpUnary(optSubExp, "not");
		optExp= expRes;
		if (debShowTrace) showTrace("optimizeNotExp",exp,optExp);

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
			break;	
		case ">=":
			optimizeStdOp(exp, opName);
			break;	
		case "<":
			optimizeStdOp(exp, opName);
			break;	
		case ">":
			//			optimizeGreater(exp);//OJO
			optimizeStdOp(exp, opName);
			break;	
		case "<>": // OJO Porque esta opName no se reconoce y nunca pasa por aqui con integer, boolean y real
			optimizeStdOp(exp, opName);
			break;	
		case "isEmpty":
			optimizeIsEmpty(exp);
			break;	
		case "notEmpty":
			optimizeIsNotEmpty(exp);
			break;	
		case "includes":
			optimizeIncludes(exp);
			break;	
		case "excludes":
			optimizeExcludes(exp);
			break;	
		case "includesAll":
			optimizeIncludesAll(exp);
			break;	
		case "excludesAll":
			optimizeExcludesAll(exp);
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
		case "including":
			optimizeIncluding(exp);
			break;
		case "size":
			optimizeSize(exp);
			break;		
		case "sum":
			optimizeSum(exp);
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
	