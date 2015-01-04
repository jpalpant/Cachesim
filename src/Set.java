import java.nio.ByteBuffer;
import java.util.List;


public class Set {
	private Frame[] myFrames;
	int associativity;
	int blocksize;
	int LRU;
	int loadCounter;
	
	public Set(int assoc, int bsize) {
		associativity = assoc;
		blocksize = bsize;
		myFrames = new Frame[associativity];
		LRU = 0;  //start with the first being unused
		
		for(int i = 0; i<associativity; i++) {
			myFrames[i] = new Frame(blocksize);
		}
	}
	
	//Determines if there is a readMiss exception
	public int findFrame(int address) {
		for(int i = 0; i<associativity; i++) {
			if (myFrames[i].tagMatch(address) & myFrames[i].hasValidBlock()) {
				return i;
			}
		}
		
		return -1;  //The start of a load miss
	}
	
	public byte[] fetchData(int frameNum, int offset, int numBytes){
		loadCounter++;
		myFrames[frameNum].setLastLoad(loadCounter);
		return myFrames[frameNum].fetchData(offset, numBytes);
	}
	
	public void putNewBlock(int address, byte[] data) {
		int frameToEvict = this.findLRUIndex();
		myFrames[frameToEvict].update(address, data);
		//If you are evicting a block, the new block place is always the most recently used
		loadCounter++;
		myFrames[frameToEvict].setLastLoad(loadCounter); 
	}
	
	//Determines which frame in the set is the LRU frame
	public int findLRUIndex() {
		int oldestcount = Integer.MAX_VALUE;
		int oldestindex = 0;
		for(int i = 0; i<associativity; i++) {
			if(myFrames[i].getLastLoad() < oldestcount) {
				oldestcount = myFrames[i].getLastLoad();
				oldestindex = i;
			}
		}
		
		return oldestindex;
	}
	
	public Frame grabFrame(int frameIndex) {
		return myFrames[frameIndex];
	}
	
	public String toString() {
		String r = "";
		for(int i = 0; i<associativity; i++) {
			r += myFrames[i].toString();
		}
		
		return r;
	}
}
