// Generated from com\juanto\simple\inter2\Simple.g4 by ANTLR 4.9.2
package org.tzi.use.gui.mvm;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MVMInterpreteParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MVMInterpreteVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(MVMInterpreteParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#defmodel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefmodel(MVMInterpreteParser.DefmodelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(MVMInterpreteParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#assocDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssocDeclaration(MVMInterpreteParser.AssocDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#invDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvDeclaration(MVMInterpreteParser.InvDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#lineInv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineInv(MVMInterpreteParser.LineInvContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#extendsClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExtendsClause(MVMInterpreteParser.ExtendsClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(MVMInterpreteParser.ClassBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#operationsBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperationsBody(MVMInterpreteParser.OperationsBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#attribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttribute(MVMInterpreteParser.AttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#operation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperation(MVMInterpreteParser.OperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MVMInterpreteParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#classReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassReference(MVMInterpreteParser.ClassReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#role}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRole(MVMInterpreteParser.RoleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#bound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBound(MVMInterpreteParser.BoundContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#constraints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstraints(MVMInterpreteParser.ConstraintsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#pre_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPre_decl(MVMInterpreteParser.Pre_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#post_decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPost_decl(MVMInterpreteParser.Post_declContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#invExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvExpression(MVMInterpreteParser.InvExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#exprBase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprBase(MVMInterpreteParser.ExprBaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#connector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConnector(MVMInterpreteParser.ConnectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#comparator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparator(MVMInterpreteParser.ComparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#blankLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlankLine(MVMInterpreteParser.BlankLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link MVMInterpreteParser#lineComment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLineComment(MVMInterpreteParser.LineCommentContext ctx);
}