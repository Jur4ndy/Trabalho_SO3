package tSO3;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PageReplacementAlgorithm {
	
	public static void main(String[] args) {
		PageReplacementAlgorithm p = new PageReplacementAlgorithm();
		p.addProcesses(null);
	}
	
	LinkedList<Page> pages = new LinkedList<Page>();
	
	
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
				 pages.add(new Page(Integer.parseInt(pageData[0]), Integer.parseInt(pageData[1]), time));
				 time++;
			}
		}
		catch(IOException erro) {
			System.out.println("ERROR! file not found");
		}
	}
	
	public void simulateFIFO() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(Page page : pages) {
			if (mem.frames.size() < 8000 || mem.frames.contains(null)) mem.addPage(page);
			else if (mem.frames.contains(page)) mem.refreshPage(time, page);
			else {
				mem.replaceFIFO(page);
				pageFaults++;
			}
			time++;
		}
		System.out.print("FIFO page faults = " + pageFaults);
	}
	
	public void simulateLRU() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(Page page : pages) {
			if (mem.frames.size() < 8000 || mem.frames.contains(null)) mem.addPage(page);
			else if (mem.frames.contains(page)) mem.refreshPage(time, page);
			else {
				mem.replaceLRU(page);
				pageFaults++;
			}
			time++;
		}
		System.out.print("LRU page faults = " + pageFaults);
	}

	public void simulateSecondChance() {
		Memory mem = new Memory();
		int time = 0;
		int pageFaults = 0;
		for(Page page : pages) {
			if (mem.frames.size() < 8000 || mem.frames.contains(null)) mem.addPage(page);
			else if (mem.frames.contains(page)) mem.refreshPage(time, page);
			else {
				mem.replaceSecondChance(page);
				pageFaults++;
			}
			time++;
		}
		System.out.print("Second Chance page faults = " + pageFaults);
	}
	

}

class Page {
	int To;
	int lastUsed;
	boolean secondChance = false;
	int process;
	int id;

	
	public Page(int process, int id, int To) {
		this.process = process;
		this.id = id;
		this.To = To;
		lastUsed = To;
	}
	
	
}

class Memory {
	
	//null elements 
	LinkedList<Page> frames = new LinkedList<Page>();
	int pointer = 0;
	
	public Memory() {
		
	}
	
	public void refreshPage(int time, Page page) {
		//look for a page and set its last used to the current time
		frames.get(frames.indexOf(page)).lastUsed = time;
		frames.get(frames.indexOf(page)).secondChance = true;
	}
	
	public void addPage(Page page) {
		//always check if there's free space on the array before doing this!!!! 
		if(frames.contains(null))
			replacePage(frames.indexOf(null), page);
		else if (frames.size() < 8000) {
			frames.add(page);
		}
			
	}
	
	public void removePage(Page page) { 
		replacePage(frames.indexOf(page), null);
	}
	
	public void replacePage(int target, Page page) {
		frames.remove(target);
		frames.add(target, page);
	}
	
	public void replaceFIFO(Page page) { 
		int min = frames.get(0).To;
		int target = 0;
		int s = frames.size();
		//look for the frame with the lowest To value
		for(int i = 1; i < s; i++) {
			if (frames.get(i).To < min) {
				target = i;
			}
		}
		
		replacePage(target, page);
	}
	
	public void replaceLRU(Page page) {
		int min = frames.get(0).To;
		int target = 0;
		int s = frames.size();
		//look for the frame with the lowest lastUsed value
		for(int i = 1; i < s; i++) {
			if (frames.get(i).lastUsed < min) {
				target = i;
			}
		}
		replacePage(target, page);
	}
	
	public void replaceSecondChance(Page page) {
		/*pick the first frame with secondChance set to false, all other frames before that one will have their 
		secondChance set to false.*/
		int target = 0;
		int s = frames.size();
		if (pointer == s) {
			pointer = 0;
		}
		for(int i = pointer; i < s; i++) {
			if (frames.get(i).secondChance) {
				frames.get(i).secondChance = false;
				if (i == s-1) {
					i = 0;
				}
			}
			else {
				target = i;
				pointer = i+1;
				break;
			}		
		}
		replacePage(target, page);
	}

}


