import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class NFAtoDFA
{
	public class DFA
	{
		ArrayList<Integer> acceptingStates;
		ArrayList<ArrayList<Integer> > DFAStates;
		ArrayList<HashMap<Character, Integer> > DFALanguage;
		public ArrayList<Character> alphabet;
		int initialState;
		
		public DFA()
		{
			acceptingStates = new ArrayList<Integer>();
			DFAStates = new ArrayList<ArrayList<Integer> >();
			DFALanguage = new ArrayList<HashMap<Character, Integer> >();
			alphabet = new ArrayList<Character>();
			initialState = 0;
		}

		public String toString()
		{
			String finalDFAString = "";
			finalDFAString += "To DFA:";
			for(ArrayList<Integer> state: DFAStates)
			{
				finalDFAString += state;
			}
			finalDFAString = finalDFAString.replace("[", "{").replace("]", "}");
			
			finalDFAString += "\nSigma:\t";
			for(Character letter: alphabet)
			{
				finalDFAString += letter + "\t";
			}
			int index = 0;
			finalDFAString += "\n";
			for(int i = 0; i < alphabet.size(); i++)
			{
				finalDFAString += "---------";
			}
			finalDFAString += "\n";
			for(HashMap<Character, Integer> hmap: DFALanguage)
			{
				finalDFAString += index + ":\t"; 
				for(Character c: alphabet)
				{
					if(c != '\\')
					{
						finalDFAString += hmap.get(c) + "\t";
					}
				}
				finalDFAString += "\n";
				index++;
			}

			for(int i = 0; i < alphabet.size(); i++)
			{
				finalDFAString += "---------";
			}
			finalDFAString += "\n";

			finalDFAString += "s: " + initialState + "\n";
			String accStates = acceptingStates.toString();
			finalDFAString += "A: " + accStates.replace("[", "{").replace("]", "}");

			return finalDFAString;
		}

		public void setAlphabet(NFA nfa)
		{
			for(Character letter: nfa.alphabet)
			{
				if(letter != '\\')
					alphabet.add(letter);
			}
		}
	}

	public class NFA
	{
		//First line
		public int numStates;
		//Second Line
		public ArrayList<Character> alphabet;
		//Third + numStates lines
		public ArrayList<ArrayList<ArrayList<Integer> > > allStates;
		//Fourth + numStates line
		public int initState;
		//Last line
		public ArrayList<Integer> finalStates;
		//Map
		public ArrayList<HashMap<Character, ArrayList<Integer> > > mapStates;
		
		public NFA()
		{
			numStates = 0;
			alphabet = new ArrayList<Character>();
			allStates = new ArrayList<ArrayList<ArrayList<Integer> > >();
			initState = 0;
			finalStates = new ArrayList<Integer>();
			mapStates = new ArrayList<HashMap<Character, ArrayList<Integer> > >();

		}

		public NFA(int n, ArrayList<Character> sigma, ArrayList<ArrayList<ArrayList<Integer> > > states, int initial, ArrayList<Integer> fStates)
		{
			numStates = n;
			for(char c: sigma)
				alphabet.add(c);
			for(ArrayList<ArrayList<Integer> > state: states)
				allStates.add(state);
			initState = initial;
			for(Integer fState: fStates)
				finalStates.add(fState);
			mapStates = new ArrayList<HashMap<Character, ArrayList<Integer> > >();
		}

		public String toString()
		{
			String finalString = "";

			finalString += "Sigma:";
			for(int i = 0; i < alphabet.size()-1; i++)
				finalString += alphabet.get(i) + " ";
			finalString += "\n------\n\t";

			for(char c: alphabet)
				finalString += c + "\t";
			finalString += "\n";

			int index = 0;
			for(ArrayList<ArrayList<Integer> > layer1: allStates)
			{
				finalString += index++ + ":\t";
				for(ArrayList<Integer> layer2: layer1)
				{
					finalString += ("{");
					for(int i: layer2)
						if(i != -1)
							finalString += (i + ",");
					finalString += "}\t";
				}
				finalString += ("\n");
			}
			finalString += "------\n";
			finalString += "s: " + initState + "\n";
			finalString += "A: {";
			for(int i: finalStates)
				finalString += i + ",";
			finalString += "}\n";
			finalString = finalString.replace(",}", "}");

			//System.out.println("Contents of map");
			//PRINTING CONTENTS OF HASHMAP
			for(HashMap<Character, ArrayList<Integer> > hmap: mapStates)
			{
				for(Character c: alphabet)
				{
					//System.out.print(hmap.get(c) + " ");
				}
				//System.out.println();
			}
			

			return finalString;
		}
	}

	public ArrayList<String> readFromFile(String filename)
	{
		ArrayList<String> allLines = new ArrayList<String>();
		File file = new File(filename);
		try
		{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null)
			{
				line = line.replace("\t", "");
				line = line.replace(" ", "");
				if(!line.equals(""))
					allLines.add(line);
			}
		}
		catch(IOException e)
		{
			//
		}
		return allLines;
	}

	public boolean charIsAlpha(char c)
	{
		if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
			return true;
		return false;
	}

	public void setStateMap(NFA nfa, ArrayList<ArrayList<ArrayList<Integer> > > states)
	{
		for(int i = 0; i < nfa.numStates; i++)
		{
			HashMap<Character, ArrayList<Integer> > tempmap = new HashMap<Character, ArrayList<Integer> >();
			int cIndex = 0;
			for(ArrayList<Integer> state: states.get(i))
			{
				tempmap.put(nfa.alphabet.get(cIndex), state);
				cIndex++;
			}
			nfa.mapStates.add(tempmap);
		}
	}

	public NFA extractNFA(ArrayList<String> lines)
	{
		/*
		
			Rules for an NFA file
		1)	First line = 	number of states, n
		2)	Second line = 	the alphabet, sigma
		3)	Third - n+2 = 	the individual states
		4)	N+2th		= 	the initial/starting state
		5)	N+3th		=	the set of final states

		*/

		NFA nfa = new NFA();

		String lastLine = lines.remove(lines.size()-1);
		while(!lastLine.substring(0, 1).equals("{"))
			lastLine = lines.remove(lines.size()-1);
		String[] fStates = lastLine.replace("{", "").replace("}", "").split(",");
		for(String s: fStates)
			nfa.finalStates.add(Integer.parseInt(s));

		int n 		= 	nfa.numStates = Integer.parseInt(lines.remove(0));
		int init 	= 	nfa.initState = Integer.parseInt(lines.remove(lines.size()-1));
		String l2 	= 	lines.remove(0);
		int alphabetSize;

		char letter = '\0';
		for(int i = 0; i < l2.length(); i++)
		{
			letter = l2.charAt(i);
			if(charIsAlpha(letter))
			{
				nfa.alphabet.add(letter);
			}
		}
		nfa.alphabet.add('\\');

		alphabetSize = nfa.alphabet.size();

		nfa.allStates = extractStates(n, alphabetSize, lines);

		return nfa;
	}

	public ArrayList<ArrayList<ArrayList<Integer> > > extractStates(int stateSize, int alphSize, ArrayList<String> states)
	{
		//This loop modifies the remaining states
		//{} becomes N for Null
		//}{ becomes S for Space
		//Numbers within the states are separated by commas
		//Ex) {1,2,3}S{2,3}S{N}
		for(int i = 0; i < states.size(); i++)
			states.set(i, ((states.get(i).split(":")[1]).replace("{}", "{N}")).replace("}{", "}S{"));

		//TODO: Split by S, add to list of lists
		//TODO: Then split by commas
		//TODO: Then add each resulting number to a list
		//TODO: When that list is complete, add that list to the final list

		ArrayList<ArrayList<String> > tempStates = new ArrayList<ArrayList<String> >();

		////System.out.println();

		for(int i = 0; i < states.size(); i++)
		{
			String[] temp = states.get(i).split("S");
			ArrayList<String> temp2 = new ArrayList<String>();
			////System.out.println("New temp");

			for(String s: temp)
			{
				////System.out.println("Adding " + s + " to this temp");
				temp2.add(s);
			}
			tempStates.add(temp2);
		}

		////System.out.println();

		ArrayList<ArrayList<ArrayList<Integer> > > all_states = new ArrayList<ArrayList<ArrayList<Integer> > >();
		for(int i = 0; i < tempStates.size(); i++)
		{
			ArrayList<ArrayList<Integer> > full_state = new ArrayList<ArrayList<Integer> >();
			for(int j = 0; j < tempStates.get(i).size(); j++)
			{
				ArrayList<Integer> single_state = new ArrayList<Integer>();
				String temp = tempStates.get(i).get(j);
				////System.out.print(temp);
				////System.out.println("\nStripping");
				temp = temp.replace("{", "").replace("}", "");
				////System.out.println(temp + "\nStripped\n");
				String[] transitionStates = temp.split(",");
				for(String s: transitionStates)
				{
					if(s.equals("N"))
						single_state.add(-1);
					else
						single_state.add(Integer.parseInt(s));
				}
				full_state.add(single_state);
			}
			all_states.add(full_state);
		}

		return all_states;
	}

	public DFA convertNFAtoDFA(NFA nfa)
	{
		DFA dfa = new DFA();
		Queue<ArrayList<Integer> > tempDFA = new LinkedList<ArrayList<Integer> >();
		ArrayList<ArrayList<Integer> > finalDFA = new ArrayList<ArrayList<Integer> >();
		ArrayList<HashMap<Character, Integer> > finalLanguage = new ArrayList<HashMap<Character, Integer> >();
		ArrayList<Integer> nullState = new ArrayList<Integer>();
		nullState.add(-1);
		int nullIndex = -1;
		int firstNullIndex = -1;
		HashMap<Character, Integer> nullAlphabet = new HashMap<Character, Integer>();

		//Given the NFA, find all Lambda transitions. nfa.initState and nfa.mapStates

		//We don't have to check the starting lambda state because it is the first
		//Additionally, we can add to the finalDFA because it's guaranteed unique
		tempDFA.offer(lambdaTransitions(nfa));
		finalDFA.add(tempDFA.peek());
		//System.out.println(tempDFA.peek());

		//This index monitors the index of the new language
		int tempDFAIndex = 0;

		//We should loop while tempDFA is not empty
		//After move and closure of all alphabet, check state for uniqueness
		//If the new state is unique, add it to the temp and final dfa set
		while(tempDFA.size() > 0)
		{
			//Create a new hashmap for the new index of tempdfa
			finalLanguage.add(new HashMap<Character, Integer>());
			//System.out.println("TDFA: " +  tempDFA.peek());
			for(Character c: nfa.alphabet)
			{
				if(c != '\\')
				{
					//Generate move for new letter using next tempdfa in queue
					ArrayList<Integer> tempmove = moveAlphabet(c, tempDFA.peek(), nfa.mapStates);
					//If it's not empty:
					if(tempmove.size() > 0)
					{
						//Generate new closure, check if it's unique
						ArrayList<Integer> tempclosure = closeAlphabet(tempmove, nfa.mapStates);
						//System.out.println("Letter: " + c + ", index: " + tempDFAIndex + ", Closure: " + tempclosure);
						int uniquestate = getUniqueState(finalDFA, tempclosure);
						//If it's unique (-1), add to temp/final dfa, add to language at finaldfa index
						if(uniquestate == -1)
						{
							for(Integer acceptingState: nfa.finalStates)
							{
								if(isInState(acceptingState, tempclosure))
								{
									dfa.acceptingStates.add(finalDFA.size());
								}
							}
							tempDFA.offer(tempclosure);
							finalDFA.add(tempclosure);
							finalLanguage.get(tempDFAIndex).put(c, finalDFA.size()-1);
						}
						//Otherwise, add to lanuage at pre-existing index
						else
						{
							finalLanguage.get(tempDFAIndex).put(c, uniquestate);
							//map at index of tempDFAIndex, list at letter, add uniquestate value
						}
					}
					if(tempmove.size() == 0)
					{
						int uniquestate = getUniqueState(finalDFA, nullState);
						if(uniquestate == -1)
						{
							nullIndex = finalDFA.size();
							firstNullIndex = tempDFAIndex+1;
							finalDFA.add(nullState);
							
							for(int i = 0; i < nfa.alphabet.size()-1; i++)
							{
								nullAlphabet.put(nfa.alphabet.get(i), finalDFA.size()-1);
							}
							finalLanguage.add(nullAlphabet);
							finalLanguage.get(tempDFAIndex).put(c, finalDFA.size()-1);
							tempDFAIndex++;
						}
						else
							finalLanguage.get(tempDFAIndex).put(c, uniquestate);
					}
				}
			}
			tempDFA.poll();
			tempDFAIndex++;
		}
		if(nullIndex != -1)
		{
			finalLanguage.remove(firstNullIndex);
			finalLanguage.add(nullIndex, nullAlphabet);
		}
		
		/*System.out.print("\nNEW LANGUAGE\n\t");
		for(Character c: nfa.alphabet)
		{
			if(c != '\\')
				//System.out.print(c + "\t");
		}
		*///System.out.println();

		int sigma = 0;
		for(HashMap<Character, Integer> hmap: finalLanguage)
		{
			//System.out.print(sigma + ":\t");
			for(Character c: nfa.alphabet)
			{
				if(c != '\\')
				{
					//System.out.print(hmap.get(c) + "\t");
				}
			}
			//System.out.println();
			sigma++;
		}
		//System.out.println();

		dfa.DFAStates = finalDFA;
		dfa.DFALanguage = finalLanguage;
		dfa.initialState = nfa.initState;
		return dfa;
	}

	//Starting lambdaTransition method, returns the list
	public ArrayList<Integer> lambdaTransitions(NFA nfa)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(nfa.initState);
		lambdaTransitions(nfa.initState, list, nfa.mapStates);
		return list;
	}

	//Recursive method for lambdaTransitions
	public void lambdaTransitions(int transition, ArrayList<Integer> state, ArrayList<HashMap<Character, ArrayList<Integer> > > mapOfStates)
	{
		for(Integer i: mapOfStates.get(transition).get('\\'))
		{
			if(!isInState(i, state) && i != -1)
			{
				state.add(i);
				lambdaTransitions(i, state, mapOfStates);
			}
		}
	}

	public ArrayList<Integer> moveAlphabet(char letter, ArrayList<Integer> lambdaState, ArrayList<HashMap<Character, ArrayList<Integer> > > mapOfStates)
	{
		ArrayList<Integer> moveList = new ArrayList<Integer>();
		for(Integer i: lambdaState)
		{
			ArrayList<Integer> j = (mapOfStates.get(i)).get(letter);
			for(Integer transition: j)
			{
				if(transition != -1 && !isInState(transition, moveList))
					moveList.add(transition);
			}
		}
		return moveList;
	}

	public ArrayList<Integer> closeAlphabet(ArrayList<Integer> moveState, ArrayList<HashMap<Character, ArrayList<Integer> > > mapOfStates)
	{
		ArrayList<Integer> closureState = new ArrayList<Integer>();
		for(Integer i: moveState)
		{
			if(!isInState(i, closureState))
				closureState.add(i);
			closeAlphabet(i, closureState, mapOfStates);
		}
		return closureState;
	}

	public void closeAlphabet(int transition, ArrayList<Integer> state, ArrayList<HashMap<Character, ArrayList<Integer> > > mapOfStates)
	{
		ArrayList<Integer> temp = mapOfStates.get(transition).get('\\');
		for(Integer i: temp)
		{
			if(!isInState(i, state) && i != -1)
			{
				state.add(i);
				closeAlphabet(i, state, mapOfStates);
			}
		}
	}

	public boolean isInState(int transition, ArrayList<Integer> state)
	{
		for(Integer i: state)
			if(transition == i)
				return true;
		return false;
	}

	public int getUniqueState(ArrayList<ArrayList<Integer> > finalStates, ArrayList<Integer> newState)
	{
		for(int i = 0; i < finalStates.size(); i++)
		{
			if(!isUniqueState(finalStates.get(i), newState))
				return i;
		}
		return -1;
	}

	public boolean isUniqueState(ArrayList<Integer> finalState, ArrayList<Integer> newState)
	{
		if(finalState.size() != newState.size())
			return true;
		for(Integer i: newState)
		{
			if(!isInState(i, finalState))
				return true;
		}
		return false;
	}

	public static void main(String[] args)
	{
		NFAtoDFA test = new NFAtoDFA();
		String filename;
		if(args.length > 0)
			filename = args[0];
		else
			filename = "nfa1.nfa";
		ArrayList<String> lines = test.readFromFile(filename);

		NFA demo = test.extractNFA(lines);
		//THIS LINE MUST HAPPEN BECAUSE I PROGRAMMED THIS POORLY
		test.setStateMap(demo, demo.allStates);
		System.out.println(demo.toString());
		ArrayList<Integer> lambdadfa = test.lambdaTransitions(demo);
		DFA finalDFA = test.convertNFAtoDFA(demo);
		finalDFA.setAlphabet(demo);
		//System.out.println("Contents of final DFA");
		
		System.out.println(finalDFA.toString());
		//for(ArrayList<Integer> iList: finalDFA.DFAStates)
		//	//System.out.println(iList);

	}
}