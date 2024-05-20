// Generated from Simple.g4 by ANTLR 4.4
package org.tzi.use.gui.mvm;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MVMInterpreteParser}.
 */
public interface MVMInterpreteListener extends org.antlr.v4.runtime.tree.ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#exprBase}.
	 * @param ctx the parse tree
	 */
	void enterExprBase(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ExprBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#exprBase}.
	 * @param ctx the parse tree
	 */
	void exitExprBase(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ExprBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#blankLine}.
	 * @param ctx the parse tree
	 */
	void enterBlankLine(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.BlankLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#blankLine}.
	 * @param ctx the parse tree
	 */
	void exitBlankLine(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.BlankLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#role}.
	 * @param ctx the parse tree
	 */
	void enterRole(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.RoleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#role}.
	 * @param ctx the parse tree
	 */
	void exitRole(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.RoleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#invExpression}.
	 * @param ctx the parse tree
	 */
	void enterInvExpression(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.InvExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#invExpression}.
	 * @param ctx the parse tree
	 */
	void exitInvExpression(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.InvExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#bound}.
	 * @param ctx the parse tree
	 */
	void enterBound(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.BoundContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#bound}.
	 * @param ctx the parse tree
	 */
	void exitBound(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.BoundContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#defmodel}.
	 * @param ctx the parse tree
	 */
	void enterDefmodel(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.DefmodelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#defmodel}.
	 * @param ctx the parse tree
	 */
	void exitDefmodel(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.DefmodelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#pre_decl}.
	 * @param ctx the parse tree
	 */
	void enterPre_decl(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.Pre_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#pre_decl}.
	 * @param ctx the parse tree
	 */
	void exitPre_decl(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.Pre_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#constraints}.
	 * @param ctx the parse tree
	 */
	void enterConstraints(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ConstraintsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#constraints}.
	 * @param ctx the parse tree
	 */
	void exitConstraints(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ConstraintsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#invDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInvDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.InvDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#invDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInvDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.InvDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classReference}.
	 * @param ctx the parse tree
	 */
	void enterClassReference(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classReference}.
	 * @param ctx the parse tree
	 */
	void exitClassReference(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#comparator}.
	 * @param ctx the parse tree
	 */
	void enterComparator(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ComparatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#comparator}.
	 * @param ctx the parse tree
	 */
	void exitComparator(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ComparatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#post_decl}.
	 * @param ctx the parse tree
	 */
	void enterPost_decl(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.Post_declContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#post_decl}.
	 * @param ctx the parse tree
	 */
	void exitPost_decl(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.Post_declContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#connector}.
	 * @param ctx the parse tree
	 */
	void enterConnector(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ConnectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#connector}.
	 * @param ctx the parse tree
	 */
	void exitConnector(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ConnectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#lineInv}.
	 * @param ctx the parse tree
	 */
	void enterLineInv(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.LineInvContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#lineInv}.
	 * @param ctx the parse tree
	 */
	void exitLineInv(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.LineInvContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#operationsBody}.
	 * @param ctx the parse tree
	 */
	void enterOperationsBody(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.OperationsBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#operationsBody}.
	 * @param ctx the parse tree
	 */
	void exitOperationsBody(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.OperationsBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#extendsClause}.
	 * @param ctx the parse tree
	 */
	void enterExtendsClause(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ExtendsClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#extendsClause}.
	 * @param ctx the parse tree
	 */
	void exitExtendsClause(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ExtendsClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void enterLineComment(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.LineCommentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#lineComment}.
	 * @param ctx the parse tree
	 */
	void exitLineComment(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.LineCommentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.OperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MVMInterpreteParser#assocDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAssocDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.AssocDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MVMInterpreteParser#assocDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAssocDeclaration(@org.antlr.v4.runtime.misc.NotNull MVMInterpreteParser.AssocDeclarationContext ctx);
}