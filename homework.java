import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


class Node
{
	public String value;
	public Node leftChild;
	public Node rightChild;
	
	Node()
	{
		this.leftChild=null;
		this.rightChild=null;
		this.value=null;
	}
	
	Node(Node leftChild,String value,Node rightChild)
	{
		this.leftChild=leftChild;
		this.rightChild=rightChild;
		this.value=value;
	}
}

class Clauses
{
	public List<Literals> sentence;
	
	Clauses()
	{
		this.sentence=new ArrayList<Literals>();
	}
}
class Literals
{
	public String Operator;
	public String[] Operands;
	
}


public class homework {

	public static int noOfQueries;
	public static int noOfRules;
	public static File file;
	public static PrintWriter printWriter;
	public static ArrayList<String> queries;
	public static HashMap<String, List<Clauses> > KB;
	public StringBuilder rule;
	
	public StringBuilder postfix(String sentence)
	{
		rule= new StringBuilder();
		
		//Infix to postfix
		Stack<Character> stack=new Stack<Character>();
				
		int i=0;
		String[] str=sentence.split("\\s");
		while(i<str.length)
		{
			int j=0;
			while(j<str[i].length())
			{
				
				char ch=str[i].charAt(j);
				//String str= new String(ch);
				
				if(ch>='A' && ch<='Z')
				{
					rule.append(str[i].charAt(j));
					j++;
					while(str[i].charAt(j)!=')')
					{
						rule.append(str[i].charAt(j));
						j++;
					}
					rule.append(str[i].charAt(j));
					rule.append(' ');
				}
				else
				{
					if(ch=='(')
					{
						stack.push(ch);
					}
					else
					{
						if(ch==')')
						{
							while(stack.peek()!='(')
							{
								if(stack.peek()!= '=')
								{
									rule.append(stack.pop());
									rule.append(' ');
								}
								else
								{
									rule.append(stack.pop());
									rule.append(stack.pop());
									rule.append(' ');
								}
								
							}
							stack.pop();
							//rule.append(' ');
						}
						else
						{
							if(ch=='=')
							{
								stack.push('>');
								stack.push('=');
								j++;
							}
							else
								stack.push(ch);
						}
					}
				}
				j++;
			}
			
			i++;
			
			
			
			
			
		}
		//End of Infix to postfix
		//System.out.println(rule);
		
		return rule;
	}
	
	public String cnf(String sentence)
	{
		StringBuilder builder=new StringBuilder();
		for(int i=0;i<sentence.length();i++)
		{
			if(sentence.charAt(i)==' ')
				continue;
			else
			{
				if(sentence.charAt(i)=='=')
				{
					builder.append(' ');
					builder.append('=');
				}
				else if(sentence.charAt(i)=='>')
				{
					builder.append('>');
					builder.append(' ');
				}
				else if(sentence.charAt(i)=='&')
				{
					builder.append(' ');
					builder.append('&');
					builder.append(' ');
				}
				else if(sentence.charAt(i)=='|')
				{
					builder.append(' ');
					builder.append('|');
					builder.append(' ');
				}
				else
				{
					builder.append(sentence.charAt(i));
				}
			}
				
		}
		String sentence1=builder.toString();
		StringBuilder strB=new StringBuilder();
		homework hw1=new homework();
		
		StringBuilder rule=hw1.postfix(sentence1);
		
		
		Node root=hw1.expressionTree(rule);
		
		root=hw1.implicationElimination(root);
		
		root=hw1.negationElimination(root);
		root=hw1.distributivity(root);
		displayTree(root,strB);
		System.out.println(strB);
		String s=strB.toString();
		//System.out.println(s);
		 return s;
		
		
		
	}
	
	public Node distributivity(Node root)
	{
		int flag=0;
		Node rootNew=new Node();
		Node node1;
		Node node2;
		if(root==null)
		{
			return null;
		}
		if(root.value.equals("|"))
		{
			if(root.rightChild.value.equals("&"))
			{
				node1=new Node(root.leftChild,"|",root.rightChild.leftChild);
				node2=new Node(root.leftChild,"|",root.rightChild.rightChild);
				rootNew=new Node(node1,"&",node2);
				flag=1;
			}
			else if(root.leftChild.value.equals("&"))
			{
				node1=new Node(root.leftChild.leftChild,"|",root.rightChild);
				node2=new Node(root.leftChild.rightChild,"|",root.rightChild);
				rootNew=new Node(node1,"&",node2);
				flag=1;
			}
			
			
		}
		
		
		root.leftChild=negationElimination(root.leftChild);
		root.rightChild=negationElimination(root.rightChild);
		
		if(flag==1)
			return rootNew;
				
		return root;
	}
		
	public void displayTree(Node root,StringBuilder strB)
	{
		if(root==null)
		{
			return ;
		}
		
		displayTree(root.leftChild,strB);
		strB.append(root.value);
		if(!root.value.equals("~")) 
			strB.append(" ");
		displayTree(root.rightChild,strB);
		
		
		
		
	}
	public Node negationElimination(Node root)
	{
		int flag=0;
		if(root==null)
		{
			return null;
		}
		if(root.value.equals("~"))
		{
			if(root.rightChild.value.equals("&"))
			{
				root.rightChild.value="|";
				Node Tnode=new Node(null,"~",root.rightChild.leftChild);
				root.rightChild.leftChild=Tnode;
				
				Node Tnode1=new Node(null,"~",root.rightChild.rightChild);
				root.rightChild.rightChild=Tnode1;
				//root=root.rightChild;
				flag=1;
			}
			else if(root.rightChild.value.equals("|"))
			{
				root.rightChild.value="&";
				Node Tnode=new Node(null,"~",root.rightChild.rightChild);
				root.rightChild.rightChild=Tnode;
				
				Node Tnode1=new Node(null,"~",root.rightChild.leftChild);
				root.rightChild.leftChild=Tnode1;
				//root=root.rightChild;
				flag=1;
			}
			else if(root.rightChild.value.equals("~"))
			{
				//in progress
				flag=2;
			}
			
		}
		
		
		root.leftChild=negationElimination(root.leftChild);
		root.rightChild=negationElimination(root.rightChild);
		
		if(flag==1)
			return root.rightChild;
		if(flag==2)
			return root.rightChild.rightChild;
			
		return root;
	}
	
	public Node implicationElimination(Node root)
	{
		if(root==null)
		{
			return null;
		}
		implicationElimination(root.leftChild);
		implicationElimination(root.rightChild);
		
		if(root.value.equals("=>"))
		{
			Node Tnode=new Node(null,"~",root.leftChild);
			root.leftChild=Tnode;
			root.value="|";
		}
		
		
			return root;
		
	}
	
	public Node expressionTree(StringBuilder rule)
	{
		String postfix=rule.toString();
		String[] str=postfix.split("\\s");
		Stack<Node> stack=new Stack<Node>();
		
		
		for(int i=0;i<str.length;i++)
		{
			Node Tnode;
			
			if(str[i].equals("~"))
			{
				Node rchild=stack.pop();
				//Node lchild=stack.pop();
				Tnode=new Node(null,"~",rchild);
				stack.push(Tnode);
			}
			else if(str[i].equals("=>") || str[i].equals("&") || str[i].equals("|"))
			{
				Node rchild=stack.pop();
				Node lchild=stack.pop();
				Tnode=new Node(lchild,str[i],rchild);
				stack.push(Tnode);
			}
			else
			{
				stack.push(new Node(null,str[i],null));
			}
			
		}
		return stack.pop();
	}
	
	public static void main(String[] args) {
		
		homework hw=new homework();
		BufferedReader bufferedReader=null;
		KB=new HashMap<String, List<Clauses> >();
		//HashMap<String, List<String> > KBcopy;
		
		file=new File("output.txt");
		
		
		
		
		//Printing solution into a output file
		
			try 
			{
					file.createNewFile();
					
					
					printWriter= new PrintWriter(file);
		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try
			{
				bufferedReader = new BufferedReader(new FileReader("input.txt"));
				
			//Reading the file input	
				noOfQueries=Integer.parseInt(bufferedReader.readLine());
				queries=new ArrayList<String>(noOfQueries);
				
				for(int i=0;i<noOfQueries;i++)
				{
					queries.add(i,bufferedReader.readLine());
				}
				
				//System.out.println(queries);
				noOfRules=Integer.parseInt(bufferedReader.readLine());
				int m=1;
				for(int j=0;j<noOfRules;j++)
				{
					
					String s=hw.cnf(bufferedReader.readLine());
					
					String keyinKB=new String();
					String[] str=s.split("&");
					for(int i=0;i<str.length;i++)
					{
						Clauses clause=new Clauses();
						String[] token=str[i].split("\\s");
						for(int k=0;k<token.length;k++)
						{
							if(!(token[k].equals("|") || token[k].equals("")))
							{
								String[] key=token[k].split("\\(");
								//keyinKB=key[0];
								//System.out.println(key[1]);
								String[] op=key[1].split("\\)");				//operator ready
								String[] operands=op[0].split(",");				//operands ready
								
								for(int n=0;n<operands.length;n++)			//////standardization of variables
								{
									if(Character.isLowerCase(operands[n].charAt(0)))
										operands[n]=operands[n]+Integer.toString(m);
								}
								//System.out.println(operands[1]);
								Literals literal=new Literals();			//literal ready
								literal.Operator=key[0];					
								literal.Operands=operands;
								
								clause.sentence.add(literal);				//making list of literals
								
								
							}
						}
						
						//adding clause to KB for constituent literals
						//for()
						for(int p=0;p<clause.sentence.size();p++)
						{
							keyinKB=clause.sentence.get(p).Operator;
						if(!(KB.containsKey(keyinKB)))
						{
							List<Clauses> ls=new ArrayList<Clauses>();
							ls.add(clause);
							
								KB.put(keyinKB,ls);
						}
						else
						{
							List<Clauses> ls=KB.get(keyinKB);
							ls.add(clause);
							KB.put(keyinKB,ls);
						}
						}
						m++;
					}
					
					
					
				}
				
				System.out.println("Knowledge base-----------------------");
				//hw.displayKB(KB);
				
				
				///////////////////////////////////Start Resolution Loop/////////////////////////////////////////
				for(int i=0;i<noOfQueries;i++)
				{
					
					int flag=0;
					String keyRemove=new String();
					String query1=queries.get(i);
					StringBuilder builder=new StringBuilder();
					////////////////////////////////////////////////////////////////////////////////////////////////
					for(int d=0;d<query1.length();d++)
					{
						if(query1.charAt(d)==' ')
							continue;
						else
						{
							if(query1.charAt(d)=='=')
							{
								builder.append(' ');
								builder.append('=');
							}
							else if(query1.charAt(d)=='>')
							{
								builder.append('>');
								builder.append(' ');
							}
							else if(query1.charAt(d)=='&')
							{
								builder.append(' ');
								builder.append('&');
								builder.append(' ');
							}
							else if(query1.charAt(d)=='|')
							{
								builder.append(' ');
								builder.append('|');
								builder.append(' ');
							}
							else
							{
								builder.append(query1.charAt(d));
							}
						}
					}
					String query=builder.toString();
					
					////////////////////////////////////////////////////////////////////////////////////////////////
					String predicate=new String();
					//KBcopy=KB.;
					Clauses clause1=new Clauses();
					if(query.charAt(0)== '~')
					{
						predicate=query.substring(1);
						String[] key=predicate.split("\\(");
								
								String[] op=key[1].split("\\)");
								String[] operands=op[0].split(",");
								
								
								Literals literal=new Literals();
								literal.Operator=key[0];
								literal.Operands=operands;
								
								clause1.sentence.add(literal);
						 
						
						if(!(KB.containsKey(key[0])))
						{
							List<Clauses> ls=new ArrayList<Clauses>();
							ls.add(clause1);
							
								KB.put(key[0],ls);
						}
						else
						{
							List<Clauses> ls=KB.get(key[0]);
							ls.add(clause1);
							KB.put(key[0],ls);
						}
					}
					else
					{
						predicate="~"+query;
						String[] key=predicate.split("\\(");
						
						String[] op=key[1].split("\\)");
						String[] operands=op[0].split(",");
						
						
						Literals literal=new Literals();
						literal.Operator=key[0];
						literal.Operands=operands;
						
						clause1.sentence.add(literal);
						
						if(!(KB.containsKey(key[0])))
						{
							List<Clauses> ls=new ArrayList<Clauses>();
							ls.add(clause1);
							
								KB.put(key[0],ls);
								flag=1;
								keyRemove=key[0];
						}
						else
						{
							List<Clauses> ls=KB.get(key[0]);
							ls.add(clause1);
							
							KB.put(key[0],ls);
							flag=2;
							keyRemove=key[0];
						}
					}
				Stack<String> infinity=new Stack<String>();
				
				infinity.push(clause1.sentence.get(0).Operator);
				boolean boo=hw.resolution(clause1,infinity);
				
				if(flag==1)
				{
					KB.remove(keyRemove);
				}
				else if(flag==2)
				{
					//ls1.remove((ls1.size()-1));
					List<Clauses> ls1=KB.get(keyRemove);
					ls1.remove(ls1.size()-1);
					KB.put(keyRemove, ls1);
				}
				
				//List<Clauses> ls2=KB.get(keyRemove);
				if(boo==true)
				{
					printWriter.println("TRUE");
					System.out.println("TRUE");
				}
				else
					{
					printWriter.println("FALSE");
					System.out.println("FALSE");
					}
				}
				
				/////////////////////////////End Resolution Loop/////////////////////////////////////////
				
				
				
			}
			catch(IOException e	)
			{
				e.printStackTrace();
			}
			finally
			{
				try {
					bufferedReader.close();
					printWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	
	
	public boolean resolution(Clauses query,Stack<String> infinity)
	{
		
		List<Literals> list_ltr=query.sentence;      //// is negative of input query
		for(int i=0;i<list_ltr.size();i++)			//// parsing through every literal of query
		{
			String pre=new String();						///pre-String  pre-to find opposite literal in KB for resolution
			if(list_ltr.get(i).Operator.charAt(0)=='~')			//// eg: pre= ~A
			{
				pre=list_ltr.get(i).Operator.substring(1);
			}
			else
			{
				pre="~"+list_ltr.get(i).Operator;
			}
			
			if(KB.containsKey(pre))						///if KB contains then process else next literal
			{
				List<Clauses> lc=KB.get(pre);
				for(int j=0;j<lc.size();j++)							///loop for parsing various clauses
				{
					
					
					
					///*******************Declare string to push on stack*****************************/////////////////
					String stackString=new String();
					String stackString1=new String();
					boolean bool;
					HashMap<String, String> theta=new HashMap<String,String>();
					//HashMap<String, String> check=new HashMap<String,String>();  //**********************
					for(int k=0;k<lc.get(j).sentence.size();k++)		///loop for parsing literals
					{
						if(lc.get(j).sentence.get(k).Operator.equals(pre))	//(ERROR(Removed)-pre is String)literal in clause found(which is opposite from query)
						{
							bool=Unify(lc.get(j).sentence.get(k).Operands,list_ltr.get(i).Operands,theta);	//Unification
							if(bool== true)
							{
								
								List<Literals> lit_list=new ArrayList<Literals>();
								//propagating and eliminating
								//String infite_loop=new String();
								
								////////////////////////For left side////////////////////////////////////////////////////////////////
								for(int l=0;l<lc.get(j).sentence.size();l++)		///loop for parsing literals
								{
									if(lc.get(j).sentence.get(l).Operator.equals(pre))		///(ERROR(Removed)-pre is String)for elimination of unified literal
									{
										stackString1=stackString1+lc.get(j).sentence.get(l).Operator;
										continue;
									}
									Literals literal2=new Literals();
									literal2.Operator=lc.get(j).sentence.get(l).Operator;
									///***************stack push string************************////
									stackString=stackString+literal2.Operator;
									stackString1=stackString1+literal2.Operator;
									String[] ope=lc.get(j).sentence.get(l).Operands;
									String[] new_ope=new String[ope.length];
									for(int q=0;q<ope.length;q++)					////loop for parsing arguments
									{
										
										if(theta.containsKey(ope[q]))
											new_ope[q]=theta.get(ope[q]);
										else
											new_ope[q]=ope[q];
	
										
									}
									literal2.Operands=new_ope;
									lit_list.add(literal2);
								}
								/////////////////////////////////////////for right side////////////////////////////////////////////////
								for(int l=0;l<list_ltr.size();l++)		///loop for parsing literals
								{
									if(list_ltr.get(l).Operator.equals(list_ltr.get(i).Operator))		///for elimination of unified literal
									{
										continue;
									}
									Literals literal2=new Literals();
									literal2.Operator=list_ltr.get(l).Operator;
									///***************stack push string************************////
									stackString=stackString+literal2.Operator;
									String[] ope=list_ltr.get(l).Operands;
									String[] new_ope=new String[ope.length];
									for(int q=0;q<ope.length;q++)					////loop for parsing arguments
									{
										if(theta.containsKey(ope[q]))
											new_ope[q]=theta.get(ope[q]);
										else
											new_ope[q]=ope[q];
										
									}
									literal2.Operands=new_ope;
									lit_list.add(literal2);
								}
								
								/////Tautology
								for(int z=0;z<(lit_list.size()-1);z++)
								{
									for(int y=z+1;y<lit_list.size();y++)
									{
										String nw=new String();
										if(lit_list.get(z).Operator.charAt(0)=='~')
											nw=lit_list.get(z).Operator.substring(1);
										else
											nw="~"+lit_list.get(z).Operator;
										
										
										if(lit_list.get(y).Operator.equals(nw))
										{
											//unify and update theta
											bool=Unify(lit_list.get(z).Operands,lit_list.get(y).Operands,theta);
											lit_list.remove(y);
											lit_list.remove(z);
											//delete(y);
											//delete(z)
										}
									}
								}
								
								for(int z=0;z<(lit_list.size());z++)
								{
									for(int y=0;y<lit_list.get(z).Operands.length;y++)
									{
										if(theta.containsKey(lit_list.get(z).Operands[y]))
											lit_list.get(z).Operands[y]=theta.get(lit_list.get(z).Operands[y]);
										
									}
								}
								
								////End of tautology
								
								
								//Recursive DFS
								Clauses cla=new Clauses();
								if(lit_list.size()==0)				//if blank list of literals formed then resolved to true 
									return true;
								
								///***************stack push string (check in stack if present then return false)************************////
								///if not present then push on stack
								if(infinity.contains(stackString) )
								{
									break;
								}
								else
								{
									infinity.push(stackString);
									//infinity.push(stackString1);
								}
								if(infinity.contains(stackString1) )
								{
									break;
								}
								else
								{
									infinity.push(stackString1);
									//infinity.push(stackString1);
								}
								cla.sentence=lit_list;
								
								///for infinite loop
								//if(infinity.containsKey(lc.get(j).sentence))
								//lc.get(j).sentence;
								
								boolean b=resolution(cla,infinity);//,infinity);
								//////****************pop from stack*******************************////////////
								infinity.pop();
								infinity.pop();
								if(b==true)
									return true;
								else 
									break;
							}
							else
								break;
						}
					}
				}
				
			}
			/*else
				continue;*/
		}
		return false;
		
	}
	
	public boolean Unify(String[] left_variable,String[] right_constant,HashMap<String, String> theta)
	{
		for(int r=0;r<left_variable.length;r++)
		{
			if(!(left_variable[r].equals(right_constant[r])))
			{
				if(Character.isLowerCase(left_variable[r].charAt(0)))
				{
					theta.put(left_variable[r], right_constant[r]);
				}
				else if(Character.isLowerCase(right_constant[r].charAt(0)))
				{
					theta.put(right_constant[r], left_variable[r]);
				}
				else
					return false;
			}
			
		}
		return true;
	}
	
	
	public void displayKB(HashMap<String, List<Clauses> > KB)
	{
		
		/*Collection<List<Clauses>> c=KB.values();
		//System.out.println(c.);
		Iterator<List<Clauses>> iterator=c.iterator();
		
		while(iterator.hasNext())
		{
			System.out.println(iterator.next());
		}*/
		
		List<Clauses> ls=KB.get("A");
		System.out.println(ls.get(2).sentence.get(1).Operator);
		String[] str=ls.get(2).sentence.get(1).Operands;
		for(int i=0;i<str.length;i++)
		{
			System.out.println(str[i]);
		}
		//System.out.println(ls.get(0).sentence.get(0).Operands[0]);
		//Collection<List<Clauses>> c=KB.values();
		/*
		while(iterator.hasNext())
		{
			System.out.println(iterator.next());
		}*/
		
	}
}
