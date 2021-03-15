import java.io.*;
import java.util.*;

public class SymbolTableBuilder extends LittleBaseListener {

	private int blockNum = 1;
	private Stack<Hashtable<String, String>> currSymTab; //Stack of symbol tables. Current symbol table is on top of stack.
	private Hashtable<String, Hashtable<String, String>> finishedSymTab; //popped symbol tables will go here
	private int position = 0; //Position of nested functions or blocks. 0 is global symtab.
	private int varPosition = 0; //Stores order in which variables were declared.
	private Hashtable<String, String> temp;
	String[] keys;

	/*no arg constructor*/
	public SymbolTableBuilder(){
		currSymTab = new Stack<Hashtable<String, String>>();
		finishedSymTab = new Hashtable<String,Hashtable<String, String>>();
	}

	/*Enter the Program*/
	@Override
	public void enterProgram(LittleParser.ProgramContext ctx) {
		Hashtable<String, String> global = new Hashtable<>();
		global.put("*", "GLOBAL"); //key "*" in symbol table denotes context
		global.put("#", String.valueOf(position)); //key "#" in symbol table denotes position of block/function. 0 is GLOBAL.
		global.put("$", "0"); //Key "$" in symbol table denotes variable's position
		position++;
		currSymTab.push(global);
	}

	/*Exit program pop last symtab*/
	@Override
	public void exitProgram(LittleParser.ProgramContext ctx) {
		temp = currSymTab.pop();
		finishedSymTab.put(temp.remove("#"), temp);
	}

	/*Recognizes STRING*/
	@Override
	public void enterString_decl(LittleParser.String_declContext ctx) {
		temp = currSymTab.pop();
		keys = (ctx.id().getText()).split(",");
		varPosition = Integer.valueOf(temp.get("$"));
		for( int i = 0; i < keys.length; i++ ) {
			checkHashtable(temp, keys[i] );
			temp.put( String.valueOf(varPosition), keys[i]);
			temp.put( keys[i], "STRING value \"" + ctx.str().getText() + "\"" );
			varPosition++;
		}
		temp.put("$", String.valueOf(varPosition));
		currSymTab.push(temp);
	}

	/*Recognizes INT and FLOAT*/
	@Override
	public void enterVar_decl(LittleParser.Var_declContext ctx) {
		temp = currSymTab.pop();
		keys = (ctx.id_list().getText()).split(",");
		varPosition = Integer.valueOf(temp.get("$"));

		for(int i = 0; i < keys.length; i++){
			checkHashtable(temp, keys[i]);
			temp.put(String.valueOf(varPosition), keys[i]);
			temp.put(keys[i], ctx.var_type().getText());
			varPosition++;
		}

		temp.put("$",String.valueOf(varPosition));
		currSymTab.push(temp);
	}

	//create a new symbol table for function scope and add parameters to it
	@Override
	public void enterFunc_decl(LittleParser.Func_declContext ctx) {
		Hashtable<String, String> func = new Hashtable<>();
		func.put("*", ctx.id().getText());
		func.put("#", String.valueOf(position));
		varPosition = 0;
		position++;

		//put function parameters into func hashtable
		if( ctx.param_decl_list().param_decl() != null ) {
			//Split function parameters INTx, INTy, FLOATx by ","
			String[] parameters = (ctx.param_decl_list().getText()).split(",");

			//Do a positive look behind to split function parameters preceded by INT or FLOAT
			for(int i = 0; i < parameters.length; i++){
				String type = (parameters[i].split("(?<=INT|FLOAT)"))[0]; //(this mess)[0] contains the type of parameter
 				String key = (parameters[i].split("(?<=INT|FLOAT)"))[1]; //(this mess)[1] contains the key for symtab
				checkHashtable(func, key);
				func.put(String.valueOf(varPosition), key);
				func.put(key, type);
				varPosition++;
			}
		}
		func.put("$",String.valueOf(varPosition));
		currSymTab.push(func);
	}

	//pop function scoped symbol table when exiting function
	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx) {
		temp = currSymTab.pop();
		finishedSymTab.put(temp.remove("#"), temp);
	}

	//Enter if statement then create new symtab
	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		block.put("#", String.valueOf(position));
		block.put("$", "0");
		position++;
		blockNum++;
		currSymTab.push(block);
	}

	//Exit if statement. Pop currsymtab
	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) {
		temp = currSymTab.pop();
		finishedSymTab.put(temp.remove("#"), temp);
	}

	/*Enter ELSE statement create new symtab.*/
	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		block.put("#", String.valueOf(position));
		block.put("$", "0");
		position++;
		blockNum++;
		currSymTab.push(block);
	}

	/*Exit ELSE statement. Pop symtab.*/
	@Override
	public void exitElse_part(LittleParser.Else_partContext ctx) {
		temp = currSymTab.pop();
		finishedSymTab.put(temp.remove("#"), temp);
	}

	//Enter while statement then create new symtab
	@Override
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
		Hashtable<String, String> block = new Hashtable<>();
		block.put("*", "BLOCK "+blockNum);
		block.put("#", String.valueOf(position));
		block.put("$", "0");
		position++;
		blockNum++;
		currSymTab.push(block);
	}

	//Exit while statement. Pop currsymtab
	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) {
		temp = currSymTab.pop();
		finishedSymTab.put(temp.remove("#"), temp);
	}

	public void prettyPrint(){
		for(int i = 0; i < position; i++){
			temp = finishedSymTab.get(String.valueOf(i));
			System.out.println("Symbol table " + temp.get("*"));

			int numOfVars = Integer.valueOf(temp.get("$"));
			String key;
			String value;
			for( int j = 0; j < numOfVars; j++ ){
				key = temp.remove(String.valueOf(j));
				value = temp.get(key);
				System.out.println("name " + key + " type " + value);
			}
			System.out.println();
		}
	}

	//check if variable name got declared twice in same scope
	private void checkHashtable( Hashtable<String, String> t, String k ){
		if( t.get(k) != null){
				System.out.println("DECLARATION ERROR "+ k);
				System.exit(1);
		}
	}
}
