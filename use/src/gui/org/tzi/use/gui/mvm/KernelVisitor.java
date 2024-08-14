package org.tzi.use.gui.mvm;

import java.util.LinkedList;
import java.util.List;

import org.tzi.use.uml.mm.MClass;
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
import org.tzi.use.uml.ocl.type.SetType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeFactory;

public class KernelVisitor implements ExpressionVisitor {

	private List<Expression> mutatedExpr;

	public KernelVisitor() {
		System.out.println("KernelVisitor***");
		mutatedExpr = new LinkedList<Expression>();
	}

	public List<Expression> getMutatedExpr() {
		return mutatedExpr;
	}

	public static List<Expression> kernel(Expression exp) {
		System.out.println("Kernel - exp ["+exp+"]");
		KernelVisitor vis = new KernelVisitor();
		exp.processWithVisitor(vis);
		return vis.getMutatedExpr();
	}

	private void defaultKernel(Expression exp) {
		System.out.println("Kernel - kernel - defaultKernel");

		SetType mc = (SetType) exp.type();
		Type type = mc.elemType();

		if (type instanceof MClass) {
			MClass mClass = (MClass) type;
			MModel model = mClass.model();	
			MClass typeClass = model.getClass(mClass.name());

			SetType classSetType = TypeFactory.mkSet(typeClass);

			try {
				ExpEmptyCollection mutant = new ExpEmptyCollection(classSetType);
				mutatedExpr.add(mutant);
			} catch (ExpInvalidException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void visitIncluding(ExpAllInstances exp) {
		System.out.println("Kernel - visitIncluding - exp ["+exp+"]");
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visitAny(ExpAny exp) {
		assert (false);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		assert (false);
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		assert (false);
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitCollect(ExpCollect exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitCollectNested(ExpCollectNested exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		assert (false);
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		assert (false);
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		assert (false);
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		assert (false);
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		assert (false);
	}

	@Override
	public void visitEmptyCollection(ExpEmptyCollection exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitExists(ExpExists exp) {
		assert (false);
	}

	@Override
	public void visitForAll(ExpForAll exp) {
		assert (false);
	}

	@Override
	public void visitIf(ExpIf exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		assert (false);
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		assert (false);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		assert (false);
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

	@Override
	public void visitLet(ExpLet exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		assert (false);
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
		assert (false);
	}

	@Override
	public void visitOne(ExpOne exp) {
		assert (false);
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		assert (false);
	}

	@Override
	public void visitReject(ExpReject exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

	@Override
	public void visitSelect(ExpSelect exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {
		assert (false);
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
		assert (false);
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
		assert (false);
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		assert (false);
	}

	@Override
	public void visitClosure(ExpClosure exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitOclInState(ExpOclInState exp) {
		assert (false);
	}

	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
		assert (false);
	}

	@Override
	public void visitVarDecl(VarDecl varDecl) {
		assert (false);
	}

	@Override
	public void visitObjectByUseId(ExpObjectByUseId exp) {
		assert (false);
	}

	@Override
	public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) {
		assert (false);
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitRange(ExpRange exp) {
		assert (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID));	defaultKernel(exp);
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
		if (exp.type().isKindOfCollection(Type.VoidHandling.EXCLUDE_VOID)){
			defaultKernel(exp);
		}
	}

}
