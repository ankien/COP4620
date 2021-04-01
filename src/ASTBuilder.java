import java.io.*;
import java.util.*;

public class ASTBuilder extends LittleBaseListener {

	/*Abstract syntax tree implementation*/
	private class AST{
		AST left;
		AST right;
		String value;
	}
	Hashtable<String, String> varsType; //stores declared vars and their types
	Stack<AST> trees; //stores all ASTs (Queue = first in first out)

	/*no arg constructor*/
	public ASTBuilder(){
		varsType = new Hashtable<>();
		trees = new Stack<>();
	}

	/*Store variables' that are FLOAT or INT*/
	@Override
	public void enterVar_decl(LittleParser.Var_declContext ctx) {
		String[] allVars = (ctx.id_list().getText()).split(",");

		for(int i = 0; i < allVars.length; i++){
			varsType.put(allVars[i], ctx.var_type().getText());
		}
	}

	/*Make AST nodes for simple assignment expressions*/
	@Override
	public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
		AST root = new AST(); //create new AST
		root.value = "EQUALSIGN";
		AST leftNode = new AST();
		leftNode.value = "VARREF " + ctx.id().getText() + " " + varsType.get(ctx.id().getText());
		root.left = leftNode;
		trees.push(root);
	}

	/*Enter addition or subtraction operation*/
	@Override
	public void enterAddop(LittleParser.AddopContext ctx) {
		AST root = trees.pop();
		AST rightNode = new AST();
		if(root.right != null){
			AST temp = root.right;
			rightNode.value = "ADDOP " + ctx.getText();
			rightNode.left = temp; //ex: e+f 'e' will be the leftchild of '+'
			root.right = rightNode;
			trees.push(root);
			return;
		}
		rightNode.value = "ADDOP " + ctx.getText();
		root.right = rightNode;
		trees.push(root);
	}

	/*Enter multiplication or division operation*/
	@Override
	public void enterMulop(LittleParser.MulopContext ctx) {
		AST root = trees.pop();
		AST rightNode = new AST();
		if(root.right != null){
			AST temp = root.right;
			rightNode.value = "MULOP " + ctx.getText();
			rightNode.left = temp; //ex: e+f 'e' will be the leftchild of '+'
			root.right = rightNode;
			trees.push(root);
			return;
		}
		rightNode.value = "MULOP " + ctx.getText();
		root.right = rightNode;
		trees.push(root);
	}

	/*Enter String declaration*/
	@Override
	public void enterString_decl(LittleParser.String_declContext ctx) {
		AST root = new AST();
		root.value = "STRING " + ctx.id().getText() + " " + ctx.str().getText();
		trees.push(root);
	}

	@Override
	public void enterPrimary(LittleParser.PrimaryContext ctx) {
		AST root = trees.pop();
		AST rightNode = root.right;
		String primaryValue = ctx.getText();

		/*Expressions with parenthesis can be ignored*/
		if( primaryValue.startsWith("(") ){
			return;
		}

		boolean numeric = true;
		numeric = (primaryValue).matches("-?\\d+(\\.\\d+)?");
		if( numeric ) {
			primaryValue = "CONSTANT " + ctx.getText();
		} else {
			primaryValue = "VARREF " + ctx.getText() + " " + varsType.get(ctx.getText());
		}

		if(rightNode == null){
			rightNode = new AST();
			rightNode.value = primaryValue;
			root.right = rightNode;
		} else if( rightNode.left == null ){
			AST temp = new AST();
			temp.value = primaryValue;
			rightNode.left = temp;
		} else if( rightNode.right == null ){
			AST temp = new AST();
			temp.value = primaryValue;
			rightNode.right = temp;
		}
		trees.push( root );
	}

	@Override
	public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
		AST root = new AST();
		root.value = "READ " + (ctx.id_list().getText()).replace(",", " ");
		trees.push(root);
	}

	@Override
	public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
		AST root = new AST();
		root.value = "WRITE " + (ctx.id_list().getText()).replace(",", " ");
		trees.push(root);
	}

	/*Print tree in post order*/
	public void prettyPrint(){
		for(AST tree: trees){
			postorderIterative(tree);
		}
	}

	// Iterative function to perform postorder traversal on the tree
	public static void postorderIterative(AST root)
	{
		// create an empty stack and push the root node
		Stack<AST> stack = new Stack<>();
		stack.push(root);

		// create another stack to store postorder traversal
		Stack<String> out = new Stack<>();

		// loop till stack is empty
		while (!stack.empty())
		{
			// pop a node from the stack and push the data into the output stack
			AST curr = stack.pop();
			out.push(curr.value);

			// push the left and right child of the popped node into the stack
			if (curr.left != null) {
				stack.push(curr.left);
			}

			if (curr.right != null) {
				stack.push(curr.right);
			}
		}

		// print postorder traversal
		while (!out.empty()) {
			System.out.println(out.pop());
		}
	}

}
