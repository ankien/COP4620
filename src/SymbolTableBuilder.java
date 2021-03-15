import java.io.*;
import java.util.*;

public class SymbolTableBuilder extends LittleBaseListener {

	int blockNum = 1;
	private Stack<Hashtable<String, String>> currSymTab; //Stack of symbol tables. Current symbol table is on top of stack.
	private Stack<Hashtable<String, String>> finishedSymTab; //popped symbol tables will go here
	private Hashtable<String, String> temp;
	String[] keys;

	/*no arg constructor*/
	public SymbolTableBuilder(){
		currSymTab = new Stack<Hashtable<String, String>>();
		finishedSymTab = new Stack<Hashtable<String, String>>();
	}

	/*Enter the Program*/
	@Override
	public void enterProgram(LittleParser.ProgramContext ctx) {
		Hashtable<String, String> global = new Hashtable<>();
		global.put("*", "GLOBAL"); //key "*" in symbol table denotes context
		currSymTab.push(global);
	}

	/*Exit program pop last symtab*/
	@Override
	public void exitProgram(LittleParser.ProgramContext ctx) {
		finishedSymTab.push(currSymTab.pop());
	}

	/*Recognizes STRING*/
	@Override
	public void enterString_decl(LittleParser.String_declContext ctx) {
		temp = currSymTab.pop();
		keys = (ctx.id().getText()).split(",");

		for( int i = 0; i < keys.length; i++ ) {
			checkHashtable(temp, keys[i] );
			temp.put( keys[i], "STRING " + ctx.str().getText() );
		}

		currSymTab.push(temp);
	}

	/*Recognizes INT and FLOAT*/
	@Override
	public void enterVar_decl(LittleParser.Var_declContext ctx) {
		temp = currSymTab.pop();
		keys = (ctx.id_list().getText()).split(",");

		for(int i = 0; i < keys.length; i++){
			checkHashtable(temp, keys[i]);
			temp.put(keys[i], ctx.var_type().getText());
		}

		currSymTab.push(temp);
	}

	//create a new symbol table for function scope and add parameters to it
	@Override
	public void enterFunc_decl(LittleParser.Func_declContext ctx) {
		Hashtable<String, String> func = new Hashtable<>();
		func.put("*", ctx.id().getText());
		if( ctx.param_decl_list().param_decl() != null ) {

			//Split function parameters INTx, INTy, FLOATx by ","
			String[] parameters = (ctx.param_decl_list().getText()).split(",");

			//Do a positive look behind to split strings preceded by INT or FLOAT
			for(int i = 0; i < parameters.length; i++){
				String type = (parameters[i].split("(?<=INT|FLOAT)"))[0]; //(this mess)[0] contains the type of parameter
 				String key = (parameters[i].split("(?<=INT|FLOAT)"))[1]; //(this mess)[1] contains the key for symtab
				checkHashtable(func, key);
				func.put(key, type);
			}
		}
		currSymTab.push(func);
	}

	//pop function scoped symbol table when exiting function
	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx) {
		finishedSymTab.push(currSymTab.pop());
	}

	//Enter if statement then create new symtab
	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		blockNum++;
		currSymTab.push(block);
	}

	//Exit if statement. Pop currsymtab
	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
		finishedSymTab.push(currSymTab.pop());
	}

	/*Enter ELSE statement create new symtab.*/
	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		blockNum++;
		currSymTab.push(block);
	}

	/*Exit ELSE statement. Pop symtab.*/
	@Override
	public void exitElse_part(LittleParser.Else_partContext ctx) {
		finishedSymTab.push(currSymTab.pop());
	}

	//Enter while statement then create new symtab
	@Override
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		blockNum++;
		currSymTab.push(block);
	}

	//Exit while statement. Pop currsymtab
	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
		finishedSymTab.push(currSymTab.pop());
	}

	public void prettyPrint(){
		System.out.println(finishedSymTab);
	}

	//check if variable name got declared twice in same scope
	private void checkHashtable( Hashtable<String, String> t, String k ){
		if( t.get(k) != null){
				System.out.println("DECLARATION ERROR "+ k);
				System.exit(1);
		}
	}
}
