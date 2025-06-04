package tSO3;
import java.util.*;

public class PageReplacement {
	
	public static void main(String[] args) {
		new PageReplacement();
	}
	
	int lastUsed;
	boolean secondChance;
	
	public PageReplacement() {
		
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
	
	LinkedList<Page> frames = new LinkedList<Page>();
	int pointer = 0;
	
	public Memory() {
		
	}
	
	public void refreshPage(int time, Page page) {
		//look for a page and set its last used to the current time
		frames.get(frames.indexOf(page)).lastUsed = time;
	}
	
	public void addPage(Page page) {
		//always check if theres free space on the array before doing this!!!! 
			frames.add(page);
	}
	
	public void removePage(Page page) { 
		frames.remove(frames.indexOf(page));
	}
	
	public void replaceFIFO(Page page) {
		//"sort" by To
	}
	
	public void replaceLUS(Page page) {
		//"sort" by lastUsed
	}
	
	public void replaceSecondChance(Page page) {
		//pick the first secondChance the pointer finds;
	}

}


