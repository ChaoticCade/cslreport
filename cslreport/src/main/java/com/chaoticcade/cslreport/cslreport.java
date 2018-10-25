package com.chaoticcade.cslreport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class cslreport extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		System.out.println("cslreport Online!");
		getServer().getPluginManager().registerEvents(this, this);
		getDataFolder().mkdir(); //TODO Update so it checks and doesn't blindly create folder
	}

	@Override
	public void onDisable() {
		System.out.println("cslreport Disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("csrep") && (args.length != 2)){
			sender.sendMessage("Invalid number of arguments!");
			return false;
		}
		if (cmd.getName().equalsIgnoreCase("csrep") && (args[0]).equalsIgnoreCase("search") && isStringInt(args[1]) == true){

			//search command
			if (Integer.parseInt(args[1]) > 7){
				sender.sendMessage("Can not search more than 7 days back!");
				return true;
			}
			if (Integer.parseInt(args[1]) < 1){
				sender.sendMessage("Minimum search is 1 day.");
				return true;
			}
			else {

				try {
					readLog(sender, Integer.parseInt(args[1]));

				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("csrep") && (args[0]).equalsIgnoreCase("page") && isStringInt(args[1]) == true){        

			//scroll through data command
			try {
				browseLog(sender, Integer.parseInt(args[1]));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		//new commands here

		return false;
	}


	public void readLog(CommandSender sender, Integer days) throws Exception {
		sender.sendMessage("Searching...");
		String workingFile = (getDataFolder() + "\\" + (((Player) sender).getUniqueId()).toString() + ".log");
		Files.deleteIfExists(Paths.get(workingFile));
		FileWriter fr = new FileWriter(workingFile, true);
		String reader = sender.getName().toString();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List<String> dateList = new ArrayList<String>();
		dateList.add("./plugins/ChestShop/ChestShop.log");
		if (days > 1) {
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			cal.setTime(date);
			int d = 1;
			while (d < days) {
				cal.add(Calendar.HOUR, (-24));
				String modDateString = format.format(cal.getTime());
				dateList.add("./plugins/ChestShop/ChestShop.log." + modDateString);
				d++;
			}
		}

		int w = 0;
		while (w < dateList.size()) {
			File log = new File(dateList.get(w));
			if (Files.exists(Paths.get(dateList.get(w))) == true) {
				BufferedReader br = new BufferedReader(new FileReader(log));
				String line = null;
				@SuppressWarnings("unused")
				int i = 0;
				while ((line = br.readLine()) != null) {
					List<String> myList = new ArrayList<String>(Arrays.asList(line.split(" ")));
					if (myList.lastIndexOf(reader) == 9) {
						String entry = myList.get(2) + " " + myList.get(3) + " " + myList.get(4) + " " + myList.get(5) + " "
								+ myList.get(6) + " " + myList.get(7) + " " + myList.get(8) + " you " + myList.get(10) + " "
								+ myList.get(11) + " " + myList.get(12) + " " + myList.get(13) + " " + myList.get(14)
								+ '\n';
						fr.write(entry);
					}
					if (myList.lastIndexOf(reader) == 10) {
						String entry = myList.get(2) + " " + myList.get(3) + " " + myList.get(4) + " " + myList.get(5) + " "
								+ myList.get(6) + " " + myList.get(7) + " " + myList.get(8) + " " + myList.get(9) + " you "
								+ myList.get(11) + " " + myList.get(12) + " " + myList.get(13) + " " + myList.get(14) + " "
								+ myList.get(15) + '\n';
						fr.write(entry);
					}

					i++;
				}
				br.close();
			}
			w++;
		}
		fr.close();
		BufferedReader lineCounter = new BufferedReader(new FileReader(workingFile));
		int lines = 0;
		while (lineCounter.readLine() != null) lines++;
		lineCounter.close();
		sender.sendMessage("Search complete! You have " + (int) Math.ceil((lines / 5.0))+ " pages of data. Use /csrep page # to browse data.");

	}

	public void browseLog(CommandSender sender, Integer pageNumber) throws Exception {
		Map<String, String> transactionData = new HashMap<String, String>();
		String file = (getDataFolder() + "\\" + (((Player) sender).getUniqueId()).toString() + ".log");
		BufferedReader br = new BufferedReader(new FileReader(file));  
		String line = null;
		int key = 0;
		while ((line = br.readLine()) != null) {
			String keyString = Integer.toString(key);
			transactionData.put(keyString, line);
			key++;
		} 
		br.close();

		//Iterate from startLine to endLine
		int startLine = ((pageNumber - 1) * 5);
		int endLine = (pageNumber * 5);



		while (startLine < endLine) {
			if (transactionData.get(Integer.toString(startLine)) != null) {
				sender.sendMessage((startLine + 1) + ". " + transactionData.get(Integer.toString(startLine)));
				startLine++;
			}
			else {
				sender.sendMessage("End of data.");
				break;
			}
		}


	}


	public boolean isStringInt(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex)
		{
			return false;
		}
	}
}
