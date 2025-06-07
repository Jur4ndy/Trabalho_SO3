package tSO3;
import java.io.File;
import java.io.IOException;
import java.util.*;


class PageInfo {
	int process;
	int id;
	
	public PageInfo(int process, int id) {
		this.process = process;
		this.id = id;
	}
	
	public boolean equals(PageInfo info) {
		if (this == info) return true;
	    if (info == null) return false;
	    return process == info.process && id == info.id;
	}
	
}

class Page {
	PageInfo info;
	int To;
	int lastUsed;
	boolean secondChance = false;
	
	public Page(PageInfo info, int To) {
		this.info = info;
		this.To = To;
		lastUsed = To;
	}
	
	public Page(int process, int id, int To) {
		info = new PageInfo(process, id);
		this.To = To;
		lastUsed = To;
	}
	
	
}


class Memory {
	public int MAX_ARRAY_SIZE;

	
	//null elements 
	LinkedList<Page> frames = new LinkedList<Page>();
	int pointer = 0;
	int size = 0;

	
	public Memory(int max_size) {
		MAX_ARRAY_SIZE = max_size;
	}
	
	public boolean contains(PageInfo info) {
		for (Page frame : frames) {
			if (frame.info.equals(info)) return true;
		}
		return false;
	}
	
	public int indexOf(PageInfo info) {
		int index = 0;
		for (Page frame : frames) {
			if (frame.info.equals(info)) return index;
			index++;
		}
		return -1;
	}
	
	public void refreshLRU(int time, int index) {
		frames.get(index).lastUsed = time;		
	}
	
	public void refreshSecondChance(int time, int index) {
		//look for a page and set its last used to the current time
		frames.get(index).secondChance = true;
	}
	
	public void addPage(Page page) {
		//always check if there's free space on the array before doing this!!!! 
		if (size < MAX_ARRAY_SIZE) {
			frames.add(page);
			size++;
		}
			
	}
	
	public void replacePage(int target, Page page) {
		frames.remove(target);
		frames.add(target, page);
	}
	
	public void replaceFIFO(Page page) { 
		frames.remove(0);
		frames.add(page);
	}
	
	public void replaceLRU(Page page) {
		int min = Integer.MAX_VALUE;
		int index = -1;
		int aux = 0;
		for (Page frame : frames) {
			if (frame.lastUsed < min) {
				min = frame.lastUsed;
				index = aux;
			}
			aux++;
		}
		try {
			frames.remove(index);
			frames.add(page);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public void replaceSecondChance(Page page) {
		/*pick the first frame with secondChance set to false, all other frames before that one will have their 
		secondChance set to false.*/
		while (true) {
			if (pointer >= size) pointer = 0;

			Page current = frames.get(pointer);
			if (current.secondChance) {
				current.secondChance = false;
				pointer++;
			} 
			else {
				frames.set(pointer, page);
				pointer++;
				break;
			}
		}
	}

}

public class PageReplacementAlgorithm {
	int max_size;

	public PageReplacementAlgorithm(int max_size) {
		this.max_size = max_size;
	}
	
	public void addProcesses(String text) {
		try {
			File file = new File(text);
			Scanner scan = new Scanner(file);
			String line = scan.nextLine();
			String[] processesData = line.split(";");
			String[] pageData;
			PageInfo breakPoint = new PageInfo(0, 0);
			int time = 0;
			
			for (String data : processesData) {   
				 pageData = data.split(",");
				 PageInfo p = new PageInfo(Integer.parseInt(pageData[0]), Integer.parseInt(pageData[1]));
				 if (p.equals(breakPoint)) break;
				 pages.add(p);
				 //System.out.println(Integer.parseInt(pageData[0]) + ", " + Integer.parseInt(pageData[1]));
				 time++;
			}
			System.out.println("pages added: " + time);
			scan.close();
		}
		catch(IOException erro) {
			System.out.println("ERROR! file not found");
		}
	}
	
	public void simulateFIFO() {
		Memory mem = new Memory(max_size);
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.frames.size() < mem.MAX_ARRAY_SIZE && !mem.contains(info)) {
				mem.addPage(new Page(info, time));
				pageFaults++;
				//System.out.println("Page added: " + mem.frames. size());
			}
			else if (!mem.contains(info)){
				mem.replaceFIFO(new Page(info, time));
				pageFaults++;
				//System.out.println(pageFaults + " at time: " + time + ". For pageInfo: " + info.process + ", " + info.id);
			}
			time++;
		}
		System.out.println("FIFO page faults = " + pageFaults);
	}
	
	public void simulateLRU() {
		Memory mem = new Memory(max_size);
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.size < mem.MAX_ARRAY_SIZE) {
				mem.addPage(new Page(info, time));
				pageFaults++;
			}
			else {
					int index = mem.indexOf(info);
					if (index > -1) mem.refreshLRU(time, index);
					
					else {
						mem.replaceLRU(new Page(info, time));
						pageFaults++;
						//System.out.println(pageFaults + " at time: " + time + ". For pageInfo: " + info.process + ", " + info.id);

					}
			}
			time++;
		}
		System.out.println("LRU page faults = " + pageFaults);
	}

	public void simulateSecondChance() {
		Memory mem = new Memory(max_size);
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.frames.size() < mem.MAX_ARRAY_SIZE) {
				mem.addPage(new Page(info, time));
				pageFaults++;
			}
			else {
				int index = mem.indexOf(info);
				if (index > -1) mem.refreshSecondChance(time, index);
				
				else {
					mem.replaceSecondChance(new Page(info, time));
					pageFaults++;
					//System.out.println(pageFaults + " at time: " + time + ". For pageInfo: " + info.process + ", " + info.id);
				}
			}
			time++;
		}
		System.out.println("Second Chance page faults = " + pageFaults);
	}
	
	public static void main(String[] args) {
		System.out.println("Teste: 3 de memoria");
		PageReplacementAlgorithm p3 = new PageReplacementAlgorithm(3);
		p3.addProcesses("/home/ju/eclipse-workspace/Trabalho_SO3/src/tSO3/StringTeste_short");
		p3.simulateFIFO();
		p3.simulateLRU();
		p3.simulateSecondChance();
		System.out.println();
		
		System.out.println("Teste: 8000 de memoria");
		PageReplacementAlgorithm p = new PageReplacementAlgorithm(8000);
		p.addProcesses("/home/ju/Downloads/StringTeste.txt");
		p.simulateFIFO();
		p.simulateLRU();
		p.simulateSecondChance();
	}
	
	LinkedList<PageInfo> pages = new LinkedList<PageInfo>();
	

}


//class MemoryArray {
//	
//	//null elements 
//	Page[] frames = new Page[8000];
//	int size = 0;
//	int pointer = 0;
//
//	
//	public MemoryArray() {
//
//	}
//	
//	public boolean contains(PageInfo info) {
//		for (Page frame : frames) {
//			if (frame.info == info) return true;
//		}
//		return false;
//	}
//	
//	public int indexOf(PageInfo info) {
//		for (int i = 0; i < size; i++) {		
//			if (frames[i].info == info) return i;
//		}
//		return -1;
//	}
//	
//	public void refreshPage(int time, int index) {
//		//look for a page and set its last used to the current time
//
//		frames[index].lastUsed = time;
//		frames[index].secondChance = true;
//	}
//	
//	public void addPage(Page page) {
//		//always check if there's free space on the array before doing this!!!! 
//		if (size < 8000) {
//			frames[size] = page;
//		}
//			
//	}
//	
//	public void replacePage(int target, Page page) {
//		frames[target] = page;
//	}
//	
//	public void replaceFIFO(Page page) { 
//		int min = frames[0].To;
//		int target = 0;
//		//look for the frame with the lowest To value
//		for(int i = 1; i < size; i++) {
//			if (frames[i].To < min) {
//				target = i;
//			}
//		}
//		
//		replacePage(target, page);
//	}
//	
//	public void replaceLRU(Page page) {
//		int min = frames[0].lastUsed;
//		int target = 0;
//		//look for the frame with the lowest lastUsed value
//		for(int i = 1; i < size; i++) {
//			if (frames[i].lastUsed < min) {
//				target = i;
//			}
//		}
//		replacePage(target, page);
//	}
//	
//	public void replaceSecondChance(Page page) {
//		/*pick the first frame with secondChance set to false, all other frames before that one will have their 
//		secondChance set to false.*/
//		int target = 0;
//		if (pointer == size) {
//			pointer = 0;
//		}
//		for(int i = pointer; i < size; i++) {
//			if (frames[i].secondChance) {
//				frames[i].secondChance = false;
//				if (i == size-1) {
//					i = -1;
//				}
//			}
//			else {
//				target = i;
//				pointer = i+1;
//				break;
//			}		
//		}
//		replacePage(target, page);
//	}
//
//}
//
