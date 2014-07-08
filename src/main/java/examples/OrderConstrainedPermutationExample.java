package examples;

import java.io.BufferedWriter;
import java.io.FileWriter;

import be.fnord.util.functions.OCP.OrderConstrainedPartitionList;
import be.fnord.util.functions.OCP.PartitionList;
import be.fnord.util.functions.OCP.PartitionListElement;
import be.fnord.util.functions.OCP.PartitionListItem;

public class OrderConstrainedPermutationExample {

	public static void main(String[] args) {

		// Example of creating a set and partitioning it:
		System.out
				.println("Starting by creating a partitioned set of 4 elements:");
		PartitionListElement<String> list = new PartitionListElement<String>();
		list.add("one");
		list.add("two");
		list.add("three");
		list.add("four");
		// Call the make partition function to put into all possible partitions
		PartitionList<String> results = OrderConstrainedPartitionList
				.makePartitions(list);

		// Display results
		for (PartitionListItem<String> newPartition : results) {
			System.out.println("Partition: " + newPartition.toString());
		}

		System.out
				.println("Now creating a second partitioned set of 3 elements.");

		// Next we create a second partitioned set
		PartitionListElement<String> list2 = new PartitionListElement<String>();
		list2.add("A");
		list2.add("B");
		list2.add("C");
		PartitionList<String> results2 = OrderConstrainedPartitionList
				.makePartitions(list2);
		// Display results
		for (PartitionListItem<String> newPartition : results2) {
			System.out.println("Partition: " + newPartition);
		}

		// Join both sets
		PartitionList<String> results3 = OrderConstrainedPartitionList
				.joinSets(results, results2);
		System.out.println("Showing all merged sets:");
		for (PartitionListItem<String> newPartition : results3) {
			System.out.println("Partition: " + newPartition);
		}

		System.out
				.println("Now creating a Third partitioned set of 3 elements.");

		// Next we create a second partitioned set
		PartitionListElement<String> list3 = new PartitionListElement<String>();
		list3.add("z");
		list3.add("y");
		list3.add("x");
		PartitionList<String> results4 = OrderConstrainedPartitionList
				.makePartitions(list3);
		// Display results
		for (PartitionListItem<String> newPartition : results4) {
			System.out.println("Partition: " + newPartition);
		}

		try {
			// Create file
			FileWriter fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);

			// Join all sets
			PartitionList<String> results6 = OrderConstrainedPartitionList
					.joinPartitionedSets(results3, results4);
			System.out.println("Saving all merged sets to out.txt");
			for (PartitionListItem<String> newPartition : results6) {
				// System.out.println("Partition: " + newPartition);
				out.write("Partition: " + newPartition + "\n");
			}

			out.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Save is complete. ");

	}

}
