package tSO3;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class PageReplacementAlgorithm {
	
	public static void main(String[] args) {
		PageReplacementAlgorithm p = new PageReplacementAlgorithm();
		p.addProcesses("/home/ju/Downloads/StringTeste.txt");
		System.out.println("open console");
		p.simulateFIFO();
		p.simulateLRU();
		p.simulateSecondChance();
	}
	
	LinkedList<PageInfo> pages = new LinkedList<PageInfo>();

	
	public PageReplacementAlgorithm() {
		
	}
	
	public void addProcesses(String text) {
		try {
			File file = new File(text);
			Scanner scan = new Scanner(file);
			String line = scan.nextLine();
			String[] processesData = line.split(";");
			String[] pageData;
			int time = 0;
			
			for (String data : processesData) {   
				 pageData = data.split(",");
				 pages.add(new PageInfo(Integer.parseInt(pageData[0]), Integer.parseInt(pageData[1])));
				 System.out.println(Integer.parseInt(pageData[0]) + ", " + Integer.parseInt(pageData[1]));
				 time++;
			}
			System.out.println(scan.hasNext());
			System.out.println("pages added: " + time);
			scan.close();
		}
		catch(IOException erro) {
			System.out.println("ERROR! file not found");
		}
	}
	
	public void simulateFIFO() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.frames.size() < 8000 && !mem.frames.contains(info)) {
				mem.addPage(new Page(info, time));
				//System.out.println("Page added: " + mem.frames. size());
			}
			else if (!mem.frames.contains(info)){
				mem.replaceFIFO(new Page(info, time));
				pageFaults++;
				//System.out.println(pageFaults);
			}
			time++;
		}
		System.out.println("time elapsed = " + time);
		System.out.println("FIFO page faults = " + pageFaults);
	}
	
	public void simulateLRU() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.size < 8000) mem.addPage(new Page(info, time));
			else {
					int index = mem.indexOf(info);
					if (index > -1) mem.refreshLRU(time, index);
					
					else {
						mem.replaceLRU(new Page(info, time));
						pageFaults++;
					}
			}
			time++;
		}
		System.out.println("time elapsed = " + time);
		System.out.println("LRU page faults = " + pageFaults);
	}

	public void simulateSecondChance() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(PageInfo info : pages) {
			if (mem.frames.size() < 8000) mem.addPage(new Page(info, time));
			else {
				int index = mem.indexOf(info);
				if (index > -1) mem.refreshSecondChance(time, index);
				
				else {
					mem.replaceSecondChance(new Page(info, time));
					pageFaults++;
				}
			}
			time++;
		}
		System.out.println("time elapsed = " + time);
		System.out.println("Second Chance page faults = " + pageFaults);
	}
	

}
class PageInfo {
	int process;
	int id;
	
	public PageInfo(int process, int id) {
		this.process = process;
		this.id = id;
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
	
	//null elements 
	LinkedList<Page> frames = new LinkedList<Page>();
	int pointer = 0;
	int size = 0;

	
	public Memory() {

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
		Page p = frames.get(index);
		frames.remove(index);
		p.lastUsed = time;
		frames.add(p);
		
	}
	
	public void refreshSecondChance(int time, int index) {
		//look for a page and set its last used to the current time
		frames.get(index).secondChance = true;
	}
	
	public void addPage(Page page) {
		//always check if there's free space on the array before doing this!!!! 
		if (size < 8000) {
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
		frames.remove(0);
		frames.add(page);
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
