package com.cjwatts.wally.util;

import java.util.Iterator;
import java.util.Map.Entry;

import org.openimaj.data.RandomData;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.MapBackedDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.experiment.dataset.split.TestSplitProvider;
import org.openimaj.experiment.dataset.split.TrainSplitProvider;
import org.openimaj.experiment.dataset.split.ValidateSplitProvider;
import org.openimaj.experiment.validation.ValidationData;
import org.openimaj.experiment.validation.cross.CrossValidationIterable;

/**
 * Implementation adapted from {@link GroupedRandomSplitter}
 * 
 * @author Chris Watts (cw17g12@ecs.soton.ac.uk), Jonathon Hare (jsh2@ecs.soton.ac.uk)
 */
public class RandomSplitter<KEY, INSTANCE>
		implements
		TrainSplitProvider<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>,
		TestSplitProvider<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>,
		ValidateSplitProvider<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>> {

	private GroupedDataset<KEY, ? extends ListDataset<INSTANCE>, INSTANCE> dataset;
	private GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> trainingSplit;
	private GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> validationSplit;
	private GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> testingSplit;
	private int numTraining;
	private int numValidation;
	private int numTesting;
	
	/**
	 * Construct the dataset splitter with the given target instance sizes for
	 * each group of the training, validation and testing data. The actual
	 * number of instances per subset and group will not necessarily be the
	 * specified number if there are not enough instances in the input dataset.
	 * Instances are assigned randomly with preference to the training set
	 * followed by the validation set. If, for example, you had 40 instances in
	 * the input dataset and requested a training size of 20,
	 * validation size of 15 and testing size of 10, then your actual testing
	 * set would only have 5 instances rather than the 10 requested. If any
	 * subset will end up having no instances of a particular group available an
	 * exception will be thrown.
	 * 
	 * @param dataset
	 *            the dataset to split
	 * @param numTraining
	 *            the number of training instances
	 * @param numValidation
	 *            the number of validation instances
	 * @param numTesting
	 *            the number of testing instances
	 */
	public RandomSplitter(
			GroupedDataset<KEY, ? extends ListDataset<INSTANCE>, INSTANCE> dataset,
			int numTraining, int numValidation, int numTesting) {
		this.dataset = dataset;
		this.numTraining = numTraining;
		this.numValidation = numValidation;
		this.numTesting = numTesting;
		
		recomputeSubsets();
	}

	public void recomputeSubsets() {
		trainingSplit = new MapBackedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>();
		validationSplit = new MapBackedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>();
		testingSplit = new MapBackedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>();

		if (dataset.size() < numTraining + 1)
			throw new RuntimeException(
					"Too many training examples; none would be available for validation or testing.");

		if (dataset.size() < numTraining + numValidation + 1)
			throw new RuntimeException(
					"Too many training and validation instances; none would be available for testing.");
		
		final int[] ids = RandomData.getUniqueRandomInts(
				Math.min(numTraining + numValidation + numTesting, dataset.size()), 0,
				dataset.size());
		
		int i = 0;
		for (final Entry<KEY, ? extends ListDataset<INSTANCE>> e : dataset.entrySet()) {
			final KEY key = e.getKey();
			final ListDataset<INSTANCE> allData = e.getValue();

			if (ids[i] < numTraining) {
				trainingSplit.put(key, allData);
			}
			else if (ids[i] < numTraining + numValidation) {
				validationSplit.put(key, allData);
			}
			else {
				testingSplit.put(key, allData);
			}
			
			i++;
		}
	}
	
	@Override
	public GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> getTestDataset() {
		return testingSplit;
	}

	@Override
	public GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> getTrainingDataset() {
		return trainingSplit;
	}

	@Override
	public GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> getValidationDataset() {
		return validationSplit;
	}

	/**
	 * Create a {@link CrossValidationIterable} from the dataset. Internally,
	 * this method creates a {@link GroupedRandomSplitter} to split the dataset
	 * into subsets of the requested size (with no test instances) and then
	 * produces an {@link CrossValidationIterable} that recomputes the subsets
	 * on each iteration through {@link #recomputeSubsets()}.
	 * 
	 * @param dataset
	 *            the dataset to split
	 * @param numTraining
	 *            the number of training instances per group
	 * @param numValidation
	 *            the number of validation instances per group
	 * @param numIterations
	 *            the number of cross-validation iterations to create
	 * @return the cross-validation datasets in the form of a
	 *         {@link CrossValidationIterable}
	 * 
	 * @param <KEY>
	 *            Type of dataset class key
	 * @param <INSTANCE>
	 *            Type of instances in the dataset
	 */
	public static <KEY, INSTANCE> CrossValidationIterable<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>
			createCrossValidationData(final GroupedDataset<KEY, ? extends ListDataset<INSTANCE>, INSTANCE> dataset,
					final int numTraining, final int numValidation, final int numIterations)
	{
		return new CrossValidationIterable<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>() {
			private GroupedRandomSplitter<KEY, INSTANCE> splits = new GroupedRandomSplitter<KEY, INSTANCE>(dataset,
					numTraining, numValidation, 0);

			@Override
			public Iterator<ValidationData<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>> iterator() {
				return new Iterator<ValidationData<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>>() {
					int current = 0;

					@Override
					public boolean hasNext() {
						return current < numIterations;
					}

					@Override
					public ValidationData<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>> next() {
						splits.recomputeSubsets();
						current++;

						return new ValidationData<GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE>>() {

							@Override
							public GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> getTrainingDataset() {
								return splits.getTrainingDataset();
							}

							@Override
							public GroupedDataset<KEY, ListDataset<INSTANCE>, INSTANCE> getValidationDataset() {
								return splits.getValidationDataset();
							}
						};
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException("Removal not supported");
					}
				};
			}

			@Override
			public int numberIterations() {
				return numIterations;
			}
		};
	}

}
