//Listens for transactions and adds them to the block, then adds the block to the block chain

import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;
import java.lang.Thread;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Scanner;
import java.util.concurrent.atomic.*;
import java.util.ArrayList;

public class Maintainer
{
	private static final int KEY_SIZE = 162;
	private static final int SIG_SIZE = 128;
//	private static final int T_PER_BLOCK = 65536; //transactions per block. By default, set to the unsigned max of a short, 65536
	private static final int T_PER_BLOCK = 4;
	private static final int T_SIZE = KEY_SIZE + KEY_SIZE + Long.BYTES + SIG_SIZE;
	private static final int HASH_SIZE = 32;
	private static final int PORT = 12000;
	
	private static final AtomicInteger blockIndexCounter = new AtomicInteger(0);
	
	private ServerSocket server;
	private Scanner input = new Scanner (System.in);
	private byte[] myKey = null; 
	private ArrayList<Block> myChain = new ArrayList<Block>();
	//TODO: need to make the current Block atomic, so that when it's being committed to the chain other threads won't interfere
	//TODO: need to sign the transactions with the block num as well as the block index. Otherwise, transactions can be copied over to new blocks and register as valid
	private Block currentBlock = null;
	
	private class Block
	{
		byte[] previousHash = new byte[HASH_SIZE];
		byte[] ownerKey = new byte[KEY_SIZE];
		ByteBuffer transactions = ByteBuffer.allocate(T_SIZE * T_PER_BLOCK);
		byte[] special = new byte[HASH_SIZE];
		
		public Block(byte[] prev)
		{
			previousHash = prev;
			ownerKey = myKey; 
		}
		
		public  Block(FileInputStream fIn)
		{
			try
			{
				fIn.read(previousHash);
				fIn.read(ownerKey);
				byte [] temp = new byte[T_SIZE * T_PER_BLOCK];
				fIn.read(temp);
				transactions.put(temp);
				fIn.read(special);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			dbPrint();
		}
		
		public void addTransaction (byte[] sender, byte[] receiver, byte[] amount, byte[] sig)
		{
			transactions.put(sender);
			transactions.put(receiver);
			transactions.put(amount);
			transactions.put(sig);
		}
		
		//find the hash of the block
		public byte[] findHash ()
		{
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("SHA-256");
			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}
			
			md.update(previousHash);
			md.update(ownerKey);
			md.update(transactions.array());
			return md.digest(special);
		}
		
		//finds the special value that gives a hash with a certain number of trailing zeros. Stores the special number in the block and returns the hash
		public byte[] findSpecial ()
		{
			System.out.println("Finding special number");
			
			special = new byte[HASH_SIZE];
			byte[] result = null;
			
			long start = System.nanoTime();
			
			result = findHash();
			
			//need hash with 8 consecutive 0's at the beginning
			while (result[0] != 0)
			{
				//increment byte array
				int index = 0;
				while (index != -1)
				{
					++special[index];
					if (special[index] != 0)
					{
						//No carry, end the addition
						index = -1;
					}
					else
					{
						//Carry bit, increment the next byte
						++index;
					}
				}
				result = findHash();
			}
			
			long stop = System.nanoTime();
			System.out.println("Found special. Took " + (stop - start) + " nano seconds");
			
			return result;
		}

		//print out the fields of the block
		public void dbPrint()
		{
			System.out.print("prev:");
			for (int i = 0; i < previousHash.length; i++)
			{
				System.out.print(previousHash[i]);
			}
			System.out.println();
			System.out.print("owner:");
			for (int i = 0; i < ownerKey.length; i++)
			{
				System.out.print(ownerKey[i]);
			}
			System.out.println();
			System.out.println("trans:");
			byte[] transArray = transactions.array();
			for (int i = 0; i < T_PER_BLOCK; i++)
			{
				System.out.println(" " + i + ": ");
				System.out.print("  sender:");
				for (int j = 0; j < KEY_SIZE; j++)
				{
					System.out.print(transArray[i*T_SIZE + j]);
				}
				System.out.print("\n  recevr:");
				for (int j = 0; j < KEY_SIZE; j++)
				{
					System.out.print(transArray[i*T_SIZE + j + KEY_SIZE]);
				}
				System.out.print("\n  amount (raw byte values):");
				for (int j = 0; j < Long.BYTES; j++)
				{
					System.out.print(transArray[i*T_SIZE + j + KEY_SIZE + KEY_SIZE] + " ");
				}
				System.out.print("\n  signtr:");
				for (int j = 0; j < SIG_SIZE; j++)
				{
					System.out.print(transArray[i*T_SIZE + j + KEY_SIZE + KEY_SIZE + Long.BYTES]);
				}
				System.out.println();
			}
			System.out.print("spec:");
			for (int i = 0; i < special.length; i++)
			{
				System.out.print(special[i]);
			}
			System.out.println();
		}
	}
	
	private class UserThread implements Runnable
	{
		Socket mySocket;
		DataInputStream in;
		DataOutputStream out;
		
		public UserThread (Socket inSocket)
		{
			try
			{
				mySocket = inSocket;
				in = new DataInputStream(new BufferedInputStream(inSocket.getInputStream()));
				out = new DataOutputStream(inSocket.getOutputStream());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void run ()
		{
			try
			{
				byte[] indexBytes = ByteBuffer.allocate(Integer.BYTES).putInt(blockIndexCounter.getAndIncrement()).array();
				//send the user their block index, for the purpose of signing the block
				out.write(indexBytes);
				
				byte[] sender = new byte[KEY_SIZE];
				byte[] receiver = new byte[KEY_SIZE];
				byte[] amount = new byte[Long.BYTES];
				byte[] sig = new byte[SIG_SIZE];
				in.read(sender);
				in.read(receiver);
				in.read(amount);
				in.read(sig);
								
				//verify the transaction
				try
				{
					PublicKey pub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(sender));
					Signature rsa = Signature.getInstance("SHA1withRSA");
					rsa.initVerify(pub);
					
					rsa.update(indexBytes);
					rsa.update(sender);
					rsa.update(receiver);
					rsa.update(amount);
					if (rsa.verify(sig))
					{
						currentBlock.addTransaction(sender, receiver, amount, sig);;
						System.out.println("got a valid transaction");
						
						//block full, commit it to the chain and make a new block
						if (blockIndexCounter.get() == T_PER_BLOCK)
						{
							System.out.println("Block full. Adding it to the block chain");
							byte[] previousHash = currentBlock.findSpecial();
							
							myChain.add(currentBlock);
							currentBlock = new Block(previousHash);
							blockIndexCounter.set(0);
							
							//after adding the block, save the chain to file
							saveChain();
						}
					}
					else
					{
						System.out.println("Invalid transaction");
					}
				}
				catch (NoSuchAlgorithmException  | InvalidKeyException | InvalidKeySpecException | SignatureException e)
				{
					e.printStackTrace();
				}
					
				mySocket.close();
				in.close();
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Maintainer (int port)
	{	
		System.out.println("Please give your public key: ");
		myKey =  Base64.getDecoder().decode(input.nextLine());
		
		//by default, the current block is a block with a prevHash of all zeros
		currentBlock = new Block(new byte[HASH_SIZE]);
		
		//Load in the chain from a local file, if there is one
		loadChain();
		
		try
		{
			server = new ServerSocket(port);
			System.out.println("Started maintainer server");
			while (true)
			{
				Socket newSocket = server.accept();
				UserThread newUser = new UserThread(newSocket);
				new Thread(newUser).start();
			}
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//Load in the chain from a local file
	public void loadChain ()
	{
		System.out.println("Loading chain");
		
		Block tempBlock = null;
		//Used to make sure that a block's hash has trailing zeros and that the hash of a block is the prevHash of the next block 
		byte[] blockHash = null;
		
		try
		{
			File chainFile = new File ("chain");
			
			if (!chainFile.exists())
			{
				System.out.println("blockchain file not found");
				return;
			}
			
			FileInputStream fIn = new FileInputStream (chainFile);
			
			//read in the blocks from the file
			while (fIn.available() != 0)
			{
				tempBlock = new Block(fIn);
				
				//make sure that the prevHash field in this block is the hash of the previous block
				if (!myChain.isEmpty())
				{
					for (int i = 0; i < blockHash.length; i++)
					{
						if (blockHash[i] != tempBlock.previousHash[i])
						{
							System.out.println("Invalid block chain. prevHash in the block does not match the hash of the previous block");
							fIn.close();
							myChain.clear();
							return;
						}
					}
				}
				
				//TODO: might need to verify the transactions of blocks coming in. Might be overkill, but who knows
				
				//Make sure that this block's hash has a certain number of trailing zeros
				blockHash = tempBlock.findHash();
				
				if (blockHash[0] != 0)
				{
					System.out.println("Invalid block chain. Hash does not start with enough consectutive zeros");
					fIn.close();
					myChain.clear();
					return;
				}
				
				//Valid block, add it to the chain
				myChain.add(tempBlock);
			}
			
			fIn.close();
			
			//Make a new block that has a prevHash of the previous block's hash
			currentBlock = new Block(blockHash);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Load complete");
	}
	
	//TODO: make this instead add a block to the end of the chain file
	//Save the blockchain to a local file
	public void saveChain ()
	{
		System.out.println("Saving chain");
		try
		{
			File chainFile = new File ("chain");
			//delete the old file and create a new one
			chainFile.delete();
			chainFile.createNewFile();
			
			FileOutputStream fOut = new FileOutputStream (chainFile);

			System.out.println("chain length:" + myChain.size());
			
			for (Block block : myChain)
			{			
				block.dbPrint();
				
				fOut.write(block.previousHash);
				fOut.write(block.ownerKey);
				fOut.write(block.transactions.array());
				fOut.write(block.special);
			}
			
			fOut.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Save complete");
	}
	
	public static void main (String args[])
	{	
		Maintainer server = new Maintainer(PORT);
	}
}
