A cryptocurrency by Darrius Anderson, Jacob Kennedy, and Sydney Yorke.
The purpose of this project is provide a secure system to exchange imaginary currency between users. Users create transactions where they give money to other users. They then broadcast these transactions to Maintainers, who validate the transactions and store them in blocks, which are then connected together into a big blockchain.

NOTE: Make sure that the names of class files, such as Maintainer.class and User.class, go unchanged (if the name is changed to due canvas, such that '-2' is appended, Please change the file name to get rid of that addition).

1. In terminal, run the command
	>java Maintainer
2. Give a public key, either by running the User program to obtain a newly generated key (seen after "Your public key is:"), or use this sample key:

MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChwRp5ZQvN8gBBBltSyGXgJtKab8FpOwTUJBcgelNanRjn4YtWS4Dz8UqzHx6G+baKKslEwPHRJyaL9q5cCRmFMTXwhD78/HkikIEepcgLwwHDQPsQcwXraofBbrgdkftz14esTIEIfArpGHLB24zPhwMJ0yFB0lM0/U/SpBW7DQIDAQAB

You should see the phrase "blockchain file not found" printed to terminal

3. In a separate terminal, run the command
	>java User
4. Enter a username. Then type 'y'
5. You will see a screen of 4 options. 1 will create a transaction. 2 will show your public key. 3 will change you to a new user. 4 will close the program.
6. Of the options, enter '1'.
7. Enter a public key (such as the sample key shown above).
8. Enter the amount of money you want to give. The maintainer will print "got a valid transaction".
9. Repeat steps 6-8 until a total of 4 transactions have been sent. Once the maintainer has received 4 transactions, it will save the transaction block and print out the blockchain's contents (This is only for the sake of demonstration. If actually shiped, blockchain contents would not be printed and the transactions per block would be 65536, the max unsigned value of a short, instead of 4). The block contents are as follows:

	prev: the hash of the previous block
	owner: the key of the block owner
	trans: the transactions and their fields
		sender: who sent the money
		recevr: who received the money
		amount: the amount of money sent, in raw signed bytes
		signtr: the signature of the sender, unique to every transaction.
	spec: a special value appended to the block so that the block's hash begins with 8 consecutive 0's 
9. After the chain has been saved, close the maintainer with ctrl-C (or I beleive command-. for Mac).
10. Repeat steps 1 and 2. The program will load in the block chain from the local file and (for demonstration) display the contents of that chain.
11. You may play with user actions at your disposal. To reset the chain or user accounts, delete the chain file and/or User directory in the project folder.