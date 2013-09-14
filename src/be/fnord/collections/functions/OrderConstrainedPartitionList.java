package be.fnord.collections.functions;
import java.util.HashSet;
import java.util.List;


public class OrderConstrainedPartitionList <T> {
	 
	/*
	 * Evans edits to a function from http://www.vogella.de/articles/JavaAlgorithmsPartitionCollection/article.html
	 * to generalize this you should paramaterize each of the following two functions to be like the bottom two functions.
	 * 
	 * Change the type parameter in PartitionListItem (currently String).   
	 */
	

	public static <T> PartitionList<T> joinSets(
			PartitionList<T> results,
			PartitionList<T> results2) {
		PartitionList<T> returnResults = new PartitionList<T>();
		
		for(PartitionListItem<T> newPartition : results){
			for(PartitionListItem<T> newPartition2 : results2){
				// Start with all elements of results on the left then filter through:
				PartitionListItem<T> mergedCollection = new PartitionListItem<T>();
				boolean switches[] = new boolean[newPartition.size() + newPartition2.size()];
				int i = 0; int numRight = newPartition2.size();
				for(PartitionListElement<T> element: newPartition){
					mergedCollection.add(element);
					switches[i] = false; i++;
				}
				for(PartitionListElement<T> element: newPartition2){
					mergedCollection.add(element);
					switches[i] = true; i++;
				}
				returnResults.add(mergedCollection);
				while(!testSwitch(switches, numRight)){
					PartitionListItem<T> submergedCollection = new PartitionListItem<T>();
					switches = nextPosition(switches, numRight);
					int part1 = 0; int part2 =0;
					
					for(int k = 0; k < newPartition.size() + newPartition2.size(); k++ ){
						if(switches[k]){
							submergedCollection.add(newPartition2.get(part2));
							part2++;
						}else{
							submergedCollection.add(newPartition.get(part1));
							part1++;
						}
					}

					
//					System.out.print("results : " );
//					for(boolean explainer : switches){
//						System.out.print( explainer == true ? "1" : "0");
//					}
//					System.out.println("");
					
					returnResults.add(submergedCollection);
						
					
				}
				
				
			}
		}
		
		HashSet<String> doubleChecker = new HashSet<String>();
		PartitionList<T> finalResults = new PartitionList<T>();
		
		for(PartitionListItem<T> newPartition: returnResults){
			String value = newPartition.toString();
			value = value.replaceAll( "[\\[\\]' ']", "" );
			//System.out.println("Value: " + value);
			if(!doubleChecker.contains(value)){
				doubleChecker.add(value);
				PartitionListItem<T> cleanResults = new PartitionListItem<T>();
				PartitionListElement<T> cleanWords = new PartitionListElement<T>();
				for(PartitionListElement<T> part : newPartition)
					for(T word : part){
						cleanWords.add(word);
					}
				cleanResults.add(cleanWords);
				finalResults.add(cleanResults);
			}
		}
		
		return finalResults; //returnResults;
	}
	
	public static boolean[] nextPosition(boolean switches[], int numTrue){
		String value = "";
		boolean retSwitches[] = new boolean [switches.length]; 
		for(int i = 0; i < switches.length; i++){
			value += switches[i] == true ? 1 : 0;
			
		}
		value = value.replaceAll( "[^\\d]", "" );
		int currentValue = Integer.parseInt(value,2);
		int count = 0;
		do{
			currentValue += 1;
			//System.err.println("currentValue = " + currentValue);
			String test = new String(Integer.toBinaryString(currentValue) + "");
			//System.err.println("currentValue = " + test);
			int length = test.length();
			test = test.replaceAll("1", "");
			if(length - test.length() == numTrue) break; 
		}while(count < 100);
		
		//System.out.println("Next value is " + Integer.toBinaryString(currentValue));
		String reverseSwitch = Integer.toBinaryString(currentValue) + "";
		int j = switches.length-1;
		for(int i = reverseSwitch.length()-1; i >= 0; i--){
			//System.err.println("i" + i + " revSw:" + reverseSwitch + " value =" + (reverseSwitch.charAt(i) == '1' ? true: false) + " char = "  + reverseSwitch.charAt(i));
			retSwitches[j] = (reverseSwitch.charAt(i) == '1' ? true: false);
			j--;
		}
		for(; j >=0 ; j--) retSwitches[j] =false;
		
		
//		System.out.println("results : " );
//		for(boolean i : retSwitches){
//			System.out.print( i == true ? "1" : "0");
//		}
		//System.out.println("Value:" +  Integer.toBinaryString(Integer.parseInt(value,2)) + " from " + value + " from " + Integer.parseInt(value,2));
		
		return retSwitches;
	}
	
	public static boolean testSwitch(boolean switches[], int numTrue){
		boolean last = true; int numFound = 0;
		for(int i = 0; i < switches.length ; i++){
			if(switches[i] == true && last == true) numFound++;
			else break;
		}
		
		if(numFound == numTrue)
			return true;
		return false;
	}
	


	// Function removes all the duplicate entries after generating all possible combos.
	public static <T> PartitionList<T> makePartitions(
			PartitionListElement<T> list) {
		T newElement = null; list.add(newElement); // <-- Bad hack but was not getting the right result
		PartitionList<T> returnResults = new PartitionList<T>();
		PartitionList<T> partition = eparition(list);
		HashSet<String> testDoubles = new HashSet<String>();
		for(PartitionListItem<T> results_part : partition){
			int i = 1;
			for(PartitionListElement<T> part : results_part){
				i += part.size();
//				for(T word : part)			// Removed to simply
//					i++;
			}

			if(i == list.size()){				
				if(!testDoubles.contains(results_part.toString())){
					testDoubles.add(results_part.toString());
					//System.out.println("Result:" + results_part.toString());
					returnResults.add(results_part);
				}
			}
		}
		
		return returnResults;
	}

	
	// Creates the partition. By chopping each partition from the partition maker into subpartitions
	private static <T> PartitionList<T> eparition(PartitionListElement<T> tail2) {
		PartitionList<T> returner = new PartitionList<T>();
		for(int i = 1; i <= tail2.size(); i++){
			
			PartitionListItem<T> partition = OrderConstrainedPartitionList.partition(tail2, i);
			returner.add(partition);
			if(partition.size() > 1){
				PartitionListElement<T> head =  partition.get(0);
				PartitionListElement<T> tail = new PartitionListElement<T>();
				for(int j = 1; j < partition.size(); j++){
					for(T backend : partition.get(j))
						tail.add(backend);
				}
				
				//if(count-- > 0) System.out.println(count + "::" + "Head:" + head.toString() + " ; Tail: " + tail.toString());
				
				PartitionList<T> tail_results = eparition(tail);
				for(PartitionListItem<T> tails : tail_results){	
					PartitionListItem<T> merged = new PartitionListItem<T>();
					merged.add(head);
					for(PartitionListElement<T> mergeTail : tails){					
						
						merged.add(mergeTail);
						
					}
					returner.add(merged);
					//if(count-- > 0) System.out.println(count + ":: added" + merged.toString()); 
				}
				
				
			}
			//if(count-- > 0) System.out.println(count + "::" +partition.size() + " <-- Number of partitions -->" + partition.toString()); 
		}
		return returner;
	}
	
	 
/**
	   * Returns consecutive {@linkplain List#subList(int, int) sublists} of a list,
	   * each of the same size (the final list may be smaller). For example,
	   * partitioning a list containing {@code [a, b, c, d, e]} with a partition
	   * size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list containing
	   * two inner lists of three and two elements, all in the original order.
	   *
	   * <p>The outer list is unmodifiable, but reflects the latest state of the
	   * source list. The inner lists are sublist views of the original list,
	   * produced on demand using {@link List#subList(int, int)}, and are subject
	   * to all the usual caveats about modification as explained in that API.
	   *
	   * * Adapted from http://code.google.com/p/google-collections/ 
	   *
	   * @param list the list to return consecutive sublists of
	   * @param size the desired size of each sublist (the last may be
	   *     smaller)
	   * @return a list of consecutive sublists
	   * @throws IllegalArgumentException if {@code partitionSize} is nonpositive
	   * 
	  
	   */


	  
	  public static <T> PartitionListItem<T> partition(PartitionListElement<T> list, int size) {
	 
	   if (list == null)
	      throw new NullPointerException(
	          "'list' must not be null");
	    if (!(size > 0))
	      throw new IllegalArgumentException(
	          "'size' must be greater than 0");

	    Partition<T> partition = new Partition<T>(list, size);
		return partition;
	  }

	  private static class Partition<T> extends PartitionListItem<T> {

		private static final long serialVersionUID = 1L;
		final PartitionListElement<T> list;
	    final int size;

	    Partition(PartitionListElement<T> list, int size) {
	      this.list = list;
	      this.size = size;
	    }

	    public PartitionListElement<T> get(int index) {
	      int listSize = size();
	      if (listSize < 0)
	        throw new IllegalArgumentException("negative size: " + listSize);
	      if (index < 0)
	        throw new IndexOutOfBoundsException(
	            "index " + index + " must not be negative");
	      if (index >= listSize)
	        throw new IndexOutOfBoundsException(
	            "index " + index + " must be less than size " + listSize);
	      int start = index * size;
	      int end = Math.min(start + size, list.size());
	      
	      PartitionListElement<T> returnListElemet = new PartitionListElement<T>();
	      for(T element: list.subList(start, end)){
	    	  returnListElemet.add(element);
	      }
	      return (PartitionListElement<T>) returnListElemet;
	    }

	    @Override
	    public int size() {
	      return (list.size() + size - 1) / size;
	    }

	    @Override
	    public boolean isEmpty() {
	      return list.isEmpty();
	    }
	  }

	  public static boolean breaker = false;
	public static PartitionList<String> joinPartitionedSets(
			PartitionList<String> results1, PartitionList<String> results2) {
		PartitionList<String> returnResults = new PartitionList<String>();
		for(PartitionListItem<String> item : results1){
			PartitionListElement<String> list4 = new PartitionListElement<String>(item);
			PartitionList<String> results5 = OrderConstrainedPartitionList.makePartitions(list4);
			
		
			PartitionList<String> results6 = OrderConstrainedPartitionList.joinSets(results5, results2);
			returnResults.addAll(results6);

		}
		
		return returnResults;
	}


}
