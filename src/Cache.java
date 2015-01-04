import java.util.List;


public class Cache {
	Set[] mySets;
	int numSets;
	int blockSize;
	
	public Cache(int cacheSizeBytes, int blockSizeBytes, int associativity) {
		numSets = (cacheSizeBytes)/(blockSizeBytes*associativity);
		blockSize = blockSizeBytes;
		mySets = new Set[numSets];
		for(int i = 0; i<numSets; i++) {
			mySets[i] = new Set(associativity, blockSizeBytes);
		}
	}
	
	public byte[] load(int address, int numBytes) throws Exception {
		int setIndex = (address / blockSize) % numSets;
		int frameIndex = mySets[setIndex].findFrame(address);
		byte[] returnData;
		System.out.print("Set idx: " + setIndex + "\t");
		if(frameIndex != -1) {
			int blockOffset = address - mySets[setIndex].grabFrame(frameIndex).getTagLow();
			returnData = mySets[setIndex].fetchData(frameIndex, blockOffset, numBytes);
		}
		else {
			throw new Exception("loadmiss");
		}
		
		return returnData;
	}
	
	public void store(int address, int numBytes, byte[] data) throws Exception {
		//Stores only work if the address is already part of a valid frame
		//Stores DO NOT and CANNOT evict a block
		//So, don't let the Set manage the data, let the set determine IF you can place the data
		//If so, simply place the data in the frame - no need to change anything else
		//If not, throw exception
		int setIndex = (address / blockSize) % numSets;
		int frameIndex = mySets[setIndex].findFrame(address);
		System.out.print("Set idx: " + setIndex + "\t");

		
		if(frameIndex != -1) { //= "if there was a valid frame for this address"
			Frame workingFrame = mySets[setIndex].grabFrame(frameIndex);
			workingFrame.updateRange(address, numBytes, data);
		}
		else {
			throw new Exception("storemiss"); 
		}
	}
	
	public void memToCache(int blockStartAddress, byte[] newBlock) {
		//Updates from memory always work - just pass the whole chunk of data through
		//Updates from memory also evict a block, according to LRU
		//The Set knows which block to evict, so a Set method will handle this
		int setIndex = (blockStartAddress / blockSize) % numSets;
		mySets[setIndex].putNewBlock(blockStartAddress, newBlock);
	}
	
	public String toString() {
		String r = "";
		for(int i = 0; i<numSets; i++) {
			r += "Set #" + i + " " + mySets[i].toString() + "\n";
		}
		
		return r;
	}
}
