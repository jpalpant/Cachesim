import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cachesim {

	Memory mainMemory;
	Cache myCache; 

	public static void main(String[] args) {
		int memorySize = 16777216;
		int cacheSizeB = 1024 * Integer.parseInt(args[1]); //Input argument is in kB, 1kB = 1024 bytes
		int blockSizeB = Integer.parseInt(args[3]);
		int assoc = Integer.parseInt(args[2]);
		Cachesim cs = new Cachesim(cacheSizeB, blockSizeB, assoc, memorySize);

		cs.processFile(args[0]);
	}

	public Cachesim(int cacheSizeB, int blockSizeB, int assoc, int memSizeB) {
		mainMemory = new Memory(memSizeB); //24bit address = 2^24 bytes = 16777216 bytes
		myCache = new Cache(cacheSizeB, blockSizeB, assoc);
	}

	public void processFile(String filename) {
		int linenum = 0;
		try {
			FileReader fr = new FileReader(new File(filename));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			// if no more lines the readLine() returns null
			while ((line = br.readLine()) != null) {
			    System.out.println("Processing line #"+linenum+": "+line);
				this.processCommand(line);
				linenum++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void processCommand(String command)  {
		if(command.equals("show")) System.out.println(myCache);
		else if(command.substring(0, 2).equals("//"))  {}
		else {
			String[] commands = command.split(" ");
			boolean isStore = commands[0].equals("store");
			int address = Integer.decode(commands[1]);
			int numBytes = Integer.decode(commands[2]);
			if(isStore) {
				String valString = commands[3];
				byte[] data = HexStringToByteArray(valString);
				
				System.out.println(this.store(address, numBytes, data));
			}
			else {
				System.out.println(this.load(address, numBytes));		
			}
		}
	}

	public String load(int address, int numBytes){
		byte[] dataLoaded = new byte[numBytes];

		try {
			dataLoaded = myCache.load(address, numBytes);
		}
		catch(Exception e) {
			//First, grab the relevant block from memory and write the whole block to the cache
			int memoryBlockStart = address - (address % myCache.blockSize);
			byte[] blockFromMem = mainMemory.read(memoryBlockStart, myCache.blockSize);
			myCache.memToCache(memoryBlockStart, blockFromMem);
			
			//Then, grab the data you wanted out of the block you just sent to memory
			
			byte[] dataFromMem = Arrays.copyOfRange(blockFromMem, address - memoryBlockStart, address - memoryBlockStart + numBytes);
		
			return "load 0x" + Integer.toHexString(address) + " miss " + ByteArrayToHexString(dataFromMem) + "\n";
		}

		return "load 0x" + Integer.toHexString(address) + " hit " + ByteArrayToHexString(dataLoaded) + "\n";
	}

	public String store(int address, int numBytes, byte[] data) {
		//First, write to memory, because the cache is write-through
		mainMemory.write(address, numBytes, data);
		try {
			myCache.store(address, numBytes, data);
		}
		catch(Exception e) {
			return "store 0x" + Integer.toHexString(address) + " miss\n";
		}

		return "store 0x" + Integer.toHexString(address) + " hit\n";
	}

	public static byte[] HexStringToByteArray(String s) {
		byte data[] = new byte[s.length()/2];
		for(int i=0;i < s.length();i+=2) {
			data[i/2] = (Integer.decode("0x"+s.charAt(i)+s.charAt(i+1))).byteValue();
		}
		return data;
	}

	public static String ByteArrayToHexString(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b:a) sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
}