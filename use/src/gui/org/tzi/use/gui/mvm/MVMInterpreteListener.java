// Generated from Simple.g4 by ANTLR 4.4
package org.tzi.use.gui.mvm;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MVMInterpreteParser}.
 */
public interface MVMInterpreteListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#blankLine}.
	 * @param ctx the parse tree
	 */
	void enterBlankLine(@NotNull MVMInterpreteParser.BlankLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#blankLine}.
	 * @param ctx the parse tree
	 */
	void exitBlankLine(@NotNull MVMInterpreteParser.BlankLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#role}.
	 * @param ctx the parse tree
	 */
	void enterRole(@NotNull MVMInterpreteParser.RoleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#role}.
	 * @param ctx the parse tree
	 */
	void exitRole(@NotNull MVMInterpreteParser.RoleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#defmodel}.
	 * @param ctx the parse tree
	 */
	void enterDefmodel(@NotNull MVMInterpreteParser.DefmodelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#defmodel}.
	 * @param ctx the parse tree
	 */
	void exitDefmodel(@NotNull MVMInterpreteParser.DefmodelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#pre_decl}.
	 * @param ctx the parse tree
	 */
	void enterPre_decl(@NotNull MVMInterpreteParser.Pre_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#pre_decl}.
	 * @param ctx the parse tree
	 */
	void exitPre_decl(@NotNull MVMInterpreteParser.Pre_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull MVMInterpreteParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull MVMInterpreteParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#constraints}.
	 * @param ctx the parse tree
	 */
	void enterConstraints(@NotNull MVMInterpreteParser.ConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#constraints}.
	 * @param ctx the parse tree
	 */
	void exitConstraints(@NotNull MVMInterpreteParser.ConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@NotNull MVMInterpreteParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@NotNull MVMInterpreteParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classReference}.
	 * @param ctx the parse tree
	 */
	void enterClassReference(@NotNull MVMInterpreteParser.ClassReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classReference}.
	 * @param ctx the parse tree
	 */
	void exitClassReference(@NotNull MVMInterpreteParser.ClassReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#lineInv}.
	 * @param ctx the parse tree
	 */
	void enterLineInv(@NotNull MVMInterpreteParser.LineInvContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#lineInv}.
	 * @param ctx the parse tree
	 */
	void exitLineInv(@NotNull MVMInterpreteParser.LineInvContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(@NotNull MVMInterpreteParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(@NotNull MVMInterpreteParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(@NotNull MVMInterpreteParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(@NotNull MVMInterpreteParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(@NotNull MVMInterpreteParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(@NotNull MVMInterpreteParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprALG}.
	 * @param ctx the parse tree
	 */
	void enterExprALG(@NotNull MVMInterpreteParser.ExprALGContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprALG}.
	 * @param ctx the parse tree
	 */
	void exitExprALG(@NotNull MVMInterpreteParser.ExprALGContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#assocDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAssocDeclaration(@NotNull MVMInterpreteParser.AssocDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#assocDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAssocDeclaration(@NotNull MVMInterpreteParser.AssocDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprBase}.
	 * @param ctx the parse tree
	 */
	void enterExprBase(@NotNull MVMInterpreteParser.ExprBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprBase}.
	 * @param ctx the parse tree
	 */
	void exitExprBase(@NotNull MVMInterpreteParser.ExprBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#invExpression}.
	 * @param ctx the parse tree
	 */
	void enterInvExpression(@NotNull MVMInterpreteParser.InvExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#invExpression}.
	 * @param ctx the parse tree
	 */
	void exitInvExpression(@NotNull MVMInterpreteParser.InvExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprRightIMPLIES}.
	 * @param ctx the parse tree
	 */
	void enterExprRightIMPLIES(@NotNull MVMInterpreteParser.ExprRightIMPLIESContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprRightIMPLIES}.
	 * @param ctx the parse tree
	 */
	void exitExprRightIMPLIES(@NotNull MVMInterpreteParser.ExprRightIMPLIESContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#bound}.
	 * @param ctx the parse tree
	 */
	void enterBound(@NotNull MVMInterpreteParser.BoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#bound}.
	 * @param ctx the parse tree
	 */
	void exitBound(@NotNull MVMInterpreteParser.BoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprCMP}.
	 * @param ctx the parse tree
	 */
	void enterExprCMP(@NotNull MVMInterpreteParser.ExprCMPContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprCMP}.
	 * @param ctx the parse tree
	 */
	void exitExprCMP(@NotNull MVMInterpreteParser.ExprCMPContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#invDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInvDeclaration(@NotNull MVMInterpreteParser.InvDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#invDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInvDeclaration(@NotNull MVMInterpreteParser.InvDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(@NotNull MVMInterpreteParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(@NotNull MVMInterpreteParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#post_decl}.
	 * @param ctx the parse tree
	 */
	void enterPost_decl(@NotNull MVMInterpreteParser.Post_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#post_decl}.
	 * @param ctx the parse tree
	 */
	void exitPost_decl(@NotNull MVMInterpreteParser.Post_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprLITERAL}.
	 * @param ctx the parse tree
	 */
	void enterExprLITERAL(@NotNull MVMInterpreteParser.ExprLITERALContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprLITERAL}.
	 * @param ctx the parse tree
	 */
	void exitExprLITERAL(@NotNull MVMInterpreteParser.ExprLITERALContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#connector}.
	 * @param ctx the parse tree
	 */
	void enterConnector(@NotNull MVMInterpreteParser.ConnectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#connector}.
	 * @param ctx the parse tree
	 */
	void exitConnector(@NotNull MVMInterpreteParser.ConnectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#operationsBody}.
	 * @param ctx the parse tree
	 */
	void enterOperationsBody(@NotNull MVMInterpreteParser.OperationsBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#operationsBody}.
	 * @param ctx the parse tree
	 */
	void exitOperationsBody(@NotNull MVMInterpreteParser.OperationsBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#extendsClause}.
	 * @param ctx the parse tree
	 */
	void enterExtendsClause(@NotNull MVMInterpreteParser.ExtendsClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#extendsClause}.
	 * @param ctx the parse tree
	 */
	void exitExtendsClause(@NotNull MVMInterpreteParser.ExtendsClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#genericType}.
	 * @param ctx the parse tree
	 */
	void enterGenericType(@NotNull MVMInterpreteParser.GenericTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#genericType}.
	 * @param ctx the parse tree
	 */
	void exitGenericType(@NotNull MVMInterpreteParser.GenericTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void enterLineComment(@NotNull MVMInterpreteParser.LineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void exitLineComment(@NotNull MVMInterpreteParser.LineCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(@NotNull MVMInterpreteParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(@NotNull MVMInterpreteParser.OperationContext ctx);
}