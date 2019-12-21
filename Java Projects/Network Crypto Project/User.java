//Makes transactions using the currency and broadcasts those transactions to Maintainers 

import java.net.*;
import java.nio.file.*;
import java.nio.ByteBuffer;
import java.io.*;
import java.lang.Thread;
import java.util.Scanner;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class User
{
	private static final int PORT = 12000;

	//    private Socket socket = null;
	//    private DataOutputStream out = null;
	//    private DataInputStream in = null;
	private Scanner input = new Scanner (System.in);

	public User (int port)
	{    
		try
		{
//			// get path to the users directory
//			Path dPath = Paths.get(System.getProperty("user.home") + "/Desktop");
//			//path to currency folder
//			Path cPath = Paths.get(dPath.toString() + "/DJScryptoCurrency");
//			//check if they have a path to the currency folder
//			if(!Files.exists(cPath)) {
//				Files.createDirectory(cPath);
//			}

			//User folder
			File userFolder = new File("Users");
			if (!userFolder.exists())
			{
				userFolder.mkdir();
			}
			
			//users name
			String user = getUserName();
//			//path to User folder
//			Path uPath = Paths.get(cPath.toString() + "/" + user);
			//gets the users keys
			KeyPair pair = getUserKeys(user);

			//promts User
			boolean running = true;
			while(running){
				System.out.println();
				System.out.println("What would you like to do today " + user);
				System.out.println("1 - Make a transaction");
				System.out.println("2 - Show my key");
				System.out.println("3 - Change user");
				System.out.println("4 - Exit");
				System.out.println();
				System.out.print("Your response: ");
				int response = input.nextInt();
				//orogress the scanner
				input.nextLine();

				switch(response) {
				case 1:
					makeTransaction(pair, port);
					break;
				case 2:
					System.out.println("Your public key is: " + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
					break;
				case 3:
					//users name
					user = getUserName();
//					//path to User folder
//					uPath = Paths.get(cPath.toString() + "/" + user);
					//gets the users keys
					pair = getUserKeys(user);
					break;
				case 4:
					System.out.println("See you later");
					running = false;
					break;
				default:
					System.out.println("That was not on the menu please input a number form 1 - 4.");
				}
			}
			//            byte[] sender = pub.getEncoded();
			//            System.out.println("Your public key is: " + Base64.getEncoder().encodeToString(sender));
			//            System.out.print("Please give the key you want to send money to: ");
			//            byte[] receiver =  Base64.getDecoder().decode(input.nextLine());
			//            System.out.print("Please give the amount of money you want to send: ");
			//            byte[] amount =  ByteBuffer.allocate(Long.BYTES).putLong(input.nextLong()).array();
			//
			//            //Connect to server
			//            socket = new Socket("127.0.0.1", port);
			//            out = new DataOutputStream(socket.getOutputStream());
			//            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			//            System.out.println("Connected to server");
			//
			//            //Get this transaction's block index
			//            byte[] index = new byte[Integer.BYTES];
			//            in.read(index);
			//
			//            //create the signature for the transaction
			//            Signature rsa = Signature.getInstance("SHA1withRSA");
			//            PrivateKey priv = pair.getPrivate();
			//            byte[] sig = null;
			//            try
			//            {
			//                rsa.initSign(priv);
			//                rsa.update(index);
			//                rsa.update(sender);
			//                rsa.update(receiver);
			//                rsa.update(amount);
			//
			//                sig = rsa.sign();
			//            }
			//            catch (InvalidKeyException e)
			//            {
			//                e.printStackTrace();
			//            }
			//            catch (SignatureException e)
			//            {
			//                e.printStackTrace();
			//            }
			//
			//            //send the message
			//            ByteBuffer transaction = ByteBuffer.allocate(4+162+162+8+128);
			//            transaction.put(sender);
			//            transaction.put(receiver);
			//            transaction.put(amount);
			//            transaction.put(sig);
			//            out.write(transaction.array());
			//
			//            in.close();
			//            out.close();
			//            socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeySpecException e)
		{
			e.printStackTrace();
		}
	}

	//code from https://snipplr.com/view/18368/ says its from  http://java.sun.com/docs/books/tutorial/security/apisign/vstep2.html
	public void SaveKeyPair(String user, KeyPair keyPair) throws IOException {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		
		FileOutputStream fos = new FileOutputStream("Users/" + user + "/public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();

		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream("Users/" + user + "/private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}


	public KeyPair LoadKeyPair(String user, String algorithm)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = new File("Users/" + user + "/public.key");
		FileInputStream fis = new FileInputStream("Users/" + user + "/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		// Read Private Key.
		File filePrivateKey = new File("Users/" + user + "/private.key");
		fis = new FileInputStream("Users/" + user + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		// Generate KeyPair.
		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}

	public String getUserName() 
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {

		System.out.println("Welcome to DJScryptoCurrency");

		//get the Users name
		String answer;
		String user;
		do {
			System.out.print("Please enter your username: ");
			user = input.nextLine();
			System.out.print("Did you mean to type \"" + user + "\" Y or N: ");
			answer = input.nextLine();
			while(!answer.equalsIgnoreCase("y") && !answer.equalsIgnoreCase("n") 
					&& !answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no"))
			{
				System.out.print("Thats not an accpatable response please type y or n: ");
				answer = input.nextLine();
			}
		}
		while(answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no"));

		return user;
	}

	public KeyPair getUserKeys(String user) 
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		//check if they have a path to the currency folder
		KeyPair pair;
		File userFile = new File("Users/" + user);
		if(!userFile.exists())
		{
			System.out.println("Seems that your a new User. Welcome");
			userFile.mkdir();
		}
		if(!Files.exists(Paths.get("Users/" + user + "/public.key")) || 
				!Files.exists(Paths.get("Users/" + user + "/private.key"))){
			//Make key pair
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024, new SecureRandom());
			pair = keyGen.generateKeyPair();
			SaveKeyPair(user, pair);
		}
		else {
			//loads the saved keypair of the user
			pair = LoadKeyPair(user, "RSA");
			System.out.println("Welcome Back " + user);
			
		}

		return pair;
	}

	public void makeTransaction (KeyPair inPair, int inPort)
	{
		try
		{
			byte[] sender = inPair.getPublic().getEncoded();
//			System.out.println("Your public key is: " + Base64.getEncoder().encodeToString(sender));
			System.out.print("Please give the key you want to send money to: ");
			byte[] receiver =  Base64.getDecoder().decode(input.nextLine());
			System.out.print("Please give the amount of money you want to send: ");
			byte[] amount =  ByteBuffer.allocate(Long.BYTES).putLong(input.nextLong()).array();

			//Connect to server
			Socket socket = new Socket("127.0.0.1", inPort);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			System.out.println("Connected to server");

			//Get this transaction's block index
			byte[] index = new byte[Integer.BYTES];
			in.read(index);

			//create the signature for the transaction
			Signature rsa = Signature.getInstance("SHA1withRSA");
			PrivateKey priv = inPair.getPrivate();
			byte[] sig = null;
			rsa.initSign(priv);
			rsa.update(index);
			rsa.update(sender);
			rsa.update(receiver);
			rsa.update(amount);

			sig = rsa.sign();	
			//send the message
			ByteBuffer transaction = ByteBuffer.allocate(4+162+162+8+128);
			transaction.put(sender);
			transaction.put(receiver);
			transaction.put(amount);
			transaction.put(sig);
			out.write(transaction.array());

			in.close();
			out.close();
			socket.close();
		}
		catch (IOException | InvalidKeyException | SignatureException | NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}

	public static void main (String args[])
	{
		User user = new User(PORT);
	}
}




////Makes transactions using the currency and broadcasts those transactions to Maintainers 
//
//import java.net.*;
//import java.nio.ByteBuffer;
//import java.nio.charset.StandardCharsets;
//import java.io.*;
//import java.lang.Thread;
//import java.util.Scanner;
//import java.security.*;
//import java.util.Base64;
//
//public class User
//{
//	private static final int TRANSACTION_SIZE = 460;
//	private static final int PORT = 12000;
//	
//	private Socket socket = null;
//	private DataOutputStream out = null;
//	private DataInputStream in = null;
//	private Scanner input = new Scanner (System.in);
//	
//	public User (int port)
//	{	
//		try
//		{
//			//Make key pair
//			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
//			keyGen.initialize(1024, new SecureRandom());
//			KeyPair pair = keyGen.generateKeyPair();
//			
//			PublicKey pub = pair.getPublic();
//			byte[] sender = pub.getEncoded();
//			System.out.println("Your public key is: " + Base64.getEncoder().encodeToString(sender));
//			System.out.println("Please give the key you want to send money to: ");
//			byte[] receiver =  Base64.getDecoder().decode(input.nextLine());
//			System.out.print("Please give the amount of money you want to send: ");
//			byte[] amount =  ByteBuffer.allocate(Long.BYTES).putLong(input.nextLong()).array();
//			
//			//Connect to server
//			socket = new Socket("127.0.0.1", port);
//			out = new DataOutputStream(socket.getOutputStream());
//			in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//			System.out.println("Connected to server");
//			
//			//Get this transaction's block index
//			byte[] index = new byte[Integer.BYTES];
//			in.read(index);
//			
//			//create the signature for the transaction
//			Signature rsa = Signature.getInstance("SHA1withRSA");
//			PrivateKey priv = pair.getPrivate();
//			byte[] sig = null;
//			try
//			{
//				rsa.initSign(priv);
//				rsa.update(index);
//				rsa.update(sender);
//				rsa.update(receiver);
//				rsa.update(amount);
//				
//				sig = rsa.sign();
//			}
//			catch (InvalidKeyException | SignatureException e)
//			{
//				e.printStackTrace();
//			}
//			
//			//send the message
//			ByteBuffer transaction = ByteBuffer.allocate(TRANSACTION_SIZE);
//			transaction.put(sender);
//			transaction.put(receiver);
//			transaction.put(amount);
//			transaction.put(sig);
//			out.write(transaction.array());
//			
//			System.out.println("Sent transaction");
//			
//			in.close();
//			out.close();
//			socket.close();
//		}
//		catch(IOException | NoSuchAlgorithmException e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	public static void main (String args[])
//	{
//		User user = new User(PORT);
//	}
//}
