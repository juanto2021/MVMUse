package org.tzi.use.gui.mvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tzi.use.parser.SemanticException;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MModel;
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
import org.tzi.use.uml.ocl.expr.ExpressionVisitor;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.BagType;
import org.tzi.use.uml.ocl.type.SetType;
import org.tzi.use.uml.ocl.type.Type;

public class HullVisitor implements ExpressionVisitor {

	private static boolean debHullMet = false;
	private List<Expression> mutatedExpr;
	private static Map<String, List<VarDecl>> mapVarsByType;

	public HullVisitor(Map<String, List<VarDecl>> pMapVarsByType) {
		mutatedExpr = new LinkedList<Expression>();
		mapVarsByType= pMapVarsByType;
	}

	public List<Expression> getMutatedExpr() {
		return mutatedExpr;
	}

	private static List<Expression> strengthen(Expression exp, Map<String, List<VarDecl>> pMapVarsByType) {
		if (debHullMet) System.out.println("Hull - strengthen - exp ["+exp+"]");
		StrengthenVisitor vis = new StrengthenVisitor(pMapVarsByType);
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}
	private static List<Expression> weaken(Expression exp, Map<String, List<VarDecl>> pMapVarsByType) {
		if (debHullMet) System.out.println("Hull - weaken - exp ["+exp+"]");
		WeakenVisitor vis = new WeakenVisitor(pMapVarsByType);
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}
	public static List<Expression> hull(Expression exp) {
		if (debHullMet) System.out.println("Hull - hull - exp ["+exp+"]");
		// create empty map
		Map<String, List<VarDecl>> mapVarsByType= new HashMap<>(); 
		// call the second signature
		return hull(exp, mapVarsByType);
	}

	public static List<Expression> hull(Expression exp,Map<String, List<VarDecl>> pMapVarsByType) {
		if (debHullMet) System.out.println("Hull - exp ["+exp+"] map ["+pMapVarsByType+"]");
		HullVisitor vis = new HullVisitor(pMapVarsByType);
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}

	public void anyIncluding(Expression exp) {
		if (debHullMet) System.out.println("Hull - anyIncluing");
		// If it is a collection, search all the declarations and see if there are any with a suitable type
		// The type is appropriate if the type of the collection matches that of the variable itself existing in map
		// (note that in map there can be variables of different types)

		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			if (debHullMet) System.out.println("Hull - exp ["+exp+"] es collection ["+exp.type().shortName());
		}else {
			return;	
		}

		// Collection type
		Type typeCollection = exp.type();
		Type colType =null;
		if (typeCollection.isKindOfBag(Type.VoidHandling.EXCLUDE_VOID)) {
			BagType mc = (BagType) exp.type();
			colType = mc.elemType();
		}else if(typeCollection.isKindOfSet(Type.VoidHandling.EXCLUDE_VOID)) {
			SetType mc = (SetType) exp.type();
			colType = mc.elemType();
		}
		if (colType!=null) {
			if (colType instanceof MClass) {
				MClass mClass = (MClass) colType;
				MModel model = mClass.model();	
				MClass typeClass = model.getClass(mClass.name());

				// The declarations to use are those of 'map' - mapVarsByType
				List<VarDecl> decl = mapVarsByType.getOrDefault(typeClass.name(), new ArrayList<>());
// Prueba JG
				// Pendiente resolver problema 'including' con variables fuera de contexto (PRINCL)
//				for (VarDecl var : decl) {
//					Type varType = var.type();
//					if(varType.conformsTo(colType)) {
//						// See if the variable is the same type as the collection
//						String varName = var.name();
//						ExpVariable expVar = new ExpVariable(varName, mClass);
//						Expression newArgs[] = {exp, expVar};
//						try {
//							Expression mutantNew = ExpStdOp.create("including", newArgs);
//							mutatedExpr.add(mutantNew);
//						} catch (ExpInvalidException e) {
//							e.printStackTrace();
//						}	
//					}
//				}
			}
		}
	}

	private void defaultHull(Expression exp) {
		if (debHullMet) System.out.println("Hull - hull - defaultHull");
		// The default hull of an expression is return an set or sequence that is 'Type.AllInstances'
		Type type =null;
		Type typeExp = exp.type();
		if (typeExp.isKindOfBag(Type.VoidHandling.EXCLUDE_VOID)) {
			BagType mc = (BagType) exp.type();
			type = mc.elemType();
		}else if(typeExp.isKindOfSet(Type.VoidHandling.EXCLUDE_VOID)) {
			SetType mc = (SetType) exp.type();
			type = mc.elemType();
		}
		if (type!=null) {
			if (type instanceof MClass) {
				MClass mClass = (MClass) type;
				MModel model = mClass.model();	
				MClass typeClass = model.getClass(mClass.name());
				try {
					ExpAllInstances mutant = new ExpAllInstances(typeClass);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
			anyIncluding( exp);
		}
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

			if (debHullMet) System.out.println("mapVarsByType "+mapVarsByType);
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
		if (debHullMet) System.out.println("mapVarsByType "+mapVarsByType);
	}

	public void wrongTypeError() throws RuntimeException {
		wrongTypeError("");	
	}

	public void wrongTypeError(String type) throws RuntimeException {
		throw new RuntimeException("Visitor reached node with incorrect operation: " + type);	
	}

	public static Map<String, List<VarDecl>> createVarsByType(VarDeclList decl) {
		Map<String, List<VarDecl>> mapVarsByType = new HashMap<>();
		int nDecs = decl.size();
		for (int nDec=0;nDec<nDecs;nDec++) {
			VarDecl v = decl.varDecl(nDec); 
			List<VarDecl> varsByType = new ArrayList<>();
			varsByType.add(v);
			mapVarsByType.put(v.type().shortName(), varsByType);
		}
		return mapVarsByType;
	}

	public void visitIncluding(ExpAllInstances exp) {
		if (debHullMet) System.out.println("Hull - visitIncluding - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}
	
	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		if (debHullMet) System.out.println("Hull - visitAllInstances - exp ["+exp+"]");
		if (mapVarsByType!=null) {
			MClassifier cls = exp.getSourceType();
			for (Map.Entry<String, List<VarDecl>> entry : mapVarsByType.entrySet()) {
				String type = entry.getKey();
				if (debHullMet) System.out.println("Tipo: " + type+ " classifier ["+cls.name()+"]");
				// Pendiente resolver problema 'including' con variables fuera de contexto (PRINCL)
//				if (cls.name().equals(type)) {
//					List<VarDecl> vars = entry.getValue();
//					for (VarDecl var : vars) {
//						String varName = var.name();
//						ExpVariable expVar = new ExpVariable(varName, cls);
//						Expression newArgs[] = {exp, expVar};
//						try {
//							Expression mutantNew = ExpStdOp.create("including", newArgs);
//							mutatedExpr.add(mutantNew);
//						} catch (ExpInvalidException e) {
//							e.printStackTrace();
//						}
//
//					}
//				}
			}
		}

		if (debHullMet) System.out.println("hull - ExpAllInstances");
		defaultHull(exp);		

	}

	@Override
	public void visitAny(ExpAny exp) {
		if (debHullMet) System.out.println("Hull - visitAny - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);

	}

	@Override
	public void visitAsType(ExpAsType exp) {
		if (debHullMet) System.out.println("Hull - visitAsType - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		if (debHullMet) System.out.println("Hull - visitAttrOp - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		if (debHullMet) System.out.println("Hull - visitBagLiteral - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitCollect(ExpCollect exp) {
		if (debHullMet) System.out.println("Hull - ExpCollect - exp ["+exp+"]");

		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();

		// Include the declarations and their type in the map

		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		includeDecl(decl);
		// Mutation 1: Compute the hull of the collection (H4a)
		for(Expression hull: rangeHull) {
			try {
				for (VarDecl v: decl) {
					Expression newMutant = new ExpCollect(v,hull,query);
					mutatedExpr.add(newMutant);
				}

			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		if (debHullMet) System.out.println("collect");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)); defaultHull(exp);

		// Remove previously included decl elements from the map
		excludeDecl(decl);	

	}

	@Override
	public void visitCollectNested(ExpCollectNested exp) {
		if (debHullMet) System.out.println("Hull - visitCollectNested - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)); defaultHull(exp);
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		if (debHullMet) System.out.println("Hull - visitConstBoolean - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		if (debHullMet) System.out.println("Hull - visitConstEnum - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		if (debHullMet) System.out.println("Hull - visitConstInteger - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		if (debHullMet) System.out.println("Hull - visitConstReal - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		if (debHullMet) System.out.println("Hull - visitConstString - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitEmptyCollection(ExpEmptyCollection exp) {
		if (debHullMet) System.out.println("Hull - visitEmptyCollection - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)); defaultHull(exp);
	}

	@Override
	public void visitExists(ExpExists exp) {
		if (debHullMet) System.out.println("Hull - visitExists - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitForAll(ExpForAll exp) {
		if (debHullMet) System.out.println("Hull - visitForAll - exp ["+exp+"]");
		assert (false);	
	}

	@Override
	public void visitIf(ExpIf exp) {
		if (debHullMet) System.out.println("Hull - visitIf - exp ["+exp+"]");

		Expression cond = exp.getCondition();
		Expression thenExp = exp.getThenExpression();
		Expression elseExp = exp.getElseExpression();

		// Mutate each of the alternatives of the conditional
		List<Expression> thenMutants = hull(thenExp);
		List<Expression> elseMutants = hull(elseExp);

		// Mutation1: we mutate both branches of the conditiona (H12a)
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
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		if (debHullMet) System.out.println("Hull - visitIsKindOf - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		if (debHullMet) System.out.println("Hull - visitIsTypeOf - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		if (debHullMet) System.out.println("Hull - visitIsUnique - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		if (debHullMet) System.out.println("Hull - visitIterate - exp ["+exp+"]");
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitLet(ExpLet exp) {
		if (debHullMet) System.out.println("Hull - visitLet - exp ["+exp+"]");
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		if (debHullMet) System.out.println("Hull - visitNavigation - exp ["+exp+"]");
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		if (debHullMet) System.out.println("Hull - visitObjAsSet - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		if (debHullMet) System.out.println("Hull - visitObjOp - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
		if (debHullMet) System.out.println("Hull - visitObjRef - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitOne(ExpOne exp) {
		if (debHullMet) System.out.println("Hull - visitOne - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		if (debHullMet) System.out.println("Hull - visitOrderedSetLiteral - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		if (debHullMet) System.out.println("Hull - ExpQuery - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitReject(ExpReject exp) {
		if (debHullMet) System.out.println("Hull - visitReject - exp ["+exp+"]");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();


		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		// Include the declarations and their type in the map
		includeDecl(decl);

		List<Expression> queryMutants = strengthen(query, mapVarsByType);

		// Generate a new mutant for each mutant of the query expression (H3a)
		for (Expression mutant: queryMutants) {
			try {
				Expression newMutant = new ExpReject(decl,range,mutant);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the hull of the collection (H3b)
		for(Expression hull: rangeHull) {
			try {
				Expression newMutant = new ExpReject(decl,hull,query);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: do both (H3c)
		for(Expression mutant: queryMutants) {
			for(Expression hull: rangeHull) {
				try {
					Expression newMutant = new ExpReject(decl,hull,mutant);
					mutatedExpr.add(newMutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
		}

		if (debHullMet) System.out.println("reject");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);

		// Remove previously included decl elements from the map
		excludeDecl(decl);
	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {
		if (debHullMet) System.out.println("Hull - visitWithValue - exp ["+exp+"]");
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitSelect(ExpSelect exp) {
		if (debHullMet) System.out.println("Hull - visitSelect - exp ["+exp+"]");
		Expression query = exp.getQueryExpression();
		Expression range = exp.getRangeExpression();
		VarDeclList decl = exp.getVariableDeclarations();

		List<Expression> rangeHull = HullVisitor.hull(range,mapVarsByType);

		// Include the declarations and their type in the map
		includeDecl(decl);
		List<Expression> queryMutants = weaken(query, mapVarsByType);

		// Generate a new mutant for each mutant of the query expression (H2a)
		for (Expression mutant: queryMutants) {
			try {
				Expression newMutant = new ExpSelect(decl,range,mutant);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the hull of the collection (H2b)
		for(Expression hull: rangeHull) {
			try {
				Expression newMutant = new ExpSelect(decl,hull,query);
				mutatedExpr.add(newMutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: do both (H2c)
		for(Expression mutant: queryMutants) {
			for(Expression hull: rangeHull) {
				try {
					Expression newMutant = new ExpSelect(decl,hull,mutant);
					mutatedExpr.add(newMutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}	
			}
		}

		if (debHullMet) System.out.println("select");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);

		// Remove previously included decl elements from the map
		excludeDecl(decl);

	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {

		if (debHullMet) System.out.println("Hull - visitSequenceLiteral - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {

		if (debHullMet) System.out.println("Hull - visitSetLiteral - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
		if (debHullMet) System.out.println("Hull - visitSortedBy - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);	
	}

	private void mutateIsEmptyExp(ExpStdOp exp) {
		if (debHullMet) System.out.println("Hull - mutateIsEmptyExp - exp ["+exp+"]");
		assert(false);
	}

	private void mutateIncluding(ExpStdOp exp) {
		if (debHullMet) System.out.println("Hull - mutateIncluding - exp ["+exp+"]");

		Expression[] args = exp.args();
		assert(args.length == 2);
		Expression baseCol  = args[0];
		Expression otherCol = args[1];

		// Generate the hull of the baseCol
		List<Expression> baseColMutantsHull = HullVisitor.hull(baseCol);

		// Mutation 1: Compute the hull of the base collection (H5a) -- e1^->including(e2) --
		for(Expression mutantCol: baseColMutantsHull) {
			Expression newArgs[] = {mutantCol, otherCol};
			try {
				Expression mutant = ExpStdOp.create("including", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	private void mutateExcluding(ExpStdOp exp) {
		if (debHullMet) System.out.println("Hull - mutateExcluding - exp ["+exp+"]");

		Expression[] args = exp.args();
		assert(args.length == 2);
		Expression baseCol  = args[0];
		Expression otherCol = args[1];

		// Generate the hull of the baseCol
		List<Expression> baseColMutantsHull = HullVisitor.hull(baseCol);

		// Mutation 1: Compute the hull of the base collection (H6a) -- e1^->excluding(e2) --
		for(Expression mutantCol: baseColMutantsHull) {
			Expression newArgs[] = {mutantCol, otherCol};
			try {
				Expression mutant = ExpStdOp.create("excluding", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: e1
		mutatedExpr.add(baseCol);

		// Mutation 3: e1^
		for(Expression mutantCol: baseColMutantsHull) {
			mutatedExpr.add(mutantCol);
		}
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	private void mutateUnionProductIntersection(ExpStdOp exp, String opName) {
		if (debHullMet) System.out.println("Hull - mutateUnionProductIntersection - exp ["+exp+"] , opName ["+opName+"]");

		Expression[] args = exp.args();
		assert(args.length == 2);
		Expression baseCol  = args[0];
		Expression otherCol = args[1];

		// Generate the hull of the baseCol
		List<Expression> baseColMutantsHull = HullVisitor.hull(baseCol);

		// Generate the hull of the otherCol
		List<Expression> otherColMutantsHull = HullVisitor.hull(otherCol);

		// Mutation 1: Compute the hull of the base collection (H7a) -- e1^->op(e2) --
		for(Expression mutantBaseCol: baseColMutantsHull) {
			Expression newArgs[] = {mutantBaseCol, otherCol};
			try {
				Expression mutant = ExpStdOp.create(opName, newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the hull of the other collection (H7b) -- // e1->op(e2^) --
		for(Expression mutantOtherCol: otherColMutantsHull) {
			Expression newArgs[] = {baseCol, mutantOtherCol};
			try {
				Expression mutant = ExpStdOp.create(opName, newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: Compute the hull of the base collection and the hull of the other collection (H7c)
		// e1^->op(e2^)
		for(Expression baseMutant: baseColMutantsHull) {
			for(Expression otherMutant: otherColMutantsHull) {
				Expression newArgs[] = {baseMutant, otherMutant};
				try {
					Expression mutant = ExpStdOp.create(opName, newArgs);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	private void mutateDifference(ExpStdOp exp) {
		if (debHullMet) System.out.println("Hull - mutateDifference - exp ["+exp+"]");

		Expression[] args = exp.args();
		assert(args.length == 2);
		Expression e1Set  = args[0];
		Expression e2Set = args[1];

		// Generate the hull of the e1
		List<Expression> e1Hull = HullVisitor.hull(e1Set);

		// Generate the kernel of the e2
		List<Expression> e2Kernel = KernelVisitor.kernel(e2Set);

		// Mutation 1: Compute the hull of the e1 (H9a) -- e1^-e2 --
		for(Expression hull: e1Hull) {
			Expression newArgs[] = {hull, e2Set};
			try {
				Expression mutant = ExpStdOp.create("-", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 2: Compute the hull of the other collection (H9b) -- // e1-e2v --
		for(Expression kernel: e2Kernel) {
			Expression newArgs[] = {e1Set, kernel};
			try {
				Expression mutant = ExpStdOp.create("-", newArgs);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e) {
				e.printStackTrace();
			}
		}

		// Mutation 3: Compute the hull of the base collection and the hull of the other collection (H9c)
		// e1^-e2v --
		for(Expression hull: e1Hull) {
			for(Expression kernel: e2Kernel) {
				Expression newArgs[] = {hull, kernel};
				try {
					Expression mutant = ExpStdOp.create("-", newArgs);
					mutatedExpr.add(mutant);
				} catch (ExpInvalidException e) {
					e.printStackTrace();
				}
			}
		}

		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		if (debHullMet) System.out.println("Hull - visitStdOp - exp ["+exp+"]");
		String opName = exp.opname();
		if (debHullMet) System.out.println("Hull - visitStdOp - opName ["+opName+"] -----------------------------------");
		switch(opName) {
		case "isEmpty":
			mutateIsEmptyExp(exp);
			break;		
		case "including":
			mutateIncluding(exp);
			break;
		case "excluding":
			mutateExcluding(exp);
			break;
		case "union":
		case "product":
		case "intersection":
			mutateUnionProductIntersection(exp, opName);
			break;	
		case "-":
			mutateDifference(exp);
			break;			
		default:
			wrongTypeError("unsupported operation type '" + opName + "'");
		}			

	}

	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {
		if (debHullMet) System.out.println("Hull - visitTupleLiteral - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
		if (debHullMet) System.out.println("Hull - visitTupleSelectOp - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
		if (debHullMet) System.out.println("Hull - visitUndefined - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		if (debHullMet) System.out.println("Hull - visitVariable - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitClosure(ExpClosure exp) {
		if (debHullMet) System.out.println("Hull - visitClosure - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)); defaultHull(exp);
	}

	@Override
	public void visitOclInState(ExpOclInState exp) {
		if (debHullMet) System.out.println("Hull - visitClosure - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
		if (debHullMet) System.out.println("Hull - visitVarDeclList - varDeclList ["+varDeclList+"]");
		assert (false);
	}

	@Override
	public void visitVarDecl(VarDecl varDecl) {
		if (debHullMet) System.out.println("Hull - visitVarDecl - varDecl ["+varDecl+"]");
		assert (false);
	}

	@Override
	public void visitObjectByUseId(ExpObjectByUseId exp) {
		if (debHullMet) System.out.println("Hull - visitObjectByUseId - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) {
		if (debHullMet) System.out.println("Hull - visitConstUnlimitedNatural - exp ["+exp+"]");
		assert (false);
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind exp) {
		if (debHullMet) System.out.println("Hull - visitSelectByKind - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType exp) {
		if (debHullMet) System.out.println("Hull - visitExpSelectByType - exp ["+exp+"]");
		Type type =null;
		Type typeExp = exp.type();
		if (typeExp.isKindOfBag(Type.VoidHandling.EXCLUDE_VOID)) {
			BagType mc = (BagType) exp.type();
			type = mc.elemType();
		}else if(typeExp.isKindOfSet(Type.VoidHandling.EXCLUDE_VOID)) {
			SetType mc = (SetType) exp.type();
			type = mc.elemType();
		}
		if (type!=null) {
			Expression range = exp.getSourceExpression();
			List<Expression> rangeHull = HullVisitor.hull(range);
			for(Expression mutantCol: rangeHull) {
				try {
					Expression mutant = new ExpSelectByType(mutantCol, type);
					mutatedExpr.add(mutant);
				} catch (SemanticException e) {
					e.printStackTrace();
				}
			}
		}
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitRange(ExpRange exp) {
		if (debHullMet) System.out.println("Hull - visitRange - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultHull(exp);
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
		if (debHullMet) System.out.println("Hull - visitNavigationClassifierSource - exp ["+exp+"]");
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultHull(exp);
		}

	}

}
