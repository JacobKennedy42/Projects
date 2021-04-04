import java.io.*;

public class AIHandwritingProject 
{
	//the file that stores the values for the reader net
	private static File readerSave;
	//the file that stores the values for the writer net
	private static File writerSave;
	private static int numImages;
	private static int numRows;
	private static int numCols;
	private static int imageSize;
	private static Node[] inputLayer;
	private static float[][] images;
	private static int[] labels;
	//the percent correct of the reader given by the reader's save file
	private static float readersReadPercent;
	//the percent correct of the reader given by the writer's save file (may be inaccurate to the reader's actual percent)
	private static float writersReadPercent;
	//the percent correct of the writer given by the writer's save file
	private static float writePercent;
	
	//the window that displays the current image
	private static ImageDisplay display;
	//is the display on or off
	private static final boolean DISPLAY_ON = true;
	
	//number of read tests we're going to do
	private static final int NUM_READ_TESTS = 10_000;
	//The number of read tests that are done before the net tries to save
	private static final int READ_TESTS_PER_SAVE = 20;
	//number of write tests we're going to do (should be around 60000 * NUM_READ_TESTS)
	private static final int NUM_WRITE_TESTS = 50_000_000;
	//The number of write tests that are done before the net tries to save
	private static final int WRITE_TESTS_PER_SAVE = 100_000;
	//number of images per batch
	private static final int BATCH_SIZE = 100;
	//whether or not random pictures are included
	private static final boolean HAS_RANDOM = false;
	//whether or not the tests are of the reader or the writer test
	private static final boolean IS_WRITER_TEST = false;
	//the number of nodes in the writer's hidden layers
	private static final int NUM_WRITER_NODES = 128;
	//the number of output nodes for a reader using random and non-random images, respectively
	private static final int RAND_NUM_OUTPUT_NODES = 11;
	private static final int NONRAND_NUM_OUTPUT_NODES = 10;
	
//	neural net that reads in an image and identifies which number it is
	private static NeuralNet MrReader;
//	neural net that takes in a numerical request and tries to draw the number
	private static NeuralNet MrsWriter;
	
	public static void main(String[] args) throws IOException
	{
		//initiate the files
		initFiles();
		//initiate the input layer
		initInput();
		
		//make the display visible if needed
		if (DISPLAY_ON)
		{
			display = new ImageDisplay(numRows, numCols, inputLayer);
			display.setVisible(true);
		}

		if (IS_WRITER_TEST)
		{
			runWritingTests();
		}
		else
		{
//			display = new ImageDisplay(numRows, numCols, inputLayer);
//			display.setVisible(true);
			runReadingTests();
		}
	}
	
	//run the tests for the reader
	private static void runReadingTests () throws IOException
	{
//		float sumCost;
		int imageIndex;
		int numBatches = numImages / BATCH_SIZE;
		int numCorrect;
		float percentCorrect;
		
		//make the reader net
		MrReader = makeReader();
		
		for (int test = 0; test < NUM_READ_TESTS; test++)
		{
			//sumCost = 0;
			numCorrect = 0;
			imageIndex = 0;

			//shuffle the images before doing the test
			shuffleImages();
			//process the images batch by batch
			for (int batch = 0; batch < numBatches; batch++)
			{
				//process the batch
				for (int i = 0; i < BATCH_SIZE; i++)
				{
					//put the image into the input layer
					setImage(imageIndex);
					//sumCost += MrReader.runTest(labels[imageIndex]);
					//run the image through the reader and check for correctness
					if (MrReader.runTest(labels[imageIndex]))
					{
						numCorrect++;
					}
					imageIndex++;
				}
				
				//once the batch is processed, adjust the values in the net by the average of the changes found in backpropogate()
				MrReader.adjustVals();
			}
			
//			if (test > 40)
//			{
//				MrReader.printNet();
//			}
				
			//System.out.println("Test #" + test + ". Cost: " + (sumCost / numImages));
			
			percentCorrect = (numCorrect / (float) numImages) * 100;
			System.out.println("Test #" + test + ". Percent Correct: " + percentCorrect + "%");
			
			//every 50th test, try to save the net
			if (test % READ_TESTS_PER_SAVE == READ_TESTS_PER_SAVE - 1)
			{
				try
				{
					writeReaderSave(percentCorrect);
				}
				catch (IOException e)
				{
					System.out.println("Could not write save.");
				}
			}
		}
		System.out.println("Reading tests complete.");
	}
	
	//run the tests for the writer
	private static void runWritingTests () throws IOException
	{
		int numCorrect = 0;
		float percentCorrect = 0;
		
		//make the writer and reader nets
		MrsWriter = makeWriter();
		MrReader = makeReader(MrsWriter);
		
		if (DISPLAY_ON)
		{
			display = new ImageDisplay(numRows, numCols, MrsWriter.getOutput());
			display.setVisible(true);
		}
		
		//the previous and current requested numbers
		int prevRequest = 0;
		int newRequest = 0;
		
		for (int test = 0; test < NUM_WRITE_TESTS; test++)
		{
			prevRequest = newRequest;
			newRequest = (int) (Math.random() * inputLayer.length);
			//input the new requested number into the input layer
			setRequest(prevRequest, newRequest);
			
			//the writer tries to draw the requested number
			MrsWriter.makeGuess();
			//the reader guesses what the writer drew, compares what was requested to what was guessed, and sends the reader
			//feedback. record if the reader's guess was correct
			if (MrReader.runTest(newRequest))
			{
				numCorrect++;
			}
			
			//the writer then changes its structure to better draw the requested number
			MrsWriter.backpropogate();
						
			//every 100th test, adjust the values in the writer
			if (test % BATCH_SIZE == BATCH_SIZE - 1)
			{
				MrsWriter.adjustVals();
			}
		
//			MrsWriter.printNet();
			
			//calculate and display the percent correct every 10000 tests
			if (test % 1000 == 999)
			{				
				percentCorrect = numCorrect / 10f; // numCorrect/1000 * 100 
				System.out.println("Test #" + test + ". Percent Correct: " + percentCorrect + "% Cost: " +
									MrsWriter.getCost(newRequest));
				numCorrect = 0;
				
				if (DISPLAY_ON)
				{
					display.repaint();
				}
			}
			
			//every 100,000th test, try to save the writer to its save file
			if (test % WRITE_TESTS_PER_SAVE == WRITE_TESTS_PER_SAVE - 1)
			{
				try
				{
					writeWriterSave(percentCorrect);
				}
				catch (IOException e)
				{
					System.out.println("Could not write save.");
				}
			}
		}
		
		System.out.println("Writing tests complete.");
	}

	//reads in and sets up the information from the files
	private static void initFiles () throws IOException
	{	
		readImageFile();
//		BitInputStream imageFile = new BitInputStream(new File("C:\\Users\\Jacob Kennedy\\Desktop\\handwritten numbers\\train-images-idx3-ubyte"));
//		//check for the magic number at the beginning
//		if (imageFile.readBits(32) == 2051)
//		{
//			numImages = imageFile.readBits(32);
//			numRows = imageFile.readBits(32);
//			numCols = imageFile.readBits(32);
//			imageSize = numRows * numCols;
//			readImages(imageFile);
//		}
//		else
//		{
//			System.out.println("File not an inputLayer file.");
//		}
		
		readLabelFile();
//		BitInputStream labelFile = new BitInputStream (new File("C:\\Users\\Jacob Kennedy\\Desktop\\handwritten numbers\\train-labels-idx1-ubyte"));
//		//check for the magic number at the beginning
//		if (labelFile.readBits(32) == 2049)
//		{
//			if (labelFile.readBits(32) != numImages)
//			{
//				System.out.println("Mismatch between number of images and number of labels.");
//			}
//			
//			else
//			{
//				readLabels(labelFile);
//			}
//		}
//		else
//		{
//			System.out.println("File not an label file.");
//		}
		
		//add the random images if necessary
		if (HAS_RANDOM)
		{
			addRandom();
		}
	}
	
	//read in the images from the file
	private static void readImageFile() throws IOException
	{
		BitInputStream imageFile = new BitInputStream(new File("Handwritten Numbers\\train-images-idx3-ubyte"));
//		BitInputStream imageFile = new BitInputStream(new File("C:\\Users\\Jacob Kennedy\\Desktop\\handwritten numbers\\train-images-idx3-ubyte"));
		//check for the magic number at the beginning
		if (imageFile.readBits(32) == 2051)
		{
			numImages = imageFile.readBits(32);
			numRows = imageFile.readBits(32);
			numCols = imageFile.readBits(32);
			imageSize = numRows * numCols;
			
			//only get the images if there going to be reading tests
			if (!IS_WRITER_TEST)
			{
				images = new float[numImages][imageSize];			
				float [] tempImage;
				for (int i = 0; i < numImages; i++)
				{
					tempImage = new float[imageSize];
					for (int j = 0; j < imageSize; j++)
					{
						tempImage[j] = imageFile.read() / 255f;
					}
					
					images[i] = tempImage;
				}
			}
		}
		else
		{
			System.out.println("File not an inputLayer file.");
		}
		
		imageFile.close();
	}
	
	//read in the labels from the file
	private static void readLabelFile() throws IOException
	{
		//only read the label file if reading tests will be performed
		if (!IS_WRITER_TEST)
		{
			BitInputStream labelFile = new BitInputStream (new File("Handwritten Numbers\\train-labels-idx1-ubyte"));
//			BitInputStream labelFile = new BitInputStream (new File("C:\\Users\\Jacob Kennedy\\Desktop\\handwritten numbers\\train-labels-idx1-ubyte"));
			//check for the magic number at the beginning
			if (labelFile.readBits(32) == 2049)
			{
				if (labelFile.readBits(32) != numImages)
				{
					System.out.println("Mismatch between number of images and number of labels.");
				}
				
				else
				{
					labels = new int[numImages];
					for (int i = 0; i < numImages; i++)
					{
						labels[i] = labelFile.read();
					}				
				}
			}
			else
			{
				System.out.println("File not an label file.");
			}
			
			labelFile.close();
		}	
	}
	
	//create the reader net using the reader's save file
	private static NeuralNet makeReader () throws IOException
	{
		DataInputStream fileReader = getReaderSave();
		
		//only read from the read file if it is not empty
		if (readerSave.length() != 0)
		{
			int numOutputNodes = fileReader.readInt();
		
			return new NeuralNet(fileReader, numOutputNodes, inputLayer);
//			return new NeuralNet(numOutputNodes, inputLayer);
		}
		
		if (HAS_RANDOM)
		{
			return new NeuralNet(RAND_NUM_OUTPUT_NODES, inputLayer);
		}
		
		return new NeuralNet(NONRAND_NUM_OUTPUT_NODES, inputLayer);
	}
	
	//create the reader and attach it to the writer
	private static NeuralNet makeReader (NeuralNet inWriter) throws IOException
	{
		DataInputStream fileReader = getReaderSave();
//		int numOutputNodes = fileReader.readInt();
//		
//		return new NeuralNet(fileReader, numOutputNodes, inWriter);
		
		//only read from the read file if it is not empty
		if (readerSave.length() != 0)
		{
			int numOutputNodes = fileReader.readInt();
		
			return new NeuralNet(fileReader, numOutputNodes, inWriter);
//					return new NeuralNet(numOutputNodes, inputLayer);
		}
		
		if (HAS_RANDOM)
		{
			return new NeuralNet(RAND_NUM_OUTPUT_NODES, inWriter);
		}
		
		return new NeuralNet(NONRAND_NUM_OUTPUT_NODES, inWriter);
	}
	
	//create the writer using the writer's save file
	private static NeuralNet makeWriter () throws IOException
	{
		DataInputStream fileReader = getWriterSave();
		
//		return new NeuralNet(fileReader, imageSize, NUM_WRITER_NODES, inputLayer);
		return new NeuralNet(imageSize, NUM_WRITER_NODES, inputLayer);
	}
	
	//get the reader's save file
	private static DataInputStream getReaderSave () throws IOException
	{
		if (HAS_RANDOM)
		{
			readerSave = new File("saves\\readerSave");
		}
		else
		{
			readerSave = new File("saves\\readerSaveNoRandom");
		}
		
		DataInputStream fileReader;
		
		//only read the file if it exists, otherwise create a new file
		if (readerSave.exists())
		{
			fileReader = new DataInputStream(new FileInputStream(readerSave));
			readersReadPercent = fileReader.readFloat();
		}
		else
		{
			readerSave.createNewFile();
			fileReader = new DataInputStream(new FileInputStream(readerSave));
			readersReadPercent = 0;
		}
		
		return fileReader;
	}
	
	//write the state of the reader to the save file
	private static void writeReaderSave (float inPercent) throws IOException
	{
		//only save the current state if it is better than the last state
		if (inPercent >= readersReadPercent)
		{
			System.out.println("Saving Reader Net");
			
			DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(readerSave));
			//write the current net's percent correct
			fileWriter.writeFloat(inPercent);
			//write the number of output nodes the net has
			if (HAS_RANDOM)
			{
				fileWriter.writeInt(RAND_NUM_OUTPUT_NODES);
			}
			else
			{
				fileWriter.writeInt(NONRAND_NUM_OUTPUT_NODES);
			}
			//write the state of the net
			MrReader.writeSave(fileWriter);
			readersReadPercent = inPercent;
			
			fileWriter.flush();
			fileWriter.close();
		}
		else
		{
			System.out.println("the last saved net state is better than the current one.");
		}
	}
	
	//get the writer's save file
	private static DataInputStream getWriterSave () throws IOException
	{
		if (HAS_RANDOM)
		{
			writerSave = new File("saves\\writerSave" + NUM_WRITER_NODES);
//			writerSave = new File("C:\\\\Users\\\\Jacob Kennedy\\\\Desktop\\\\handwritten numbers\\\\writerSave"
//									+ NUM_WRITER_NODES);
		}
		else
		{
			writerSave = new File("saves\\writerSaveNoRandom" + NUM_WRITER_NODES);
//			writerSave = new File("C:\\\\Users\\\\Jacob Kennedy\\\\Desktop\\\\handwritten numbers\\\\writerSaveNoRandom"
//									+ NUM_WRITER_NODES);
		}
		
		DataInputStream fileReader;
		
		//only read the file if it exists, otherwise create a new file
		if (writerSave.exists())
		{
			fileReader = new DataInputStream(new FileInputStream(writerSave));
			writePercent = fileReader.readFloat();
			writersReadPercent = fileReader.readFloat();
		}
		else
		{
			writerSave.createNewFile();
			fileReader = new DataInputStream(new FileInputStream(writerSave));
			writePercent = 0;
			writersReadPercent = 0;
		}
		
		return fileReader;
	}
	
	//write to the writer's save file
	private static void writeWriterSave (float inPercent) throws IOException
	{
		//immediately save if the percent from the reader's file is higher than the readersReadPercent from the writer's file
		// (this means that the reader has been improved at some point and so is the new standard)
		//if the readersReadPercent from the reader's and writer's files are equal, then save if the writer's percentage improved
		if (readersReadPercent > writersReadPercent || 
			(readersReadPercent == writersReadPercent && inPercent >= writePercent))
		{
			System.out.println("Saving Writer Net");
			
			DataOutputStream fileWriter = new DataOutputStream(new FileOutputStream(writerSave));
			//write the writer's percent correct
			fileWriter.writeFloat(inPercent);
			//write the reader's percent correct
			fileWriter.writeFloat(readersReadPercent);
			
			//write the state of the net
			MrsWriter.writeSave(fileWriter);
			writersReadPercent = readersReadPercent;
			writePercent = inPercent;			
			
			fileWriter.flush();
			fileWriter.close();
		}
		else
		{
			System.out.println("the last saved net state is better than the current one.");
		}
	}
	
	//add random images and labels
	private static void addRandom ()
	{	
		//only add random images if reading tests will be performed
		if (!IS_WRITER_TEST)
		{
			int temp = numImages * 2;
			int i = 0;
			float[][] tempImages = new float[temp][imageSize];
			int[] tempLabels = new int[temp];
			
			//copy the images and labels into the new image and label lists
			while (i < images.length)
			{
				tempImages[i] = images[i];
				tempLabels[i] = labels[i];
				i++;
			}
			
			//double numImages and add the random images
			numImages = temp;
			float[] tempImage;
			while (i < tempImages.length)
			{
				tempImage = new float[imageSize];
				for (int j = 0; j < tempImage.length; j++)
				{
					tempImage[j] = (float) Math.random();
				}
				tempImages[i] = tempImage;
				tempLabels[i] = 10;
				i++;
			}
			
			images = tempImages;
			labels = tempLabels;
		}
	}
	
	//Initialize the inputLayer (fill it with nodes)
	private static void initInput ()
	{
		if (IS_WRITER_TEST)
		{
			inputLayer = new Node[10];
			
			for (int i = 0; i < inputLayer.length; i++)
			{
				inputLayer[i] = new Node(0);
			}
		}
		else
		{
			inputLayer = new Node[imageSize];
			
			for (int i = 0; i < inputLayer.length; i++)
			{
				inputLayer[i] = new Node(images[0][i]);
			}
		}		
	}
	
	//put an image into the input layer
	private static void setImage (int inIndex)
	{
		for (int i = 0; i < inputLayer.length; i++)
		{
			inputLayer[i].setVal(images[inIndex][i]);
		}
		
		if (DISPLAY_ON)
		{
			display.repaint();
		}
	}
	
	//put a request into the input layer (for efficiency, only change the requested node and reset the previously requested node)
	private static void setRequest (int prevRequest, int inRequest)
	{
		//reset the previously requested node
		inputLayer[prevRequest].setVal(0);
		//light up the requested node
		inputLayer[inRequest].setVal(1);
	}
	
	//shuffle the images and labels
	private static void shuffleImages()
	{
		float[][] newImages = new float[numImages][imageSize];
		int[] newLabels = new int[numImages];
		int randIndex;
		//the length of the old list of images
		int oldLength = images.length;
		for (int i = 0; i < images.length; i++)
		{
			randIndex = (int) (Math.random() * oldLength);
			newImages[i] = images[randIndex];
			newLabels[i] = labels[randIndex];
			//move the last element of images and labels into the random index
			images[randIndex] = images[oldLength - 1];
			labels[randIndex] = labels[oldLength -1];
			oldLength--;
		}
		
		images = newImages;
		labels = newLabels;
	}
	
	public static int getNumImages()
	{
		return numImages;
	}
	
	public static int getRows ()
	{
		return numRows;
	}
	
	public static int getCols ()
	{
		return numCols;
	}
	
	public static int getImageSize()
	{
		return imageSize;
	}
	
	public static int getBatchSize ()
	{
		return BATCH_SIZE;
	}
}
