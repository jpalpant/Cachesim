import java.util.Arrays;
import java.lang.*;


public class Memory {
	byte[] memoryData;
	
	public Memory(int size) { //In bytes
		memoryData = new byte[size];
		//Write all bytes to be equal to zero
		Arrays.fill(memoryData, (byte) 0);
	}

	public void write(int startAddress, int numBytes, byte[] data) {
		System.arraycopy(data, 0, memoryData, startAddress, numBytes);
	}
	
	//Allows for reading from main memory in any size chunk
	//Cachesim will always read in blockSize size chunks
	public byte[] read(int startAddress, int numBytes) {
		byte[] returnable = new byte[numBytes];
		
		System.arraycopy(memoryData, startAddress, returnable, 0, numBytes);
		
		return returnable;
	}
	
}
