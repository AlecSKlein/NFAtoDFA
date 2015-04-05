import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class NFAtoDFA
{
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
		
		public NFA()
		{
			numStates = 0;
			alphabet = new ArrayList<Character>();
			allStates = new ArrayList<ArrayList<ArrayList<Integer> > >();
			initState = 0;
			finalStates = new ArrayList<Integer>();
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

		int n 		= 	nfa.numStates = Integer.parseInt(lines.remove(0));
		int init 	= 	nfa.initState = Integer.parseInt(lines.remove(lines.size()-1));
		String l2 	= 	lines.remove(0);
		int alphabetSize;

		char letter = '\0';
		for(int i = 0; i < l2.length(); i++)
		{
			letter = l2.charAt(i);
			if(charIsAlpha(letter))
				nfa.alphabet.add(letter);
		}

		alphabetSize = nfa.alphabet.size();

		for(int i = 0; i < alphabetSize; i++)
			System.out.print(nfa.alphabet.get(i) + "\t");
		System.out.println();

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

		//System.out.println();

		for(int i = 0; i < states.size(); i++)
		{
			String[] temp = states.get(i).split("S");
			ArrayList<String> temp2 = new ArrayList<String>();
			//System.out.println("New temp");

			for(String s: temp)
			{
				//System.out.println("Adding " + s + " to this temp");
				temp2.add(s);
			}
			tempStates.add(temp2);
		}

		//System.out.println();

		ArrayList<ArrayList<ArrayList<Integer> > > all_states = new ArrayList<ArrayList<ArrayList<Integer> > >();
		for(int i = 0; i < tempStates.size(); i++)
		{
			ArrayList<ArrayList<Integer> > full_state = new ArrayList<ArrayList<Integer> >();
			for(int j = 0; j < tempStates.get(i).size(); j++)
			{
				ArrayList<Integer> single_state = new ArrayList<Integer>();
				String temp = tempStates.get(i).get(j);
				//System.out.print(temp);
				//System.out.println("\nStripping");
				temp = temp.replace("{", "").replace("}", "");
				//System.out.println(temp + "\nStripped\n");
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

		for(int i = 0; i < all_states.size(); i++)
		{
			for(int j = 0; j < all_states.get(i).size(); j++)
			{
				System.out.print("{");
				for(int k = 0; k < all_states.get(i).get(j).size(); k++)
				{
					System.out.print(all_states.get(i).get(j).get(k) + ",");
				}
				System.out.print("}");
			}
			System.out.println();
		}

		return all_states;
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
	}
}