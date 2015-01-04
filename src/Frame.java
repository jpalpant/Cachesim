public class Frame {
	private boolean valid;
	private byte[] data;
	private int tagLow;
	private int blockSize;
	private int lastLoadIter;
	
	public Frame(int bsize) {
		valid = false;
		blockSize = bsize;
		data = new byte[blockSize];
		for(int i = 0; i<blockSize; i++) {
			data[i] = (byte) 0;
		}
		
		tagLow = 0;
		lastLoadIter = 0;
	}

	
	//Used when replacing an entire block from memory; should update tag and validbit
	public void update(int blockStartAddress, byte[] newData) {
		tagLow = blockStartAddress;
		valid = true;
		System.arraycopy(newData, 0, data, 0, blockSize);
	}
	
	//Used during stores - does not affect tag or validbit, only data
	public void updateRange(int address, int numBytes, byte[] newData) {
		System.arraycopy(newData, 0, data, address - tagLow, numBytes);
	}
	
	public byte[] fetchData(int blockOffset, int numBytes) {
		byte[] returnable = new byte[numBytes];
		
		System.arraycopy(data, blockOffset, returnable, 0, numBytes);
		return returnable;
	}
	
	public boolean tagMatch(int address) {
		return (address>=tagLow && address<tagLow+blockSize);
	}
	
	public boolean hasValidBlock() {
		return valid;
	}
	
	public void setLastLoad(int counter) {
		lastLoadIter = counter;
	}
	
	public int getLastLoad() {
		return lastLoadIter;
	}
	
	public int getTagLow() {
		return tagLow;
	}
	
	public String toString() {
		String v = valid ? "TRUE" : "FALSE";
		String datas = ByteArrayToHexString(data);
		
		return "|"+v+"-"+tagLow+"-"+datas+"-iter:"+lastLoadIter+"|";
	}
	
	public static String ByteArrayToHexString(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b:a) sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
	
}